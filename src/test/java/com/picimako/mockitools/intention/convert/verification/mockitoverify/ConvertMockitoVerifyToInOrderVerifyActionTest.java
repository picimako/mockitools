//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertMockitoVerifyToInOrderVerifyAction}.
 */
class ConvertMockitoVerifyToInOrderVerifyActionTest extends MockitoolsActionTestBase {

    //Caret based conversion

    @Test
    void testConvertsMockitoVerifyToInOrderVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToInOrderVerifyAction(false),
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
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.verify(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToInOrderVerifyWithVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToInOrderVerifyAction(false),
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
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.verify(mockObject, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToInOrderVerifyWithVerificationModeWithLineWrapping() {
        checkAction(() -> new ConvertMockitoVerifyToInOrderVerifyAction(false),
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
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.verify(mockObject, Mockito.times(2)
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
    void testConvertsMockitoVerifyToInOrderVerifyWithoutVerificationModeInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToInOrderVerifyAction(true),
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
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.verify(mockObject).doSomething();
                        order.verify(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToInOrderVerifyWithVerificationModeInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToInOrderVerifyAction(true),
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
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.verify(mockObject, Mockito.times(2)).doSomething();
                        order.verify(mockObject, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToInOrderVerifyWithVerificationModeWithLineWrappingInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToInOrderVerifyAction(true),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject, Mockito.times(2));
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
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.verify(mockObject, Mockito.times(2));
                        order.verify(mockObject, Mockito.times(2)
                            .description("some description"))
                            .doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToInOrderVerifyWithVerificationModeInSelectionMultipleMockObjects() {
        checkAction(() -> new ConvertMockitoVerifyToInOrderVerifyAction(true),
            """
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject, Mockito.times(2)).doSomething();
                        Mockito.verify(mockObject2, Mockito.times(2)).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""",
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject, mockObject2);
                        order.verify(mockObject, Mockito.times(2)).doSomething();
                        order.verify(mockObject2, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }
}
