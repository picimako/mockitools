//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import com.intellij.codeInsight.intention.IntentionAction;

import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;

/**
 * Functional test for {@link ConvertMockitoVerifyToBDDMockitoThenIntention}.
 */
public class ConvertMockitoVerifyToBDDMockitoThenIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockitoVerifyToBDDMockitoThenIntention();
    }

    //Availability

    public void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    public void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    public void testNotAvailableOnNonMockitoVerifyMethod() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        checkIntentionIsNotAvailable("Available.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenMockitoVerifyHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableOnMockitoVerifyWithNoArgument() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.ve<caret>rify();\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoVerifyWithoutVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoVerifyWithVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Conversion

    public void testConvertsMockitoVerifyToBDDMockitoThenWithoutVerificationMode() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.then(mockObject).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationMode() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeWithLineWrapping() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject, Mockito.times(2)\n" +
                "            .description(\"some description\"))\n" +
                "            .doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.then(mockObject).should(Mockito.times(2)\n" +
                "            .description(\"some description\"))\n" +
                "            .doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
