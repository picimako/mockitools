//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.captor;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.resources.MockitoolsBundle;

/**
 * Base class for {@code @Captor} field related quick fixes.
 */
public abstract class CaptorFieldBaseQuickFix extends InspectionGadgetsFix {

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return MockitoolsBundle.message("quick.fix.captor.field.family.name");
    }
}
