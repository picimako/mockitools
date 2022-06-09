package com.picimako.mockitools.intention;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiFile;
import com.picimako.mockitools.MockitoolsTestBase;

/**
 * Base class for testing intention actions.
 */
public abstract class MockitoolsIntentionTestBase extends MockitoolsTestBase {
    
    protected abstract IntentionAction getIntention();
    
    protected void checkIntentionIsAvailable(String text) {
        PsiFile psiFile = myFixture.configureByText("Available.java", text);

        assertThat(getIntention().isAvailable(getProject(), myFixture.getEditor(), psiFile))
            .withFailMessage(() -> "Intention is NOT available while it IS supposed to be.")
            .isTrue();
    }

    protected void checkIntentionIsNotAvailable(String filename, String text) {
        PsiFile psiFile = myFixture.configureByText(filename, text);

        assertThat(getIntention().isAvailable(getProject(), myFixture.getEditor(), psiFile))
            .withFailMessage(() -> "Intention IS available while it is NOT supposed to be.")
            .isFalse();
    }

    protected void checkIntentionIsNotAvailable(String text) {
        PsiFile psiFile = myFixture.configureByText("NotAvailable.java", text);

        assertThat(getIntention().isAvailable(getProject(), myFixture.getEditor(), psiFile))
            .withFailMessage(() -> "Intention IS available while it is NOT supposed to be.")
            .isFalse();
    }
    
    protected void checkIntentionRun(String beforeText, String afterText) {
        PsiFile psiFile = myFixture.configureByText("ConversionTest.java", beforeText);
        runIntentionOn(psiFile, getIntention());
        myFixture.checkResult(afterText);
    }

    protected void runIntentionOn(PsiFile psiFile, IntentionAction intention) {
        ReadAction.run(() ->
            CommandProcessor.getInstance().executeCommand(getProject(),
                () -> intention.invoke(getProject(), myFixture.getEditor(), psiFile), "Intention", ""));
    }
}
