//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

/**
 * Qualified names for Mockito classes.
 */
public final class MockitoQualifiedNames {

    //Classes
    public static final String ORG_MOCKITO_MOCKITO = "org.mockito.Mockito";
    public static final String ORG_MOCKITO_BDDMOCKITO = "org.mockito.BDDMockito";
    public static final String ORG_MOCKITO_BDD_MY_ONGOING_STUBBING = "org.mockito.BDDMockito.BDDMyOngoingStubbing";
    public static final String ORG_MOCKITO_BDD_STUBBER = "org.mockito.BDDMockito.BDDStubber";
    public static final String ORG_MOCKITO_ARGUMENT_MATCHERS = "org.mockito.ArgumentMatchers";
    public static final String ORG_MOCKITO_ADDITIONAL_MATCHERS = "org.mockito.AdditionalMatchers";
    public static final String ORG_MOCKITO_STUBBING_STUBBER = "org.mockito.stubbing.Stubber";
    public static final String ORG_MOCKITO_STUBBING_BASESTUBBER = "org.mockito.stubbing.BaseStubber";
    public static final String ORG_MOCKITO_ONGOING_STUBBING = "org.mockito.stubbing.OngoingStubbing";
    public static final String ORG_MOCKITO_ARGUMENT_CAPTOR = "org.mockito.ArgumentCaptor";
    public static final String ORG_MOCKITO_MOCK_SETTINGS = "org.mockito.MockSettings";
    public static final String ORG_MOCKITO_ANSWER = "org.mockito.stubbing.Answer";
    public static final String ORG_MOCKITO_ANSWERS = "org.mockito.Answers";
    public static final String ORG_MOCKITO_MATCHERS = "org.mockito.Matchers";
    public static final String ORG_MOCKITO_MOCKED_STATIC = "org.mockito.MockedStatic";
    public static final String ORG_MOCKITO_VERIFICATION_VERIFICATION_MODE = "org.mockito.verification.VerificationMode";
    public static final String ORG_MOCKITO_MOCKED_STATIC_VERIFICATION = "org.mockito.MockedStatic.Verification";
    public static final String ORG_MOCKITO_MOCK_SERIALIZABLE_MODE = "org.mockito.mock.SerializableMode";

    //Plugin classes
    public static final String ORG_MOCKITO_CONFIGURATION_ANNOTATION_ENGINE = "org.mockito.configuration.AnnotationEngine";
    public static final String ORG_MOCKITO_PLUGINS_ANNOTATION_ENGINE = "org.mockito.plugins.AnnotationEngine";
    public static final String ORG_MOCKITO_PLUGINS_INSTANTIATOR_PROVIDER = "org.mockito.plugins.InstantiatorProvider";
    public static final String ORG_MOCKITO_PLUGINS_INSTANTIATOR_PROVIDER_2 = "org.mockito.plugins.InstantiatorProvider2";

    //Runners
    public static final String ORG_MOCKITO_RUNNERS_CONSOLE_SPAMMING_MOCKITO_JUNIT_RUNNER = "org.mockito.runners.ConsoleSpammingMockitoJUnitRunner";
    public static final String ORG_MOCKITO_RUNNERS_VERBOSE_MOCKITO_JUNIT_RUNNER = "org.mockito.runners.VerboseMockitoJUnitRunner";
    public static final String ORG_MOCKITO_RUNNERS_MOCKITO_JUNIT_RUNNER = "org.mockito.runners.MockitoJUnitRunner";
    public static final String ORG_MOCKITO_JUNIT_MOCKITO_JUNIT_RUNNER = "org.mockito.junit.MockitoJUnitRunner";

    //Classnames
    public static final String ARGUMENT_MATCHERS = "ArgumentMatchers";
    public static final String MATCHERS = "Matchers";

    //Matchers
    public static final String ANY = "any";
    public static final String ANY_OBJECT = "anyObject";
    public static final String ANY_VARARG = "anyVararg";
    public static final String ANY_COLLECTION_OF = "anyCollectionOf";
    public static final String ANY_ITERABLE_OF = "anyIterableOf";
    public static final String ANY_LIST_OF = "anyListOf";
    public static final String ANY_MAP_OF = "anyMapOf";
    public static final String ANY_SET_OF = "anySetOf";
    public static final String IS_NULL = "isNull";
    public static final String IS_NOT_NULL = "isNotNull";
    public static final String NOT_NULL = "notNull";

    //Method names
    public static final String WHEN = "when";
    public static final String MOCK = "mock";
    public static final String SPY = "spy";
    public static final String GIVEN = "given";
    public static final String TIMES = "times";
    public static final String AT_LEAST = "atLeast";
    public static final String AT_MOST = "atMost";
    public static final String CALLS = "calls";
    public static final String AFTER = "after";
    public static final String TIMEOUT = "timeout";
    public static final String RESET = "reset";
    public static final String IN_ORDER = "inOrder";
    public static final String VERIFY_NO_INTERACTIONS = "verifyNoInteractions";
    public static final String VERIFY_NO_MORE_INTERACTION = "verifyNoMoreInteractions";
    public static final String VERIFY_ZERO_INTERACTIONS = "verifyZeroInteractions";
    public static final String IGNORE_STUBS = "ignoreStubs";
    public static final String VERIFY = "verify";

    public static final String THEN_THROW = "thenThrow";
    public static final String DO_THROW = "doThrow";
    public static final String WILL_THROW = "willThrow";
    public static final String THEN_RETURN = "thenReturn";
    public static final String DO_RETURN = "doReturn";
    public static final String WILL_RETURN = "willReturn";

    //MockSettings
    public static final String ANSWER = "answer";
    public static final String DEFAULT_ANSWER = "defaultAnswer";
    public static final String SERIALIZABLE = "serializable";
    public static final String STUB_ONLY = "stubOnly";
    public static final String LENIENT = "lenient";
    public static final String NAME = "name";
    public static final String EXTRA_INTERFACES = "extraInterfaces";

    //Annotations
    public static final String ORG_MOCKITO_CAPTOR = "org.mockito.Captor";
    public static final String ORG_MOCKITO_MOCK = "org.mockito.Mock";
    public static final String ORG_MOCKITO_SPY = "org.mockito.Spy";
    public static final String ORG_MOCKITO_DO_NOT_MOCK = "org.mockito.DoNotMock";

    //Method calls
    public static final String ORG_MOCKITO_MOCKITO_NEVER = "org.mockito.Mockito.never";

    //Framework integration
    public static final String ORG_MOCKITO_NOT_EXTENSIBLE = "org.mockito.NotExtensible";

    private MockitoQualifiedNames() {
        //Utility class
    }
}
