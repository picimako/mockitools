//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.framework;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import com.picimako.mockitools.inspection.framework.NotExtensibleClassInspection;

/**
 * Unit test for {@link NotExtensibleClassInspection}.
 */
public class NotExtensibleClassInspectionTest extends MockitoolsInspectionTestBase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/framework/inspection";
    }

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return JAVA_15; //to be able to test Java records
    }

    @Override
    protected InspectionProfileEntry getInspection() {
        return new NotExtensibleClassInspection();
    }

    public void testNotExtensibleValidation() {
        doJavaTest();
    }
}
