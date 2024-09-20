//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import static com.picimako.mockitools.MockitoMockMatchers.MOCK_WITH_SETTINGS;
import static com.picimako.mockitools.MockitoQualifiedNames.DEFAULT_ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.NAME;
import static com.picimako.mockitools.MockitoQualifiedNames.SPIED_INSTANCE;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromLast;
import static com.picimako.mockitools.util.PsiMethodUtil.get2ndArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.hasTwoArguments;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection reports {@code Mockito.mock(..., withSettings()...)} mock creations that have convenience methods
 * or simpler variants, and provides a quick fix to replace them with their corresponding simpler versions.
 * <p>
 * Currently {@code spiedInstance}, {@code code} and {@code defaultAnswer} are supported in {@code MockSettings}.
 *
 * @see com.picimako.mockitools.intention.mocking.ExpandMockCreationIntention
 * @since 0.11.0
 */
final class SimplifyMockCreationInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //mock(<type>, withSettings()...)
        if (MOCK_WITH_SETTINGS.matches(expression) && hasTwoArguments(expression)) {
            var withSettings = get2ndArgument(expression);
            var calls = collectCallsInChainFromLast(withSettings);

            //withSettings() + one additional settings call
            if (calls.size() != 2) return;

            String settingsMethodName = getMethodName(calls.getFirst());
            if (SPIED_INSTANCE.equals(settingsMethodName))
                //Mockito.mock(<type>, withSettings().spiedInstance(instance)) -> Mockito.spy(instance)
                registerProblem(expression, holder, "spy(<spiedInstance>)");
            else if (NAME.equals(settingsMethodName))
                //Mockito.mock(<type>, withSettings().name(name)) -> Mockito.mock(<type>, name)
                registerProblem(expression, holder, "mock(<class>, <name>)");
            else if (DEFAULT_ANSWER.equals(settingsMethodName))
                //Mockito.mock(<type>, withSettings().defaultAnswer(answer)) -> Mockito.mock(<type>, answer)
                registerProblem(expression, holder, "mock(<class>, <answer>)");
        }
    }

    private void registerProblem(PsiElement element, @NotNull ProblemsHolder holder, String replacementHint) {
        holder.registerProblem(element,
            MockitoolsBundle.message("inspection.mock.creation.with.settings.can.be.simplified", replacementHint),
            //Highlight type is added because the inspection itself is on INFO level, and this makes it appear in tests as well.
            ProblemHighlightType.WEAK_WARNING,
            new ReplaceWithSimplerMockCreationQuickFix());
    }

    private static final class ReplaceWithSimplerMockCreationQuickFix extends InspectionGadgetsFix {

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MockitoolsBundle.message("quick.fix.replace.mock.creation.with.simpler.variant");
        }

        @Override
        protected void doFix(@NotNull Project project, ProblemDescriptor descriptor) {
            if (descriptor.getPsiElement() instanceof PsiMethodCallExpression mockitoMock) {
                var settingsMethodCall = collectCallsInChainFromLast(/*withSettings*/ get2ndArgument(mockitoMock)).getFirst();
                String settingsMethodName = getMethodName(settingsMethodCall);

                if (SPIED_INSTANCE.equals(settingsMethodName)) {
                    //org.mockito.Mockito.spy(<spiedInstance>)
                    String mockitoSpy = "org.mockito.Mockito.spy(" + getFirstArgument(settingsMethodCall).getText() + ")";
                    simplify(mockitoMock, mockitoSpy, project);
                } else if (NAME.equals(settingsMethodName) || DEFAULT_ANSWER.equals(settingsMethodName)) {
                    //org.mockito.Mockito.mock(<clazz>.class, <answer>)
                    //org.mockito.Mockito.mock(<clazz>.class, <name>)
                    String newMockitoMock = "org.mockito.Mockito.mock(" + getFirstArgument(mockitoMock).getText() + ", " + getFirstArgument(settingsMethodCall).getText() + ")";
                    simplify(mockitoMock, newMockitoMock, project);
                }
            }
        }

        /**
         * Replaces the {@code mockitoMock} call with a new method call created from {@code replacementText}.
         *
         * @param mockitoMock     the {@code Mockito.mock()} call to simplify
         * @param replacementText the text of the simplified replacement call
         * @param project         the current project
         */
        private void simplify(PsiMethodCallExpression mockitoMock, String replacementText, Project project) {
            var simplifiedReplacementCall = JavaCodeStyleManager.getInstance(project)
                .shortenClassReferences(JavaPsiFacade.getElementFactory(project).createExpressionFromText(replacementText, mockitoMock));
            mockitoMock.replace(simplifiedReplacementCall);
        }
    }
}
