//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.MockableTypesUtil.getDoNotMockTypeInHierarchy;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiField;
import com.intellij.testFramework.RunsInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link com.picimako.mockitools.MockitoolsPsiUtil}.
 */
@RunsInEdt
class MockitoolsPsiUtilDoNotMockTest extends MockitoolsTestBase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection/donotmockreason";
    }

    //getDoNotMockAnnotatedTypeAndReasonInHierarchy

    @Test
    void testReturnsDoNotMockedClassWithDefaultReason() {
        getFixture().configureByText("DoNotMockTest.java",
            "import org.mockito.DoNotMock;\n" +
                "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    NotMockable <caret>notMockable;\n" +
                "\n" +
                "    @DoNotMock\n" +
                "    private static class NotMockable {}\n" +
                "}\n");

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason).isEqualTo("Create a real instance instead.");
    }

    @Test
    void testReturnsDoNotMockedClassWithCustomReason() {
        getFixture().configureByText("DoNotMockTest.java",
            "import org.mockito.DoNotMock;\n" +
                "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    NotMockable <caret>notMockable;\n" +
                "\n" +
                "    @DoNotMock(reason = \"A custom reason\")\n" +
                "    private static class NotMockable {}\n" +
                "}\n");

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason).isEqualTo("A custom reason");
    }

    @Test
    void testReturnsDoNotMockedClassWithEmptyReason() {
        getFixture().configureByText("DoNotMockTest.java",
            "import org.mockito.DoNotMock;\n" +
                "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    NotMockable <caret>notMockable;\n" +
                "\n" +
                "    @DoNotMock(reason = \"\")\n" +
                "    private static class NotMockable {}\n" +
                "}\n");

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason).isEmpty();
    }

    @Test
    void testReturnsCustomDoNotMockedClassWithDefaultReason() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            "import pm.org.mockito.DoNotMock;\n" +
                "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    NotMockable <caret>notMockable;\n" +
                "\n" +
                "    @DoNotMock\n" +
                "    private static class NotMockable {}\n" +
                "}\n");

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason).isEqualTo("Default reason");
    }

    @Test
    void testReturnsCustomDoNotMockedClassWithCustomReason() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            "import pm.org.mockito.DoNotMock;\n" +
                "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    NotMockable <caret>notMockable;\n" +
                "\n" +
                "    @DoNotMock(reason = \"Custom reason\")\n" +
                "    private static class NotMockable {}\n" +
                "}\n");

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason).isEqualTo("Custom reason");
    }

    @Test
    void testReturnsCustomDoNotMockedClassWithEmptyReason() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            "import pm.org.mockito.DoNotMock;\n" +
                "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    NotMockable <caret>notMockable;\n" +
                "\n" +
                "    @DoNotMock(reason = \"\")\n" +
                "    private static class NotMockable {}\n" +
                "}\n");

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason).isEmpty();
    }

    //isMockableTypeInAnyWay

    @Test
    void testIsNotMockableTypeInAnyWayDoNotMock() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            "import pm.org.mockito.DoNotMock;\n" +
                "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    NotMockable <caret>notMockable;\n" +
                "\n" +
                "    @DoNotMock(reason = \"\")\n" +
                "    private static class NotMockable {}\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableTypeInAnyWay(getField().getTypeElement().getType())).isFalse();
    }

    @Test
    void testIsNotMockableTypeInAnyWay() {
        getFixture().configureByText("DoNotMockTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    String <caret>notMockable;\n" +
                "}\n");

        assertThat(MockableTypesUtil.isMockableTypeInAnyWay(getField().getTypeElement().getType())).isFalse();
    }

    private PsiField getField() {
        return (PsiField) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent();
    }
}
