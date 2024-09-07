//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.intellij.psi.JavaPsiFacade.getElementFactory;
import static com.intellij.psi.search.GlobalSearchScope.moduleWithLibrariesScope;

import java.util.Optional;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Replaces code reference elements with the predefined class ({@link #replacementClassFqn}).
 * <p>
 * This quick fix is for locations where it doesn't matter whether the qualifier of the reference should remain untouched or not.
 * <p>
 * In any case, the reference is replaced with the short name of the predefined fully qualified name.
 */
@RequiredArgsConstructor
public class NameCollisionlessReferenceReplacerQuickFix extends MigrationAidV4BaseQuickFix {

    private final String nameMessageKey;
    private final String replacementClassFqn;

    @Override
    public @IntentionName @NotNull String getName() {
        return MockitoolsBundle.message(nameMessageKey);
    }

    @Override
    protected void doFix(@NotNull Project project, ProblemDescriptor descriptor) {
        Optional.ofNullable(ModuleUtilCore.findModuleForFile(descriptor.getPsiElement().getContainingFile().getVirtualFile(), project))
            .map(module -> getElementFactory(project).createReferenceElementByFQClassName(replacementClassFqn, moduleWithLibrariesScope(module)))
            .ifPresent(pluginsAnnotationEngine -> descriptor.getPsiElement().replace(pluginsAnnotationEngine));
    }
}
