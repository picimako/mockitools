//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

/**
 * Unit test for {@link UnitTestPsiUtil}.
 */
public class UnitTestPsiUtilTest extends LightJavaCodeInsightFixtureTestCase {

    //isUnitTest
    
    public void testIsUnitTest() {
        PsiFile psiFile = myFixture.configureByText("IsUnitTest.java", "");

        assertThat(UnitTestPsiUtil.isUnitTest(psiFile)).isTrue();
    }

    public void testIsNotUnitTest() {
        PsiFile psiFile = myFixture.configureByText("IsUnitTestNot.java", "");

        assertThat(UnitTestPsiUtil.isUnitTest(psiFile)).isFalse();
    }
}
