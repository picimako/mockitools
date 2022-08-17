//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static com.picimako.mockitools.util.PointersUtil.toPointers;

import java.util.List;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.SmartPsiElementPointer;

/**
 * Data class to be used during quick fix application.
 */
class ConsecutiveCallQuickFixContext {
    /**
     * @see ConsecutiveCallAnalysisDescriptor#consecutiveMethodName
     */
    final String consecutiveMethodName;
    /**
     * The indeces of the target section of consecutive calls within {@code callsInWholeChain}. 
     */
    final List<Integer> consecutiveCallIndeces;
    /**
     * The list of {@code callsInWholeChain} as {@link SmartPsiElementPointer}s used within the quick fix.
     */
    private final List<SmartPsiElementPointer<PsiMethodCallExpression>> wholeChainPointers;

    public ConsecutiveCallQuickFixContext(ConsecutiveCallRegistrarContext registrar) {
        this.consecutiveMethodName = registrar.consecutiveMethodName;
        wholeChainPointers = toPointers(registrar.callsInWholeChain);
        this.consecutiveCallIndeces = registrar.consecutiveCallIndeces;
    }

    PsiMethodCallExpression getElement(int index) {
        return wholeChainPointers.get(index).getElement();
    }

    PsiMethodCallExpression getFirstConsecutiveCall() {
        return wholeChainPointers.get(consecutiveCallIndeces.get(0)).getElement();
    }

    VirtualFile getContainingFile() {
        return wholeChainPointers.get(0).getVirtualFile();
    }
}
