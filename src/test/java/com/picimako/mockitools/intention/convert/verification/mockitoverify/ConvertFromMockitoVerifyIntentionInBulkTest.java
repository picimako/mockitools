//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.junit5.RunInEdt;
import com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertFromMockitoVerifyIntention}.
 */
@RunInEdt
class ConvertFromMockitoVerifyIntentionInBulkTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromMockitoVerifyIntention();
    }

    //Availability

    @Test
    void testNotAvailableWithSelectionShorterThanMinRequiredLength() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        <selection>Mockito</selection>.mock(Object.class);
                    }
                }""");
    }

    @Test
    void testNotAvailableForOnlyWhitespaceSelection() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                <selection>                </selection>Mockito.mock(Object.class);
                    }
                }""");
    }

    @Test
    void testNotAvailableForIncorrectSelectionEndDot() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito</selection>.verify(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableOnNonMockitoVerifyChain() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        <selection>Mockito.mock(Object.class);</selection>
                    }
                }""");
    }

    @Test
    void testNotAvailableWhenMockitoVerifyHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject);</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableOnMockitoVerifyWithNoArgument() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify();</selection>
                    }
                }""");
    }

    @Test
    void testNotAvailableOnMultipleMixedVerificationSelected() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject, mockObject2);
                        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();
                        Mockito.verify(mockObject2).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableOnBDDMockitoThenSelected() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForMockitoVerifyWithoutVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForMockitoVerifyWithVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject, Mockito.times(2)).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnSingleMockitoVerifySelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject, Mockito.times(2)).doSomething();</selection>
                        Mockito.verify(mockObject2).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnMultipleMockitoVerifySelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        <selection>Mockito.verify(mockObject, Mockito.times(2)).doSomething();
                        Mockito.verify(mockObject2).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }
}
