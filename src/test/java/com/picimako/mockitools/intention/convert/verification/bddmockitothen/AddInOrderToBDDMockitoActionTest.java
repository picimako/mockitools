//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link AddInOrderToBDDMockitoAction}.
 */
class AddInOrderToBDDMockitoActionTest extends MockitoolsActionTestBase {

    @Test
    void testAddsInOrderArgumentToBDDMockitoThenWithoutVerificationMode() {
        checkAction(() -> new AddInOrderToBDDMockitoAction(false),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(order).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAddsInOrderArgumentToBDDMockitoThenWithVerificationMode() {
        checkAction(() -> new AddInOrderToBDDMockitoAction(false),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject).should(Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(order, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAddsInOrderArgumentToBDDMockitoThenWithoutVerificationModeInSelection() {
        checkAction(() -> new AddInOrderToBDDMockitoAction(true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should().doSomething();
                        BDDMockito.then(mockObject).should().doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(order).doSomething();
                        BDDMockito.then(mockObject).should(order).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAddsInOrderArgumentToBDDMockitoThenWithVerificationModeInSelection() {
        checkAction(() -> new AddInOrderToBDDMockitoAction(true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""",
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(order, Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject).should(order, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }
}
