//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Functional test for {@link SimplifyConsecutiveReturnCallsInspection}.
 */
class SimplifyConsecutiveReturnCallsInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {

    private static final String MOCK_OBJECT_CLASS =
        """
                private static class MockObject {
                   public int didSomething() {
                       return 0;
                   }
                }
            """;

    private static Stream<Arguments> whenThenReturnCases() {
        return Stream.of(
            arguments(
                "       Mockito.when(mockObject.didSomething()).thenReturn(1).thenR<caret>eturn(2);",
                "       Mockito.when(mockObject.didSomething()).thenReturn(1, 2);"),
            arguments(
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1)
                               .thenCallRealMethod()
                               .thenReturn(2)
                               .then<caret>Return(3);\
                    """,
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1)
                               .thenCallRealMethod()
                               .thenReturn(2, 3);\
                    """
            ),
            arguments(
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1)
                               .thenR<caret>eturn(2)
                               .thenCallRealMethod()
                               .thenReturn(3);\
                    """,
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1, 2)
                               .thenCallRealMethod()
                               .thenReturn(3);\
                    """
            ),
            arguments(
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1)
                               .thenR<caret>eturn(2)
                               .thenCallRealMethod()
                               .thenReturn(3)
                               .thenReturn(4);\
                    """,
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1, 2)
                               .thenCallRealMethod()
                               .thenReturn(3)
                               .thenReturn(4);\
                    """
            ),
            arguments(
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1)
                               .thenReturn(2)
                               .thenCallRealMethod()
                               .thenReturn(3)
                               .thenRet<caret>urn(4);\
                    """,
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1)
                               .thenReturn(2)
                               .thenCallRealMethod()
                               .thenReturn(3, 4);\
                    """
            ),
            arguments(
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1, 2, 3)
                               .thenReturn(4)
                               .thenCallRealMethod()
                               .thenReturn(5)
                               .thenR<caret>eturn(6, 7);\
                    """,
                """
                           Mockito.when(mockObject.didSomething()).thenReturn(1, 2, 3)
                               .thenReturn(4)
                               .thenCallRealMethod()
                               .thenReturn(5, 6, 7);\
                    """
            ));
    }

    private static Stream<Arguments> doReturnWhenCases() {
        return Stream.of(
            arguments(
                "       Mockito.doReturn(1).doRe<caret>turn(2).when(mockObject).didSomething();",
                "       Mockito.doReturn(1, 2).when(mockObject).didSomething();"
            ),
            arguments(
                """
                           Mockito.doReturn(1)
                               .doCallRealMethod()
                               .doReturn(2)
                               .doRet<caret>urn(3)
                               .when(mockObject).didSomething();\
                    """,
                """
                           Mockito.doReturn(1)
                               .doCallRealMethod()
                               .doReturn(2, 3)
                               .when(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doReturn(1)
                               .doR<caret>eturn(2)
                               .doCallRealMethod()
                               .doReturn(3)
                               .when(mockObject).didSomething();\
                    """,
                """
                           Mockito.doReturn(1, 2)
                               .doCallRealMethod()
                               .doReturn(3)
                               .when(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doReturn(1)
                               .doRet<caret>urn(2)
                               .doCallRealMethod()
                               .doReturn(3)
                               .doReturn(4)
                               .when(mockObject).didSomething();\
                    """,
                """
                           Mockito.doReturn(1, 2)
                               .doCallRealMethod()
                               .doReturn(3)
                               .doReturn(4)
                               .when(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doReturn(1)
                               .doReturn(2)
                               .doCallRealMethod()
                               .doReturn(3)
                               .doRe<caret>turn(4)
                               .when(mockObject).didSomething();\
                    """,
                """
                           Mockito.doReturn(1)
                               .doReturn(2)
                               .doCallRealMethod()
                               .doReturn(3, 4)
                               .when(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doReturn(1, 2, 3)
                               .doReturn(4)
                               .doCallRealMethod()
                               .doReturn(5)
                               .doRe<caret>turn(6, 7)
                               .when(mockObject).didSomething();\
                    """,
                """
                           Mockito.doReturn(1, 2, 3)
                               .doReturn(4)
                               .doCallRealMethod()
                               .doReturn(5, 6, 7)
                               .when(mockObject).didSomething();\
                    """
            ));
    }

    private static Stream<Arguments> givenWillReturnCases() {
        return Stream.of(
            arguments(
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1).willRe<caret>turn(2);",
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1, 2);"
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1)
                               .willCallRealMethod()
                               .willReturn(2)
                               .willR<caret>eturn(3);\
                    """,
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1)
                               .willCallRealMethod()
                               .willReturn(2, 3);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1)
                               .willRet<caret>urn(2)
                               .willCallRealMethod()
                               .willReturn(3);\
                    """,
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1, 2)
                               .willCallRealMethod()
                               .willReturn(3);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1)
                               .willRe<caret>turn(2)
                               .willCallRealMethod()
                               .willReturn(3)
                               .willReturn(4);\
                    """,
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1, 2)
                               .willCallRealMethod()
                               .willReturn(3)
                               .willReturn(4);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1)
                               .willReturn(2)
                               .willCallRealMethod()
                               .willReturn(3)
                               .willRe<caret>turn(4);\
                    """,
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1)
                               .willReturn(2)
                               .willCallRealMethod()
                               .willReturn(3, 4);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1, 2, 3)
                               .willRe<caret>turn(4)
                               .willCallRealMethod()
                               .willReturn(5)
                               .willReturn(6, 7);\
                    """,
                """
                           BDDMockito.given(mockObject.didSomething()).willReturn(1, 2, 3, 4)
                               .willCallRealMethod()
                               .willReturn(5)
                               .willReturn(6, 7);\
                    """
            ));
    }

    private static Stream<Arguments> willReturnGivenCases() {
        return Stream.of(
            arguments(
                "       BDDMockito.willReturn(1).willRe<caret>turn(2).given(mockObject).didSomething();",
                "       BDDMockito.willReturn(1, 2).given(mockObject).didSomething();"
            ),
            arguments(
                """
                           BDDMockito.willReturn(1)
                               .willCallRealMethod()
                               .willReturn(2)
                               .willR<caret>eturn(3).given(mockObject).didSomething();\
                    """,
                """
                           BDDMockito.willReturn(1)
                               .willCallRealMethod()
                               .willReturn(2, 3).given(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           BDDMockito.willReturn(1)
                               .willRet<caret>urn(2)
                               .willCallRealMethod()
                               .willReturn(3).given(mockObject).didSomething();\
                    """,
                """
                           BDDMockito.willReturn(1, 2)
                               .willCallRealMethod()
                               .willReturn(3).given(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           BDDMockito.willReturn(1)
                               .willR<caret>eturn(2)
                               .willCallRealMethod()
                               .willReturn(3)
                               .willReturn(4).given(mockObject).didSomething();\
                    """,
                """
                           BDDMockito.willReturn(1, 2)
                               .willCallRealMethod()
                               .willReturn(3)
                               .willReturn(4).given(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           BDDMockito.willReturn(1)
                               .willReturn(2)
                               .willCallRealMethod()
                               .willReturn(3)
                               .willRe<caret>turn(4).given(mockObject).didSomething();\
                    """,
                """
                           BDDMockito.willReturn(1)
                               .willReturn(2)
                               .willCallRealMethod()
                               .willReturn(3, 4).given(mockObject).didSomething();\
                    """
            ),
            arguments(
                """
                           BDDMockito.willReturn(1, 2, 3)
                               .willRe<caret>turn(4)
                               .willCallRealMethod()
                               .willReturn(5)
                               .willReturn(6, 7).given(mockObject).didSomething();\
                    """,
                """
                           BDDMockito.willReturn(1, 2, 3, 4)
                               .willCallRealMethod()
                               .willReturn(5)
                               .willReturn(6, 7).given(mockObject).didSomething();\
                    """
            ));
    }

    private static Stream<Arguments> mockedStaticWhenThenReturnCases() {
        return Stream.of(
            arguments(
                "       mock.when(List::of).thenReturn(List.of()).then<caret>Return(Collections.emptyList());",
                "       mock.when(List::of).thenReturn(List.of(), Collections.emptyList());"
            ),
            arguments(
                """
                           mock.when(List::of).thenReturn(List.of())
                               .thenCallRealMethod()
                               .thenReturn(Collections.emptyList())
                               .thenR<caret>eturn(Collections.EMPTY_LIST);\
                    """,
                """
                           mock.when(List::of).thenReturn(List.of())
                               .thenCallRealMethod()
                               .thenReturn(Collections.emptyList(), Collections.EMPTY_LIST);\
                    """
            ),
            arguments(
                """
                           mock.when(List::of).thenReturn(List.of())
                               .thenRet<caret>urn(Collections.emptyList())
                               .thenCallRealMethod()
                               .thenReturn(Collections.EMPTY_LIST);\
                    """,
                """
                           mock.when(List::of).thenReturn(List.of(), Collections.emptyList())
                               .thenCallRealMethod()
                               .thenReturn(Collections.EMPTY_LIST);\
                    """
            ),
            arguments(
                """
                           mock.when(List::of).thenReturn(List.of())
                               .thenR<caret>eturn(Collections.emptyList())
                               .thenCallRealMethod()
                               .thenReturn(Collections.EMPTY_LIST)
                               .thenReturn(null);\
                    """,
                """
                           mock.when(List::of).thenReturn(List.of(), Collections.emptyList())
                               .thenCallRealMethod()
                               .thenReturn(Collections.EMPTY_LIST)
                               .thenReturn(null);\
                    """
            ),
            arguments(
                """
                           mock.when(List::of).thenReturn(List.of())
                               .thenReturn(Collections.emptyList())
                               .thenCallRealMethod()
                               .thenReturn(Collections.EMPTY_LIST)
                               .thenRe<caret>turn(null);\
                    """,
                """
                           mock.when(List::of).thenReturn(List.of())
                               .thenReturn(Collections.emptyList())
                               .thenCallRealMethod()
                               .thenReturn(Collections.EMPTY_LIST, null);\
                    """
            ),
            arguments(
                """
                           mock.when(List::of).thenReturn(List.of(), Collections.emptyList(), Collections.EMPTY_LIST)
                               .thenRe<caret>turn(null)
                               .thenCallRealMethod()
                               .thenReturn(List.of())
                               .thenReturn(Collections.emptyList(), Collections.EMPTY_LIST);\
                    """,
                """
                           mock.when(List::of).thenReturn(List.of(), Collections.emptyList(), Collections.EMPTY_LIST, null)
                               .thenCallRealMethod()
                               .thenReturn(List.of())
                               .thenReturn(Collections.emptyList(), Collections.EMPTY_LIST);\
                    """
            ));
    }

    @Override
    protected InspectionProfileEntry getInspection() {
        return new SimplifyConsecutiveReturnCallsInspection();
    }

//Highlighting

    @Test
    void testSimplifyConsecutiveStubbingCalls() {
        doJavaTest();
    }

//Quick fixes

    @ParameterizedTest
    @MethodSource("whenThenReturnCases")
    void testReplacesWhenThenReturns(String before, String after) {
        doQuickFixTest("Merge thenReturn calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("doReturnWhenCases")
    void testReplacesDoReturnWhens(String before, String after) {
        doQuickFixTest("Merge doReturn calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("givenWillReturnCases")
    void testReplacesGivenWillReturns(String before, String after) {
        doQuickFixTest("Merge willReturn calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("willReturnGivenCases")
    void testReplacesWillReturnGivens(String before, String after) {
        doQuickFixTest("Merge willReturn calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("mockedStaticWhenThenReturnCases")
    void testReplacesMockedStaticWhenThenReturns(String before, String after) {
        doQuickFixTest("Merge thenReturn calls", "QuickFix.java", createMockedStaticClassText(before), createMockedStaticClassText(after));
    }

    private String createClassText(String beforeOrAfter) {
        return "import org.mockito.Mockito;\n" +
               "import org.mockito.BDDMockito;\n" +
               "\n" +
               "class QuickFix {\n" +
               "   void testMethod() {\n" +
               beforeOrAfter + "\n" +
               "   }\n" +
               "\n" +
               MOCK_OBJECT_CLASS +
               "}";
    }

    private String createMockedStaticClassText(String beforeOrAfter) {
        return "import org.mockito.Mockito;\n" +
               "import org.mockito.MockedStatic;\n" +
               "import java.util.Collections;\n" +
               "import java.util.List;\n" +
               "\n" +
               "class QuickFix {\n" +
               "   void testMethod() {\n" +
               "       try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
               beforeOrAfter + "\n" +
               "       }\n" +
               "   }\n" +
               "}";
    }
}
