//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction}.
 */
@RunsInEdt
class ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderActionTest extends MockitoolsActionTestBase {

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithoutVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(false),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        order.ver<caret>ify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(false),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        order.ver<caret>ify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithoutVerificationModeInBulkSingle() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        <selection>order.verify(mockObject).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithVerificationModeInBulkSingle() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        <selection>order.verify(mockObject, Mockito.times(2)).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithoutVerificationModeInBulkMultiple() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        <selection>order.verify(mockObject).doSomething();\n" +
                "        order.verify(mockObject2).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        BDDMockito.then(mockObject).should().doSomething();\n" +
                "        BDDMockito.then(mockObject2).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithVerificationModeInBulkMultiple() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        <selection>order.verify(mockObject, Mockito.times(2)).doSomething();\n" +
                "        order.verify(mockObject2).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "        BDDMockito.then(mockObject2).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
