//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.psi.CommonClassNames;
import com.siyeh.ig.callMatcher.CallMatcher;

import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ANSWER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCK_SETTINGS;
import static com.picimako.mockitools.MockitoQualifiedNames.SPY;
import static com.picimako.mockitools.MockitoolsPsiUtil.MOCKITO_MOCK;
import static com.siyeh.ig.callMatcher.CallMatcher.anyOf;
import static com.siyeh.ig.callMatcher.CallMatcher.staticCall;

/**
 * {@link CallMatcher}s for identifying calls to {@code Mockito.mock()} and {@code Mockito.spy()}.
 */
public final class MockitoMockMatchers {

    //Mock

    private static final String JAVA_LANG_CLASS = "java.lang.Class<T>";
    public static final CallMatcher MOCK = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS);
    public static final CallMatcher MOCK_WITH_NAME = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS, CommonClassNames.JAVA_LANG_STRING);
    public static final CallMatcher MOCK_WITH_ANSWER = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS, ORG_MOCKITO_ANSWER);
    public static final CallMatcher MOCK_WITH_SETTINGS = MOCKITO_MOCK.parameterTypes(JAVA_LANG_CLASS, ORG_MOCKITO_MOCK_SETTINGS);

    //Spy

    public static final CallMatcher MOCKITO_SPY_T = staticCall(ORG_MOCKITO_MOCKITO, SPY).parameterTypes("T");

    //Since Mockito 4.9.0

    public static final CallMatcher MOCK_GENERIC_INFERRED = MOCKITO_MOCK.parameterTypes("T...");
    public static final CallMatcher SPY_GENERIC_INFERRED = staticCall(ORG_MOCKITO_MOCKITO, SPY).parameterTypes("T...");
    private static final CallMatcher MOCK_WITH_NAME_GENERIC_INFERRED = MOCKITO_MOCK.parameterTypes(CommonClassNames.JAVA_LANG_STRING, "T...");
    private static final CallMatcher MOCK_WITH_ANSWER_GENERIC_INFERRED = MOCKITO_MOCK.parameterTypes(ORG_MOCKITO_ANSWER, "T...");
    private static final CallMatcher MOCK_WITH_SETTINGS_GENERIC_INFERRED = MOCKITO_MOCK.parameterTypes(ORG_MOCKITO_MOCK_SETTINGS, "T...");
    public static final CallMatcher MOCKS_GENERIC_INFERRED_WITH_CONFIG = anyOf(MOCK_WITH_NAME_GENERIC_INFERRED, MOCK_WITH_ANSWER_GENERIC_INFERRED, MOCK_WITH_SETTINGS_GENERIC_INFERRED);

    //Since Mockito 5.7.0

    public static final CallMatcher CAPTOR_GENERIC_INFERRED = CallMatcher.staticCall(MockitoQualifiedNames.ORG_MOCKITO_ARGUMENT_CAPTOR, "captor").parameterTypes("U...");

    //Other

    public static final CallMatcher MOCKITO_MOCK_OR_SPY = staticCall(ORG_MOCKITO_MOCKITO, MockitoQualifiedNames.MOCK, SPY);

    private MockitoMockMatchers() {
        //Utility class
    }
}
