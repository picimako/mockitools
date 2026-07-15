//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.completion;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.completion.CompletionType;
import com.picimako.mockitools.MockitoolsTestBase;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for testing code completion.
 */
public abstract class MockitoolsCodeCompletionTestBase extends MockitoolsTestBase {

    /**
     * Configures a file with the provided {@code text}, test basic code completion at the marked caret position,
     * and validates if the completion contains exactly the expected {@code completionItems}.
     */
    protected void doTestCodeCompletionContains(@NotNull String text, @NotNull String... completionItems) {
        assertThat(text).withFailMessage("File text must contain <caret> to invoke code completion.").contains("<caret>");

        getFixture().configureByText("CompletionTest.java", text);
        getFixture().complete(CompletionType.BASIC);

        assertThat(getFixture().getLookupElementStrings()).containsExactlyInAnyOrder(completionItems);
    }
}
