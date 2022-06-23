//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.hasArgument;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.picimako.mockitools.intention.convert.verification.BaseConvertVerificationAction;

/**
 * Converts {@code BDDMockito.then().should()} call chains to the {@code Mockito.verify()} approach.
 * <p>
 * If {@code BDDMockito.then().should()} is {@code InOrder} specific, it simply omits the {@code InOrder} argument
 * during conversion.
 *
 * @since 0.4.0
 */
public class ConvertBDDMockitoThenToMockitoVerifyAction extends BaseConvertVerificationAction {
    public ConvertBDDMockitoThenToMockitoVerifyAction(Project project, Document document, PsiFile file) {
        super(project, document, file, "Mockito.verify()");
    }

    @Override
    protected void performAction(Project project, Editor editor, PsiFile file) {
        var bddMockitoThen = getMethodCallAtCaret(file, editor);
        var calls = collectCallsInChainFromFirst(bddMockitoThen, true);

        //Get verification mode argument from 'should()' (or empty string if there's none), and add it after the mock object argument
        String verificationModeArgument = hasArgument(calls.get(1)) ? ", " + getFirstArgument(calls.get(1)).getText() : "";
        document.insertString(endOffsetOf(getFirstArgument(bddMockitoThen)), verificationModeArgument);
        //Replace BDDMockito.then with Mockito.verify
        replaceBeginningOfChain(calls, "Mockito.verify", ORG_MOCKITO_MOCKITO);
        //Delete should() with its arguments, if any
        performAndCommitDocument(() -> document.deleteString(endOffsetOf(calls.get(0)), endOffsetOf(calls.get(1))));
    }
}
