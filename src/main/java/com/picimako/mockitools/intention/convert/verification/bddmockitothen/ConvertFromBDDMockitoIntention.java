//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO_THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoThen;
import static com.picimako.mockitools.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.PsiMethodUtil.getSubsequentMethodCall;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isBDDMockitoEnforced;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.isMockitoEnforced;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.picimako.mockitools.PsiMethodUtil;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.picimako.mockitools.intention.convert.verification.NoActionAvailableAction;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts {@code BDDMockito.then()} based verification to other approaches.
 * <p>
 * The intention is available
 * <ul>
 *     <li>in single mode, on {@code BDDMockito.then()} when it is followed by {@code should()} and a method call on the mock object,</li>
 *     <li>in bulk mode, when all selected verifications satisfy the single mode criteria.</li>
 * </ul>
 *
 * @see ConvertBDDMockitoThenToMockitoVerifyAction
 * @see ConvertBDDMockitoThenToInOrderVerifyAction
 * @see AddInOrderToBDDMockitoAction
 * @see NoActionAvailableAction
 * @since 0.5.0
 */
public class ConvertFromBDDMockitoIntention extends ConvertVerificationIntentionBase {
    private static final CallMatcher.Simple SHOULD = instanceCall(ORG_MOCKITO_BDDMOCKITO_THEN, "should");
    static final CallMatcher THEN_SHOULD_WITHOUT_INORDER = CallMatcher.anyOf(
        SHOULD.parameterCount(0),
        SHOULD.parameterTypes(ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE));
    private static final CallMatcher THEN_SHOULD = CallMatcher.anyOf(
        THEN_SHOULD_WITHOUT_INORDER,
        SHOULD.parameterTypes(ORG_MOCKITO_INORDER),
        SHOULD.parameterTypes(ORG_MOCKITO_INORDER, ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE)
    );

    public ConvertFromBDDMockitoIntention() {
        super("BDDMockito.then()");
    }

    @Override
    public boolean isAvailableFor(PsiMethodCallExpression methodCall) {
        if (THEN.equals(getMethodName(methodCall)) && isBDDMockitoThen(methodCall)) {
            var should = getSubsequentMethodCall(methodCall);
            return THEN_SHOULD.matches(should) && hasSubsequentMethodCall(should);
        }
        return false;
    }

    @Override
    protected boolean isQualifierHaveCorrectType(PsiExpression qualifier) {
        if (qualifier instanceof PsiReferenceExpression) {
            var psiClass = ((PsiReferenceExpression) qualifier).resolve();
            return psiClass instanceof PsiClass && ORG_MOCKITO_BDDMOCKITO.equals(((PsiClass) psiClass).getQualifiedName());
        }
        return false;
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = editor.getSelectionModel().hasSelection();
        var actions = new ArrayList<AnAction>(3);

        if (!isBDDMockitoEnforced(file)) {
            //In bulk mode, it can be converted one-by-one since any InOrder variable is omitted
            actions.add(new ConvertBDDMockitoThenToMockitoVerifyAction(isBulkMode));

            if (!isBulkMode) actions.add(new ConvertBDDMockitoThenToInOrderVerifyAction(false));
            else {
                var shoulds = collectShouldCalls(editor, file);
                boolean areAllVerificationsWithoutInOrder = shoulds.stream().allMatch(THEN_SHOULD_WITHOUT_INORDER::matches);

                //In bulk mode, we only allow conversion if either none of the should() calls use an IrOrder variable,
                // or all of them use the same one.
                //This keeps the logic and the user experience simpler, not having to deal with mixed cases.
                if (areAllVerificationsWithoutInOrder || (areAllVerificationsWithInOrder(shoulds) && areAllVerificationsUsingSameInOrder(shoulds))) {
                    actions.add(new ConvertBDDMockitoThenToInOrderVerifyAction(true));
                }
            }
        }
        //Execution of {@link AddInOrderToBDDMockitoAction} is possible only when none of the selected BDDMockito.then()
        //call chains use an InOrder object.
        if (!isMockitoEnforced(file) && areAllVerificationsWithoutInOrder(editor, file, isBulkMode)) {
            actions.add(new AddInOrderToBDDMockitoAction(isBulkMode));
        }
        if (actions.isEmpty()) actions.add(NoActionAvailableAction.INSTANCE);
        return actions;
    }

    @NotNull
    private List<PsiMethodCallExpression> collectShouldCalls(Editor editor, PsiFile file) {
        return collectStatementsInSelection(editor, file).stream()
            .map(statement -> findChildOfType(statement, PsiIdentifier.class))
            .map(identifier -> getParentOfType(identifier, PsiMethodCallExpression.class))
            .map(PsiMethodUtil::getSubsequentMethodCall)
            .collect(toList());
    }

    private boolean areAllVerificationsWithInOrder(List<PsiMethodCallExpression> shoulds) {
        return shoulds.stream().noneMatch(THEN_SHOULD_WITHOUT_INORDER::matches);
    }

    private boolean areAllVerificationsUsingSameInOrder(List<PsiMethodCallExpression> shoulds) {
        return shoulds.stream().map(PsiMethodUtil::getFirstArgument).map(PsiElement::getText).distinct().count() == 1;
    }

    /**
     * Returns whether all verifications in a selection don't, or the single verification under the caret doesn't,
     * use an {@code InOrder} variable.
     */
    private boolean areAllVerificationsWithoutInOrder(Editor editor, PsiFile file, boolean isBulkMode) {
        return !isBulkMode
            ? THEN_SHOULD_WITHOUT_INORDER.matches(getSubsequentMethodCall(getMethodCallAtCaret(file, editor)))
            : collectShouldCalls(editor, file).stream().allMatch(THEN_SHOULD_WITHOUT_INORDER::matches);
    }
}
