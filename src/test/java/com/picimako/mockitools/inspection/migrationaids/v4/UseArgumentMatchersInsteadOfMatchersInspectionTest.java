//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;

import com.picimako.mockitools.inspection.MockitoolsV3InspectionTestBase;

/**
 * Functional test for {@link ArgumentMatchersCalledViaMatchersInspection}.
 */
public class UseArgumentMatchersInsteadOfMatchersInspectionTest extends MockitoolsV3InspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new ArgumentMatchersCalledViaMatchersInspection();
    }

    public void testUseArgumentMatchersInsteadOfMatchersWithFqn() {
        doQuickFixTest("Use matcher from ArgumentMatchers", "UseArgumentMatchersInsteadOfMatchersTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class UseArgumentMatchersInsteadOfMatchersTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(org.mocki<caret>to.Matchers.anyString(), org.mockito.Matchers.eq(Integer.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s, Class<? extends Object> clazz) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class UseArgumentMatchersInsteadOfMatchersTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(org.mockito.ArgumentMatchers.anyString(), org.mockito.Matchers.eq(Integer.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s, Class<? extends Object> clazz) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    public void testUseArgumentMatchersInsteadOfMatchersWithClassNameQualifier() {
        doQuickFixTest("Use matcher from ArgumentMatchers", "UseArgumentMatchersInsteadOfMatchersTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class UseArgumentMatchersInsteadOfMatchersTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.anyString(), Matc<caret>hers.eq(Integer.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s, Class<? extends Object> clazz) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.ArgumentMatchers;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class UseArgumentMatchersInsteadOfMatchersTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.anyString(), ArgumentMatchers.eq(Integer.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s, Class<? extends Object> clazz) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    public void testNoHighlightForStaticImportedMatcherFromArgumentMatchers() {
        doJavaTest("NoHighlightTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.ArgumentMatchers.anyString;\n" +
                "import static org.mockito.ArgumentMatchers.eq;\n" +
                "\n" +
                "public class NoHighlightTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(anyString(), eq(Integer.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s, Class<? extends Object> clazz) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

}
