//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import com.intellij.testFramework.junit5.RunInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction}.
 */
@RunInEdt
class ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderActionTest extends MockitoolsActionTestBase {

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithoutVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(false),
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.ver<caret>ify(mockObject).doSomething();
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
                        BDDMockito.then(mockObject).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(false),
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        order.ver<caret>ify(mockObject, Mockito.times(2)).doSomething();
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
                        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithoutVerificationModeInBulkSingle() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        <selection>order.verify(mockObject).doSomething();</selection>
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
                        BDDMockito.then(mockObject).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithVerificationModeInBulkSingle() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        <selection>order.verify(mockObject, Mockito.times(2)).doSomething();</selection>
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
                        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithoutVerificationModeInBulkMultiple() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject, mockObject2);
                        <selection>order.verify(mockObject).doSomething();
                        order.verify(mockObject2).doSomething();</selection>
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
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject, mockObject2);
                        BDDMockito.then(mockObject).should().doSomething();
                        BDDMockito.then(mockObject2).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsInOrderVerifyToBDDMockitoThenWithVerificationModeInBulkMultiple() {
        checkAction(() -> new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(true),
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject, mockObject2);
                        <selection>order.verify(mockObject, Mockito.times(2)).doSomething();
                        order.verify(mockObject2).doSomething();</selection>
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
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject, mockObject2);
                        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject2).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }
}
