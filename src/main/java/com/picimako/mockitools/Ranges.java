//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for working with {@link com.intellij.openapi.util.TextRange}s.
 */
public final class Ranges {

    /**
     * Returns the end offset of the argument element.
     */
    public static int endOffsetOf(@NotNull PsiElement element) {
        return element.getTextRange().getEndOffset();
    }

    public static int selectionLengthIn(SelectionModel model) {
        return model.getSelectionEnd() - model.getSelectionStart();
    }

    public static CharSequence charSequenceInRange(Editor editor, int start, int end) {
        return editor.getDocument().getCharsSequence().subSequence(start, end);
    }

    private Ranges() {
        //Utility class
    }
}
