//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;

/**
 * Functional test for {@link UsageOfDeprecatedVerifyInspection}.
 */
public class UsageOfDeprecatedVerifyInspectionMockedStaticTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfDeprecatedVerifyInspection();
    }

    public void testVerifyArgumentsAreSwitchedSimpleLambda() {
        doQuickFixTest("Use non-deprecated 'verify()'", "VerifyArgumentsAreSwitchedTest.java",
            "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.Collections;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {\n" +
                "             collections.verif<caret>y(Mockito.times(1), () -> Collections.reverse(List.of()));\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.Collections;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {\n" +
                "             collections.verify(() -> Collections.reverse(List.of()), Mockito.times(1));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testVerifyArgumentsAreSwitchedLambdaBlock() {
        doQuickFixTest("Use non-deprecated 'verify()'", "VerifyArgumentsAreSwitchedTest.java",
            "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.Collections;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {\n" +
                "             collections.ver<caret>ify(Mockito.times(1), () -> {\n" +
                "                Collections.reverse(List.of());\n" +
                "             });\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.Collections;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class VerifyArgumentsAreSwitchedTest {\n" +
                "    public void testMethod() {\n" +
                "        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {\n" +
                "             collections.verify(() -> {\n" +
                "                Collections.reverse(List.of());\n" +
                "             }, Mockito.times(1));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNonDeprecatedVerifyIsNotReported() {
        doJavaTest("NoVerifyIsReportedTest.java",
            "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.Collections;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class NoVerifyIsReportedTest {\n" +
                "    public void testMethod() {\n" +
                "        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {\n" +
                "             collections.verify(() -> {\n" +
                "                Collections.reverse(List.of());\n" +
                "             }, Mockito.times(1));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNonDeprecatedVerifyIsNotReportedSimpleLambda() {
        doJavaTest("NoVerifyIsReportedTest.java",
            "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.Collections;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class NoVerifyIsReportedTest {\n" +
                "    public void testMethod() {\n" +
                "        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {\n" +
                "             collections.verify(() -> Collections.reverse(List.of()), Mockito.times(1));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
