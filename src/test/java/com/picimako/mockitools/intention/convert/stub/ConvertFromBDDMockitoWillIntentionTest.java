//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
 * Integration test for {@link ConvertFromBDDMockitoWillIntention}.
 */
@RunsInEdt
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
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    @Test
    void testNotAvailableForNonBDDMockitoWillCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.invocation.InvocationOnMock\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.wh<caret>en(mockObject.doSomething()).thenReturn(10).then(InvocationOnMock::callRealMethod);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForNonBDDMockitoWillCallInBulk() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.invocation.InvocationOnMock\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.when(mockObject.doSomething()).thenReturn(10).then(InvocationOnMock::callRealMethod);\n" +
                "        Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject);</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutGiven() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willT<caret>hrow(IllegalArgumentException.class);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutGivenInBulk() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "        BDDMockito.willThrow(IllegalArgumentException.class);</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutCallOnStub() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForBDDMockitoWillWithoutCallOnStubInBulk() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject);.doSomething();\n" +
                "        BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject);</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnBDDMockitoWill() {
        checkIntentionIsAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willT<caret>hrow(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnBDDMockitoWillInBulk() {
        checkIntentionIsAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "        BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    // Action selection options

    @Test
    void testOptionsWhenBDDMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);

        getFixture().configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("BDDMockito.given()");
    }

    @Test
    void testOptionsWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);

        getFixture().configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()");
    }

    @Test
    void testOptionsWhenNothingIsEnforced() {
        getFixture().configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsWill() {
        getFixture().configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.invocation.InvocationOnMock\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).will(InvocationOnMock::callRealMethod).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.when()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainWill() {
        getFixture().configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()", "Mockito.when()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsWillDoNothing() {
        getFixture().configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).willDoNothing().given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.do*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainWillDoNothing() {
        getFixture().configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.willTh<caret>row(IllegalArgumentException.class).given(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

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
