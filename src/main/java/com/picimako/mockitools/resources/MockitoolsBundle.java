//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.resources;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Resource bundle for all messages in this plugin.
 */
public class MockitoolsBundle extends DynamicBundle {

    @NonNls
    private static final String MOCKITO_BUNDLE = "messages.MockitoolsBundle";
    private static final MockitoolsBundle INSTANCE = new MockitoolsBundle();

    private MockitoolsBundle() {
        super(MOCKITO_BUNDLE);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = MOCKITO_BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}
