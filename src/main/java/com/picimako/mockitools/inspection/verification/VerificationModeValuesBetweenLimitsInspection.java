//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import static com.picimako.mockitools.MockitoQualifiedNames.AT_LEAST;
import static com.picimako.mockitools.MockitoQualifiedNames.AT_MOST;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.TIMES;
import static com.picimako.mockitools.MockitoolsPsiUtil.isAfter;
import static com.picimako.mockitools.MockitoolsPsiUtil.isCalls;
import static com.picimako.mockitools.MockitoolsPsiUtil.isTimeout;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import javax.swing.*;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.SingleIntegerFieldOptionsPanel;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiLiteralUtil;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.siyeh.ig.callMatcher.CallMatcher;
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
final class VerificationModeValuesBetweenLimitsInspection extends MockitoolsBaseInspection {

    private static final CallMatcher MOCKITO_OCCURRENCE_BASED_VERIFICATION_MODES = staticCall(ORG_MOCKITO_MOCKITO, TIMES, AT_LEAST, AT_MOST).parameterCount(1);

    @SuppressWarnings("PublicField")
    public int timeoutMaxThreshold = 5000;

    @Override
    public @Nullable JComponent createOptionsPanel() {
        return new SingleIntegerFieldOptionsPanel(MockitoolsBundle.message("inspection.timeout.max.threshold.config.title"), this, "timeoutMaxThreshold");
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (MOCKITO_OCCURRENCE_BASED_VERIFICATION_MODES.matches(expression))
            checkIntegerArgumentValue(0, expression, holder);
        else if (isCalls(expression))
            checkIntegerArgumentValue(1, expression, holder);
        else if (isAfter(expression))
            checkLongArgumentValue(0L, expression, false, holder);
        else if (isTimeout(expression))
            checkLongArgumentValue(0L, expression, true, holder);
    }

    private void checkIntegerArgumentValue(int upperLimit, PsiMethodCallExpression methodCall, @NotNull ProblemsHolder holder) {
        var verificationModeArgument = getFirstArgument(methodCall);
        Integer argValue = PsiLiteralUtil.parseInteger(verificationModeArgument.getText());

        if (argValue != null && argValue < upperLimit) {
            holder.registerProblem(verificationModeArgument,
                MockitoolsBundle.message("inspection.verification.mode.value.less.than.allowed", getMethodName(methodCall), upperLimit));
        }
    }

    private void checkLongArgumentValue(long upperLimit, PsiMethodCallExpression methodCall, boolean isTimeout, @NotNull ProblemsHolder holder) {
        var verificationModeArgument = getFirstArgument(methodCall);
        Long argValue = PsiLiteralUtil.parseLong(verificationModeArgument.getText());

        if (argValue != null) {
            if (argValue < upperLimit) {
                holder.registerProblem(verificationModeArgument,
                    MockitoolsBundle.message("inspection.verification.mode.value.less.than.allowed", getMethodName(methodCall), upperLimit));
            }
            if (isTimeout && argValue > timeoutMaxThreshold) {
                holder.registerProblem(verificationModeArgument, MockitoolsBundle.message("inspection.timeout.value.more.than.allowed", timeoutMaxThreshold));
            }
        }
    }
}
