//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.captor;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link CaptorFieldOfTypeArgumentCaptorInspection}.
 */
class CaptorFieldOfTypeArgumentCaptorInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new CaptorFieldOfTypeArgumentCaptorInspection();
    }

    @Test
    void testCaptorFieldOfType() {
        doJavaTest();
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorArrayTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorArrayTest.java",
            "import org.mockito.Captor;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorArrayTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public String[] <caret>captor;\n" +
                "}\n",
            "import org.mockito.ArgumentCaptor;\n" +
                "import org.mockito.Captor;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorArrayTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public ArgumentCaptor<String[]> captor;\n" +
                "}\n");
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorPrimitiveTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorPrimitiveTest.java",
            "import org.mockito.Captor;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorPrimitiveTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public char <caret>captor;\n" +
                "}\n",
            "import org.mockito.ArgumentCaptor;\n" +
                "import org.mockito.Captor;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorPrimitiveTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public ArgumentCaptor<Character> captor;\n" +
                "}\n");
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorGenericsTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorGenericsTest.java",
            "import org.mockito.Captor;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorGenericsTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public List<List<String>> <caret>captor;\n" +
                "}\n",
            "import org.mockito.ArgumentCaptor;\n" +
                "import org.mockito.Captor;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorGenericsTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public ArgumentCaptor<List<List<String>>> captor;\n" +
                "}\n");
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorWildcardGenericsTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorWildcardGenericsTest.java",
            "import org.mockito.Captor;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorWildcardGenericsTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public Map<List<String>, List<?>> <caret>captor;\n" +
                "}\n",
            "import org.mockito.ArgumentCaptor;\n" +
                "import org.mockito.Captor;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class CaptorFieldConvertToArgumentCaptorWildcardGenericsTest {\n" +
                "\n" +
                "    @Captor\n" +
                "    public ArgumentCaptor<Map<List<String>, List<?>>> captor;\n" +
                "}\n");
    }
}
