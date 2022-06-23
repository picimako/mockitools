//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;

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
import com.intellij.psi.PsiFile;
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
    private final PsiDocumentManager documentManager;
    private final Project project;
    protected final Document document;
    private final PsiFile file;

    protected BaseConvertVerificationAction(Project project, Document document, PsiFile file, String actionText) {
        super(actionText);
        documentManager = PsiDocumentManager.getInstance(project);
        this.project = project;
        this.document = document;
        this.file = file;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) return;
        Project project = e.getProject();

        runWriteCommandAction(project, () -> performAction(project, editor, file));
    }

    protected abstract void performAction(Project project, Editor editor, PsiFile file);

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
        importClass(mockitoClass);
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
    protected final void importClass(String fqn) {
        PsiClass mockitoClass = JavaPsiFacade.getInstance(project).findClass(fqn, ProjectScope.getLibrariesScope(project));
        if (mockitoClass != null) {
            performAndCommitDocument(() -> ImportUtils.addImportIfNeeded(mockitoClass, file));
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
        importClass(MockitoQualifiedNames.ORG_MOCKITO_INORDER);
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

    protected final int endOffsetOf(@NotNull PsiElement element) {
        return element.getTextRange().getEndOffset();
    }
}
