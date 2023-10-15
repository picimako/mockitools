//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.picimako.mockitools.VerificationApproach;
import com.picimako.mockitools.dsl.MockObject;
import com.picimako.mockitools.inspection.MockitoolsBaseInspection;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Reports mock objects referenced in verifications (e.g. in {@code Mockito.verify()}),
 * when the mocks are configured as stub-only.
 * <p>
 * Spies and MockedStatic mocks are ignored since they have no stub-only configuration.
 *
 * @since 0.8.0
 */
final class StubOnlyMockInVerificationInspection extends MockitoolsBaseInspection {

    @Override
    protected void checkMethodCallExpression(PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        var verifiedMock = VerificationApproach.NON_MOCKED_STATIC_APPROACHES.stream()
            .filter(approach -> approach.isVerifiedBy(expression))
            .findFirst()
            .map(approach -> approach.getVerifiedMock(expression));
        verifiedMock
            .filter(PsiReferenceExpression.class::isInstance)
            .map(PsiReferenceExpression.class::cast)
            .map(MockObject::isStubOnly)
            .ifPresent(isStubOnly -> {
                if (isStubOnly)
                    holder.registerProblem(verifiedMock.get(), MockitoolsBundle.message("inspection.stub.only.mock.used.in.verification"));
            });
    }
}
