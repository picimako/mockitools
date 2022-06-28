//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasTwoArguments;
import static com.picimako.mockitools.Ranges.endOffsetOf;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MockitoQualifiedNames;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Converts {@code InOrder.verify()} call chains to the {@code BDDMockito.then()}, omitting the InOrder
 * variable altogether in the resulting call chain.
 * <p>
 * It keeps the original InOrder object, even if it is not used anymore.
 *
 * @since 0.5.0
 */
public class ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction extends BaseConvertVerificationAction {

    public ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(Editor editor, boolean isBulkMode) {
        super(editor, MockitoolsBundle.message("intention.convert.verification.bddmockito.without.inorder"), isBulkMode);
    }

    @Override
    protected void perform(PsiMethodCallExpression inOrderVerify, Project project, Editor editor) {
        var calls = collectCallsInChainFromFirst(inOrderVerify, true);

        //Replace '<inorder>.verify' with 'BDDMockito.then'
        replaceBeginningOfChain(calls, "BDDMockito.then", MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO);
        //It replaces the text between the two arguments of InOrder.verify() with the text ").should("
        //E.g. 'inOrder.verify(mock)' becomes 'BDDMockito.then(mock).should()'
        //E.g. 'inOrder.verify(mock, times(2))' becomes 'BDDMockito.then(mock).should(times(2))'
        int endOffsetOfMockArgument = endOffsetOf(getFirstArgument(inOrderVerify));
        document.replaceString(
            endOffsetOfMockArgument,
            hasTwoArguments(inOrderVerify) ? get2ndArgument(inOrderVerify).getTextOffset() : endOffsetOfMockArgument,
            ").should(");
    }
}
