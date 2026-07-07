package com.picimako.mockitools.intention;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.picimako.mockitools.MockitoolsTestBase;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for testing intention actions.
 */
public abstract class MockitoolsIntentionTestBase extends MockitoolsTestBase {

    /**
     * Returns the intention action instance that is the subject of this test.
     */
    protected abstract IntentionAction getIntention();

    //Availability

    /**
     * Configures a file with the provided {@code text}, and checks that the subject intention action
     * IS available in the context set up in it.
     */
    protected void checkIntentionIsAvailable(@NotNull String text) {
        var psiFile = getFixture().configureByText("Available.java", text);

        assertThat(getIntention().isAvailable(getProject(), getEditor(), psiFile))
            .withFailMessage(() -> "Intention is NOT available while it IS supposed to be.")
            .isTrue();
    }

    /**
     * Configures a file with the provided {@code filename} and {@code text}, and checks that the subject intention action
     * IS NOT available in the context set up in it.
     */
    protected void checkIntentionIsNotAvailable(@NotNull String filename, @NotNull String text) {
        var psiFile = getFixture().configureByText(filename, text);

        assertThat(getIntention().isAvailable(getProject(), getEditor(), psiFile))
            .withFailMessage(() -> "Intention IS available while it is NOT supposed to be.")
            .isFalse();
    }

    /**
     * Configures a file with the provided {@code filename} and {@code text}, and checks that the subject intention action
     * IS NOT available in the context set up in it.
     * <p>
     * Same as {@link #checkIntentionIsNotAvailable(String, String)} but the filename is fixed.
     */
    protected void checkIntentionIsNotAvailable(@NotNull String text) {
        checkIntentionIsNotAvailable("NotAvailable.java", text);
    }

    //Execution

    /**
     * Configures a file with the provided {@code beforeText}, executes the intention action, and checks
     * that the test file's content has changed to {@code afterText}.
     */
    protected void checkIntentionRun(@NotNull String beforeText, @NotNull String afterText) {
        var psiFile = getFixture().configureByText("ConversionTest.java", beforeText);
        getIntention().invoke(getProject(), getEditor(), psiFile);
        getFixture().checkResult(afterText);
    }

    /**
     * Configures a file with the provided {@code beforeText}, executes the intention action, and checks
     * that the test file's content has changed to {@code afterText}.
     * <p>
     * Essentially the same as {@link #checkIntentionRun(String, String)}, but this one invokes the intention on EDT
     * because there are cases when e.g. a list popup is displayed during the intention action's behaviour that requires
     * EDT as a caller.
     */
    protected void checkIntentionRunOnEdt(@NotNull String beforeText, @NotNull String afterText) {
        var psiFile = getFixture().configureByText("ConversionTest.java", beforeText);
        ApplicationManager.getApplication().invokeAndWait(() -> getIntention().invoke(getProject(), getEditor(), psiFile));
        getFixture().checkResult(afterText);
    }
}
