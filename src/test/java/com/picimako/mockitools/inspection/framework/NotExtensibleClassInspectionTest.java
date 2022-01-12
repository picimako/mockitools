//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.framework;

import com.intellij.codeInspection.InspectionProfileEntry;

import com.picimako.mockitools.inspection.MockitoolsV3InspectionTestBase;

/**
 * Unit test for {@link NotExtensibleClassInspection}.
 */
public class NotExtensibleClassInspectionTest extends MockitoolsV3InspectionTestBase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/framework/inspection";
    }

    @Override
    protected InspectionProfileEntry getInspection() {
        return new NotExtensibleClassInspection();
    }

    public void testNotExtensibleValidation() {
        doJavaTest();
    }
}