//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.picimako.mockitools.completion;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.codeInsight.completion.CompletionType;
import com.picimako.mockitools.MockitoolsTestBase;

/**
 * Base class for testing code completion.
 */
public abstract class MockitoolsCodeCompletionTestBase extends MockitoolsTestBase {

    /**
     * Tests basic code completion in the provided file text at the marked caret position,
     * and validates if the completion contains exactly the expected items.
     *
     * @param text            the file text on which the code completion is invoked
     * @param completionItems the list of items that are expected to be in the displayed completion list
     */
    protected void doTestCodeCompletionContains(String text, String... completionItems) {
        assertThat(text).withFailMessage("File text must contain <caret> to invoke code completion.").contains("<caret>");

        getFixture().configureByText("CompletionTest.java", text);
        getFixture().complete(CompletionType.BASIC);

        assertThat(getFixture().getLookupElementStrings()).containsExactlyInAnyOrder(completionItems);
    }
}
