//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;

import java.util.List;

/**
 * Integration test for {@link ConvertFromBDDMockitoIntention}.
 */
public class ConvertFromBDDMockitoIntentionInBulkTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromBDDMockitoIntention();
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
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject).</selection>should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableOnNonBDDMockitoThenChain() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        <selection>Mockito.mock(Object.class);</selection>\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenBDDMockitoThenHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject);</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableOnBDDMockitoThenWithNoArgument() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then().should();</selection>\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableOnMultipleMixedVerificationSelected() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "        Mockito.verify(mockObject2).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableWhenMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject).should().doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForBDDMockitoThenWithoutVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject).should().doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForBDDMockitoThenWithVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnSingleBDDMockitoThenSelected() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();</selection>\n" +
                "        BDDMockito.then(mockObject2).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableOnMultipleBDDMockitoThenSelected() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.BDDMockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        MockObject mockObject2 = Mockito.mock(MockObject.class);\n" +
                "        <selection>BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "        BDDMockito.then(mockObject2).should().doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Action selection options

    public void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedAllVerificationsWithoutInOrder() {
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        <selection>BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();\n" +
                "        BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class,
            AddInOrderToBDDMockitoAction.class);
    }

    public void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedAllVerificationsWithInOrder() {
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        <selection>BDDMockito.then(mockObject).should(inOrder).doSomething();\n" +
                "        BDDMockito.then(mockObject).should(inOrder).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class);
    }

    public void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedVerificationsMixedWithAndWithoutInOrder() {
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        <selection>BDDMockito.then(mockObject).should(inOrder).doSomething();\n" +
                "        BDDMockito.then(Mockito.mock(Object.class)).should().doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertBDDMockitoThenToMockitoVerifyAction.class);
    }

    public void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedVerificationsMultipleDifferentInOrders() {
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        InOrder inOrde2 = Mockito.inOrder(mockObject);\n" +
                "        <selection>BDDMockito.then(mockObject).should(inOrder).doSomething();\n" +
                "        BDDMockito.then(mockObject).should(inOrder2).doSomething();</selection>\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(myFixture.getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertBDDMockitoThenToMockitoVerifyAction.class);
    }
}
