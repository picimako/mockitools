//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Functional test for {@link ConvertFromInOrderVerifyIntention}.
 */
@RunsInEdt
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
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    @Test
    void testNotAvailableOnNonInOrderVerifyMethod() {
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
    void testNotAvailableWhenInOrderVerifyHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.ve<caret>rify(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableOnInOrderVerifyWithNoArgument() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.ve<caret>rify();\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.ve<caret>rify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableForInOrderVerifyWithoutVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.ve<caret>rify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableForInOrderVerifyWithVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.ve<caret>rify(mockObject, Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Action selection options - non-MockedStatic

    @Test
    void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedMockitoIsNotEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mock);\n" +
                "        inOrder.ve<caret>rify(mock).doSomething();\n" +
                "    }\n" +
                "}");
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
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mock);\n" +
                "        inOrder.ve<caret>rify(mock).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockitoVerifyAction.class);
    }

    @Test
    void testReturnsAvailableActionsWhenNothingIsEnforced() {
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mock);\n" +
                "        inOrder.ve<caret>rify(mock).doSomething();\n" +
                "    }\n" +
                "}");
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
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            mock.ve<caret>rify(List::of);\n" +
                "        }" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockedStaticVerifyAction.class);
    }

    @Test
    void testReturnsAvailableActionsForMockedStaticWithVerificationMode() {
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            mock.ver<caret>ify(List::copyOf, Mockito.times(3));\n" +
                "        }" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockedStaticVerifyAction.class);
    }
}
