//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.intellij.psi.JavaPsiFacade.getElementFactory;
import static com.intellij.psi.search.GlobalSearchScope.moduleWithLibrariesScope;

import java.util.Optional;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Replaces code reference elements with the predefined class ({@link #replacementClassFqn}).
 * <p>
 * This quick fix is for locations where it matters whether the qualifier of the reference should remain untouched,
 * due to a possible name collision between the original and the replacement names.
 * <p>
 * If the reference is qualified, the reference is replaced with the predefined fully qualified name,
 * otherwise it is the import statement of the reference that is replaced.
 */
public class NameCollisionAwareReferenceReplacerQuickFix extends MigrationAidV4BaseQuickFix {

    private final String nameMessageKey;
    private final String replacementClassFqn;

    public NameCollisionAwareReferenceReplacerQuickFix(String nameMessageKey, String replacementClassFqn) {
        this.nameMessageKey = nameMessageKey;
        this.replacementClassFqn = replacementClassFqn;
    }

    @Override
    public @IntentionName @NotNull String getName() {
        return MockitoolsBundle.quickFix(nameMessageKey);
    }

    @Override
    protected void doFix(Project project, ProblemDescriptor descriptor) {
        var element = (PsiJavaCodeReferenceElement) descriptor.getPsiElement();
        Module module = ModuleUtilCore.findModuleForFile(element.getContainingFile().getVirtualFile(), project);
        if (module == null) return;

        if (element.isQualified()) {
            element.replace(getElementFactory(project).createReferenceElementByFQClassName(replacementClassFqn, moduleWithLibrariesScope(module)));
        } else {
            var containingFile = (PsiJavaFile) element.getContainingFile();
            Optional.ofNullable(containingFile.findImportReferenceTo((PsiClass) element.resolve()))
                .ifPresent(importStmt -> importStmt.replace(getElementFactory(project).createFQClassNameReferenceElement(replacementClassFqn, moduleWithLibrariesScope(module))));
        }
    }

}
