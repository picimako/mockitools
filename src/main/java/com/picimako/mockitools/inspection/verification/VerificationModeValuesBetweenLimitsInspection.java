//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import static com.picimako.mockitools.MockitoolsPsiUtil.MOCKITO_OCCURRENCE_BASED_VERIFICATION_MODES;
import static com.picimako.mockitools.MockitoolsPsiUtil.isAfter;
import static com.picimako.mockitools.MockitoolsPsiUtil.isCalls;
import static com.picimako.mockitools.MockitoolsPsiUtil.isTimeout;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;

import javax.swing.*;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.SingleIntegerFieldOptionsPanel;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiLiteralUtil;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.siyeh.ig.psiutils.MethodCallUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports when arguments of calls on time and occurrence based {@code VerificationMode}s are out of allowed bounds.
 * <p>
 * Supported VerificationModes:
 * <ul>
 *     <li>{@code Mockito.times}</li>
 *     <li>{@code Mockito.atLeast}</li>
 *     <li>{@code Mockito.atMost}</li>
 *     <li>{@code Mockito.calls}</li>
 *     <li>{@code Mockito.after}</li>
 *     <li>{@code Mockito.timeout}</li>
 * </ul>
 * <p>
 * None of these calls are allowed negative values as argument. Additionally, {@code Mockito.calls()} doesn't allow 0 as argument either,
 * and {@code Mockito.timeout()} doesn't allow values above a user-defined threshold (with 5000 as its default value.)
 * <p>
 * Only unit test classes (class name ending with Test) are considered, since (in ordinary projects) Mockito is supposed to be used only in test classes.
 * <p>
 *
 * @since 0.1.0
 */
public class VerificationModeValuesBetweenLimitsInspection extends MockitoolsBaseInspection {

    @SuppressWarnings("PublicField")
    public int timeoutMaxThreshold = 5000;

    @Override
    public @Nullable JComponent createOptionsPanel() {
        return new SingleIntegerFieldOptionsPanel(MockitoolsBundle.inspection("timeout.max.threshold.config.title"), this, "timeoutMaxThreshold");
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (MOCKITO_OCCURRENCE_BASED_VERIFICATION_MODES.matches(expression)) {
            checkIntegerArgumentValue(0, expression, holder);
        } else if (isCalls(expression)) {
            checkIntegerArgumentValue(1, expression, holder);
        } else if (isAfter(expression)) {
            checkLongArgumentValue(0, expression, false, holder);
        } else if (isTimeout(expression)) {
            checkLongArgumentValue(0, expression, true, holder);
        }
    }

    private void checkIntegerArgumentValue(int upperLimit, PsiMethodCallExpression methodCall, @NotNull ProblemsHolder holder) {
        PsiExpression verificationModeArgument = getFirstArgument(methodCall);
        Integer argValue = PsiLiteralUtil.parseInteger(verificationModeArgument.getText());

        if (argValue != null && argValue < upperLimit) {
            holder.registerProblem(verificationModeArgument,
                MockitoolsBundle.inspection("verification.mode.value.less.than.allowed", MethodCallUtils.getMethodName(methodCall), upperLimit));
        }
    }

    private void checkLongArgumentValue(int upperLimit, PsiMethodCallExpression methodCall, boolean isTimeout, @NotNull ProblemsHolder holder) {
        PsiExpression verificationModeArgument = getFirstArgument(methodCall);
        Long argValue = PsiLiteralUtil.parseLong(verificationModeArgument.getText());

        if (argValue != null) {
            if (argValue < upperLimit) {
                holder.registerProblem(verificationModeArgument,
                    MockitoolsBundle.inspection("verification.mode.value.less.than.allowed", MethodCallUtils.getMethodName(methodCall), upperLimit));
            }
            if (isTimeout && argValue > timeoutMaxThreshold) {
                holder.registerProblem(verificationModeArgument, MockitoolsBundle.inspection("timeout.value.more.than.allowed", timeoutMaxThreshold));
            }
        }
    }
}
