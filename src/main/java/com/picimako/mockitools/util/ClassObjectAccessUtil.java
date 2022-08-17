/*
 * Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.picimako.mockitools.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
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

    /**
     * Alternate version of {@link #getOperandType(PsiElement)} for PsiExpressions.
     */
    @Nullable
    public static PsiType getOperandType(@Nullable PsiExpression expression) {
        return getOperandType((PsiElement) expression);
    }

    /**
     * Resolves the operand type of the provided element as a PsiClass.
     *
     * @return the resolved type as PsiClass or null if resolution could not happen, or type is not PsiClassType
     */
    @Nullable
    public static PsiClass resolveOperandType(@NotNull PsiElement element) {
        PsiType operandType = getOperandType(element);
        if (operandType instanceof PsiClassType) {
            return ((PsiClassType) operandType).resolve();
        }
        return null;
    }

    private ClassObjectAccessUtil() {
        //Utility class
    }
}
