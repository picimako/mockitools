//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO_NEVER;
import static com.picimako.mockitools.MockitoolsPsiUtil.isTimes;
import static com.picimako.mockitools.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;

import javax.swing.*;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiLiteralUtil;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.mockitools.resources.MockitoolsBundle;

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
 * <p>
 * TODO: it is a further possible improvement to figure out what calls remain after deleting times(1) and static import those calls.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#exact_verification">Mockito exact verification documentation</a>
 * @since 0.1.0
 */
public class TimesVerificationModeInspection extends MockitoolsBaseInspection {

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
        return !isInTestSourceContent(session.getFile()) || (!reportTimesZeroToNever && !reportTimesOneCanBeOmitted)
            ? PsiElementVisitor.EMPTY_VISITOR
            : methodCallVisitor(holder);
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (isTimes(expression)) {
            Integer timesValue = PsiLiteralUtil.parseInteger(getArguments(expression)[0].getText());

            if (timesValue != null) {
                if (reportTimesZeroToNever && timesValue == 0) {
                    holder.registerProblem(expression, MockitoolsBundle.inspection("times.zero.can.be.replaced.with.never"), new ReplaceTimesZeroWithNeverQuickFix());
                }
                if (reportTimesOneCanBeOmitted && timesValue == 1 && !hasSubsequentMethodCall(expression)) {
                    holder.registerProblem(expression, MockitoolsBundle.inspection("times.one.can.be.omitted"), ProblemHighlightType.LIKE_UNUSED_SYMBOL, new DeleteTimesOneQuickFix());
                }
            }
        }
    }

    /**
     * Replaces {@code Mockito.times(0)} calls with {@code Mockito.never()}. Additional methods called on times()
     * are not affected, they are left as they are.
     * <p>
     * At least for now, static import of {@code Mockito.never()} is not applied.
     */
    private static final class ReplaceTimesZeroWithNeverQuickFix extends InspectionGadgetsFix {

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) descriptor.getPsiElement();
            PsiElement replaced = methodCall.replace(JavaPsiFacade.getElementFactory(project).createExpressionFromText(ORG_MOCKITO_MOCKITO_NEVER + "()", methodCall));
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(replaced);
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.quickFix("times.zero.replace.with.never");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MockitoolsBundle.quickFix("times.family.name");
        }
    }

    /**
     * Deletes {@code Mockito.times(1)} calls.
     * <p>
     * When applied, code snippets like {@code Mockito.verify(mock, times(1))} become {@code Mockito.verify(mock)}.
     */
    private static final class DeleteTimesOneQuickFix extends InspectionGadgetsFix {

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            descriptor.getPsiElement().delete();
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.quickFix("times.one.delete.call");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MockitoolsBundle.quickFix("times.family.name");
        }
    }
}
