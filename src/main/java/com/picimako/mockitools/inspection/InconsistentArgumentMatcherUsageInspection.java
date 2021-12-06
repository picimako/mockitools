//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ADDITIONAL_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoolsPsiUtil.isAdditionalMatchers;
import static com.picimako.mockitools.MockitoolsPsiUtil.isBDDMockitoGiven;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoDoXWhen;
import static com.picimako.mockitools.MockitoolsPsiUtil.isMockitoWhen;
import static com.picimako.mockitools.PsiMethodUtil.getFirstArgument;
import static com.picimako.mockitools.PsiMethodUtil.hasSubsequentMethodCall;
import static com.picimako.mockitools.UnitTestPsiUtil.isInTestSourceContent;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethodCallExpression;
import com.siyeh.ig.callMatcher.CallMatcher;
import com.siyeh.ig.psiutils.MethodCallUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Reports argument matchers used inconsistently, as in during stubbing all stubbed call arguments should either be matchers or non-matchers.
 * Mixed usage is not permitted by Mockito.
 * <p>
 * Supported ways of stubbing:
 * <ul>
 *     <li>{@code Mockito.when()}</li>
 *     <li>{@code BDDMockito.given()}</li>
 *     <li>{@code Mockito.do...().when()}</li>
 *     <li>matchers in {@code AdditionalMatchers}</li>
 * </ul>
 * <p>
 * Only unit test classes (class name ending with Test) are considered, since (in ordinary projects) Mockito is supposed to be used only in test classes.
 * <p>
 * TODO: Additional customization option may be added in IDE settings to configure classes that also provide static methods for creating argument matchers,
 *  besides ArgumentMatchers and AdditionalMatchers.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#argument_matchers">Argument matchers documentation</a>
 * @see <a href="https://javadoc.io/static/org.mockito/mockito-core/3.11.2/org/mockito/ArgumentMatchers.html">ArgumentMatchers javadoc</a>
 * @since 0.1.0
 */
public class InconsistentArgumentMatcherUsageInspection extends MockitoolsBaseInspection {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return !isInTestSourceContent(session.getFile()) ? PsiElementVisitor.EMPTY_VISITOR : methodCallVisitor(holder);
    }

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //expression e.g.: "Mockito.when(mock.method(argument1, argument2))" or "BDDMockito.given(mock.method(argument1, argument2))"
        //if there is only one method call specified in Mockito.when() or BDDMockito.given()
        if (isMockitoWhen(expression) || isBDDMockitoGiven(expression)) {
            //mockingCall: mock.method(argument1, argument2)
            PsiExpression mockingCall = getFirstArgument(expression);
            if (mockingCall instanceof PsiMethodCallExpression) {
                //argument list: [argument1, argument2]
                findAndRegisterInconsistentArguments(((PsiMethodCallExpression) mockingCall).getArgumentList(), holder);
            }
        } else if (isMockitoDoXWhen(expression)) {
            /* The grandparent PsiMethodCallExpression is the subsequent method call in the call chain
             * e.g. expression is "Mockito.doReturn(10).when(mock)"
             * while grandparent is "Mockito.doReturn(10).when(mock).methodWithParams(String.class, Integer.class);"*/
            if (hasSubsequentMethodCall(expression)) {
                PsiMethodCallExpression mockMethodCall = (PsiMethodCallExpression) expression.getParent().getParent();
                findAndRegisterInconsistentArguments(mockMethodCall.getArgumentList(), holder);
            }
        } else if (isAdditionalMatchers(expression)) {
            //expression e.g.: AdditionalMatchers.and(argument1, argument2)
            findAndRegisterInconsistentArguments(expression.getArgumentList(), holder);
        }
    }

    private void findAndRegisterInconsistentArguments(@Nullable PsiExpressionList arguments, @NotNull ProblemsHolder holder) {
        //If there is only 0 or 1 argument, then it is correct by default. Either 1 matcher, 1 non-matcher or no argument is present.
        if (arguments != null && arguments.getExpressions().length > 1) {
            boolean hasNonMatcher = false;
            boolean hasMatcher = false;
            //Iterates through the list of arguments, and if there is at least one matcher and non-matcher, then the arguments are invalid
            for (var arg : arguments.getExpressions()) {
                if (arg instanceof PsiMethodCallExpression) {
                    String methodName = MethodCallUtils.getMethodName((PsiMethodCallExpression) arg);
                    hasMatcher = isArgumentMatcher(arg, methodName);
                } else {
                    hasNonMatcher = true;
                }
                if (hasNonMatcher && hasMatcher) {
                    holder.registerProblem(arguments, MockitoolsBundle.inspection("inconsistent.argument.matchers"));
                    break;
                }
            }
        }
    }

    private boolean isArgumentMatcher(PsiExpression arg, String methodName) {
        return CallMatcher.anyOf(
            CallMatcher.staticCall(ORG_MOCKITO_ARGUMENT_MATCHERS, methodName),
            CallMatcher.staticCall(ORG_MOCKITO_ADDITIONAL_MATCHERS, methodName)
        ).matches(arg);
    }
}
