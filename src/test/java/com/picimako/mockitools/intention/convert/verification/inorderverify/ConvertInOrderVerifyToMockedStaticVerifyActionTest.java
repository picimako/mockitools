//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertInOrderVerifyToMockedStaticVerifyAction}.
 */
@RunsInEdt
class ConvertInOrderVerifyToMockedStaticVerifyActionTest extends MockitoolsActionTestBase {

    @Test
    void testConvertsInOrderVerifyToMockedStaticVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockedStaticVerifyAction(false),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.InOrder;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder inOrder = Mockito.inOrder(List.class);\n" +
                "            inOrder.ve<caret>rify(mock, List::of);\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.InOrder;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder inOrder = Mockito.inOrder(List.class);\n" +
                "            mock.verify(List::of);\n" +
                "        }\n" +
                "    }\n" +
                "}"
        );
    }

    @Test
    void testConvertsInOrderVerifyToMockedStaticVerifyWithVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockedStaticVerifyAction(false),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.InOrder;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder inOrder = Mockito.inOrder(List.class);\n" +
                "            inOrder.ve<caret>rify(mock, List::of, Mockito.times(3));\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.InOrder;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder inOrder = Mockito.inOrder(List.class);\n" +
                "            mock.verify(List::of, Mockito.times(3));\n" +
                "        }\n" +
                "    }\n" +
                "}"
        );
    }

    @Test
    void testConvertsInOrderVerifyToMockedStaticVerifyInBulk() {
        checkAction(() -> new ConvertInOrderVerifyToMockedStaticVerifyAction(true),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.InOrder;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder inOrder = Mockito.inOrder(List.class);\n" +
                "            <selection>inOrder.verify(mock, List::of);\n" +
                "            inOrder.verify(mock, List::of, Mockito.times(3));</selection>\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.InOrder;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder inOrder = Mockito.inOrder(List.class);\n" +
                "            mock.verify(List::of);\n" +
                "            mock.verify(List::of, Mockito.times(3));\n" +
                "        }\n" +
                "    }\n" +
                "}"
        );
    }
}
