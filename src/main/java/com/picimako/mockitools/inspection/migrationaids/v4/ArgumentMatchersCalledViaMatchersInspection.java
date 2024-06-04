//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.intellij.psi.search.GlobalSearchScope.moduleWithLibrariesScope;
import static com.picimako.mockitools.MockitoQualifiedNames.ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MATCHERS;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMatchers;
import static com.picimako.mockitools.util.PsiMethodUtil.getQualifier;
import static com.siyeh.ig.psiutils.ImportUtils.nameCanBeImported;

import java.util.Optional;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports usages of argument matchers called via {@code org.mockito.Matchers}. That class is deprecated since
 * Mockito v2, and is removed in v4.
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
        protected void doFix(@NotNull Project project, ProblemDescriptor descriptor) {
            replace((PsiExpression) descriptor.getPsiElement(), project);
        }

        public void replace(PsiExpression qualifier, @NotNull Project project) {
            //if the matcher is referenced with fully qualified name: replace the fqn appropriately
            //This also respects that the class is referenced by its fqn rather via import by keeping the fqn of ArgumentMatchers.
            if (qualifier.textMatches(ORG_MOCKITO_MATCHERS)) {
                replaceQualifierWith(ORG_MOCKITO_ARGUMENT_MATCHERS, qualifier);
            }
            //if the matcher is referenced as Matchers.<matcher>:
            //  then replace the classname (the qualifier) with ArgumentMatchers,
            //  add import statement for org.mockito.ArgumentMatchers if necessary
            else if (qualifier.textMatches(MATCHERS)) {
                PsiElement elementAfterReplace = replaceQualifierWith(ARGUMENT_MATCHERS, qualifier);
                if (nameCanBeImported(ORG_MOCKITO_ARGUMENT_MATCHERS, elementAfterReplace)) {
                    //Module scope is used because different modules may use different versions of Mockito
                    Optional.ofNullable(ModuleUtilCore.findModuleForFile(elementAfterReplace.getContainingFile().getVirtualFile(), project))
                        .map(module -> JavaPsiFacade.getInstance(project).findClass(ORG_MOCKITO_ARGUMENT_MATCHERS, moduleWithLibrariesScope(module)))
                        .ifPresent(argumentMatchers -> ImportUtils.addImportIfNeeded(argumentMatchers, elementAfterReplace));
                }
            }
        }

        private PsiElement replaceQualifierWith(String replacement, PsiExpression qualifier) {
            return qualifier.replace(PsiElementFactory.getInstance(qualifier.getProject()).createExpressionFromText(replacement, qualifier));
        }
    }
}
