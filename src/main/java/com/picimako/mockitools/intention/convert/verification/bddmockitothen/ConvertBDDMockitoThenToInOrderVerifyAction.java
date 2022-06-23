//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.hasArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasTwoArguments;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MemberInplaceRenameHelper;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Converts {@code BDDMockito.then().should()} call chains to the {@code InOrder.verify()} approach.
 * <p>
 * If the {@code BDDMockito.then().should()} call chain is InOrder specific, it uses that InOrder object during
 * conversion, otherwise it creates a new InOrder variable right before the verification call chain.
 *
 * @since 0.5.0
 */
public class ConvertBDDMockitoThenToInOrderVerifyAction extends BaseConvertVerificationAction {
    public ConvertBDDMockitoThenToInOrderVerifyAction(@NotNull Project project, @NotNull Document document, PsiFile file) {
        super(project, document, file, "InOrder.verify()");
    }

    @Override
    protected void performAction(Project project, Editor editor, PsiFile file) {
        var bddMockitoThen = getMethodCallAtCaret(file, editor);
        var calls = collectCallsInChainFromFirst(bddMockitoThen, true);
        var should = calls.get(1);

        //If there is no InOrder argument, then create one, and convert to inOrder.verify
        if (ConvertFromBDDMockitoIntention.THEN_SHOULD_WITHOUT_INORDER.matches(should)) {
            //Get verification mode argument from 'should()', or empty string if there's none
            String verificationModeArgument = hasArgument(should) ? ", " + getFirstArgument(should).getText() : "";
            var addedVariable = createAndAddInOrderVariable(bddMockitoThen, calls);
            replaceBDDMockitoVerification(bddMockitoThen, calls, verificationModeArgument, "inOrder");
            MemberInplaceRenameHelper.rename(addedVariable.getFirstChild(), editor);
        }
        //If there is InOrder argument, don't create one, just use that for the inOrder.verify
        else {
            String inOrderArgument = getFirstArgument(should).getText();
            //Get verification mode argument from 'should()', or empty string if there's none
            String verificationModeArgument = hasTwoArguments(should) ? ", " + get2ndArgument(should).getText() : "";
            replaceBDDMockitoVerification(bddMockitoThen, calls, verificationModeArgument, inOrderArgument);
        }
    }

    private void replaceBDDMockitoVerification(PsiMethodCallExpression bddMockitoThen, List<PsiMethodCallExpression> calls,
                                               String verificationModeArgument, String inOrderArgument) {
        document.insertString(endOffsetOf(getFirstArgument(bddMockitoThen)), verificationModeArgument);
        //Replace BDDMockito.then with <InOrder argument>.verify
        replaceBeginningOfChain(calls, inOrderArgument + ".verify");
        //Delete should() with its arguments, if any
        performAndCommitDocument(() -> document.deleteString(endOffsetOf(bddMockitoThen), endOffsetOf(calls.get(1))));
    }
}
