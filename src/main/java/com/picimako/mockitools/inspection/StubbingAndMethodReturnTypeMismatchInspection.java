//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.google.common.collect.Iterables.getLast;
import static com.picimako.mockitools.CallChainEndsWith.ENDS_WITH_GIVEN;
import static com.picimako.mockitools.CallChainEndsWith.ENDS_WITH_WHEN;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoWillX;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoDoX;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static java.util.stream.Collectors.toSet;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiCall;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.picimako.mockitools.CallChainEndsWith;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Reports {@code doNothing()} and {@code willDoNothing()} calls when the stubbed method's return type is not void.
 * <p>
 * Based on Mockito's <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java#L568">corresponding error handling</a>,
 * <i>Only void methods can doNothing()</i>.
 * <p>
 * Also reports {@code *Return()} calls when the stubbed method's return type is void,
 * which is based on Mockito's <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java#L546">corresponding error handling</a>,
 * <p>
 * It highlights every instance of {@code doNothing()}, {@code willDoNothing()} and {@code *Return()} calls in the affected call chains.
 *
 * @since 0.7.0
 */
public class StubbingAndMethodReturnTypeMismatchInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (isMockitoDoX(expression)) checkAndRegister("doNothing", "doReturn", ENDS_WITH_WHEN, expression, holder);
        else if (isBDDMockitoWillX(expression))
            checkAndRegister("willDoNothing", "willReturn", ENDS_WITH_GIVEN, expression, holder);
    }

    private void checkAndRegister(@NotNull String doNothingMethod, @NotNull String doReturnMethod,
                                  CallChainEndsWith endsWith,
                                  PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        var calls = collectCallsInChainFromFirst(expression, true);
        if (endsWith.analyze(calls)) {
            var doNothingMethodCalls = findMethodCalls(doNothingMethod, calls);
            if (!doNothingMethodCalls.isEmpty())
                registerIfStubbedMethodsReturnTypeIsNotVoid(getLast(calls), doNothingMethodCalls, holder);

            var doReturnMethodCalls = findMethodCalls(doReturnMethod, calls);
            if (!doReturnMethodCalls.isEmpty())
                registerIfStubbedMethodsReturnTypeIsVoid(getLast(calls), doReturnMethodCalls, holder);
        }
    }

    /**
     * Returns all {@code doNothing()} or {@code willDoNothing()} calls from the provided call chain.
     *
     * @param methodName {@code doNothing} or {@code willDoNothing}, or {@code doReturn}, {@code thenReturn} or {@code willReturn}
     * @param calls      the stubbing call chain
     */
    private static Set<PsiMethodCallExpression> findMethodCalls(@NotNull String methodName, List<PsiMethodCallExpression> calls) {
        return calls.stream().filter(call -> methodName.equals(getMethodName(call))).collect(toSet());
    }

    /**
     * Registers all {@code doNothing()} and {@code willDoNothing()} calls if the {@code stubbedMethod}'s return type is not void.
     *
     * @param stubbedMethod the stubbed method called sequent to {@code given(mock)} or {@code when(mock)}
     */
    private void registerIfStubbedMethodsReturnTypeIsNotVoid(PsiMethodCallExpression stubbedMethod, Set<PsiMethodCallExpression> doNothingMethodCalls, @NotNull ProblemsHolder holder) {
        getReturnTypeOf(stubbedMethod)
            .filter(type -> !type.equals(PsiType.VOID))
            .ifPresent(__ -> {
                for (var doNothing : doNothingMethodCalls) {
                    holder.registerProblem(getReferenceNameElement(doNothing), MockitoolsBundle.inspection("void.method.is.stubbed.to.do.nothing", getMethodName(stubbedMethod)));
                }
            });
    }

    /**
     * Registers all {@code doNothing()} and {@code willDoNothing()} calls if the {@code stubbedMethod}'s return type is not void.
     *
     * @param stubbedMethod the stubbed method called sequent to {@code given(mock)} or {@code when(mock)}
     */
    private void registerIfStubbedMethodsReturnTypeIsVoid(PsiMethodCallExpression stubbedMethod, Set<PsiMethodCallExpression> doReturnMethodCalls, @NotNull ProblemsHolder holder) {
        getReturnTypeOf(stubbedMethod)
            .filter(type -> type.equals(PsiType.VOID))
            .ifPresent(__ -> {
                for (var doReturn : doReturnMethodCalls) {
                    holder.registerProblem(getReferenceNameElement(doReturn), MockitoolsBundle.inspection("non.void.method.is.stubbed.with.return.value", getMethodName(stubbedMethod)));
                }
            });
    }

    private Optional<PsiType> getReturnTypeOf(PsiMethodCallExpression stubbedMethod) {
        return Optional.ofNullable(stubbedMethod)
            .map(PsiCall::resolveMethod)
            .map(PsiMethod::getReturnType);
    }
}
