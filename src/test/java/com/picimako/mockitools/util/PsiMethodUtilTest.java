//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.MockitoolsTestBase;
import com.picimako.mockitools.util.PsiMethodUtil;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link PsiMethodUtil}.
 */
@RunsInEdt
class PsiMethodUtilTest extends MockitoolsTestBase {

    //hasOneArgument

    @Test
    void testHasOnlyOneArgument() {
        getFixture().configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class);\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasOneArgument(methodCall)).isTrue();
    }

    @Test
    void testDoesntHaveOnlyOneArgument() {
        getFixture().configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class, Mockito.withSettings());\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasOneArgument(methodCall)).isFalse();
    }

    //hasAtLeastOneArgument

    @Test
    void testDoesntHaveAtLeastOneArgumentForZero() {
        getFixture().configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>withSettings();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isFalse();
    }

    @Test
    void testHasAtLeastOneArgumentForOne() {
        getFixture().configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class);\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isTrue();
    }

    @Test
    void testHasAtLeastOneArgumentForMoreThanOne() {
        getFixture().configureByText("MultipleArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class MultipleArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class, Mockito.withSettings());\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isTrue();
    }

    @Test
    void testHasNoArgument() {
        getFixture().configureByText("NoArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NoArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isFalse();
    }

    //hasSubsequentMethodCall

    @Test
    void testHasSubsequentCall() {
        getFixture().configureByText("HasSubsequentCallTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class HasSubsequentCallTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>times(1).description(\"message\")).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasSubsequentMethodCall(methodCall)).isTrue();
    }

    @Test
    void testDoesntHaveSubsequentCallDueToNotMatchingParentPsi() {
        getFixture().configureByText("HasSubsequentCallNotMatchingParentTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class HasNoSubsequentCallTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>times(1)).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasSubsequentMethodCall(methodCall)).isFalse();
    }

    //getArguments

    @Test
    void testGetArguments() {
        getFixture().configureByText("GetArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class GetArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>verify(new Object(), times(1)).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.getArguments(methodCall)).hasSize(2);
    }

    @Test
    void testGetEmptyArguments() {
        getFixture().configureByText("GetEmptyArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.never;\n" +
                "\n" +
                "public class GetEmptyArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>never()).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.getArguments(methodCall)).isEmpty();
    }

    //getFirstArgument

    @Test
    void testGetFirstArgument() {
        getFixture().configureByText("GetArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class GetArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>verify(new Object(), times(1)).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.getFirstArgument(methodCall).getText()).isEqualTo("new Object()");
    }

    //deleteArguments

    @Test
    void testDeletesArguments() {
        getFixture().configureByText("DeletesArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.never;\n" +
                "\n" +
                "public class DeletesArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.ve<caret>rify(new Object(), never()).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
        WriteAction.run(() -> CommandProcessor.getInstance().executeCommand(getFixture().getProject(), () -> PsiMethodUtil.deleteArguments(methodCall), "Delete", "group.id"));

        assertThat(methodCall.getArgumentList().isEmpty()).isTrue();
    }

    //getSubsequentMethodCall

    @Test
    void testGetsSubsequentCall() {
        getFixture().configureByText("SubSequent.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class SubSequent {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.ve<caret>rify(new Object()).toString();\n" +
                "    }\n" +
                "}");

        var methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
        var subsequentMethodCall = PsiMethodUtil.getSubsequentMethodCall(methodCall);

        assertThat(subsequentMethodCall.getMethodExpression().getReferenceName()).isEqualTo("toString");
    }

    @Test
    void testDoesntGetSubsequentCallForNullArgument() {
        assertThat(PsiMethodUtil.getSubsequentMethodCall(null)).isNull();
    }

    @Test
    void testDoesntGetSubsequentCallWhenThereIsNoSubsequentCall() {
        getFixture().configureByText("SubSequent.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class SubSequent {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object()).toS<caret>tring();\n" +
                "    }\n" +
                "}");

        var methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
        var subsequentMethodCall = PsiMethodUtil.getSubsequentMethodCall(methodCall);

        assertThat(subsequentMethodCall).isNull();
    }

    //findCallUpwardsInChain

    @Test
    void testFindsCallUpwards() {
        getFixture().configureByText("FindUpwards.java",
            "public class FindUpwards {\n" +
                "    public void testMethod() {\n" +
                "        \"string\".substring(1).subS<caret>equence(2, 3).toString();\n" +
                "    }\n" +
                "}");


        var methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
        var foundCall = PsiMethodUtil.findCallUpwardsInChain(methodCall, "substring");

        assertThat(foundCall.get().getText()).isEqualTo("\"string\".substring(1)");
    }

    @Test
    void testDoesntFindNonExistentCallUpwards() {
        getFixture().configureByText("FindUpwards.java",
            "public class FindUpwards {\n" +
                "    public void testMethod() {\n" +
                "        \"string\".substring(1).subS<caret>equence(2, 3).toString();\n" +
                "    }\n" +
                "}");


        var methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
        var foundCall = PsiMethodUtil.findCallUpwardsInChain(methodCall, "toString");

        assertThat(foundCall).isEmpty();
    }

    //findCallDownwardsInChain

    @Test
    void testFindsCallDownwards() {
        getFixture().configureByText("FindDownwards.java",
            "public class FindDownwards {\n" +
                "    public void testMethod() {\n" +
                "        \"string\".substring(1).subS<caret>equence(2, 3).toString();\n" +
                "    }\n" +
                "}");


        var methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
        var foundCall = PsiMethodUtil.findCallDownwardsInChain(methodCall, "toString");

        assertThat(foundCall.get().getText()).isEqualTo("\"string\".substring(1).subSequence(2, 3).toString()");
    }

    @Test
    void testDoesntFindNonExistentCallDownwards() {
        getFixture().configureByText("FindDownwards.java",
            "public class FindDownwards {\n" +
                "    public void testMethod() {\n" +
                "        \"string\".substring(1).subS<caret>equence(2, 3).toString();\n" +
                "    }\n" +
                "}");


        var methodCall = (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
        var foundCall = PsiMethodUtil.findCallDownwardsInChain(methodCall, "substring");

        assertThat(foundCall).isEmpty();
    }
}
