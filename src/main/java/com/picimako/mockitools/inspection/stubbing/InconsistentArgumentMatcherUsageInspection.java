//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.stubbing;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ADDITIONAL_MATCHERS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_MATCHERS;
import static com.picimako.mockitools.MockitoolsPsiUtil.isAdditionalMatchers;
import static com.picimako.mockitools.StubbingApproach.BDDMOCKITO_GIVEN;
import static com.picimako.mockitools.StubbingApproach.MOCKITO_DO_X;
import static com.picimako.mockitools.StubbingApproach.MOCKITO_WHEN;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiTypeCastExpression;
import com.picimako.mockitools.inspection.HasSonarLintAlternative;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * TODO: Additional customization option may be added in IDE settings to configure classes that also provide static methods for creating argument matchers,
 *  besides ArgumentMatchers and AdditionalMatchers.
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#argument_matchers">Argument matchers documentation</a>
 * @see <a href="https://javadoc.io/static/org.mockito/mockito-core/3.11.2/org/mockito/ArgumentMatchers.html">ArgumentMatchers javadoc</a>
 * @since 0.1.0
 */
@HasSonarLintAlternative("https://rules.sonarsource.com/java/tag/mockito/RSPEC-6073")
public class InconsistentArgumentMatcherUsageInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        //expression e.g.: "Mockito.when(mock.method(argument1, argument2))" or "BDDMockito.given(mock.method(argument1, argument2))"
        //if there is only one method call specified in Mockito.when() or BDDMockito.given()
        if (MOCKITO_WHEN.isStubbedBy(expression) || BDDMOCKITO_GIVEN.isStubbedBy(expression)) {
            MOCKITO_WHEN.getStubbedMethodCall(expression)
                //argument list: [argument1, argument2]
                .ifPresent(stubbedMethodCall -> findAndRegisterInconsistentArguments(stubbedMethodCall.getArgumentList(), holder));
        } else if (MOCKITO_DO_X.isStubbedBy(expression)) {
            /* The grandparent PsiMethodCallExpression is the subsequent method call in the call chain
             * e.g. expression is "Mockito.doReturn(10).when(mock)"
             * while grandparent is "Mockito.doReturn(10).when(mock).methodWithParams(String.class, Integer.class);"*/
            MOCKITO_DO_X.getStubbedMethodCall(expression).ifPresent(stubbedMethodCall -> findAndRegisterInconsistentArguments(stubbedMethodCall.getArgumentList(), holder));
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
                //To support matcher calls like 'any()'
                if (arg instanceof PsiMethodCallExpression potentialMatcher) {
                    hasMatcher = isArgumentMatcher(arg, getMethodName(potentialMatcher));
                }
                //To support constructs like '(Object) any()', and consider them as matchers. Mockito doesn't fail on these type of usages of matchers.
                else if (arg instanceof PsiTypeCastExpression typeCast && typeCast.getOperand() instanceof PsiMethodCallExpression potentialMatcher) {
                    hasMatcher = isArgumentMatcher(arg, getMethodName(potentialMatcher));
                } else {
                    hasNonMatcher = true;
                }

                if (hasNonMatcher && hasMatcher) {
                    holder.registerProblem(arguments, MockitoolsBundle.message("inspection.inconsistent.argument.matchers"));
                    break;
                }
            }
        }
    }

    private boolean isArgumentMatcher(PsiExpression arg, String methodName) {
        return CallMatcher.anyOf(
            staticCall(ORG_MOCKITO_ARGUMENT_MATCHERS, methodName),
            staticCall(ORG_MOCKITO_ADDITIONAL_MATCHERS, methodName)
        ).matches(arg);
    }
}
