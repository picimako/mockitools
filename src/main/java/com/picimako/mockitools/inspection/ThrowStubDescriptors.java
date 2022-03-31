//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoQualifiedNames.DO_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.GIVEN;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDDMOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDD_MY_ONGOING_STUBBING;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_BDD_STUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_MOCKITO;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_ONGOING_STUBBING;
import static com.picimako.mockitools.MockitoQualifiedNames.ORG_MOCKITO_STUBBING_BASESTUBBER;
import static com.picimako.mockitools.MockitoQualifiedNames.THEN_THROW;
import static com.picimako.mockitools.MockitoQualifiedNames.WHEN;
import static com.picimako.mockitools.MockitoQualifiedNames.WILL_THROW;

/**
 * Provides pre-defined {@link ThrowStubDescriptor}s.
 */
public final class ThrowStubDescriptors {

    // Mockito.when().thenThrow()[.thenThrow()]
    public static final ThrowStubDescriptor WHEN_THEN_THROW =
        new ThrowStubDescriptor(WHEN, ThrowStubDescriptor.StubType.STUBBING, THEN_THROW, ORG_MOCKITO_ONGOING_STUBBING, null);
    // BDDMockito.given().willThrow()[.willThrow()]
    public static final ThrowStubDescriptor GIVEN_WILL_THROW =
        new ThrowStubDescriptor(GIVEN, ThrowStubDescriptor.StubType.STUBBING, WILL_THROW, ORG_MOCKITO_BDD_MY_ONGOING_STUBBING, null);
    // Mockito.doThrow()[.doThrow()].when()
    public static final ThrowStubDescriptor DO_THROW_WHEN =
        new ThrowStubDescriptor(WHEN, ThrowStubDescriptor.StubType.STUBBER, DO_THROW, ORG_MOCKITO_STUBBING_BASESTUBBER, ORG_MOCKITO_MOCKITO);
    // BDDMockito.willThrow()[.willThrow()].given()
    public static final ThrowStubDescriptor WILL_THROW_GIVEN =
        new ThrowStubDescriptor(GIVEN, ThrowStubDescriptor.StubType.STUBBER, WILL_THROW, ORG_MOCKITO_BDD_STUBBER, ORG_MOCKITO_BDDMOCKITO);

    private ThrowStubDescriptors() {
        //Utility class
    }
}
