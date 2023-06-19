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
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class ArgumentMatchersIsNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNot<caret>Null(String.class));\n" +
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
                "public class ArgumentMatchersIsNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNotNull());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testArgumentMatchersNotNullIsReplaced() {
        doQuickFixTest("Remove matcher argument", "ArgumentMatchersNotNullIsReplacedTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class ArgumentMatchersNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.not<caret>Null(String.class));\n" +
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
                "public class ArgumentMatchersNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.notNull());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testArgumentMatchersIsNullIsReplaced() {
        doQuickFixTest("Remove matcher argument", "ArgumentMatchersIsNullIsReplacedTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class ArgumentMatchersIsNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.is<caret>Null(String.class));\n" +
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
                "public class ArgumentMatchersIsNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNull());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testMatchersIsNotNullIsReplaced() {
        doQuickFixTest("Replace with ArgumentMatchers.isNotNull()", "ArgumentMatchersIsNotNullIsReplacedTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class ArgumentMatchersIsNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.isNot<caret>Null(String.class));\n" +
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
                "public class ArgumentMatchersIsNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNotNull());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testMatchersNotNullIsReplaced() {
        doQuickFixTest("Replace with ArgumentMatchers.notNull()", "ArgumentMatchersNotNullIsReplacedTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class ArgumentMatchersNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.not<caret>Null(String.class));\n" +
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
                "public class ArgumentMatchersNotNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.notNull());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testMatchersIsNullIsReplaced() {
        doQuickFixTest("Replace with ArgumentMatchers.isNull()", "ArgumentMatchersIsNullIsReplacedTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class ArgumentMatchersIsNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.is<caret>Null(String.class));\n" +
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
                "public class ArgumentMatchersIsNullIsReplacedTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.isNull());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(String s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }
}
