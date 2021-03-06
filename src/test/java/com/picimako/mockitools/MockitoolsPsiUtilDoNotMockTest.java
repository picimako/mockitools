//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito4Latest;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

/**
 * Functional test for {@link com.picimako.mockitools.MockitoolsPsiUtil}.
 */
public class MockitoolsPsiUtilDoNotMockTest extends MockitoolsTestBase {

    @Override
    protected void loadLibs() {
        loadMockito4Latest(myFixture.getProjectDisposable(), getModule());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection/donotmockreason";
    }

    //getDoNotMockAnnotatedTypeAndReasonInHierarchy

    public void testReturnsDoNotMockedClassWithDefaultReason() {
        myFixture.configureByText("DoNotMockTest.java",
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

        Pair<PsiClass, String> doNotMock = MockitoolsPsiUtil.getDoNotMockAnnotatedTypeAndReasonInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.first.getQualifiedName()).isEqualTo("DoNotMockTest.NotMockable");
        assertThat(doNotMock.second).isEqualTo("Create a real instance instead.");
    }

    public void testReturnsDoNotMockedClassWithCustomReason() {
        myFixture.configureByText("DoNotMockTest.java",
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

        Pair<PsiClass, String> doNotMock = MockitoolsPsiUtil.getDoNotMockAnnotatedTypeAndReasonInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.first.getQualifiedName()).isEqualTo("DoNotMockTest.NotMockable");
        assertThat(doNotMock.second).isEqualTo("A custom reason");
    }

    public void testReturnsDoNotMockedClassWithEmptyReason() {
        myFixture.configureByText("DoNotMockTest.java",
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

        Pair<PsiClass, String> doNotMock = MockitoolsPsiUtil.getDoNotMockAnnotatedTypeAndReasonInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.first.getQualifiedName()).isEqualTo("DoNotMockTest.NotMockable");
        assertThat(doNotMock.second).isEmpty();
    }

    public void testReturnsCustomDoNotMockedClassWithDefaultReason() {
        myFixture.copyFileToProject("DoNotMock.java");
        myFixture.configureByText("DoNotMockTest.java",
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

        Pair<PsiClass, String> doNotMock = MockitoolsPsiUtil.getDoNotMockAnnotatedTypeAndReasonInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.first.getQualifiedName()).isEqualTo("DoNotMockTest.NotMockable");
        assertThat(doNotMock.second).isEqualTo("Default reason");
    }

    public void testReturnsCustomDoNotMockedClassWithCustomReason() {
        myFixture.copyFileToProject("DoNotMock.java");
        myFixture.configureByText("DoNotMockTest.java",
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

        Pair<PsiClass, String> doNotMock = MockitoolsPsiUtil.getDoNotMockAnnotatedTypeAndReasonInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.first.getQualifiedName()).isEqualTo("DoNotMockTest.NotMockable");
        assertThat(doNotMock.second).isEqualTo("Custom reason");
    }

    public void testReturnsCustomDoNotMockedClassWithEmptyReason() {
        myFixture.copyFileToProject("DoNotMock.java");
        myFixture.configureByText("DoNotMockTest.java",
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

        Pair<PsiClass, String> doNotMock = MockitoolsPsiUtil.getDoNotMockAnnotatedTypeAndReasonInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.first.getQualifiedName()).isEqualTo("DoNotMockTest.NotMockable");
        assertThat(doNotMock.second).isEmpty();
    }

    //isMockableTypeInAnyWay

    public void testIsNotMockableTypeInAnyWayDoNotMock() {
        myFixture.copyFileToProject("DoNotMock.java");
        myFixture.configureByText("DoNotMockTest.java",
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

        assertThat(MockitoolsPsiUtil.isMockableTypeInAnyWay(getField().getTypeElement().getType())).isFalse();
    }

    public void testIsNotMockableTypeInAnyWay() {
        myFixture.configureByText("DoNotMockTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class DoNotMockTest {\n" +
                "\n" +
                "    @Mock\n" +
                "    String <caret>notMockable;\n" +
                "}\n");

        assertThat(MockitoolsPsiUtil.isMockableTypeInAnyWay(getField().getTypeElement().getType())).isFalse();
    }

    private PsiField getField() {
        return (PsiField) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
    }
}
