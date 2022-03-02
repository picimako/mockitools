//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoolsTestBase.JUNIT_4;
import static com.picimako.mockitools.MockitoolsTestBase.MOCKITO_3;

import com.intellij.codeInspection.InspectionProfileEntry;

import com.picimako.mockitools.inspection.MockitoolsV3InspectionTestBase;

/**
 * Functional test for {@link UsageOfOldJUnitRunnerInspection}.
 */
public class UsageOfOldJUnitRunnerInspectionTest extends MockitoolsV3InspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfOldJUnitRunnerInspection();
    }

    @Override
    protected String[] libsToLoad() {
        return new String[]{MOCKITO_3, JUNIT_4};
    }

    public void testReplaceConsoleSpammingMockitoJUnitRunner() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(ConsoleSpammingMockitoJU<caret>nitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.junit.MockitoJUnitRunner;\n" +
                "import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(MockitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}");
    }

    public void testReplaceVerboseMockitoJUnitRunner() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.runners.VerboseMockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(VerboseMoc<caret>kitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.junit.MockitoJUnitRunner;\n" +
                "import org.mockito.runners.VerboseMockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(MockitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}");
    }

    public void testReplaceMockitoJUnitRunner() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.runners.MockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(MockitoJUni<caret>tRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.junit.MockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(MockitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}");
    }

    public void testReplaceConsoleSpammingMockitoJUnitRunnerWithFqn() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            "import org.junit.runner.RunWith;\n" +
                "@RunWith(org.mockito.runners.ConsoleSpammingMockitoJU<caret>nitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.junit.MockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(MockitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}");
    }

    public void testReplaceVerboseMockitoJUnitRunnerWithFqn() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            "import org.junit.runner.RunWith;\n" +
                "@RunWith(org.mockito.runners.VerboseMoc<caret>kitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.junit.MockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(MockitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}");
    }

    public void testReplaceMockitoJUnitRunnerWithFqn() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            "import org.junit.runner.RunWith;\n" +
                "@RunWith(org.mockito.runners.MockitoJUni<caret>tRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}",
            "import org.junit.runner.RunWith;\n" +
                "import org.mockito.junit.MockitoJUnitRunner;\n" +
                "\n" +
                "@RunWith(MockitoJUnitRunner.class)\n" +
                "public class ReplaceOldRunnerTest {\n" +
                "}");
    }
}
