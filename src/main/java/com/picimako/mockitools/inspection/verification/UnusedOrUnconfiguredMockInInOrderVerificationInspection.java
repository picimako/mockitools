//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.IN_ORDER_VERIFY;
import static com.picimako.mockitools.intention.convert.verification.bddmockitothen.ConvertFromBDDMockitoIntention.THEN_SHOULD_WITH_INORDER;
import static com.siyeh.ig.psiutils.TypeUtils.typeEquals;
import static java.util.stream.Collectors.toList;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.SmartList;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Reports mock objects in {@code InOrder} verifications in the following cases:
 * <ul>
 *     <li>The mock is added to the arguments of {@code Mockito.inOrder()} but is not used in any verification
 *     performed via that {@code InOrder} object.</li>
 *     <li>The mock is used in an {@code InOrder} verification but it is not added to the arguments of {@code Mockito.inOrder()}.</li>
 * </ul>
 *
 * @since 0.5.0
 */
public class UnusedOrUnconfiguredMockInInOrderVerificationInspection extends LocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (isInTestSourceContent(session.getFile())) {
            return new JavaElementVisitor() {
                @Override
                public void visitLocalVariable(PsiLocalVariable variable) {
                    if (!typeEquals(ORG_MOCKITO_INORDER, variable.getType())
                        || !variable.hasInitializer()
                        || !(variable.getInitializer() instanceof PsiMethodCallExpression)) return;

                    var inOrderRefs = ReferencesSearch.search(variable).toArray(PsiReference.EMPTY_ARRAY);
                    if (inOrderRefs.length > 0 && areAllVerifications(inOrderRefs)) {
                        var mocksInVerifications = collectMocksInVerifications(inOrderRefs);
                        var mockitoInOrder = (PsiMethodCallExpression) variable.getInitializer();
                        //The mock arguments in 'Mockito.inOrder()'
                        var mocksInMockitoInOrder = getArguments(mockitoInOrder);

                        //The mock arguments from each 'InOrder.verify()' and 'BDDMockito.then().should(InOrder)' call
                        var mockInVerificationsAsString = mocksInVerifications.stream().map(PsiElement::getText).collect(toList());
                        //Report all mocks in 'Mockito.inOrder()' that are don't use in a verification
                        for (var mockInInOrder : mocksInMockitoInOrder) {
                            if (!mockInVerificationsAsString.contains(mockInInOrder.getText()))
                                holder.registerProblem(mockInInOrder, MockitoolsBundle.inspection("no.in.order.verification.for.mock"));
                        }

                        //The mock arguments as Strings from 'Mockito.inOrder()'
                        var mocksInMockitoInOrderAsString = Arrays.stream(mocksInMockitoInOrder).map(PsiElement::getText).collect(toList());
                        //Report all mocks in verifications that are not configured in 'Mockito.inOrder()'
                        for (var mockInVerification : mocksInVerifications) {
                            if (!mocksInMockitoInOrderAsString.contains(mockInVerification.getText()))
                                holder.registerProblem(mockInVerification, MockitoolsBundle.inspection("mock.is.not.configured.in.in.order"));
                        }
                    }

                    super.visitLocalVariable(variable);
                }

                /**
                 * Returns whether all InOrder references in the argument areInOrder verifications, and not references
                 * passed into method arguments, or something else.
                 * <p>
                 * This is needed, so that we only take into consideration actual verifications when determining where
                 * the related mock objects are used.
                 */
                private boolean areAllVerifications(PsiReference[] inOrderRefs) {
                    return Arrays.stream(inOrderRefs).allMatch(ref -> {
                        if (ref instanceof PsiReferenceExpression) {
                            var verifyOrShould = getParentOfType((PsiReferenceExpression) ref, PsiMethodCallExpression.class);
                            return IN_ORDER_VERIFY.matches(verifyOrShould) || THEN_SHOULD_WITH_INORDER.matches(verifyOrShould);
                        } else return false;
                    });
                }

                /**
                 * Collects all mock object expressions from the argument InOrder verifications.
                 */
                private List<PsiExpression> collectMocksInVerifications(PsiReference[] inOrderRefs) {
                    var mocksUsed = new SmartList<PsiExpression>();

                    for (var ref : inOrderRefs) {
                        if (ref instanceof PsiReferenceExpression) {
                            var verifyOrShould = getParentOfType((PsiReferenceExpression) ref, PsiMethodCallExpression.class);
                            if (IN_ORDER_VERIFY.matches(verifyOrShould)) {
                                saveMockFrom(verifyOrShould, mocksUsed);
                            } else if (THEN_SHOULD_WITH_INORDER.matches(verifyOrShould)) {
                                saveMockFrom(/*then*/findChildOfType(verifyOrShould, PsiMethodCallExpression.class), mocksUsed);
                            }
                        }
                    }
                    return mocksUsed;
                }

                /**
                 * Stores the mock object used in the argument verification call, for later comparison.
                 */
                private void saveMockFrom(PsiMethodCallExpression verifyOrThen, List<PsiExpression> mocksUsed) {
                    var mock = getFirstArgument(verifyOrThen);
                    if (!mocksUsed.contains(mock)) mocksUsed.add(mock);
                }
            };
        }
        return PsiElementVisitor.EMPTY_VISITOR;
    }
}
