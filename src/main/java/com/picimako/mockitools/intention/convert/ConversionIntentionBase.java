//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.intellij.psi.util.PsiTreeUtil.getPrevSiblingOfType;
import static com.intellij.util.text.CharArrayUtil.containsOnlyWhiteSpaces;
import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.selectionLengthIn;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallForIdentifier;
import static com.picimako.mockitools.util.PsiMethodUtil.getQualifier;
import static com.picimako.mockitools.util.PsiMethodUtil.isIdentifierOfMethodCall;
import static com.picimako.mockitools.util.Ranges.charSequenceInRange;
import static com.picimako.mockitools.util.TokenTypes.isTokenType;

import com.intellij.codeInsight.intention.IntentionAction;
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
import com.picimako.mockitools.util.ListPopupHelper;
import com.picimako.mockitools.intention.convert.verification.NoActionAvailableAction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Base class for stubbing and verification conversion intention actions.
 *
 * @see com.picimako.mockitools.intention.convert.stub.ConvertStubbingIntentionBase
 * @see com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ConversionIntentionBase implements IntentionAction {
    protected static final List<AnAction> NO_ACTION_AVAILABLE = Collections.singletonList(NoActionAvailableAction.INSTANCE);
    protected final String sourceApproachName;
    private final int minSelectionLength;

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (compute(() -> !editor.getSelectionModel().hasSelection())) {
            final var element = file.findElementAt(compute(() -> editor.getCaretModel().getOffset()));
            return isIdentifierOfMethodCall(element) && isAvailableFor(getMethodCallForIdentifier(element));
        } else return isAvailableForBulkConversion(editor, file);
    }

    /**
     * Returns whether this intention will be available for selection based conversion.
     * <p>
     * Selections shorter than {@code minSelectionLength}, and ones containing only whitespaces, are not considered a suitable selection.
     */
    private boolean isAvailableForBulkConversion(Editor editor, PsiFile file) {
        var model = editor.getSelectionModel();
        int selectionStart = compute(model::getSelectionStart);
        int selectionEnd = compute(model::getSelectionEnd);

        //This is to prevent executing further logic for unnecessarily short selections.
        if (selectionLengthIn(model) < minSelectionLength
            || containsOnlyWhiteSpaces(charSequenceInRange(editor, selectionStart, selectionEnd))
            || !isSymbolAfterMethodCallChain(compute(() -> file.findElementAt(selectionEnd))))
            return false;

        var statementsInSelection = collectStatementsInSelection(editor, file);
        //If the selection is suitable enough, but it is considered as containing no statement, then the intention won't be available
        if (statementsInSelection.isEmpty()) return false;

        for (var statement : statementsInSelection) {
            var identifier = compute(() -> findChildOfType(statement, PsiIdentifier.class));
            if (identifier == null) return false;
            var verificationCall = compute(() -> getParentOfType(identifier, PsiMethodCallExpression.class));
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
        var qualifier = getQualifier(verificationCall);
        return PsiManager.getInstance(identifier.getProject()).areElementsEquivalent(compute(identifier::getParent), qualifier)
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

    /**
     * Returns the title of the list popup that is presented for this conversion to select the target approach.
     */
    protected abstract String approachSelectionListTitle();

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        ListPopupHelper.showActionsInListPopup(approachSelectionListTitle(), actionSelectionOptions(editor, file), editor);
    }

    /**
     * Returns the list of actions that should be displayed as possible conversion options.
     * <p>
     * If there is no option, it should return a single-item collection containing {@link NoActionAvailableAction#INSTANCE}.
     * <p>
     * Visibility is public, so that it is available in tests.
     */
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        return NO_ACTION_AVAILABLE;
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
