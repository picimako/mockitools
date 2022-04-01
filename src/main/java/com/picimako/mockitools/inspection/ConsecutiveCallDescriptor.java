//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Arrays;
import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConsecutiveCallDescriptor {
    /**
     * Used in the quick fix as the beginning of the expression that is built for replacement.
     * <p>
     * Usually either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
     */
    @NotNull
    public final String mockitoClass;
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
    /**
     * The method name whose consecutiveness the inspection looks for.
     * <p>
     * E.g. {@code doReturn}, {@code thenReturn}, etc.
     */
    @NotNull
    public final String consecutiveMethodName;
    /**
     * The index to start inspecting the call chain from, because in case of e.g. {@code Mockito.when()} it is certain that
     * {@code when()} will never match e.g. {@code thenReturn()}, so we can skip that comparison and start at the next call.
     */
    public final int indexToStartInspectionAt;

    @Nullable
    private final ThrowStubDescriptor throwDescriptor;

    private ConsecutiveCallDescriptor(Builder builder) {
        mockitoClass = builder.mockitoClass;
        chainStarterMethodNames = builder.chainStarterMethodNames;
        chainStarterMethodMatcher = builder.chainStarterMethodMatcher;
        consecutiveMethodName = builder.consecutiveMethodName;
        indexToStartInspectionAt = builder.indexToStartInspectionAt;
        throwDescriptor = builder.throwDescriptor;
    }

    public boolean matches(PsiMethodCallExpression expression) {
        return chainStarterMethodNames.contains(getMethodName(expression)) && chainStarterMethodMatcher.matches(expression);
    }

    public boolean isCallToClasses(PsiMethodCallExpression call) {
        return throwDescriptor != null && throwDescriptor.classMatcher.matches(call);
    }

    public boolean isCallToThrowables(PsiMethodCallExpression call) {
        return throwDescriptor != null && throwDescriptor.throwablesMatcher.matches(call);
    }

    public static final class Builder {
        private final String mockitoClass;
        private List<String> chainStarterMethodNames;
        private CallMatcher chainStarterMethodMatcher;
        private String consecutiveMethodName;
        private int indexToStartInspectionAt = 0;
        @Nullable
        private ThrowStubDescriptor throwDescriptor;

        public Builder(String mockitoClass) {
            this.mockitoClass = mockitoClass;
        }

        public Builder chainStarterMethodNames(String... chainStarterMethodNames) {
            this.chainStarterMethodNames = Arrays.asList(chainStarterMethodNames);
            this.chainStarterMethodMatcher = CallMatcher.staticCall(mockitoClass, chainStarterMethodNames);
            return this;
        }

        public Builder consecutiveMethodName(String consecutiveMethodName) {
            this.consecutiveMethodName = consecutiveMethodName;
            return this;
        }

        public Builder indexToStartInspectionAt(int index) {
            indexToStartInspectionAt = index;
            return this;
        }

        public Builder throwDescriptor(ThrowStubDescriptor throwDescriptor) {
            this.throwDescriptor = throwDescriptor;
            return this;
        }

        public ConsecutiveCallDescriptor build() {
            return new ConsecutiveCallDescriptor(this);
        }
    }
}
