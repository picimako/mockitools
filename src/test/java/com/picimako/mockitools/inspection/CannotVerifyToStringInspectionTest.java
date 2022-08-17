//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.verification.CannotVerifyToStringInspection;

/**
 * Integration test for {@link CannotVerifyToStringInspection}.
 */
public class CannotVerifyToStringInspectionTest extends MockitoolsInspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new CannotVerifyToStringInspection();
    }

    public void testCannotVerifyToString() {
        doJavaTest();
    }
}
