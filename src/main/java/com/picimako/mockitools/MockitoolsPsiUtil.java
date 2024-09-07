//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.picimako.mockitools.MockitoQualifiedNames.AFTER;
import static com.picimako.mockitools.MockitoQualifiedNames.CALLS;
import static com.picimako.mockitools.MockitoQualifiedNames.EXTRA_INTERFACES;
import static com.picimako.mockitools.MockitoQualifiedNames.IGNORE_STUBS;
import static com.picimako.mockitools.MockitoQualifiedNames.MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ADDITIONAL_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_CAPTOR;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SETTINGS;
import static com.picimako.mockitools.MockitoQualifiedNames.RESET;
import static com.picimako.mockitools.MockitoQualifiedNames.SPY;
import static com.picimako.mockitools.MockitoQualifiedNames.TIMEOUT;
import static com.picimako.mockitools.MockitoQualifiedNames.TIMES;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.util.PsiMethodUtil.getQualifier;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.siyeh.ig.callMatcher.CallMatcher;

import java.util.Optional;

/**
 * Utilities for working with Mockito PSI.
 */
public final class MockitoolsPsiUtil {

    public static final CallMatcher MOCKITO_WITH_SETTINGS = staticCall(ORG_MOCKITO_MOCKITO, "withSettings");

    public static final CallMatcher.Simple MOCKITO_MOCK = staticCall(ORG_MOCKITO_MOCKITO, MOCK);
    private static final CallMatcher MOCKITO_SPY = staticCall(ORG_MOCKITO_MOCKITO, SPY).parameterCount(1);
    private static final CallMatcher MOCKITO_TIMES = staticCall(ORG_MOCKITO_MOCKITO, TIMES).parameterCount(1);
    private static final CallMatcher MOCKITO_CALLS = staticCall(ORG_MOCKITO_MOCKITO, CALLS).parameterCount(1);
    private static final CallMatcher MOCKITO_AFTER = staticCall(ORG_MOCKITO_MOCKITO, AFTER).parameterCount(1);
    private static final CallMatcher MOCKITO_TIMEOUT = staticCall(ORG_MOCKITO_MOCKITO, TIMEOUT).parameterCount(1);
    private static final CallMatcher MOCKITO_RESET = staticCall(ORG_MOCKITO_MOCKITO, RESET);
    private static final CallMatcher MOCKED_STATIC_RESET = instanceCall(ORG_MOCKITO_MOCKED_STATIC, RESET);
    private static final CallMatcher MOCKITO_IGNORE_STUBS = staticCall(ORG_MOCKITO_MOCKITO, IGNORE_STUBS);
    public static final CallMatcher.Simple MOCKITO_VERIFY = staticCall(ORG_MOCKITO_MOCKITO, VERIFY);
    public static final CallMatcher.Simple INORDER_VERIFY = instanceCall(ORG_MOCKITO_INORDER, VERIFY);
    public static final CallMatcher.Simple MOCKED_STATIC_VERIFY = instanceCall(ORG_MOCKITO_MOCKED_STATIC, VERIFY);
    private static final CallMatcher MOCK_SETTING_EXTRA_INTERFACES = instanceCall(ORG_MOCKITO_MOCK_SETTINGS, EXTRA_INTERFACES);

    //Argument: T...
    public static final CallMatcher MOCK_OBJECT_PARAMETER_HOLDER = staticCall(ORG_MOCKITO_MOCKITO, RESET, "clearInvocations");

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.mock} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.mock, false otherwise
     */
    public static boolean isMockitoMock(PsiMethodCallExpression expression) {
        return compute(() -> MOCKITO_MOCK.matches(expression));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.spy} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.spy, false otherwise
     */
    public static boolean isMockitoSpy(PsiMethodCallExpression expression) {
        return compute(() -> MOCKITO_SPY.matches(expression));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.verify} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.verify, false otherwise
     */
    public static boolean isMockitoVerify(PsiMethodCallExpression expression) {
        return compute(() -> MOCKITO_VERIFY.matches(expression));
    }

    /**
     * Gets whether the argument expression is a call on a matcher in {@code org.mockito.AdditionalMatchers}.
     *
     * @param expression the method call expression
     * @return true if the method is a call on an AdditionalMatchers matcher, false otherwise
     */
    public static boolean isAdditionalMatchers(PsiMethodCallExpression expression) {
        return matchesAnyMethodIn(ORG_MOCKITO_ADDITIONAL_MATCHERS, expression);
    }

    /**
     * Gets whether the argument expression is a call on a matcher in {@code org.mockito.Matchers}.
     *
     * @param expression the method call expression
     * @return true if the method is a call on a Matchers matcher, false otherwise
     * @see com.picimako.mockitools.inspection.migrationaids.v4.ArgumentMatchersCalledViaMatchersInspection
     */
    public static boolean isMatchers(PsiMethodCallExpression expression) {
        return matchesAnyMethodIn(ORG_MOCKITO_ARGUMENT_MATCHERS, expression)
               && Optional.ofNullable((PsiReferenceExpression) getQualifier(expression))
                   .map(qualifier -> (PsiClass) compute(qualifier::resolve))
                   .filter(matchers -> ORG_MOCKITO_MATCHERS.equals(matchers.getQualifiedName()))
                   .isPresent();
    }

    private static boolean matchesAnyMethodIn(String methodFqn, PsiMethodCallExpression expression) {
        return compute(() -> staticCall(methodFqn, getMethodName(expression))
            .parameterCount(expression.getArgumentList().getExpressionCount()) //matchers can have various numbers of arguments, so lets match with the current call's parameter count
            .matches(expression));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.times} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.times, false otherwise
     */
    public static boolean isTimes(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCKITO_TIMES.matches(methodCall));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.calls} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.calls, false otherwise
     */
    public static boolean isCalls(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCKITO_CALLS.matches(methodCall));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.after} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.after, false otherwise
     */
    public static boolean isAfter(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCKITO_AFTER.matches(methodCall));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.timeout} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.timeout, false otherwise
     */
    public static boolean isTimeout(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCKITO_TIMEOUT.matches(methodCall));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.MockitoSettings.extraInterfaces} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a MockitoSettings.extraInterfaces, false otherwise
     */
    public static boolean isExtraInterfaces(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCK_SETTING_EXTRA_INTERFACES.matches(methodCall));
    }

    /**
     * Gets whether the argument expression is a {@code org.mockito.Mockito.reset} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.reset, false otherwise
     */
    public static boolean isReset(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCKITO_RESET.matches(methodCall));
    }

    /**
     * Gets whether the argument expression is a {@code org.mockito.MockedStatic.reset} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a MockedStatic.reset, false otherwise
     */
    public static boolean isMockedStaticReset(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCKED_STATIC_RESET.matches(methodCall));
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.ignoreStubs} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.ignoreStubs, false otherwise
     */
    public static boolean isIgnoreStubs(PsiMethodCallExpression methodCall) {
        return compute(() -> MOCKITO_IGNORE_STUBS.matches(methodCall));
    }

    /**
     * Gets whether the type of the argument field is {@code org.mockito.ArgumentCaptor}.
     * <p>
     * This logic is used instead of inspecting the PsiType of the field because for the comparison we need only
     * the ArgumentCaptor type, but we don't want to inspect the generic type.
     *
     * @param field the field to inspect the type of
     * @return true if the field is org.mockito.ArgumentCaptor, false otherwise
     */
    public static boolean isOfTypeArgumentCaptor(PsiField field) {
        return compute(() -> {
            PsiTypeElement typeElement = field.getTypeElement();
            return typeElement != null
                   && typeElement.getType() instanceof PsiClassReferenceType type
                   && ORG_MOCKITO_ARGUMENT_CAPTOR.equals(type.rawType().getCanonicalText());
        });
    }

    private MockitoolsPsiUtil() {
        //Utility class
    }
}
