//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;

import com.intellij.psi.PsiMethodCallExpression;
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
class ConsecutiveCallAnalysisDescriptor {
    /**
     * The method name whose consecutiveness the inspection looks for.
     * <p>
     * E.g. {@code doReturn}, {@code thenReturn}, etc.
     */
    @NotNull
    final String consecutiveMethodName;
    /**
     * The index to start inspecting the call chain from, because in case of e.g. {@code Mockito.when()} it is certain that
     * {@code when()} will never match e.g. {@code thenReturn()}, so we can skip that comparison and start at the next call.
     */
    final int indexToStartInspectionAt;
    /**
     * Used to identify {@code *Throw()} calls in case of {@link SimplifyConsecutiveThrowCallsInspection}.
     */
    @Nullable
    final ExceptionStubber exceptionStubber;
    /**
     * The call matcher built for {@link Builder#inCallChainsBeginningWith}.
     */
    @NotNull
    private final CallMatcher chainStarterMethodMatcher;

    private ConsecutiveCallAnalysisDescriptor(Builder builder) {
        chainStarterMethodMatcher = builder.chainStarterMethodMatcher;
        consecutiveMethodName = builder.consecutiveMethodName;
        indexToStartInspectionAt = builder.indexToStartInspectionAt;
        exceptionStubber = builder.exceptionStubbingVia;
    }

    boolean matches(PsiMethodCallExpression expression) {
        return chainStarterMethodMatcher.matches(expression);
    }

    @RequiredArgsConstructor
    static final class Builder {
        /**
         * Used in the quick fix as the beginning of the expression that is built for replacement.
         * <p>
         * Usually either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
         */
        private final String mockitoClass;
        private CallMatcher chainStarterMethodMatcher;
        @Accessors(fluent = true)
        @Setter
        private String consecutiveMethodName;
        @Accessors(fluent = true)
        @Setter
        private int indexToStartInspectionAt = 0;
        /**
         * Applicable only in case of {@link SimplifyConsecutiveThrowCallsInspection}.
         */
        @Nullable
        @Accessors(fluent = true)
        @Setter
        private ExceptionStubber exceptionStubbingVia;

        /**
         * Configures the method names as static methods in {@link #mockitoClass}.
         *
         * @param chainStarterMethodNames The first call in a stubbing call chain from where the calls are collected.
         *                                E.g. {@code Mockito.when()}, {@code BDDMockito.willReturn()}, etc.
         */
        Builder inCallChainsBeginningWith(String... chainStarterMethodNames) {
            return inCallChainsBeginningWith(MethodType.STATIC, chainStarterMethodNames);
        }

        /**
         * Configures the method names as instance or static methods (based on the method type) in {@link #mockitoClass}.
         *
         * @param chainStarterMethodNames The first call in a stubbing call chain from where the calls are collected.
         *                                E.g. {@code Mockito.when()}, {@code BDDMockito.willReturn()}, etc.
         */
        Builder inCallChainsBeginningWith(MethodType methodtype, String... chainStarterMethodNames) {
            this.chainStarterMethodMatcher = methodtype == MethodType.STATIC
                ? CallMatcher.staticCall(mockitoClass, chainStarterMethodNames)
                : CallMatcher.instanceCall(mockitoClass, chainStarterMethodNames);
            return this;
        }

        ConsecutiveCallAnalysisDescriptor build() {
            return new ConsecutiveCallAnalysisDescriptor(this);
        }

        static Builder forMockito(String consecutiveMethodName) {
            return new Builder(ORG_MOCKITO_MOCKITO).consecutiveMethodName(consecutiveMethodName);
        }

        static Builder forBDDMockito(String consecutiveMethodName) {
            return new Builder(ORG_MOCKITO_BDDMOCKITO).consecutiveMethodName(consecutiveMethodName);
        }

        static Builder forMockedStatic(String consecutiveMethodName) {
            return new Builder(ORG_MOCKITO_MOCKED_STATIC).consecutiveMethodName(consecutiveMethodName);
        }
    }

    enum MethodType {
        STATIC, INSTANCE
    }
}
