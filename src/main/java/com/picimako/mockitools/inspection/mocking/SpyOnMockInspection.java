//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import static com.picimako.mockitools.MockitoQualifiedNames.*;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoMock;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;

/**
 * This inspection reports spy creation on mock objects.
 * <p>
 * Although, the corresponding feature was introduced in Mockito 5.4.0, this inspection does not do a library version check,
 * and validates test code regardless of the Mockito version.
 * <p>
 * NOTE: variables with Mockito.mock() initializers are not recognized as mocks yet.
 *
 * @since 0.11.0
 */
final class SpyOnMockInspection extends MockitoolsBaseInspection {

    private static final CallMatcher MOCKITO_SPY = staticCall(ORG_MOCKITO_MOCKITO, SPY).parameterTypes("T");

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (MOCKITO_SPY.matches(expression)) {
            var firstArgument = getFirstArgument(expression);
            if (firstArgument instanceof PsiMethodCallExpression callArg) {
                //Mockito.spy(Mockito.mock(...))
                if (isMockitoMock(callArg))
                    holder.registerProblem(firstArgument, MockitoolsBundle.message("inspection.spying.on.mock.is.not.allowed"));
            }
            //Mockito.spy(Mockito.mock(<name of field annotated with @Mock>))
            else if (firstArgument instanceof PsiReferenceExpression variableRef
                && variableRef.resolve() instanceof PsiField field
                && field.hasAnnotation(ORG_MOCKITO_MOCK)) {
                holder.registerProblem(firstArgument, MockitoolsBundle.message("inspection.spying.on.mock.is.not.allowed"));
            }
        }
    }
}
