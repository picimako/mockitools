//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO_THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC_VERIFICATION;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoVerify;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getSubsequentMethodCall;
import static com.picimako.mockitools.util.PsiMethodUtil.hasSubsequentMethodCall;
import static com.siyeh.ig.callMatcher.CallMatcher.anyOf;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection;
import com.siyeh.ig.callMatcher.CallMatcher;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an approach of verifying methods in Mockito.
 *
 * @since 0.7.0
 */
@RequiredArgsConstructor
public enum VerificationApproach {

    MOCKITO_VERIFY("Mockito.verify()") {
        @Override
        public boolean isVerifiedBy(PsiMethodCallExpression expression) {
            return isMockitoVerify(expression);
        }

        @Override
        public boolean isValid(PsiMethodCallExpression verificationCall) {
            return hasSubsequentMethodCall(verificationCall);
        }

        @Override
        public boolean isInOrderSpecific(PsiMethodCallExpression expression) {
            return false;
        }

        @Override
        public Optional<PsiMethodCallExpression> getVerifiedMethodCall(List<PsiMethodCallExpression> verificationCalls) {
            return Optional.of(verificationCalls.get(1));
        }
    },
    BDDMOCKITO_THEN_SHOULD("BDDMockito.then()") {
        private final CallMatcher BDDMOCKITO_THEN = staticCall(ORG_MOCKITO_BDDMOCKITO, THEN).parameterCount(1);
        private final CallMatcher.Simple SHOULD = instanceCall(ORG_MOCKITO_BDDMOCKITO_THEN, "should");
        private final CallMatcher THEN_SHOULD_WITHOUT_INORDER = anyOf(
            SHOULD.parameterCount(0),
            SHOULD.parameterTypes(ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE));
        private final CallMatcher THEN_SHOULD_WITH_INORDER = anyOf(
            SHOULD.parameterTypes(ORG_MOCKITO_INORDER),
            SHOULD.parameterTypes(ORG_MOCKITO_INORDER, ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE)
        );
        private final CallMatcher THEN_SHOULD = anyOf(THEN_SHOULD_WITHOUT_INORDER, THEN_SHOULD_WITH_INORDER);

        @Override
        public boolean isVerifiedBy(PsiMethodCallExpression expression) {
            return BDDMOCKITO_THEN.matches(expression);
        }

        @Override
        public boolean isValid(PsiMethodCallExpression verificationCall) {
            var should = getSubsequentMethodCall(verificationCall);
            return THEN_SHOULD.matches(should) && hasSubsequentMethodCall(should);
        }

        @Override
        public boolean isInOrderSpecific(PsiMethodCallExpression should) {
            return THEN_SHOULD_WITH_INORDER.matches(should) && !THEN_SHOULD_WITHOUT_INORDER.matches(should);
        }

        @Override
        public Optional<PsiMethodCallExpression> getVerifiedMethodCall(List<PsiMethodCallExpression> verificationCalls) {
            return Optional.of(verificationCalls.get(2));
        }
    },
    INORDER_VERIFY("InOrder.verify()") {
        @Override
        public boolean isVerifiedBy(PsiMethodCallExpression expression) {
            return EnforceConventionInspection.IN_ORDER_VERIFY_NON_MOCKED_STATIC.matches(expression);
        }

        @Override
        public boolean isValid(PsiMethodCallExpression verificationCall) {
            return hasSubsequentMethodCall(verificationCall);
        }

        @Override
        public boolean isInOrderSpecific(PsiMethodCallExpression expression) {
            return true;
        }

        @Override
        public Optional<PsiMethodCallExpression> getVerifiedMethodCall(List<PsiMethodCallExpression> verificationCalls) {
            return Optional.of(verificationCalls.get(1));
        }
    },
    INORDER_VERIFY_MOCKED_STATIC("InOrder.verify()") {
        private final CallMatcher IN_ORDER_VERIFY_MOCKED_STATIC = CallMatcher.anyOf(
            instanceCall(ORG_MOCKITO_INORDER, VERIFY).parameterTypes(ORG_MOCKITO_MOCKED_STATIC, ORG_MOCKITO_MOCKED_STATIC_VERIFICATION),
            instanceCall(ORG_MOCKITO_INORDER, VERIFY).parameterCount(3));

        @Override
        public boolean isVerifiedBy(PsiMethodCallExpression expression) {
            return IN_ORDER_VERIFY_MOCKED_STATIC.matches(expression);
        }

        @Override
        public boolean isInOrderSpecific(PsiMethodCallExpression expression) {
            return true;
        }

        @Override
        public Optional<PsiMethodCallExpression> getVerifiedMethodCall(List<PsiMethodCallExpression> verificationCalls) {
            return Optional.empty();
//            return Optional.of(get2ndArgument(calls.get(0)));
        }
    },
    MOCKED_STATIC_VERIFY("MockedStatic.verify()") {
        private final CallMatcher MOCKED_STATIC_VERIFY = CallMatcher.anyOf(
            instanceCall(ORG_MOCKITO_MOCKED_STATIC, VERIFY).parameterCount(1),
            //with mode
            instanceCall(ORG_MOCKITO_MOCKED_STATIC, VERIFY).parameterCount(2));

        @Override
        public boolean isVerifiedBy(PsiMethodCallExpression expression) {
            return MOCKED_STATIC_VERIFY.matches(expression);
        }

        @Override
        public boolean isInOrderSpecific(PsiMethodCallExpression expression) {
            return false;
        }

        @Override
        public Optional<PsiMethodCallExpression> getVerifiedMethodCall(List<PsiMethodCallExpression> verificationCalls) {
            return Optional.empty();
//            return Optional.of(getFirstArgument(calls.get(0)));
        }
    };

    public static final Set<VerificationApproach> NON_MOCKED_STATIC_APPROACHES = Set.of(MOCKITO_VERIFY, BDDMOCKITO_THEN_SHOULD, INORDER_VERIFY);

    public final String presentableText;

    /**
     * Returns whether the argument method call is the call in a verification call chain that accepts the mock object
     * that is being verified.
     * <p>
     * For example:
     * <pre>
     * Mockito.verify(mock).doSomething(); //returns true if 'Mockito.verify(mock)' is passed in
     * BDDMockito.then(mock).should().doSomething(); //return true if 'BDDMockito.then(mock)' is passed in
     * </pre>
     *
     * @param expression the verification method accepting the mock object
     */
    public abstract boolean isVerifiedBy(PsiMethodCallExpression expression);

    /**
     * Returns whether the argument verification call is valid and meets preconditions to validate the call chain,
     * or extract information from it.
     *
     * @param verificationCall the verification call (e.g. Mockito.verify()) in the call chain constituting as a verification
     */
    public boolean isValid(PsiMethodCallExpression verificationCall) {
        return true;
    }

    /**
     * Returns whether this verification call chain uses {@code InOrder} to verify method calls.
     * <p>
     * For example:
     * <pre>
     * inOrder.verify(mock).doSomething(); //returns true
     * BDDMockito.then(mock).should(inOrder).doSomething(); //return true if 'should(inOrder)' is passed in
     * </pre>
     * <p>
     * This is introduced primarily for {@link #BDDMOCKITO_THEN_SHOULD}, since it handles the original and InOrder
     * specific verification within the same API.
     *
     * @param expression the verification method accepting the mock object, or in case of BDDMockito, then {@code should} call
     */
    public abstract boolean isInOrderSpecific(PsiMethodCallExpression expression);

    /**
     * Finds and returns the verified method call from the argument verification call chain.
     * <p>
     * For example:
     * <pre>
     * //Returns 'mock.doSomething()' is both cases:
     *
     * Mockito.verify(mock).doSomething();
     * BDDMockito.then(mock).should().doSomething();
     * </pre>
     *
     * @param verificationCalls the calls in the call chain constituting as a verification
     * @return the found call as PsiMethodCallExpression, or empty Optional if the call is not found
     */
    public abstract Optional<PsiMethodCallExpression> getVerifiedMethodCall(List<PsiMethodCallExpression> verificationCalls);

    /**
     * Returns the mock argument used in the provided verification call.
     *
     * @param verificationCall the verification method call
     */
    public PsiExpression getVerifiedMock(PsiMethodCallExpression verificationCall) {
        return getFirstArgument(verificationCall);
    }
}
