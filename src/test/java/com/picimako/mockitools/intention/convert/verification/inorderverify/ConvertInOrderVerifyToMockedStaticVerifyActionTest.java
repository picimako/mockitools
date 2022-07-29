//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import com.picimako.mockitools.MockitoolsActionTestBase;

/**
 * Integration test for {@link ConvertInOrderVerifyToMockedStaticVerifyAction}.
 */
public class ConvertInOrderVerifyToMockedStaticVerifyActionTest extends MockitoolsActionTestBase {

    public void testConvertsInOrderVerifyToMockedStaticVerifyWithoutVerificationMode() {
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

    public void testConvertsInOrderVerifyToMockedStaticVerifyWithVerificationMode() {
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

    public void testConvertsInOrderVerifyToMockedStaticVerifyInBulk() {
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
