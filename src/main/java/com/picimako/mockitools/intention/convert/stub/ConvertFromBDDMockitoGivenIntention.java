//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoGiven;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.BDDMOCKITO_WILL;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.MOCKITO_DO;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.MOCKITO_WHEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Converts stubbing call chains from the {@code BDDMockito.given().will*()} approach.
 * <p>
 * The reason for excluding chains containing {@code will()} calls is that it doesn't have a matching method in the
 * {@code Mockito.do*().when()} approach.
 *
 * @since 0.6.0
 */
public class ConvertFromBDDMockitoGivenIntention extends ConvertStubbingIntentionBase {

    public ConvertFromBDDMockitoGivenIntention() {
        super("BDDMockito.given()", ORG_MOCKITO_BDDMOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return GIVEN.equals(getMethodName(methodCall))
            && isBDDMockitoGiven(methodCall)
            && hasSubsequentMethodCall(methodCall);
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file)) {
            if (doAllCallChainsMatch(DOESNT_CONTAIN_WILL, isBulkMode, editor, file)) {
                actions.add(new ConvertStubbingAction(ConvertStubbingAction.BDDMOCKITO_GIVEN, MOCKITO_DO, isBulkMode));
            }
            actions.add(new ConvertStubbingAction(ConvertStubbingAction.BDDMOCKITO_GIVEN, MOCKITO_WHEN, isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertStubbingAction(ConvertStubbingAction.BDDMOCKITO_GIVEN, BDDMOCKITO_WILL, isBulkMode));
        }
        return actions;
    }
}
