//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.util.PsiClassUtil;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;

/**
 * Converts {@code InOrder.verify()} call chains to {@code Mockito.verify()}, omitting the InOrder
 * variable altogether in the resulting call chain.
 * <p>
 * It keeps the original InOrder variable, even if it is not used anymore.
 *
 * @since 0.5.0
 */
public class ConvertInOrderVerifyToMockitoVerifyAction extends ConvertVerificationActionBase {

    public ConvertInOrderVerifyToMockitoVerifyAction(boolean isBulkMode) {
        super("Mockito.verify()", isBulkMode);
    }

    @Override
    public void perform(PsiMethodCallExpression verificationCall, Project project) {
        var calls = collectCallsInChainFromFirst(verificationCall, true);
        PsiClassUtil.importClassAndCommit(ORG_MOCKITO_MOCKITO, project, verificationCall.getContainingFile(), editor.getDocument());
        replaceBeginningOfChain(calls, "Mockito.verify");
    }
}
