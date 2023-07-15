//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import static com.picimako.mockitools.MockableTypesUtil.getDoNotMockTypeInHierarchy;
import static com.picimako.mockitools.MockableTypesUtil.isMockableType;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_SPY;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoMock;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoSpy;
import static com.picimako.mockitools.util.EvaluationHelper.evaluateClassObjectOrNewExpressionType;
import static com.picimako.mockitools.util.PsiMethodUtil.*;
import static com.picimako.mockitools.util.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Reports {@code @Mock} and {@code @Spy} annotated fields' types and the types specified as the arguments of
 * {@code Mockito.mock()} and {@code Mockito.spy()} calls, since certain types are not mockable by Mockito, or are annotated as
 * {@code @DoNotMock} be it Mockito's own such annotation or a custom one.
 * <p>
 * You can find the logic and the types in the linked resources.
 * <p>
 * {@code @Mock} and {@code @Spy} annotated fields are validated only in actual unit test classes, while Mockito calls are validated in any file
 * within test sources.
 * <p>
 * Types' class hierarchy is inspected and the first type annotated with {@code @DoNotMock} is returned, and used for constructing the inspection message
 * including the annotation's reason in it, if there is any.
 *
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineDelegateByteBuddyMockMaker.java">InlineDelegateByteBuddyMockMaker#isTypeMockable(Class)</a>
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineBytecodeGenerator.java">InlineBytecodeGenerator#EXCLUDES</a>
 * @see <a href="https://github.com/picimako/mockitools/issues/2">Mocktiools: Add support for the @DoNotMock annotation</a>
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/DoNotMock.html">@DoNotMock javadoc</a>
 * @see <a href="https://github.com/mockito/mockito/pull/1833/files">Mockito: Add annotation to mark a type as DoNotMock</a>
 * @since 0.1.0
 */
public class MockTypeInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile()) ? fieldAndMethodCallVisitor(holder) : PsiElementVisitor.EMPTY_VISITOR;
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //Mockito.spy method has overloads only with single arguments
        if (isMockitoSpy(expression) || (isMockitoMock(expression) && hasArgument(expression))) {
            var typeToMock = getFirstArgument(expression);
            var type = evaluateClassObjectOrNewExpressionType(typeToMock);
            if (type != null) registerIfNotMockable(type, typeToMock, holder);
        }
    }

    @Override
    protected void checkField(PsiField field, @NotNull ProblemsHolder holder) {
        if ((field.hasAnnotation(ORG_MOCKITO_MOCK) || field.hasAnnotation(ORG_MOCKITO_SPY)) && field.getTypeElement() != null) {
            registerIfNotMockable(field.getTypeElement().getType(), getPartToHighLight(field), holder);
        }
    }

    private void registerIfNotMockable(PsiType type, PsiElement toHighlight, @NotNull ProblemsHolder holder) {
        if (!isMockableType(type))
            holder.registerProblem(toHighlight, MockitoolsBundle.message("inspection.non.mockable.type"));
        else checkForDoNotMockType(type, holder, toHighlight);
    }

    private PsiElement getPartToHighLight(PsiField field) {
        return field.getTypeElement() != null ? field.getTypeElement() : field.getNameIdentifier();
    }

    private void checkForDoNotMockType(PsiType type, @NotNull ProblemsHolder holder, PsiElement toHighlight) {
        getDoNotMockTypeInHierarchy(type).ifPresent(doNotMockType ->
            holder.registerProblem(toHighlight, doNotMockType.hasReason()
                ? MockitoolsBundle.message("inspection.non.mockable.type.do.not.mock.reason", doNotMockType.reason())
                : MockitoolsBundle.message("inspection.non.mockable.type.do.not.mock.no.reason")));
    }
}
