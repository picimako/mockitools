//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.collectMockObjects;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;

import java.util.List;

/**
 * Converts {@code Mockito.verify()} call chains to {@code InOrder.verify()}.
 * <p>
 * It creates an InOrder object right before the {@code Mockito.verify()} call chain and uses that object during
 * conversion.
 * <p>
 * In bulk mode, it creates a single common InOrder variable and uses it in all selected verifications.
 *
 * @since 0.5.0
 */
public class ConvertMockitoVerifyToInOrderVerifyAction extends ConvertVerificationActionBase {
    public ConvertMockitoVerifyToInOrderVerifyAction(boolean isBulkMode) {
        super("InOrder.verify()", isBulkMode);
    }

    @Override
    protected void perform(PsiMethodCallExpression mockitoVerify, Project project) {
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        var addedVariable = createAndAddInOrderVariable(mockitoVerify, calls);
        //convert Mockito.verify beginning to inOrder.verify
        replaceBeginningOfChain(calls, "inOrder.verify");

        MemberInplaceRenameHelper.rename(addedVariable.getFirstChild(), editor);
    }

    @Override
    protected void performActionInBulk(List<PsiExpressionStatement> statementsInSelection,
                                       PsiMethodCallExpression firstVerification,
                                       List<PsiMethodCallExpression> callsInFirstVerification) {
        var inOrderVariable = createAndAddInOrderVariable(firstVerification, callsInFirstVerification, collectMockObjects(statementsInSelection));

        statementsInSelection.stream()
            .map(this::getVerificationCall)
            .map(verify -> collectCallsInChainFromFirst(verify, true))
            .forEach(calls -> performAndCommitDocument(() -> replaceBeginningOfChain(calls, "inOrder.verify")));

        MemberInplaceRenameHelper.rename(inOrderVariable.getFirstChild(), editor);
    }
}
