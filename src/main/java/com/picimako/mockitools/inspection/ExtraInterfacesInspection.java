//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoQualifiedNames.EXTRA_INTERFACES;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoolsPsiUtil.isExtraInterfaces;
import static com.picimako.mockitools.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.picimako.mockitools.ClassObjectAccessUtil.getOperandType;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.PsiImplUtil;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Verifies the {@code @Mock} annotation's {@code extraInterfaces} attribute, as well as the arguments of
 * {@code Mockito.withSettings().extraInterfaces()} calls whether the types provided there are actual interfaces.
 * <p>
 * It also validates if there is at least one argument specified in {@code extraInterfaces()} calls.
 * <p>
 * These issues all would prevent Mockito logic and unit tests to execute properly. Mockito would throw an exception
 * with the appropriate message.
 * <p>
 * Mockito annotations are validated only in actual unit test classes, while Mockito calls are validated in any file
 * within test sources.
 *
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java">Reporter.java: look for extraInterfacesAcceptsOnlyInterfaces(Class)</a>
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java">Report.java: look for extraInterfacesRequiresAtLeastOneInterface()</a>
 * @since 0.1.0
 */
public class ExtraInterfacesInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? annotationAndMethodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkAnnotation(PsiAnnotation annotation, @NotNull ProblemsHolder holder) {
        if (annotation.hasQualifiedName(ORG_MOCKITO_MOCK)) {
            var extraInterfaces = PsiImplUtil.findAttributeValue(annotation, EXTRA_INTERFACES);
            //e.g. @Mock(extraInterfaces = {Set.class, Object.class}), or @Mock(extraInterfaces = Set.class)
            for (var initializer : AnnotationUtil.arrayAttributeValues(extraInterfaces)) {
                checkAndReportNonInterfaceArgument(initializer, "annotation.extra.interfaces.not.interface", holder);
            }
        }
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        if (isExtraInterfaces(expression) && !expression.getArgumentList().isEmpty()) {
            for (var argument : getArguments(expression)) {
                checkAndReportNonInterfaceArgument(argument, "extra.interfaces.not.interface", holder);
            }
        }
    }

    private void checkAndReportNonInterfaceArgument(PsiElement argument, String inspectionMessageKey, @NotNull ProblemsHolder holder) {
        //e.g. argument: List.class, operandType: List
        PsiType operandType = getOperandType(argument);
        if (operandType instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) operandType).resolve();
            if (psiClass != null && !psiClass.isInterface()) {
                holder.registerProblem(argument, MockitoolsBundle.inspection(inspectionMessageKey));
            }
        }
    }
}
