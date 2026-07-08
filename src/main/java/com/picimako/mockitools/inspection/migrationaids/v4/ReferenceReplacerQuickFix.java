//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.intellij.psi.JavaPsiFacade.getElementFactory;
import static com.intellij.psi.search.GlobalSearchScope.moduleWithLibrariesScope;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.picimako.mockitools.resources.MockitoolsBundle;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Base class for quick fixes that perform reference replacements.
 */
@RequiredArgsConstructor
public abstract class ReferenceReplacerQuickFix extends MigrationAidV4BaseQuickFix {

    @PropertyKey(resourceBundle = "messages.MockitoolsBundle")
    protected final String nameMessageKey;
    protected final String replacementClassFqn;

    @Override
    public @IntentionName @NotNull String getName() {
        return MockitoolsBundle.message(nameMessageKey);
    }

    protected PsiJavaCodeReferenceElement createRefElementByFQN(Project project, Module module) {
        return getElementFactory(project)
            .createReferenceElementByFQClassName(replacementClassFqn, moduleWithLibrariesScope(module));
    }

    protected PsiJavaCodeReferenceElement createClassNameRefElement(Project project, Module module) {
        return getElementFactory(project)
            .createFQClassNameReferenceElement(replacementClassFqn, moduleWithLibrariesScope(module));
    }
}
