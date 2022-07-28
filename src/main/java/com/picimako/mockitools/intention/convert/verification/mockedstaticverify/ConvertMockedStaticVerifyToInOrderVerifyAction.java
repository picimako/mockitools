//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.getQualifier;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;
import static com.siyeh.ig.psiutils.ExpressionUtils.getFirstExpressionInList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiResourceVariable;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationActionBase;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Converts {@code MockedStatic.verify()} call chains to {@code InOrder.verify()}.
 * <p>
 * It creates an InOrder object right before the {@code MockedStatic.verify()} call chain and uses that object during
 * conversion.
 * <p>
 * In bulk mode, it creates a single common InOrder variable and uses it in all selected verifications.
 *
 * @since 0.6.0
 */
public class ConvertMockedStaticVerifyToInOrderVerifyAction extends ConvertVerificationActionBase {

    private static final CallMatcher MOCK_STATIC = CallMatcher.anyOf(
        staticCall(ORG_MOCKITO_MOCKITO, "mockStatic").parameterCount(1),
        staticCall(ORG_MOCKITO_MOCKITO, "mockStatic").parameterCount(2)
    );

    public ConvertMockedStaticVerifyToInOrderVerifyAction(boolean isBulkMode) {
        super("InOrder.verify()", isBulkMode);
    }

    @Override
    protected void perform(PsiMethodCallExpression mockitoVerify, Project project) {
        var calls = collectCallsInChainFromFirst(mockitoVerify, true);

        var mockedVarAndClassType = getMockedVarAndClassType(calls);
        if (isNotEmpty(mockedVarAndClassType)) {
            var inOrderVariable = createAndAddInOrderVariable(mockitoVerify, calls, mockedVarAndClassType.first);
            convertVerification(calls, mockitoVerify, mockedVarAndClassType.second);
            rename(inOrderVariable);
        }
    }

    @Override
    protected void performActionInBulk(List<PsiExpressionStatement> statementsInSelection,
                                       PsiMethodCallExpression firstVerification,
                                       List<PsiMethodCallExpression> callsInFirstVerification) {
        //The list of call chains from each selected statement
        var verificationCallChains = statementsInSelection.stream()
            .map(this::getVerificationCall)
            .map(verify -> collectCallsInChainFromFirst(verify, true))
            .collect(toList());

        //The mocked class types concatenated with commas. E.g. 'List.class' for a single class, or 'List.class, Set.class' for multiple ones.
        String mockedClassTypes = verificationCallChains.stream()
            .map(this::getMockedVarAndClassType)
            .filter(this::isNotEmpty)
            .map(type -> type.first)
            .distinct() //to filter out duplicate types if multiple mocks has the same type
            .collect(joining(", "));

        var inOrderVariable = createAndAddInOrderVariable(firstVerification, callsInFirstVerification, mockedClassTypes);

        for (var calls : verificationCallChains) {
            var mockedVarAndClassType = getMockedVarAndClassType(calls);
            if (isNotEmpty(mockedVarAndClassType))
                convertVerification(calls, calls.get(0), mockedVarAndClassType.second);
        }

        rename(inOrderVariable);
    }

    private void convertVerification(List<PsiMethodCallExpression> calls, PsiMethodCallExpression verify, PsiElement mockedStaticVar) {
        performAndCommitDocument(() -> replaceBeginningOfChain(calls, "inOrder.verify"));
        addMockedStaticVariableAsArgumentToVerify(verify, createMockedStaticArgument(mockedStaticVar, verify));
    }
    //Helpers

    /**
     * Returns the first argument of the Mockito.mockStatic() call (the class type mocked) paired with its MockedStatic-type
     * resource variable from the parent try-catch block.
     *
     * @param calls the call chain of the current verification
     * @return the class type + resource variable, or an empty Pair
     */
    @NotNull
    private Pair<String, PsiElement> getMockedVarAndClassType(List<PsiMethodCallExpression> calls) {
        var mockVariableExpr = getQualifier(calls.get(0));
        if (mockVariableExpr instanceof PsiReferenceExpression) {
            var mockedStaticResourceVar = ((PsiReferenceExpression) mockVariableExpr).resolve();
            if (mockedStaticResourceVar instanceof PsiResourceVariable) {
                var mockStaticCall = ((PsiResourceVariable) mockedStaticResourceVar).getInitializer();
                if (MOCK_STATIC.matches(mockStaticCall))
                    return Pair.create(getFirstArgument((PsiMethodCallExpression) mockStaticCall).getText(), mockedStaticResourceVar);
            }
        }
        return Pair.empty();
    }

    private boolean isNotEmpty(Pair<String, PsiElement> pair) {
        return pair != Pair.<String, PsiElement>empty();
    }

    /**
     * Creates a reference expression from the argument {@code mockedStaticVariable}, so that reference can be
     * added as the argument of {@code should()}.
     *
     * @param mockedStaticVariable the MockedStatic-type resource variable
     * @param verify               the calls in the call chain, to retrieve the verify() call from
     */
    @NotNull
    private PsiExpression createMockedStaticArgument(PsiElement mockedStaticVariable, PsiMethodCallExpression verify) {
        return JavaPsiFacade.getElementFactory(mockedStaticVariable.getProject())
            .createExpressionFromText(((PsiResourceVariable) mockedStaticVariable).getName(), verify);
    }

    /**
     * Adds the MockedStatic reference expression as the first argument, to the {@code verify()} call.
     */
    private void addMockedStaticVariableAsArgumentToVerify(PsiMethodCallExpression verify, PsiExpression mockedStaticVar) {
        var verifyArgumentList = verify.getArgumentList();
        if (!verifyArgumentList.isEmpty())
            verifyArgumentList.addBefore(mockedStaticVar, getFirstExpressionInList(verifyArgumentList));
        documentManager.doPostponedOperationsAndUnblockDocument(editor.getDocument());
    }
}
