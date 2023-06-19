//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import java.util.List;

import com.intellij.psi.PsiMethodCallExpression;

/**
 * Common interface for analyzing call chains.
 */
public interface CallChainAnalyzer {
    /**
     * Analyzes the argument call chain.
     *
     * @return true if the call chain satisfies the analyzer, false otherwise
     */
    boolean analyze(List<PsiMethodCallExpression> calls);
}
