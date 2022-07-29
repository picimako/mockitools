//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.picimako.mockitools.FromSelectionDataRetriever.collectMockObjects;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasTwoArguments;
import static com.picimako.mockitools.Ranges.endOffsetOf;
import static com.picimako.mockitools.intention.convert.verification.bddmockitothen.ConvertFromBDDMockitoThenIntention.THEN_SHOULD_WITHOUT_INORDER;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.TriConsumer;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;

import java.util.List;

/**
 * Converts {@code BDDMockito.then().should()} call chains to the {@code InOrder.verify()} approach.
 * <p>
 * If the {@code BDDMockito.then().should()} call chain is InOrder specific, it uses that InOrder object during
 * conversion, otherwise it creates a new InOrder variable right before the verification call chain.
 * <p>
 * In bulk mode, it converts the verifications in almost the same way, except that it creates a single common InOrder
 * variable and uses it in all selected verifications after conversion.
 *
 * @since 0.5.0
 */
public class ConvertBDDMockitoThenToInOrderVerifyAction extends ConvertVerificationActionBase {
    public ConvertBDDMockitoThenToInOrderVerifyAction(boolean isBulkMode) {
        super("InOrder.verify()", isBulkMode);
    }

    //Caret based conversion

    @Override
    protected void perform(PsiMethodCallExpression bddMockitoThen, Project project) {
        var calls = collectCallsInChainFromFirst(bddMockitoThen, true);
        var should = calls.get(1);

        //If there is no InOrder argument, then create one, and convert to inOrder.verify
        if (THEN_SHOULD_WITHOUT_INORDER.matches(should)) {
            //Get verification mode argument from 'should()', or empty string if there's none
            var inOrderVariable = createAndAddInOrderVariable(bddMockitoThen, calls);
            convertWithoutInOrder(bddMockitoThen, calls, should);
            MemberInplaceRenameHelper.rename(inOrderVariable.getFirstChild(), editor);
        }
        //If there is InOrder argument, don't create one, just use that for the inOrder.verify
        else {
            convertWithInOrder(bddMockitoThen, calls, should);
        }
    }

    //Selection based conversion

    @Override
    protected void performActionInBulk(List<PsiExpressionStatement> statementsInSelection,
                                       PsiMethodCallExpression firstVerification,
                                       List<PsiMethodCallExpression> callsInFirstVerification) {
        if (THEN_SHOULD_WITHOUT_INORDER.matches(callsInFirstVerification.get(1))) {
            //Create the InOrder variable before converting any of the selected call chains, so that they can use the same InOrder variable.
            var inOrderVariable = createAndAddInOrderVariable(firstVerification, callsInFirstVerification, collectMockObjects(statementsInSelection));
            convertWithinSelection(this::convertWithoutInOrder, statementsInSelection);
            MemberInplaceRenameHelper.rename(inOrderVariable.getFirstChild(), editor);
        } else {
            convertWithinSelection(this::convertWithInOrder, statementsInSelection);
        }
    }

    /**
     * Go through all selected call chains, convert them one by one, adding the same InOrder variable.
     */
    private void convertWithinSelection(TriConsumer<PsiMethodCallExpression, List<PsiMethodCallExpression>, PsiMethodCallExpression> converter,
                                        List<PsiExpressionStatement> statementsInSelection) {
        statementsInSelection.stream()
            .map(this::getVerificationCall)
            .forEach(bddMockitoThen -> {
                var calls = collectCallsInChainFromFirst(bddMockitoThen, true);
                converter.accept(bddMockitoThen, calls, /*should*/calls.get(1));
            });
    }

    //Helpers

    private void convertWithoutInOrder(PsiMethodCallExpression bddMockitoThen, List<PsiMethodCallExpression> calls, PsiMethodCallExpression should) {
        String verificationModeArgument = hasArgument(should) ? ", " + getFirstArgument(should).getText() : "";
        replaceBDDMockitoVerification(bddMockitoThen, calls, verificationModeArgument, "inOrder");
    }

    private void convertWithInOrder(PsiMethodCallExpression bddMockitoThen, List<PsiMethodCallExpression> calls, PsiMethodCallExpression should) {
        String inOrderArgument = getFirstArgument(should).getText();
        //Get verification mode argument from 'should()', or empty string if there's none
        String verificationModeArgument = hasTwoArguments(should) ? ", " + get2ndArgument(should).getText() : "";
        replaceBDDMockitoVerification(bddMockitoThen, calls, verificationModeArgument, inOrderArgument);
    }

    private void replaceBDDMockitoVerification(PsiMethodCallExpression bddMockitoThen, List<PsiMethodCallExpression> calls,
                                               String verificationModeArgument, String inOrderArgument) {
        editor.getDocument().insertString(endOffsetOf(getFirstArgument(bddMockitoThen)), verificationModeArgument);
        //Replace BDDMockito.then with <InOrder argument>.verify
        replaceBeginningOfChain(calls, inOrderArgument + ".verify");
        //Delete should() with its arguments, if any
        performAndCommitDocument(() -> editor.getDocument().deleteString(endOffsetOf(bddMockitoThen), endOffsetOf(calls.get(1))));
    }
}
