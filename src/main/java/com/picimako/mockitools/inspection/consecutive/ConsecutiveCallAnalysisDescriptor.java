//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Arrays;
import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.mockitools.inspection.ThrowStubDescriptor;

/**
 * Data class to be used during the analysis phase.
 */
class ConsecutiveCallAnalysisDescriptor {
    /**
     * Used in the quick fix as the beginning of the expression that is built for replacement.
     * <p>
     * Usually either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
     */
    @NotNull
    final String mockitoClass;
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
    final ThrowStubDescriptor throwDescriptor;
    /**
     * The first call in a stubbing call chain from where the calls are collected.
     * <p>
     * E.g. {@code Mockito.when()}, {@code BDDMockito.willReturn()}, etc.
     */
    @NotNull
    private final List<String> chainStarterMethodNames;
    /**
     * The call matcher built for {@link #chainStarterMethodNames}.
     */
    @NotNull
    private final CallMatcher chainStarterMethodMatcher;

    private ConsecutiveCallAnalysisDescriptor(Builder builder) {
        mockitoClass = builder.mockitoClass;
        chainStarterMethodNames = builder.chainStarterMethodNames;
        chainStarterMethodMatcher = builder.chainStarterMethodMatcher;
        consecutiveMethodName = builder.consecutiveMethodName;
        indexToStartInspectionAt = builder.indexToStartInspectionAt;
        throwDescriptor = builder.throwDescriptor;
    }

    boolean matches(PsiMethodCallExpression expression) {
        return chainStarterMethodNames.contains(getMethodName(expression)) && chainStarterMethodMatcher.matches(expression);
    }

    static final class Builder {
        private final String mockitoClass;
        private List<String> chainStarterMethodNames;
        private CallMatcher chainStarterMethodMatcher;
        private String consecutiveMethodName;
        private int indexToStartInspectionAt = 0;
        @Nullable
        private ThrowStubDescriptor throwDescriptor;

        Builder(String mockitoClass) {
            this.mockitoClass = mockitoClass;
        }

        Builder chainStarterMethodNames(String... chainStarterMethodNames) {
            this.chainStarterMethodNames = Arrays.asList(chainStarterMethodNames);
            this.chainStarterMethodMatcher = CallMatcher.staticCall(mockitoClass, chainStarterMethodNames);
            return this;
        }

        Builder consecutiveMethodName(String consecutiveMethodName) {
            this.consecutiveMethodName = consecutiveMethodName;
            return this;
        }

        Builder indexToStartInspectionAt(int index) {
            indexToStartInspectionAt = index;
            return this;
        }

        Builder throwDescriptor(ThrowStubDescriptor throwDescriptor) {
            this.throwDescriptor = throwDescriptor;
            return this;
        }

        ConsecutiveCallAnalysisDescriptor build() {
            return new ConsecutiveCallAnalysisDescriptor(this);
        }
    }
}
