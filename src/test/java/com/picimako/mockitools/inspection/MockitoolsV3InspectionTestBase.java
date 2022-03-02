/*
 * Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.MockitoolsTestBase.MOCKITO_3_LIB;

/**
 * Base class for testing Mockito 3 specific inspections.
 */
public abstract class MockitoolsV3InspectionTestBase extends MockitoolsInspectionTestBase {
    
    @Override
    protected String[] libsToLoad() {
        return MOCKITO_3_LIB;
    }
}
