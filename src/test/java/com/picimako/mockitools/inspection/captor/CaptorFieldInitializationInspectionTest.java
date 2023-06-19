//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.captor;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link CaptorFieldInitializationInspection}.
 */
class CaptorFieldInitializationInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new CaptorFieldInitializationInspection();
    }

    @Test
    void testCaptorFieldInit() {
        doJavaTest();
    }

    @Test
    void testCaptorFieldInitReplaceTest() {
        doQuickFixTest("Remove initializer");
    }
}
