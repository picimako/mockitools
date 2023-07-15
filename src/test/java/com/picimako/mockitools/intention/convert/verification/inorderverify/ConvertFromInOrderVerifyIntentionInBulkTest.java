//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Functional test for {@link ConvertFromInOrderVerifyIntention}.
 */
@RunsInEdt
class ConvertFromInOrderVerifyIntentionInBulkTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromInOrderVerifyIntention();
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
                import org.mockito.InOrder;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.inOrder(mockObject).</selection>verify(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableOnNonInOrderVerifyChain() {
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
    void testNotAvailableWhenInOrderVerifyHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        <selection>inOrder.verify(mockObject);</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableOnInOrderVerifyWithNoArgument() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        <selection>inOrder.verify();</selection>
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
    void testAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        <selection>inOrder.verify(mockObject).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForInOrderVerifyWithoutVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        <selection>inOrder.verify(mockObject).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForInOrderVerifyWithVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnSingleInOrderVerifySelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject, mockObject2);
                        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();</selection>
                        inOrder.verify(mockObject2).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnMultipleInOrderVerifySelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        MockObject mockObject2 = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject, mockObject2);
                        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();
                        inOrder.verify(mockObject2).doSomething();</selection>
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    //Action selection options - non-MockedStatic

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedMockitoIsNotEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Options {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mock);
                        <selection>inOrder.verify(mock).doSomething();</selection>
                    }
                }""");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction.class,
            ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction.class
        );
    }

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsNotEnforcedMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Options {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mock);
                        <selection>inOrder.verify(mock).doSomething();</selection>
                    }
                }""");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockitoVerifyAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenNothingIsEnforced() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Options {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mock);
                        <selection>inOrder.verify(mock).doSomething();</selection>
                    }
                }""");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertInOrderVerifyToMockitoVerifyAction.class,
            ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction.class,
            ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction.class
        );
    }

    //Action selection options - MockedStatic

    @Test
    void testReturnsAvailableActionsForMockedStatic() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class Options {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::of);</selection>
                        }    }
                }""");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockedStaticVerifyAction.class);
    }

    @Test
    void testReturnsAvailableActionsForMockedStaticWithVerificationMode() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class Options {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::copyOf, Mockito.times(3));</selection>
                        }    }
                }""");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockedStaticVerifyAction.class);
    }
}
