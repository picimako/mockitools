//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertThrowStubbingArgumentsIntention}.
 */
class ConvertThrowStubbingArgumentsIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertThrowStubbingArgumentsIntention();
    }

    //Availability

    @Test
    void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            """
                class NotAvailable {
                    private String fiel<caret>d;
                }""");
    }

    @Test
    void testNotAvailableWhenMethodNameDoesntMatch() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.given(mockObject.doSomething()).willR<caret>eturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableWhenNotAllArgumentsAreTheSameKind() {
        checkIntentionIsNotAvailable(
            """
                import java.io.IOException;
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.given(mockObject.doSomething()).wi<caret>llThrow(new IOException(), IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableWhenThereIsNonDefaultConstructorNewExpressionArgument() {
        checkIntentionIsNotAvailable(
            """
                import java.io.IOException;
                import java.sql.SQLException;
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.given(mockObject.doSomething()).will<caret>Throw(new IOException(), new IllegalArgumentException("messa"));
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnThenThrowClasses() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.when(mockObject.doSomething()).then<caret>Throw(IOException.class, IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnThenThrowThrowables() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.when(mockObject.doSomething()).then<caret>Throw(new IOException(), new IllegalArgumentException());
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnDoThrowClasses() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doThr<caret>ow(IOException.class, IllegalArgumentException.class).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnDoThrowThrowables() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.do<caret>Throw(new IOException(), new IllegalArgumentException()).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnGivenWillThrowClasses() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.given(mockObject.doSomething()).willT<caret>hrow(IOException.class, IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnGivenWillThrowThrowables() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.given(mockObject.doSomething()).willThr<caret>ow(new IOException(), new IllegalArgumentException());
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableWillThrowGivenClasses() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IOException.class, IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableWillThrowGivenThrowables() {
        checkIntentionIsAvailable(
            """
                import java.io.IOException;
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(new IOException(), new IllegalArgumentException()).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    //Conversion

    @Test
    void testConvertsThrowablesToClasses() {
        checkIntentionRun(
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class ConversionTest {

                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.when(mockObject.doSomething()).the<caret>nThrow(new IOException(), new IllegalArgumentException());
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class ConversionTest {

                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testConvertsClassesToThrowables() {
        checkIntentionRun(
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.when(mockObject.doSomething()).thenT<caret>hrow(IOException.class, IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""",
            """
                import java.io.IOException;
                import org.mockito.Mockito;

                class ConversionTest {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new IllegalArgumentException());
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }
}
