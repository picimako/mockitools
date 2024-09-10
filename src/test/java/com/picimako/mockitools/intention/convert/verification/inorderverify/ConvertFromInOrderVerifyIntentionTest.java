//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.Convention;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Functional test for {@link ConvertFromInOrderVerifyIntention}.
 */
class ConvertFromInOrderVerifyIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromInOrderVerifyIntention();
    }

    //Availability

    @Test
    void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    @Test
    void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            """
                class NotAvailable {
                    private String fiel<caret>d;
                }""");
    }

    @Test
    void testNotAvailableOnNonInOrderVerifyMethod() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        Mockito.mo<caret>ck(Object.class);
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
                        inOrder.ve<caret>rify(mockObject);
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
                        inOrder.ve<caret>rify();
                    }
                }""");
    }

    @Test
    void testAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(Convention.MOCKITO);
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        inOrder.ve<caret>rify(mockObject).doSomething();
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
                        inOrder.ve<caret>rify(mockObject).doSomething();
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
                        inOrder.ve<caret>rify(mockObject, Mockito.times(2)).doSomething();
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
        addEnforceConventionInspection(Convention.BDD_MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Options {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mock);
                        inOrder.ve<caret>rify(mock).doSomething();
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
        addEnforceConventionInspection(Convention.MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;
                import org.mockito.InOrder;

                class Options {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mock);
                        inOrder.ve<caret>rify(mock).doSomething();
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
                        inOrder.ve<caret>rify(mock).doSomething();
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
                            mock.ve<caret>rify(List::of);
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
                            mock.ver<caret>ify(List::copyOf, Mockito.times(3));
                        }    }
                }""");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockedStaticVerifyAction.class);
    }
}
