//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO_THEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INORDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC_VERIFICATION;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO_NEVER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.MockitoolsPsiUtil.isTimes;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromLast;
import static com.picimako.mockitools.util.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.UnitTestPsiUtil.isInTestSourceContent;
import static com.siyeh.ig.callMatcher.CallMatcher.instanceCall;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiLiteralUtil;
import com.picimako.mockitools.MockitoolsPsiUtil;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Inspects {@code Mockito.times()} calls whether they can be optimized or deleted based on their argument values.
 * <ul>
 *     <li>{@code times(0)} calls can be replaced with {@code Mockito.never()} for better readability,</li>
 *     <li>{@code times(1)} calls can be removed since 1 is the default value when using {@code times()} verification</li>
 * </ul>
 * <p>
 * Quick fixes are also provided for the replacement and removal of these calls.
 * <p>
 * {@code Mockito.times(1)} calls are reported only when they have no subsequent calls on them, e.g.
 * {@code Mockito.times(1).description("message")}.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#exact_verification">Mockito exact verification documentation</a>
 * @since 0.1.0
 */
public class TimesVerificationModeInspection extends MockitoolsBaseInspection {

    private static final CallMatcher VERIFICATION = CallMatcher.anyOf(
        MockitoolsPsiUtil.MOCKITO_VERIFY.parameterTypes("T", ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE),
        MockitoolsPsiUtil.INORDER_VERIFY.parameterTypes("T", ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE),
        MockitoolsPsiUtil.MOCKED_STATIC_VERIFY.parameterTypes(ORG_MOCKITO_MOCKED_STATIC_VERIFICATION, ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE),
        instanceCall(ORG_MOCKITO_BDDMOCKITO_THEN, "should").parameterTypes(ORG_MOCKITO_INORDER, ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE));
    private static final CallMatcher BDDMOCKITO_SHOULD =
        instanceCall(ORG_MOCKITO_BDDMOCKITO_THEN, "should").parameterTypes(ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE);

    @SuppressWarnings("PublicField")
    public boolean reportTimesZeroToNever = true;

    @SuppressWarnings("PublicField")
    public boolean reportTimesOneCanBeOmitted = true;

    @Override
    public @Nullable JComponent createOptionsPanel() {
        final MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
        panel.addCheckbox(MockitoolsBundle.inspectionOption("report.times.zero.to.never"), "reportTimesZeroToNever");
        panel.addCheckbox(MockitoolsBundle.inspectionOption("report.times.one.to.omit"), "reportTimesOneCanBeOmitted");
        return panel;
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) && (reportTimesZeroToNever || reportTimesOneCanBeOmitted) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //times(0) can be replaced with never() wherever it is used
        if (reportTimesZeroToNever && isTimesWithValueEqualTo(expression, 0)) {
            holder.registerProblem(expression, MockitoolsBundle.inspection("times.zero.can.be.replaced.with.never"), new ReplaceTimesZeroWithNeverQuickFix());
            return;
        }

        //times(1) without a sequent method call
        if (reportTimesOneCanBeOmitted) {
            //times(1) is allowed to be removed only when it is used in the Mockito-provided verification methods
            var verificationModeCalls = getCallsInVerificationModeArgument(expression);
            if (verificationModeCalls.size() == 1) {
                var timesMode = verificationModeCalls.get(0);
                if (isTimesWithValueEqualTo(timesMode, 1))
                    holder.registerProblem(timesMode, MockitoolsBundle.inspection("times.one.can.be.omitted"), ProblemHighlightType.LIKE_UNUSED_SYMBOL, new DeleteTimesOneQuickFix());
            }
        }
    }

    private boolean isTimesWithValueEqualTo(PsiMethodCallExpression timesMode, Integer value) {
        if (!isTimes(timesMode)) return false;
        Integer timesValue = PsiLiteralUtil.parseInteger(getFirstArgument(timesMode).getText());
        return Objects.equals(timesValue, value);
    }

    private List<PsiMethodCallExpression> getCallsInVerificationModeArgument(PsiMethodCallExpression verification) {
        PsiExpression verificationMode = null;
        if (VERIFICATION.matches(verification)) verificationMode = get2ndArgument(verification);
        else if (BDDMOCKITO_SHOULD.matches(verification)) verificationMode = getFirstArgument(verification);

        return verificationMode instanceof PsiMethodCallExpression ? collectCallsInChainFromLast(verificationMode) : Collections.emptyList();
    }

    /**
     * Replaces {@code Mockito.times(0)} calls with {@code Mockito.never()}. Additional methods called on times()
     * are not affected, they are left as they are.
     * <p>
     * At least for now, static import of {@code Mockito.never()} is not applied.
     */
    private static final class ReplaceTimesZeroWithNeverQuickFix extends TimesQuickFix {
        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            var methodCall = (PsiMethodCallExpression) descriptor.getPsiElement();
            var replaced = methodCall.replace(
                JavaPsiFacade.getElementFactory(project).createExpressionFromText(ORG_MOCKITO_MOCKITO_NEVER + "()", methodCall));
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(replaced);
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.quickFix("times.zero.replace.with.never");
        }
    }

    /**
     * Deletes {@code Mockito.times(1)} calls.
     * <p>
     * When applied, code snippets like {@code Mockito.verify(mock, times(1))} become {@code Mockito.verify(mock)}.
     */
    private static final class DeleteTimesOneQuickFix extends TimesQuickFix {
        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            descriptor.getPsiElement().delete();
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.quickFix("times.one.delete.call");
        }
    }

    private abstract static class TimesQuickFix extends InspectionGadgetsFix {
        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MockitoolsBundle.quickFix("times.family.name");
        }
    }
}
