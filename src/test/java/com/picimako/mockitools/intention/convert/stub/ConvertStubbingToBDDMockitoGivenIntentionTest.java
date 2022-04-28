//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import com.intellij.codeInsight.intention.IntentionAction;

import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;

/**
 * Functional test for {@link ConvertStubbingToBDDMockitoGivenIntention}.
 */
public class ConvertStubbingToBDDMockitoGivenIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertStubbingToBDDMockitoGivenIntention();
    }

    //Availability

    public void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    public void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    public void testNotAvailableForConversionToItself() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.gi<caret>ven(mockObject.doSomething()).willReturn(10);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
    
    public void testNotAvailableForMockitoWhenWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForMockitoDoXWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.doTh<caret>row(IllegalArgumentException.class);\n" +
                "    }\n" +
                "}");
    }
    
    public void testNotAvailableForMockitoDoXWithDoNothing() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doTh<caret>row(IllegalArgumentException.class).doNothing().when(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForMockitoDoXWithoutCallAfterWhen() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doR<caret>eturn(10).when(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForMockitoDoXWithoutCallToWhen() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.doR<caret>eturn(10);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForBDDMockitoWillXWithWillNothing() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).willNothing().given(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForBDDMockitoDoXWithoutCallAfterGiven() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willR<caret>eturn(10).given(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForBDDMockitoDoXWithoutCallToGiven() {
        checkIntentionIsNotAvailable("NotAvailable.java",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.willR<caret>eturn(10);\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoWhen() {
        checkIntentionIsAvailable("Available.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10).thenThrow(IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoWhenWhenConventionEnforcingIsDisabled() {
        addDisabledEnforceConventionInspection();
        checkIntentionIsAvailable("Available.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoDoX() {
        checkIntentionIsAvailable("Available.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doR<caret>eturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoDoXWhenConventionEnforcingIsDisabled() {
        addDisabledEnforceConventionInspection();
        checkIntentionIsAvailable("Available.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doR<caret>eturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForBDDMockitoWillX() {
        checkIntentionIsAvailable("Available.java",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.wi<caret>llReturn(10).willThrow(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Conversion

    public void testConvertsFromMockitoWhen() {
        checkIntentionRun("ConversionTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10).thenThrow(IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).willReturn(10).willThrow(IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsFromMockitoDoX() {
        checkIntentionRun("ConversionTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doR<caret>eturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).willReturn(10).willThrow(IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsFromBDDMockitoWillX() {
        checkIntentionRun("ConversionTest.java",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willRet<caret>urn(10).willThrow(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.given(mockObject.doSomething()).willReturn(10).willThrow(IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
