//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.intellij.psi.util.PsiTreeUtil.getPrevSiblingOfType;
import static com.intellij.util.text.CharArrayUtil.containsOnlyWhiteSpaces;
import static com.picimako.mockitools.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.FromSelectionDataRetriever.selectionLengthIn;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallForIdentifier;
import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;
import static com.picimako.mockitools.Ranges.charSequenceInRange;
import static com.picimako.mockitools.TokenTypes.isTokenType;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.IncorrectOperationException;
import com.picimako.mockitools.ListPopupHelper;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

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

        if (!editor.getSelectionModel().hasSelection()) {
            final PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
            return isIdentifierOfMethodCall(element) && isAvailableFor(getMethodCallForIdentifier(element));
        } else return isAvailableForBulkConversion(editor, file);
    }

    /**
     * Returns whether this intention will be available for selection based conversion.
     * <p>
     * Selections shorter than 15 characters, and ones containing only whitespaces, are not considered a suitable selection.
     */
    private boolean isAvailableForBulkConversion(Editor editor, PsiFile file) {
        var model = editor.getSelectionModel();
        int selectionStart = model.getSelectionStart();
        int selectionEnd = model.getSelectionEnd();

        //The shortest option is selecting 'x.verify(y).z();'. This is to prevent executing further logic for unnecessarily short selections.
        if (selectionLengthIn(model) < 15
            || containsOnlyWhiteSpaces(charSequenceInRange(editor, selectionStart, selectionEnd))
            || !isSymbolAfterMethodCallChain(file.findElementAt(selectionEnd)))
            return false;

        for (var statement : collectStatementsInSelection(editor, file)) {
            var identifier = findChildOfType(statement, PsiIdentifier.class);
            if (identifier == null) return false;
            var verificationCall = getParentOfType(identifier, PsiMethodCallExpression.class);
            if (verificationCall == null) return false;
            //If we find at least one call chain in the selection that doesn't satisfy the current intention's availability criteria,
            //then the intention won't be available.
            if (!isCorrectIdentifier(identifier, verificationCall) || !isAvailableFor(verificationCall)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if the argument identifier is the same as the currently inspected call chain's qualifier expression,
     * and that the call chain has the proper type of qualifier expression.
     */
    private boolean isCorrectIdentifier(@NotNull PsiElement identifier, @NotNull PsiMethodCallExpression verificationCall) {
        var qualifier = verificationCall.getMethodExpression().getQualifierExpression();
        return PsiManager.getInstance(identifier.getProject()).areElementsEquivalent(identifier.getParent(), qualifier)
            && isQualifierHaveCorrectType(qualifier);
    }

    /**
     * Returns if the element is a whitespace, or semicolon, succeeding a method call.
     */
    private boolean isSymbolAfterMethodCallChain(PsiElement element) {
        return (element instanceof PsiWhiteSpace && getPrevSiblingOfType(element, PsiExpressionStatement.class) != null)
            || (isTokenType(element, JavaTokenType.SEMICOLON) && getPrevSiblingOfType(element, PsiMethodCallExpression.class) != null);
    }

    /**
     * Returns if this intention should be available for the provided method call.
     */
    public abstract boolean isAvailableFor(PsiMethodCallExpression methodCall);

    /**
     * Returns whether the argument qualifier expression has the correct type. The qualifier may be a local variable
     * (in case of e.g. InOrder), or a class (in case of Mockito or BDDMockito).
     */
    protected abstract boolean isQualifierHaveCorrectType(PsiExpression qualifier);

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

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
