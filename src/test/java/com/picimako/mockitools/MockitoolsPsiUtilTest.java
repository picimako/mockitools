//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.intellij.openapi.application.ReadAction.compute;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Functional test for {@link MockitoolsPsiUtil}.
 */
class MockitoolsPsiUtilTest extends MockitoolsTestBase {

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
            new TestData("IsMockitoMockTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsMockitoMockTest {
                        public void testMethod() {
                            Object mock = Mockito.<caret>mock(Object.class);
                        }
                    }""", () -> MockitoolsPsiUtil.isMockitoMock(getMethodCall())),
            new TestData("IsMockitoSpyTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsMockitoSpyTest {
                        public void testMethod() {
                            Object mock = Mockito.<caret>spy(Object.class);
                        }
                    }""", () -> MockitoolsPsiUtil.isMockitoSpy(getMethodCall())),
            new TestData("IsAdditionalMatchersTest.java",
                """
                    import org.mockito.BDDMockito;
                    import org.mockito.AdditionalMatchers;

                    public class IsAdditionalMatchersTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            BDDMockito.given(mock.equals(AdditionalMatchers.<caret>cmpEq(new Object()))).willReturn(10);
                        }
                    }""", () -> MockitoolsPsiUtil.isAdditionalMatchers(getMethodCall())),
            new TestData("IsTimesTest.java",
                """
                    import org.mockito.Mockito;
                    import static org.mockito.Mockito.times;

                    public class IsTimesTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, <caret>times(1)).toString();
                        }
                    }""", () -> MockitoolsPsiUtil.isTimes(getMethodCall())),
            new TestData("IsCallsTest.java",
                """
                    import org.mockito.Mockito;
                    import static org.mockito.Mockito.calls;

                    public class IsCallsTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, <caret>calls(1)).toString();
                        }
                    }""", () -> MockitoolsPsiUtil.isCalls(getMethodCall())),
            new TestData("IsAfterTest.java",
                """
                    import org.mockito.Mockito;
                    import static org.mockito.Mockito.after;

                    public class IsAfterTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, <caret>after(1)).toString();
                        }
                    }""", () -> MockitoolsPsiUtil.isAfter(getMethodCall())),
            new TestData("IsTimeoutTest.java",
                """
                    import org.mockito.Mockito;
                    import static org.mockito.Mockito.timeout;

                    public class IsTimeoutTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, <caret>timeout(1)).toString();
                        }
                    }""", () -> MockitoolsPsiUtil.isTimeout(getMethodCall())),
            new TestData("IsExtraInterfacesTest.java",
                """
                    import org.mockito.Mockito;
                    import java.util.List;

                    public class IsExtraInterfacesTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class, Mockito.withSettings().<caret>extraInterfaces(List.class));
                        }
                    }""", () -> MockitoolsPsiUtil.isExtraInterfaces(getMethodCall())),
            new TestData("IsResetTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsResetTest {
                        public void testMethod() {
                            Mockito.re<caret>set(Mockito.mock(Object.class));
                        }
                    }""", () -> MockitoolsPsiUtil.isReset(getMethodCall())),
            new TestData("IsIgnoreStubsTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsIgnoreStubsTest {
                        public void testMethod() {
                            Mockito.ignore<caret>Stubs(Mockito.mock(Object.class));
                        }
                    }""", () -> MockitoolsPsiUtil.isIgnoreStubs(getMethodCall())));
    }

    @ParameterizedTest
    @MethodSource("notASpecifiedMethodData")
    void testIsNotASpecificMethod(TestData data) {
        getFixture().configureByText(data.fileName, data.fileContent);
        assertThat(data.isSpecificMethod.get())
            .describedAs("Failed during the assertion of " + data.fileName)
            .isFalse();
    }

    private Stream<TestData> notASpecifiedMethodData() {
        String fileContent = """
            import org.mockito.Mockito;

            public class %s {
                public void testMethod() {
                    String toString = new Object().<caret>toString();
                }
            }""";
        return Stream.of(
            new TestData("IsNotMockitoMockTest.java", String.format(fileContent, "IsNotMockitoMockTest"), () -> MockitoolsPsiUtil.isMockitoMock(getMethodCall())),
            new TestData("IsNotMockitoSpyTest.java", String.format(fileContent, "IsNotMockitoSpyTest"), () -> MockitoolsPsiUtil.isMockitoSpy(getMethodCall())),
            new TestData("IsNotAdditionalMatchersTest.java", String.format(fileContent, "IsNotAdditionalMatchersTest"), () -> MockitoolsPsiUtil.isAdditionalMatchers(getMethodCall())),
            new TestData("isNotTimesTest.java", String.format(fileContent, "isNotTimesTest"), () -> MockitoolsPsiUtil.isTimes(getMethodCall())),
            new TestData("isNotCallsTest.java", String.format(fileContent, "isNotCallsTest"), () -> MockitoolsPsiUtil.isCalls(getMethodCall())),
            new TestData("isNotAfterTest.java", String.format(fileContent, "isNotAfterTest"), () -> MockitoolsPsiUtil.isAfter(getMethodCall())),
            new TestData("isNotTimeoutTest.java", String.format(fileContent, "isNotTimeoutTest"), () -> MockitoolsPsiUtil.isTimeout(getMethodCall())),
            new TestData("isNotExtraInterfacesTest.java", String.format(fileContent, "isNotExtraInterfacesTest"), () -> MockitoolsPsiUtil.isExtraInterfaces(getMethodCall())),
            new TestData("isNotResetTest.java", String.format(fileContent, "isNotResetTest"), () -> MockitoolsPsiUtil.isReset(getMethodCall())),
            new TestData("isNotIgnoreStunsTest.java", String.format(fileContent, "isNotIgnoreStubsTest"), () -> MockitoolsPsiUtil.isIgnoreStubs(getMethodCall()))
        );
    }

    @ParameterizedTest
    @MethodSource("isCalledTheSameButNotTheSpecificMethodData")
    void testIsCalledTheSameButNotTheSpecificMethod(TestData data) {
        getFixture().configureByText(data.fileName, data.fileContent);
        assertThat(data.isSpecificMethod.get())
            .describedAs("Failed during the assertion of " + data.fileName)
            .isFalse();
    }

    private Stream<TestData> isCalledTheSameButNotTheSpecificMethodData() {
        return Stream.of(
            new TestData("IsMockNotMockitoMockTest.java",
                """

                    public class IsMockNotMockitoMockTest {
                        public void testMethod() {
                            Mock.<caret>mock(Object.class);
                        }

                        private static final class Mock {
                            public static Mock mock(Object object) {
                                return new Mock();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isMockitoMock(getMethodCall())),
            new TestData("IsSpyNotMockitoSpyTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsSpyNotMockitoSpyTest {
                        public void testMethod() {
                            Spy.<caret>spy(Object.class);
                        }

                        private static final class Spy {
                            public static Spy spy(Object object) {
                                return new Spy();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isMockitoSpy(getMethodCall())),
            new TestData("IsTimesNotMockitoTimesTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsTimesNotMockitoTimesTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, Times.<caret>times(1)).toString();
                        }

                        private static final class Times {
                            public static Times times(Object object) {
                                return new Times();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isTimes(getMethodCall())),
            new TestData("IsCallsNotMockitoCallsTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsCallsNotMockitoCallsTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, Times.<caret>calls(1)).toString();
                        }

                        private static final class Calls {
                            public static Calls calls(Object object) {
                                return new Calls();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isCalls(getMethodCall())),
            new TestData("IsAfterNotMockitoAfterTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsAfterNotMockitoAfterTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, Times.<caret>after(1)).toString();
                        }

                        private static final class After {
                            public static After after(Object object) {
                                return new After();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isAfter(getMethodCall())),
            new TestData("IsTimeoutNotMockitoTimeoutTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsTimeoutNotMockitoTimeoutTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, Times.<caret>timeout(1)).toString();
                        }

                        private static final class Timeout {
                            public static Timeout timeout(Object object) {
                                return new Timeout();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isTimeout(getMethodCall())),
            new TestData("IsExtraInterfacesNotMockitoSettingsExtraInterfacesTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsExtraInterfacesNotMockitoSettingsExtraInterfacesTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Mockito.verify(mock, ExtraInterfaces.<caret>extraInterfaces(1)).toString();
                        }

                        private static final class ExtraInterfaces {
                            public static ExtraInterfaces extraInterfaces(Object object) {
                                return new ExtraInterfaces();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isExtraInterfaces(getMethodCall())),
            new TestData("IsResetNotMockitoResetTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsResetNotMockitoResetTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            Reset.re<caret>set(mock);
                        }

                        private static final class Reset {
                            public static Reset reset(Object object) {
                                return new Reset();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isReset(getMethodCall())),
            new TestData("IsIgnoreStubsNotMockitoIgnoreStubsTest.java",
                """
                    import org.mockito.Mockito;

                    public class IsIgnoreStubsNotMockitoIgnoreStubsTest {
                        public void testMethod() {
                            Object mock = Mockito.mock(Object.class);
                            IgnoreStubs.ignore<caret>Stubs(mock);
                        }

                        private static final class IgnoreStubs {
                            public static IgnoreStubs ignoreStubs(Object object) {
                                return new IgnoreStubs();
                            }
                        }
                    }""", () -> MockitoolsPsiUtil.isIgnoreStubs(getMethodCall()))
        );
    }

    //isOfTypeArgumentCaptor

    @Test
    void testIsArgumentCaptor() {
        getFixture().configureByText("IsArgumentCaptorTest.java",
            """
                import org.mockito.ArgumentCaptor;
                import org.mockito.Captor;

                public class IsArgumentCaptorTest {
                    @Captor
                    public ArgumentCaptor<String> <caret>captor;
                }
                """);

        assertThat(MockitoolsPsiUtil.isOfTypeArgumentCaptor(getField())).isTrue();
    }

    @Test
    void testIsNotArgumentCaptor() {
        getFixture().configureByText("IsNotArgumentCaptorTest.java",
            """
                import org.mockito.ArgumentCaptor;
                import org.mockito.Captor;

                public class IsNotArgumentCaptorTest {
                    @Captor
                    public String <caret>captor;
                }
                """);

        assertThat(MockitoolsPsiUtil.isOfTypeArgumentCaptor(getField())).isFalse();
    }

    //isMatchers

    @Test
    void testIsNotMatchers() {
        getFixture().configureByText("isNotMatchersTest.java",
            """
                import org.mockito.ArgumentMatchers;

                public class isNotMatchersTest {
                    public void testMethod() {
                        ArgumentMatchers.anyS<caret>tring();
                    }
                }""");

        assertThat(MockitoolsPsiUtil.isMatchers(getMethodCall())).isFalse();
    }

    private PsiMethodCallExpression getMethodCall() {
        return (PsiMethodCallExpression) compute(() -> getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent());
    }

    private PsiField getField() {
        return (PsiField) compute(() -> getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent());
    }

    private record TestData(String fileName, String fileContent, Supplier<Boolean> isSpecificMethod) {
    }
}
