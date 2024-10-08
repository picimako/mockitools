//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.util.PointersUtil.toPointers;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.SmartPsiElementPointer;
import com.picimako.mockitools.inspection.stubbing.ExceptionStubber;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Data class to be used during quick fix registration.
 */
class ConsecutiveCallRegistrar {
    /**
     * @see ConsecutiveCallAnalyzer#consecutiveMethodName
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
     * @see ConsecutiveCallAnalyzer#exceptionStubber
     */
    private final ExceptionStubber exceptionStubber;
    /**
     * The list of {@code callsInWholeChain} as {@link SmartPsiElementPointer}s used within the respective quick fix.
     * <p>
     * This is lazy-initialized, and instantiated only when {@code MergeConsecutiveStubbingCallsQuickFix} is actually called.
     */
    private List<SmartPsiElementPointer<PsiMethodCallExpression>> wholeChainPointers = null;

    ConsecutiveCallRegistrar(@NotNull ConsecutiveCallAnalyzer analyzer,
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

    //For quick fixes

    PsiMethodCallExpression getElement(int index) {
        initializePointersIfNotAlready();
        return wholeChainPointers.get(index).getElement();
    }

    PsiMethodCallExpression getFirstConsecutiveCall() {
        initializePointersIfNotAlready();
        return wholeChainPointers.get(consecutiveCallIndeces.getFirst()).getElement();
    }

    VirtualFile getContainingFile() {
        initializePointersIfNotAlready();
        return wholeChainPointers.getFirst().getVirtualFile();
    }

    private void initializePointersIfNotAlready() {
        if (wholeChainPointers == null) wholeChainPointers = toPointers(callsInWholeChain);
    }
}
