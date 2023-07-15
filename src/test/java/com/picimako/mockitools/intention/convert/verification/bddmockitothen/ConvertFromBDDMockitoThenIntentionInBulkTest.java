//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for {@link ConvertFromBDDMockitoThenIntention}.
 */
@RunsInEdt
class ConvertFromBDDMockitoThenIntentionInBulkTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromBDDMockitoThenIntention();
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
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).</selection>should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableOnNonBDDMockitoThenChain() {
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
    void testNotAvailableWhenBDDMockitoThenHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject);</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableOnBDDMockitoThenWithNoArgument() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then().should();</selection>
                    }
                }""");
    }

    @Test
    void testNotAvailableOnMultipleMixedVerificationSelected() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                        Mockito.verify(mockObject2).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should().doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForBDDMockitoThenWithoutVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should().doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForBDDMockitoThenWithVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnSingleBDDMockitoThenSelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();</selection>
                        BDDMockito.then(mockObject2).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnMultipleBDDMockitoThenSelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.BDDMockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
                        BDDMockito.then(mockObject2).should().doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    //Action selection options

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedAllVerificationsWithoutInOrder() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        <selection>BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();
                        BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class,
            AddInOrderToBDDMockitoAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedAllVerificationsWithInOrder() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        <selection>BDDMockito.then(mockObject).should(inOrder).doSomething();
                        BDDMockito.then(mockObject).should(inOrder).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedVerificationsMixedWithAndWithoutInOrder() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        <selection>BDDMockito.then(mockObject).should(inOrder).doSomething();
                        BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertBDDMockitoThenToMockitoVerifyAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedVerificationsMultipleDifferentInOrders() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        InOrder inOrde2 = Mockito.inOrder(mockObject);
                        <selection>BDDMockito.then(mockObject).should(inOrder).doSomething();
                        BDDMockito.then(mockObject).should(inOrder2).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertBDDMockitoThenToMockitoVerifyAction.class);
    }
}
