//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction}.
 */
@RunsInEdt
class ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderActionTest extends MockitoolsActionTestBase {

    //Caret based conversion

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithoutVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(false),
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

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(false),
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

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeWithLineWrapping() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(false),
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

    //Selection based conversion

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithoutVerificationModeInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(true),
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.verify(mockObject).doSomething();\n" +
                "        Mockito.verify(mockObject).doSomething();</selection>\n" +
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
                "        BDDMockito.then(mockObject).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(true),
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.verify(mockObject, Mockito.times(2)).doSomething();\n" +
                "        Mockito.verify(mockObject, Mockito.times(2)).doSomething();</selection>\n" +
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
                "        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeWithLineWrappingInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(true),
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.verify(mockObject, Mockito.times(2)\n" +
                "            .description(\"some description\"))\n" +
                "            .doSomething();\n" +
                "        Mockito.verify(mockObject, Mockito.times(2)\n" +
                "            .description(\"some description\"))\n" +
                "            .doSomething();</selection>\n" +
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
