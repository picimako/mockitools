//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import com.picimako.mockitools.MockitoolsActionTestBase;

/**
 * Integration test for {@link ConvertMockedStaticVerifyToInOrderVerifyAction}.
 */
public class ConvertMockedStaticVerifyToInOrderVerifyActionTest extends MockitoolsActionTestBase {

    //Caret based conversion

    public void testConvertsMockedStaticVerifyToInOrderVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(false),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            mock.veri<caret>fy(List::of);\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder order = Mockito.inOrder(List.class);\n" +
                "            order.verify(mock, List::of);\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockedStaticVerifyToInOrderVerifyWithVerificationMode() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(false),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            mock.veri<caret>fy(List::of, Mockito.times(2));\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder order = Mockito.inOrder(List.class);\n" +
                "            order.verify(mock, List::of, Mockito.times(2));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Selection based conversion

    public void testConvertsMockedStaticVerifyToInOrderVerifyWithoutVerificationModeInSelection() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(true),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::of, Mockito.times(2));\n" +
                "            mock.verify(List::copyOf, Mockito.times(3));</selection>\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder order = Mockito.inOrder(List.class);\n" +
                "            order.verify(mock, List::of, Mockito.times(2));\n" +
                "            order.verify(mock, List::copyOf, Mockito.times(3));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockedStaticVerifyToInOrderVerifyWithVerificationModeInSelection() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(true),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::of);\n" +
                "            mock.verify(List::copyOf);</selection>\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            InOrder order = Mockito.inOrder(List.class);\n" +
                "            order.verify(mock, List::of);\n" +
                "            order.verify(mock, List::copyOf);\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockedStaticVerifyToInOrderVerifyWithVerificationModeInSelectionMultipleMockObjects() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(true),
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mockList = Mockito.mockStatic(List.class); MockedStatic<Set> mockSet = Mockito.mockStatic(Set.class)) {\n" +
                "            <selection>mockList.verify(List::of);\n" +
                "            mockSet.verify(Set::of, Mockito.times(2));</selection>\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "class ConversionTest {\n" +
                "    void testMethod() {\n" +
                "        try (MockedStatic<List> mockList = Mockito.mockStatic(List.class); MockedStatic<Set> mockSet = Mockito.mockStatic(Set.class)) {\n" +
                "            InOrder order = Mockito.inOrder(List.class, Set.class);\n" +
                "            order.verify(mockList, List::of);\n" +
                "            order.verify(mockSet, Set::of, Mockito.times(2));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
