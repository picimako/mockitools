//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.ExceptionUtil.isCheckedException;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;
import static com.picimako.mockitools.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.PsiMethodUtil.getSubsequentMethodCall;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.picimako.mockitools.inspection.ThrowStubDescriptors.DO_THROW_WHEN;
import static com.picimako.mockitools.inspection.ThrowStubDescriptors.GIVEN_WILL_THROW;
import static com.picimako.mockitools.inspection.ThrowStubDescriptors.WHEN_THEN_THROW;
import static com.picimako.mockitools.inspection.ThrowStubDescriptors.WILL_THROW_GIVEN;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Arrays;
import java.util.Optional;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiCall;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.mockitools.PsiMethodUtil;
import com.picimako.mockitools.StubType;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports exception references in {@code *Throw()} stubbing methods based on Mockito's rule on checked exceptions:
 * <i>If [the specified exception types] contain a checked exception then it has to match one of the checked exceptions of method signature.</i>
 * <p>
 * The following constructs are supported:
 * <ul>
 *     <li>{@code Mockito.when().thenThrow()} including further chained {@code thenThrow()} calls</li>
 *     <li>{@code BDDMockito.given().willThrow(){} including further chained {@code willThrow()} calls</li>
 *     <li>{@code Mockito.doThrow().when()}</li>
 *     <li>{@code Mockito.doThrow().doThrow().when()}</li>
 *     <li>{@code BDDMockito.willThrow().given()}</li>
 *     <li>{@code BDDMockito.willThrow().willThrow().given()}</li>
 * </ul>
 * <p>
 * In case of an empty list, no problem is reported.
 *
 * @since 0.3.0
 */
public class ThrowsCheckedExceptionStubbingInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    /**
     * String-based method name validation is put in place to increase performance by avoiding executing CallMatcher validation logic unnecessarily.
     * If the method name doesn't match, the CallMatchers won't be called.
     */
    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        PsiExpression[] exceptionRefs = getArguments(expression);
        if (exceptionRefs.length == 0) return;
        String methodName = getMethodName(expression);

        if (THEN_THROW.equals(methodName))
            checkAndRegister(WHEN_THEN_THROW, expression, exceptionRefs, holder);
        else if (WILL_THROW.equals(methodName) && !checkAndRegister(GIVEN_WILL_THROW, expression, exceptionRefs, holder))
            checkAndRegister(WILL_THROW_GIVEN, expression, exceptionRefs, holder);
        else if (DO_THROW.equals(methodName))
            checkAndRegister(DO_THROW_WHEN, expression, exceptionRefs, holder);
    }

    /**
     * Retrieves the method that is being stubbed on a mock object, resolves the original method and compares the exceptions
     * specified in the {@code *Throw()} call with the ones in the resolved method's {@code throws} clause.
     * <p>
     * If an exception specified during stubbing is a checked exception, but it is not listed in the throws clause, it is then reported.
     * <p>
     * The return value signals whether the execution stepped into this method. It helps shorten the code in #checkMethodCallExpression().
     */
    private boolean checkAndRegister(ThrowStubDescriptor descriptor, PsiMethodCallExpression expression, PsiExpression[] exceptionRefs, ProblemsHolder holder) {
        if (descriptor.matcher.matches(expression)) {
            descriptor.findStubbingCallInChain(expression) //e.g. when(mockObject.doSomething()) / given(mockObject).doSomething()
                .map(PsiMethodUtil::getFirstArgument) //mockObject.doSomething() / mockObject
                .filter(descriptor::isValidStubbingArgument)
                .map(stub -> resolveStubbedMethod(stub, descriptor.stubType)) //doSomething()
                .map(psiMethod -> psiMethod.getThrowsList().getReferencedTypes())
                .ifPresent(throwsClauseTypes -> {
                    for (var exceptionRef : exceptionRefs) {
                        if (isCheckedException(exceptionRef)
                            && Arrays.stream(throwsClauseTypes).noneMatch(type -> type.equals(evaluateType(exceptionRef)))) {
                            holder.registerProblem(exceptionRef, MockitoolsBundle.inspection("invalid.checked.exception.in.stubbing"));
                        }
                    }
                });
            return true;
        }
        return false;
    }

    private PsiType evaluateType(PsiExpression expression) {
        return expression instanceof PsiClassObjectAccessExpression ? ClassObjectAccessUtil.getOperandType(expression) : expression.getType();
    }

    /**
     * Resolves the method that is being stubbed.
     * <p>
     * In case of {@link StubType#STUBBING}, e.g. {@code when(mockObject.doSomething()).then...()} it resolves the argument call.
     * <p>
     * In case of {@link StubType#STUBBER}, e.g. {@code do...().given(mockObject).doSomething()} it finds the subsequent method
     * to the one that the mock object is specified in: subsequent to {@code given()} in this case, resulting in {@code doSomething()}.
     */
    @Nullable
    private PsiMethod resolveStubbedMethod(PsiExpression stub, StubType stubType) {
        return stubType == StubType.STUBBING
            ? ((PsiMethodCallExpression) stub).resolveMethod()
            : Optional.ofNullable(getSubsequentMethodCall(PsiTreeUtil.getParentOfType(stub, PsiMethodCallExpression.class)))
            .map(PsiCall::resolveMethod)
            .orElse(null);
    }
}
