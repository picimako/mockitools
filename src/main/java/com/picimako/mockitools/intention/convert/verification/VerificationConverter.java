//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.google.common.collect.Iterables.getLast;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getReferenceNameElement;
import static com.picimako.mockitools.PsiMethodUtil.hasArgument;

import java.util.List;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;

import com.picimako.mockitools.intention.convert.ConverterBase;

/**
 * Converts verification call chains between the different approaches.
 */
public final class VerificationConverter extends ConverterBase {

    private static final CallMatcher MOCKITO_VERIFY_MOCK = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, VERIFY).parameterCount(1);
    private static final CallMatcher MOCKITO_VERIFY_MOCK_MODE = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, VERIFY).parameterCount(2);

    public VerificationConverter(Project project, Document document, PsiFile file) {
        super(project, document, file);
    }

    /**
     * Converts the argument {@code Mockito.verify()} call and the following chain to the {@code BDDMockito.then().should()} approach.
     */
    public void convertToBDDMockito(PsiMethodCallExpression mockitoVerify) {
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        runWriteCommandAction(project, () -> {
            if (MOCKITO_VERIFY_MOCK.matches(mockitoVerify)) {
                //Replace Mockito.verify with BDDMockito.then
                replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);
                //Insert should() after BDDMockito.then
                performAndCommitDocument(() -> document.insertString(getReferenceNameElement(getLast(calls)).getTextOffset() - 1, ".should()"));
            } else if (MOCKITO_VERIFY_MOCK_MODE.matches(mockitoVerify)) {
                //It replaces the text between the two arguments of Mockito.verify() with the text ").should("
                //E.g. 'Mockito.verify(mock, times(2))' becomes 'Mockito.verify(mock).should(times(2))'
                document.replaceString(endOffsetOf(getFirstArgument(mockitoVerify)), get2ndArgument(mockitoVerify).getTextOffset(), ").should(");
                //Replace Mockito.verify with BDDMockito.then
                replaceBeginningOfChain(calls, "BDDMockito.then", ORG_MOCKITO_BDDMOCKITO);

            }
        });
    }

    /**
     * Converts the argument {@code BDDMockito.then().should()} call and the following chain to the {@code Mockito.verify()} approach.
     */
    public void convertToMockitoVerify(PsiMethodCallExpression bddMockitoThen) {
        runWriteCommandAction(project, () -> {
            var calls = collectCallsInChainFromFirst(bddMockitoThen, true);

            //Get verification mode argument from 'should()' (or empty string if there's none), and add it after the mock object argument
            String verificationModeArgument = hasArgument(calls.get(1)) ? ", " + getFirstArgument(calls.get(1)).getText() : "";
            document.insertString(endOffsetOf(getFirstArgument(bddMockitoThen)), verificationModeArgument);
            //Replace BDDMockito.then with Mockito.verify
            replaceBeginningOfChain(calls, "Mockito.verify", ORG_MOCKITO_MOCKITO);
            //Delete should() with its arguments, if any
            performAndCommitDocument(() -> document.deleteString(endOffsetOf(calls.get(0)), endOffsetOf(calls.get(1))));
        });
    }

    /**
     * Replaces the beginning of the text of the call chain with the provided replacement text.
     * <p>
     * For example:
     * <pre>
     * //The following text:
     * Mockito.verify(mock).doSomething();
     * //becomes:
     * BDDMockito.then(mock).doSomething();
     * </pre>
     *
     * @param calls        the call chain
     * @param replacement  the replacement text
     * @param mockitoClass the fully qualified class name to import. Either {@code org.mockito.Mockito}, or {@code org.mockito.BDDMockito}.
     */
    private void replaceBeginningOfChain(List<PsiMethodCallExpression> calls, String replacement, String mockitoClass) {
        //end offset of Mockito.verify/BDDMockito.then
        int endOffset = endOffsetOf(getReferenceNameElement(calls.get(0)));
        performAndCommitDocument(() -> document.replaceString(calls.get(0).getTextOffset(), endOffset, replacement));
        importClass(mockitoClass);
    }
}
