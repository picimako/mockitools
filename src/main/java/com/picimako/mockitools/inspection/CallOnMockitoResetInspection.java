//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoolsPsiUtil.isMockedStaticReset;
import static com.picimako.mockitools.MockitoolsPsiUtil.isReset;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports calls on {@code Mockito.reset()} and {@code MockedStatic.reset()}.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#resetting_mocks">Resetting mocks</a>
 * @since 0.1.0
 */
public class CallOnMockitoResetInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (getReferenceNameElement(expression) != null) {
            if (isReset(expression)) {
                holder.registerProblem(getReferenceNameElement(expression), MockitoolsBundle.inspection("call.to.reset", "Mockito"));
            } else if (isMockedStaticReset(expression)) {
                holder.registerProblem(getReferenceNameElement(expression), MockitoolsBundle.inspection("call.to.reset", "MockedStatic"));
            }
        }
    }
}
