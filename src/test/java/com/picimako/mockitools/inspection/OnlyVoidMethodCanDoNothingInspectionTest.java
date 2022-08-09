//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;

/**
 * Integration test for {@link OnlyVoidMethodCanDoNothingInspection}.
 */
public class OnlyVoidMethodCanDoNothingInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new OnlyVoidMethodCanDoNothingInspection();
    }

    public void testDoNothingStubbing() {
        doJavaTest();
    }
}