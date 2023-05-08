//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertThrowStubbingArgumentsIntention}.
 */
@RunsInEdt
class ConvertThrowStubbingArgumentsIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertThrowStubbingArgumentsIntention();
    }

    //Availability

    @Test
    void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    @Test
    void testNotAvailableWhenMethodNameDoesntMatch() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).willR<caret>eturn(10);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableWhenNotAllArgumentsAreTheSameKind() {
        checkIntentionIsNotAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).wi<caret>llThrow(new IOException(), IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableWhenThereIsNonDefaultConstructorNewExpressionArgument() {
        checkIntentionIsNotAvailable(
            "import java.io.IOException;\n" +
                "import java.sql.SQLException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).will<caret>Throw(new IOException(), new IllegalArgumentException(\"messa\"));\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnThenThrowClasses() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mockObject.doSomething()).then<caret>Throw(IOException.class, IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnThenThrowThrowables() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mockObject.doSomething()).then<caret>Throw(new IOException(), new IllegalArgumentException());\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnDoThrowClasses() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doThr<caret>ow(IOException.class, IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnDoThrowThrowables() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(new IOException(), new IllegalArgumentException()).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnGivenWillThrowClasses() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).willT<caret>hrow(IOException.class, IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnGivenWillThrowThrowables() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).willThr<caret>ow(new IOException(), new IllegalArgumentException());\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableWillThrowGivenClasses() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IOException.class, IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableWillThrowGivenThrowables() {
        checkIntentionIsAvailable(
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(new IOException(), new IllegalArgumentException()).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Conversion

    @Test
    void testConvertsThrowablesToClasses() {
        checkIntentionRun(
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mockObject.doSomething()).the<caret>nThrow(new IOException(), new IllegalArgumentException());\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsClassesToThrowables() {
        checkIntentionRun(
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mockObject.doSomething()).thenT<caret>hrow(IOException.class, IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException());\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
