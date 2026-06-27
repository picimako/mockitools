//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Base implementation for quick fixes. Based on the original {@code com.siyeh.ig.InspectionGadgetsFix}.
 */
public abstract class BaseQuickFix implements LocalQuickFix {

    public final void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        var problemElement = descriptor.getPsiElement();
        if (problemElement != null && problemElement.isValid()) {
            try {
                doFix(project, descriptor, problemElement);
            } catch (IncorrectOperationException e) {
                Logger.getInstance(this.getClass().getName()).error(e);
            }
        }
    }

    protected abstract void doFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor, PsiElement problemElement);
}
