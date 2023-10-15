//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_JUNIT_MOCKITO_JUNIT_RUNNER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_RUNNERS_CONSOLE_SPAMMING_MOCKITO_JUNIT_RUNNER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_RUNNERS_MOCKITO_JUNIT_RUNNER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_RUNNERS_VERBOSE_MOCKITO_JUNIT_RUNNER;
import static com.picimako.mockitools.util.ModuleDependencyHelper.isMockitoCore2xOr3xAvailableInModuleOf;
import static com.picimako.mockitools.util.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports usages of JUnit runners used from the {@code org.mockito.runners} package, which is removed Mockito v4.
 * <p>
 * The applicable quick fixes replace them with {@code org.mockito.junit.MockitoJUnitRunner}.
 *
 * @since 0.1.0
 */
final class UsageOfOldJUnitRunnerInspection extends MigrationAidBase.V23ToV4BaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        //Whether the file contains JUnit test classes is not validated, since runners may be referenced in non-unittest (i.e. util) classes.
        if (!isInTestSourceContent(session.getFile()) || !isMockitoCore2xOr3xAvailableInModuleOf(session.getFile(), holder.getProject())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JavaElementVisitor() {
            @Override
            public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
                if (ORG_MOCKITO_RUNNERS_CONSOLE_SPAMMING_MOCKITO_JUNIT_RUNNER.equals(reference.getQualifiedName())
                    || ORG_MOCKITO_RUNNERS_VERBOSE_MOCKITO_JUNIT_RUNNER.equals(reference.getQualifiedName())) {
                    holder.registerProblem(reference,
                        MockitoolsBundle.message("inspection.migration.aid.v4.org.mockito.runners.is.removed"),
                        new NameCollisionlessReferenceReplacerQuickFix("quick.fix.migration.aid.v4.replace.with.mockito.junit.runner",
                            ORG_MOCKITO_JUNIT_MOCKITO_JUNIT_RUNNER));
                    return;
                }
                
                if (ORG_MOCKITO_RUNNERS_MOCKITO_JUNIT_RUNNER.equals(reference.getQualifiedName())) {
                    holder.registerProblem(reference,
                        MockitoolsBundle.message("inspection.migration.aid.v4.org.mockito.runners.is.removed"),
                        new NameCollisionAwareReferenceReplacerQuickFix("quick.fix.migration.aid.v4.replace.with.mockito.junit.runner",
                            ORG_MOCKITO_JUNIT_MOCKITO_JUNIT_RUNNER));
                }
            }
        };
    }
}
