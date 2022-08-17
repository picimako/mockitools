//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static com.intellij.psi.util.InheritanceUtil.getSuperClasses;

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.TypeConversionUtil;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
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
     * Finds the first @DoNotMock annotated type in the class hierarchy, and returns it with the optional reason provided.
     *
     * @param type the type to check the type hierarchy of for the @DoNotMock annotation
     * @return the optional reason of the @DoNotMock annotation, or empty optional if no @DoNotMock annotation is found
     */
    public static Optional<DoNotMockType> getDoNotMockTypeInHierarchy(@Nullable PsiType type) {
        if (type instanceof PsiClassType) {
            PsiClass referencedClass = ((PsiClassType) type).resolve();
            if (referencedClass != null) {
                //Checks if the use class type is annotated
                var doNotMock = getDoNotMockAnnotationOn(referencedClass);
                if (doNotMock.isPresent()) {
                    return Optional.of(new DoNotMockType(getStringAttributeValue(doNotMock.get(), "reason")));
                }
                //If the direct class type is not annotated, proceeds to check all its super classes
                for (PsiClass cls : getSuperClasses(referencedClass)) {
                    var doNotMockInHierarchy = getDoNotMockAnnotationOn(cls);
                    if (doNotMockInHierarchy.isPresent()) {
                        return Optional.of(new DoNotMockType(getStringAttributeValue(doNotMockInHierarchy.get(), "reason")));
                    }
                }
            }
        }
        return Optional.empty();
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

    /**
     * This is a simplified version {@link #getDoNotMockTypeInHierarchy(PsiType)} that returns a boolean whether any of
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
                    || getSuperClasses(referencedClass).stream().anyMatch(cls -> getDoNotMockAnnotationOn(cls).isPresent());
            }
        }
        return false;
    }

    private MockableTypesUtil() {
        //Utility class
    }

    @AllArgsConstructor
    public static final class DoNotMockType {
        @Nullable
        public final String reason;

        public boolean hasReason() {
            return reason != null && !reason.isBlank();
        }
    }
}
