//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.PsiClassUtil.importClassAndCommit;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.Ranges.endOffsetOf;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Base action class for verification approach conversion.
 */
public abstract class ConvertVerificationActionBase extends AnAction {
    protected Editor editor;
    protected PsiDocumentManager documentManager;
    /**
     * Whether the conversion is caret- (single call chain) or selection-based (one or more call chains).
     */
    private final boolean isBulkMode;
    private InOrderVariableCreator inOrderCreator;

    protected ConvertVerificationActionBase(String actionText, boolean isBulkMode) {
        super(actionText);
        this.isBulkMode = isBulkMode;
    }

    //Perform action

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) return;
        Project project = e.getProject();

        runWriteCommandAction(project, () -> {
            documentManager = PsiDocumentManager.getInstance(project);
            inOrderCreator = new InOrderVariableCreator(editor.getDocument());
            if (isBulkMode) {
                var statementsInSelection = collectStatementsInSelection(editor, file);
                var firstVerification = getVerificationCall(statementsInSelection.get(0));
                performActionInBulk(statementsInSelection, firstVerification, collectCallsInChainFromFirst(firstVerification, true));
            } else performAction(project, file);
        });
    }

    /**
     * Performs this action for a single call chain, when the action is invoked under a single caret.
     */
    protected void performAction(Project project, PsiFile file) {
        perform(getMethodCallAtCaret(file, editor), project);
    }

    /**
     * Performs this action for one or more call chains under selection.
     */
    protected void performActionInBulk(List<PsiExpressionStatement> statementsInSelection,
                                       PsiMethodCallExpression firstVerification,
                                       List<PsiMethodCallExpression> callsInFirstVerification) {
        statementsInSelection.stream()
            .map(statement -> findChildOfType(statement, PsiIdentifier.class))
            .map(identifier -> getParentOfType(identifier, PsiMethodCallExpression.class))
            .forEach(verificationCall -> performAndCommitDocument(() -> perform(verificationCall, editor.getProject())));
    }

    /**
     * Common logic for caret- and selection-based conversions.
     */
    protected abstract void perform(PsiMethodCallExpression verificationCall, Project project);

    protected final void performAndCommitDocument(Runnable runnable) {
        runnable.run();
        documentManager.commitDocument(editor.getDocument());
    }

    //Helpers

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
    protected void replaceBeginningOfChain(List<PsiMethodCallExpression> calls, String replacement, @NotNull String... mockitoClass) {
        //end offset of Mockito.verify/BDDMockito.then
        var verifyOrThen = calls.get(0);
        int endOffset = endOffsetOf(getReferenceNameElement(verifyOrThen));
        performAndCommitDocument(() -> editor.getDocument().replaceString(verifyOrThen.getTextOffset(), endOffset, replacement));
        if (mockitoClass.length == 1)
            importClassAndCommit(mockitoClass[0], verifyOrThen.getProject(), verifyOrThen.getContainingFile(), editor.getDocument());
    }

    /**
     * Returns the verification call, e.g. Mockito.verify() from the argument expression statement.
     * That is the first PsiMethodCallExpression of the identifier in front of the call chain, e.g. Mockito, BDDMockito, or &lt;inOrder>
     * for an InOrder variable.
     */
    @Nullable
    protected PsiMethodCallExpression getVerificationCall(PsiExpressionStatement statement) {
        return getParentOfType(/*identifier*/findChildOfType(statement, PsiIdentifier.class), PsiMethodCallExpression.class);
    }

    //InOrder variable creation

    protected PsiElement createAndAddInOrderVariable(PsiMethodCallExpression verificationCall, List<PsiMethodCallExpression> calls) {
        return inOrderCreator.createAndAddInOrderVariable(verificationCall, calls);
    }

    protected PsiElement createAndAddInOrderVariable(PsiMethodCallExpression verificationCall, List<PsiMethodCallExpression> calls, String mockObjectsArgs) {
        return inOrderCreator.createAndAddInOrderVariable(verificationCall, calls, mockObjectsArgs);
    }

    //Variable renaming

    protected void rename(@NotNull PsiElement variable) {
        MemberInplaceRenameHelper.rename(variable.getFirstChild(), editor);
    }
}
