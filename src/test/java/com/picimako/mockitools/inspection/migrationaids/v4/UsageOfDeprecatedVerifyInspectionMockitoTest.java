//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;

/**
 * Functional test for {@link UsageOfDeprecatedVerifyInspection}.
 */
public class UsageOfDeprecatedVerifyInspectionMockitoTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfDeprecatedVerifyInspection();
    }

    public void testVerifyZeroInteractionsIsReplacedWithVerifyNoMoreInteractionsNoArgument() {
        doQuickFixTest("Replace with verifyNoMoreInteractions()", "ReplaceVerifyZeroInteractionsTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verifyZeroInt<caret>eractions();\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verifyNoMoreInteractions();\n" +
                "    }\n" +
                "}");
    }

    public void testVerifyZeroInteractionsIsReplacedWithVerifyNoMoreInteractionsArguments() {
        doQuickFixTest("Replace with verifyNoMoreInteractions()", "ReplaceVerifyZeroInteractionsTest.java",
            "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verifyZeroInt<caret>eractions(Mockito.mock(List.class), Mockito.mock(Object.class));\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verifyNoMoreInteractions(Mockito.mock(List.class), Mockito.mock(Object.class));\n" +
                "    }\n" +
                "}");
    }

    public void testVerifyZeroInteractionsIsReplacedWithVerifyNoMoreInteractionsArgumentsStaticImported() {
        doQuickFixTest("Replace with verifyNoMoreInteractions()", "ReplaceVerifyZeroInteractionsTest.java",
            "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "import static org.mockito.Mockito.verifyZeroInteractions;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        verifyZeroInt<caret>eractions(Mockito.mock(List.class), Mockito.mock(Object.class));\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "import static org.mockito.Mockito.verifyNoMoreInteractions;\n" +
                "import static org.mockito.Mockito.verifyZeroInteractions;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        verifyNoMoreInteractions(Mockito.mock(List.class), Mockito.mock(Object.class));\n" +
                "    }\n" +
                "}");
    }
}
