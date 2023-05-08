//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.picimako.mockitools.VerificationApproach;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.siyeh.ig.psiutils.TypeUtils;

import java.util.Collections;
import java.util.List;

/**
 * Converts {@code MockedStatic.verify()} based verification to other approaches.
 * <p>
 * The intention is available
 * <ul>
 *     <li>in single mode, on {@code MockedStatic.verify()}</li>
 *     <li>in bulk mode, when all selected verifications satisfy the single mode criteria.</li>
 * </ul>
 *
 * @see ConvertMockedStaticVerifyToInOrderVerifyAction
 * @since 0.6.0
 */
public class ConvertFromMockedStaticVerifyIntention extends ConvertVerificationIntentionBase {
    public ConvertFromMockedStaticVerifyIntention() {
        //Shortest is 'x.verify(Y::Z);'
        super(VerificationApproach.MOCKED_STATIC_VERIFY, 15);
    }

    //Availability

    /**
     * Returns whether the argument element is of a {@code MockedStatic} type variable.
     */
    @Override
    protected boolean isQualifierHaveCorrectType(PsiExpression qualifier) {
        return TypeUtils.expressionHasTypeOrSubtype(qualifier, ORG_MOCKITO_MOCKED_STATIC);
    }

    //Invocation

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        return Collections.singletonList(new ConvertMockedStaticVerifyToInOrderVerifyAction(isBulkMode));
    }
}
