//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.MOCKITO_VERIFY;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.callMatcher.CallMatcher;

import java.util.List;

/**
 * Converts {@code Mockito.verify()} call chains to {@code BDDMockito.then().should()}.
 *
 * @since 0.4.0
 */
public final class ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction extends ConvertVerificationActionBase {
    private static final CallMatcher MOCKITO_VERIFY_MOCK = MOCKITO_VERIFY.parameterCount(1);
    private static final CallMatcher MOCKITO_VERIFY_MOCK_MODE = MOCKITO_VERIFY.parameterCount(2);

    public ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(boolean isBulkMode) {
        super(MockitoolsBundle.message("intention.convert.verification.bddmockito.without.inorder"), isBulkMode);
    }

    @Override
    protected void perform(PsiMethodCallExpression mockitoVerify, Project project) {
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        if (MOCKITO_VERIFY_MOCK.matches(mockitoVerify)) {
            //Replace Mockito.verify with BDDMockito.then
            replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);
            //Insert should() after BDDMockito.then
            performAndCommitDocument(() -> editor.getDocument().insertString(getReferenceNameElement(getLast(calls)).getTextOffset() - 1, ".should()"));
        } else if (MOCKITO_VERIFY_MOCK_MODE.matches(mockitoVerify)) {
            //It replaces the text between the two arguments of Mockito.verify() with the text ").should("
            //E.g. 'Mockito.verify(mock, times(2))' becomes 'Mockito.verify(mock).should(times(2))'
            editor.getDocument().replaceString(endOffsetOf(getFirstArgument(mockitoVerify)), get2ndArgument(mockitoVerify).getTextOffset(), ").should(");
            //Replace Mockito.verify with BDDMockito.then
            replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);
        }
    }

    @Override
    protected void performActionInBulk(List<PsiExpressionStatement> statementsInSelection,
                                       PsiMethodCallExpression firstVerification,
                                       List<PsiMethodCallExpression> callsInFirstVerification) {
        //Verifications can be converted one by one without any additional logic
        statementsInSelection.forEach(statement -> perform(getVerificationCall(statement), editor.getProject()));
    }
}
