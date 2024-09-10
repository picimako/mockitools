//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.stubbing;

import static com.picimako.mockitools.resources.MockitoolsBundle.message;
import static com.picimako.mockitools.util.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.util.PsiMethodUtil.getSubsequentMethodCall;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiModifier;
import com.picimako.mockitools.StubbingApproach;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.util.PsiMethodUtil;
import com.siyeh.ig.psiutils.MethodUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Reports stubbing of private, native, {@code equals()} and {@code hashCode()} methods.
 *
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java#L118">Reporter.missingMethodInvocation()</a>
 * @see <a href="https://github.com/mockito/mockito/pull/3283/files">Add native method to exception message</a>
 * @since 1.3.0
 */
final class CannotStubMethodInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //Validates the first argument of the method call to the stubbed method:
        // - Mockito.when(<stub>)
        // - BDDMockito.given(<stub>)
        if (StubbingApproach.MOCKITO_WHEN.isStubbedBy(expression) || StubbingApproach.BDDMOCKITO_GIVEN.isStubbedBy(expression)) {
            if (getFirstArgument(expression) instanceof PsiMethodCallExpression callToStubbedMethod) {
                checkCallToStubbedMethod(holder, callToStubbedMethod, () -> callToStubbedMethod);
            }
        }
        //Validates the method call to the stubbed method after the current stubber method:
        // - Mockito.do*().when().<stub>()
        // - BDDMockito.will*().given().<stub>()
        else if (StubbingApproach.MOCKITO_DO_X.isStubbedBy(expression) || StubbingApproach.BDDMOCKITO_WILL_X.isStubbedBy(expression)) {
            var callToStubbedMethod = getSubsequentMethodCall(expression);
            if (callToStubbedMethod != null)
                checkCallToStubbedMethod(holder, callToStubbedMethod, () -> PsiMethodUtil.getReferenceNameElement(callToStubbedMethod));
        }
    }

    @SuppressWarnings("DialogTitleCapitalization")
    private static void checkCallToStubbedMethod(@NotNull ProblemsHolder holder, PsiMethodCallExpression callToStubbedMethod, Supplier<PsiElement> elementToRegister) {
        var stubbedMethod = callToStubbedMethod.resolveMethod();
        if (stubbedMethod != null) {
            //'hashCode()' is checked before native/private, so that, because hashCode() is native,
            // the inspection message will say hashCode() instead of native method.
            if (MethodUtils.isHashCode(stubbedMethod) || MethodUtils.isEquals(stubbedMethod)) {
                holder.registerProblem(elementToRegister.get(), message("inspection.equals.and.hashcode.cant.be.stubbed"));
            }
            //'final' is not checked because there are ways to stub final methods
            else if (stubbedMethod.hasModifierProperty(PsiModifier.PRIVATE) || stubbedMethod.hasModifierProperty(PsiModifier.NATIVE)) {
                holder.registerProblem(elementToRegister.get(), message("inspection.private.and.native.methods.cant.be.stubbed"));
            }
        }
    }
}
