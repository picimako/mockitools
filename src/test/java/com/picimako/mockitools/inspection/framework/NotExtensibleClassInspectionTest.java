//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.framework;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link NotExtensibleClassInspection}.
 */
class NotExtensibleClassInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Nullable
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/framework/inspection";
    }

    @Override
    protected InspectionProfileEntry getInspection() {
        return new NotExtensibleClassInspection();
    }

    @Test
    void testNotExtensibleValidation() {
        doJavaTest();
    }
}