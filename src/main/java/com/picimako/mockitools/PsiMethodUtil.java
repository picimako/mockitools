//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for handling PsiMethods.
 */
public final class PsiMethodUtil {

    /**
     * Returns whether the argument method call has only one argument.
     */
    public static boolean hasOneArgument(@NotNull PsiMethodCallExpression methodCall) {
        return methodCall.getArgumentList() != null && methodCall.getArgumentList().getExpressionCount() == 1;
    }

    /**
     * Gets whether the argument method call has at least one argument of any kind.
     */
    public static boolean hasAtLeastOneArgument(@NotNull PsiMethodCallExpression methodCall) {
        return methodCall.getArgumentList() != null && methodCall.getArgumentList().getExpressionCount() >= 1;
    }

    /**
     * Gets whether the argument method call has a subsequent method call chained.
     * <p>
     * For example:
     * <pre>
     * Mockito.verify(mock, times(1))... //times() doesn't have a chained call
     * Mockito.verify(mock, times(1).description("message"))... //times() has description() as the subsequent call
     * </pre>
     *
     * @param methodCall the method call to check
     * @return true if there is a subsequent method call, false otherwise
     */
    public static boolean hasSubsequentMethodCall(@NotNull PsiMethodCallExpression methodCall) {
        return methodCall.getParent() instanceof PsiReferenceExpression && methodCall.getParent().getParent() instanceof PsiMethodCallExpression;
    }

    /**
     * Gets the arguments of the provided method call, given that the argument list exists and is not null.
     */
    public static PsiExpression[] getArguments(@NotNull PsiMethodCallExpression methodCall) {
        return methodCall.getArgumentList().getExpressions();
    }

    /**
     * Gets the first argument of the provided method call, given that the argument list exists and is not null.
     */
    public static PsiExpression getFirstArgument(@NotNull PsiMethodCallExpression methodCall) {
        return getArguments(methodCall)[0];
    }

    /**
     * Returns the qualifier expression of the argument method call.
     */
    @Nullable
    public static PsiExpression getQualifier(@NotNull PsiMethodCallExpression methodCall) {
        return methodCall.getMethodExpression().getQualifierExpression();
    }

    /**
     * Deletes the arguments of the provided method call.
     */
    public static void deleteArguments(@Nullable PsiMethodCallExpression methodCall) {
        if (methodCall != null) {
            for (var param : methodCall.getArgumentList().getExpressions()) {
                param.delete();
            }
        }
    }

    /**
     * Returns the parent PsiMethodCallExpression of the argument element, if there's one.
     */
    @Nullable
    public static PsiMethodCallExpression getParentCall(@Nullable PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
    }

    /**
     * Return whether the argument PSI element is identifier that belongs to a PsiMethodCallExpression.
     */
    public static boolean isIdentifierOfMethodCall(PsiElement element) {
        return element instanceof PsiIdentifier
            && element.getParent() instanceof PsiReferenceExpression
            && element.getParent().getParent() instanceof PsiMethodCallExpression;
    }

    private PsiMethodUtil() {
        //Utility class
    }
}
