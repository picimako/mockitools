//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection.isMockitoEnforced;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceExpression;
import com.picimako.mockitools.VerificationApproach;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts {@code Mockito.verify()} based verification to other approaches.
 * <p>
 * The intention is available
 * <ul>
 *     <li>in single mode, on {@code Mockito.verify()} when it is followed by a method call on the mock object,</li>
 *     <li>in bulk mode, when all selected verifications satisfy the single mode criteria.</li>
 * </ul>
 *
 * @see ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction
 * @see ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction
 * @see ConvertMockitoVerifyToInOrderVerifyAction
 * @since 0.5.0
 */
public class ConvertFromMockitoVerifyIntention extends ConvertVerificationIntentionBase {

    public ConvertFromMockitoVerifyIntention() {
        super(VerificationApproach.MOCKITO_VERIFY);
    }

    @Override
    protected boolean isQualifierHaveCorrectType(PsiExpression qualifier) {
        return qualifier instanceof PsiReferenceExpression qualifierAsRef
            && qualifierAsRef.resolve() instanceof PsiClass qualifierClass
            && ORG_MOCKITO_MOCKITO.equals(qualifierClass.getQualifiedName());
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(2);
        if (!isBDDMockitoEnforced(file)) {
            actions.add(new ConvertMockitoVerifyToInOrderVerifyAction(isBulkMode));
        }
        if (!isMockitoEnforced(file)) {
            actions.add(new ConvertMockitoVerifyToBDDMockitoThenWithoutInOrderAction(isBulkMode));
            actions.add(new ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(isBulkMode));
        }
        return actions;
    }
}
