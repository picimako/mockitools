//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

/**
 * Utility for shortening create expression logic.
 */
public final class ExpressionCreationHelper {

    /**
     * Creates an expression from the provided expression text.
     */
    public static PsiElement createExpressionFromText(String expressionText, PsiElement context, Project project) {
        return JavaCodeStyleManager.getInstance(project)
            .shortenClassReferences(JavaPsiFacade.getElementFactory(project)
                .createExpressionFromText(expressionText, context));
    }

    private ExpressionCreationHelper() {
        //Utility class
    }
}
