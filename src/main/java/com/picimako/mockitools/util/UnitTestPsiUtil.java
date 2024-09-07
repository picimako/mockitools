//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.siyeh.ig.psiutils.TestUtils;

/**
 * Utility for unit tests.
 */
public final class UnitTestPsiUtil {

    /**
     * Validates whether the argument file is within test sources.
     * <p>
     * In case the application is in unit test mode, it always returns true to simplify unit testing of
     * functionality relying on this method.
     * <p>
     * Otherwise, just delegates to {@link TestUtils#isInTestSourceContent(PsiElement)}.
     *
     * @param file the file to validate
     * @return true if the file is in test sources, false otherwise
     */
    public static boolean isInTestSourceContent(PsiFile file) {
        return ApplicationManager.getApplication().isUnitTestMode() || TestUtils.isInTestSourceContent(file);
    }

    private UnitTestPsiUtil() {
        //Utility class
    }
}
