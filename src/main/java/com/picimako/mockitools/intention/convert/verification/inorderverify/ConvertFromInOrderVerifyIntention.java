//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.IN_ORDER_VERIFY;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static com.siyeh.ig.psiutils.TypeUtils.typeEquals;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;

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
 * @see ConvertInOrderVerifyToMockitoVerifyAction
 * @see ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction
 * @see ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction
 * @since 0.5.0
 */
public class ConvertFromInOrderVerifyIntention extends ConvertVerificationIntentionBase {
    public ConvertFromInOrderVerifyIntention() {
        super("InOrder.verify()");
    }

    //Availability

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return VERIFY.equals(getMethodName(methodCall))
            && IN_ORDER_VERIFY.matches(methodCall)
            && hasSubsequentMethodCall(methodCall);
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
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file)) {
            actions.add(new ConvertInOrderVerifyToMockitoVerifyAction(isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(isBulkMode));
            actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction(isBulkMode));
        }
        return actions;
    }
}
