//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getNextSiblingOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.Ranges.endOffsetOf;

import com.google.common.collect.Iterables;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.picimako.mockitools.MockitoQualifiedNames;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base action class for verification approach conversion.
 */
public abstract class BaseConvertVerificationAction extends AnAction {
    protected final Document document;
    private final PsiDocumentManager documentManager;
    /**
     * Whether the conversion is caret- (single call chain) or selection-based (one or more call chains).
     */
    private final boolean isBulkMode;

    protected BaseConvertVerificationAction(Editor editor, String actionText, boolean isBulkMode) {
        super(actionText);
        document = editor.getDocument();
        documentManager = PsiDocumentManager.getInstance(editor.getProject());
        this.isBulkMode = isBulkMode;
    }

    protected BaseConvertVerificationAction(Editor editor, String actionText) {
        this(editor, actionText, false);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) return;
        Project project = e.getProject();

        runWriteCommandAction(project, () -> {
            if (isBulkMode) performActionInBulk(editor, file);
            else performAction(project, editor, file);
        });
    }

    /**
     * Performs this action for a single call chain.
     */
    protected void performAction(Project project, Editor editor, PsiFile file) {
        perform(getMethodCallAtCaret(file, editor), project, editor);
    }

    /**
     * Performs this action for one or more call chain under selection.
     */
    protected void performActionInBulk(Editor editor, PsiFile file) {
        var statement = ConvertVerificationIntentionBase.findFirstSelectedStatement(file, editor.getSelectionModel().getSelectionStart());
        while (statement != null && endOffsetOf(statement) <= editor.getSelectionModel().getSelectionEnd()) {
            var identifier = findChildOfType(statement, PsiIdentifier.class);
            var verificationIdentifier = getParentOfType(identifier, PsiMethodCallExpression.class);
            performAndCommitDocument(() -> perform(verificationIdentifier, editor.getProject(), editor));
            statement = getNextSiblingOfType(statement, PsiExpressionStatement.class);
        }
    }

    /**
     * Performs the actual conversion.
     */
    protected abstract void perform(PsiMethodCallExpression verificationCall, Project project, Editor editor);

    /**
     * Replaces the beginning of the text of the call chain with the provided replacement text.
     * <p>
     * For example:
     * <pre>
     * //The following text:
     * Mockito.verify(mock).doSomething();
     * //becomes:
     * BDDMockito.then(mock).doSomething();
     * </pre>
     *
     * @param calls        the call chain
     * @param replacement  the replacement text
     * @param mockitoClass the fully qualified class name to import. Either {@code org.mockito.Mockito}, or {@code org.mockito.BDDMockito}.
     */
    protected void replaceBeginningOfChain(List<PsiMethodCallExpression> calls, String replacement, String mockitoClass) {
        //end offset of Mockito.verify/BDDMockito.then
        int endOffset = endOffsetOf(getReferenceNameElement(calls.get(0)));
        performAndCommitDocument(() -> document.replaceString(calls.get(0).getTextOffset(), endOffset, replacement));
        importClass(mockitoClass, calls.get(0).getProject(), calls.get(0).getContainingFile());
    }

    /**
     * Replaces the beginning of the text of the call chain with the provided replacement text.
     * <p>
     * This differs from {@link #replaceBeginningOfChain(List, String, String)} only in that this method doesn't import
     * any class.
     * <p>
     * For example:
     * <pre>
     * //The following text:
     * Mockito.verify(mock).doSomething();
     * //becomes:
     * BDDMockito.then(mock).doSomething();
     * </pre>
     *
     * @param calls       the call chain
     * @param replacement the replacement text
     */
    protected void replaceBeginningOfChain(List<PsiMethodCallExpression> calls, String replacement) {
        //end offset of Mockito.verify/BDDMockito.then
        int endOffset = endOffsetOf(getReferenceNameElement(calls.get(0)));
        performAndCommitDocument(() -> document.replaceString(calls.get(0).getTextOffset(), endOffset, replacement));
    }

    /**
     * Imports the class the stubbing call chain starts with: either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
     */
    protected final void importClass(String fqn, Project project, PsiElement context) {
        PsiClass mockitoClass = JavaPsiFacade.getInstance(project).findClass(fqn, ProjectScope.getLibrariesScope(project));
        if (mockitoClass != null) {
            performAndCommitDocument(() -> ImportUtils.addImportIfNeeded(mockitoClass, context));
            documentManager.doPostponedOperationsAndUnblockDocument(document);
        }
    }

    /**
     * Creates an {@code org.mockito.InOrder} type local variable and inserts it right before the verification call
     * that is being converted.
     *
     * @param verificationCall the verification call being converted
     * @param calls the calls in the verification call chain
     * @return the PSI element of the added variable declaration statement
     */
    protected PsiElement createAndAddInOrderVariable(PsiMethodCallExpression verificationCall, List<PsiMethodCallExpression> calls) {
        //Create an InOrder object from the verified mock: 'InOrder inOrder = Mockito.InOrder(mock);'
        var project = verificationCall.getProject();
        importClass(MockitoQualifiedNames.ORG_MOCKITO_INORDER, project, verificationCall.getContainingFile());
        String inOrderVariableText = "InOrder inOrder = Mockito.inOrder(" + getFirstArgument(verificationCall).getText() + ");";
        var inOrderVariable = JavaPsiFacade.getElementFactory(project).createStatementFromText(inOrderVariableText, verificationCall);

        //Add the new variable to the parent block, before the Mockito.verify()/BDDMockito.then() call chain
        var parentBlock = PsiTreeUtil.getParentOfType(verificationCall, PsiCodeBlock.class);
        //'Iterables.getLast(calls)' returns the call expression for the whole call chain.
        // The addition has to happen to before the whole call chain, so it has the proper formatting and underlying PSI.
        var addedVariable = parentBlock.addBefore(inOrderVariable, Iterables.getLast(calls).getParent());
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
        return addedVariable;
    }

    protected final void performAndCommitDocument(Runnable runnable) {
        runnable.run();
        documentManager.commitDocument(document);
    }
}
