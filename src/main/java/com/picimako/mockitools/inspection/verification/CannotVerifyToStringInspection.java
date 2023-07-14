//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromFirst;
import static com.picimako.mockitools.util.PsiMethodUtil.getReferenceNameElement;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethodCallExpression;
import com.picimako.mockitools.VerificationApproach;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Report calls to {@code toString()} in mock verifications, since Mockito cannot verify toString().
 * <p>
 * The inspection doesn't distinguish between {@code toString()} calls that come directly as overridden methods
 * from the mock objects' classes, or when they come from a type deeper in the class hierarchy.
 *
 * @see <a href="https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java#L719">Cannot verify toString()</a>
 * @since 0.7.0
 */
public class CannotVerifyToStringInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        VerificationApproach.NON_MOCKED_STATIC_APPROACHES.stream()
            .filter(approach -> approach.isVerifiedBy(expression))
            .filter(approach -> approach.isValid(expression))
            .findFirst()
            .flatMap(approach -> approach.getVerifiedMethodCall(collectCallsInChainFromFirst(expression, true)))
            .filter(call -> "toString".equals(getMethodName(call)))
            .ifPresent(verifiedMethodCall -> holder.registerProblem(getReferenceNameElement(verifiedMethodCall), MockitoolsBundle.message("inspection.cannot.verify.to.string")));
    }
}
