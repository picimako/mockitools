//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;

import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.inspection.stubbing.ExceptionStubber;
import com.siyeh.ig.callMatcher.CallMatcher;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data class to be used during the analysis phase.
 */
class ConsecutiveCallAnalyzer {
    /**
     * The method name whose consecutiveness the inspection looks for.
     * <p>
     * E.g. {@code doReturn}, {@code thenReturn}, etc.
     */
    @NotNull
    final String consecutiveMethodName;
    /**
     * Whether to skip the analysis of the first call in a call chain, because in case of e.g. {@code Mockito.when()} it is certain that
     * {@code when()} will never match e.g. {@code thenReturn()}, so we can skip that comparison and start at the next call.
     */
    final boolean skipAnalysisOfFirstCall;
    /**
     * Used to identify {@code *Throw()} calls in case of {@link SimplifyConsecutiveThrowCallsInspection}.
     */
    @Nullable
    final ExceptionStubber exceptionStubber;
    /**
     * The call matcher built for {@link Analyzer#inCallChainsBeginningWithStatic}.
     */
    @NotNull
    private final CallMatcher chainStarterMethodMatcher;

    private ConsecutiveCallAnalyzer(Analyzer builder) {
        chainStarterMethodMatcher = builder.chainStarterMethodMatcher;
        consecutiveMethodName = builder.consecutiveMethodName;
        skipAnalysisOfFirstCall = builder.skipAnalysisOfFirstCall;
        exceptionStubber = builder.exceptionStubbingVia;
    }

    boolean canAnalyze(PsiMethodCallExpression expression) {
        return chainStarterMethodMatcher.matches(expression);
    }

    /**
     * Builder for {@code ConsecutiveCallAnalyzer}.
     */
    @RequiredArgsConstructor
    static final class Analyzer {
        /**
         * Used in the quick fix as the beginning of the expression that is built for replacement.
         * <p>
         * Either {@code org.mockito.Mockito}, {@code org.mockito.BDDMockito} or {@code org.mockito.MockedStatic}.
         */
        private final String mockitoClass;
        private CallMatcher chainStarterMethodMatcher;
        @Accessors(fluent = true)
        @Setter
        private String consecutiveMethodName;
        private boolean skipAnalysisOfFirstCall = false;
        /**
         * Applicable only in case of {@link SimplifyConsecutiveThrowCallsInspection}.
         */
        @Nullable
        private ExceptionStubber exceptionStubbingVia;

        /**
         * Configures the method names as static methods in {@link #mockitoClass}.
         *
         * @param chainStarterMethodNames The first call in a stubbing call chain from where the calls are collected.
         *                                E.g. {@code Mockito.when()}, {@code BDDMockito.willReturn()}, etc.
         */
        Analyzer inCallChainsBeginningWithStatic(String... chainStarterMethodNames) {
            this.chainStarterMethodMatcher = CallMatcher.staticCall(mockitoClass, chainStarterMethodNames);
            return this;
        }

        /**
         * Configures the method names as instance methods in {@link #mockitoClass}.
         *
         * @param chainStarterMethodNames The first call in a stubbing call chain from where the calls are collected.
         *                                E.g. {@code Mockito.when()}, {@code BDDMockito.willReturn()}, etc.
         */
        Analyzer inCallChainsBeginningWithInstance(String... chainStarterMethodNames) {
            this.chainStarterMethodMatcher = CallMatcher.instanceCall(mockitoClass, chainStarterMethodNames);
            return this;
        }

        /**
         * Configures this builder to do skip the analysis of the first call in a chain.
         */
        Analyzer skippingAnalysisOfFirstCall() {
            skipAnalysisOfFirstCall = true;
            return this;
        }

        /**
         * Configure the exception stubber from the provide stubbing approach
         */
        Analyzer exceptionStubbingVia(StubbingApproach stubbingApproach) {
            exceptionStubbingVia = stubbingApproach.getExceptionStubber();
            return this;
        }

        ConsecutiveCallAnalyzer build() {
            return new ConsecutiveCallAnalyzer(this);
        }

        //Static factory methods

        static Analyzer forMockito(String consecutiveMethodName) {
            return new Analyzer(ORG_MOCKITO_MOCKITO).consecutiveMethodName(consecutiveMethodName);
        }

        static Analyzer forBDDMockito(String consecutiveMethodName) {
            return new Analyzer(ORG_MOCKITO_BDDMOCKITO).consecutiveMethodName(consecutiveMethodName);
        }

        static Analyzer forMockedStatic(String consecutiveMethodName) {
            return new Analyzer(ORG_MOCKITO_MOCKED_STATIC).consecutiveMethodName(consecutiveMethodName);
        }
    }
}
