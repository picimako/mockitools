//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.hasOneArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.hasTwoArguments;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;
import static com.siyeh.ig.psiutils.TypeUtils.typeEquals;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;

/**
 * Converts {@code BDDMockito.then().should()} call chains to the {@code Mockito.verify()} approach.
 * <p>
 * If {@code BDDMockito.then().should()} is {@code InOrder} specific, it simply omits the {@code InOrder} argument
 * during conversion, thus in bulk mode, selected call chains can be converted the same way.
 *
 * @since 0.4.0
 */
public class ConvertBDDMockitoThenToMockitoVerifyAction extends ConvertVerificationActionBase {
    public ConvertBDDMockitoThenToMockitoVerifyAction(boolean isBulkMode) {
        super("Mockito.verify()", isBulkMode);
    }

    @Override
    protected void perform(PsiMethodCallExpression bddMockitoThen, Project project) {
        var calls = collectCallsInChainFromFirst(bddMockitoThen, true);

        //Get verification mode argument from 'should()' (or empty string if there's none), and add it after the mock object argument
        String verificationModeArgument = "";
        var should = calls.get(1);
        if (hasOneArgument(should)) {
            if (typeEquals(ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE, getFirstArgument(should).getType()))
                verificationModeArgument = ", " + getFirstArgument(should).getText();
        } else if (hasTwoArguments(should) && typeEquals(ORG_MOCKITO_INORDER, getFirstArgument(should).getType())) {
            verificationModeArgument = ", " + get2ndArgument(should).getText();
        }

        editor.getDocument().insertString(endOffsetOf(getFirstArgument(bddMockitoThen)), verificationModeArgument);
        //Replace BDDMockito.then with Mockito.verify
        replaceBeginningOfChain(calls, "Mockito.verify", ORG_MOCKITO_MOCKITO);
        //Delete should() with its arguments, if any
        performAndCommitDocument(() -> editor.getDocument().deleteString(endOffsetOf(calls.get(0)), endOffsetOf(should)));
    }
}
