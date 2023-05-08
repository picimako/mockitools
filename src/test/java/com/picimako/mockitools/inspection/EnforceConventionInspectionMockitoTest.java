//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link EnforceConventionInspection}.
 */
class EnforceConventionInspectionMockitoTest extends MockitoolsInspectionTestBase.MockitoV4 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new EnforceConventionInspection();
    }

    @Test
    void testEnforceMockitoMethods() {
        EnforceConventionInspection inspection = new EnforceConventionInspection();
        inspection.conventionToEnforce = EnforceConventionInspection.Convention.MOCKITO;
        
        doJavaTest(inspection);
    }

    @Test
    void testEnforceBDDMockitoMethods() {
        EnforceConventionInspection inspection = new EnforceConventionInspection();
        inspection.conventionToEnforce = EnforceConventionInspection.Convention.BDD_MOCKITO;
        
        doJavaTest(inspection);
    }
}
