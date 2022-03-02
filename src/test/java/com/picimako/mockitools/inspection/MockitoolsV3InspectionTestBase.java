/*
 * Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito3;

/**
 * Base class for testing Mockito 3 specific inspections.
 */
public abstract class MockitoolsV3InspectionTestBase extends MockitoolsInspectionTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadMockito3(myFixture.getProjectDisposable(), getModule());
        loadLibs();
    }
}
