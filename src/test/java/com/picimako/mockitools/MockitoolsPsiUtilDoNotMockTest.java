//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.intellij.openapi.application.ReadAction.computeBlocking;
import static com.picimako.mockitools.MockableTypesUtil.getDoNotMockTypeInHierarchy;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.testFramework.TestDataPath;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link com.picimako.mockitools.MockitoolsPsiUtil}.
 */
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

        assertHasReason(getDoNotMockTypeInHierarchy(getFieldType()), "Create a real instance instead.");
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

        assertHasReason(getDoNotMockTypeInHierarchy(getFieldType()), "A custom reason");
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

        assertHasNoReason(getDoNotMockTypeInHierarchy(getFieldType()));
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

        assertHasReason(getDoNotMockTypeInHierarchy(getFieldType()), "Default reason");
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

        assertHasReason(getDoNotMockTypeInHierarchy(getFieldType()), "Custom reason");
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

        assertHasNoReason(getDoNotMockTypeInHierarchy(getFieldType()));
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

        assertThat(MockableTypesUtil.isMockableTypeInAnyWay(getFieldType())).isFalse();
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

        assertThat(MockableTypesUtil.isMockableTypeInAnyWay(getFieldType())).isFalse();
    }

    private PsiType getFieldType() {
        return computeBlocking(() -> ((PsiField) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent()).getTypeElement().getType());
    }

    private static void assertHasReason(MockableTypesUtil.DoNotMockType doNotMock, String expected) {
        assertThat(doNotMock)
            .isNotNull()
            .extracting(MockableTypesUtil.DoNotMockType::hasReason)
            .isEqualTo(true);
        assertThat(doNotMock.getReason()).isEqualTo(expected);
    }

    private static void assertHasNoReason(MockableTypesUtil.DoNotMockType doNotMock) {
        assertThat(doNotMock)
            .isNotNull()
            .extracting(MockableTypesUtil.DoNotMockType::hasReason)
            .isEqualTo(false);
    }
}
