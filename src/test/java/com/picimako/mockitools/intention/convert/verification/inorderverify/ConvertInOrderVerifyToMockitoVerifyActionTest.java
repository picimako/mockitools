//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertInOrderVerifyToMockitoVerifyAction}.
 */
@RunsInEdt
class ConvertInOrderVerifyToMockitoVerifyActionTest extends MockitoolsActionTestBase {

    @Test
    void testConvertsInOrderVerifyToMockitoVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(false),
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
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        Mockito.verify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}"
            );
    }

    @Test
    void testConvertsInOrderVerifyToMockitoVerifyWithVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(false),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        order.ve<caret>rify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        Mockito.verify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToMockitoVerifyWithVerificationModeWithLineWrapping() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(false),
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        order.ver<caret>ify(mockObject, Mockito.times(2)\n" +
                "            .description(\"some description\"))\n" +
                "            .doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        Mockito.verify(mockObject, Mockito.times(2)\n" +
                "            .description(\"some description\"))\n" +
                "            .doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToMockitoVerifyWithoutVerificationModeInBulkSingle() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(true),
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
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        Mockito.verify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToMockitoVerifyWithVerificationModeInBulkSingle() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(true),
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
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject);\n" +
                "        Mockito.verify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToMockitoVerifyWithoutVerificationModeInBulkMultiple() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(true),
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
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        Mockito.verify(mockObject).doSomething();\n" +
                "        Mockito.verify(mockObject2).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsInOrderVerifyToMockitoVerifyWithVerificationModeInBulkMultiple() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(true),
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
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder order = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        Mockito.verify(mockObject, Mockito.times(2)).doSomething();\n" +
                "        Mockito.verify(mockObject2).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
