//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.CallChainEndsWith.ENDS_WITH_GIVEN;
import static com.picimako.mockitools.CallChainEndsWith.ENDS_WITH_WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDD_MY_ONGOING_STUBBING;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDD_STUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ONGOING_STUBBING;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_STUBBING_BASESTUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_STUBBING_STUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;
import static com.picimako.mockitools.util.PsiMethodUtil.findCallDownwardsInChain;
import static com.picimako.mockitools.util.PsiMethodUtil.findCallUpwardsInChain;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.util.PsiMethodUtil.isMethodCall;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.picimako.mockitools.inspection.ExceptionStubber;
import com.picimako.mockitools.util.PsiMethodUtil;
import com.siyeh.ig.callMatcher.CallMatcher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents an approach of stubbing methods in Mockito.
 *
 * @since 0.7.0
 */
@RequiredArgsConstructor
@Getter
public enum StubbingApproach {

    /**
     * Mockito.when().then*()
     */
    MOCKITO_WHEN(StubType.STUBBING, "when", "Mockito.when()", ORG_MOCKITO_MOCKITO, "Mockito", THEN) {
        private final CallMatcher MOCKITO_WHEN = staticCall(ORG_MOCKITO_MOCKITO, WHEN).parameterCount(1);
        private final CallMatcher MOCKITO_WHEN_THEN_X = instanceCall(ORG_MOCKITO_ONGOING_STUBBING,
            "then", "thenReturn", "thenThrow", "thenAnswer", "thenCallRealMethod");
        // Mockito.when().thenThrow()[.thenThrow()]
        public final ExceptionStubber WHEN_THEN_THROW =
            new ExceptionStubber(THEN_THROW, ORG_MOCKITO_ONGOING_STUBBING, null);

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(PsiMethodCallExpression mockitoWhen) {
            //first argument (the stubbed method call): mock.method(argument1, argument2)
            return Optional.ofNullable(getFirstArgument(mockitoWhen))
                .filter(PsiMethodCallExpression.class::isInstance)
                .map(PsiMethodCallExpression.class::cast);
        }

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(List<PsiMethodCallExpression> stubbingCalls) {
            return getStubbedMethodCall(stubbingCalls.get(0));
        }

        @Override
        public Optional<PsiExpression> getStubbedMethodCallAnywhere(PsiMethodCallExpression stubCall) {
            return findCallUpwardsInChain(stubCall, methodCallStubber).map(PsiMethodUtil::getFirstArgument).filter(this::isValidStubbingArgument);
        }

        @Override
        public boolean isStubbedBy(PsiMethodCallExpression expression) {
            return MOCKITO_WHEN.matches(expression);
        }

        @Override
        public boolean isAnyOfStubs(PsiMethodCallExpression expression) {
            return MOCKITO_WHEN_THEN_X.matches(expression);
        }

        @Override
        public boolean isValid(PsiMethodCallExpression stubbingCall) {
            return hasSubsequentMethodCall(stubbingCall);
        }

        @Override
        public boolean isValidStubbingArgument(PsiExpression mockObjectOrCallToStubbedMethod) {
            return isMethodCall(mockObjectOrCallToStubbedMethod);
        }

        @NotNull
        @Override
        public ExceptionStubber getExceptionStubber() {
            return WHEN_THEN_THROW;
        }
    },
    /**
     * Mockito.do*().when()
     */
    MOCKITO_DO_X(StubType.STUBBER, "when", "Mockito.do*()", ORG_MOCKITO_MOCKITO, "Mockito", "do") {
        private final CallMatcher MOCKITO_DO_X_WHEN = instanceCall(ORG_MOCKITO_STUBBING_STUBBER, WHEN);
        private final CallMatcher MOCKITO_DO_X =
            staticCall(ORG_MOCKITO_MOCKITO, "doReturn", "doThrow", "doAnswer", "doCallRealMethod", "doNothing");
        // Mockito.doThrow()[.doThrow()].when()
        public final ExceptionStubber DO_THROW_WHEN =
            new ExceptionStubber(DO_THROW, ORG_MOCKITO_STUBBING_BASESTUBBER, ORG_MOCKITO_MOCKITO);

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(PsiMethodCallExpression mockitoDoXWhen) {
            return hasSubsequentMethodCall(mockitoDoXWhen)
                /* The grandparent PsiMethodCallExpression is the subsequent method call in the call chain
                 * e.g. mockitoDoXWhen is "Mockito.doReturn(10).when(mock)"
                 * while grandparent is "Mockito.doReturn(10).when(mock).methodWithParams(String.class, Integer.class);"*/
                ? Optional.of(mockitoDoXWhen).map(PsiElement::getParent).map(PsiElement::getParent).map(PsiMethodCallExpression.class::cast)
                : Optional.empty();
        }

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(List<PsiMethodCallExpression> stubbingCalls) {
            return Optional.of(getLast(stubbingCalls));
        }

        @Override
        public Optional<PsiExpression> getStubbedMethodCallAnywhere(PsiMethodCallExpression stubCall) {
            return findCallDownwardsInChain(stubCall, methodCallStubber).map(PsiMethodUtil::getFirstArgument).filter(this::isValidStubbingArgument);
        }

        @Override
        public boolean isStubbedBy(PsiMethodCallExpression expression) {
            return MOCKITO_DO_X_WHEN.matches(expression);
        }

        @Override
        public boolean isAnyOfStubs(PsiMethodCallExpression expression) {
            return MOCKITO_DO_X.matches(expression);
        }

        @Override
        public boolean isValid(List<PsiMethodCallExpression> stubbingCalls) {
            return ENDS_WITH_WHEN.analyze(stubbingCalls);
        }

        @Override
        public boolean isValidStubbingArgument(PsiExpression mockObjectOrCallToStubbedMethod) {
            return mockObjectOrCallToStubbedMethod instanceof PsiReferenceExpression;
        }

        @NotNull
        @Override
        public ExceptionStubber getExceptionStubber() {
            return DO_THROW_WHEN;
        }
    },
    /**
     * BDDMockito.given().will*()
     */
    BDDMOCKITO_GIVEN(StubType.STUBBING, "given", "BDDMockito.given()", ORG_MOCKITO_BDDMOCKITO, "BDDMockito", "will") {
        private final CallMatcher BDDMOCKITO_GIVEN = staticCall(ORG_MOCKITO_BDDMOCKITO, GIVEN).parameterCount(1);
        private final CallMatcher BDDMOCKITO_GIVEN_WILL_X = instanceCall(ORG_MOCKITO_BDD_MY_ONGOING_STUBBING,
            "will", "willReturn", "willThrow", "willAnswer", "willCallRealMethod");
        // BDDMockito.given().willThrow()[.willThrow()]
        public final ExceptionStubber GIVEN_WILL_THROW =
            new ExceptionStubber(WILL_THROW, ORG_MOCKITO_BDD_MY_ONGOING_STUBBING, null);

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(PsiMethodCallExpression bddMockitoGiven) {
            return Optional.ofNullable(getFirstArgument(bddMockitoGiven))
                .filter(PsiMethodCallExpression.class::isInstance)
                .map(PsiMethodCallExpression.class::cast);
        }

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(List<PsiMethodCallExpression> stubbingCalls) {
            return getStubbedMethodCall(stubbingCalls.get(0));
        }

        @Override
        public Optional<PsiExpression> getStubbedMethodCallAnywhere(PsiMethodCallExpression stubCall) {
            return findCallUpwardsInChain(stubCall, methodCallStubber).map(PsiMethodUtil::getFirstArgument).filter(this::isValidStubbingArgument);
        }

        @Override
        public boolean isStubbedBy(PsiMethodCallExpression expression) {
            return BDDMOCKITO_GIVEN.matches(expression);
        }

        @Override
        public boolean isAnyOfStubs(PsiMethodCallExpression expression) {
            return BDDMOCKITO_GIVEN_WILL_X.matches(expression);
        }

        @Override
        public boolean isValid(PsiMethodCallExpression stubbingCall) {
            return hasSubsequentMethodCall(stubbingCall);
        }

        @Override
        public boolean isValidStubbingArgument(PsiExpression mockObjectOrCallToStubbedMethod) {
            return isMethodCall(mockObjectOrCallToStubbedMethod);
        }

        @NotNull
        @Override
        public ExceptionStubber getExceptionStubber() {
            return GIVEN_WILL_THROW;
        }
    },
    /**
     * BDDMockito.will*().given()
     */
    BDDMOCKITO_WILL_X(StubType.STUBBER, "given", "BDDMockito.will*()", ORG_MOCKITO_BDDMOCKITO, "BDDMockito", "will") {
        private final CallMatcher BDDMOCKITO_WILL_X =
            staticCall(ORG_MOCKITO_BDDMOCKITO, "will", "willReturn", "willThrow", "willAnswer", "willCallRealMethod", "willDoNothing");
        private final CallMatcher BDDMOCKITO_WILL_X_GIVEN =
            instanceCall(ORG_MOCKITO_BDD_STUBBER, "given").parameterCount(1);
        // BDDMockito.willThrow()[.willThrow()].given()
        public final ExceptionStubber WILL_THROW_GIVEN =
            new ExceptionStubber(WILL_THROW, ORG_MOCKITO_BDD_STUBBER, ORG_MOCKITO_BDDMOCKITO);

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(PsiMethodCallExpression bddMockitoWillXGiven) {
            return hasSubsequentMethodCall(bddMockitoWillXGiven)
                ? Optional.of(bddMockitoWillXGiven).map(PsiElement::getParent).map(PsiElement::getParent).map(PsiMethodCallExpression.class::cast)
                : Optional.empty();
        }

        @Override
        public Optional<PsiMethodCallExpression> getStubbedMethodCall(List<PsiMethodCallExpression> stubbingCalls) {
            return Optional.of(getLast(stubbingCalls));
        }

        @Override
        public Optional<PsiExpression> getStubbedMethodCallAnywhere(PsiMethodCallExpression stubCall) {
            return findCallDownwardsInChain(stubCall, methodCallStubber).map(PsiMethodUtil::getFirstArgument).filter(this::isValidStubbingArgument);
        }

        @Override
        public boolean isStubbedBy(PsiMethodCallExpression expression) {
            return BDDMOCKITO_WILL_X_GIVEN.matches(expression);
        }

        @Override
        public boolean isAnyOfStubs(PsiMethodCallExpression expression) {
            return BDDMOCKITO_WILL_X.matches(expression);
        }

        @Override
        public boolean isValid(List<PsiMethodCallExpression> stubbingCalls) {
            return ENDS_WITH_GIVEN.analyze(stubbingCalls);
        }

        @Override
        public boolean isValidStubbingArgument(PsiExpression mockObjectOrCallToStubbedMethod) {
            return mockObjectOrCallToStubbedMethod instanceof PsiReferenceExpression;
        }

        @NotNull
        @Override
        public ExceptionStubber getExceptionStubber() {
            return WILL_THROW_GIVEN;
        }
    };

    public static final List<ExceptionStubber> EXCEPTION_STUBBERS = List.of(
        MOCKITO_WHEN.getExceptionStubber(),
        MOCKITO_DO_X.getExceptionStubber(),
        BDDMOCKITO_GIVEN.getExceptionStubber(),
        BDDMOCKITO_WILL_X.getExceptionStubber());

    public final StubType stubType;
    public final String methodCallStubber;
    public final String presentableText;
    public final String stubStarterClassFqn;
    public final String stubStarterClassName;
    public final String stubPrefix;

    /**
     * For example:
     * <pre>
     * //Returns 'mock.doSomething()' if 'Mockito.when(mock.doSomething())' is passed in
     * Mockito.when(mock.doSomething()).thenReturn(10);
     *
     * //Returns 'mock.doSomething()' if 'given(mock)' is passed in
     * BDDMockito.willReturn(10).given(mock).doSomething();
     * </pre>
     *
     * @param methodCallStubber the call in a stubbing call chain that accepts the mock object,
     *                          or the method call on a mock object that is being stubbed
     * @return the found call as PsiMethodCallExpression, or empty Optional if the call is not found, or the stubbing
     * is found, but it is not a method call
     */
    public abstract Optional<PsiMethodCallExpression> getStubbedMethodCall(PsiMethodCallExpression methodCallStubber);

    /**
     * Finds and returns the stubbed method call from the argument stubbing call chain.
     * <p>
     * For example:
     * <pre>
     * //Returns 'mock.doSomething()' is both cases:
     *
     * Mockito.when(mock.doSomething()).thenReturn(10);
     * BDDMockito.willReturn(10).given(mock).doSomething();
     * </pre>
     *
     * @param stubbingCalls the calls in the call chain constituting as a stubbing
     * @return the found call as PsiMethodCallExpression, or empty Optional if the call is not found, or the stubbing
     * is found, but it is not a method call
     */
    public abstract Optional<PsiMethodCallExpression> getStubbedMethodCall(List<PsiMethodCallExpression> stubbingCalls);

    /**
     * Finds and returns the stubbed method call from a call chain based on the argument stub call.
     * <p>
     * Based on the position a stub call can take in a stubbing call chain, the stubbed method call can be
     * located either upwards or downwards on the call chain.
     *
     * @param stubCall any stub call: {@code then*()}, {@code do*()} or {@code will*()}
     * @return the found call as PsiExpression, or empty Optional if the call is not found
     */
    public abstract Optional<PsiExpression> getStubbedMethodCallAnywhere(PsiMethodCallExpression stubCall);

    /**
     * Returns whether the argument method call is the call in a stubbing call chain that accepts the mock object,
     * or the method call on a mock object that is being stubbed.
     * <p>
     * For example:
     * <pre>
     * Mockito.when(mock.doSomething()).thenReturn(10); //returns true if 'Mockito.when(mock.doSomething())' is passed in
     * BDDMockito.willReturn(10).given(mock).doSomething(); //return true if 'given(mock)' is passed in
     * </pre>
     *
     * @param expression the method accepting the mock object, or the call to the stubbed method
     */
    public abstract boolean isStubbedBy(PsiMethodCallExpression expression);

    /**
     * Returns whether the argument is a call to a method that constitutes as a stubbing method, e.g.
     * {@code do*()}, {@code then*()} or {@code will*()}
     *
     * @param expression the method call that may be a stubbing
     */
    public abstract boolean isAnyOfStubs(PsiMethodCallExpression expression);

    /**
     * Returns whether the argument stubbing call chain is valid and meets preconditions to validate it,
     * or extract information from it.
     *
     * @param stubbingCalls the calls in the call chain constituting as a stubbing
     */
    public boolean isValid(List<PsiMethodCallExpression> stubbingCalls) {
        return true; //no specific criteria is assigned by default to it, so considering valid by default
    }

    /**
     * Returns whether the argument method call stubber is valid and meets preconditions to validate it,
     * or extract information from it.
     * <p>
     * It is used in conjunction with {@link #isStubbedBy(PsiMethodCallExpression)}.
     *
     * @param stubbingCall the call in the call chain constituting as the method call stubber
     */
    public boolean isValid(PsiMethodCallExpression stubbingCall) {
        return true; //no specific criteria is assigned by default to it, so considering valid by default
    }

    /**
     * Return whether the argument of a method call stubber, specifically the call to a stubbed method from a
     * {@code Mockito.when(mock.doSomething())} type call, or the mock object from a {@code Mockito.do*().when(mock).doSomething()} call,
     * is valid.
     * <p>
     * In the first case, the argument has to be a call to the stubbed method, while in the latter one, a mock object must be passed in.
     *
     * @param mockObjectOrCallToStubbedMethod the mock object or a call to a stubbed method
     */
    protected abstract boolean isValidStubbingArgument(PsiExpression mockObjectOrCallToStubbedMethod);

    /**
     * Returns the object for describing a call to an exception stubber, a {@code *Throw()} method.
     */
    @NotNull
    public abstract ExceptionStubber getExceptionStubber();

    public static Stream<ExceptionStubber> findExceptionStubberApplicableTo(PsiMethodCallExpression call) {
        return EXCEPTION_STUBBERS.stream()
            .filter(descriptor -> descriptor.isApplicableTo(call));
    }

    /**
     * Returns whether this and the provided descriptors have the same stub type.
     */
    public boolean hasSameStubTypeAs(StubbingApproach other) {
        return stubType == other.stubType;
    }

    /**
     * Returns e.g. {@code Mockito.when}.
     */
    public String getBeginningOfStubbing(StubbingApproach from) {
        if (hasSameStubTypeAs(from) || from.stubType == StubType.STUBBING) return stubStarterClassName;
        return stubStarterClassName + "." + methodCallStubber;
    }
}
