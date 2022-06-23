//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
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
    public ConvertMockitoVerifyToInOrderVerifyAction(Project project, Document document, PsiFile file) {
        super(project, document, file, "InOrder.verify()");
    }

    @Override
    protected void performAction(Project project, Editor editor, PsiFile file) {
        var mockitoVerify = getMethodCallAtCaret(file, editor);
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        var addedVariable = createAndAddInOrderVariable(mockitoVerify, calls);
        //convert Mockito.verify beginning to inOrder.verify
        replaceBeginningOfChain(calls, "inOrder.verify");

        MemberInplaceRenameHelper.rename(addedVariable.getFirstChild(), editor);
    }
}
