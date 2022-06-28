//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;

/**
 * Converts {@code InOrder.verify()} call chains to the {@code Mockito.verify()} approach.
 * <p>
 * It keeps the original InOrder object, even if it is not used anymore.
 *
 * @since 0.5.0
 */
public class ConvertInOrderVerifyToMockitoVerifyAction extends BaseConvertVerificationAction {

    public ConvertInOrderVerifyToMockitoVerifyAction(Editor editor, boolean isBulkMode) {
        super(editor, "Mockito.verify()", isBulkMode);
    }

    @Override
    public void perform(PsiMethodCallExpression verificationCall, Project project, Editor editor) {
        var calls = collectCallsInChainFromFirst(verificationCall, true);
        importClass(ORG_MOCKITO_MOCKITO, project, verificationCall.getContainingFile());
        replaceBeginningOfChain(calls, "Mockito.verify");
    }
}
