//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_CONFIGURATION_ANNOTATION_ENGINE;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_PLUGINS_ANNOTATION_ENGINE;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_PLUGINS_INSTANTIATOR_PROVIDER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_PLUGINS_INSTANTIATOR_PROVIDER_2;
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
 * Reports usages of {@code org.mockito.configuration.AnnotationEngine} and {@code org.mockito.plugins.InstantiatorProvider},
 * since they are removed in Mockito v4.
 * <p>
 * Related quick fixes can replace these references with the proper classes.
 *
 * @since 0.1.0
 */
public class UsageOfDeprecatedPluginClassesInspection extends MigrationAidBase.V23ToV4BaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        //Whether the file contains JUnit test classes is not validated, since runners may be referenced in non-unittest (i.e. util) classes.
        if (!isInTestSourceContent(session.getFile()) || !isMockitoCore2xOr3xAvailableInModuleOf(session.getFile(), holder.getProject())) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new JavaElementVisitor() {
            @Override
            public void visitReferenceElement(PsiJavaCodeReferenceElement reference) {
                if (ORG_MOCKITO_CONFIGURATION_ANNOTATION_ENGINE.equals(reference.getQualifiedName())) {
                    holder.registerProblem(reference,
                        MockitoolsBundle.inspection("migration.aid.v4.plugin.configuration.annotation.engine"),
                        new NameCollisionAwareReferenceReplacerQuickFix("migration.aid.v4.replace.with.org.mockito.plugins.AnnotationEngine",
                            ORG_MOCKITO_PLUGINS_ANNOTATION_ENGINE));
                    return;
                }

                if (ORG_MOCKITO_PLUGINS_INSTANTIATOR_PROVIDER.equals(reference.getQualifiedName())) {
                    holder.registerProblem(reference,
                        MockitoolsBundle.inspection("migration.aid.v4.plugin.instantiator.provider"),
                        new NameCollisionlessReferenceReplacerQuickFix("migration.aid.v4.replace.with.instantiator.provider.2",
                            ORG_MOCKITO_PLUGINS_INSTANTIATOR_PROVIDER_2));
                }
            }
        };
    }
}
