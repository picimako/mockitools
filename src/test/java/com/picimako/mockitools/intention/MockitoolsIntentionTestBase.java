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
    
    protected void checkIntentionIsAvailable(String filename, String text) {
        PsiFile psiFile = myFixture.configureByText(filename, text);

        assertThat(getIntention().isAvailable(getProject(), myFixture.getEditor(), psiFile)).isTrue();
    }

    protected void checkIntentionIsNotAvailable(String filename, String text) {
        PsiFile psiFile = myFixture.configureByText(filename, text);

        assertThat(getIntention().isAvailable(getProject(), myFixture.getEditor(), psiFile)).isFalse();
    }
    
    protected void checkIntentionRun(String filename, String beforeText, String afterText) {
        PsiFile psiFile = myFixture.configureByText(filename, beforeText);
        runIntentionOn(psiFile, getIntention());
        myFixture.checkResult(afterText);
    }

    protected void runIntentionOn(PsiFile psiFile, IntentionAction intention) {
        ReadAction.run(() ->
            CommandProcessor.getInstance().executeCommand(getProject(),
                () -> intention.invoke(getProject(), myFixture.getEditor(), psiFile), "Intention", ""));
    }
}
