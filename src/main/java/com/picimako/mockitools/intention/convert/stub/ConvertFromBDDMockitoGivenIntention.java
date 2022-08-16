//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL;
import static com.picimako.mockitools.util.PsiMethodUtil.hasSubsequentMethodCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubbingApproach;

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
            && StubbingApproach.BDDMOCKITO_GIVEN.isStubbedBy(methodCall)
            && hasSubsequentMethodCall(methodCall);
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file)) {
            if (doAllCallChainsMatch(DOESNT_CONTAIN_WILL, isBulkMode, editor, file)) {
                actions.add(new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_GIVEN, StubbingApproach.MOCKITO_DO_X, isBulkMode));
            }
            actions.add(new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_GIVEN, StubbingApproach.MOCKITO_WHEN, isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_GIVEN, StubbingApproach.BDDMOCKITO_WILL_X, isBulkMode));
        }
        return actions;
    }
}
