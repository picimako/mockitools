//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiMethodCallExpression;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link MockitoolsPsiUtil}. Contains test cases specific to Mockito 3.x.
 */
class MockitoolsPsiUtilV3Test extends MockitoolsTestBase {

    public MockitoolsPsiUtilV3Test() {
        super(ThirdPartyLibrary.MOCKITO_V3);
    }

    @Test
    void testIsASpecificMethod() {
        getFixture().configureByText("IsMatchersTest.java", """
            import org.mockito.Matchers;
            
            public class IsMatchersTest {
                public void testMethod() {
                    Matchers.anyS<caret>tring();
                }
            }""");
        assertThat(MockitoolsPsiUtil.isMatchers(getMethodCall()))
            .describedAs("Failed during the assertion of " + "IsMatchersTest.java")
            .isTrue();
    }


    private PsiMethodCallExpression getMethodCall() {
        return (PsiMethodCallExpression) ReadAction.compute(() -> getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent());
    }
}
