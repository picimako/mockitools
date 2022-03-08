//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for {@link SmartPsiElementPointer}s.
 */
public final class PointersUtil {

    public static List<SmartPsiElementPointer<PsiMethodCallExpression>> toPointers(List<PsiMethodCallExpression> calls) {
        return calls.stream().map(PointersUtil::toPointer).collect(toList());
    }

    @NotNull
    public static SmartPsiElementPointer<PsiMethodCallExpression> toPointer(PsiMethodCallExpression element) {
        return SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element, element.getContainingFile());
    }

    private PointersUtil() {
        //Utility class
    }
}
