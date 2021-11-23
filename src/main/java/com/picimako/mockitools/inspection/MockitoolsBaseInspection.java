//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for inspections that has to distinguish validation between files in test sources and actual unit test classes.
 */
public abstract class MockitoolsBaseInspection extends LocalInspectionTool {

    @NotNull
    protected JavaElementVisitor methodCallVisitor(@NotNull ProblemsHolder holder) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                checkMethodCallExpression(expression, holder);
            }

            @Override
            public void visitCallExpression(PsiCallExpression callExpression) {
                //it is only method call expressions that we need to take into account
            }

            @Override
            public void visitNewExpression(PsiNewExpression expression) {
                //it is only method call expressions that we need to take into account
            }
        };
    }

    @NotNull
    protected JavaElementVisitor fieldVisitor(@NotNull ProblemsHolder holder) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                checkField(field, holder);
            }
        };
    }

    @NotNull
    protected JavaElementVisitor fieldAndMethodCallVisitor(@NotNull ProblemsHolder holder) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                checkField(field, holder);
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                checkMethodCallExpression(expression, holder);
            }

            @Override
            public void visitCallExpression(PsiCallExpression callExpression) {
                //it is only method call expressions that we need to take into account
            }

            @Override
            public void visitNewExpression(PsiNewExpression expression) {
                //it is only method call expressions that we need to take into account
            }
        };
    }

    @NotNull
    protected JavaElementVisitor annotationAndMethodCallVisitor(@NotNull ProblemsHolder holder) {
        return new JavaElementVisitor() {
            @Override
            public void visitAnnotation(PsiAnnotation annotation) {
                checkAnnotation(annotation, holder);
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                checkMethodCallExpression(expression, holder);
            }

            @Override
            public void visitCallExpression(PsiCallExpression callExpression) {
                //it is only method call expressions that we need to take into account
            }

            @Override
            public void visitNewExpression(PsiNewExpression expression) {
                //it is only method call expressions that we need to take into account
            }
        };
    }

    /**
     * No-op by default since not all implementations of this class needs it.
     */
    protected void checkAnnotation(PsiAnnotation annotation, @NotNull ProblemsHolder holder) {
    }

    /**
     * No-op by default since not all implementations of this class needs it.
     */
    protected void checkField(PsiField field, @NotNull ProblemsHolder holder) {
    }

    /**
     * No-op by default since not all implementations of this class needs it.
     */
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
    }

}
