//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.siyeh.ig.psiutils.ExpressionUtils.getFirstExpressionInList;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Creates an InOrder variable right before the {@code BDDMockito.then().should()} call chain,
 * and parameterizes the {@code should()} call with that InOrder variable.
 * <p>
 * This action doesn't reuse any, potentially existing InOrder objects.
 *
 * @since 0.5.0
 */
public class AddInOrderToBDDMockitoAction extends BaseConvertVerificationAction {
    public AddInOrderToBDDMockitoAction(Project project, Document document, PsiFile file) {
        super(project, document, file, MockitoolsBundle.message("intention.convert.verification.bddmockito.with.inorder"));
    }

    @Override
    protected void performAction(Project project, Editor editor, PsiFile file) {
        var bddMockitoThen = getMethodCallAtCaret(file, editor);
        var calls = collectCallsInChainFromFirst(bddMockitoThen, true);

        PsiElement addedVariable = createAndAddInOrderVariable(bddMockitoThen, calls);

        var inOrderRefExpression = JavaPsiFacade.getElementFactory(project)
            .createExpressionFromText(((PsiLocalVariable) addedVariable.getFirstChild()).getName(), calls.get(1));
        var shouldArgumentList = calls.get(1).getArgumentList();
        if (shouldArgumentList.isEmpty())
            shouldArgumentList.add(inOrderRefExpression);
        else
            shouldArgumentList.addBefore(inOrderRefExpression, getFirstExpressionInList(shouldArgumentList));
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());

        MemberInplaceRenameHelper.rename(addedVariable.getFirstChild(), editor);
    }
}
