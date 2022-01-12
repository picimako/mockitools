//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;

/**
 * Unit test for {@link CaptorFieldInitializationInspection}.
 */
public class CaptorFieldInitializationInspectionTest extends MockitoolsV3InspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new CaptorFieldInitializationInspection();
    }

    public void testCaptorFieldInitTest() {
        doJavaTest();
    }

    public void testCaptorFieldInit() {
        doJavaTest();
    }

    public void testCaptorFieldInitReplaceTest() {
        doQuickFixTest("Remove initializer");
    }
}
