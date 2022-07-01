//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import static com.google.common.collect.Iterables.getLast;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.PsiClassUtil.importClassAndCommit;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.MockitoQualifiedNames;

import java.util.List;

/**
 * Creates {@code InOrder} type local variables in the form of an {@code InOrder inOrder = Mockito.inOrder([mock objects])} call.
 */
public class InOrderVariableCreator {

    private final Document document;

    public InOrderVariableCreator(Document document) {
        this.document = document;
    }

    /**
     * Creates an {@code org.mockito.InOrder} type local variable and inserts it right before the verification call
     * that is being converted.
     * <p>
     * The arguments of the {@code Mockito.inOrder()} call will be the mock object used in {@code verificationCall}-
     *
     * @param verificationCall the verification call being converted
     * @param calls            the calls in the verification call chain
     * @return the PSI element of the added variable declaration statement
     */
    public PsiElement createAndAddInOrderVariable(PsiMethodCallExpression verificationCall, List<PsiMethodCallExpression> calls) {
        return createAndAddInOrderVariable(verificationCall, calls, getFirstArgument(verificationCall).getText());
    }

    /**
     * Creates an {@code org.mockito.InOrder} type local variable and inserts it right before the verification call
     * that is being converted.
     *
     * @param verificationCall the verification call being converted
     * @param calls            the calls in the verification call chain
     * @param mockObjectsArgs  the mock objects as String as the arguments of the {@code Mockito.inOrder()} call
     * @return the PSI element of the added variable declaration statement
     */
    public PsiElement createAndAddInOrderVariable(PsiMethodCallExpression verificationCall, List<PsiMethodCallExpression> calls, String mockObjectsArgs) {
        //Create an InOrder object from the verified mock: 'InOrder inOrder = Mockito.InOrder(mock);'
        var project = verificationCall.getProject();

        importClassAndCommit(MockitoQualifiedNames.ORG_MOCKITO_INORDER, project, verificationCall.getContainingFile(), document);

        String inOrderVariableText = "InOrder inOrder = Mockito.inOrder(" + mockObjectsArgs + ");";
        var inOrderVariable = JavaPsiFacade.getElementFactory(project).createStatementFromText(inOrderVariableText, verificationCall);

        //Add the new variable to the parent block, before the Mockito.verify()/BDDMockito.then() call chain
        var parentBlock = getParentOfType(verificationCall, PsiCodeBlock.class);

        //'Iterables.getLast(calls)' returns the call expression for the whole call chain.
        // The addition has to happen to before the whole call chain, so it has the proper formatting and underlying PSI.
        var addedVariable = parentBlock.addBefore(inOrderVariable, getLast(calls).getParent());
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
        return addedVariable;
    }
}
