//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ArgumentMatchersCalledViaMatchersInspection}.
 */
class UseArgumentMatchersInsteadOfMatchersInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new ArgumentMatchersCalledViaMatchersInspection();
    }

    @Test
    void testUseArgumentMatchersInsteadOfMatchersWithFqn() {
        doQuickFixTest("Use matcher from ArgumentMatchers", "UseArgumentMatchersInsteadOfMatchersTest.java",
            """
                import org.mockito.Mockito;

                public class UseArgumentMatchersInsteadOfMatchersTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(org.mocki<caret>to.Matchers.anyString(), org.mockito.Matchers.eq(Integer.class));
                   }
                   private static final class MockObject {
                       public int method(String s, Class<? extends Object> clazz) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.Mockito;

                public class UseArgumentMatchersInsteadOfMatchersTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(org.mockito.ArgumentMatchers.anyString(), org.mockito.Matchers.eq(Integer.class));
                   }
                   private static final class MockObject {
                       public int method(String s, Class<? extends Object> clazz) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testUseArgumentMatchersInsteadOfMatchersWithClassNameQualifier() {
        doQuickFixTest("Use matcher from ArgumentMatchers", "UseArgumentMatchersInsteadOfMatchersTest.java",
            """
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class UseArgumentMatchersInsteadOfMatchersTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.anyString(), Matc<caret>hers.eq(Integer.class));
                   }
                   private static final class MockObject {
                       public int method(String s, Class<? extends Object> clazz) {
                           return 0;
                       }
                   }
                }""",
            """
                import org.mockito.ArgumentMatchers;
                import org.mockito.Mockito;
                import org.mockito.Matchers;

                public class UseArgumentMatchersInsteadOfMatchersTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(Matchers.anyString(), ArgumentMatchers.eq(Integer.class));
                   }
                   private static final class MockObject {
                       public int method(String s, Class<? extends Object> clazz) {
                           return 0;
                       }
                   }
                }""");
    }

    @Test
    void testNoHighlightForStaticImportedMatcherFromArgumentMatchers() {
        doJavaTest("NoHighlightTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.ArgumentMatchers.anyString;
                import static org.mockito.ArgumentMatchers.eq;

                public class NoHighlightTest {
                   public void testMethod() {
                       MockObject mock = Mockito.mock(MockObject.class);
                       Mockito.doReturn(10).when(mock).method(anyString(), eq(Integer.class));
                   }
                   private static final class MockObject {
                       public int method(String s, Class<? extends Object> clazz) {
                           return 0;
                       }
                   }
                }""");
    }
}
