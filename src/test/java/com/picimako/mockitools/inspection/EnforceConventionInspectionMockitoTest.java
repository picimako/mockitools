//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;

/**
 * Functional test for {@link EnforceConventionInspection}.
 */
public class EnforceConventionInspectionMockitoTest extends MockitoolsV4InspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new EnforceConventionInspection();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection";
    }

    public void testEnforceMockitoMethods() {
        EnforceConventionInspection inspection = new EnforceConventionInspection();
        inspection.conventionToEnforce = EnforceConventionInspection.Convention.MOCKITO;
        
        doJavaTest(inspection);
    }

    public void testEnforceBDDMockitoMethods() {
        EnforceConventionInspection inspection = new EnforceConventionInspection();
        inspection.conventionToEnforce = EnforceConventionInspection.Convention.BDD_MOCKITO;
        
        doJavaTest(inspection);
    }
}
