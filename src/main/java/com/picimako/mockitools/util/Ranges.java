//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import static com.intellij.openapi.application.ReadAction.compute;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static CharSequence charSequenceInRange(Editor editor, int start, int end) {
        return editor.getDocument().getCharsSequence().subSequence(start, end);
    }

    public static boolean isWithinSelection(@Nullable PsiElement element, Editor editor) {
        return element != null && endOffsetOf(element) <= compute(() -> editor.getSelectionModel().getSelectionEnd());
    }

    private Ranges() {
        //Utility class
    }
}
