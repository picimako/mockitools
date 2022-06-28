//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.intellij.psi.util.PsiTreeUtil.getNextSiblingOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallForIdentifier;
import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.ListPopupHelper;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Base class for intention actions that convert call chains between the different verification approaches.
 *
 * @see com.picimako.mockitools.intention.convert.verification.mockitoverify.ConvertFromMockitoVerifyIntention
 * @see com.picimako.mockitools.intention.convert.verification.bddmockitothen.ConvertFromBDDMockitoIntention
 * @see com.picimako.mockitools.intention.convert.verification.inorderverify.ConvertFromInOrderVerifyIntention
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
        return MockitoolsBundle.message("intention.convert.verification.to");
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
        return isIdentifierOfMethodCall(element) && isAvailableFor(getMethodCallForIdentifier(element));
    }

    /**
     * Returns if this intention should be available for the provided method call.
     */
    public abstract boolean isAvailableFor(PsiMethodCallExpression methodCall);

    //Invocation

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        ListPopupHelper.showActionsInListPopup(MockitoolsBundle.message("intention.convert.verification.select.target"), actionSelectionOptions(editor, file), editor);
    }

    /**
     * Returns the list of actions that should be displayed as possible conversion options.
     * <p>
     * If there is no option, it should return a single-item collection containing {@link NoActionAvailableAction#INSTANCE}.
     * <p>
     * Visibility is public, so that it is available in tests.
     */
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        return Collections.singletonList(NoActionAvailableAction.INSTANCE);
    }

    /**
     * It checks if the selection start is at whitespaces preceding a call chain, to improve user experience,
     * since the first expression doesn't have to be selected precisely.
     */
    @Nullable
    public static PsiExpressionStatement findFirstSelectedStatement(PsiFile file, int selectionStart) {
        var elementAtStart = file.findElementAt(selectionStart);
        if (elementAtStart instanceof PsiIdentifier)
            return getParentOfType(elementAtStart, PsiExpressionStatement.class);
        else if (elementAtStart instanceof PsiWhiteSpace)
            return getNextSiblingOfType(elementAtStart, PsiExpressionStatement.class);
        return null;
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
