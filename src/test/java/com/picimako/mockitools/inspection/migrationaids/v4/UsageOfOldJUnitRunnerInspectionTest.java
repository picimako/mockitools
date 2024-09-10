//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.ThirdPartyLibrary;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link UsageOfOldJUnitRunnerInspection}.
 */
class UsageOfOldJUnitRunnerInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    public UsageOfOldJUnitRunnerInspectionTest() {
        super(ThirdPartyLibrary.JUNIT_4, ThirdPartyLibrary.MOCKITO_V3);
    }

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfOldJUnitRunnerInspection();
    }

    @Test
    void testReplaceConsoleSpammingMockitoJUnitRunner() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            """
                import org.junit.runner.RunWith;
                import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;

                @RunWith(ConsoleSpammingMockitoJU<caret>nitRunner.class)
                public class ReplaceOldRunnerTest {
                }""",
            """
                import org.junit.runner.RunWith;
                import org.mockito.junit.MockitoJUnitRunner;
                import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;

                @RunWith(MockitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""");
    }

    @Test
    void testReplaceVerboseMockitoJUnitRunner() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            """
                import org.junit.runner.RunWith;
                import org.mockito.runners.VerboseMockitoJUnitRunner;

                @RunWith(VerboseMoc<caret>kitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""",
            """
                import org.junit.runner.RunWith;
                import org.mockito.junit.MockitoJUnitRunner;
                import org.mockito.runners.VerboseMockitoJUnitRunner;

                @RunWith(MockitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""");
    }

    @Test
    void testReplaceMockitoJUnitRunner() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            """
                import org.junit.runner.RunWith;
                import org.mockito.runners.MockitoJUnitRunner;

                @RunWith(MockitoJUni<caret>tRunner.class)
                public class ReplaceOldRunnerTest {
                }""",
            """
                import org.junit.runner.RunWith;
                import org.mockito.junit.MockitoJUnitRunner;

                @RunWith(MockitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""");
    }

    @Test
    void testReplaceConsoleSpammingMockitoJUnitRunnerWithFqn() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            """
                import org.junit.runner.RunWith;
                @RunWith(org.mockito.runners.ConsoleSpammingMockitoJU<caret>nitRunner.class)
                public class ReplaceOldRunnerTest {
                }""",
            """
                import org.junit.runner.RunWith;
                import org.mockito.junit.MockitoJUnitRunner;

                @RunWith(MockitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""");
    }

    @Test
    void testReplaceVerboseMockitoJUnitRunnerWithFqn() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            """
                import org.junit.runner.RunWith;
                @RunWith(org.mockito.runners.VerboseMoc<caret>kitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""",
            """
                import org.junit.runner.RunWith;
                import org.mockito.junit.MockitoJUnitRunner;

                @RunWith(MockitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""");
    }

    @Test
    void testReplaceMockitoJUnitRunnerWithFqn() {
        doQuickFixTest("Replace with org.mockito.junit.MockitoJUnitRunner", "ReplaceOldRunnerTest.java",
            """
                import org.junit.runner.RunWith;
                @RunWith(org.mockito.runners.MockitoJUni<caret>tRunner.class)
                public class ReplaceOldRunnerTest {
                }""",
            """
                import org.junit.runner.RunWith;
                import org.mockito.junit.MockitoJUnitRunner;

                @RunWith(MockitoJUnitRunner.class)
                public class ReplaceOldRunnerTest {
                }""");
    }
}
