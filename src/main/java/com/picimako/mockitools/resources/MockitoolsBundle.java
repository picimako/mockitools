//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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

    private static final String MOCKITOOLS = "Mockitools: ";
    @NonNls
    private static final String MOCKITO_BUNDLE = "messages.MockitoolsBundle";
    private static final MockitoolsBundle INSTANCE = new MockitoolsBundle();

    private MockitoolsBundle() {
        super(MOCKITO_BUNDLE);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = MOCKITO_BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

    /**
     * Retrieves an inspection specific message for the provided id.
     *
     * @param id the suffix of the message key
     * @return the actual message
     */
    public static String inspection(@NonNls String id, Object @NotNull ... params) {
        return MOCKITOOLS + message("inspection." + id, params);
    }
    
    /**
     * Retrieves a quick fix specific message for the provided id.
     *
     * @param id the suffix of the message key
     * @return the actual message
     */
    public static String quickFix(@NonNls String id, Object @NotNull ... params) {
        return message("quick.fix." + id, params);
    }

    /**
     * Retrieves a quick fix family specific message for the provided id.
     *
     * @param id the suffix of the message key
     * @return the actual message
     */
    public static String quickFixFamily(@NonNls String id, Object @NotNull ... params) {
        return message("quick.fix.family." + id, params);
    }

    /**
     * Retrieves an inspection option specific message for the provided id.
     *
     * @param id the suffix of the message key
     * @return the actual message
     */
    public static String inspectionOption(@NonNls String id, Object @NotNull ... params) {
        return message("inspection.option." + id, params);
    }
}
