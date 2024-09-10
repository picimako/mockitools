//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.tree.IElementType;

/**
 * Utility for token types.
 */
public final class TokenTypes {

    /**
     * Returns if the element is of the provided type.
     */
    public static boolean isTokenType(PsiElement element, IElementType type) {
        return element instanceof PsiJavaToken && element.getNode().getElementType() == type;
    }

    private TokenTypes() {
        //Utility class
    }
}
