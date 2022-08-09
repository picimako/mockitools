//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoDoX;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.picimako.mockitools.CallChainEndsWith.ENDS_WITH_WHEN;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.BDDMOCKITO_GIVEN;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.BDDMOCKITO_WILL;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.MOCKITO_DO;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.MOCKITO_WHEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_DO_NOTHING;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;

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
public class ConvertFromMockitoDoIntention extends ConvertStubbingIntentionBase {

    public ConvertFromMockitoDoIntention() {
        super("Mockito.do*()", ORG_MOCKITO_MOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return isMockitoDoX(methodCall) && ENDS_WITH_WHEN.analyze(collectCallsInChainFromFirst(methodCall));
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file) && doAllCallChainsMatch(DOESNT_CONTAIN_DO_NOTHING, isBulkMode, editor, file)) {
            actions.add(new ConvertStubbingAction(MOCKITO_DO, MOCKITO_WHEN, isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertStubbingAction(MOCKITO_DO, BDDMOCKITO_WILL, isBulkMode));
            if (doAllCallChainsMatch(DOESNT_CONTAIN_DO_NOTHING, isBulkMode, editor, file))
                actions.add(new ConvertStubbingAction(MOCKITO_DO, BDDMOCKITO_GIVEN, isBulkMode));
        }
        return actions;
    }
}
