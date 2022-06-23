//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO_THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoThen;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.getSubsequentMethodCall;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.picimako.mockitools.intention.convert.verification.NoActionAvailableAction;
import com.siyeh.ig.callMatcher.CallMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts {@code BDDMockito.then()} based verification to other approaches.
 * <p>
 * The intention is available on {@code BDDMockito.then()} when it is followed by {@code should()} and a method call on the mock object.
 *
 * @since 0.5.0
 */
public class ConvertFromBDDMockitoIntention extends ConvertVerificationIntentionBase {
    private static final CallMatcher.Simple SHOULD = instanceCall(ORG_MOCKITO_BDDMOCKITO_THEN, "should");
    static final CallMatcher THEN_SHOULD_WITHOUT_INORDER = CallMatcher.anyOf(
        SHOULD.parameterCount(0),
        SHOULD.parameterTypes(ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE));
    private static final CallMatcher THEN_SHOULD = CallMatcher.anyOf(
        THEN_SHOULD_WITHOUT_INORDER,
        SHOULD.parameterTypes(ORG_MOCKITO_INORDER),
        SHOULD.parameterTypes(ORG_MOCKITO_INORDER, ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE)
    );

    public ConvertFromBDDMockitoIntention() {
        super("BDDMockito.then()");
    }

    @Override
    protected boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        if (THEN.equals(getMethodName(methodCall)) && isBDDMockitoThen(methodCall)) {
            var should = getSubsequentMethodCall(methodCall);
            return THEN_SHOULD.matches(should) && hasSubsequentMethodCall(should);
        }
        return false;
    }

    @Override
    public List<AnAction> actionSelectionOptions(Project project, Editor editor, PsiFile file) {
        var actions = new ArrayList<AnAction>(3);
        var then = getMethodCallAtCaret(file, editor);
        var should = getSubsequentMethodCall(then);
        if (!isBDDMockitoEnforced(then)) {
            actions.add(new ConvertBDDMockitoThenToMockitoVerifyAction(project, editor.getDocument(), file));
            actions.add(new ConvertBDDMockitoThenToInOrderVerifyAction(project, editor.getDocument(), file));
        }
        if (!isMockitoEnforced(then) && THEN_SHOULD_WITHOUT_INORDER.matches(should)) {
            actions.add(new AddInOrderToBDDMockitoAction(project, editor.getDocument(), file));
        }
        if (actions.isEmpty()) actions.add(NoActionAvailableAction.INSTANCE);
        return actions;
    }
}
