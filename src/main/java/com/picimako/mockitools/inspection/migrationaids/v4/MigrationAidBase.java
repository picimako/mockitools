//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import static com.picimako.mockitools.util.ModuleDependencyHelper.isMockitoCore2xOr3xAvailableInModuleOf;
import static com.picimako.mockitools.util.ModuleDependencyHelper.isMockitoCore3xAvailableInModuleOf;
import static com.picimako.mockitools.util.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.inspection.MockitoolsBaseInspection;

/**
 * Provides base classes for migration aid inspections.
 */
public interface MigrationAidBase {

    abstract class V3ToV4BaseInspection extends MockitoolsBaseInspection {
        @Override
        public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
            return isInTestSourceContent(session.getFile()) && isMockitoCore3xAvailableInModuleOf(session.getFile(), holder.getProject()) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
        }
    }

    abstract class V23ToV4BaseInspection extends MockitoolsBaseInspection {
        @Override
        public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
            return isInTestSourceContent(session.getFile()) && isMockitoCore2xOr3xAvailableInModuleOf(session.getFile(), holder.getProject()) ? methodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
        }
    }
}
