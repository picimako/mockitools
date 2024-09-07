//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.stubbing;

import static com.picimako.mockitools.MockitoQualifiedNames.IN_ORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY_NO_INTERACTIONS;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY_NO_MORE_INTERACTIONS;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY_ZERO_INTERACTIONS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoolsPsiUtil.isExtraInterfaces;
import static com.picimako.mockitools.MockitoolsPsiUtil.isIgnoreStubs;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.Optional;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports method calls that must have at least one argument passed but none is provided.
 * <p>
 * These are usually methods with a vararg as their only parameter.
 *
 * @since 0.1.0
 */
final class NoMethodCallArgumentSpecifiedInspection extends MockitoolsBaseInspection {

    private static final CallMatcher VERIFY_CALLS =
        CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, VERIFY_NO_INTERACTIONS, VERIFY_NO_MORE_INTERACTIONS, VERIFY_ZERO_INTERACTIONS, IN_ORDER);

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (expression.getArgumentList().isEmpty()) {
            if (VERIFY_CALLS.matches(expression) || isExtraInterfaces(expression)) {
                registerProblem(expression, holder, ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
            } else if (isIgnoreStubs(expression)) {
                registerProblem(expression, holder, ProblemHighlightType.WARNING);
            }
        }
    }

    private void registerProblem(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder, ProblemHighlightType problemHighlightType) {
        PsiElement elementToHighlight = Optional.ofNullable(getReferenceNameElement(expression)).orElseGet(expression::getMethodExpression);
        holder.registerProblem(elementToHighlight, MockitoolsBundle.message("inspection.method.call.no.argument.specified", getMethodName(expression)), problemHighlightType);
    }
}
