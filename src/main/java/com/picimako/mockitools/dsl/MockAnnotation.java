//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.dsl;

import static com.intellij.openapi.application.ReadAction.compute;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.picimako.mockitools.MockitoQualifiedNames;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for the {@code @Mock} annotation.
 *
 * @since 0.8.0
 */
public final class MockAnnotation {

    /**
     * Returns whether the provided boolean attribute is set to true (thus enabled) on the {@code mockAnnotation}.
     * <p>
     * This method doesn't check if the provided annotation is actually a {@code @Mock} one, that check has to be done
     * separately, before calling this method.
     *
     * @param mockAnnotation the @Mock annotation on a field
     * @param attributeName  the name of a boolean-value attribute on the @Mock annotation.
     */
    public static boolean isAttributeEnabledOnMockAnnotation(PsiAnnotation mockAnnotation, String attributeName) {
        Boolean attributeValue = compute(() -> AnnotationUtil.getBooleanAttributeValue(mockAnnotation, attributeName));
        return Boolean.TRUE.equals(attributeValue); //Given that the default values are false, and to be true they have to be specified explicitly
    }

    /**
     * Returns whether the argument annotation is {@code @org.mockito.Mock}.
     *
     * @param annotation the annotation to examine
     */
    public static boolean isMockAnnotation(@NotNull PsiAnnotation annotation) {
        return annotation.hasQualifiedName(MockitoQualifiedNames.ORG_MOCKITO_MOCK);
    }

    private MockAnnotation() {
        //utility class
    }
}
