//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;
import org.jetbrains.annotations.NotNull;

/**
 * Converts {@code InOrder.verify()} call chains to the {@code Mockito.verify()} approach.
 * <p>
 * It keeps the original InOrder object, even if it is not used anymore.
 *
 * @since 0.5.0
 */
public class ConvertInOrderVerifyToMockitoVerifyAction extends BaseConvertVerificationAction {

    public ConvertInOrderVerifyToMockitoVerifyAction(Project project, @NotNull Document document, PsiFile file) {
        super(project, document, file, "Mockito.verify()");
    }

    @Override
    protected void performAction(Project project, Editor editor, PsiFile file) {
        var inOrderVerify = getMethodCallAtCaret(file, editor);
        var calls = collectCallsInChainFromFirst(inOrderVerify, true);
        importClass(ORG_MOCKITO_MOCKITO);
        replaceBeginningOfChain(calls, "Mockito.verify");
    }
}
