//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link MockFieldInitializationInspection}.
 */
class MockFieldInitializationInspectionTest extends MockitoolsInspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new MockFieldInitializationInspection();
    }

    @Test
    void testMockFieldInit() {
        doJavaTest();
    }

    @Test
    void testMockFieldInitReplaceTest() {
        doQuickFixTest("Remove initializer");
    }
}
