//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert;

import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.profile.codeInspection.InspectionProfileManager;
import com.intellij.testFramework.InspectionsKt;

import com.picimako.mockitools.inspection.EnforceConventionInspection;
import com.picimako.mockitools.intention.MockitoolsIntentionTestBase;

/**
 * Base class for testing intentions which depend on the state of {@link EnforceConventionInspection}. 
 */
public abstract class EnforceConventionAwareIntentionTestBase extends MockitoolsIntentionTestBase {

    protected void addEnforceConventionInspection(EnforceConventionInspection.Convention convention) {
        var profile = InspectionProfileManager.getInstance(getProject()).getCurrentProfile();
        var inspection = new EnforceConventionInspection();
        inspection.conventionToEnforce = convention;
        InspectionsKt.disableAllTools(profile);
        profile.addTool(getProject(), new LocalInspectionToolWrapper(inspection), null);
        profile.enableTool(EnforceConventionInspection.SHORT_NAME, getProject());
    }

    protected void addDisabledEnforceConventionInspection() {
        var profile = InspectionProfileManager.getInstance(getProject()).getCurrentProfile();
        InspectionsKt.disableAllTools(profile);
        profile.addTool(getProject(), new LocalInspectionToolWrapper(new EnforceConventionInspection()), null);
    }
}
