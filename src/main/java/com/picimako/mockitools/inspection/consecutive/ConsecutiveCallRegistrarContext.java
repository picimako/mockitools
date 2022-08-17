//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.google.common.collect.Iterables.getLast;

import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.inspection.ExceptionStubber;
import org.jetbrains.annotations.NotNull;

/**
 * Data class to be used during quick fix registration.
 */
class ConsecutiveCallRegistrarContext {
    /**
     * @see ConsecutiveCallAnalysisDescriptor#consecutiveMethodName
     */
    final String consecutiveMethodName;
    /**
     * The full list of calls in the currently analyzed call chain.
     */
    final List<PsiMethodCallExpression> callsInWholeChain;
    /**
     * The indeces of the target section of consecutive calls within {@link #callsInWholeChain}.
     */
    final List<Integer> consecutiveCallIndeces;
    /**
     * @see ConsecutiveCallAnalysisDescriptor#exceptionStubber
     */
    private final ExceptionStubber exceptionStubber;

    ConsecutiveCallRegistrarContext(@NotNull ConsecutiveCallAnalysisDescriptor analyzer,
                                    @NotNull List<PsiMethodCallExpression> callsInWholeChain,
                                    @NotNull List<Integer> consecutiveCallIndeces) {
        consecutiveMethodName = analyzer.consecutiveMethodName;
        exceptionStubber = analyzer.exceptionStubber;
        this.callsInWholeChain = callsInWholeChain;
        this.consecutiveCallIndeces = List.copyOf(consecutiveCallIndeces);
    }

    /**
     * Returns the last consecutive call from the current section of calls.
     */
    PsiMethodCallExpression getLastConsecutiveCall() {
        return callsInWholeChain.get(getLast(consecutiveCallIndeces));
    }

    boolean isCallToClasses(PsiMethodCallExpression call) {
        return exceptionStubber != null && exceptionStubber.classMatcher.matches(call);
    }

    boolean isCallToThrowables(PsiMethodCallExpression call) {
        return exceptionStubber != null && exceptionStubber.throwablesMatcher.matches(call);
    }
}
