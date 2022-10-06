//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.dsl;

import static com.picimako.mockitools.MockitoQualifiedNames.STUB_ONLY;
import static com.picimako.mockitools.dsl.MockAnnotation.isAttributeEnabledOnMockAnnotation;
import static com.picimako.mockitools.dsl.MockAnnotation.isMockAnnotation;
import static com.picimako.mockitools.dsl.MockSettings.hasCallTo;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;

import java.util.Optional;

/**
 * Represents a mock variable regardless if it is a local variable or a field.
 *
 * @since 0.8.0
 */
public final class MockObject {

    /**
     * Returns whether the mock object referenced by the argument expression is configured as stub only.
     * <p>
     * The configuration may happen via the {@code @Mock} annotation as {@code @Mock(stubOnly = true)},
     * or via the {@code Mockito.mock()} call as e.g. {@code Mockito.mock(Type.class, withSettings().stubOnly())}.
     *
     * @param ref the expression referencing the mock object
     */
    public static boolean isStubOnly(PsiReferenceExpression ref) {
        return Optional.of(ref)
            .map(PsiReference::resolve)
            .map(mockVariable -> {
                if (mockVariable instanceof PsiField) return isFieldMock((PsiField) mockVariable);
                else if (mockVariable instanceof PsiLocalVariable)
                    return isVariableMock((PsiLocalVariable) mockVariable);
                return false;
            }).orElse(false);
    }

    private static boolean isFieldMock(PsiField mockField) {
        var annotations = mockField.getAnnotations();
        //If the field is annotated only with @Mock
        return annotations.length == 1
            && isMockAnnotation(annotations[0])
            && isAttributeEnabledOnMockAnnotation(annotations[0], STUB_ONLY);
    }

    private static boolean isVariableMock(PsiLocalVariable mockVariable) {
        return MockSettings.fromMockVariable(mockVariable)
            .map(mockSettings -> hasCallTo(mockSettings, STUB_ONLY))
            .orElse(false);
    }

    private MockObject() {
        //utility class
    }
}
