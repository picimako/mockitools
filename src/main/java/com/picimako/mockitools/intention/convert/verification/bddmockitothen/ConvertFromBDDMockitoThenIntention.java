//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.bddmockitothen;

import static com.intellij.openapi.application.ReadAction.compute;
import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.EnforceConventionUtil.isBDDMockitoEnforced;
import static com.picimako.mockitools.EnforceConventionUtil.isMockitoEnforced;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.VerificationApproach.BDDMOCKITO_THEN_SHOULD;
import static com.picimako.mockitools.intention.convert.FromSelectionDataRetriever.collectStatementsInSelection;
import static com.picimako.mockitools.util.PsiMethodUtil.getMethodCallAtCaret;
import static com.picimako.mockitools.util.PsiMethodUtil.getSubsequentMethodCall;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.picimako.mockitools.intention.convert.verification.ConvertVerificationIntentionBase;
import com.picimako.mockitools.intention.convert.verification.NoActionAvailableAction;
import com.picimako.mockitools.util.PsiMethodUtil;
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
final class ConvertFromBDDMockitoThenIntention extends ConvertVerificationIntentionBase {
    public ConvertFromBDDMockitoThenIntention() {
        super(BDDMOCKITO_THEN_SHOULD);
    }

    @Override
    protected boolean isQualifierHaveCorrectType(PsiExpression qualifier) {
        return compute(() -> qualifier instanceof PsiReferenceExpression qualifierAsRef
                             && qualifierAsRef.resolve() instanceof PsiClass qualifierClass
                             && ORG_MOCKITO_BDDMOCKITO.equals(qualifierClass.getQualifiedName()));
    }

    @Override
    public List<AnAction> actionSelectionOptions(Editor editor, PsiFile file) {
        boolean isBulkMode = compute(() -> editor.getSelectionModel().hasSelection());
        var actions = new ArrayList<AnAction>(3);

        if (!isBDDMockitoEnforced(file)) {
            //In bulk mode, it can be converted one-by-one since any InOrder variable is omitted
            actions.add(new ConvertBDDMockitoThenToMockitoVerifyAction(isBulkMode));

            if (!isBulkMode) actions.add(new ConvertBDDMockitoThenToInOrderVerifyAction(false));
            else {
                var shoulds = collectShouldCalls(editor, file);
                boolean areAllVerificationsWithoutInOrder = shoulds.stream().noneMatch(BDDMOCKITO_THEN_SHOULD::isInOrderSpecific);

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
        return compute(() -> collectStatementsInSelection(editor, file).stream()
            .map(statement -> findChildOfType(statement, PsiIdentifier.class))
            .map(identifier -> getParentOfType(identifier, PsiMethodCallExpression.class))
            .map(PsiMethodUtil::getSubsequentMethodCall)
            .toList());
    }

    private boolean areAllVerificationsWithInOrder(List<PsiMethodCallExpression> shoulds) {
        return shoulds.stream().allMatch(BDDMOCKITO_THEN_SHOULD::isInOrderSpecific);
    }

    private boolean areAllVerificationsUsingSameInOrder(List<PsiMethodCallExpression> shoulds) {
        return shoulds.stream().map(PsiMethodUtil::getFirstArgument).map(arg -> compute(arg::getText)).distinct().count() == 1;
    }

    /**
     * Returns whether all verifications in a selection don't, or the single verification under the caret doesn't,
     * use an {@code InOrder} variable.
     */
    private boolean areAllVerificationsWithoutInOrder(Editor editor, PsiFile file, boolean isBulkMode) {
        return !isBulkMode
               ? !BDDMOCKITO_THEN_SHOULD.isInOrderSpecific(getSubsequentMethodCall(getMethodCallAtCaret(file, editor)))
               : collectShouldCalls(editor, file).stream().noneMatch(BDDMOCKITO_THEN_SHOULD::isInOrderSpecific);
    }
}
