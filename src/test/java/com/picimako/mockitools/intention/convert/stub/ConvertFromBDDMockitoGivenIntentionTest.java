//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for {@link ConvertFromBDDMockitoGivenIntention}.
 */
@RunsInEdt
class ConvertFromBDDMockitoGivenIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromBDDMockitoGivenIntention();
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
    void testNotAvailableForNonBDDMockitoGivenCall() {
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
    void testNotAvailableForNonBDDMockitoGivenCallInBulk() {
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
    void testNotAvailableForBDDMockitoGivenWithoutWill() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.giv<caret>en(mockObject.doSomething());
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testNotAvailableForBDDMockitoGivenWithoutWillInBulk() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>Mockito.doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
                        BDDMockito.given(mockObject.doSomething());</selection>
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnBDDMockitoGiven() {
        checkIntentionIsAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.gi<caret>ven(mockObject.doSomething()).willReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");
    }

    @Test
    void testAvailableOnBDDMockitoGivenInBulk() {
        checkIntentionIsAvailable(
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        <selection>BDDMockito.given(mockObject.doSomething()).willReturn(15);
                        BDDMockito.given(mockObject.doSomething()).willReturn(10);</selection>
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
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);

        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.gi<caret>ven(mockObject.doSomething()).willReturn(10);
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
    void testOptionsWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);

        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.gi<caret>ven(mockObject.doSomething()).willReturn(10);
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

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.gi<caret>ven(mockObject.doSomething()).willReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()", "BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsWill() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;
                import org.mockito.invocation.InvocationOnMock

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.gi<caret>ven(mockObject.doSomething()).willReturn(10).will(InvocationOnMock::callRealMethod);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.when()", "BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainWill() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                class Available {
                    void testMethod(){
                        MockObject mockObject = Mockito.mock(MockObject.class);
                        BDDMockito.gi<caret>ven(mockObject.doSomething()).willReturn(10);
                    }
                    private static class MockObject {
                        public int doSomething() {
                            return 0;
                        }
                    }
                }""");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()", "BDDMockito.will*()");
    }

    @NotNull
    private List<String> getActionTexts() {
        return new ConvertFromBDDMockitoGivenIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream()
            .map(action -> (ConvertStubbingAction) action)
            .map(ConvertStubbingAction::getTo)
            .map(StubbingApproach::getPresentableText)
            .collect(toList());
    }
}
