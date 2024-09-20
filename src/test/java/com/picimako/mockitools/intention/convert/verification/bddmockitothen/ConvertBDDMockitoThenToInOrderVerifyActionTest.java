//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link com.picimako.mockitools.intention.convert.verification.bddmockitothen.ConvertBDDMockitoThenToInOrderVerifyAction}.
 * <p>
 * NOTE: for some unknown reason these test have become a bit flaky after upgrading to Java 21. The import statement
 * of 'org.mockito.BDDMockito' is sometimes removed, sometimes not, from the 'after' text. Thus, they are validated
 * with allowing the result to contain or to not contain that import statement.
 */
class ConvertBDDMockitoThenToInOrderVerifyActionTest extends MockitoolsActionTestBase {

    //Caret based conversion

    @Test
    void testConvertsBDDMockitoThenToInOrderVerifyWithNewInOrderWithoutVerificationMode() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(false),
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
    void testConvertsBDDMockitoThenToInOrderVerifyWithNewInOrderWithVerificationMode() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(false),
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
    void testConvertsBDDMockitoThenToInOrderVerifyReusingSpecifiedInOrderWithoutVerificationMode() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(false),
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        BDDMockito.th<caret>en(mockObject).should(order).doSomething();
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
    void testConvertsBDDMockitoThenToInOrderVerifyReusingSpecifiedInOrderWithVerificationMode() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(false),
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        BDDMockito.th<caret>en(mockObject).should(order, Mockito.times(2)).doSomething();
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

    //Selection based conversion

    @Test
    void testConvertsBDDMockitoThenToInOrderVerifyWithNewInOrderWithoutVerificationModeInSelection() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(true),
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
    void testConvertsBDDMockitoThenToInOrderVerifyWithNewInOrderWithVerificationModeInSelection() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject).should(Mockito.times(3)).doSomething();</selection>
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
                        order.verify(mockObject, Mockito.times(3)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsBDDMockitoThenToInOrderVerifyReusingSpecifiedInOrderWithoutVerificationModeInSelection() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        <selection>BDDMockito.then(mockObject).should(order).doSomething();
                        BDDMockito.then(mockObject).should(order).doSomething();</selection>
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
    void testConvertsBDDMockitoThenToInOrderVerifyReusingSpecifiedInOrderWithVerificationModeInSelection() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        <selection>BDDMockito.then(mockObject).should(order, Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject).should(order, Mockito.times(2)).doSomething();</selection>
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
    void testConvertsBDDMockitoThenToInOrderVerifyWithNewInOrderWithVerificationModeInSelectionMultipleMockObjects() {
        checkActionFlexible(() -> new ConvertBDDMockitoThenToInOrderVerifyAction(true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject2).should(Mockito.times(2)).doSomething();</selection>
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
