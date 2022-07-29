//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;

/**
 * Converts MockedStatic specific {@code InOrder.verify()} call chains to {@code MockedStatic.verify()}, omitting the InOrder
 * variable altogether in the resulting call chain.
 * <p>
 * It keeps the original InOrder variable, even if it is not used anymore.
 *
 * @since 0.6.0
 */
public class ConvertInOrderVerifyToMockedStaticVerifyAction extends ConvertVerificationActionBase {

    protected ConvertInOrderVerifyToMockedStaticVerifyAction(boolean isBulkMode) {
        super("MockedStatic.verify()", isBulkMode);
    }

    @Override
    protected void perform(PsiMethodCallExpression verificationCall, Project project) {
        var calls = collectCallsInChainFromFirst(verificationCall, true);
        var mockedStaticArgument = getFirstArgument(calls.get(0));
        String mockedStaticText = mockedStaticArgument.getText();

        mockedStaticArgument.delete();
        documentManager.doPostponedOperationsAndUnblockDocument(editor.getDocument());
        replaceBeginningOfChain(calls, mockedStaticText + ".verify");
    }
}
