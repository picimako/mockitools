//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyIterableInsteadOfAnyIterableOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyIterable<caret>Of(String.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Iterable<String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class UseAnyIterableInsteadOfAnyIterableOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyIterable());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Iterable<String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testArgumentMatchersAnyMapOfReplacedWithAnyMap() {
        doQuickFixTest("Replace with ArgumentMatchers.anyMap()", "UseAnyMapInsteadOfAnyMapOfTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class UseAnyMapInsteadOfAnyMapOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyMap<caret>Of(String.class, String.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Map<String, String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.ArgumentMatchers;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class UseAnyMapInsteadOfAnyMapOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyMap());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Map<String, String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testArgumentMatchersAnyIterableOfReplacedWithAnyIterableForStaticImport() {
        doQuickFixTest("Replace with ArgumentMatchers.anyIterable()", "UseAnyIterableInsteadOfAnyIterableOfTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "import static org.mockito.ArgumentMatchers.anyIterableOf;\n" +
                "\n" +
                "public class UseAnyIterableInsteadOfAnyIterableOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(anyIterable<caret>Of(String.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Iterable<String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "import static org.mockito.ArgumentMatchers.anyIterable;\n" +
                "import static org.mockito.ArgumentMatchers.anyIterableOf;\n" +
                "\n" +
                "public class UseAnyIterableInsteadOfAnyIterableOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(anyIterable());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Iterable<String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testArgumentMatchersAnyMapOfReplacedWithAnyMapForStaticImport() {
        doQuickFixTest("Replace with ArgumentMatchers.anyMap()", "UseAnyMapInsteadOfAnyMapOfTest.java",
            "import org.mockito.Mockito;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "import static org.mockito.ArgumentMatchers.anyMapOf;\n" +
                "\n" +
                "public class UseAnyMapInsteadOfAnyMapOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(anyMap<caret>Of(String.class, String.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Map<String, String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "import static org.mockito.ArgumentMatchers.anyMap;\n" +
                "import static org.mockito.ArgumentMatchers.anyMapOf;\n" +
                "\n" +
                "public class UseAnyMapInsteadOfAnyMapOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(anyMap());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Map<String, String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testMatchersAnyIterableOfReplacedWithAnyIterable() {
        doQuickFixTest("Replace with ArgumentMatchers.anyIterable()", "UseAnyIterableInsteadOfAnyIterableOfTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class UseAnyIterableInsteadOfAnyIterableOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.anyIterable<caret>Of(String.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Iterable<String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.ArgumentMatchers;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "\n" +
                "public class UseAnyIterableInsteadOfAnyIterableOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyIterable());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Iterable<String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }

    @Test
    void testMatchersAnyMapOfReplacedWithAnyMap() {
        doQuickFixTest("Replace with ArgumentMatchers.anyMap()", "UseAnyMapInsteadOfAnyMapOfTest.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class UseAnyMapInsteadOfAnyMapOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(Matchers.anyMap<caret>Of(String.class, String.class));\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Map<String, String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}",
            "import org.mockito.ArgumentMatchers;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Matchers;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class UseAnyMapInsteadOfAnyMapOfTest {\n" +
                "   public void testMethod() {\n" +
                "       MockObject mock = Mockito.mock(MockObject.class);\n" +
                "       Mockito.doReturn(10).when(mock).method(ArgumentMatchers.anyMap());\n" +
                "   }\n" +
                "   private static final class MockObject {\n" +
                "       public int method(Map<String, String> s) {\n" +
                "           return 0;\n" +
                "       }\n" +
                "   }\n" +
                "}");
    }
}
