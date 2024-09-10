//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.EnforceConventionUtil.isBDDMockitoEnforced;
import static com.picimako.mockitools.EnforceConventionUtil.isMockitoEnforced;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.StubbingApproach.MOCKITO_DO_X;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_DO_NOTHING;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubbingApproach;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts stubbing call chains from the {@code Mockito.do*().when*()} approach.
 * <p>
 * The reason for excluding chains containing {@code doNothing()} calls is that it doesn't have a matching method in the
 * {@code Mockito.when().then*()} and {@code BDDMockito.given().will*()} approaches.
 *
 * @since 0.6.0
 */
final class ConvertFromMockitoDoIntention extends ConvertStubbingIntentionBase {

    public ConvertFromMockitoDoIntention() {
        super("Mockito.do*()", ORG_MOCKITO_MOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return MOCKITO_DO_X.isAnyOfStubs(methodCall) && MOCKITO_DO_X.isValid(collectCallsInChainFromFirst(methodCall));
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = ReadAction.compute(() -> editor.getSelectionModel().hasSelection());
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file) && doAllCallChainsMatch(DOESNT_CONTAIN_DO_NOTHING, isBulkMode, editor, file)) {
            addConversion(actions, StubbingApproach.MOCKITO_WHEN, isBulkMode);
        }
        if (!isMockitoEnforced(file)) {
            addConversion(actions, StubbingApproach.BDDMOCKITO_WILL_X, isBulkMode);
            if (doAllCallChainsMatch(DOESNT_CONTAIN_DO_NOTHING, isBulkMode, editor, file))
                addConversion(actions, StubbingApproach.BDDMOCKITO_GIVEN, isBulkMode);
        }
        return actions;
    }

    private void addConversion(List<AnAction> actions, StubbingApproach targetApproach, boolean isBulkMode) {
        actions.add(new ConvertStubbingAction(MOCKITO_DO_X, targetApproach, isBulkMode));
    }
}
