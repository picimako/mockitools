//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Base class for testing {@link AnAction} implementations.
 */
public abstract class MockitoolsActionTestBase extends MockitoolsTestBase {

    /**
     * Configures a test file with {@code beforeText}, runs the provided {@code action} and checks
     * that the test file's content has changed to {@code afterText}.
     */
    protected void checkAction(@NotNull Supplier<AnAction> action, @NotNull String beforeText, @NotNull String afterText) {
        getFixture().configureByText("ConversionTest.java", beforeText);
        ApplicationManager.getApplication().invokeAndWait(() -> getFixture().testAction(action.get()));
        getFixture().checkResult(afterText);
    }
}
