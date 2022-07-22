//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.MockitoQualifiedNames.AFTER;
import static com.picimako.mockitools.MockitoQualifiedNames.AT_LEAST;
import static com.picimako.mockitools.MockitoQualifiedNames.AT_MOST;
import static com.picimako.mockitools.MockitoQualifiedNames.CALLS;
import static com.picimako.mockitools.MockitoQualifiedNames.EXTRA_INTERFACES;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.IGNORE_STUBS;
import static com.picimako.mockitools.MockitoQualifiedNames.MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ADDITIONAL_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_CAPTOR;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SETTINGS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_STUBBING_STUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.RESET;
import static com.picimako.mockitools.MockitoQualifiedNames.SPY;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.TIMEOUT;
import static com.picimako.mockitools.MockitoQualifiedNames.TIMES;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.PsiMethodUtil.getQualifier;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.TypeConversionUtil;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for working with Mockito PSI.
 */
public final class MockitoolsPsiUtil {

    public static final CallMatcher MOCKITO_OCCURRENCE_BASED_VERIFICATION_MODES = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, TIMES, AT_LEAST, AT_MOST).parameterCount(1);
    public static final CallMatcher MOCKITO_WITH_SETTINGS = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, "withSettings");

    /**
     * The original logic and set of non-mockable types can be found in Mockito's
     * <ul>
     *     <li><a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineDelegateByteBuddyMockMaker.java">InlineDelegateByteBuddyMockMaker#isTypeMockable(Class) method</a>,</li>
     *     <li><a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineBytecodeGenerator.java">InlineBytecodeGenerator#EXCLUDES set</a>.</li>
     * </ul>
     */
    private static final Set<String> NON_MOCKABLE_TYPES = Set.of(CommonClassNames.JAVA_LANG_CLASS, CommonClassNames.JAVA_LANG_STRING);

    public static final CallMatcher.Simple MOCKITO_MOCK = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, MOCK);
    private static final CallMatcher MOCKITO_SPY = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, SPY).parameterCount(1);
    private static final CallMatcher BDDMOCKITO_GIVEN = CallMatcher.staticCall(ORG_MOCKITO_BDDMOCKITO, GIVEN).parameterCount(1);
    private static final CallMatcher BDDMOCKITO_WILL_X =
        CallMatcher.staticCall(ORG_MOCKITO_BDDMOCKITO, "will", "willReturn", "willThrow", "willAnswer", "willCallRealMethod");
    private static final CallMatcher BDDMOCKITO_THEN = CallMatcher.staticCall(ORG_MOCKITO_BDDMOCKITO, THEN).parameterCount(1);
    private static final CallMatcher MOCKITO_WHEN = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, WHEN).parameterCount(1);
    private static final CallMatcher MOCKITO_DO_X_WHEN = CallMatcher.instanceCall(ORG_MOCKITO_STUBBING_STUBBER, WHEN);
    private static final CallMatcher MOCKITO_DO_X =
        CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, "doReturn", "doThrow", "doAnswer", "doCallRealMethod", "doNothing");
    private static final CallMatcher MOCKITO_TIMES = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, TIMES).parameterCount(1);
    private static final CallMatcher MOCKITO_CALLS = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, CALLS).parameterCount(1);
    private static final CallMatcher MOCKITO_AFTER = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, AFTER).parameterCount(1);
    private static final CallMatcher MOCKITO_TIMEOUT = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, TIMEOUT).parameterCount(1);
    private static final CallMatcher MOCKITO_RESET = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, RESET);
    private static final CallMatcher MOCKED_STATIC_RESET = CallMatcher.instanceCall(ORG_MOCKITO_MOCKED_STATIC, RESET);
    private static final CallMatcher MOCKITO_IGNORE_STUBS = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, IGNORE_STUBS);
    public static final CallMatcher.Simple MOCKITO_VERIFY = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, VERIFY);
    public static final CallMatcher.Simple INORDER_VERIFY = CallMatcher.instanceCall(ORG_MOCKITO_INORDER, VERIFY);
    private static final CallMatcher MOCK_SETTING_EXTRA_INTERFACES = CallMatcher.instanceCall(ORG_MOCKITO_MOCK_SETTINGS, EXTRA_INTERFACES);

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.mock} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.mock, false otherwise
     */
    public static boolean isMockitoMock(PsiMethodCallExpression expression) {
        return MOCKITO_MOCK.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.spy} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.spy, false otherwise
     */
    public static boolean isMockitoSpy(PsiMethodCallExpression expression) {
        return MOCKITO_SPY.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.when} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.when, false otherwise
     */
    public static boolean isMockitoWhen(PsiMethodCallExpression expression) {
        return MOCKITO_WHEN.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.verify} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.verify, false otherwise
     */
    public static boolean isMockitoVerify(PsiMethodCallExpression expression) {
        return MOCKITO_VERIFY.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.InOrder.verify} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a InOrder.verify, false otherwise
     */
    public static boolean isInOrderVerify(PsiMethodCallExpression expression) {
        return INORDER_VERIFY.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.BDDMockito.given} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a BDDMockito.given, false otherwise
     */
    public static boolean isBDDMockitoGiven(PsiMethodCallExpression expression) {
        return BDDMOCKITO_GIVEN.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.BDDMockito.will*} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a BDDMockito.will*, false otherwise
     */
    public static boolean isBDDMockitoWillX(PsiMethodCallExpression expression) {
        return BDDMOCKITO_WILL_X.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.BDDMockito.then} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a BDDMockito.then, false otherwise
     */
    public static boolean isBDDMockitoThen(PsiMethodCallExpression expression) {
        return BDDMOCKITO_THEN.matches(expression);
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
     */
    public static boolean isMatchers(PsiMethodCallExpression expression) {
        return matchesAnyMethodIn(ORG_MOCKITO_ARGUMENT_MATCHERS, expression)
            && Optional.ofNullable((PsiReferenceExpression) getQualifier(expression))
            .map(qualifier -> (PsiClass) qualifier.resolve())
            .filter(matchers -> ORG_MOCKITO_MATCHERS.equals(matchers.getQualifiedName()))
            .isPresent();
    }

    private static boolean matchesAnyMethodIn(String methodFqn, PsiMethodCallExpression expression) {
        return CallMatcher.staticCall(methodFqn, getMethodName(expression))
            .parameterCount(expression.getArgumentList().getExpressionCount()) //matchers can have various numbers of arguments, so lets match with the current call's parameter count
            .matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.do...().when} method call.
     * <p>
     * The corresponding {@code when} method is defined in {@code org.mockito.stubbing.Stubber} which is related to the {@code do...()} family stubbing.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.do...().when, false otherwise
     */
    public static boolean isMockitoDoXWhen(PsiMethodCallExpression expression) {
        return MOCKITO_DO_X_WHEN.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.do*} method call.
     *
     * @param expression the method call expression
     * @return true if the method is a Mockito.do*, false otherwise
     */
    public static boolean isMockitoDoX(PsiMethodCallExpression expression) {
        return MOCKITO_DO_X.matches(expression);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.times} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.times, false otherwise
     */
    public static boolean isTimes(PsiMethodCallExpression methodCall) {
        return MOCKITO_TIMES.matches(methodCall);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.calls} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.calls, false otherwise
     */
    public static boolean isCalls(PsiMethodCallExpression methodCall) {
        return MOCKITO_CALLS.matches(methodCall);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.after} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.after, false otherwise
     */
    public static boolean isAfter(PsiMethodCallExpression methodCall) {
        return MOCKITO_AFTER.matches(methodCall);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.timeout} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.timeout, false otherwise
     */
    public static boolean isTimeout(PsiMethodCallExpression methodCall) {
        return MOCKITO_TIMEOUT.matches(methodCall);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.MockitoSettings.extraInterfaces} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a MockitoSettings.extraInterfaces, false otherwise
     */
    public static boolean isExtraInterfaces(PsiMethodCallExpression methodCall) {
        return MOCK_SETTING_EXTRA_INTERFACES.matches(methodCall);
    }

    /**
     * Gets whether the argument expression is a {@code org.mockito.Mockito.reset} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.reset, false otherwise
     */
    public static boolean isReset(PsiMethodCallExpression methodCall) {
        return MOCKITO_RESET.matches(methodCall);
    }

    /**
     * Gets whether the argument expression is a {@code org.mockito.MockedStatic.reset} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.isReset, false otherwise
     */    /**
     * Gets whether the argument expression is a {@code org.mockito.MockedStatic.reset} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a MockedStatic.reset, false otherwise
     */
    public static boolean isMockedStaticReset(PsiMethodCallExpression methodCall) {
        return MOCKED_STATIC_RESET.matches(methodCall);
    }

    /**
     * Gets whether the argument expression is an {@code org.mockito.Mockito.ignoreStubs} method call.
     *
     * @param methodCall the method call expression
     * @return true if the method is a Mockito.ignoreStubs, false otherwise
     */
    public static boolean isIgnoreStubs(PsiMethodCallExpression methodCall) {
        return MOCKITO_IGNORE_STUBS.matches(methodCall);
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
        PsiTypeElement typeElement = field.getTypeElement();
        return typeElement != null
            && typeElement.getType() instanceof PsiClassReferenceType
            && ORG_MOCKITO_ARGUMENT_CAPTOR.equals(((PsiClassReferenceType) typeElement.getType()).rawType().getCanonicalText());
    }

    /**
     * Finds the first @DoNotMock annotated type in the class hierarchy, and returns it with the optional reason provided.
     *
     * @param type the type to check the type hierarchy of for the @DoNotMock annotation
     * @return the class annotated as @DoNotMock or null if not found, and the reason provided if any if the class is annotated as @DoNotMock
     */
    public static Pair<PsiClass, String> getDoNotMockAnnotatedTypeAndReasonInHierarchy(@Nullable PsiType type) {
        if (type instanceof PsiClassType) {
            PsiClass referencedClass = ((PsiClassType) type).resolve();
            if (referencedClass != null) {
                var doNotMock = getDoNotMockAnnotationOn(referencedClass);
                if (doNotMock.isPresent()) {
                    return Pair.create(referencedClass, AnnotationUtil.getStringAttributeValue(doNotMock.get(), "reason"));
                }
                for (PsiClass cls : InheritanceUtil.getSuperClasses(referencedClass)) {
                    var doNotMockInHierarchy = getDoNotMockAnnotationOn(cls);
                    if (doNotMockInHierarchy.isPresent()) {
                        return Pair.create(cls, AnnotationUtil.getStringAttributeValue(doNotMockInHierarchy.get(), "reason"));
                    }
                }
            }
        }
        return Pair.empty();
    }

    /**
     * This is a simplified version {@link #getDoNotMockAnnotatedTypeAndReasonInHierarchy(PsiType)} that returns a boolean whether any of
     * the types in the type hierarchy is annotated with @DoNotMock.
     *
     * @param type the type to check the type hierarchy of for the @DoNotMock annotation
     * @since 0.2.0
     */
    private static boolean isDoNotMockAnnotatedInHierarchy(@Nullable PsiType type) {
        if (type instanceof PsiClassType) {
            PsiClass referencedClass = ((PsiClassType) type).resolve();
            if (referencedClass != null) {
                return getDoNotMockAnnotationOn(referencedClass).isPresent()
                    || InheritanceUtil.getSuperClasses(referencedClass).stream().anyMatch(cls -> getDoNotMockAnnotationOn(cls).isPresent());
            }
        }
        return false;
    }

    private static Optional<PsiAnnotation> getDoNotMockAnnotationOn(PsiClass clazz) {
        return !CommonClassNames.JAVA_LANG_OBJECT.equals(clazz.getQualifiedName())
            ? Arrays.stream(clazz.getAnnotations())
            .filter(annotation -> annotation.getQualifiedName().endsWith(MockitoQualifiedNames.ORG_MOCKITO_DO_NOT_MOCK))
            .findFirst()
            : Optional.empty();
    }

    /**
     * Returns whether the argument type is mockable, be it not restricted by Mockito itself, or by a @DoNotMock annotation.
     *
     * @since 0.2.0
     */
    public static boolean isMockableTypeInAnyWay(@Nullable PsiType type) {
        return isMockableType(type) && !isDoNotMockAnnotatedInHierarchy(type);
    }

    /**
     * Gets whether the argument type is mockable by Mockito.
     *
     * @param type the type to validate
     * @return true if the type is mockable, false otherwise
     * @see #NON_MOCKABLE_TYPES
     */
    public static boolean isMockableType(@Nullable PsiType type) {
        return type != null
            && !TypeConversionUtil.isPrimitiveWrapper(type)
            && !TypeConversionUtil.isPrimitive(type.getCanonicalText())
            && !NON_MOCKABLE_TYPES.contains(type.getCanonicalText());
    }

    private MockitoolsPsiUtil() {
        //Utility class
    }
}
