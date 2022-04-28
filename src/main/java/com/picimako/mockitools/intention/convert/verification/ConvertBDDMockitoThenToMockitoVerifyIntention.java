//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO_THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoThen;
import static com.picimako.mockitools.PsiMethodUtil.getSubsequentMethodCall;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;

/**
 * Converts {@code BDDMockito.then().should()} call chains to the {@code Mockito.verify()} approach.
 * <p>
 * The intention is available on {@code BDDMockito.then()} when it is followed by {@code should()} and a method call on the mock object.
 * <p>
 * It doesn't support the conversion of {@code InOrder} verification.
 * <p>
 * Conversion is possible only when{@link com.picimako.mockitools.inspection.EnforceConventionInspection} is disabled,
 * or it doesn't enforce {@code org.mockito.BDDMockito}.
 *
 * @since 0.4.0
 */
public class ConvertBDDMockitoThenToMockitoVerifyIntention extends ConvertVerificationIntentionBase {

    private static final CallMatcher THEN_SHOULD = CallMatcher.anyOf(
        instanceCall(ORG_MOCKITO_BDDMOCKITO_THEN, "should").parameterCount(0),
        instanceCall(ORG_MOCKITO_BDDMOCKITO_THEN, "should").parameterTypes(ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE)
    );

    public ConvertBDDMockitoThenToMockitoVerifyIntention() {
        super("BDDMockito.then()", "Mockito.verify()");
    }

    @Override
    protected boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        if (THEN.equals(getMethodName(methodCall)) && isBDDMockitoThen(methodCall) && !isBDDMockitoEnforced(methodCall)) {
            var should = getSubsequentMethodCall(methodCall);
            return THEN_SHOULD.matches(should) && hasSubsequentMethodCall(should);
        }
        return false;
    }
}
