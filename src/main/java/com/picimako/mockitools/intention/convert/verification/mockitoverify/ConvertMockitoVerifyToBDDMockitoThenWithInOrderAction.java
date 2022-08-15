//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.collectMockObjects;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.hasTwoArguments;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNamedElement;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;
import com.picimako.mockitools.resources.MockitoolsBundle;

import java.util.List;

/**
 * Converts {@code Mockito.verify()} call chains to {@code BDDMockito.then().should(InOrder)},
 * using a newly created InOrder variable.
 * <p>
 * In bulk mode, it creates a single common InOrder variable and uses it in all selected verifications.
 *
 * @since 0.5.0
 */
public final class ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction extends ConvertVerificationActionBase {
    public ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(boolean isBulkMode) {
        super(MockitoolsBundle.message("intention.convert.verification.bddmockito.with.inorder"), isBulkMode);
    }

    @Override
    protected void perform(PsiMethodCallExpression mockitoVerify, Project project) {
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        var addedVariable = createAndAddInOrderVariable(mockitoVerify, calls);
        performConversion(addedVariable, mockitoVerify, calls);
        MemberInplaceRenameHelper.rename(addedVariable.getFirstChild(), editor);
    }

    @Override
    protected void performActionInBulk(List<PsiExpressionStatement> statementsInSelection,
                                       PsiMethodCallExpression firstVerification,
                                       List<PsiMethodCallExpression> callsInFirstVerification) {
        var inOrderVariable = createAndAddInOrderVariable(firstVerification, callsInFirstVerification, collectMockObjects(statementsInSelection));

        statementsInSelection.stream()
            .map(this::getVerificationCall)
            .forEach(verify -> {
                var calls = collectCallsInChainFromFirst(verify, true);
                performAndCommitDocument(() -> performConversion(inOrderVariable, verify, calls));
            });

        MemberInplaceRenameHelper.rename(inOrderVariable.getFirstChild(), editor);
    }

    private void performConversion(PsiElement inOrderVariable, PsiMethodCallExpression verificationCall, List<PsiMethodCallExpression> calls) {
        replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);
        int endOffsetOfMockArgument = endOffsetOf(getFirstArgument(verificationCall));
        if (hasTwoArguments(verificationCall)) {
            editor.getDocument().replaceString(
                endOffsetOfMockArgument,
                get2ndArgument(verificationCall).getTextOffset(),
                ").should(" + ((PsiNamedElement) inOrderVariable.getFirstChild()).getName() + ", ");
        } else {
            editor.getDocument().replaceString(endOffsetOfMockArgument, endOffsetOfMockArgument, ").should(" + ((PsiNamedElement) inOrderVariable.getFirstChild()).getName());
        }
    }
}
