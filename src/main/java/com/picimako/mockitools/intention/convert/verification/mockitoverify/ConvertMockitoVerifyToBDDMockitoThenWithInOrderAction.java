//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockitoverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.hasTwoArguments;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Converts {@code Mockito.verify()} call chains to the {@code BDDMockito.then().should(InOrder)} approach,
 * using a newly created InOrder variable.
 *
 * @since 0.5.0
 */
public final class ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction extends BaseConvertVerificationAction {
    public ConvertMockitoVerifyToBDDMockitoThenWithInOrderAction(Project project, Document document, PsiFile file) {
        super(project, document, file, MockitoolsBundle.message("intention.convert.verification.bddmockito.with.inorder"));
    }

    @Override
    protected void performAction(Project project, Editor editor, PsiFile file) {
        var mockitoVerify = getMethodCallAtCaret(file, editor);
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        var addedVariable = createAndAddInOrderVariable(mockitoVerify, calls);
        replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);
        int endOffsetOfMockArgument = endOffsetOf(getFirstArgument(mockitoVerify));
        if (hasTwoArguments(mockitoVerify)) {
            document.replaceString(
                endOffsetOfMockArgument,
                get2ndArgument(mockitoVerify).getTextOffset(),
                ").should(" + ((PsiNamedElement) addedVariable.getFirstChild()).getName() + ", ");
        } else {
            document.replaceString(endOffsetOfMockArgument, endOffsetOfMockArgument, ").should(" + ((PsiNamedElement) addedVariable.getFirstChild()).getName());
        }
        MemberInplaceRenameHelper.rename(addedVariable.getFirstChild(), editor);
    }
}
