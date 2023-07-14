//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.captor;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_CAPTOR;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_CAPTOR;
import static com.picimako.mockitools.MockitoolsPsiUtil.isOfTypeArgumentCaptor;
import static com.picimako.mockitools.util.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Fields that are annotated as {@code @Captor} must have {@code ArgumentCaptor} as their type, otherwise Mockito will fail
 * with a message saying the type must be ArgumentCaptor.
 * <p>
 * This inspection reports fields that are annotated as {@code @Captor} but their types are not {@code ArgumentCaptor}.
 * <p>
 * It also provides a quick fix to convert the field type to ArgumentCaptor with the appropriate generic type.
 *
 * @since 0.1.0
 */
public class CaptorFieldOfTypeArgumentCaptorInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? fieldVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkField(PsiField field, @NotNull ProblemsHolder holder) {
        if (field.hasAnnotation(ORG_MOCKITO_CAPTOR) && !isOfTypeArgumentCaptor(field)) {
            holder.registerProblem(field.getNameIdentifier(), MockitoolsBundle.message("inspection.captor.field.must.be.argument.captor"),
                new ConvertFieldTypeToArgumentCaptorQuickFix());
        }
    }

    /**
     * This quick fix doesn't deal with importing the original types because they should already be imported.
     * <p>
     * With that said, it imports {@code org.mockito.ArgumentCaptor} upon applying the quick fix.
     */
    private static final class ConvertFieldTypeToArgumentCaptorQuickFix extends CaptorFieldBaseQuickFix {

        @Override
        public @IntentionName @NotNull String getName() {
            return MockitoolsBundle.message("quick.fix.captor.field.convert.to.argumentcaptor");
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            PsiElement context = descriptor.getPsiElement().getContext();
            if (context instanceof PsiField) {
                PsiTypeElement fieldType = ((PsiField) context).getTypeElement();
                if (fieldType != null) {
                    PsiType type = fieldType.getType();
                    //If the field type is a primitive, then it converts it to its boxed type, otherwise leaves it as it is.
                    PsiType boxedType = type instanceof PsiPrimitiveType ? ((PsiPrimitiveType) type).getBoxedType(context) : type;
                    if (boxedType != null) {
                        PsiElement replaced = fieldType.replace(JavaPsiFacade.getElementFactory(project)
                            .createTypeElementFromText(ORG_MOCKITO_ARGUMENT_CAPTOR + "<" + boxedType.getCanonicalText() + ">", context));
                        JavaCodeStyleManager.getInstance(project).shortenClassReferences(replaced);
                    }
                }
            }
        }
    }
}
