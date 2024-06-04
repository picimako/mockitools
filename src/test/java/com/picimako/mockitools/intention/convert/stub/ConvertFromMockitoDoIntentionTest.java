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
 * Integration test for {@link ConvertFromMockitoDoIntention}.
 */
class ConvertFromMockitoDoIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromMockitoDoIntention();
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
    void testNotAvailableForNonMockitoDoCall() {
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
    void testNotAvailableForNonMockitoDoCallInBulk() {
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
    void testNotAvailableForMockitoDoWithoutWhen() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doTh<caret>row(IllegalArgumentException.class).doNothing();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForMockitoDoWithoutWhenInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();
                        Mockito.doThrow(IllegalArgumentException.class).doNothing();</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForMockitoDoWithoutCallOnStub() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doTh<caret>row(IllegalArgumentException.class).doNothing().when(mockObject);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForMockitoDoWithoutCallOnStubInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();
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
    void testAvailableOnMockitoDo() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.doT<caret>hrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnMockitoDoInBulk() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();
                        Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();</selection>
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

        assertThat(getActionTexts()).containsExactly("BDDMockito.will*()", "BDDMockito.given()");
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
                        Mockito.do<caret>Throw(IllegalArgumentException.class).when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.when()");
    }

    @Test
    void testOptionsWhenNothingIsEnforced() {
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

        assertThat(getActionTexts()).containsExactly("Mockito.when()", "BDDMockito.will*()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsDoNothing() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.do<caret>Throw(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainDoNothing() {
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

        assertThat(getActionTexts()).containsExactly("Mockito.when()", "BDDMockito.will*()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsEnforcedAndCallChainContainsDoNothing() {
        addEnforceConventionInspection(Convention.BDD_MOCKITO);

        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        Mockito.do<caret>Throw(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsEnforcedAndCallChainDoesntContainDoNothing() {
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

        assertThat(getActionTexts()).containsExactly("BDDMockito.will*()", "BDDMockito.given()");
    }

    @NotNull
    private List<String> getActionTexts() {
        return new ConvertFromMockitoDoIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream()
            .map(action -> (ConvertStubbingAction) action)
            .map(ConvertStubbingAction::getTo)
            .map(StubbingApproach::getPresentableText)
            .collect(toList());
    }
}
