//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link UsageOfDeprecatedVerifyInspection}.
 */
class UsageOfDeprecatedVerifyInspectionMockitoTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfDeprecatedVerifyInspection();
    }

    @Test
    void testVerifyZeroInteractionsIsReplacedWithVerifyNoMoreInteractionsNoArgument() {
        doQuickFixTest("Replace with verifyNoMoreInteractions()", "ReplaceVerifyZeroInteractionsTest.java",
            """
                import org.mockito.Mockito;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        Mockito.verifyZeroInt<caret>eractions();
                    }
                }""",
            """
                import org.mockito.Mockito;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        Mockito.verifyNoMoreInteractions();
                    }
                }""");
    }

    @Test
    void testVerifyZeroInteractionsIsReplacedWithVerifyNoMoreInteractionsArguments() {
        doQuickFixTest("Replace with verifyNoMoreInteractions()", "ReplaceVerifyZeroInteractionsTest.java",
            """
                import org.mockito.Mockito;
                import java.util.List;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        Mockito.verifyZeroInt<caret>eractions(Mockito.mock(List.class), Mockito.mock(Object.class));
                    }
                }""",
            """
                import org.mockito.Mockito;
                import java.util.List;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        Mockito.verifyNoMoreInteractions(Mockito.mock(List.class), Mockito.mock(Object.class));
                    }
                }""");
    }

    @Test
    void testVerifyZeroInteractionsIsReplacedWithVerifyNoMoreInteractionsArgumentsStaticImported() {
        doQuickFixTest("Replace with verifyNoMoreInteractions()", "ReplaceVerifyZeroInteractionsTest.java",
            """
                import org.mockito.Mockito;
                import java.util.List;

                import static org.mockito.Mockito.verifyZeroInteractions;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        verifyZeroInt<caret>eractions(Mockito.mock(List.class), Mockito.mock(Object.class));
                    }
                }""",
            """
                import org.mockito.Mockito;
                import java.util.List;

                import static org.mockito.Mockito.verifyNoMoreInteractions;
                import static org.mockito.Mockito.verifyZeroInteractions;

                public class VerifyArgumentsAreSwitchedTest {
                    public void testMethod() {
                        verifyNoMoreInteractions(Mockito.mock(List.class), Mockito.mock(Object.class));
                    }
                }""");
    }
}
