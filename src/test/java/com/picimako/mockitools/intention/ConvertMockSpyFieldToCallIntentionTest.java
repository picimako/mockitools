//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito4Latest;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertMockSpyFieldToCallIntention}.
 */
@RunsInEdt
class ConvertMockSpyFieldToCallIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockSpyFieldToCallIntention();
    }

    @Override
    protected void loadLibs() {
        loadMockito4Latest(getFixture().getProjectDisposable(), getFixture().getModule());
    }

    //Intention availability

    @Test
    void testIntentionIsNotAvailableForNonMockNonSpyField() {
        checkIntentionIsNotAvailable(
            "public class NotAvailable {\n" +
                "    Object mo<caret>ck;\n" +
                "}");
    }

    @Test
    void testIntentionIsNotAvailableWhenTheParentClassHasNoMethod() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "}");
    }

    @Test
    void testIntentionIsNotAvailableWhenBothMockAndSpyAnnotationsAreOnField() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mock;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    @Mock\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "}");
    }

    @Test
    void testIntentionIsAvailableOnMockField() {
        checkIntentionIsAvailable(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class Available {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}");
    }

    @Test
    void testIntentionIsAvailableOnSpyField() {
        checkIntentionIsAvailable(
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class Available {\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}");
    }

    //@Mock -> Mockito.mock(<type>.class)

    @Test
    void testConvertsMockFieldToMockCallFirstInCodeBlockOfManyStatements() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "        int number = 10;\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        int number = 10;\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsMockFieldToMockCallFirstInCodeBlockOfNoStatement() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsMockFieldToMockCallIntoSelectedMethod() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "\n" +
                "    public void anotherMethod() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "    }\n" +
                "\n" +
                "    public void anotherMethod() {\n" +
                "    }\n" +
                "}");
    }

    //@Spy -> Mockito.spy(<type>.class)

    @Test
    void testConvertsSpyFieldToSpyCallFirstInCodeBlockOfManyStatements() {
        checkIntentionRun(
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "        int number = 10;\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.spy(Object.class);\n" +
                "        int number = 10;\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsSpyFieldToSpyCallFirstInCodeBlockOfNoStatement() {
        checkIntentionRun(
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.spy(Object.class);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsSpyFieldToSpyCallIntoSelectedMethod() {
        checkIntentionRun(
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "\n" +
                "    public void anotherMethod() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.spy(Object.class);\n" +
                "    }\n" +
                "\n" +
                "    public void anotherMethod() {\n" +
                "    }\n" +
                "}");
    }

    //Generics: @Spy Type<type>; -> Mockito.spy(Type.class)

    @Test
    void testConvertsSpyFieldWithGenericsToSpyCall() {
        checkIntentionRun(
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    MockObject<String> mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class MockObject<T> { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        MockObject<String> mock = Mockito.spy(MockObject.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class MockObject<T> { }\n" +
                "}");
    }

    @Test
    void testConvertsMockFieldWithGenericsToMockCall() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    MockObject<String> mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class MockObject<T> { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        MockObject<String> mock = Mockito.mock(MockObject.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class MockObject<T> { }\n" +
                "}");
    }
}
