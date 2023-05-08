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
 * Integration test for {@link ConvertFromMockitoDoIntention}.
 */
@RunsInEdt
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
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    @Test
    void testNotAvailableForNonMockitoDoCall() {
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
    void testNotAvailableForNonMockitoDoCallInBulk() {
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
    void testNotAvailableForMockitoDoWithoutWhen() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doTh<caret>row(IllegalArgumentException.class).doNothing();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForMockitoDoWithoutWhenInBulk() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();\n" +
                "        Mockito.doThrow(IllegalArgumentException.class).doNothing();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForMockitoDoWithoutCallOnStub() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doTh<caret>row(IllegalArgumentException.class).doNothing().when(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForMockitoDoWithoutCallOnStubInBulk() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();\n" +
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
    void testAvailableOnMockitoDo() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.doT<caret>hrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnMockitoDoInBulk() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();\n" +
                "        Mockito.doThrow(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();</selection>\n" +
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
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("BDDMockito.will*()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);

        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.when()");
    }

    @Test
    void testOptionsWhenNothingIsEnforced() {
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.when()", "BDDMockito.will*()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainContainsDoNothing() {
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsNotEnforcedAndCallChainDoesntContainDoNothing() {
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("Mockito.when()", "BDDMockito.will*()", "BDDMockito.given()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsEnforcedAndCallChainContainsDoNothing() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);

        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(IllegalArgumentException.class).doNothing().when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

        assertThat(getActionTexts()).containsExactly("BDDMockito.will*()");
    }

    @Test
    void testOptionsWhenBDDMockitoIsEnforcedAndCallChainDoesntContainDoNothing() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);

        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.do<caret>Throw(IllegalArgumentException.class).when(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public int doSomething() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    }\n" +
                "}");

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
