//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ParameterizedIsNotNullMatcherInspection}.
 */
class ParameterizedIsNotNullMatcherInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new ParameterizedIsNotNullMatcherInspection();
    }

    @Test
    void testArgumentMatchersIsNotNullIsReplaced() {
        doQuickFixTest("Remove matcher argument", "ArgumentMatchersIsNotNullIsReplacedTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class ArgumentMatchersIsNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNot<caret>Null(String.class));
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

                public class ArgumentMatchersIsNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNotNull());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testArgumentMatchersNotNullIsReplaced() {
        doQuickFixTest("Remove matcher argument", "ArgumentMatchersNotNullIsReplacedTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class ArgumentMatchersNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.not<caret>Null(String.class));
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

                public class ArgumentMatchersNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.notNull());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testArgumentMatchersIsNullIsReplaced() {
        doQuickFixTest("Remove matcher argument", "ArgumentMatchersIsNullIsReplacedTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class ArgumentMatchersIsNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.is<caret>Null(String.class));
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

                public class ArgumentMatchersIsNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNull());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testMatchersIsNotNullIsReplaced() {
        doQuickFixTest("Replace with ArgumentMatchers.isNotNull()", "ArgumentMatchersIsNotNullIsReplacedTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class ArgumentMatchersIsNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.isNot<caret>Null(String.class));
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

                public class ArgumentMatchersIsNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNotNull());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testMatchersNotNullIsReplaced() {
        doQuickFixTest("Replace with ArgumentMatchers.notNull()", "ArgumentMatchersNotNullIsReplacedTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class ArgumentMatchersNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.not<caret>Null(String.class));
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

                public class ArgumentMatchersNotNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.notNull());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testMatchersIsNullIsReplaced() {
        doQuickFixTest("Replace with ArgumentMatchers.isNull()", "ArgumentMatchersIsNullIsReplacedTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class ArgumentMatchersIsNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.is<caret>Null(String.class));
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

                public class ArgumentMatchersIsNullIsReplacedTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNull());
                   }
                   private static final class MockObject {
                       public int method(String s) {
                           return 0;
                       }
                   }
                }""");
    }
}
