//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.dsl;

import static com.intellij.psi.CommonClassNames.JAVA_LANG_CLASS;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SETTINGS;
import static com.picimako.mockitools.MockitoolsPsiUtil.MOCKITO_MOCK;
import static com.picimako.mockitools.util.PsiMethodUtil.collectCallsInChainFromLast;
import static com.siyeh.ig.psiutils.MethodCallUtils.getMethodName;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiVariable;
import com.picimako.mockitools.util.PsiMethodUtil;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Utilities for Mockito's {@code MockSettings} class.
 *
 * @since 0.8.0
 */
public final class MockSettings {

    private static final CallMatcher MOCK_WITH_SETTINGS = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS, ORG_MOCKITO_MOCK_SETTINGS);

    /**
     * Returns the PSI expression representing the {@code MockSettings} call chain from the provided
     * {@code Type mock = Mockito.mock(Type.class, MockSettings)} type local mock variable.
     *
     * @return the MockSettings call chain expression, or empty optional if there is no such argument
     */
    public static Optional<PsiExpression> fromMockVariable(PsiLocalVariable mockVariable) {
        return Optional.of(mockVariable) //e.g. 'Type mock = Mockito.mock(Type.class, withSettings().stubOnly());'
            .map(PsiVariable::getInitializer) //e.g. 'Mockito.mock(Type.class, withSettings().stubOnly());'
            .filter(PsiMethodCallExpression.class::isInstance)
            .map(PsiMethodCallExpression.class::cast)
            .filter(MOCK_WITH_SETTINGS::matches)
            .map(PsiMethodUtil::get2ndArgument); //e.g. withSettings().stubOnly()
    }

    /**
     * Returns whether the provided {@code MockSettings} call chain has a call to a method with {@code methodName}.
     *
     * @param mockSettings the MockSettings call chain expression, e.g. 'withSettings().stubOnly().name("name")'
     * @param methodName   the MockSettings method name to look for
     */
    public static boolean hasCallTo(@NotNull PsiExpression mockSettings, @NotNull String methodName) {
        return collectCallsInChainFromLast(mockSettings).stream().anyMatch(call -> methodName.equals(getMethodName(call)));
    }

    private MockSettings() {
        //utility class
    }
}
