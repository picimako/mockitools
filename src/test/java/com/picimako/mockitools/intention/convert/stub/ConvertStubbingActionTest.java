//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import com.picimako.mockitools.StubbingApproach;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertStubbingAction}.
 */
@RunsInEdt
class ConvertStubbingActionTest extends MockitoolsActionTestBase {

    @Test
    void testConvertsFromMockitoWhen() {
        checkAction(() -> new ConvertStubbingAction(StubbingApproach.MOCKITO_WHEN, StubbingApproach.MOCKITO_DO_X, false),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10).thenThrow(IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doReturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testConvertsFromMockitoWhenInBulk() {
        checkAction(() -> new ConvertStubbingAction(StubbingApproach.MOCKITO_WHEN, StubbingApproach.MOCKITO_DO_X, true),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.when(mockObject.doSomething()).thenReturn(10).thenThrow(IllegalArgumentException.class);
                        Mockito.when(mockObject.doSomething()).thenReturn(30);</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doReturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                        Mockito.doReturn(30).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testConvertsFromBDDMockitoGiven() {
        checkAction(() -> new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_GIVEN, StubbingApproach.MOCKITO_DO_X, false),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.giv<caret>en(mockObject.doSomething()).willReturn(10).willThrow(IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doReturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testConvertsFromBDDMockitoGivenInBulk() {
        checkAction(() -> new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_GIVEN, StubbingApproach.MOCKITO_DO_X, true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.given(mockObject.doSomething()).willReturn(10).willThrow(IllegalArgumentException.class);
                        BDDMockito.given(mockObject.doSomething()).willReturn(30);</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doReturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                        Mockito.doReturn(30).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testConvertsFromBDDMockitoWillX() {
        checkAction(() -> new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_WILL_X, StubbingApproach.MOCKITO_DO_X, false),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willR<caret>eturn(10).willThrow(IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doReturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testConvertsFromBDDMockitoWillXInBulk() {
        checkAction(() -> new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_WILL_X, StubbingApproach.MOCKITO_DO_X, true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.willReturn(10).willThrow(IllegalArgumentException.class).given(mockObject).doSomething();
                        BDDMockito.willReturn(30).given(mockObject).doSomething();</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doReturn(10).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                        Mockito.doReturn(30).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }
}
