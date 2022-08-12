//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiField;

public class MockableTypesUtilTest extends MockitoolsTestBase {

    public void testIsMockableType() {
        myFixture.configureByText("IsMockableTypeTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class IsMockableTypeTest {\n" +
                "    @Mock\n" +
                "    public List<String> <caret>mock;\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableType(getField().getTypeElement().getType())).isTrue();
    }

    public void testIsNotMockableTypePrimitive() {
        myFixture.configureByText("IsNotMockableTypePrimitiveTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class IsNotMockableTypePrimitiveTest {\n" +
                "    @Mock\n" +
                "    public int <caret>mock;\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableType(getField().getTypeElement().getType())).isFalse();
    }

    public void testIsNotMockableTypeWrapper() {
        myFixture.configureByText("IsNotMockableTypeWrapperTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class IsNotMockableTypeWrapperTest {\n" +
                "    @Mock\n" +
                "    public String <caret>mock;\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableType(getField().getTypeElement().getType())).isFalse();
    }

    private PsiField getField() {
        return (PsiField) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
    }
}
