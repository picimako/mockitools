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
public class ConvertFromInOrderVerifyIntentionInBulkTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromInOrderVerifyIntention();
    }

    //Availability

    public void testNotAvailableWithSelectionShorterThanMinRequiredLength() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        <selection>Mockito</selection>.mock(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForOnlyWhitespaceSelection() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "<selection>                </selection>Mockito.mock(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForIncorrectSelectionEndDot() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>Mockito.inOrder(mockObject).</selection>verify(mockObject).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableOnNonInOrderVerifyChain() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        <selection>Mockito.mock(Object.class);</selection>\n" +
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
                "        <selection>inOrder.verify(mockObject);</selection>\n" +
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
                "        <selection>inOrder.verify();</selection>\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableOnMultipleMixedVerificationSelected() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();\n" +
                "        Mockito.verify(mockObject2).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
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
                "        <selection>inOrder.verify(mockObject).doSomething();</selection>\n" +
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
                "        <selection>inOrder.verify(mockObject).doSomething();</selection>\n" +
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
                "        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnSingleInOrderVerifySelected() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();</selection>\n" +
                "        inOrder.verify(mockObject2).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnMultipleInOrderVerifySelected() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject, mockObject2);\n" +
                "        <selection>inOrder.verify(mockObject, Mockito.times(2)).doSomething();\n" +
                "        inOrder.verify(mockObject2).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Action selection options - non-MockedStatic

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
                "        <selection>inOrder.verify(mock).doSomething();</selection>\n" +
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
                "        <selection>inOrder.verify(mock).doSomething();</selection>\n" +
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
                "        <selection>inOrder.verify(mock).doSomething();</selection>\n" +
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

    //Action selection options - MockedStatic

    public void testReturnsAvailableActionsForMockedStatic() {
        myFixture.configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::of);</selection>\n" +
                "        }" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockedStaticVerifyAction.class);
    }

    public void testReturnsAvailableActionsForMockedStaticWithVerificationMode() {
        myFixture.configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::copyOf, Mockito.times(3));</selection>\n" +
                "        }" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromInOrderVerifyIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertInOrderVerifyToMockedStaticVerifyAction.class);
    }
}
