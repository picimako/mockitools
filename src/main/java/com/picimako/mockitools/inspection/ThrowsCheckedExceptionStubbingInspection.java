//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.util.ExceptionUtil.isCheckedException;
import static com.picimako.mockitools.util.PointersUtil.toPointer;
import static com.picimako.mockitools.util.PsiMethodUtil.getArguments;
import static com.picimako.mockitools.util.PsiMethodUtil.getSubsequentMethodCall;
import static com.picimako.mockitools.util.EvaluationHelper.evaluateClassObjectOrNewExpressionType;
import static com.picimako.mockitools.util.EvaluationHelper.evaluateType;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiCall;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.util.PsiTreeUtil;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.StubType;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

/**
 * Reports exception references in {@code *Throw()} stubbing methods based on Mockito's rule on checked exceptions:
 * <i>If [the specified exception types] contain a checked exception then it has to match one of the checked exceptions of method signature.</i>
 * <p>
 * The following constructs are supported:
 * <ul>
 *     <li>{@code Mockito.when().thenThrow()} including further chained {@code thenThrow()} calls</li>
 *     <li>{@code BDDMockito.given().willThrow()} including further chained {@code willThrow()} calls</li>
 *     <li>{@code Mockito.doThrow().when()}</li>
 *     <li>{@code Mockito.doThrow().doThrow().when()}</li>
 *     <li>{@code BDDMockito.willThrow().given()}</li>
 *     <li>{@code BDDMockito.willThrow().willThrow().given()}</li>
 * </ul>
 * <p>
 * In case of an empty list, no problem is reported.
 *
 * @since 0.3.0
 */
public class ThrowsCheckedExceptionStubbingInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        Arrays.stream(StubbingApproach.values())
            .filter(approach -> approach.getExceptionStubber().isApplicableTo(expression))
            .findFirst()
            .ifPresent(approach -> checkAndRegister(approach, expression, holder));
    }

    /**
     * Retrieves the method that is being stubbed on a mock object, resolves the original method and compares the exceptions
     * specified in the {@code *Throw()} call with the ones in the resolved method's {@code throws} clause.
     * <p>
     * If an exception specified during stubbing is a checked exception, but it is not listed in the {@code throws} clause,
     * it is then reported.
     * <p>
     * The return value signals whether the descriptor matched the current method call expression.
     * It helps shorten the code in {@link #checkMethodCallExpression(PsiMethodCallExpression, ProblemsHolder)}.
     */
    private void checkAndRegister(StubbingApproach approach, PsiMethodCallExpression expression, ProblemsHolder holder) {
        var stubbedExceptions = getArguments(expression);
        if (stubbedExceptions.length == 0) return;

        //e.g. 'mockObject.doSomething()' from either of 'when(mockObject.doSomething())' or 'given(mockObject).doSomething()'
        approach.getStubbedMethodCallAnywhere(expression)
            .map(stub -> resolveStubbedMethod(stub, approach.stubType)) //doSomething()
            .ifPresent(stubbedMethod -> {
                var exceptionTypesInThrowsClause = stubbedMethod.getThrowsList().getReferencedTypes();
                for (var stubbedException : stubbedExceptions) {
                    //If the stubbed exception is a checked one, and it is not present in the method's throws clause
                    if (isCheckedException(stubbedException)
                        && Arrays.stream(exceptionTypesInThrowsClause).noneMatch(type -> type.equals(evaluateType(stubbedException)))) {
                        holder.registerProblem(stubbedException,
                            MockitoolsBundle.inspection("invalid.checked.exception.in.stubbing"),
                            new AddExceptionToThrowsClauseQuickFix(toPointer(stubbedMethod)));
                    }
                }
            });
    }

    /**
     * Resolves the method that is being stubbed.
     * <p>
     * In case of {@link StubType#STUBBING}, e.g. {@code when(mockObject.doSomething()).then...()} it resolves the argument call.
     * <p>
     * In case of {@link StubType#STUBBER}, e.g. {@code do...().given(mockObject).doSomething()} it finds the subsequent method
     * to the one that the mock object is specified in: subsequent to {@code given()} in this case, resulting in {@code doSomething()}.
     */
    @Nullable
    private PsiMethod resolveStubbedMethod(PsiExpression stub, StubType stubType) {
        return stubType == StubType.STUBBING
            ? ((PsiMethodCallExpression) stub).resolveMethod()
            : Optional.ofNullable(getSubsequentMethodCall(PsiTreeUtil.getParentOfType(stub, PsiMethodCallExpression.class)))
            .map(PsiCall::resolveMethod)
            .orElse(null);
    }

    /**
     * Quick fix to add the stubbed checked exception to the {@code throws} clause of the stubbed method,
     * if it is not specified there.
     * <p>
     * For example:
     * <pre>
     * MockObject mock = Mockito.mock(MockObject.class);
     * Mockito.when(mock.doSomething()).thenThrow(IOException.class);
     *
     * //From:
     * private static class MockObject {
     *     public int doSomething() throws NoSuchMethodException {
     *         return 0;
     *     }
     * }
     *
     * //to:
     * private static class MockObject {
     *     public int doSomething() throws NoSuchMethodException, IOException {
     *         return 0;
     *     }
     * }
     * </pre>
     *
     * @since 0.5.0
     */
    private static final class AddExceptionToThrowsClauseQuickFix extends InspectionGadgetsFix {
        private final SmartPsiElementPointer<PsiMethod> stubbedMethod;

        public AddExceptionToThrowsClauseQuickFix(SmartPsiElementPointer<PsiMethod> stubbedMethod) {
            this.stubbedMethod = stubbedMethod;
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) {
            var exception = descriptor.getPsiElement();
            if (!(exception instanceof PsiExpression)) return;

            PsiType stubbedException = evaluateClassObjectOrNewExpressionType(exception);
            if (stubbedException instanceof PsiClassType) {
                var stubbedExceptionRef =
                    JavaPsiFacade.getElementFactory(project).createReferenceElementByType((PsiClassType) stubbedException);
                stubbedMethod.getElement().getThrowsList().add(stubbedExceptionRef);
            }
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MockitoolsBundle.quickFix("add.exception.to.method.throws.clause");
        }
    }
}
