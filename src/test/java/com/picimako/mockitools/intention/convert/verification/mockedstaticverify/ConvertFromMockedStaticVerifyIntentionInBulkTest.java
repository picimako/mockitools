//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertFromMockedStaticVerifyIntention}.
 */
class ConvertFromMockedStaticVerifyIntentionInBulkTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromMockedStaticVerifyIntention();
    }

    @Test
    void testNotAvailableWithSelectionShorterThanMinRequiredLength() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            mock.<selection>verify(Li</selection>st::of);
                        }    }
                }""");
    }

    @Test
    void testNotAvailableForOnlyWhitespaceSelection() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                 <selection>         </selection>  mock.verify(List::of);
                        }    }
                }""");
    }

    @Test
    void testNotAvailableOnNonMockedStaticVerifyChain() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            Object mockObject = Mockito.mock(Object.class);
                            <selection>Mockito.verify(mockObject);</selection>
                        }    }
                }""");
    }

    @Test
    void testNotAvailableOnMultipleMixedVerificationSelected() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            Object mockObject = Mockito.mock(Object.class);
                            <selection>Mockito.verify(mockObject);
                            mock.verify(List::of);</selection>
                        }    }
                }""");
    }

    @Test
    void testAvailableForMockedStaticVerifyWithoutVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::of);</selection>
                        }    }
                }""");
    }

    @Test
    void testAvailableForMockedStaticVerifyWithVerificationMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::of, Mockito.times(2));</selection>
                        }    }
                }""");
    }

    @Test
    void testAvailableOnSingleMockedStaticVerifySelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::of);</selection>
                            mock.verify(List::copyOf, Mockito.times(3));
                        }    }
                }""");
    }

    @Test
    void testAvailableOnMultipleMockedStaticVerifySelected() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.MockedStatic;
                import java.util.List;

                class NotAvailable {
                    void testMethod(){
                        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
                            <selection>mock.verify(List::of);
                            mock.verify(List::copyOf, Mockito.times(3));</selection>
                        }    }
                }""");
    }
}
