//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.MockableTypesUtil.getDoNotMockTypeInHierarchy;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiField;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.junit5.RunInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link com.picimako.mockitools.MockitoolsPsiUtil}.
 */
@RunInEdt
@TestDataPath("$CONTENT_ROOT/testData/inspection/donotmockreason")
class MockitoolsPsiUtilDoNotMockTest extends MockitoolsTestBase {

    //getDoNotMockAnnotatedTypeAndReasonInHierarchy

    @Test
    void testReturnsDoNotMockedClassWithDefaultReason() {
        getFixture().configureByText("DoNotMockTest.java",
            """
                import org.mockito.DoNotMock;
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    NotMockable <caret>notMockable;

                    @DoNotMock
                    private static class NotMockable {}
                }
                """);

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason()).isEqualTo("Create a real instance instead.");
    }

    @Test
    void testReturnsDoNotMockedClassWithCustomReason() {
        getFixture().configureByText("DoNotMockTest.java",
            """
                import org.mockito.DoNotMock;
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    NotMockable <caret>notMockable;

                    @DoNotMock(reason = "A custom reason")
                    private static class NotMockable {}
                }
                """);

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason()).isEqualTo("A custom reason");
    }

    @Test
    void testReturnsDoNotMockedClassWithEmptyReason() {
        getFixture().configureByText("DoNotMockTest.java",
            """
                import org.mockito.DoNotMock;
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    NotMockable <caret>notMockable;

                    @DoNotMock(reason = "")
                    private static class NotMockable {}
                }
                """);

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason()).isEmpty();
    }

    @Test
    void testReturnsCustomDoNotMockedClassWithDefaultReason() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            """
                import pm.org.mockito.DoNotMock;
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    NotMockable <caret>notMockable;

                    @DoNotMock
                    private static class NotMockable {}
                }
                """);

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason()).isEqualTo("Default reason");
    }

    @Test
    void testReturnsCustomDoNotMockedClassWithCustomReason() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            """
                import pm.org.mockito.DoNotMock;
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    NotMockable <caret>notMockable;

                    @DoNotMock(reason = "Custom reason")
                    private static class NotMockable {}
                }
                """);

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason()).isEqualTo("Custom reason");
    }

    @Test
    void testReturnsCustomDoNotMockedClassWithEmptyReason() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            """
                import pm.org.mockito.DoNotMock;
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    NotMockable <caret>notMockable;

                    @DoNotMock(reason = "")
                    private static class NotMockable {}
                }
                """);

        var doNotMock = getDoNotMockTypeInHierarchy(getField().getTypeElement().getType());
        assertThat(doNotMock.get().reason()).isEmpty();
    }

    //isMockableTypeInAnyWay

    @Test
    void testIsNotMockableTypeInAnyWayDoNotMock() {
        getFixture().copyFileToProject("DoNotMock.java");
        getFixture().configureByText("DoNotMockTest.java",
            """
                import pm.org.mockito.DoNotMock;
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    NotMockable <caret>notMockable;

                    @DoNotMock(reason = "")
                    private static class NotMockable {}
                }
                """);

        assertThat(MockableTypesUtil.isMockableTypeInAnyWay(getField().getTypeElement().getType())).isFalse();
    }

    @Test
    void testIsNotMockableTypeInAnyWay() {
        getFixture().configureByText("DoNotMockTest.java",
            """
                import org.mockito.Mock;

                public class DoNotMockTest {

                    @Mock
                    String <caret>notMockable;
                }
                """);

        assertThat(MockableTypesUtil.isMockableTypeInAnyWay(getField().getTypeElement().getType())).isFalse();
    }

    private PsiField getField() {
        return (PsiField) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent();
    }
}
