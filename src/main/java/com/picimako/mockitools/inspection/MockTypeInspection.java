//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_SPY;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockableType;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoMock;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoSpy;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasAtLeastOneArgument;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;
import static com.picimako.mockitools.UnitTestPsiUtil.isUnitTest;
import static com.picimako.mockitools.inspection.ClassObjectAccessUtil.getOperandType;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports {@code @Mock} and {@code @Spy} annotated fields' types and the types specified as the arguments of
 * {@code Mockito.mock()} and {@code Mockito.spy()} calls, since certain types are not mockable by Mockito.
 * <p>
 * You can find the logic and the types in the linked resources.
 * <p>
 * {@code @Mock} and {@code @Spy} annotated fields are validated only in actual unit test classes, while Mockito calls are validated in any file
 * within test sources.
 *
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineDelegateByteBuddyMockMaker.java">InlineDelegateByteBuddyMockMaker#isTypeMockable(Class)</a>
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineBytecodeGenerator.java">InlineBytecodeGenerator#EXCLUDES</a>
 * @since 0.1.0
 */
public class MockTypeInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return isInTestSourceContent(session.getFile())
            ? isUnitTest(session.getFile()) ? fieldAndMethodCallVisitor(holder) : methodCallVisitor(holder)
            : PsiElementVisitor.EMPTY_VISITOR;
    }


    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //Mockito.spy method has overloads with single arguments
        if ((isMockitoMock(expression) && hasAtLeastOneArgument(expression)) || isMockitoSpy(expression)) {
            PsiExpression typeToMock = getFirstArgument(expression);
            if (!isMockableType(getOperandType(typeToMock))) {
                holder.registerProblem(typeToMock, MockitoolsBundle.inspection("non.mockable.type"));
            }
        }
    }

    @Override
    protected void checkField(PsiField field, @NotNull ProblemsHolder holder) {
        if ((field.hasAnnotation(ORG_MOCKITO_MOCK) || field.hasAnnotation(ORG_MOCKITO_SPY))
            && field.getTypeElement() != null && !isMockableType(field.getTypeElement().getType())) {
            holder.registerProblem(field.getTypeElement() != null ? field.getTypeElement() : field.getNameIdentifier(), MockitoolsBundle.inspection("non.mockable.type"));
        }
    }
}
