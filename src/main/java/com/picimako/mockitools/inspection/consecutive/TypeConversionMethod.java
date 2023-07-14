//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiNewExpression;

import com.picimako.mockitools.resources.MockitoolsBundle;
import lombok.RequiredArgsConstructor;

/**
 * Defines the conversion from {@link PsiClassObjectAccessExpression}s to {@link PsiNewExpression}s and vice versa.
 * <p>
 * It is used when users have multiple choices how to merge the *Throw() calls together.
 */
@RequiredArgsConstructor
public enum TypeConversionMethod {
    TO_CLASSES(MockitoolsBundle.message("quick.fix.to.class.objects")) {
        @Override
        public PsiElement convert(PsiExpression expression) {
            return expression instanceof PsiNewExpression
                ? createExpression(expression, expression.getType().getPresentableText() + ".class")
                : super.convert(expression);
        }
    },
    TO_THROWABLES(MockitoolsBundle.message("quick.fix.to.throwables")) {
        @Override
        public PsiElement convert(PsiExpression expression) {
            return expression instanceof PsiClassObjectAccessExpression
                ? createExpression(expression, "new " + ((PsiClassObjectAccessExpression) expression).getOperand().getType().getPresentableText() + "()")
                : super.convert(expression);
        }
    },
    TO_THROWABLES_SIMPLE("") {
        @Override
        public PsiElement convert(PsiExpression expression) {
            return TO_THROWABLES.convert(expression);
        }
    },
    NO_CONVERSION("");

    public final String message;

    public PsiElement convert(PsiExpression expression) {
        return expression;
    }

    private static PsiElement createExpression(PsiExpression expression, String text) {
        return JavaPsiFacade.getElementFactory(expression.getProject()).createExpressionFromText(text, expression.getParent());
    }
}
