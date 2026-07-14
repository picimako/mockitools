//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * An extension to {@link com.intellij.util.containers.ContainerUtil}.
 */
public final class MockitoolsContainerUtil {

    /**
     * Returns if the {@code iterable} doesn't contain any item matching the {@code predicate}.
     */
    public static <T> boolean none(@NotNull Iterable<? extends T> iterable, @NotNull Predicate<? super T> predicate) {
        for (T t : iterable) {
            if (predicate.test(t)) return false;
        }

        return true;
    }

    /**
     * Returns if the {@code array} doesn't contain any item matching the {@code predicate}.
     */
    public static <T> boolean none(T @NotNull [] array, @NotNull Predicate<? super T> predicate) {
        for (T t : array) {
            if (predicate.test(t)) return false;
        }

        return true;
    }

    private MockitoolsContainerUtil() {
        //Utility class
    }
}
