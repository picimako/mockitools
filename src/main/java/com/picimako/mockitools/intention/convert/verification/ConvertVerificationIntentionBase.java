//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification;

import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.picimako.mockitools.intention.convert.ConversionIntentionBase;
import com.picimako.mockitools.resources.MockitoolsBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for intention actions that convert call chains between the different verification approaches.
 *
 * @see com.picimako.mockitools.intention.convert.verification.mockitoverify.ConvertFromMockitoVerifyIntention
 * @see com.picimako.mockitools.intention.convert.verification.bddmockitothen.ConvertFromBDDMockitoThenIntention
 * @see com.picimako.mockitools.intention.convert.verification.inorderverify.ConvertFromInOrderVerifyIntention
 * @since 0.4.0
 */
public abstract class ConvertVerificationIntentionBase extends ConversionIntentionBase {

    protected ConvertVerificationIntentionBase(String sourceApproachName, int minSelectionLength) {
        super(sourceApproachName, minSelectionLength);
    }

    protected ConvertVerificationIntentionBase(String sourceApproachName) {
        super(sourceApproachName, 14);
    }

    //Intention names

    @Override
    public @IntentionName @NotNull String getText() {
        return MockitoolsBundle.message("intention.convert.verification.to");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MockitoolsBundle.message("intention.convert.verification.x.to.family", sourceApproachName);
    }
}
