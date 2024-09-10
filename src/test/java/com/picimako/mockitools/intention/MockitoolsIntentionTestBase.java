package com.picimako.mockitools.intention;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import com.picimako.mockitools.MockitoolsTestBase;

/**
 * Base class for testing intention actions.
 */
public abstract class MockitoolsIntentionTestBase extends MockitoolsTestBase {

    protected abstract IntentionAction getIntention();

    protected void checkIntentionIsAvailable(String text) {
        PsiFile psiFile = getFixture().configureByText("Available.java", text);

        assertThat(getIntention().isAvailable(getFixture().getProject(), getFixture().getEditor(), psiFile))
            .withFailMessage(() -> "Intention is NOT available while it IS supposed to be.")
            .isTrue();
    }

    protected void checkIntentionIsNotAvailable(String filename, String text) {
        PsiFile psiFile = getFixture().configureByText(filename, text);

        assertThat(getIntention().isAvailable(getFixture().getProject(), getFixture().getEditor(), psiFile))
            .withFailMessage(() -> "Intention IS available while it is NOT supposed to be.")
            .isFalse();
    }

    protected void checkIntentionIsNotAvailable(String text) {
        PsiFile psiFile = getFixture().configureByText("NotAvailable.java", text);

        assertThat(getIntention().isAvailable(getFixture().getProject(), getFixture().getEditor(), psiFile))
            .withFailMessage(() -> "Intention IS available while it is NOT supposed to be.")
            .isFalse();
    }

    protected void checkIntentionRun(String beforeText, String afterText) {
        PsiFile psiFile = getFixture().configureByText("ConversionTest.java", beforeText);
        getIntention().invoke(getFixture().getProject(), getFixture().getEditor(), psiFile);
        getFixture().checkResult(afterText);
    }

    /**
     * Invokes the intention on EDT when e.g. a list popup is displayed during the intention actions behaviour
     * that requires that thread as a caller.
     */
    protected void checkIntentionRunOnEdt(String beforeText, String afterText) {
        PsiFile psiFile = getFixture().configureByText("ConversionTest.java", beforeText);
        ApplicationManager.getApplication().invokeAndWait(() -> getIntention().invoke(getFixture().getProject(), getFixture().getEditor(), psiFile));
        getFixture().checkResult(afterText);
    }
}
