//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.siyeh.ig.callMatcher.CallMatcher;
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
    private static final CallMatcher VERIFY_VERIFICATION = instanceCall(ORG_MOCKITO_MOCKED_STATIC, VERIFY).parameterCount(1);
    private static final CallMatcher VERIFY_VERIFICATION_AND_MODE = instanceCall(ORG_MOCKITO_MOCKED_STATIC, VERIFY).parameterCount(2);
    private static final CallMatcher MOCKED_STATIC_VERIFY = CallMatcher.anyOf(VERIFY_VERIFICATION, VERIFY_VERIFICATION_AND_MODE);

    public ConvertFromMockedStaticVerifyIntention() {
        //Shortest is 'x.verify(Y::Z);'
        super("InOrder.verify()", 15);
    }

    //Availability

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        return VERIFY.equals(getMethodName(methodCall)) && MOCKED_STATIC_VERIFY.matches(methodCall);
    }

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
