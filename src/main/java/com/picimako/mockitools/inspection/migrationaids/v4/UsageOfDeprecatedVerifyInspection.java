//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKED_STATIC_VERIFICATION;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY_NO_MORE_INTERACTIONS;
import static com.picimako.mockitools.MockitoQualifiedNames.VERIFY_ZERO_INTERACTIONS;
import static com.picimako.mockitools.util.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.util.PsiMethodUtil.getParentCall;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports calls to various verify methods which are deprecated, and removed in v4.
 * <p>
 * Reported calls:
 * <ul>
 *     <li>{@code MockedStatic.verify(VerificationMode, Verification)} that can be replaced with
 *     {@code Verification, VerificationMode}, essentially switching the two arguments in the call.</li>
 *     <li>{@code Mockito.verifyZeroInteractions(Object...)} that can be replaced with
 *     {@code Mockito.verifyNoMoreInteractions(Object...)}</li>
 * </ul>
 *
 * @since 0.1.0
 */
public class UsageOfDeprecatedVerifyInspection extends MigrationAidBase.V3ToV4BaseInspection {

    private static final CallMatcher MOCKED_STATIC_VERIFY =
        CallMatcher.instanceCall(ORG_MOCKITO_MOCKED_STATIC, VERIFY).parameterTypes(ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE, ORG_MOCKITO_MOCKED_STATIC_VERIFICATION);
    private static final CallMatcher MOCKITO_VERIFY_ZERO_INTERACTIONS = CallMatcher.staticCall(ORG_MOCKITO_MOCKITO, VERIFY_ZERO_INTERACTIONS);

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (MOCKITO_VERIFY_ZERO_INTERACTIONS.matches(expression)) {
            holder.registerProblem(
                getReferenceNameElement(expression), //referenceName null value is already checked by the CallMatcher
                MockitoolsBundle.message("inspection.migration.aid.v4.use.no.more.interactions"),
                new ReplaceZeroInteractionsWithNoMoreInteractionsQuickFix());
            return;
        }
        if (MOCKED_STATIC_VERIFY.matches(expression)) {
            holder.registerProblem(
                getReferenceNameElement(expression), //referenceName null value is already checked by the CallMatcher
                MockitoolsBundle.message("inspection.migration.aid.v4.mocked.static.verify"),
                new SwitchMockedStaticVerifyArgumentsQuickFix());
        }
    }

    private static final class SwitchMockedStaticVerifyArgumentsQuickFix extends MigrationAidV4BaseQuickFix {

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.migration.aid.v4.switch.mocked.static.verify.args");
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            var parentCall = getParentCall(descriptor.getPsiElement());
            if (parentCall != null) {
                var arguments = getArguments(parentCall);
                var save = JavaPsiFacade.getElementFactory(project).createExpressionFromText(arguments[0].getText(), parentCall);
                arguments[0].replace(arguments[1]);
                arguments[1].replace(save);
            }
        }
    }

    private static final class ReplaceZeroInteractionsWithNoMoreInteractionsQuickFix extends MigrationAidV4BaseQuickFix {

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.migration.aid.v4.replace.zero.interactions.with.no.more.interactions");
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            var parentCall = getParentCall(descriptor.getPsiElement());
            if (parentCall != null) {
                PsiElement elementAfterReplace = descriptor.getPsiElement().replace(PsiElementFactory.getInstance(project).createIdentifier(VERIFY_NO_MORE_INTERACTIONS));
                if (!parentCall.getMethodExpression().isQualified()) {
                    staticImport(parentCall, ORG_MOCKITO_MOCKITO, VERIFY_NO_MORE_INTERACTIONS, elementAfterReplace);
                }
            }
        }
    }
}
