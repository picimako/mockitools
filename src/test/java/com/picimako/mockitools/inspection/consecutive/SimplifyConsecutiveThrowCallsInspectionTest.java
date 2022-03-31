//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.consecutive;

import java.util.Map;

import com.intellij.codeInspection.InspectionProfileEntry;

import com.picimako.mockitools.inspection.MockitoolsV4InspectionTestBase;
import com.picimako.mockitools.inspection.consecutive.SimplifyConsecutiveThrowCallsInspection;

/**
 * Functional test for {@link SimplifyConsecutiveThrowCallsInspection};
 */
public class SimplifyConsecutiveThrowCallsInspectionTest extends MockitoolsV4InspectionTestBase {

    private static final String MOCK_OBJECT_CLASS =
        "    private static class MockObject {\n" +
            "       public int didSomething() {\n" +
            "           return 0;\n" +
            "       }\n" +
            "    }\n";

    private static final Map<String, String> WHEN_THEN_THROW_CASES = Map.of(
        "       Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodException.class).thenThr<caret>ow(IOException.class);",
        "       Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodException.class, IOException.class);",
        "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, NoSuchMethodException.class).the<caret>nThrow(IllegalArgumentException.class);",
        "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, NoSuchMethodException.class, IllegalArgumentException.class);",
        "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).thenT<caret>hrow(IllegalArgumentException.class, NoSuchMethodException.class);",
        "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class);",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException()).thenT<caret>hrow(new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).the<caret>nThrow(new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException(), new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).thenThrow(new IllegalArgumentException())\n" +
            "          .thenReturn(10)\n" +
            "          .thenThrow(new IOException(), new NoSuchMethodException()).the<caret>nThrow(new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException(), new IllegalArgumentException()).thenReturn(10).thenThrow(new IOException(), new NoSuchMethodException(), new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException()).then<caret>Throw(new IllegalArgumentException(\"message\"));",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException(\"message\"));",
        "       Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodException.class).then<caret>Throw(new IllegalArgumentException(\"message\"));",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new NoSuchMethodException(), new IllegalArgumentException(\"message\"));"
    );

    private static final Map<String, String> DO_THROW_WHEN_CASES = Map.of(
        "       Mockito.doThrow(IllegalArgumentException.class).doTh<caret>row(NoSuchMethodException.class).when(mockObject).doSomething();",
        "       Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class).when(mockObject).doSomething();",
        "       Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class).doThr<caret>ow(IOException.class).when(mockObject).doSomething();",
        "       Mockito.doThrow(IllegalArgumentException.class, NoSuchMethodException.class, IOException.class).when(mockObject).doSomething();",
        "       Mockito.doThrow(IllegalArgumentException.class).do<caret>Throw(IOException.class, NoSuchMethodException.class).when(mockObject).doSomething();",
        "       Mockito.doThrow(IllegalArgumentException.class, IOException.class, NoSuchMethodException.class).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException()).doTh<caret>row(new IOException()).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new IOException()).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doT<caret>hrow(new IOException()).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException()).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException()).do<caret>Throw(new IOException())\n" +
            "          .doReturn(10)\n" +
            "          .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())\n" +
            "          .when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException()).doReturn(10).doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException()).when(mockObject).doSomething();",
        "       Mockito.doReturn(5)\n" +
            "          .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doThrow(new IOException())\n" +
            "          .doReturn(10)\n" +
            "          .doThrow(new IllegalArgumentException(), new NoSuchMethodException()).doT<caret>hrow(new IOException())\n" +
            "          .when(mockObject).doSomething();",
        "       Mockito.doReturn(5).doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException()).doReturn(10).doThrow(new IllegalArgumentException(), new NoSuchMethodException(), new IOException()).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException()).doThr<caret>ow(new IOException(\"message\")).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new IOException(\"message\")).when(mockObject).doSomething();",
        "       Mockito.doThrow(IllegalArgumentException.class).doT<caret>hrow(new IOException(\"message\")).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new IOException(\"message\")).when(mockObject).doSomething();",
        "       Mockito.doThrow(IllegalArgumentException.class).doT<caret>hrow(new IOException()).doT<caret>hrow(new IOException(\"message\")).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IllegalArgumentException(), new IOException(), new IOException(\"message\")).when(mockObject).doSomething();"
    );

    private static final Map<String, String> GIVEN_WILL_THROW_CASES = Map.of(
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).will<caret>Throw(NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class).willT<caret>hrow(IllegalArgumentException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class, IllegalArgumentException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).will<caret>Throw(IOException.class, NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class, IOException.class, NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).wil<caret>lThrow(new IllegalArgumentException(\"message\"));",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(\"message\"));",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).wil<caret>lThrow(new IllegalArgumentException(\"message\"));",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(\"message\"));"
    );

    private static final Map<String, String> TO_CLASSES_MIXED_CASES = Map.of(
        "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)\n" +
            "           .willReturn(10)\n" +
            "           .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)\n" +
            "           .given(mockObject.doSomething());",
        "       BDDMockito.willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class).willReturn(10).willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class).given(mockObject.doSomething());",
        "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)\n" +
            "           .willReturn(10)\n" +
            "           .willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)\n" +
            "           .given(mockObject.doSomething());",
        "       BDDMockito.willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class).willReturn(10).willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class).given(mockObject.doSomething());",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).will<caret>Throw(NoSuchMethodException.class)\n" +
            "           .willReturn(10)\n" +
            "           .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class).willReturn(10).willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).wil<caret>lThrow(IllegalArgumentException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).wil<caret>lThrow(new IllegalArgumentException());",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willT<caret>hrow(NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, IllegalArgumentException.class, NoSuchMethodException.class);",
        "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).then<caret>Throw(new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, IllegalArgumentException.class);",
        "       Mockito.doThrow(IOException.class).do<caret>Throw(new IllegalArgumentException()).when(mockObject).doSomething();",
        "       Mockito.doThrow(IOException.class, IllegalArgumentException.class).when(mockObject).doSomething();"
    );

    private static final Map<String, String> TO_THROWABLES_MIXED_CASES = Map.of(
        "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)\n" +
            "           .willReturn(10)\n" +
            "           .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)\n" +
            "           .given(mockObject.doSomething());",
        "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException()).willReturn(10).willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException()).given(mockObject.doSomething());",
        "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class)\n" +
            "           .willReturn(10)\n" +
            "           .willThrow(new IOException(), new IllegalArgumentException()).wil<caret>lThrow(NoSuchMethodException.class)\n" +
            "           .given(mockObject.doSomething());",
        "       BDDMockito.willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException()).willReturn(10).willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException()).given(mockObject.doSomething());",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).will<caret>Throw(NoSuchMethodException.class)\n" +
            "           .willReturn(10)\n" +
            "           .willThrow(new IOException(), new IllegalArgumentException()).willThrow(NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException()).willReturn(10).willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException());",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).wil<caret>lThrow(IllegalArgumentException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException());",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).wil<caret>lThrow(new IllegalArgumentException());",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException());",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).willT<caret>hrow(NoSuchMethodException.class);",
        "       BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException(), new NoSuchMethodException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).then<caret>Throw(new IllegalArgumentException());",
        "       Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException());",
        "       Mockito.doThrow(IOException.class).do<caret>Throw(new IllegalArgumentException()).when(mockObject).doSomething();",
        "       Mockito.doThrow(new IOException(), new IllegalArgumentException()).when(mockObject).doSomething();"
    );

    @Override
    protected InspectionProfileEntry getInspection() {
        return new SimplifyConsecutiveThrowCallsInspection();
    }

    //Highlighting

    public void testSimplifyConsecutiveThrowCalls() {
        doJavaTest();
    }

    //Quick fixes

    public void testReplacesWhenThenThrows() {
        WHEN_THEN_THROW_CASES.forEach((before, after) ->
            doQuickFixTest("Merge thenThrow calls", "QuickFix.java",
                createClassText(before), createClassText(after)));
    }

    public void testReplacesDoThrowWhens() {
        DO_THROW_WHEN_CASES.forEach((before, after) ->
            doQuickFixTest("Merge doThrow calls", "QuickFix.java",
                createClassText(before), createClassText(after)));
    }

    public void testReplacesGivenWillThrows() {
        GIVEN_WILL_THROW_CASES.forEach((before, after) ->
            doQuickFixTest("Merge willThrow calls", "QuickFix.java",
                createClassText(before), createClassText(after)));
    }

    public void testMixedToClassesCases() {
        TO_CLASSES_MIXED_CASES.forEach((before, after) ->
            doQuickFixTest("Merge calls, convert parameters to Class objects", "QuickFix.java",
                createClassText(before), createClassText(after)));
    }

    public void testMixedToThrowablesCases() {
        TO_THROWABLES_MIXED_CASES.forEach((before, after) ->
            doQuickFixTest("Merge calls, convert parameters to Throwables", "QuickFix.java",
                createClassText(before), createClassText(after)));
    }

    private String createClassText(String beforeOrAfter) {
        return "import org.mockito.Mockito;\n" +
            "import org.mockito.BDDMockito;\n" +
            "import java.io.IOException;\n" +
            "\n" +
            "class QuickFix {\n" +
            "   void testMethod() {\n" +
            beforeOrAfter + "\n" +
            "   }\n" +
            "\n" +
            MOCK_OBJECT_CLASS +
            "}";
    }
}
