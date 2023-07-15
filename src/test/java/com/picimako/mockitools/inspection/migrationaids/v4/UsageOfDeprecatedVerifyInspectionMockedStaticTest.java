//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link UsageOfDeprecatedVerifyInspection}.
 */
class UsageOfDeprecatedVerifyInspectionMockedStaticTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfDeprecatedVerifyInspection();
    }

    @Test
    void testVerifyArgumentsAreSwitchedSimpleLambda() {
        doQuickFixTest("Use non-deprecated 'verify()'", "VerifyArgumentsAreSwitchedTest.java",
            """
                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.Collections;
                import java.util.List;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {
                             collections.verif<caret>y(Mockito.times(1), () -> Collections.reverse(List.of()));
                        }
                    }
                }""",
            """
                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.Collections;
                import java.util.List;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {
                             collections.verify(() -> Collections.reverse(List.of()), Mockito.times(1));
                        }
                    }
                }""");
    }

    @Test
    void testVerifyArgumentsAreSwitchedLambdaBlock() {
        doQuickFixTest("Use non-deprecated 'verify()'", "VerifyArgumentsAreSwitchedTest.java",
            """
                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.Collections;
                import java.util.List;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {
                             collections.ver<caret>ify(Mockito.times(1), () -> {
                                Collections.reverse(List.of());
                             });
                        }
                    }
                }""",
            """
                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.Collections;
                import java.util.List;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {
                             collections.verify(() -> {
                                Collections.reverse(List.of());
                             }, Mockito.times(1));
                        }
                    }
                }""");
    }

    @Test
    void testNonDeprecatedVerifyIsNotReported() {
        doJavaTest("NoVerifyIsReportedTest.java",
            """
                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.Collections;
                import java.util.List;

                public class NoVerifyIsReportedTest {
                    public void testMethod() {
                        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {
                             collections.verify(() -> {
                                Collections.reverse(List.of());
                             }, Mockito.times(1));
                        }
                    }
                }""");
    }

    @Test
    void testNonDeprecatedVerifyIsNotReportedSimpleLambda() {
        doJavaTest("NoVerifyIsReportedTest.java",
            """
                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.Collections;
                import java.util.List;

                public class NoVerifyIsReportedTest {
                    public void testMethod() {
                        try (MockedStatic<Collections> collections = Mockito.mockStatic(Collections.class)) {
                             collections.verify(() -> Collections.reverse(List.of()), Mockito.times(1));
                        }
                    }
                }""");
    }
}
