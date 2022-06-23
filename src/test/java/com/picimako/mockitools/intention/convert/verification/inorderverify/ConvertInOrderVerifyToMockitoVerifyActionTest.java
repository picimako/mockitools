//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import com.picimako.mockitools.MockitoolsActionTestBase;

/**
 * Integration test for {@link ConvertInOrderVerifyToMockitoVerifyAction}.
 */
public class ConvertInOrderVerifyToMockitoVerifyActionTest extends MockitoolsActionTestBase {

    public void testConvertsInOrderVerifyToMockitoVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(getProject(), myFixture.getEditor().getDocument(), getFile()),
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

    public void testConvertsInOrderVerifyToMockitoVerifyWithVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(getProject(), myFixture.getEditor().getDocument(), getFile()),
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

    public void testConvertsInOrderVerifyToMockitoVerifyWithVerificationModeWithLineWrapping() {
        checkAction(() -> new ConvertInOrderVerifyToMockitoVerifyAction(getProject(), myFixture.getEditor().getDocument(), getFile()),
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
}
