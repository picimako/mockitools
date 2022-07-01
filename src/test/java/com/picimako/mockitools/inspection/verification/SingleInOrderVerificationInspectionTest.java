//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;

/**
 * Integration test for {@link SingleInOrderVerificationInspection};
 */
public class SingleInOrderVerificationInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new SingleInOrderVerificationInspection();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection";
    }

    public void testSingleInOrderVerification() {
        doJavaTest();
    }
}
