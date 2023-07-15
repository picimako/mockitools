//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.VerificationApproach.INORDER_VERIFY;
import static com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection.isMockitoEnforced;
import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallForIdentifier;
import static com.siyeh.ig.psiutils.TypeUtils.typeEquals;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.VerificationApproach;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Converts {@code InOrder.verify()} based verification to other approaches.
 * <p>
 * The intention is available on {@code InOrder.verify()}
 * <ul>
 *     <li>if non-MockedStatic specific, then when it is followed by a method call on the mock object</li>
 *     <li>if MockedStatic specific</li>
 * </ul>
 * <p>
 * It is also available when the users select one or more {@code InOrder.verify()} call chains that they want to convert.
 * <p>
 * Call chains when there is no separate {@code InOrder} local variable created, but {@code Mockito.inOrder(mockObject)}
 * is called inline, are not supported at the moment.
 *
 * @see ConvertInOrderVerifyToMockitoVerifyAction
 * @see ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction
 * @see ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction
 * @since 0.5.0
 */
public class ConvertFromInOrderVerifyIntention extends ConvertVerificationIntentionBase {
    public ConvertFromInOrderVerifyIntention() {
        super(INORDER_VERIFY);
    }

    //Availability

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return VerificationApproach.INORDER_VERIFY_MOCKED_STATIC.isVerifiedBy(methodCall)
            || (INORDER_VERIFY.isVerifiedBy(methodCall) && INORDER_VERIFY.isValid(methodCall));
    }

    /**
     * Returns whether the argument element is the PsiIdentifier of an {@code InOrder} type variable.
     */
    @Override
    protected boolean isQualifierHaveCorrectType(PsiExpression qualifier) {
        return typeEquals(ORG_MOCKITO_INORDER, qualifier.getType());
    }

    //Invocation

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();

        var verificationCall = !isBulkMode
            //the method call at the caret position. The identifier at this point cannot be null.
            ? getMethodCallForIdentifier(file.findElementAt(editor.getCaretModel().getOffset()))
            //the method call of the first selected statement
            : getParentOfType(findChildOfType(collectStatementsInSelection(editor, file).get(0), PsiIdentifier.class), PsiMethodCallExpression.class);

        if (INORDER_VERIFY.isVerifiedBy(verificationCall)) {
            var actions = new ArrayList<AnAction>(3);
            if (!isBDDMockitoEnforced(file)) {
                actions.add(new ConvertInOrderVerifyToMockitoVerifyAction(isBulkMode));
            }
            if (!isMockitoEnforced(file)) {
                actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(isBulkMode));
                actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction(isBulkMode));
            }
            return actions;
        } else {
            return Collections.singletonList(new ConvertInOrderVerifyToMockedStaticVerifyAction(isBulkMode));
        }
    }
}
