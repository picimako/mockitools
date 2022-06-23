//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.ListPopupHelper;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base class for intention actions that convert call chains between the different verification approaches.
 *
 * @see com.picimako.mockitools.intention.convert.verification.mockitoverify.ConvertFromMockitoVerifyIntention
 * @see com.picimako.mockitools.intention.convert.verification.bddmockitothen.ConvertFromBDDMockitoIntention
 * @since 0.4.0
 */
public abstract class ConvertVerificationIntentionBase implements IntentionAction {

    private final String sourceApproachName;

    protected ConvertVerificationIntentionBase(String sourceApproachName) {
        this.sourceApproachName = sourceApproachName;
    }

    //Intention names

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.convert.verification.to.x", sourceApproachName);
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.verification.x.to.y.family", sourceApproachName);
    }

    //Availability

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!file.getFileType().equals(JavaFileType.INSTANCE)) return false;

        final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (isIdentifierOfMethodCall(element)) {
            var methodCall = (PsiMethodCallExpression) element.getParent().getParent();
            return isAvailableFor(methodCall);
        }
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        ListPopupHelper.showActionsInListPopup(MockitoolsBundle.message("intention.convert.verification.select.target"), actionSelectionOptions(project, editor, file), editor);
    }

    /**
     * Returns the list of actions that should be displayed as possible conversion options.
     * <p>
     * If there is no option, it should return a single-item collection containing {@link NoActionAvailableAction#INSTANCE}.
     * <p>
     * Visibility is public, so that it is available in tests.
     */
    public abstract List<AnAction> actionSelectionOptions(Project project, Editor editor, PsiFile file);

    /**
     * Returns if this intention should be available for the provided method call.
     */
    protected abstract boolean isAvailableFor(PsiMethodCallExpression methodCall);

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
