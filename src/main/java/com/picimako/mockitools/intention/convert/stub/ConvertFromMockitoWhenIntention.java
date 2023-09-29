//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.stub;

import static com.picimako.mockitools.EnforceConventionUtil.isBDDMockitoEnforced;
import static com.picimako.mockitools.EnforceConventionUtil.isMockitoEnforced;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.StubbingApproach.MOCKITO_WHEN;
import static com.picimako.mockitools.intention.convert.stub.DoesntContainUnsupportedMethod.DOESNT_CONTAIN_THEN;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.StubbingApproach;

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
final class ConvertFromMockitoWhenIntention extends ConvertStubbingIntentionBase {

    public ConvertFromMockitoWhenIntention() {
        super("Mockito.when()", ORG_MOCKITO_MOCKITO);
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return MOCKITO_WHEN.isStubbedBy(methodCall) && MOCKITO_WHEN.isValid(methodCall);
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);
        if (!isBDDMockitoEnforced(file) && doAllCallChainsMatch(DOESNT_CONTAIN_THEN, isBulkMode, editor, file)) {
            actions.add(new ConvertStubbingAction(MOCKITO_WHEN, StubbingApproach.MOCKITO_DO_X, isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertStubbingAction(MOCKITO_WHEN, StubbingApproach.BDDMOCKITO_GIVEN, isBulkMode));
            actions.add(new ConvertStubbingAction(MOCKITO_WHEN, StubbingApproach.BDDMOCKITO_WILL_X, isBulkMode));
        }
        return actions;
    }
}
