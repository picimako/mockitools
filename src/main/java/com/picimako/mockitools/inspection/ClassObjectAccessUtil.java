/*
 * Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.picimako.mockitools.inspection;

import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for working with {@link com.intellij.psi.PsiClassObjectAccessExpression}s.
 */
public final class ClassObjectAccessUtil {

    /**
     * Returns the operand type of the argument element if it is a {@link com.intellij.psi.PsiClassObjectAccessExpression},
     * null otherwise.
     */
    @Nullable
    public static PsiType getOperandType(@Nullable PsiElement element) {
        return element instanceof PsiClassObjectAccessExpression
            ? ((PsiClassObjectAccessExpression) element).getOperand().getType()
            : null;
    }

    private ClassObjectAccessUtil() {
        //Utility class
    }
}
