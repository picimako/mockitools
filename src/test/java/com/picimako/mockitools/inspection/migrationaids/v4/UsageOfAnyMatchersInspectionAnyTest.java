//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyObjectTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyObjec<caret>t());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyObjectTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testArgumentMatchersAnyVarargIsReplacedWithAny() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyVarargTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyVarargTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyVar<caret>arg());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyVarargTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testMatchersAnyObjectIsReplacedWithAny() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyObjectTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyObjectTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.anyObjec<caret>t());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.ArgumentMatchers;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyObjectTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testMatchersAnyVarargIsReplacedWithAny() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyVarargTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyVarargTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.anyVar<caret>arg());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyVarargTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testAnyVarargIsReplacedWithAnyWithStaticImport() {
        doQuickFixTest("Replace with ArgumentMatchers.any()", "UseAnyInsteadOfAnyVarargStaticImportTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.ArgumentMatchers.anyVararg;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyVarargStaticImportTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(anyVar<caret>arg());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "import static org.mockito.ArgumentMatchers.any;\n" +
                "import static org.mockito.ArgumentMatchers.anyVararg;\n" +
                "\n" +
                "public class UseAnyInsteadOfAnyVarargStaticImportTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(any());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testAnyIsNotReported() {
        doJavaTest("AnyIsNotReportedTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class AnyIsNotReportedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.any());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testAnyIsNotReportedWithStaticImport() {
        doJavaTest("AnyIsNotReportedTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.ArgumentMatchers.any;\n" +
                "\n" +
                "public class AnyIsNotReportedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(any());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }
}
