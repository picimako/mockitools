//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiMethodCallExpression;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Functional test for {@link MockitoolsPsiUtil}. Contains test cases specific to Mockito 3.x.
 */
class MockitoolsPsiUtilv3Test extends MockitoolsTestBase {

    public MockitoolsPsiUtilv3Test() {
        super(ThirdPartyLibrary.MOCKITO_V3);
    }

    @ParameterizedTest
    @MethodSource("specificMethodData")
    void testIsASpecificMethod(TestData data) {
        getFixture().configureByText(data.fileName, data.fileContent);
        assertThat(data.isSpecificMethod.get())
            .describedAs("Failed during the assertion of " + data.fileName)
            .isTrue();
    }

    private Stream<TestData> specificMethodData() {
        return Stream.of(
            new TestData("IsMatchersTest.java",
                """
                    import org.mockito.Matchers;

                    public class IsMatchersTest {
                        public void testMethod() {
                            Matchers.anyS<caret>tring();
                        }
                    }""", () -> MockitoolsPsiUtil.isMatchers(getMethodCall())));
    }

    private PsiMethodCallExpression getMethodCall() {
        return (PsiMethodCallExpression) ReadAction.compute(() -> getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent());
    }

    private record TestData(String fileName, String fileContent, Supplier<Boolean> isSpecificMethod) {
    }
}
