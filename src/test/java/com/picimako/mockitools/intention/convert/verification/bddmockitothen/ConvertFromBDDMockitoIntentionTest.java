//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import com.picimako.mockitools.intention.convert.verification.NoActionAvailableAction;

import java.util.List;

/**
 * Integration test for {@link ConvertFromBDDMockitoIntention}.
 */
public class ConvertFromBDDMockitoIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromBDDMockitoIntention();
    }

    public void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    public void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            "class NotAvailable {\n" +
                "    private String fiel<caret>d;\n" +
                "}");
    }

    public void testNotAvailableOnNonBDDMockitoThenMethod() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenBDDMockitoThenHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.th<caret>en(mockObject);\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenSubsequentMethodCallToBDDMockitoThenIsNotShould() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.th<caret>en(mockObject).shouldHaveNoInteractions();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableWhenBDDMockitoThenShouldHasNoSubsequentMethodCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.th<caret>en(mockObject).should();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableWhenBDDMockitoIsEnforced() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        checkIntentionIsAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.th<caret>en(mockObject).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForBDDMockitoThenShouldWithoutVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.th<caret>en(mockObject).should().doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForBDDMockitoThenShouldWithVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        MockObject mockObject = Mockito.mock(MockObject.class);\n" +
                "        BDDMockito.th<caret>en(mockObject).should(Mockito.times(2)).doSomething();\n" +
                "    }\n" +
                "    private static class MockObject {\n" +
                "        public void doSomething() {\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    //Action selection options

    public void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedWithoutInOrder() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.th<caret>en(Mockito.mock(Object.class)).should().doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(getProject(), getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(AddInOrderToBDDMockitoAction.class);
    }

    public void testReturnsAvailableActionsWhenBDDMockitoIsEnforcedWithInOrder() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.BDD_MOCKITO);
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        BDDMockito.th<caret>en(mock).should(Mockito.inOrder(mock)).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(getProject(), getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(NoActionAvailableAction.class);
    }

    public void testReturnsAvailableActionsWhenMockitoIsEnforcedWithoutInOrder() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.th<caret>en(Mockito.mock(Object.class)).should().doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(getProject(), getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class
        );
    }

    public void testReturnsAvailableActionsWhenMockitoIsEnforcedWithInOrder() {
        addEnforceConventionInspection(EnforceConventionInspection.Convention.MOCKITO);
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.th<caret>en(mock).should(Mockito.inOrder(mock)).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(getProject(), getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class
        );
    }

    public void testReturnsAvailableActionsWhenNothingIsEnforcedWithoutInOrder() {
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        BDDMockito.th<caret>en(Mockito.mock(Object.class)).should().doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(getProject(), getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class,
            AddInOrderToBDDMockitoAction.class
        );
    }

    public void testReturnsAvailableActionsWhenNothingIsEnforcedWithInOrder() {
        myFixture.configureByText("Options.java",
            "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        BDDMockito.th<caret>en(mock).should(Mockito.inOrder(mock)).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromBDDMockitoIntention().actionSelectionOptions(getProject(), getEditor(), getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(
            ConvertBDDMockitoThenToMockitoVerifyAction.class,
            ConvertBDDMockitoThenToInOrderVerifyAction.class
        );
    }
}
