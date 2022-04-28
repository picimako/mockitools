//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoThen;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoVerify;
import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Base class for intention actions that convert call chains between the different verification approaches.
 *
 * @see ConvertMockitoVerifyToBDDMockitoThenIntention
 * @since 0.4.0
 */
public abstract class ConvertVerificationIntentionBase implements IntentionAction {

    private final String sourceApproachName;
    private final String targetApproachName;

    protected ConvertVerificationIntentionBase(String sourceApproachName, String targetApproachName) {
        this.sourceApproachName = sourceApproachName;
        this.targetApproachName = targetApproachName;
    }

    //Intention names

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.convert.verification.to.x", targetApproachName);
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.verification.x.to.y.family", sourceApproachName, targetApproachName);
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

    /**
     * Returns if this intention should be available for the provided method call.
     */
    protected abstract boolean isAvailableFor(PsiMethodCallExpression methodCall);

    //Conversion

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        var methodCall = (PsiMethodCallExpression) element.getParent().getParent();

        VerificationConverter converter = new VerificationConverter(project, editor.getDocument(), file);

        if (isMockitoVerify(methodCall)) converter.convertToBDDMockito(methodCall);
        else if (isBDDMockitoThen(methodCall)) converter.convertToMockitoVerify(methodCall);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
