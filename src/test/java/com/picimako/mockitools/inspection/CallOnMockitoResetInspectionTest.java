//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link CallOnMockitoResetInspection}
 */
class CallOnMockitoResetInspectionTest extends MockitoolsInspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new CallOnMockitoResetInspection();
    }

    @Test
    void testCodeSmellsTest() {
        doJavaTest();
    }
}
