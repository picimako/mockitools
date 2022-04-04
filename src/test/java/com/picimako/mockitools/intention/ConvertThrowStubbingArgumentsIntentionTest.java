/*
 * Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;

/**
 * Functional test for {@link ConvertThrowStubbingArgumentsIntention}.
 */
public class ConvertThrowStubbingArgumentsIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertThrowStubbingArgumentsIntention();
    }

    //Availability

    public void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    public void testNotAvailableWhenMethodNameDoesntMatch() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.given(mockObject.doSomething()).willR<caret>eturn(10);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenNotAllArgumentsAreTheSameKind() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.given(mockObject.doSomething()).wi<caret>llThrow(new IOException(), IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenThereIsNonDefaultConstructorNewExpressionArgument() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import java.io.IOException;\n" +
                "import java.sql.SQLException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.given(mockObject.doSomething()).will<caret>Throw(new IOException(), new IllegalArgumentException(\"messa\"));\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnThenThrowClasses() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        Mockito.when(mockObject.doSomething()).then<caret>Throw(IOException.class, IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnThenThrowThrowables() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        Mockito.when(mockObject.doSomething()).then<caret>Throw(new IOException(), new IllegalArgumentException());\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnDoThrowClasses() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        Mockito.doThr<caret>ow(IOException.class, IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnDoThrowThrowables() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        Mockito.do<caret>Throw(new IOException(), new IllegalArgumentException()).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnGivenWillThrowClasses() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.given(mockObject.doSomething()).willT<caret>hrow(IOException.class, IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnGivenWillThrowThrowables() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.given(mockObject.doSomething()).willThr<caret>ow(new IOException(), new IllegalArgumentException());\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableWillThrowGivenClasses() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.willTh<caret>row(IOException.class, IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableWillThrowGivenThrowables() {
        checkIntentionIsAvailable("Available.java",
            "import java.io.IOException;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
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
    
    public void testConvertsThrowablesToClasses() {
        checkIntentionRun("ConvertArguments.java",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "\n" +
                "    void testMethod(){\n" +
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
                "class Available {\n" +
                "\n" +
                "    void testMethod(){\n" +
                "        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsClassesToThrowables() {
        checkIntentionRun("ConvertArguments.java",
            "import java.io.IOException;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
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
                "class Available {\n" +
                "    void testMethod(){\n" +
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
