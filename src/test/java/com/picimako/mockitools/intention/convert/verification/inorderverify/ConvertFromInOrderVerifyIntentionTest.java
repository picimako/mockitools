//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;

import java.util.List;

/**
 * Functional test for {@link ConvertFromInOrderVerifyIntention}.
 */
public class ConvertFromInOrderVerifyIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromInOrderVerifyIntention();
    }

    //Availability

    public void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    public void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    public void testNotAvailableOnNonInOrderVerifyMethod() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenInOrderVerifyHasNoSubsequentMethodCall() {
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

    public void testNotAvailableOnInOrderVerifyWithNoArgument() {
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

    public void testAvailableWhenMockitoIsEnforced() {
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

    public void testAvailableForInOrderVerifyWithoutVerificationMode() {
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

    public void testAvailableForInOrderVerifyWithVerificationMode() {
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

    //Action selection options

    public void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedMockitoIsNotEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        myFixture.configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mock);\n" +
                "        inorder.ve<caret>rify(mock).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction.class,
            ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction.class
        );
    }

    public void testReturnsAvailableActionsWhenBDDMockitoIsNotEnforcedMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        myFixture.configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mock);\n" +
                "        inorder.ve<caret>rify(mock).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockitoVerifyAction.class);
    }

    public void testReturnsAvailableActionsWhenNothingIsEnforced() {
        myFixture.configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mock);\n" +
                "        inorder.ve<caret>rify(mock).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertInOrderVerifyToMockitoVerifyAction.class,
            ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction.class,
            ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction.class
        );
    }
}
