//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Optional;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for handling PsiMethods.
 */
public final class PsiMethodUtil {

    public static boolean isMethodCall(PsiElement call) {
        return call instanceof PsiMethodCallExpression;
    }

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
     * Returns the next method call in the chain the argument method call is located.
     * <p>
     * In the chain {@code variable.doFirst().doSecond()}, for {@code doFirst()} the subsequent call is {@code doSecond()}.
     *
     * @param methodCall the call to get the next call of.
     * @return the next method call, or null if the starting call is null, or if it has no subsequent call
     * @since 0.3.0
     */
    @Nullable
    public static PsiMethodCallExpression getSubsequentMethodCall(@Nullable PsiMethodCallExpression methodCall) {
        if (methodCall == null) return null;
        PsiElement parent = methodCall.getParent();
        if (parent instanceof PsiReferenceExpression) {
            PsiElement grandParent = parent.getParent();
            if (grandParent instanceof PsiMethodCallExpression) return (PsiMethodCallExpression) grandParent;
        }
        return null;
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

    /**
     * Finds the method call in a chain named by the argument {@code methodNameToFind}, upwards from {@code aCallInChain}.
     * <p>
     * For a chain {@code variable.doFirst().doSecond().doThird().doFourth()}, if the method is called with {@code PsiExpression[doThird()], "doFirst"}
     * arguments, it will find the first call, but would not find e.g. {@code doFourth()}.
     *
     * @param aCallInChain     the starting point in the chain
     * @param methodNameToFind the method name to find
     * @return the found method call, or empty optional if none found
     * @since 0.3.0
     */
    public static Optional<PsiMethodCallExpression> findCallUpwardsInChain(@NotNull PsiExpression aCallInChain, String methodNameToFind) {
        PsiElement current = aCallInChain;
        while (current.getFirstChild() instanceof PsiReferenceExpression) {
            PsiElement previousCall = current.getFirstChild().getFirstChild();
            if (isMethodCall(previousCall)) {
                var prevCall = (PsiMethodCallExpression) previousCall;
                if (methodNameToFind.equals(getMethodName(prevCall))) {
                    return Optional.of(prevCall);
                }
                current = previousCall;
            } else break;
        }
        return Optional.empty();
    }

    /**
     * Finds the method call in a chain named by the argument {@code methodNameToFind}, downwards from {@code aCallInChain}.
     * <p>
     * For a chain {@code variable.doFirst().doSecond().doThird().doFourth()}, if the method is called with {@code PsiExpression[doSecond()], "doFourth"}
     * arguments, it will find the last call, but would not find e.g. {@code doSecond()}.
     *
     * @param aCallInChain     the starting point in the chain
     * @param methodNameToFind the method name to find
     * @return the found method call, or empty optional if none found
     * @since 0.3.0
     */
    public static Optional<PsiMethodCallExpression> findCallDownwardsInChain(@NotNull PsiExpression aCallInChain, String methodNameToFind) {
        var callsInChain = PsiTreeUtil.collectParents(aCallInChain,
            PsiMethodCallExpression.class, false, e -> e instanceof PsiExpressionList || e instanceof PsiStatement);
        return callsInChain.stream()
            .filter(call -> methodNameToFind.equals(getMethodName(call)))
            .findFirst();
    }

    private PsiMethodUtil() {
        //Utility class
    }
}
