//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link EnforceConventionInspection}.
 */
class EnforceConventionInspectionBDDMockitoV3Test extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        EnforceConventionInspection inspection = new EnforceConventionInspection();
        inspection.conventionToEnforce = EnforceConventionInspection.Convention.BDD_MOCKITO;
        return inspection;
    }

    @Test
    void testEnforceBDDMockitoV3Methods() {
        doJavaTest();
    }
}
