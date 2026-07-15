//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;

import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.dsl.StubbingApproach;
import com.picimako.mockitools.inspection.stubbing.ExceptionStubber;
import com.siyeh.ig.callMatcher.CallMatcher;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data class to be used during the analysis phase.
 */
class ConsecutiveCallAnalyzer {
    /**
     * The method name whose consecutiveness the inspection looks for. E.g. {@code doReturn}, {@code thenReturn}, etc.
     */
    @NotNull
    final String consecutiveMethodName;
    /**
     * Whether to skip the analysis of the first call in a call chain. In case of e.g. {@code Mockito.when()} it is certain that
     * {@code when()} will never match e.g. {@code thenReturn()}, so we can skip that comparison and start at the next call.
     */
    final boolean skipAnalysisOfFirstCall;
    /**
     * Used to identify {@code *Throw()} calls in case of {@link SimplifyConsecutiveThrowCallsInspection}.
     */
    @Nullable
    final ExceptionStubber exceptionStubber;
    /**
     * The call matcher built with {@link Analyzer#inCallChainsBeginningWithStatic} and {@link Analyzer#inCallChainsBeginningWithInstance}.
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
     * Builder for {@link ConsecutiveCallAnalyzer}.
     */
    static final class Analyzer {
        /**
         * Used in the quick fix as the beginning of the expression that is built for replacement.
         */
        private final String mockitoClass;
        @NotNull
        @Accessors(fluent = true)
        @Setter
        private String consecutiveMethodName;
        private boolean skipAnalysisOfFirstCall = false;
        @Nullable
        private ExceptionStubber exceptionStubbingVia;
        private CallMatcher chainStarterMethodMatcher;

        Analyzer(@MagicConstant(stringValues = {ORG_MOCKITO_MOCKITO, ORG_MOCKITO_BDDMOCKITO, ORG_MOCKITO_MOCKED_STATIC}) String mockitoClass) {
            this.mockitoClass = mockitoClass;
        }

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
         * Configures this builder to skip the analysis of the first call in a chain.
         */
        Analyzer skippingAnalysisOfFirstCall() {
            skipAnalysisOfFirstCall = true;
            return this;
        }

        /**
         * Configure the exception stubber from the provided stubbing approach.
         */
        Analyzer exceptionStubbingVia(StubbingApproach stubbingApproach) {
            exceptionStubbingVia = stubbingApproach.getExceptionStubber();
            return this;
        }

        ConsecutiveCallAnalyzer build() {
            return new ConsecutiveCallAnalyzer(this);
        }

        //Static factory methods

        static Analyzer forMockito(@NotNull String consecutiveMethodName) {
            return new Analyzer(ORG_MOCKITO_MOCKITO).consecutiveMethodName(consecutiveMethodName);
        }

        static Analyzer forBDDMockito(@NotNull String consecutiveMethodName) {
            return new Analyzer(ORG_MOCKITO_BDDMOCKITO).consecutiveMethodName(consecutiveMethodName);
        }

        static Analyzer forMockedStatic(@NotNull String consecutiveMethodName) {
            return new Analyzer(ORG_MOCKITO_MOCKED_STATIC).consecutiveMethodName(consecutiveMethodName);
        }
    }
}
