//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
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
     * Gets whether the argument method call has at least one argument of any kind.
     *
     * @param methodCall the method call to check the argument count of
     * @return true if the method call has at least one argument, false otherwise
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
     * Mockito.verify(mock, times(1).description("message"))... //times() has description() as the subquent call
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

    private PsiMethodUtil() {
        //Utility class
    }
}
