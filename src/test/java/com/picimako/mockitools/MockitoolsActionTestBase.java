//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import org.assertj.core.api.Assertions;

import java.util.function.Supplier;

/**
 * Base class for testing {@link AnAction} implementations.
 */
public abstract class MockitoolsActionTestBase extends MockitoolsTestBase {

    protected void checkAction(Supplier<AnAction> action, String beforeText, String afterText) {
        getFixture().configureByText("ConversionTest.java", beforeText);
        ApplicationManager.getApplication().invokeAndWait(() -> getFixture().testAction(action.get()));
        getFixture().checkResult(afterText);
    }

    protected void checkActionFlexible(Supplier<AnAction> action, String beforeText, String afterText) {
        getFixture().configureByText("ConversionTest.java", beforeText);
        ApplicationManager.getApplication().invokeAndWait(() -> getFixture().testAction(action.get()));
        Assertions.assertThat(getFixture().getEditor().getDocument().getText()).endsWith(afterText);
    }
}
