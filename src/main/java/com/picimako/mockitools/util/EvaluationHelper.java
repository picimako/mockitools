/*
 * Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.picimako.mockitools.util;

import static com.picimako.mockitools.util.ClassObjectAccessUtil.getOperandType;

import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.Nullable;

/**
 * Utility to work with PSI types.
 */
public final class EvaluationHelper {

    /**
     * Returns the type of the element.
     * <p>
     * This method can be used for cases when we are certain that an element or expression can only be
     * either a {@link PsiClassObjectAccessExpression} or a {@link PsiNewExpression}.
     * <p>
     * Returns null, if the expression is neither type.
     *
     * @param element the element to get the type of
     */
    @Nullable
    public static PsiType evaluateClassObjectOrNewExpressionType(PsiElement element) {
        PsiType elementType = null;
        if (element instanceof PsiClassObjectAccessExpression) {
            elementType = getOperandType(element);
        } else if (element instanceof PsiNewExpression) {
            elementType = ((PsiExpression) element).getType();
        }
        return elementType;
    }

    /**
     * Returns the type of the expression.
     * <p>
     * This method can be used for cases when the expression can be a {@link PsiClassObjectAccessExpression}
     *
     * @param expression the element to get the type of
     */
    @Nullable
    public static PsiType evaluateType(PsiExpression expression) {
        return expression instanceof PsiClassObjectAccessExpression ? ClassObjectAccessUtil.getOperandType(expression) : expression.getType();
    }

    private EvaluationHelper() {
        //Utility class
    }
}
