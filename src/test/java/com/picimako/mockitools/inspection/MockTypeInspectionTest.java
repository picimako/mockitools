//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link MockTypeInspection}.
 */
class MockTypeInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {
    
    @Override
    protected InspectionProfileEntry getInspection() {
        return new MockTypeInspection();
    }

    @Test
    void testNonMockableTypesTest() {
        doJavaTest();
    }

    @Test
    void testNonMockableTypesDoNotMockTest() {
        getFixture().copyFileToProject("DoNotMock.java");
        doJavaTest();
    }
}
