//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;

/**
 * Functional test for {@link MockTypeInspection}.
 */
public class MockTypeInspectionTest extends MockitoolsInspectionTestBase.MockitoV4 {
    
    @Override
    protected InspectionProfileEntry getInspection() {
        return new MockTypeInspection();
    }

    public void testNonMockableTypesTest() {
        doJavaTest();
    }
    
    public void testNonMockableTypesDoNotMockTest() {
        myFixture.copyFileToProject("DoNotMock.java");
        doJavaTest();
    }
}
