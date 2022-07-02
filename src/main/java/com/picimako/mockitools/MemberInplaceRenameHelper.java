//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;

/**
 * Utility to perform inplace renaming via {@link MemberInplaceRenamer}.
 */
public final class MemberInplaceRenameHelper {

    /**
     * Performs an inplace rename on {@code namedElement}.
     *
     * @param namedElement the element to perform the rename on
     * @param editor       the editor in which the rename takes place
     */
    public static void rename(PsiElement namedElement, Editor editor) {
        editor.getCaretModel().moveToOffset(namedElement.getTextOffset());
        editor.getSelectionModel().selectWordAtCaret(false);
        new MemberInplaceRenamer((PsiNamedElement) namedElement, null, editor).performInplaceRename();
    }

    private MemberInplaceRenameHelper() {
        //Utility class
    }
}
