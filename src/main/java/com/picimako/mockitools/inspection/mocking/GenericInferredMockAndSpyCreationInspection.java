//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import static com.intellij.util.ArrayUtil.getLastElement;
import static com.picimako.mockitools.MockitoMockMatchers.MOCKS_GENERIC_INFERRED_WITH_CONFIG;
import static com.picimako.mockitools.MockitoMockMatchers.MOCK_GENERIC_INFERRED;
import static com.picimako.mockitools.MockitoMockMatchers.SPY_GENERIC_INFERRED;
import static com.picimako.mockitools.util.PsiMethodUtil.hasArgument;
import static com.picimako.mockitools.util.Ranges.endOffsetOf;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * This inspection reports arguments passed into {@code Mockito.mock()} and {@code Mockito.spy()} which are designed to determine the mock type based on
 * the variable's type they are assigned to, and not by the type passed into them.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#54">Mocking/Spying without specifying class</a>
 * @see com.picimako.mockitools.inspection.captor.GenericInferredCaptorCreationInspection
 * @since 0.11.0
 */
final class GenericInferredMockAndSpyCreationInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (MOCK_GENERIC_INFERRED.matches(expression) || SPY_GENERIC_INFERRED.matches(expression)) {
            if (hasArgument(expression)) registerProblem(expression, holder, 0);
        } else if (MOCKS_GENERIC_INFERRED_WITH_CONFIG.matches(expression) && expression.getArgumentList().getExpressionCount() > 1) {
            registerProblem(expression, holder, 1);
        }
    }

    /**
     * Calculates the text range for the arguments passed into the {@code reified} parameter of {@code Mockito.mock()/spy()}.
     * Depending on the signature of {@code Mockito.mock()/spy()}, it starts from the 0th or 1st argument, and ends at the last argument's end offset.
     */
    private void registerProblem(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder, @Range(from = 0, to = 1) int startArgumentIndex) {
        var argumentList = expression.getArgumentList();
        var arguments = argumentList.getExpressions();
        var textRange = TextRange.create(
            arguments[startArgumentIndex].getTextOffset() - argumentList.getTextOffset(),
            endOffsetOf(getLastElement(arguments)) - argumentList.getTextOffset());

        holder.registerProblem(argumentList, textRange, MockitoolsBundle.message("inspection.argument.in.generic.inferred.mock.spy.creation"));
    }
}
