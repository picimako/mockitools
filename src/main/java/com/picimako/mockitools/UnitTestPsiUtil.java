//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.siyeh.ig.psiutils.TestUtils;

/**
 * Utility for unit tests.
 */
public final class UnitTestPsiUtil {

    private static final String UNIT_TEST_FILE_IDENTIFIER = "Test.java";

    /**
     * Validates whether the argument file is a unit test.
     * <p>
     * This is based on the rule that unit test has to end with the word {@code Test}.
     *
     * @param file the file to validate
     * @return true if the file is a unit test, false otherwise
     */
    public static boolean isUnitTest(PsiFile file) {
        return file.getName().endsWith(UNIT_TEST_FILE_IDENTIFIER);
    }

    /**
     * Validates whether the argument file is within test sources.
     * <p>
     * In case the application is in unit test mode, it always returns true to simplify unit testing of
     * functionality relying on this method.
     * <p>
     * Otherwise just delegates to {@link TestUtils#isInTestSourceContent(PsiElement)}.
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
