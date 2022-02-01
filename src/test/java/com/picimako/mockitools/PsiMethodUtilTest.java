//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiMethodCallExpression;

/**
 * Unit test for {@link PsiMethodUtil}.
 */
public class PsiMethodUtilTest extends MockitoolsTestBase {

    //hasOneArgument

    public void testHasOnlyOneArgument() {
        myFixture.configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class);\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasOneArgument(methodCall)).isTrue();
    }

    public void testDoesntHaveOnlyOneArgument() {
        myFixture.configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class, Mockito.withSettings());\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasOneArgument(methodCall)).isFalse();
    }
    
    //hasAtLeastOneArgument

    public void testDoesntHaveAtLeastOneArgumentForZero() {
        myFixture.configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>withSettings();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isFalse();
    }

    public void testHasAtLeastOneArgumentForOne() {
        myFixture.configureByText("OneArgumentTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class OneArgumentTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class);\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isTrue();
    }

    public void testHasAtLeastOneArgumentForMoreThanOne() {
        myFixture.configureByText("MultipleArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class MultipleArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock(Object.class, Mockito.withSettings());\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isTrue();
    }

    public void testHasNoArgument() {
        myFixture.configureByText("NoArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NoArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>mock();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasAtLeastOneArgument(methodCall)).isFalse();
    }

    //hasSubsequentMethodCall

    public void testHasSubsequentCall() {
        myFixture.configureByText("HasSubsequentCallTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class HasSubsequentCallTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>times(1).description(\"message\")).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasSubsequentMethodCall(methodCall)).isTrue();
    }

    public void testDoesntHaveSubsequentCallDueToNotMatchingParentPsi() {
        myFixture.configureByText("HasSubsequentCallNotMatchingParentTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class HasNoSubsequentCallTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>times(1)).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.hasSubsequentMethodCall(methodCall)).isFalse();
    }

    //getArguments

    public void testGetArguments() {
        myFixture.configureByText("GetArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class GetArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>verify(new Object(), times(1)).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.getArguments(methodCall)).hasSize(2);
    }

    public void testGetEmptyArguments() {
        myFixture.configureByText("GetEmptyArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.never;\n" +
                "\n" +
                "public class GetEmptyArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>never()).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.getArguments(methodCall)).isEmpty();
    }

    //getFirstArgument

    public void testGetFirstArgument() {
        myFixture.configureByText("GetArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "public class GetArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.<caret>verify(new Object(), times(1)).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();

        assertThat(PsiMethodUtil.getFirstArgument(methodCall).getText()).isEqualTo("new Object()");
    }

    //deleteArguments

    public void testDeletesArguments() {
        myFixture.configureByText("DeletesArgumentsTest.java",
            "import org.mockito.Mockito;\n" +
                "import static org.mockito.Mockito.never;\n" +
                "\n" +
                "public class DeletesArgumentsTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.ve<caret>rify(new Object(), never()).toString();\n" +
                "    }\n" +
                "}");

        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent().getParent();
        WriteAction.run(() -> CommandProcessor.getInstance().executeCommand(getProject(), () -> PsiMethodUtil.deleteArguments(methodCall), "Delete", "group.id"));

        assertThat(methodCall.getArgumentList().isEmpty()).isTrue();
    }
}
