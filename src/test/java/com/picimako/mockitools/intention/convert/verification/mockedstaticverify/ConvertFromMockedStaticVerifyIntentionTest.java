//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Integration test for {@link ConvertFromMockedStaticVerifyIntention}.
 */
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
            """
                class NotAvailable {
                    private String fiel<caret>d;
                }""");
    }

    @Test
    void testAvailableOnNonVerifyMethod() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        Mockito.mo<caret>ck(Object.class);
                    }
                }""");
    }

    @Test
    void testAvailableOnNonMockedStaticVerifyMethod() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                class NotAvailable {
                    void testMethod(){
                        Object mock = Mockito.mock(Object.class);
                        Mockito.veri<caret>fy(mock);
                    }
                }""");
    }

    @Test
    void testAvailableOnMockedStaticVerifyMethod() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class Available {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            mock.veri<caret>fy(List::of);
                        }    }
                }""");
    }

    @Test
    void testAvailableOnMockedStaticVerifyWithVerificationModeMethod() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class Available {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            mock.ve<caret>rify(List::of, Mockito.times(2));
                        }    }
                }""");
    }

    //Action selection options

    @Test
    void testReturnsAvailableActions() {
        getFixture().configureByText("Options.java",
            """
                import org.mockito.Mockito;

                class Options {
                    void testMethod(){
                        Mockito.ve<caret>rify(Mockito.mock(Object.class)).doSomething();
                    }
                }""");
        List<Class<?>> actions = new ConvertFromMockedStaticVerifyIntention().actionSelectionOptions(getFixture().getEditor(), getFixture().getFile())
            .stream().map(Object::getClass).collect(toList());

        assertThat(actions).containsExactly(ConvertMockedStaticVerifyToInOrderVerifyAction.class);
    }
}
