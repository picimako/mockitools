//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Functional test for {@link ConvertFromMockitoVerifyIntention}.
 */
@RunsInEdt
class ConvertFromMockitoVerifyIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromMockitoVerifyIntention();
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
    void testNotAvailableOnNonMockitoVerifyMethod() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableWhenMockitoVerifyHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableOnMockitoVerifyWithNoArgument() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.ve<caret>rify();\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableForMockitoVerifyWithoutVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableForMockitoVerifyWithVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        Mockito.ve<caret>rify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Action selection options

    @Test
    void testReturnsAvailableActionsWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Mockito.ve<caret>rify(Mockito.mock(Object.class)).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromMockitoVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertMockitoVerifyToInOrderVerifyAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Mockito.ve<caret>rify(Mockito.mock(Object.class)).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromMockitoVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction.class,
            ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction.class
        );
    }

    @Test
    void testReturnsAvailableActionsWhenNothingIsEnforced() {
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Mockito.ve<caret>rify(Mockito.mock(Object.class)).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromMockitoVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertMockitoVerifyToInOrderVerifyAction.class,
            ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction.class,
            ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction.class
        );
    }
}
