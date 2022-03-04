//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.ExceptionUtil.isCheckedException;
import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDD_MY_ONGOING_STUBBING;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDD_STUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ONGOING_STUBBING;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_STUBBING_BASESTUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;
import static com.picimako.mockitools.PsiMethodUtil.findCallDownwardsInChain;
import static com.picimako.mockitools.PsiMethodUtil.findCallUpwardsInChain;
import static com.picimako.mockitools.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.PsiMethodUtil.getSubsequentMethodCall;
import static com.picimako.mockitools.PsiMethodUtil.isMethodCall;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.ArrayList;
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
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.mockitools.PsiMethodUtil;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports exception references in {@code *throw()} stubbing methods based on Mockito's rule on checked exceptions:
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
    private static final CallMatcher[] CALL_MATCHER_EMPTY = new CallMatcher[0];
    private static final String CLASS_THROWABLE = "java.lang.Class<? extends java.lang.Throwable>";
    private static final String CLASS_THROWABLES = CLASS_THROWABLE + "...";
    private static final String JAVA_LANG_THROWABLES = "java.lang.Throwable...";

    // Mockito.when().thenThrow()[.thenThrow()]
    private static final ThrowDescriptor WHEN_THEN_THROW = new ThrowDescriptor(WHEN, StubType.STUBBING, THEN_THROW, ORG_MOCKITO_ONGOING_STUBBING, null);
    // BDDMockito.given().willThrow()[.willThrow()]
    private static final ThrowDescriptor GIVEN_WILL_THROW = new ThrowDescriptor(GIVEN, StubType.STUBBING, WILL_THROW, ORG_MOCKITO_BDD_MY_ONGOING_STUBBING, null);
    // Mockito.doThrow()[.doThrow()].when()
    private static final ThrowDescriptor DO_THROW_WHEN = new ThrowDescriptor(WHEN, StubType.STUBBER, DO_THROW, ORG_MOCKITO_STUBBING_BASESTUBBER, ORG_MOCKITO_MOCKITO);
    // BDDMockito.willThrow()[.willThrow()].given()
    private static final ThrowDescriptor WILL_THROW_GIVEN = new ThrowDescriptor(GIVEN, StubType.STUBBER, WILL_THROW, ORG_MOCKITO_BDD_STUBBER, ORG_MOCKITO_BDDMOCKITO);

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
     * specified in the {@code *throw()} call with the ones in the resolved method's {@code throws} clause.
     * <p>
     * If an exception specified during stubbing is a checked exception, but it is not listed in the throws clause, it is then reported.
     * <p>
     * The return value signals whether the execution stepped into this method. It helps shorten the code in #checkMethodCallExpression().
     */
    private boolean checkAndRegister(ThrowDescriptor descriptor, PsiMethodCallExpression expression, PsiExpression[] exceptionRefs, ProblemsHolder holder) {
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

    /**
     * Descriptor for holding *throw() call related information.
     */
    private static final class ThrowDescriptor {
        /**
         * The method name that accepts the mock object or the call on a mock object. Usually {@code given} or {@code when}.
         */
        private final String stubberCallName;
        private final StubType stubType;
        private final CallMatcher matcher;

        private ThrowDescriptor(String stubberCallName, StubType stubType, String methodName, String instanceName, @Nullable String staticName) {
            this.stubberCallName = stubberCallName;
            this.stubType = stubType;
            this.matcher = createMatcher(methodName, instanceName, staticName);
        }

        boolean isValidStubbingArgument(PsiExpression stub) {
            return stubType == StubType.STUBBING ? isMethodCall(stub) : stub instanceof PsiReferenceExpression;
        }

        Optional<PsiMethodCallExpression> findStubbingCallInChain(PsiMethodCallExpression expression) {
            return stubType == StubType.STUBBING
                ? findCallUpwardsInChain(expression, stubberCallName)
                : findCallDownwardsInChain(expression, stubberCallName);
        }

        /**
         * Creates {@link CallMatcher}s for the provided method name in the provided classes for the method signatures with
         * {@code Class} and {@code Class, Class...} parameter lists.
         * <p>
         * If {@code staticClassName} is specified as well (so there are both static and instance calls available for the method name),
         * then call matchers for that static method are created too.
         * <p>
         * It behaves similarly to the Throwables based parameterization.
         */
        private static CallMatcher createMatcher(String methodName, String instanceName, @Nullable String staticName) {
            var matchers = new ArrayList<CallMatcher>(4);
            matchers.add(instanceCall(instanceName, methodName).parameterTypes(CLASS_THROWABLE));
            matchers.add(instanceCall(instanceName, methodName).parameterTypes(CLASS_THROWABLE, CLASS_THROWABLES));
            matchers.add(instanceCall(instanceName, methodName).parameterTypes(JAVA_LANG_THROWABLES));

            if (staticName != null) {
                matchers.add(staticCall(staticName, methodName).parameterTypes(CLASS_THROWABLE));
                matchers.add(staticCall(staticName, methodName).parameterTypes(CLASS_THROWABLE, CLASS_THROWABLES));
                matchers.add(staticCall(staticName, methodName).parameterTypes(JAVA_LANG_THROWABLES));
            }
            return CallMatcher.anyOf(matchers.toArray(CALL_MATCHER_EMPTY));
        }
    }

    /**
     * Naming is according the Mockito naming conventions.
     */
    private enum StubType {
        /**
         * When the stubbing of the action is called later in a stubbing call chain than the specification of the mock object.
         * <p>
         * E.g. {@code Mockito.when(mockObject.doesSomething()).thenThrow(SomeException.class)}
         */
        STUBBING,
        /**
         * When the stubbing of the action is called earlier in a stubbing call chain than the specification of the mock object.
         * <p>
         * E.g. {@code Mockito.doThrow(SomeException.class).when(mockObject.doesSomething())}
         */
        STUBBER
    }
}
