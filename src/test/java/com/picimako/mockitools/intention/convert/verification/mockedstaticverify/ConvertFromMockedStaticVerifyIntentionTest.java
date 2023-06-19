//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for {@link ConvertFromMockedStaticVerifyIntention}.
 */
@RunsInEdt
class ConvertFromMockedStaticVerifyIntentionTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromMockedStaticVerifyIntention();
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
    void testAvailableOnNonVerifyMethod() {
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
    void testAvailableOnNonMockedStaticVerifyMethod() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        Mockito.veri<caret>fy(mock);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnMockedStaticVerifyMethod() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            mock.veri<caret>fy(List::of);\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnMockedStaticVerifyWithVerificationModeMethod() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class Available {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            mock.ve<caret>rify(List::of, Mockito.times(2));\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    //Action selection options

    @Test
    void testReturnsAvailableActions() {
        getFixture().configureByText("Options.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "class Options {\n" +
                "    void testMethod(){\n" +
                "        Mockito.ve<caret>rify(Mockito.mock(Object.class)).doSomething();\n" +
                "    }\n" +
                "}");
        List<Class<?>> actions = new ConvertFromMockedStaticVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertMockedStaticVerifyToInOrderVerifyAction.class);
    }
}
