//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertBDDMockitoThenToMockitoVerifyAction}.
 */
class ConvertBDDMockitoThenToMockitoVerifyActionTest extends MockitoolsActionTestBase {

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(false),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.verify(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithVerificationMode() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(false),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.verify(mockObject, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithInOrder() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(false),
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
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        Mockito.verify(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithInOrderAndVerificationMode() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(false),
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
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder order = Mockito.inOrder(mockObject);
                        Mockito.verify(mockObject, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithVerificationModeWithLineWrapping() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(false),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject)
                            .should(Mockito.times(2)
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.verify(mockObject, Mockito.times(2)
                                .description("some description"))
                            .doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    //Selection

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithoutVerificationModeInSelection() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(true),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.verify(mockObject).doSomething();
                        Mockito.verify(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithVerificationModeInSelection() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(true),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.verify(mockObject, Mockito.times(2)).doSomething();
                        Mockito.verify(mockObject, Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsBDDMockitoThenToMockitoVerifyWithVerificationModeWithLineWrappingInSelection() {
        checkAction(() -> new ConvertBDDMockitoThenToMockitoVerifyAction(true),
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject)
                            .should(Mockito.times(2)
                                .description("some description"))
                            .doSomething();
                        BDDMockito.then(mockObject)
                            .should(Mockito.times(2)
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.verify(mockObject, Mockito.times(2)
                                .description("some description"))
                            .doSomething();
                        Mockito.verify(mockObject, Mockito.times(2)
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
