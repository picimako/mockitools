//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link UsageOfAnyMatchersInspection}.
 */
class UsageOfAnyMatchersInspectionAnyXTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfAnyMatchersInspection();
    }

    @Test
    void testArgumentMatchersAnyIterableOfReplacedWithAnyIterable() {
        doQuickFixTest("Replace with ArgumentMatchers.anyIterable()", "UseAnyIterableInsteadOfAnyIterableOfTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class UseAnyIterableInsteadOfAnyIterableOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyIterable<caret>Of(String.class));
                   }
                   private static final class MockObject {
                       public int method(Iterable<String> s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;

                public class UseAnyIterableInsteadOfAnyIterableOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyIterable());
                   }
                   private static final class MockObject {
                       public int method(Iterable<String> s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testArgumentMatchersAnyMapOfReplacedWithAnyMap() {
        doQuickFixTest("Replace with ArgumentMatchers.anyMap()", "UseAnyMapInsteadOfAnyMapOfTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;
                import java.util.Map;

                public class UseAnyMapInsteadOfAnyMapOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyMap<caret>Of(String.class, String.class));
                   }
                   private static final class MockObject {
                       public int method(Map<String, String> s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.ArgumentMatchers;
                import java.util.Map;

                public class UseAnyMapInsteadOfAnyMapOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyMap());
                   }
                   private static final class MockObject {
                       public int method(Map<String, String> s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testArgumentMatchersAnyIterableOfReplacedWithAnyIterableForStaticImport() {
        doQuickFixTest("Replace with ArgumentMatchers.anyIterable()", "UseAnyIterableInsteadOfAnyIterableOfTest.java",
            """
                import org.mockito.Mockito;

                import static org.mockito.ArgumentMatchers.anyIterableOf;

                public class UseAnyIterableInsteadOfAnyIterableOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(anyIterable<caret>Of(String.class));
                   }
                   private static final class MockObject {
                       public int method(Iterable<String> s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;

                import static org.mockito.ArgumentMatchers.anyIterable;
                import static org.mockito.ArgumentMatchers.anyIterableOf;

                public class UseAnyIterableInsteadOfAnyIterableOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(anyIterable());
                   }
                   private static final class MockObject {
                       public int method(Iterable<String> s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testArgumentMatchersAnyMapOfReplacedWithAnyMapForStaticImport() {
        doQuickFixTest("Replace with ArgumentMatchers.anyMap()", "UseAnyMapInsteadOfAnyMapOfTest.java",
            """
                import org.mockito.Mockito;
                import java.util.Map;

                import static org.mockito.ArgumentMatchers.anyMapOf;

                public class UseAnyMapInsteadOfAnyMapOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(anyMap<caret>Of(String.class, String.class));
                   }
                   private static final class MockObject {
                       public int method(Map<String, String> s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;
                import java.util.Map;

                import static org.mockito.ArgumentMatchers.anyMap;
                import static org.mockito.ArgumentMatchers.anyMapOf;

                public class UseAnyMapInsteadOfAnyMapOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(anyMap());
                   }
                   private static final class MockObject {
                       public int method(Map<String, String> s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testMatchersAnyIterableOfReplacedWithAnyIterable() {
        doQuickFixTest("Replace with ArgumentMatchers.anyIterable()", "UseAnyIterableInsteadOfAnyIterableOfTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class UseAnyIterableInsteadOfAnyIterableOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.anyIterable<caret>Of(String.class));
                   }
                   private static final class MockObject {
                       public int method(Iterable<String> s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.ArgumentMatchers;
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class UseAnyIterableInsteadOfAnyIterableOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyIterable());
                   }
                   private static final class MockObject {
                       public int method(Iterable<String> s) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testMatchersAnyMapOfReplacedWithAnyMap() {
        doQuickFixTest("Replace with ArgumentMatchers.anyMap()", "UseAnyMapInsteadOfAnyMapOfTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;
                import java.util.Map;

                public class UseAnyMapInsteadOfAnyMapOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.anyMap<caret>Of(String.class, String.class));
                   }
                   private static final class MockObject {
                       public int method(Map<String, String> s) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.ArgumentMatchers;
                import org.mockito.Mockito;
                import org.mockito.Matchers;
                import java.util.Map;

                public class UseAnyMapInsteadOfAnyMapOfTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyMap());
                   }
                   private static final class MockObject {
                       public int method(Map<String, String> s) {
                           return 0;
                       }
                   }
                }""");
    }
}
