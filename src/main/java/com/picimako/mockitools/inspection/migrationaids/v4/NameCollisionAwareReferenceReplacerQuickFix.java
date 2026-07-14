//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * Replaces code reference elements with the predefined class ({@link replacementClassFqn}).
 * <p>
 * This quick fix is for locations where it matters whether the qualifier of the reference should remain untouched,
 * due to a possible name collision between the original and the replacement names.
 * <p>
 * If the reference is qualified, the reference is replaced with the predefined fully qualified name,
 * otherwise it is the import statement of the reference that is replaced.
 *
 * @see NameCollisionlessReferenceReplacerQuickFix
 */
public final class NameCollisionAwareReferenceReplacerQuickFix extends ReferenceReplacerQuickFix {

    public NameCollisionAwareReferenceReplacerQuickFix(String nameMessageKey, String replacementClassFqn) {
        super(nameMessageKey, replacementClassFqn);
    }

    @Override
    protected void doFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor, PsiElement problemElement) {
        var element = (PsiJavaCodeReferenceElement) problemElement;
        var containingFile = (PsiJavaFile) element.getContainingFile();
        Module module = ModuleUtilCore.findModuleForFile(containingFile.getVirtualFile(), project);
        if (module == null) return;

        if (element.isQualified()) {
            element.replace(createRefElementByFQN(project, module));
        } else {
            var importReference = containingFile.findImportReferenceTo((PsiClass) element.resolve());
            if (importReference != null) importReference.replace(createClassNameRefElement(project, module));
        }
    }
}
