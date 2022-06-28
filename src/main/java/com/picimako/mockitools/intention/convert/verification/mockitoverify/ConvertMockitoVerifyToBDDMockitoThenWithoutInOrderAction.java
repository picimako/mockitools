//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.MOCKITO_VERIFY;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.Ranges.endOffsetOf;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.callMatcher.CallMatcher;

/**
 * Converts {@code Mockito.verify()} call chains to the {@code BDDMockito.then().should()} approach.
 *
 * @since 0.4.0
 */
public final class ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction extends BaseConvertVerificationAction {
    private static final CallMatcher MOCKITO_VERIFY_MOCK = MOCKITO_VERIFY.parameterCount(1);
    private static final CallMatcher MOCKITO_VERIFY_MOCK_MODE = MOCKITO_VERIFY.parameterCount(2);

    public ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(Editor editor) {
        super(editor, MockitoolsBundle.message("intention.convert.verification.bddmockito.without.inorder"));
    }

    @Override
    protected void perform(PsiMethodCallExpression mockitoVerify, Project project, Editor editor) {
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        if (MOCKITO_VERIFY_MOCK.matches(mockitoVerify)) {
            //Replace Mockito.verify with BDDMockito.then
            replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);
            //Insert should() after BDDMockito.then
            performAndCommitDocument(() -> document.insertString(getReferenceNameElement(getLast(calls)).getTextOffset() - 1, ".should()"));
        } else if (MOCKITO_VERIFY_MOCK_MODE.matches(mockitoVerify)) {
            //It replaces the text between the two arguments of Mockito.verify() with the text ").should("
            //E.g. 'Mockito.verify(mock, times(2))' becomes 'Mockito.verify(mock).should(times(2))'
            document.replaceString(endOffsetOf(getFirstArgument(mockitoVerify)), get2ndArgument(mockitoVerify).getTextOffset(), ").should(");
            //Replace Mockito.verify with BDDMockito.then
            replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);
        }
    }
}
