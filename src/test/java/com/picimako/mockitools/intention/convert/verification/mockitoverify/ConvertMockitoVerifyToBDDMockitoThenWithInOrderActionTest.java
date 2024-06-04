//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction}.
 */
class ConvertMockitoVerifyToBDDMockitoThenWithInOrderActionTest extends MockitoolsActionTestBase {

    //Caret based conversion

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithoutVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(false),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.ve<caret>rify(mockObject).doSomething();
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
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(false),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.ve<caret>rify(mockObject, Mockito.times(2)).doSomething();
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
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeWithLineWrapping() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(false),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.ve<caret>rify(mockObject, Mockito.times(2)
                            .description("some description"))
                            .doSomething();
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
                        BDDMockito.then(mockObject).should(order, Mockito.times(2)
                            .description("some description"))
                            .doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    //Selection based conversion

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithoutVerificationModeInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(true),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject).doSomething();
                        Mockito.verify(mockObject).doSomething();</selection>
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
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(true),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject, Mockito.times(2)).doSomething();
                        Mockito.verify(mockObject, Mockito.times(2)).doSomething();</selection>
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

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeWithLineWrappingInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(true),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject, Mockito.times(2)
                            .description("some description"))
                            .doSomething();
                        Mockito.verify(mockObject, Mockito.times(2)
                            .description("some description"))
                            .doSomething();</selection>
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
                        BDDMockito.then(mockObject).should(order, Mockito.times(2)
                            .description("some description"))
                            .doSomething();
                        BDDMockito.then(mockObject).should(order, Mockito.times(2)
                            .description("some description"))
                            .doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }
}
