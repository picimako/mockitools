//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.picimako.mockitools.EnforceConventionUtil.isBDDMockitoEnforced;
import static com.picimako.mockitools.EnforceConventionUtil.isMockitoEnforced;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_WILL;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubbingApproach;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts stubbing call chains from the {@code BDDMockito.given().will*()} approach.
 * <p>
 * The reason for excluding chains containing {@code will()} calls is that it doesn't have a matching method in the
 * {@code Mockito.do*().when()} approach.
 *
 * @since 0.6.0
 */
final class ConvertFromBDDMockitoGivenIntention extends ConvertStubbingIntentionBase {

    public ConvertFromBDDMockitoGivenIntention() {
        super("BDDMockito.given()", ORG_MOCKITO_BDDMOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return StubbingApproach.BDDMOCKITO_GIVEN.isStubbedBy(methodCall)
            && StubbingApproach.BDDMOCKITO_GIVEN.isValid(methodCall);
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = compute(() -> editor.getSelectionModel().hasSelection());
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
