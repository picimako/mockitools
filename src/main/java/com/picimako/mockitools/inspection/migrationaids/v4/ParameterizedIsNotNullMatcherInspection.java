//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoQualifiedNames.IS_NOT_NULL;
import static com.picimako.mockitools.MockitoQualifiedNames.IS_NULL;
import static com.picimako.mockitools.MockitoQualifiedNames.MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.NOT_NULL;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MATCHERS;
import static com.picimako.mockitools.util.PsiMethodUtil.deleteArguments;
import static com.picimako.mockitools.util.PsiMethodUtil.getParentCall;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.util.PsiMethodUtil;
import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports calls to the parameterized variants of {@code isNull()}, {@code isNotNull()} and {@code notNull()},
 * which are deprecated since Mockito v2, and are removed in v4.
 * <p>
 * The provided quick fix removes the method argument, so that the non-parameterized variant is used.
 *
 * @since 0.1.0
 */
final class ParameterizedIsNotNullMatcherInspection extends MigrationAidBase.V23ToV4BaseInspection {

    private static final CallMatcher ANY_OF_PARAMETERIZED_NULL_MATCHERS =
        CallMatcher.staticCall(ORG_MOCKITO_ARGUMENT_MATCHERS, IS_NULL, IS_NOT_NULL, NOT_NULL).parameterCount(1);

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (ANY_OF_PARAMETERIZED_NULL_MATCHERS.matches(expression)) {
            var qualifier = PsiMethodUtil.getQualifier(expression);
            String methodName = getMethodName(expression);
            String quickFixName = qualifier != null && (qualifier.textMatches(ORG_MOCKITO_MATCHERS) || qualifier.textMatches(MATCHERS))
                ? MockitoolsBundle.message("quick.fix.migration.aid.v4.replace.with", methodName)
                : MockitoolsBundle.message("quick.fix.migration.aid.v4.use.non.parameterized.null.matcher");
            holder.registerProblem(
                getReferenceNameElement(expression), //referenceName null value is already checked by the CallMatcher
                MockitoolsBundle.message("inspection.migration.aid.v4.use.non.parameterized.null.matcher", methodName),
                new ReplaceNullMatcherWithNonParameterizedVariantQuickfix(quickFixName));
        }
    }

    @RequiredArgsConstructor
    private static final class ReplaceNullMatcherWithNonParameterizedVariantQuickfix extends MigrationAidV4BaseQuickFix {
        private final String quickFixName;

        @Override
        public @IntentionName @NotNull String getName() {
            return quickFixName;
        }

        @Override
        protected void doFix(@NotNull Project project, ProblemDescriptor descriptor) {
            var parentCall = getParentCall(descriptor.getPsiElement());
            if (parentCall != null) {
                deleteArguments(parentCall);
                replaceMatchersQualifier(parentCall);
            }
        }
    }
}
