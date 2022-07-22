//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoWhen;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.MOCKITO_WHEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_THEN;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts stubbing call chains from the {@code Mockito.when().then*()} approach.
 * <p>
 * The reason for excluding chains containing {@code then()} calls is that it doesn't have a matching method in the
 * {@code Mockito.do*().when()} approach.
 *
 * @since 0.6.0
 */
public class ConvertFromMockitoWhenIntention extends ConvertStubbingIntentionBase {

    public ConvertFromMockitoWhenIntention() {
        super("Mockito.when()", ORG_MOCKITO_MOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return WHEN.equals(getMethodName(methodCall))
            && isMockitoWhen(methodCall)
            && hasSubsequentMethodCall(methodCall);
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file) && doAllCallChainsMatch(DOESNT_CONTAIN_THEN, isBulkMode, editor, file)) {
            actions.add(new ConvertStubbingAction(MOCKITO_WHEN, ConvertStubbingAction.MOCKITO_DO, isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertStubbingAction(MOCKITO_WHEN, ConvertStubbingAction.BDDMOCKITO_GIVEN, isBulkMode));
            actions.add(new ConvertStubbingAction(MOCKITO_WHEN, ConvertStubbingAction.BDDMOCKITO_WILL, isBulkMode));
        }
        return actions;
    }
}
