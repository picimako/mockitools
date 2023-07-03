//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertMockedStaticVerifyToInOrderVerifyAction}.
 */
@RunsInEdt
class ConvertMockedStaticVerifyToInOrderVerifyActionTest extends MockitoolsActionTestBase {

    //Caret based conversion

    @Test
    void testConvertsMockedStaticVerifyToInOrderVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(false),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            mock.veri<caret>fy(List::of);
                        }
                    }
                }""",
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder order = Mockito.inOrder(List.class);
                            order.verify(mock, List::of);
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockedStaticVerifyToInOrderVerifyWithVerificationMode() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(false),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            mock.veri<caret>fy(List::of, Mockito.times(2));
                        }
                    }
                }""",
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder order = Mockito.inOrder(List.class);
                            order.verify(mock, List::of, Mockito.times(2));
                        }
                    }
                }""");
    }

    //Selection based conversion

    @Test
    void testConvertsMockedStaticVerifyToInOrderVerifyWithoutVerificationModeInSelection() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(true),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::of, Mockito.times(2));
                            mock.verify(List::copyOf, Mockito.times(3));</selection>
                        }
                    }
                }""",
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder order = Mockito.inOrder(List.class);
                            order.verify(mock, List::of, Mockito.times(2));
                            order.verify(mock, List::copyOf, Mockito.times(3));
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockedStaticVerifyToInOrderVerifyWithVerificationModeInSelection() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(true),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::of);
                            mock.verify(List::copyOf);</selection>
                        }
                    }
                }""",
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder order = Mockito.inOrder(List.class);
                            order.verify(mock, List::of);
                            order.verify(mock, List::copyOf);
                        }
                    }
                }""");
    }

    @Test
    void testConvertsMockedStaticVerifyToInOrderVerifyWithVerificationModeInSelectionMultipleMockObjects() {
        checkAction(() -> new ConvertMockedStaticVerifyToInOrderVerifyAction(true),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;
                import java.util.Set;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mockList = Mockito.mockStatic(List.class); MockedStatic<Set> mockSet = Mockito.mockStatic(Set.class)) {
                            <selection>mockList.verify(List::of);
                            mockSet.verify(Set::of, Mockito.times(2));</selection>
                        }
                    }
                }""",
            """
                import org.mockito.InOrder;
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;
                import java.util.Set;

                class ConversionTest {
                    void testMethod() {
                        try (MockedStatic<List> mockList = Mockito.mockStatic(List.class); MockedStatic<Set> mockSet = Mockito.mockStatic(Set.class)) {
                            InOrder order = Mockito.inOrder(List.class, Set.class);
                            order.verify(mockList, List::of);
                            order.verify(mockSet, Set::of, Mockito.times(2));
                        }
                    }
                }""");
    }
}
