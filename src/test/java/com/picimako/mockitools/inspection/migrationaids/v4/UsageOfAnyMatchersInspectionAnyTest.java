//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link UsageOfAnyMatchersInspection}.
 */
class UsageOfAnyMatchersInspectionAnyTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfAnyMatchersInspection();
    }

    @Test
    void testArgumentMatchersAnyObjectIsReplacedWithAny() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyObjectTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class UseAnyInsteadOfAnyObjectTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyObjec<caret>t());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class UseAnyInsteadOfAnyObjectTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testArgumentMatchersAnyVarargIsReplacedWithAny() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyVarargTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class UseAnyInsteadOfAnyVarargTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyVar<caret>arg());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class UseAnyInsteadOfAnyVarargTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testMatchersAnyObjectIsReplacedWithAny() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyObjectTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class UseAnyInsteadOfAnyObjectTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.anyObjec<caret>t());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.ArgumentMatchers;
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class UseAnyInsteadOfAnyObjectTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testMatchersAnyVarargIsReplacedWithAny() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyVarargTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;
                import org.mockito.ArgumentMatchers;

                public class UseAnyInsteadOfAnyVarargTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.anyVar<caret>arg());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;
                import org.mockito.ArgumentMatchers;

                public class UseAnyInsteadOfAnyVarargTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testAnyVarargIsReplacedWithAnyWithStaticImport() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyVarargStaticImportTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.ArgumentMatchers.anyVararg;

                public class UseAnyInsteadOfAnyVarargStaticImportTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(anyVar<caret>arg());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;

                import static org.mockito.ArgumentMatchers.any;
                import static org.mockito.ArgumentMatchers.anyVararg;

                public class UseAnyInsteadOfAnyVarargStaticImportTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(any());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testAnyIsNotReported() {
        doJavaTest("AnyIsNotReportedTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class AnyIsNotReportedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testAnyIsNotReportedWithStaticImport() {
        doJavaTest("AnyIsNotReportedTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.ArgumentMatchers.any;

                public class AnyIsNotReportedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(any());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }
}
