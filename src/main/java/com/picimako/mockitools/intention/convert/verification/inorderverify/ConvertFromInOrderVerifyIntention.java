//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MockitoolsPsiUtil;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.siyeh.ig.callMatcher.CallMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts {@code InOrder.verify()} based verification to other approaches.
 * <p>
 * The intention is available on {@code InOrder.verify()} when it is followed by a method call on the mock object.
 *
 * @since 0.5.0
 */
public class ConvertFromInOrderVerifyIntention extends ConvertVerificationIntentionBase {

    private static final CallMatcher IN_ORDER_VERIFY = CallMatcher.anyOf(
        MockitoolsPsiUtil.INORDER_VERIFY.parameterCount(1),
        MockitoolsPsiUtil.INORDER_VERIFY.parameterCount(2)
    );

    public ConvertFromInOrderVerifyIntention() {
        super("InOrder.verify()");
    }

    @Override
    protected boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return VERIFY.equals(getMethodName(methodCall))
            && IN_ORDER_VERIFY.matches(methodCall)
            && hasSubsequentMethodCall(methodCall);
    }

    @Override
    public List<AnAction> actionSelectionOptions(Project project, Editor editor, PsiFile file) {
        var actions = new ArrayList<AnAction>(3);
        var inOrderVerify = getMethodCallAtCaret(file, editor);
        if (!isBDDMockitoEnforced(inOrderVerify)) {
            actions.add(new ConvertInOrderVerifyToMockitoVerifyAction(project, editor.getDocument(), file));
        }
        if (!isMockitoEnforced(inOrderVerify)) {
            actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithoutInOrderAction(project, editor.getDocument(), file));
            actions.add(new ConvertInOrderVerifyToBDDMockitoThenWithInOrderAction(project, editor.getDocument(), file));
        }
        return actions;
    }
}
