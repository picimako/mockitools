//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.junit5.RunInEdt;
import com.picimako.mockitools.Convention;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for {@link ConvertFromBDDMockitoWillIntention}.
 */
@RunInEdt
class ConvertFromBDDMockitoWillIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromBDDMockitoWillIntention();
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
    void testNotAvailableForNonBDDMockitoWillCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.invocation.InvocationOnMock

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10).then(InvocationOnMock::callRealMethod);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForNonBDDMockitoWillCallInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.invocation.InvocationOnMock

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.when(mockObject.doSomething()).thenReturn(10).then(InvocationOnMock::callRealMethod);
                        Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject);</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutGiven() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willT<caret>hrow(IllegalArgumentException.class);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutGivenInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();
                        BDDMockito.willThrow(IllegalArgumentException.class);</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutCallOnStub() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutCallOnStubInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject);.doSomething();
                        BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject);</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnBDDMockitoWill() {
        checkIntentionIsAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willT<caret>hrow(IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnBDDMockitoWillInBulk() {
        checkIntentionIsAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();
                        BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();</selection>
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
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("BDDMockito.given()");
    }

    @Test
    void testOptionsWhenMockitoIsEnforced() {
        addEnforceConventionInspection(Convention.MOCKITO);

        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()");
    }

    @Test
    void testOptionsWhenNothingIsEnforced() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsWill() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;
                import org.mockito.invocation.InvocationOnMock

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).will(InvocationOnMock::callRealMethod).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.when()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainWill() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsWillDoNothing() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).willDoNothing().given(mockObject).doSomething();
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
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainWillDoNothing() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()", "BDDMockito.given()");
    }

    @NotNull
    private List<String> getActionTexts() {
        return new ConvertFromBDDMockitoWillIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream()
            .map(action -> (ConvertStubbingAction) action)
            .map(ConvertStubbingAction::getTo)
            .map(StubbingApproach::getPresentableText)
            .collect(toList());
    }
}
