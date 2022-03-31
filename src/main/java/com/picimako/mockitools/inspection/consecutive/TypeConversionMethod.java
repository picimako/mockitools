//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiNewExpression;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Defines the conversion from {@link PsiClassObjectAccessExpression}s to {@link PsiNewExpression}s and vice versa.
 * <p>
 * It is used when users have multiple choices how to merge the *Throw() calls together.
 */
public enum TypeConversionMethod {
    TO_CLASSES(MockitoolsBundle.quickFix("to.class.objects")) {
        @Override
        String convert(PsiExpression expression) {
            return expression instanceof PsiNewExpression ? expression.getType().getPresentableText() + ".class" : super.convert(expression);
        }
    },
    TO_THROWABLES(MockitoolsBundle.quickFix("to.throwables")) {
        @Override
        String convert(PsiExpression expression) {
            return expression instanceof PsiClassObjectAccessExpression
                ? "new " + ((PsiClassObjectAccessExpression) expression).getOperand().getType().getPresentableText() + "()"
                : super.convert(expression);
        }
    },
    TO_THROWABLES_SIMPLE("") {
        @Override
        String convert(PsiExpression expression) {
            return TO_THROWABLES.convert(expression);
        }
    },
    NO_CONVERSION("");

    public final String message;

    TypeConversionMethod(String message) {
        this.message = message;
    }

    String convert(PsiExpression expression) {
        return expression.getText();
    }
}
