//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.inorderverify;

import com.intellij.testFramework.junit5.RunInEdt;
import com.picimako.mockitools.MockitoolsActionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertInOrderVerifyToMockedStaticVerifyAction}.
 */
@RunInEdt
class ConvertInOrderVerifyToMockedStaticVerifyActionTest extends MockitoolsActionTestBase {

    @Test
    void testConvertsInOrderVerifyToMockedStaticVerifyWithoutVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockedStaticVerifyAction(false),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import org.mockito.InOrder;
                import java.util.List;

                class ConversionTest {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder inOrder = Mockito.inOrder(List.class);
                            inOrder.ve<caret>rify(mock, List::of);
                        }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import org.mockito.InOrder;
                import java.util.List;

                class ConversionTest {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder inOrder = Mockito.inOrder(List.class);
                            mock.verify(List::of);
                        }
                    }
                }"""
        );
    }

    @Test
    void testConvertsInOrderVerifyToMockedStaticVerifyWithVerificationMode() {
        checkAction(() -> new ConvertInOrderVerifyToMockedStaticVerifyAction(false),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import org.mockito.InOrder;
                import java.util.List;

                class ConversionTest {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder inOrder = Mockito.inOrder(List.class);
                            inOrder.ve<caret>rify(mock, List::of, Mockito.times(3));
                        }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import org.mockito.InOrder;
                import java.util.List;

                class ConversionTest {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder inOrder = Mockito.inOrder(List.class);
                            mock.verify(List::of, Mockito.times(3));
                        }
                    }
                }"""
        );
    }

    @Test
    void testConvertsInOrderVerifyToMockedStaticVerifyInBulk() {
        checkAction(() -> new ConvertInOrderVerifyToMockedStaticVerifyAction(true),
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import org.mockito.InOrder;
                import java.util.List;

                class ConversionTest {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder inOrder = Mockito.inOrder(List.class);
                            <selection>inOrder.verify(mock, List::of);
                            inOrder.verify(mock, List::of, Mockito.times(3));</selection>
                        }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import org.mockito.InOrder;
                import java.util.List;

                class ConversionTest {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            InOrder inOrder = Mockito.inOrder(List.class);
                            mock.verify(List::of);
                            mock.verify(List::of, Mockito.times(3));
                        }
                    }
                }"""
        );
    }
}
