//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import static com.intellij.openapi.application.ReadAction.compute;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MockitoolsTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link PsiMethodUtil}.
 */
class PsiMethodUtilTest extends MockitoolsTestBase {

    //hasOneArgument

    @Test
    void testHasOnlyOneArgument() {
        getFixture().configureByText("OneArgumentTest.java",
            """
                import org.mockito.Mockito;

                public class OneArgumentTest {
                    public void testMethod() {
                        Mockito.<caret>mock(Object.class);
                    }
                }""");

        assertThat(PsiMethodUtil.hasOneArgument(getMethodCall())).isTrue();
    }

    @Test
    void testDoesntHaveOnlyOneArgument() {
        getFixture().configureByText("OneArgumentTest.java",
            """
                import org.mockito.Mockito;

                public class OneArgumentTest {
                    public void testMethod() {
                        Mockito.<caret>mock(Object.class, Mockito.withSettings());
                    }
                }""");

        assertThat(PsiMethodUtil.hasOneArgument(getMethodCall())).isFalse();
    }

    //hasArgument

    @Test
    void testDoesntHaveAtLeastOneArgumentForZero() {
        getFixture().configureByText("OneArgumentTest.java",
            """
                import org.mockito.Mockito;

                public class OneArgumentTest {
                    public void testMethod() {
                        Mockito.<caret>withSettings();
                    }
                }""");

        assertThat(PsiMethodUtil.hasArgument(getMethodCall())).isFalse();
    }

    @Test
    void testHasAtLeastOneArgumentForOne() {
        getFixture().configureByText("OneArgumentTest.java",
            """
                import org.mockito.Mockito;

                public class OneArgumentTest {
                    public void testMethod() {
                        Mockito.<caret>mock(Object.class);
                    }
                }""");

        assertThat(PsiMethodUtil.hasArgument(getMethodCall())).isTrue();
    }

    @Test
    void testHasAtLeastOneArgumentForMoreThanOne() {
        getFixture().configureByText("MultipleArgumentsTest.java",
            """
                import org.mockito.Mockito;

                public class MultipleArgumentsTest {
                    public void testMethod() {
                        Mockito.<caret>mock(Object.class, Mockito.withSettings());
                    }
                }""");

        assertThat(PsiMethodUtil.hasArgument(getMethodCall())).isTrue();
    }

    @Test
    void testHasNoArgument() {
        getFixture().configureByText("NoArgumentsTest.java",
            """
                import org.mockito.Mockito;

                public class NoArgumentsTest {
                    public void testMethod() {
                        Mockito.<caret>mock();
                    }
                }""");

        assertThat(PsiMethodUtil.hasArgument(getMethodCall())).isFalse();
    }

    //hasSubsequentMethodCall

    @Test
    void testHasSubsequentCall() {
        getFixture().configureByText("HasSubsequentCallTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.Mockito.times;

                public class HasSubsequentCallTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), <caret>times(1).description("message")).toString();
                    }
                }""");

        assertThat(PsiMethodUtil.hasSubsequentMethodCall(getMethodCall())).isTrue();
    }

    @Test
    void testDoesntHaveSubsequentCallDueToNotMatchingParentPsi() {
        getFixture().configureByText("HasSubsequentCallNotMatchingParentTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.Mockito.times;

                public class HasNoSubsequentCallTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), <caret>times(1)).toString();
                    }
                }""");

        assertThat(PsiMethodUtil.hasSubsequentMethodCall(getMethodCall())).isFalse();
    }

    //getArguments

    @Test
    void testGetArguments() {
        getFixture().configureByText("GetArgumentsTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.Mockito.times;

                public class GetArgumentsTest {
                    public void testMethod() {
                        Mockito.<caret>verify(new Object(), times(1)).toString();
                    }
                }""");

        assertThat(PsiMethodUtil.getArguments(getMethodCall())).hasSize(2);
    }

    @Test
    void testGetEmptyArguments() {
        getFixture().configureByText("GetEmptyArgumentsTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.Mockito.never;

                public class GetEmptyArgumentsTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), <caret>never()).toString();
                    }
                }""");

        assertThat(PsiMethodUtil.getArguments(getMethodCall())).isEmpty();
    }

    //getFirstArgument

    @Test
    void testGetFirstArgument() {
        getFixture().configureByText("GetArgumentsTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.Mockito.times;

                public class GetArgumentsTest {
                    public void testMethod() {
                        Mockito.<caret>verify(new Object(), times(1)).toString();
                    }
                }""");

        assertThat(compute(() -> PsiMethodUtil.getFirstArgument(getMethodCall()).getText())).isEqualTo("new Object()");
    }

    //deleteArguments

    @Test
    void testDeletesArguments() {
        getFixture().configureByText("DeletesArgumentsTest.java",
            """
                import org.mockito.Mockito;
                import static org.mockito.Mockito.never;

                public class DeletesArgumentsTest {
                    public void testMethod() {
                        Mockito.ve<caret>rify(new Object(), never()).toString();
                    }
                }""");

        PsiMethodCallExpression methodCall = getMethodCall();
        ApplicationManager.getApplication().invokeAndWait(() ->
            WriteAction.run(() ->
                CommandProcessor.getInstance().executeCommand(
                    getFixture().getProject(),
                    () -> PsiMethodUtil.deleteArguments(methodCall), "Delete", "group.id")));

        assertThat(compute(() -> methodCall.getArgumentList().isEmpty())).isTrue();
    }

    //getSubsequentMethodCall

    @Test
    void testGetsSubsequentCall() {
        getFixture().configureByText("SubSequent.java",
            """
                import org.mockito.Mockito;

                public class SubSequent {
                    public void testMethod() {
                        Mockito.ve<caret>rify(new Object()).toString();
                    }
                }""");

        var subsequentMethodCall = PsiMethodUtil.getSubsequentMethodCall(getMethodCall());

        assertThat(subsequentMethodCall.getMethodExpression().getReferenceName()).isEqualTo("toString");
    }

    @Test
    void testDoesntGetSubsequentCallForNullArgument() {
        assertThat(PsiMethodUtil.getSubsequentMethodCall(null)).isNull();
    }

    @Test
    void testDoesntGetSubsequentCallWhenThereIsNoSubsequentCall() {
        getFixture().configureByText("SubSequent.java",
            """
                import org.mockito.Mockito;

                public class SubSequent {
                    public void testMethod() {
                        Mockito.verify(new Object()).toS<caret>tring();
                    }
                }""");

        var subsequentMethodCall = PsiMethodUtil.getSubsequentMethodCall(getMethodCall());

        assertThat(subsequentMethodCall).isNull();
    }

    //findCallUpwardsInChain

    @Test
    void testFindsCallUpwards() {
        getFixture().configureByText("FindUpwards.java",
            """
                public class FindUpwards {
                    public void testMethod() {
                        "string".substring(1).subS<caret>equence(2, 3).toString();
                    }
                }""");


        var foundCall = PsiMethodUtil.findCallUpwardsInChain(getMethodCall(), "substring");

        assertThat(compute(() -> foundCall.get().getText())).isEqualTo("\"string\".substring(1)");
    }

    @Test
    void testDoesntFindNonExistentCallUpwards() {
        getFixture().configureByText("FindUpwards.java",
            """
                public class FindUpwards {
                    public void testMethod() {
                        "string".substring(1).subS<caret>equence(2, 3).toString();
                    }
                }""");


        var foundCall = PsiMethodUtil.findCallUpwardsInChain(getMethodCall(), "toString");

        assertThat(foundCall).isEmpty();
    }

    //findCallDownwardsInChain

    @Test
    void testFindsCallDownwards() {
        getFixture().configureByText("FindDownwards.java",
            """
                public class FindDownwards {
                    public void testMethod() {
                        "string".substring(1).subS<caret>equence(2, 3).toString();
                    }
                }""");


        var foundCall = PsiMethodUtil.findCallDownwardsInChain(getMethodCall(), "toString");

        assertThat(compute(() -> foundCall.get().getText())).isEqualTo("\"string\".substring(1).subSequence(2, 3).toString()");
    }

    @Test
    void testDoesntFindNonExistentCallDownwards() {
        getFixture().configureByText("FindDownwards.java",
            """
                public class FindDownwards {
                    public void testMethod() {
                        "string".substring(1).subS<caret>equence(2, 3).toString();
                    }
                }""");


        var foundCall = PsiMethodUtil.findCallDownwardsInChain(getMethodCall(), "substring");

        assertThat(foundCall).isEmpty();
    }

    //Helpers

    private PsiMethodCallExpression getMethodCall() {
        return compute(() -> (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent());
    }
}
