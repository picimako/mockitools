//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ThrowsCheckedExceptionStubbingInspection}.
 */
class ThrowsCheckedExceptionStubbingInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new ThrowsCheckedExceptionStubbingInspection();
    }

    @Test
    void testThrowsCheckedExceptionStubbing() {
        doJavaTest();
    }

    //Quick fixes

    @Test
    void testAddsExceptionToEmptyThrowsClause() {
        doQuickFixTest("Add exception to throws clause", "QuickFix.java",
            "import java.io.IOException;\n" +
                "import java.lang.NoSuchMethodException;\n" +
                "import org.mockito.Mockito;\n" +
                "class QuickFix {\n" +
                "    void testMethod() {\n" +
                "        MockObject mock = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mock.doSomething()).thenThrow(IOExcep<caret>tion.class);\n" +
                "    }\n" +
                "\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import java.io.IOException;\n" +
                "import java.lang.NoSuchMethodException;\n" +
                "import org.mockito.Mockito;\n" +
                "class QuickFix {\n" +
                "    void testMethod() {\n" +
                "        MockObject mock = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mock.doSomething()).thenThrow(IOException.class);\n" +
                "    }\n" +
                "\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() throws IOException {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAddsExceptionToNonEmptyThrowsClause() {
        doQuickFixTest("Add exception to throws clause", "QuickFix.java",
            "import java.io.IOException;\n" +
                "import java.lang.NoSuchMethodException;\n" +
                "import org.mockito.Mockito;\n" +
                "class QuickFix {\n" +
                "    void testMethod() {\n" +
                "        MockObject mock = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mock.doSomething()).thenThrow(IOExcep<caret>tion.class);\n" +
                "    }\n" +
                "\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() throws NoSuchMethodException {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import java.io.IOException;\n" +
                "import java.lang.NoSuchMethodException;\n" +
                "import org.mockito.Mockito;\n" +
                "class QuickFix {\n" +
                "    void testMethod() {\n" +
                "        MockObject mock = Mockito.mock(MockObject.class);\n" +
                "        Mockito.when(mock.doSomething()).thenThrow(IOException.class);\n" +
                "    }\n" +
                "\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() throws NoSuchMethodException, IOException {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
