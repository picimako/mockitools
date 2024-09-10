//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Utility for handling PsiMethods.
 */
public final class PsiMethodUtil {

    /**
     * Returns whether the argument method call has any argument.
     */
    public static boolean hasArgument(@NotNull PsiMethodCallExpression methodCall) {
        return compute(() -> methodCall.getArgumentList() != null && methodCall.getArgumentList().getExpressionCount() > 0);
    }

    /**
     * Returns whether the argument method call has only one argument.
     */
    public static boolean hasOneArgument(@NotNull PsiMethodCallExpression methodCall) {
        return compute(() -> methodCall.getArgumentList() != null && methodCall.getArgumentList().getExpressionCount() == 1);
    }

    /**
     * Returns whether the argument method call has 2 arguments.
     */
    public static boolean hasTwoArguments(@NotNull PsiMethodCallExpression methodCall) {
        return compute(() -> methodCall.getArgumentList() != null && methodCall.getArgumentList().getExpressionCount() == 2);
    }

    /**
     * Use this instead of {@link #getMethodCallAtCaretOrEmpty(PsiFile, Editor)}, when you are sure
     * (you've probably made sure beforehand), that the caret is at a method call identifier.
     */
    public static PsiMethodCallExpression getMethodCallAtCaret(PsiFile file, Editor editor) {
        return compute(() -> getMethodCallForIdentifier(file.findElementAt(editor.getCaretModel().getOffset())));
    }

    public static Optional<PsiMethodCallExpression> getMethodCallAtCaretOrEmpty(PsiFile file, Editor editor) {
        var elementAtCaret = file.findElementAt(compute(() -> editor.getCaretModel().getOffset()));
        return isIdentifierOfMethodCall(elementAtCaret)
               ? Optional.ofNullable(getMethodCallForIdentifier(elementAtCaret))
               : Optional.empty();
    }

    /**
     * Returns the {@link PsiMethodCallExpression} for the argument element, which is checked prior that it is an actual
     * identifier.
     */
    @Nullable
    public static PsiMethodCallExpression getMethodCallForIdentifier(@NotNull PsiElement identifier) {
        return compute(() -> (PsiMethodCallExpression) identifier.getParent().getParent());
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
    @Nullable("When either the method argument is null, or it has no subsequent call.")
    public static PsiMethodCallExpression getSubsequentMethodCall(@Nullable PsiMethodCallExpression methodCall) {
        if (methodCall == null) return null;

        PsiElement parent = methodCall.getParent();
        return parent instanceof PsiReferenceExpression && parent.getParent() instanceof PsiMethodCallExpression grandParent
               ? grandParent
               : null;
    }

    /**
     * Gets the arguments of the provided method call, given that the argument list exists and is not null.
     */
    public static PsiExpression[] getArguments(@NotNull PsiMethodCallExpression methodCall) {
        return compute(() -> methodCall.getArgumentList().getExpressions());
    }

    /**
     * Gets the first argument of the provided method call, or null if there is no argument specified.
     */
    public static PsiExpression getFirstArgument(@NotNull PsiMethodCallExpression methodCall) {
        var arguments = getArguments(methodCall);
        return arguments.length > 0 ? arguments[0] : null;
    }

    /**
     * Gets the 2nd argument of the provided method call, given that the argument list exists and is not null.
     */
    public static PsiExpression get2ndArgument(@NotNull PsiMethodCallExpression methodCall) {
        return getArguments(methodCall)[1];
    }

    /**
     * Returns the qualifier expression of the argument method call.
     */
    @Nullable
    public static PsiExpression getQualifier(@NotNull PsiMethodCallExpression methodCall) {
        return methodCall.getMethodExpression().getQualifierExpression();
    }

    @Nullable
    public static PsiElement getReferenceNameElement(PsiMethodCallExpression methodCall) {
        return methodCall.getMethodExpression().getReferenceNameElement();
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
        return compute(() -> element instanceof PsiIdentifier
                             && element.getParent() instanceof PsiReferenceExpression parentRef
                             && parentRef.getParent() instanceof PsiMethodCallExpression);
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
        while (current.getFirstChild() instanceof PsiReferenceExpression previousCallRef) {
            if (previousCallRef.getFirstChild() instanceof PsiMethodCallExpression prevCall) {
                if (methodNameToFind.equals(getMethodName(prevCall))) {
                    return Optional.of(prevCall);
                }
                current = prevCall;
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
        return collectCallsInChainFromFirst(aCallInChain)
            .stream()
            .filter(call -> methodNameToFind.equals(getMethodName(call)))
            .findFirst();
    }

    /**
     * Collects the method calls from the call chain in which the provided call is the last one.
     * <p>
     * Thus, the calls will be in a reverse order compared to the order they are actually called.
     */
    public static List<PsiMethodCallExpression> collectCallsInChainFromLast(@NotNull PsiExpression lastCallInChain) {
        var calls = new SmartList<PsiElement>(lastCallInChain);
        PsiElement current = lastCallInChain;
        while (current.getFirstChild() instanceof PsiReferenceExpression previousCallRef) {
            if (previousCallRef.getFirstChild() instanceof PsiMethodCallExpression previousCall) {
                calls.add(previousCall);
                current = previousCall;
            } else break;
        }
        return calls.stream().map(PsiMethodCallExpression.class::cast).toList();
    }

    public static List<PsiMethodCallExpression> collectCallsInChainFromFirst(PsiExpression expression, boolean includeMySelf) {
        return PsiTreeUtil.collectParents(expression,
            PsiMethodCallExpression.class, includeMySelf, e -> e instanceof PsiExpressionList || e instanceof PsiStatement);
    }

    public static List<PsiMethodCallExpression> collectCallsInChainFromFirst(PsiMethodCallExpression expression) {
        return collectCallsInChainFromFirst(expression, false);
    }

    public static List<PsiMethodCallExpression> collectCallsInChainFromFirst(PsiExpression expression) {
        return collectCallsInChainFromFirst(expression, false);
    }

    /**
     * Returns whether the provided set of expressions contain any {@link PsiNewExpression} that references
     * a non-default constructor call.
     */
    public static boolean containsCallToNonDefaultConstructor(PsiExpression[] arguments) {
        for (var argument : arguments) {
            if (argument instanceof PsiNewExpression constructorCall) {
                var argumentList = compute(constructorCall::getArgumentList);
                if (argumentList != null && compute(() -> !argumentList.isEmpty())) return true;
            }
        }
        return false;
    }

    private PsiMethodUtil() {
        //Utility class
    }
}
