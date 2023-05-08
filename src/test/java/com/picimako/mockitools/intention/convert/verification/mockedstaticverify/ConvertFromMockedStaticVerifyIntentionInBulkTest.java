//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.convert.verification.mockedstaticverify;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import com.picimako.mockitools.intention.convert.EnforceConventionAwareIntentionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ConvertFromMockedStaticVerifyIntention}.
 */
@RunsInEdt
class ConvertFromMockedStaticVerifyIntentionInBulkTest extends EnforceConventionAwareIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertFromMockedStaticVerifyIntention();
    }

    @Test
    void testNotAvailableWithSelectionShorterThanMinRequiredLength() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            mock.<selection>verify(Li</selection>st::of);\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForOnlyWhitespaceSelection() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                " <selection>         </selection>  mock.verify(List::of);\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableOnNonMockedStaticVerifyChain() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            Object mockObject = Mockito.mock(Object.class);\n" +
                "            <selection>Mockito.verify(mockObject);</selection>\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableOnMultipleMixedVerificationSelected() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            Object mockObject = Mockito.mock(Object.class);\n" +
                "            <selection>Mockito.verify(mockObject);\n" +
                "            mock.verify(List::of);</selection>\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableForMockedStaticVerifyWithoutVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::of);</selection>\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableForMockedStaticVerifyWithVerificationMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::of, Mockito.times(2));</selection>\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnSingleMockedStaticVerifySelected() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::of);</selection>\n" +
                "            mock.verify(List::copyOf, Mockito.times(3));\n" +
                "        }" +
                "    }\n" +
                "}");
    }

    @Test
    void testAvailableOnMultipleMockedStaticVerifySelected() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.MockedStatic;\n" +
                "import java.util.List;\n" +
                "\n" +
                "class NotAvailable {\n" +
                "    void testMethod(){\n" +
                "        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {\n" +
                "            <selection>mock.verify(List::of);\n" +
                "            mock.verify(List::copyOf, Mockito.times(3));</selection>\n" +
                "        }" +
                "    }\n" +
                "}");
    }
}
