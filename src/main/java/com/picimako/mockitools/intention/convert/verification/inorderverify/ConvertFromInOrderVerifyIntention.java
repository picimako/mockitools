//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getNextSiblingOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.intellij.psi.util.PsiTreeUtil.getPrevSiblingOfType;
import static com.intellij.util.text.CharArrayUtil.containsOnlyWhiteSpaces;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallForIdentifier;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.PsiMethodUtil.isIdentifierOfMethodCall;
import static com.picimako.mockitools.Ranges.charSequenceInRange;
import static com.picimako.mockitools.Ranges.endOffsetOf;
import static com.picimako.mockitools.Ranges.selectionLengthIn;
import static com.picimako.mockitools.TokenTypes.isTokenType;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.IN_ORDER_VERIFY;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static com.siyeh.ig.psiutils.TypeUtils.typeEquals;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiWhiteSpace;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts {@code InOrder.verify()} based verification to other approaches.
 * <p>
 * The intention is available on {@code InOrder.verify()} when it is followed by a method call on the mock object,
 * when the user wants to convert only a single verification call.
 * <p>
 * It is also available when the users select one or more {@code InOrder.verify()} call chains that they want to convert.
 * <p>
 * Call chains when there is no separate {@code InOrder} local variable is created, but {@code Mockito.inOrder(mockObject)}
 * is called inline, is not supported at the moment.
 *
 * @since 0.5.0
 */
public class ConvertFromInOrderVerifyIntention extends ConvertVerificationIntentionBase {
    public ConvertFromInOrderVerifyIntention() {
        super("InOrder.verify()");
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

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return VERIFY.equals(getMethodName(methodCall))
            && IN_ORDER_VERIFY.matches(methodCall)
            && hasSubsequentMethodCall(methodCall);
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

        var statement = findFirstSelectedStatement(file, selectionStart);
        while (statement != null && endOffsetOf(statement) <= selectionEnd) {
            var identifier = findChildOfType(statement, PsiIdentifier.class);
            if (!isInOrderVariableIdentifier(identifier) || !isAvailableFor(/*inOrderVerify*/getParentOfType(identifier, PsiMethodCallExpression.class))) {
                return false;
            }
            statement = getNextSiblingOfType(statement, PsiExpressionStatement.class);
        }
        return true;
    }

    /**
     * Returns whether the argument element is the PsiIdentifier of an {@code InOrder} type variable.
     */
    private boolean isInOrderVariableIdentifier(@Nullable PsiElement element) {
        if (element == null) return false;
        var methodCall = getParentOfType(element, PsiMethodCallExpression.class);
        if (methodCall == null) return false;
        var qualifier = methodCall.getMethodExpression().getQualifierExpression();
        return PsiManager.getInstance(element.getProject()).areElementsEquivalent(element.getParent(), qualifier)
            && typeEquals(ORG_MOCKITO_INORDER, qualifier.getType());
    }

    /**
     * Returns if the element is a whitespace, or semicolon, succeeding a method call.
     */
    private boolean isSymbolAfterMethodCallChain(PsiElement element) {
        return (element instanceof PsiWhiteSpace && getPrevSiblingOfType(element, PsiExpressionStatement.class) != null)
            || (isTokenType(element, JavaTokenType.SEMICOLON) && getPrevSiblingOfType(element, PsiMethodCallExpression.class) != null);
    }

    //Invocation

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file)) {
            actions.add(new ConvertInOrderVerifyToMockitoVerifyAction(editor, isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(editor, isBulkMode));
            actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction(editor, isBulkMode));
        }
        return actions;
    }
}
