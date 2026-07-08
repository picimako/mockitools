//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoolsPsiUtil.isMatchers;
import static com.picimako.mockitools.util.PsiMethodUtil.getQualifier;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Reports usages of argument matchers called via {@code org.mockito.Matchers}.
 * That class is deprecated since Mockito v2 and is removed in v4.
 * <p>
 * It also provides a quick fix to replace the qualifier of the method call and use {@code org.mockito.ArgumentMatchers}.
 *
 * @since 0.1.0
 */
final class ArgumentMatchersCalledViaMatchersInspection extends MigrationAidBase.V23ToV4BaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (isMatchers(expression) && expression.getMethodExpression().isQualified()) {
            holder.registerProblem(getQualifier(expression), MockitoolsBundle.message("inspection.migration.aid.v4.use.argument.matchers.instead.of.matchers"),
                new ReplaceMatchersWithArgumentMatchersQuickFix());
        }
    }

    public static final class ReplaceMatchersWithArgumentMatchersQuickFix extends MigrationAidV4BaseQuickFix {
        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.migration.aid.v4.use.argument.matchers.instead.of.matchers");
        }

        @Override
        protected void doFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor, PsiElement problemElement) {
            QualifierReplacements.replaceMatchersWithArgumentMatchers((PsiExpression) problemElement, project);
        }
    }
}
