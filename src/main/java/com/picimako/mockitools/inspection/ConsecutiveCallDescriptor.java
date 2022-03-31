 //Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Arrays;
import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;

public class ConsecutiveCallDescriptor {
    /**
     * Used in the quick fix as the beginning of the expression that is built for replacement.
     * <p>
     * Usually either {@code org.mockito.Mockito} or {@code org.mockito.BDDMockito}.
     */
    public final String mockitoClass;
    /**
     * The first call in a stubbing call chain from where the calls are collected.
     * <p>
     * E.g. {@code Mockito.when()}, {@code BDDMockito.willReturn()}, etc.
     */
    private final List<String> chainStarterMethodNames;
    /**
     * The call matcher built for {@link #chainStarterMethodNames}.
     */
    private final CallMatcher chainStarterMethodMatcher;
    /**
     * The method name whose consecutiveness the inspection looks for.
     * <p>
     * E.g. {@code doReturn}, {@code thenReturn}, etc.
     */
    public final String consecutiveMethodName;
    /**
     * The index to start inspecting the call chain from, because in case of e.g. {@code Mockito.when()} it is certain that
     * {@code when()} will never match e.g. {@code thenReturn()}, so we can skip that comparison and start at the next call.
     */
    public final int indexToStartInspectionAt;

    public ConsecutiveCallDescriptor(String mockitoClass, String consecutiveMethodName, int indexToStartInspectionAt, String... chainStarterMethodNames) {
        this.mockitoClass = mockitoClass;
        this.chainStarterMethodNames = Arrays.asList(chainStarterMethodNames);
        this.consecutiveMethodName = consecutiveMethodName;
        this.indexToStartInspectionAt = indexToStartInspectionAt;
        this.chainStarterMethodMatcher = CallMatcher.staticCall(mockitoClass, chainStarterMethodNames);
    }

    public boolean matches(PsiMethodCallExpression expression) {
        return chainStarterMethodNames.contains(getMethodName(expression)) && chainStarterMethodMatcher.matches(expression);
    }
}
