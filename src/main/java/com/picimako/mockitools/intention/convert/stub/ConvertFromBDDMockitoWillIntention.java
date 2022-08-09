//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoWillX;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.picimako.mockitools.CallChainEndsWith.ENDS_WITH_GIVEN;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.BDDMOCKITO_GIVEN;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.MOCKITO_DO;
import static com.picimako.mockitools.intention.convert.stub.ConvertStubbingAction.MOCKITO_WHEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL_DO_NOTHING;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;

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
public class ConvertFromBDDMockitoWillIntention extends ConvertStubbingIntentionBase {

    public ConvertFromBDDMockitoWillIntention() {
        super("BDDMockito.will*()", ORG_MOCKITO_BDDMOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return isBDDMockitoWillX(methodCall) && ENDS_WITH_GIVEN.analyze(collectCallsInChainFromFirst(methodCall));
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file)) {
            if (doAllCallChainsMatch(DOESNT_CONTAIN_WILL, isBulkMode, editor, file))
                actions.add(new ConvertStubbingAction(ConvertStubbingAction.BDDMOCKITO_WILL, MOCKITO_DO, isBulkMode));
            if (doAllCallChainsMatch(DOESNT_CONTAIN_WILL_DO_NOTHING, isBulkMode, editor, file))
                actions.add(new ConvertStubbingAction(ConvertStubbingAction.BDDMOCKITO_WILL, MOCKITO_WHEN, isBulkMode));
        }
        if (!isMockitoEnforced(file) && doAllCallChainsMatch(DOESNT_CONTAIN_WILL_DO_NOTHING, isBulkMode, editor, file)) {
            actions.add(new ConvertStubbingAction(ConvertStubbingAction.BDDMOCKITO_WILL, BDDMOCKITO_GIVEN, isBulkMode));
        }
        return !actions.isEmpty() ? actions : NO_ACTION_AVAILABLE;
    }
}
