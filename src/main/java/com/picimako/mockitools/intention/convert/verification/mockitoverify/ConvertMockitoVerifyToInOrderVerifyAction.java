//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;

/**
 * Converts {@code Mockito.verify()} call chains to the {@code InOrder.verify()} approach.
 * <p>
 * It creates an InOrder object right before the {@code Mockito.verify()} call chain and uses that object during
 * conversion.
 *
 * @since 0.5.0
 */
public class ConvertMockitoVerifyToInOrderVerifyAction extends BaseConvertVerificationAction {
    public ConvertMockitoVerifyToInOrderVerifyAction(Editor editor) {
        super(editor, "InOrder.verify()");
    }

    @Override
    protected void perform(PsiMethodCallExpression mockitoVerify, Project project, Editor editor) {
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        var addedVariable = createAndAddInOrderVariable(mockitoVerify, calls);
        //convert Mockito.verify beginning to inOrder.verify
        replaceBeginningOfChain(calls, "inOrder.verify");

        MemberInplaceRenameHelper.rename(addedVariable.getFirstChild(), editor);
    }
}
