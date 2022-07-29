//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.picimako.mockitools.inspection.EnforceConventionInspection.IN_ORDER_VERIFY_NON_MOCKED_STATIC;
import static com.picimako.mockitools.intention.convert.verification.bddmockitothen.ConvertFromBDDMockitoThenIntention.THEN_SHOULD_WITH_INORDER;
import static com.siyeh.ig.psiutils.TypeUtils.typeEquals;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Reports {@code InOrder} local variables on which only one verification is called in the form of either
 * an {@code InOrder.verify()} or a {@code BDDMockito.then().should(InOrder)} call.
 *
 * @since 0.5.0
 */
public class SingleInOrderVerificationInspection extends LocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        if (isInTestSourceContent(session.getFile())) {
            return new JavaElementVisitor() {
                @Override
                public void visitLocalVariable(PsiLocalVariable variable) {
                    if (!typeEquals(ORG_MOCKITO_INORDER, variable.getType())) return;

                    var inOrderRefs = ReferencesSearch.search(variable).findAll();
                    if (inOrderRefs.size() == 1) {
                        var ref = inOrderRefs.iterator().next();
                        if (ref instanceof PsiReferenceExpression) {
                            var verifyOrShould = getParentOfType((PsiReferenceExpression) ref, PsiMethodCallExpression.class);
                            if (IN_ORDER_VERIFY_NON_MOCKED_STATIC.matches(verifyOrShould) || THEN_SHOULD_WITH_INORDER.matches(verifyOrShould))
                                holder.registerProblem(variable.getNameIdentifier(), MockitoolsBundle.inspection("in.order.is.used.only.once"));
                        }
                    }

                    super.visitLocalVariable(variable);
                }
            };
        }
        return PsiElementVisitor.EMPTY_VISITOR;
    }
}
