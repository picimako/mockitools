//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Replaces code reference elements with the predefined class ({@link #replacementClassFqn}).
 * <p>
 * This quick fix is for locations where it doesn't matter whether the qualifier of the reference should remain untouched or not.
 * <p>
 * In any case, the reference is replaced with the short name of the predefined fully qualified name.
 */
public final class NameCollisionlessReferenceReplacerQuickFix extends ReferenceReplacerQuickFix {

    public NameCollisionlessReferenceReplacerQuickFix(String nameMessageKey, String replacementClassFqn) {
        super(nameMessageKey, replacementClassFqn);
    }

    @Override
    protected void doFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor, PsiElement problemElement) {
        var module = ModuleUtilCore.findModuleForFile(problemElement.getContainingFile().getVirtualFile(), project);
        if (module == null) return;

        var refElement = createRefElementByFQN(project, module);
        if (refElement != null) problemElement.replace(refElement);
    }
}
