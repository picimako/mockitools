//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
 * Functional test for {@link SimplifyConsecutiveThrowCallsInspection};
 */
class SimplifyConsecutiveThrowCallsInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {

    private static final String MOCK_OBJECT_CLASS =
        """
                private static class MockObject {
                   public int didSomething() {
                       return 0;
                   }
                }
            """;

    private static Stream<Arguments> whenThenThrowCases() {
        return Stream.of(
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodException.class).thenThr<caret>ow(IOException.class);",
                "       Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodException.class, IOException.class);"
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, NoSuchMethodException.class).the<caret>nThrow(IllegalArgumentException.class);",
                "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, NoSuchMethodException.class, IllegalArgumentException.class);"
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).thenT<caret>hrow(IllegalArgumentException.class, NoSuchMethodException.class);",
                "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class);"
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException()).thenT<caret>hrow(new IllegalArgumentException());",
                "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException());"
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).the<caret>nThrow(new IllegalArgumentException());",
                "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException(), new IllegalArgumentException());"
            ),
            arguments(
                """
                           Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).thenThrow(new IllegalArgumentException())
                              .thenReturn(10)
                              .thenThrow(new IOException(), new NoSuchMethodException()).the<caret>nThrow(new IllegalArgumentException());\
                    """,
                """
                           Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).thenThrow(new IllegalArgumentException())
                              .thenReturn(10)
                              .thenThrow(new IOException(), new NoSuchMethodException(), new IllegalArgumentException());\
                    """
            ),
            arguments(
                """
                           Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).thenTh<caret>row(new IllegalArgumentException())
                              .thenReturn(10)
                              .thenThrow(new IOException(), new NoSuchMethodException()).thenThrow(new IllegalArgumentException());\
                    """,
                """
                           Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException(), new IllegalArgumentException())
                              .thenReturn(10)
                              .thenThrow(new IOException(), new NoSuchMethodException()).thenThrow(new IllegalArgumentException());\
                    """
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException()).then<caret>Throw(new IllegalArgumentException(\"message\"));",
                "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException(\"message\"));"
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodException.class).then<caret>Throw(new IllegalArgumentException(\"message\"));",
                "       Mockito.when(mockObject.doSomething()).thenThrow(new NoSuchMethodException(), new IllegalArgumentException(\"message\"));"
            ));
    }

    private static Stream<Arguments> givenWillThrowCases() {
        return Stream.of(
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).will<caret>Throw(NoSuchMethodException.class);",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class);"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class).willT<caret>hrow(IllegalArgumentException.class);",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class, IllegalArgumentException.class);"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).will<caret>Throw(IOException.class, NoSuchMethodException.class);",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class, IOException.class, NoSuchMethodException.class);"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).wil<caret>lThrow(new IllegalArgumentException(\"message\"));",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(\"message\"));"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).wil<caret>lThrow(new IllegalArgumentException(\"message\"));",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(\"message\"));"
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException("message")).will<caret>Throw(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException("message"), new NoSuchMethodException())
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IllegalArgumentException("message")).wi<caret>llThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IllegalArgumentException("message"), new NoSuchMethodException())
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """
            ));
    }

    private static Stream<Arguments> willThrowGivenCases() {
        return Stream.of(
            arguments(
                "       BDDMockito.willThrow(IOException.class).will<caret>Throw(NoSuchMethodException.class).given(mockObject).doSomething();",
                "       BDDMockito.willThrow(IOException.class, NoSuchMethodException.class).given(mockObject).doSomething();"
            ),
            arguments(
                "       BDDMockito.willThrow(IOException.class, NoSuchMethodException.class).willT<caret>hrow(IllegalArgumentException.class).given(mockObject).doSomething();",
                "       BDDMockito.willThrow(IOException.class, NoSuchMethodException.class, IllegalArgumentException.class).given(mockObject).doSomething();"
            ),
            arguments(
                "       BDDMockito.willThrow(IllegalArgumentException.class).will<caret>Throw(IOException.class, NoSuchMethodException.class).given(mockObject).doSomething();",
                "       BDDMockito.willThrow(IllegalArgumentException.class, IOException.class, NoSuchMethodException.class).given(mockObject).doSomething();"
            ),
            arguments(
                "       BDDMockito.willThrow(new IOException()).wil<caret>lThrow(new IllegalArgumentException(\"message\")).given(mockObject).doSomething();",
                "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException(\"message\")).given(mockObject).doSomething();"
            ),
            arguments(
                "       BDDMockito.willThrow(IOException.class).wil<caret>lThrow(new IllegalArgumentException(\"message\")).given(mockObject).doSomething();",
                "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException(\"message\")).given(mockObject).doSomething();"
            ));
    }

    private static Stream<Arguments> toClassesMixedCases() {
        return Stream.of(
            arguments(
                """
                           BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .given(mockObject.doSomething());\
                    """,
                """
                           BDDMockito.willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .given(mockObject.doSomething());\
                    """
            ),
            arguments(
                """
                           BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)
                               .given(mockObject.doSomething());\
                    """,
                """
                           BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class)
                               .given(mockObject.doSomething());\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).will<caret>Throw(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).will<caret>Throw(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException("message")).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).wi<caret>llThrow(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException("message")).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class);\
                    """
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).wil<caret>lThrow(IllegalArgumentException.class);",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class);"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).wil<caret>lThrow(new IllegalArgumentException());",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class);"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willT<caret>hrow(NoSuchMethodException.class);",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class);"
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).then<caret>Throw(new IllegalArgumentException());",
                "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, IllegalArgumentException.class);"
            ),
            arguments(
                "      Mockito.doThrow(IOException.class).do<caret>Throw(new IllegalArgumentException()).when(mockObject).doSomething();",
                "      Mockito.doThrow(IOException.class, IllegalArgumentException.class).when(mockObject).doSomething();"
            ));
    }

    private static Stream<Arguments> toThrowableMixedCases() {
        return Stream.of(
            arguments(
                """
                           BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .given(mockObject.doSomething());\
                    """,
                """
                           BDDMockito.willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException())
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .given(mockObject.doSomething());\
                    """
            ),
            arguments(
                """
                           BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)
                               .given(mockObject.doSomething());\
                    """,
                """
                           BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException())
                               .given(mockObject.doSomething());\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).will<caret>Throw(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException())
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException());\
                    """
            ),
            arguments(
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException("message")).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException()).wi<caret>llThrow(NoSuchMethodException.class);\
                    """,
                """
                           BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException("message")).willThrow(NoSuchMethodException.class)
                               .willReturn(10)
                               .willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException());\
                    """
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).wil<caret>lThrow(IllegalArgumentException.class);",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException());"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).wil<caret>lThrow(new IllegalArgumentException());",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException());"
            ),
            arguments(
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willT<caret>hrow(NoSuchMethodException.class);",
                "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException());"
            ),
            arguments(
                "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).then<caret>Throw(new IllegalArgumentException());",
                "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException());"
            ),
            arguments(
                "      Mockito.doThrow(IOException.class).do<caret>Throw(new IllegalArgumentException()).when(mockObject).doSomething();",
                "      Mockito.doThrow(new IOException(), new IllegalArgumentException()).when(mockObject).doSomething();"
            ));
    }

    private static Stream<Arguments> mockedStaticWhenThenThrowsCases() {
        return Stream.of(
            arguments(
                "       mock.when(List::of).thenThrow(IOException.class).then<caret>Throw(NoSuchMethodException.class);",
                "       mock.when(List::of).thenThrow(IOException.class, NoSuchMethodException.class);"
            ),
            arguments(
                "       mock.when(List::of).thenThrow(IOException.class, NoSuchMethodException.class).thenT<caret>hrow(IllegalArgumentException.class);",
                "       mock.when(List::of).thenThrow(IOException.class, NoSuchMethodException.class, IllegalArgumentException.class);"
            ),
            arguments(
                "       mock.when(List::of).thenThrow(IllegalArgumentException.class).then<caret>Throw(IOException.class, NoSuchMethodException.class);",
                "       mock.when(List::of).thenThrow(IllegalArgumentException.class, IOException.class, NoSuchMethodException.class);"
            ),
            arguments(
                "       mock.when(List::of).thenThrow(new IOException()).then<caret>Throw(new IllegalArgumentException(\"message\"));",
                "       mock.when(List::of).thenThrow(new IOException(), new IllegalArgumentException(\"message\"));"
            ),
            arguments(
                "       mock.when(List::of).thenThrow(IOException.class).then<caret>Throw(new IllegalArgumentException(\"message\"));",
                "       mock.when(List::of).thenThrow(new IOException(), new IllegalArgumentException(\"message\"));"
            ),
            arguments(
                """
                           mock.when(List::of).thenThrow(new IOException(), new IllegalArgumentException("message")).then<caret>Throw(NoSuchMethodException.class)
                               .thenReturn(List.of())
                               .thenThrow(new IOException(), new IllegalArgumentException()).thenThrow(NoSuchMethodException.class);\
                    """,
                """
                           mock.when(List::of).thenThrow(new IOException(), new IllegalArgumentException("message"), new NoSuchMethodException())
                               .thenReturn(List.of())
                               .thenThrow(new IOException(), new IllegalArgumentException()).thenThrow(NoSuchMethodException.class);\
                    """
            ),
            arguments(
                """
                           mock.when(List::of).thenThrow(new IllegalArgumentException("message")).th<caret>enThrow(NoSuchMethodException.class)
                               .thenReturn(List.of())
                               .thenThrow(new IOException(), new IllegalArgumentException()).thenThrow(NoSuchMethodException.class);\
                    """,
                """
                           mock.when(List::of).thenThrow(new IllegalArgumentException("message"), new NoSuchMethodException())
                               .thenReturn(List.of())
                               .thenThrow(new IOException(), new IllegalArgumentException()).thenThrow(NoSuchMethodException.class);\
                    """
            ));
    }

    private static Stream<Arguments> doThrowWhenCases() {
        return Stream.of(
            arguments(
                "       Mockito.doThrow(IllegalArgumentException.class).doTh<caret>row(NoSuchMethodException.class).when(mockObject).doSomething();",
                "       Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class).when(mockObject).doSomething();"
            ),
            arguments(
                "       Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class).doThr<caret>ow(IOException.class).when(mockObject).doSomething();",
                "       Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class, IOException.class).when(mockObject).doSomething();"
            ),
            arguments(
                "       Mockito.doThrow(IllegalArgumentException.class).do<caret>Throw(IOException.class, NoSuchMethodException.class).when(mockObject).doSomething();",
                "       Mockito.doThrow(IllegalArgumentException.class, IOException.class, NoSuchMethodException.class).when(mockObject).doSomething();"
            ),
            arguments(
                "       Mockito.doThrow(new IllegalArgumentException()).doTh<caret>row(new IOException()).when(mockObject).doSomething();",
                "       Mockito.doThrow(new IllegalArgumentException(), new IOException()).when(mockObject).doSomething();"
            ),
            arguments(
                "       Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doT<caret>hrow(new IOException()).when(mockObject).doSomething();",
                "       Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException()).when(mockObject).doSomething();"
            ),
            arguments(
                """
                           Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException()).do<caret>Throw(new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .when(mockObject).doSomething();\
                    """,
                """
                           Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .when(mockObject).doSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).do<caret>Throw(new IOException())
                              .when(mockObject).doSomething();\
                    """,
                """
                           Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException())
                              .when(mockObject).doSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class).do<caret>Throw(IOException.class)
                              .doReturn(10)
                              .doThrow(IllegalArgumentException.class, NoSuchMethodException.class).doThrow(IOException.class)
                              .when(mockObject).doSomething();\
                    """,
                """
                           Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class, IOException.class)
                              .doReturn(10)
                              .doThrow(IllegalArgumentException.class, NoSuchMethodException.class).doThrow(IOException.class)
                              .when(mockObject).doSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class).doThrow(IOException.class)
                              .doReturn(10)
                              .doThrow(IllegalArgumentException.class, NoSuchMethodException.class).do<caret>Throw(IOException.class)
                              .when(mockObject).doSomething();\
                    """,
                """
                           Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class).doThrow(IOException.class)
                              .doReturn(10)
                              .doThrow(IllegalArgumentException.class, NoSuchMethodException.class, IOException.class)
                              .when(mockObject).doSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doReturn(5)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doT<caret>hrow(new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .when(mockObject).doSomething();\
                    """,
                """
                           Mockito.doReturn(5)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .when(mockObject).doSomething();\
                    """
            ),
            arguments(
                """
                           Mockito.doReturn(5)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doT<caret>hrow(new IOException())
                              .when(mockObject).doSomething();\
                    """,
                """
                           Mockito.doReturn(5)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())
                              .doReturn(10)
                              .doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException())
                              .when(mockObject).doSomething();\
                    """
            ),
            arguments(
                "       Mockito.doThrow(new IllegalArgumentException()).doThr<caret>ow(new IOException(\"message\")).when(mockObject).doSomething();",
                "       Mockito.doThrow(new IllegalArgumentException(), new IOException(\"message\")).when(mockObject).doSomething();"
            ),
            arguments(
                "       Mockito.doThrow(IllegalArgumentException.class).doT<caret>hrow(new IOException(\"message\")).when(mockObject).doSomething();",
                "       Mockito.doThrow(new IllegalArgumentException(), new IOException(\"message\")).when(mockObject).doSomething();"
            ),
            arguments(
                "       Mockito.doThrow(IllegalArgumentException.class).doThrow(new IOException()).doT<caret>hrow(new IOException(\"message\")).when(mockObject).doSomething();",
                "       Mockito.doThrow(new IllegalArgumentException(), new IOException(), new IOException(\"message\")).when(mockObject).doSomething();"
            ));
    }

    @Override
    protected InspectionProfileEntry getInspection() {
        return new SimplifyConsecutiveThrowCallsInspection();
    }

    //Highlighting

    @Test
    void testSimplifyConsecutiveThrowCalls() {
        doJavaTest();
    }

    //Quick fixes

    @ParameterizedTest
    @MethodSource("whenThenThrowCases")
    void testReplacesWhenThenThrows(String before, String after) {
        doQuickFixTest("Merge thenThrow calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("doThrowWhenCases")
    void testReplacesDoThrowWhens(String before, String after) {
        doQuickFixTest("Merge doThrow calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("givenWillThrowCases")
    void testReplacesGivenWillThrows(String before, String after) {
        doQuickFixTest("Merge willThrow calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("willThrowGivenCases")
    void testReplacesWillThrowGivens(String before, String after) {
        doQuickFixTest("Merge willThrow calls", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("toClassesMixedCases")
    void testMixedToClassesCases(String before, String after) {
        doQuickFixTest("Merge calls, convert parameters to Class objects", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("toThrowableMixedCases")
    void testMixedToThrowablesCases(String before, String after) {
        doQuickFixTest("Merge calls, convert parameters to Throwables", "QuickFix.java", createClassText(before), createClassText(after));
    }

    @ParameterizedTest
    @MethodSource("mockedStaticWhenThenThrowsCases")
    void testReplacesMockedStaticWhenThenThrows(String before, String after) {
        doQuickFixTest("Merge thenThrow calls", "QuickFix.java", createMockedStaticClassText(before), createMockedStaticClassText(after));
    }

    private String createClassText(String beforeOrAfter) {
        return "import org.mockito.Mockito;\n" +
               "import org.mockito.BDDMockito;\n" +
               "import java.io.IOException;\n" +
               "\n" +
               "class QuickFix {\n" +
               "   void testMethod() {\n" +
               "       MockObject mockObject = Mockito.mock(MockObject.class);\n" +
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
               "import java.io.IOException;\n" +
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
