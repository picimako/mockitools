//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.profile.codeInspection.InspectionProfileManager;
import com.intellij.psi.PsiElement;
import com.picimako.mockitools.inspection.stubbing.EnforceConventionInspection;

/**
 * Helpers for checking which {@link Convention} is enforced.
 */
public final class EnforceConventionUtil {

    /**
     * @see EnforceConventionInspection#SHORT_NAME
     */
    public static final String ENFORCE_CONVENTION_INSPECTION_SHORT_NAME = "EnforceConvention";

    /**
     * Returns whether {@link EnforceConventionInspection} is enabled in the inspection profile currently active in the current project,
     * and {@link Convention#MOCKITO} is being enforced.
     *
     * @param methodCall the method call the intention availability is being checked
     */
    public static boolean isMockitoEnforced(PsiElement methodCall) {
        return isEnforced(methodCall, Convention.MOCKITO);
    }

    /**
     * Returns whether {@link EnforceConventionInspection} is enabled in the inspection profile currently active in the current project,
     * and {@link Convention#BDD_MOCKITO} is being enforced.
     *
     * @param methodCall the method call the intention availability is being checked
     */
    public static boolean isBDDMockitoEnforced(PsiElement methodCall) {
        return isEnforced(methodCall, Convention.BDD_MOCKITO);
    }

    private static boolean isEnforced(PsiElement methodCall, Convention convention) {
        var profile = InspectionProfileManager.getInstance(methodCall.getProject()).getCurrentProfile();
        if (profile.isToolEnabled(HighlightDisplayKey.find(ENFORCE_CONVENTION_INSPECTION_SHORT_NAME))) {
            var enforceConvention = (EnforceConventionInspection) profile.getUnwrappedTool(ENFORCE_CONVENTION_INSPECTION_SHORT_NAME, methodCall);
            return enforceConvention != null && enforceConvention.conventionToEnforce == convention;
        }
        return false;
    }

    private EnforceConventionUtil() {
        //Utility class
    }
}
