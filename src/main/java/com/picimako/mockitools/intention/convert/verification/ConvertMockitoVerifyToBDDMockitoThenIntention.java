//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoVerify;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.psi.PsiMethodCallExpression;

/**
 * Converts {@code Mockito.verify()} call chains to the {@code BDDMockito.then().should()} approach.
 * <p>
 * The intention is available on {@code Mockito.verify()} when it is followed by a method call on the mock object.
 * <p>
 * It doesn't support the conversion of {@code InOrder} verification.
 *
 * @since 0.4.0
 */
public class ConvertMockitoVerifyToBDDMockitoThenIntention extends ConvertVerificationIntentionBase {

    public ConvertMockitoVerifyToBDDMockitoThenIntention() {
        super("Mockito.verify()", "BDDMockito.then()");
    }

    @Override
    protected boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return VERIFY.equals(getMethodName(methodCall)) && isMockitoVerify(methodCall) && hasSubsequentMethodCall(methodCall);
    }
}
