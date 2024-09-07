//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.picimako.mockitools.EnforceConventionUtil.isBDDMockitoEnforced;
import static com.picimako.mockitools.EnforceConventionUtil.isMockitoEnforced;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL_DO_NOTHING;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubbingApproach;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts stubbing call chains from the {@code BDDMockito.will*().given()} approach.
 * <p>
 * The reason for excluding chains containing {@code will()} and {@code willDoNothing()} calls is that they don't have
 * matching methods in the rest of the approaches.
 *
 * @since 0.6.0
 */
final class ConvertFromBDDMockitoWillIntention extends ConvertStubbingIntentionBase {

    public ConvertFromBDDMockitoWillIntention() {
        super("BDDMockito.will*()", ORG_MOCKITO_BDDMOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return StubbingApproach.BDDMOCKITO_WILL_X.isAnyOfStubs(methodCall) && StubbingApproach.BDDMOCKITO_WILL_X.isValid(collectCallsInChainFromFirst(methodCall));
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = compute(() -> editor.getSelectionModel().hasSelection());
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file)) {
            if (doAllCallChainsMatch(DOESNT_CONTAIN_WILL, isBulkMode, editor, file))
                actions.add(new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_WILL_X, StubbingApproach.MOCKITO_DO_X, isBulkMode));
            if (doAllCallChainsMatch(DOESNT_CONTAIN_WILL_DO_NOTHING, isBulkMode, editor, file))
                actions.add(new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_WILL_X, StubbingApproach.MOCKITO_WHEN, isBulkMode));
        }
        if (!isMockitoEnforced(file) && doAllCallChainsMatch(DOESNT_CONTAIN_WILL_DO_NOTHING, isBulkMode, editor, file)) {
            actions.add(new ConvertStubbingAction(StubbingApproach.BDDMOCKITO_WILL_X, StubbingApproach.BDDMOCKITO_GIVEN, isBulkMode));
        }
        return !actions.isEmpty() ? actions : NO_ACTION_AVAILABLE;
    }
}
