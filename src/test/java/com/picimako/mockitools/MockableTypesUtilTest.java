//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiField;
import com.intellij.testFramework.RunsInEdt;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link MockableTypesUtil}.
 */
@RunsInEdt
class MockableTypesUtilTest extends MockitoolsTestBase {

    @Test
    void testIsMockableType() {
        getFixture().configureByText("IsMockableTypeTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class IsMockableTypeTest {\n" +
                "    @Mock\n" +
                "    public List<String> <caret>mock;\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableType(getField().getTypeElement().getType())).isTrue();
    }

    @Test
    void testIsNotMockableTypePrimitive() {
        getFixture().configureByText("IsNotMockableTypePrimitiveTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class IsNotMockableTypePrimitiveTest {\n" +
                "    @Mock\n" +
                "    public int <caret>mock;\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableType(getField().getTypeElement().getType())).isFalse();
    }

    @Test
    void testIsNotMockableTypeWrapper() {
        getFixture().configureByText("IsNotMockableTypeWrapperTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class IsNotMockableTypeWrapperTest {\n" +
                "    @Mock\n" +
                "    public String <caret>mock;\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableType(getField().getTypeElement().getType())).isFalse();
    }

    private PsiField getField() {
        return (PsiField) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent();
    }
}
