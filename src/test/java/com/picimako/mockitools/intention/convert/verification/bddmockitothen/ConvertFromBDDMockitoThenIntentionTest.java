//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.junit5.RunInEdt;
import com.picimako.mockitools.Convention;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import com.picimako.mockitools.intention.convert.verification.NoActionAvailableAction;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for {@link ConvertFromBDDMockitoThenIntention}.
 */
@RunInEdt
class ConvertFromBDDMockitoThenIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromBDDMockitoThenIntention();
    }

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
    void testNotAvailableOnNonBDDMockitoThenMethod() {
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
    void testNotAvailableWhenBDDMockitoThenHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject);
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableWhenSubsequentMethodCallToBDDMockitoThenIsNotShould() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject).shouldHaveNoInteractions();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableWhenBDDMockitoThenShouldHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject).should();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableWhenBDDMockitoIsEnforced() {
        addEnforceConventionInspection(Convention.BDD_MOCKITO);
        checkIntentionIsAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForBDDMockitoThenShouldWithoutVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject).should().doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    @Test
    void testAvailableForBDDMockitoThenShouldWithVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.th<caret>en(mockObject).should(Mockito.times(2)).doSomething();
                    }
                    private static class MockObject {
                        public void doSomething() {
                        }
                    }
                }""");
    }

    //Action selection options

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedWithoutInOrder() {
        addEnforceConventionInspection(Convention.BDD_MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        BDDMockito.th<caret>en(Mockito.mock(Object.class)).should().doSomething();
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(AddInOrderToBDDMockitoAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedWithInOrder() {
        addEnforceConventionInspection(Convention.BDD_MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        BDDMockito.th<caret>en(mock).should(Mockito.inOrder(mock)).doSomething();
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(NoActionAvailableAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedWithoutInOrderInSelection() {
        addEnforceConventionInspection(Convention.BDD_MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        <selection>BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();
                        BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();</selection>
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(AddInOrderToBDDMockitoAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenMockitoIsEnforcedWithoutInOrder() {
        addEnforceConventionInspection(Convention.MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        BDDMockito.th<caret>en(Mockito.mock(Object.class)).should().doSomething();
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class
        );
    }

    @Test
    void testReturnsAvailableActionsWhenMockitoIsEnforcedWithInOrder() {
        addEnforceConventionInspection(Convention.MOCKITO);
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        BDDMockito.th<caret>en(mock).should(Mockito.inOrder(mock)).doSomething();
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class
        );
    }

    @Test
    void testReturnsAvailableActionsWhenNothingIsEnforcedWithoutInOrder() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        BDDMockito.th<caret>en(Mockito.mock(Object.class)).should().doSomething();
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class,
            AddInOrderToBDDMockitoAction.class
        );
    }

    @Test
    void testReturnsAvailableActionsWhenNothingIsEnforcedWithInOrder() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        BDDMockito.th<caret>en(mock).should(Mockito.inOrder(mock)).doSomething();
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class
        );
    }

    @Test
    void testReturnsAvailableActionsWhenNothingIsEnforcedWithoutInOrderInSelection() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        <selection>BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();
                        BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();</selection>
                    }
                }""");
        List<Class<?>> actions = new ConvertFromBDDMockitoThenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class,
            AddInOrderToBDDMockitoAction.class
        );
    }
}
