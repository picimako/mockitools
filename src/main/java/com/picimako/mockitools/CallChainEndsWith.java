//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;
import lombok.RequiredArgsConstructor;

/**
 * Analyzes a call chain whether the penultimate call is a call to a predefined method name, either {@code when()} or {@code given()}, in which the mock
 * object, or the call to a mock object is specified.
 */
@RequiredArgsConstructor
public final class CallChainEndsWith implements CallChainAnalyzer {

    public static final CallChainEndsWith ENDS_WITH_WHEN = new CallChainEndsWith("when");
    public static final CallChainEndsWith ENDS_WITH_GIVEN = new CallChainEndsWith("given");

    private final String stubbingCallName;

    @Override
    public boolean analyze(List<PsiMethodCallExpression> calls) {
        return calls.size() >= 2 && stubbingCallName.equals(getMethodName(calls.get(calls.size() - 2)));
    }
}
