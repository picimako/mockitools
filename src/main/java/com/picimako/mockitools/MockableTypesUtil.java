//Copyright 2025 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static com.intellij.openapi.application.ReadAction.computeBlocking;
import static com.intellij.psi.util.InheritanceUtil.getSuperClasses;
import static com.intellij.psi.util.TypeConversionUtil.isPrimitive;
import static com.intellij.psi.util.TypeConversionUtil.isPrimitiveWrapper;

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;

/**
 * Utility for type mockability.
 */
public final class MockableTypesUtil {

    /**
     * The original logic and set of non-mockable types can be found in Mockito's
     * <ul>
     *     <li><a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineDelegateByteBuddyMockMaker.java">InlineDelegateByteBuddyMockMaker#isTypeMockable(Class) method</a>,</li>
     *     <li><a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineBytecodeGenerator.java">InlineBytecodeGenerator#EXCLUDES set</a>.</li>
     * </ul>
     */
    private static final Set<String> NON_MOCKABLE_TYPES = Set.of(CommonClassNames.JAVA_LANG_CLASS, CommonClassNames.JAVA_LANG_STRING);

    /**
     * Finds the first @DoNotMock annotated type in the class hierarchy, and returns it with the reason provided.
     *
     * @param type the type to check the type hierarchy of for the @DoNotMock annotation
     * @return the reason of the @DoNotMock annotation, or null if no @DoNotMock annotation is found
     */
    @Nullable
    public static DoNotMockType getDoNotMockTypeInHierarchy(@Nullable PsiType type) {
        if (type instanceof PsiClassType classType) {
            PsiClass referencedClass = computeBlocking(classType::resolve);
            if (referencedClass != null) {
                //Checks if the use class type is annotated
                var doNotMock = getDoNotMockAnnotationOn(referencedClass);
                if (doNotMock != null) {
                    return new DoNotMockType(doNotMock);
                }
                //If the direct class type is not annotated, proceeds to check all its super classes
                for (PsiClass cls : getSuperClasses(referencedClass)) {
                    var doNotMockInHierarchy = getDoNotMockAnnotationOn(cls);
                    if (doNotMockInHierarchy != null) {
                        return new DoNotMockType(doNotMockInHierarchy);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static PsiAnnotation getDoNotMockAnnotationOn(PsiClass clazz) {
        return computeBlocking(() -> !CommonClassNames.JAVA_LANG_OBJECT.equals(clazz.getQualifiedName())
            ? Arrays.stream(clazz.getAnnotations())
            .filter(annotation -> annotation.getQualifiedName().endsWith(MockitoQualifiedNames.ORG_MOCKITO_DO_NOT_MOCK))
            .findFirst()
            .orElse(null)
            : null);
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
        if (type != null) {
            return computeBlocking(() -> {
                if (computeBlocking(() -> !isPrimitiveWrapper(type))) {
                    String canonicalText = computeBlocking(type::getCanonicalText);
                    return computeBlocking(() -> !isPrimitive(canonicalText))
                        && !NON_MOCKABLE_TYPES.contains(computeBlocking(type::getCanonicalText));
                }
                return false;
            });
        }

        return false;
    }

    /**
     * This is a simplified version {@link #getDoNotMockTypeInHierarchy(PsiType)} that returns a boolean whether any of
     * the types in the type hierarchy is annotated with @DoNotMock.
     *
     * @param type the type to check the type hierarchy of for the @DoNotMock annotation
     * @since 0.2.0
     */
    private static boolean isDoNotMockAnnotatedInHierarchy(@Nullable PsiType type) {
        if (type instanceof PsiClassType classType) {
            PsiClass referencedClass = computeBlocking(classType::resolve);
            if (referencedClass != null) {
                return getDoNotMockAnnotationOn(referencedClass) != null
                    || computeBlocking(() -> getSuperClasses(referencedClass).stream().anyMatch(cls -> getDoNotMockAnnotationOn(cls) != null));
            }
        }
        return false;
    }

    private MockableTypesUtil() {
        //Utility class
    }

    @RequiredArgsConstructor
    public static final class DoNotMockType {
        @NotNull
        private final PsiAnnotation doNotMock;
        @Getter
        @Nullable
        private String reason;

        public boolean hasReason() {
            reason = computeBlocking(() -> getStringAttributeValue(doNotMock, "reason"));
            return reason != null && !reason.isBlank();
        }
    }
}
