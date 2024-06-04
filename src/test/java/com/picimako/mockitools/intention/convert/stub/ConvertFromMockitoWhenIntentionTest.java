//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.Convention;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for {@link ConvertFromMockitoWhenIntention}.
 */
class ConvertFromMockitoWhenIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromMockitoWhenIntention();
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
    void testNotAvailableForNonMockitoWhenCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.do<caret>Return(10).when(mockObject).doSomething());
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForNonMockitoWhenCallInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.invocation.InvocationOnMock

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.when(mockObject.doSomething()).thenReturn(10).then(InvocationOnMock::callRealMethod);
                        Mockito.doReturn(10).when(mockObject).doSomething());</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForMockitoWhenWithoutThen() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.wh<caret>en(mockObject.doSomething());
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForMockitoWhenWithoutThenInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                        Mockito.when(mockObject.doSomething());</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnMockitoWhen() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnMockitoWhenInBulk() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.when(mockObject.doSomething()).thenReturn(15);
                        Mockito.when(mockObject.doSomething()).thenReturn(10);</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    // Action selection options

    @Test
    void testOptionsWhenBDDMockitoIsEnforced() {
        addEnforceConventionInspection(Convention.BDD_MOCKITO);

        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.do<caret>Throw(IllegalArgumentException.class).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("BDDMockito.given()", "BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenMockitoIsEnforced() {
        addEnforceConventionInspection(Convention.MOCKITO);

        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.whe<caret>n(mockObject.doSomething()).thenReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()");
    }

    @Test
    void testOptionsWhenNothingIsEnforced() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.whe<caret>n(mockObject.doSomething()).thenReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "BDDMockito.given()", "BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsThen() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.whe<caret>n(mockObject.doSomething()).thenReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "BDDMockito.given()", "BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainThen() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.whe<caret>n(mockObject.doSomething()).thenReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "BDDMockito.given()", "BDDMockito.will*()");
    }

    @NotNull
    private List<String> getActionTexts() {
        return new ConvertFromMockitoWhenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream()
            .map(action -> (ConvertStubbingAction) action)
            .map(ConvertStubbingAction::getTo)
            .map(StubbingApproach::getPresentableText)
            .collect(toList());
    }
}
