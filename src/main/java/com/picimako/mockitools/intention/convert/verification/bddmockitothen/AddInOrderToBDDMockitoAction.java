//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.siyeh.ig.psiutils.ExpressionUtils.getFirstExpressionInList;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Creates an InOrder variable right before the {@code BDDMockito.then().should()} call chain,
 * and parameterizes the {@code should()} call with that InOrder variable.
 * <p>
 * This action doesn't reuse any, potentially existing InOrder objects.
 * <p>
 * In bulk mode, instead of creating a separate {@code InOrder} local variable for each selected call, it creates a single one,
 * and adds that one to all selected calls.
 *
 * @since 0.5.0
 */
public class AddInOrderToBDDMockitoAction extends ConvertVerificationActionBase {
    public AddInOrderToBDDMockitoAction(boolean isBulkMode) {
        super(MockitoolsBundle.message("intention.convert.verification.bddmockito.with.inorder"), isBulkMode);
    }

    //Caret based conversion

    @Override
    protected void perform(PsiMethodCallExpression bddMockitoThen, Project project) {
        var calls = collectCallsInChainFromFirst(bddMockitoThen, true);
        var inOrderVariable = createAndAddInOrderVariable(bddMockitoThen, calls);

        addInOrderVariableToShould(calls.get(1), createInOrderRefExpression(inOrderVariable, calls), editor);

        MemberInplaceRenameHelper.rename(inOrderVariable.getFirstChild(), editor);
    }

    //Selection based conversion

    @Override
    protected void performActionInBulk(List<PsiExpressionStatement> statementsInSelection,
                                       PsiMethodCallExpression firstVerification,
                                       List<PsiMethodCallExpression> callsInFirstVerification) {
        //Create the InOrder variable before converting any of the selected call chains, so that they can use the same InOrder variable.
        var inOrderVariable = createAndAddInOrderVariable(firstVerification, callsInFirstVerification);
        var inOrderRefExpression = createInOrderRefExpression(inOrderVariable, callsInFirstVerification);

        //Go through all selected call chains, convert them one by one, adding the same InOrder variable.
        statementsInSelection.stream()
            .map(this::getVerificationCall)
            .map(bddMockitoThen -> collectCallsInChainFromFirst(bddMockitoThen, true))
            .forEach(calls -> performAndCommitDocument(() -> addInOrderVariableToShould(calls.get(1), inOrderRefExpression, editor)));

        MemberInplaceRenameHelper.rename(inOrderVariable.getFirstChild(), editor);
    }

    //Helpers

    /**
     * Creates a reference expression from the argument {@code inOrderVariable}, so that reference can be
     * added as the argument of {@code should()}.
     *
     * @param inOrderVariable the InOrder-type local variable
     * @param calls           the calls in the call chain, to retrieve the should() call from
     */
    @NotNull
    private PsiExpression createInOrderRefExpression(PsiElement inOrderVariable, List<PsiMethodCallExpression> calls) {
        return JavaPsiFacade.getElementFactory(inOrderVariable.getProject())
            .createExpressionFromText(((PsiLocalVariable) inOrderVariable.getFirstChild()).getName(), /*should*/calls.get(1));
    }

    /**
     * Adds the InOrder reference expression to the argument list of the {@code should()} call.
     */
    private void addInOrderVariableToShould(PsiMethodCallExpression should, PsiExpression inOrderRefExpression, Editor editor) {
        var shouldArgumentList = should.getArgumentList();
        if (shouldArgumentList.isEmpty())
            shouldArgumentList.add(inOrderRefExpression);
        else
            shouldArgumentList.addBefore(inOrderRefExpression, getFirstExpressionInList(shouldArgumentList));
        documentManager.doPostponedOperationsAndUnblockDocument(editor.getDocument());
    }
}
