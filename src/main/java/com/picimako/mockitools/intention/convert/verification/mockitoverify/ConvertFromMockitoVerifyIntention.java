//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoVerify;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts {@code Mockito.verify()} based verification to other approaches.
 * <p>
 * The intention is available on {@code Mockito.verify()} when it is followed by a method call on the mock object.
 *
 * @see ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction
 * @see ConvertMockitoVerifyToInOrderVerifyAction
 *
 * @since 0.5.0
 */
public class ConvertFromMockitoVerifyIntention extends ConvertVerificationIntentionBase {

    public ConvertFromMockitoVerifyIntention() {
        super("Mockito.verify()");
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return VERIFY.equals(getMethodName(methodCall))
            && isMockitoVerify(methodCall)
            && hasSubsequentMethodCall(methodCall);
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        var actions = new ArrayList<AnAction>(2);
        var mockitoVerify = getMethodCallAtCaret(file, editor);
        if (!isBDDMockitoEnforced(mockitoVerify)) {
            actions.add(new ConvertMockitoVerifyToInOrderVerifyAction(editor));
        }
        if (!isMockitoEnforced(mockitoVerify)) {
            actions.add(new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(editor));
            actions.add(new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(editor));
        }
        return actions;
    }
}
