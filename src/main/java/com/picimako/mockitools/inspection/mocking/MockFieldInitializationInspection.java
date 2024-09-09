//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_INJECT_MOCKS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.util.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

/**
 * Since {@code @Mock} and {@code InjectMocks} annotated fields are initialized automagically by Mockito via {@code MockitoJUnitRunner}, {@code MockitoJUnit.rule()} or
 * {@code MockitoAnnotations.initMocks()} / {@code MockitoAnnotations.openMocks()}, there is no need for explicit initialization of them.
 * <p>
 * Explicit initializer is reported only when the field is annotated as either {@code @Mock} or {@code InjectMocks}.
 * <p>
 * It also provides a quick fix to remove the field initializer.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#mock_annotation">@Mock annotation</a>
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#21">@InjectMocks annotation</a>
 * @see <a href="https://www.baeldung.com/mockito-annotations">Baeldung - Mockito Annotations</a>
 * @since 1.3.0
 */
final class MockFieldInitializationInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? fieldVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkField(PsiField field, @NotNull ProblemsHolder holder) {
        if ((field.hasAnnotation(ORG_MOCKITO_MOCK) || field.hasAnnotation(ORG_MOCKITO_INJECT_MOCKS)) && field.hasInitializer()) {
            holder.registerProblem(field.getInitializer(), MockitoolsBundle.message("inspection.mock.field.init.not.required"),
                new RemoveFieldInitQuickFix());
        }
    }

    private static final class RemoveFieldInitQuickFix extends InspectionGadgetsFix {

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.field.remove.init");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MockitoolsBundle.message("quick.fix.mock.field.family.name");
        }

        @Override
        protected void doFix(@NotNull Project project, ProblemDescriptor descriptor) {
            descriptor.getPsiElement().delete();
        }
    }
}
