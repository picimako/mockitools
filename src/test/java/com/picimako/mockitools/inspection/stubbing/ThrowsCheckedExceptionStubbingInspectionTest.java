//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.stubbing;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
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
            """
                import java.io.IOException;
                import java.lang.NoSuchMethodException;
                import org.mockito.Mockito;
                class QuickFix {
                    void testMethod() {
                        MockObject mock = Mockito.mock(MockObject.class);
                        Mockito.when(mock.doSomething()).thenThrow(IOExcep<caret>tion.class);
                    }

                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import java.io.IOException;
                import java.lang.NoSuchMethodException;
                import org.mockito.Mockito;
                class QuickFix {
                    void testMethod() {
                        MockObject mock = Mockito.mock(MockObject.class);
                        Mockito.when(mock.doSomething()).thenThrow(IOException.class);
                    }

                    private static class MockObject {
                        public int doSomething() throws IOException {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAddsExceptionToNonEmptyThrowsClause() {
        doQuickFixTest("Add exception to throws clause", "QuickFix.java",
            """
                import java.io.IOException;
                import java.lang.NoSuchMethodException;
                import org.mockito.Mockito;
                class QuickFix {
                    void testMethod() {
                        MockObject mock = Mockito.mock(MockObject.class);
                        Mockito.when(mock.doSomething()).thenThrow(IOExcep<caret>tion.class);
                    }

                    private static class MockObject {
                        public int doSomething() throws NoSuchMethodException {
                            return 0;
                        }
                    }
                }""",
            """
                import java.io.IOException;
                import java.lang.NoSuchMethodException;
                import org.mockito.Mockito;
                class QuickFix {
                    void testMethod() {
                        MockObject mock = Mockito.mock(MockObject.class);
                        Mockito.when(mock.doSomething()).thenThrow(IOException.class);
                    }

                    private static class MockObject {
                        public int doSomething() throws NoSuchMethodException, IOException {
                            return 0;
                        }
                    }
                }""");
    }
}
