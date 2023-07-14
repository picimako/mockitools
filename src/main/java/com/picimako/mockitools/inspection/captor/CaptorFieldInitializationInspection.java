//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.captor;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_CAPTOR;
import static com.picimako.mockitools.MockitoolsPsiUtil.isOfTypeArgumentCaptor;
import static com.picimako.mockitools.util.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Since {@code @Captor} annotated fields are initialized automagically by Mockito via {@code MockitoJUnitRunner}, {@code MockitoJUnit.rule()} or
 * {@code MockitoAnnotations.initMocks()} / {@code MockitoAnnotations.openMocks()}, there is no need for explicit initialization of them.
 * <p>
 * Explicit initializer is reported only when the field is annotated as {@code @Captor} and its type is {@code ArgumentCaptor}.
 * Otherwise {@link CaptorFieldOfTypeArgumentCaptorInspection} will take effect.
 * <p>
 * It also provides a quick fix to remove the field initializer.
 *
 * @see CaptorFieldOfTypeArgumentCaptorInspection
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Captor.html">@Captor javadoc</a>
 * @see <a href="https://www.baeldung.com/mockito-annotations">Baeldung - Mockito Annotations</a>
 * @since 0.1.0
 */
public class CaptorFieldInitializationInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? fieldVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkField(PsiField field, @NotNull ProblemsHolder holder) {
        if (field.hasAnnotation(ORG_MOCKITO_CAPTOR) && isOfTypeArgumentCaptor(field) && field.hasInitializer()) {
            holder.registerProblem(field.getInitializer(), MockitoolsBundle.message("inspection.captor.field.init.not.required"),
                new RemoveArgumentCaptorInitQuickFix());
        }
    }

    private static final class RemoveArgumentCaptorInitQuickFix extends CaptorFieldBaseQuickFix {

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.captor.field.remove.init");
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            descriptor.getPsiElement().delete();
        }
    }
}
