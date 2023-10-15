//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import com.intellij.testFramework.junit5.RunInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction}.
 */
@RunInEdt
class ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderActionTest extends MockitoolsActionTestBase {

    //Caret based conversion

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithoutVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(false),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.then(mockObject).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationMode() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(false),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeWithLineWrapping() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(false),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.then(mockObject).should(Mockito.times(2)
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
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(true),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.then(mockObject).should().doSomething();
                        BDDMockito.then(mockObject).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(true),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockitoVerifyToBDDMockitoThenWithVerificationModeWithLineWrappingInSelection() {
        checkAction(() -> new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(true),
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
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.then(mockObject).should(Mockito.times(2)
                            .description("some description"))
                            .doSomething();
                        BDDMockito.then(mockObject).should(Mockito.times(2)
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
