//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link MockableTypesUtil}.
 */
class MockableTypesUtilTest extends MockitoolsTestBase {

    @Test
    void testIsMockableType() {
        getFixture().configureByText("IsMockableTypeTest.java",
            """
                import org.mockito.Mock;

                public class IsMockableTypeTest {
                    @Mock
                    public List<String> <caret>mock;
                }
                """);

        assertThat(MockableTypesUtil.isMockableType(getFieldType())).isTrue();
    }

    @Test
    void testIsNotMockableTypePrimitive() {
        getFixture().configureByText("IsNotMockableTypePrimitiveTest.java",
            """
                import org.mockito.Mock;

                public class IsNotMockableTypePrimitiveTest {
                    @Mock
                    public int <caret>mock;
                }
                """);

        assertThat(MockableTypesUtil.isMockableType(getFieldType())).isFalse();
    }

    @Test
    void testIsNotMockableTypeWrapper() {
        getFixture().configureByText("IsNotMockableTypeWrapperTest.java",
            """
                import org.mockito.Mock;

                public class IsNotMockableTypeWrapperTest {
                    @Mock
                    public String <caret>mock;
                }
                """);

        assertThat(MockableTypesUtil.isMockableType(getFieldType())).isFalse();
    }

    private PsiType getFieldType() {
        return ReadAction.compute(() -> ((PsiField) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent()).getTypeElement().getType());
    }
}
