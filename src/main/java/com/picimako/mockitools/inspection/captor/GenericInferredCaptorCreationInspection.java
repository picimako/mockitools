//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.captor;

import static com.intellij.util.ArrayUtil.getLastElement;
import static com.picimako.mockitools.MockitoMockMatchers.CAPTOR_GENERIC_INFERRED;
import static com.picimako.mockitools.util.PsiMethodUtil.hasArgument;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection reports arguments passed into {@code ArgumentCaptor.captor()} which is designed to determine the mock type based on
 * the variable's type the call is assigned to, and not by the type passed into them.
 *
 * @see com.picimako.mockitools.inspection.mocking.GenericInferredMockAndSpyCreationInspection
 * @since 1.3.0
 */
final class GenericInferredCaptorCreationInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (CAPTOR_GENERIC_INFERRED.matches(expression) && hasArgument(expression)) registerProblem(expression, holder);
    }

    /**
     * Calculates the text range for the arguments passed into the {@code reified} parameter of {@code ArgumentCaptor.captor}.
     */
    private void registerProblem(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        var argumentList = expression.getArgumentList();
        var arguments = argumentList.getExpressions();
        int argumentListTextOffset = argumentList.getTextOffset();
        var textRange = TextRange.create(
            arguments[0].getTextOffset() - argumentListTextOffset,
            endOffsetOf(getLastElement(arguments)) - argumentListTextOffset);

        holder.registerProblem(argumentList, textRange, MockitoolsBundle.message("inspection.argument.in.generic.inferred.captor.creation"));
    }
}
