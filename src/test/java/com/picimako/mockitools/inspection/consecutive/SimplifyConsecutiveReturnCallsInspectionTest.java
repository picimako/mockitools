//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Functional test for {@link SimplifyConsecutiveReturnCallsInspection}.
 */
class SimplifyConsecutiveReturnCallsInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {

    private static final String MOCK_OBJECT_CLASS =
        "    private static class MockObject {\n" +
            "       public int didSomething() {\n" +
            "           return 0;\n" +
            "       }\n" +
            "    }\n";

    private static String[][] whenThenReturnCases() {
        return new String[][]{
            {
                "       Mockito.when(mockObject.didSomething()).thenReturn(1).thenR<caret>eturn(2);",
                "       Mockito.when(mockObject.didSomething()).thenReturn(1, 2);"
            },
            {
                "       Mockito.when(mockObject.didSomething()).thenReturn(1)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(2)\n" +
                    "           .then<caret>Return(3);",
                "       Mockito.when(mockObject.didSomething()).thenReturn(1)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(2, 3);"
            },
            {
                "       Mockito.when(mockObject.didSomething()).thenReturn(1)\n" +
                    "           .thenR<caret>eturn(2)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(3);",
                "       Mockito.when(mockObject.didSomething()).thenReturn(1, 2)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(3);"
            },
            {
                "       Mockito.when(mockObject.didSomething()).thenReturn(1)\n" +
                    "           .thenR<caret>eturn(2)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(3)\n" +
                    "           .thenReturn(4);",
                "       Mockito.when(mockObject.didSomething()).thenReturn(1, 2)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(3)\n" +
                    "           .thenReturn(4);"
            },
            {
                "       Mockito.when(mockObject.didSomething()).thenReturn(1)\n" +
                    "           .thenReturn(2)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(3)\n" +
                    "           .thenRet<caret>urn(4);",
                "       Mockito.when(mockObject.didSomething()).thenReturn(1)\n" +
                    "           .thenReturn(2)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(3, 4);"
            },
            {
                "       Mockito.when(mockObject.didSomething()).thenReturn(1, 2, 3)\n" +
                    "           .thenReturn(4)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(5)\n" +
                    "           .thenR<caret>eturn(6, 7);",
                "       Mockito.when(mockObject.didSomething()).thenReturn(1, 2, 3)\n" +
                    "           .thenReturn(4)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(5, 6, 7);"
            },
        };
    }

    private static String[][] doReturnWhenCases() {
        return new String[][]{
            {
                "       Mockito.doReturn(1).doRe<caret>turn(2).when(mockObject).didSomething();",
                "       Mockito.doReturn(1, 2).when(mockObject).didSomething();"
            },
            {
                "       Mockito.doReturn(1)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(2)\n" +
                    "           .doRet<caret>urn(3)\n" +
                    "           .when(mockObject).didSomething();",
                "       Mockito.doReturn(1)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(2, 3)\n" +
                    "           .when(mockObject).didSomething();"
            },
            {
                "       Mockito.doReturn(1)\n" +
                    "           .doR<caret>eturn(2)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(3)\n" +
                    "           .when(mockObject).didSomething();",
                "       Mockito.doReturn(1, 2)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(3)\n" +
                    "           .when(mockObject).didSomething();"
            },
            {
                "       Mockito.doReturn(1)\n" +
                    "           .doRet<caret>urn(2)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(3)\n" +
                    "           .doReturn(4)\n" +
                    "           .when(mockObject).didSomething();",
                "       Mockito.doReturn(1, 2)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(3)\n" +
                    "           .doReturn(4)\n" +
                    "           .when(mockObject).didSomething();"
            },
            {
                "       Mockito.doReturn(1)\n" +
                    "           .doReturn(2)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(3)\n" +
                    "           .doRe<caret>turn(4)\n" +
                    "           .when(mockObject).didSomething();",
                "       Mockito.doReturn(1)\n" +
                    "           .doReturn(2)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(3, 4)\n" +
                    "           .when(mockObject).didSomething();"
            },
            {
                "       Mockito.doReturn(1, 2, 3)\n" +
                    "           .doReturn(4)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(5)\n" +
                    "           .doRe<caret>turn(6, 7)\n" +
                    "           .when(mockObject).didSomething();",
                "       Mockito.doReturn(1, 2, 3)\n" +
                    "           .doReturn(4)\n" +
                    "           .doCallRealMethod()\n" +
                    "           .doReturn(5, 6, 7)\n" +
                    "           .when(mockObject).didSomething();"
            }
        };
    }

    private static String[][] givenWillReturnCases() {
        return new String[][]{
            {
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1).willRe<caret>turn(2);",
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1, 2);"
            },
            {
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(2)\n" +
                    "           .willR<caret>eturn(3);",
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(2, 3);"
            },
            {
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1)\n" +
                    "           .willRet<caret>urn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3);",
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1, 2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3);"
            },
            {
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1)\n" +
                    "           .willRe<caret>turn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3)\n" +
                    "           .willReturn(4);",
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1, 2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3)\n" +
                    "           .willReturn(4);"
            },
            {
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1)\n" +
                    "           .willReturn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3)\n" +
                    "           .willRe<caret>turn(4);",
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1)\n" +
                    "           .willReturn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3, 4);"
            },
            {
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1, 2, 3)\n" +
                    "           .willRe<caret>turn(4)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(5)\n" +
                    "           .willReturn(6, 7);",
                "       BDDMockito.given(mockObject.didSomething()).willReturn(1, 2, 3, 4)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(5)\n" +
                    "           .willReturn(6, 7);"
            }
        };
    }

    private static String[][] willReturnGivenCases() {
        return new String[][]{
            {
                "       BDDMockito.willReturn(1).willRe<caret>turn(2).given(mockObject).didSomething();",
                "       BDDMockito.willReturn(1, 2).given(mockObject).didSomething();"
            },
            {
                "       BDDMockito.willReturn(1)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(2)\n" +
                    "           .willR<caret>eturn(3).given(mockObject).didSomething();",
                "       BDDMockito.willReturn(1)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(2, 3).given(mockObject).didSomething();"
            },
            {
                "       BDDMockito.willReturn(1)\n" +
                    "           .willRet<caret>urn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3).given(mockObject).didSomething();",
                "       BDDMockito.willReturn(1, 2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3).given(mockObject).didSomething();"
            },
            {
                "       BDDMockito.willReturn(1)\n" +
                    "           .willR<caret>eturn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3)\n" +
                    "           .willReturn(4).given(mockObject).didSomething();",
                "       BDDMockito.willReturn(1, 2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3)\n" +
                    "           .willReturn(4).given(mockObject).didSomething();"
            },
            {
                "       BDDMockito.willReturn(1)\n" +
                    "           .willReturn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3)\n" +
                    "           .willRe<caret>turn(4).given(mockObject).didSomething();",
                "       BDDMockito.willReturn(1)\n" +
                    "           .willReturn(2)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(3, 4).given(mockObject).didSomething();"
            },
            {
                "       BDDMockito.willReturn(1, 2, 3)\n" +
                    "           .willRe<caret>turn(4)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(5)\n" +
                    "           .willReturn(6, 7).given(mockObject).didSomething();",
                "       BDDMockito.willReturn(1, 2, 3, 4)\n" +
                    "           .willCallRealMethod()\n" +
                    "           .willReturn(5)\n" +
                    "           .willReturn(6, 7).given(mockObject).didSomething();"
            }
        };
    }

    private static String[][] mockedStaticWhenThenReturnCases() {
        return new String[][]{
            {
                "       mock.when(List::of).thenReturn(List.of()).then<caret>Return(Collections.emptyList());",
                "       mock.when(List::of).thenReturn(List.of(), Collections.emptyList());"
            },
            {
                "       mock.when(List::of).thenReturn(List.of())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.emptyList())\n" +
                    "           .thenR<caret>eturn(Collections.EMPTY_LIST);",
                "       mock.when(List::of).thenReturn(List.of())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.emptyList(), Collections.EMPTY_LIST);"
            },
            {
                "       mock.when(List::of).thenReturn(List.of())\n" +
                    "           .thenRet<caret>urn(Collections.emptyList())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.EMPTY_LIST);",
                "       mock.when(List::of).thenReturn(List.of(), Collections.emptyList())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.EMPTY_LIST);"
            },
            {
                "       mock.when(List::of).thenReturn(List.of())\n" +
                    "           .thenR<caret>eturn(Collections.emptyList())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.EMPTY_LIST)\n" +
                    "           .thenReturn(null);",
                "       mock.when(List::of).thenReturn(List.of(), Collections.emptyList())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.EMPTY_LIST)\n" +
                    "           .thenReturn(null);"
            },
            {
                "       mock.when(List::of).thenReturn(List.of())\n" +
                    "           .thenReturn(Collections.emptyList())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.EMPTY_LIST)\n" +
                    "           .thenRe<caret>turn(null);",
                "       mock.when(List::of).thenReturn(List.of())\n" +
                    "           .thenReturn(Collections.emptyList())\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(Collections.EMPTY_LIST, null);"
            },
            {
                "       mock.when(List::of).thenReturn(List.of(), Collections.emptyList(), Collections.EMPTY_LIST)\n" +
                    "           .thenRe<caret>turn(null)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(List.of())\n" +
                    "           .thenReturn(Collections.emptyList(), Collections.EMPTY_LIST);",
                "       mock.when(List::of).thenReturn(List.of(), Collections.emptyList(), Collections.EMPTY_LIST, null)\n" +
                    "           .thenCallRealMethod()\n" +
                    "           .thenReturn(List.of())\n" +
                    "           .thenReturn(Collections.emptyList(), Collections.EMPTY_LIST);"
            }
        };
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
