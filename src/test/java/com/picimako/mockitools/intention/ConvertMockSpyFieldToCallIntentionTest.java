//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito4Latest;

import com.intellij.codeInsight.intention.IntentionAction;

/**
 * Functional test for {@link ConvertMockSpyFieldToCallIntention}.
 */
public class ConvertMockSpyFieldToCallIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockSpyFieldToCallIntention();
    }

    @Override
    protected void loadLibs() {
        loadMockito4Latest(myFixture.getProjectDisposable(), getModule());
    }

    //Intention availability

    public void testIntentionIsNotAvailableForNonMockNonSpyField() {
        checkIntentionIsNotAvailable("ConvertFieldTest.java",
            "public class ConvertFieldTest {\n" +
                "    Object mo<caret>ck;\n" +
                "}");
    }

    public void testIntentionIsNotAvailableWhenTheParentClassHasNoMethod() {
        checkIntentionIsNotAvailable("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "}");
    }

    public void testIntentionIsNotAvailableWhenBothMockAndSpyAnnotationsAreOnField() {
        checkIntentionIsNotAvailable("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "}");
    }

    public void testIntentionIsAvailableOnMockField() {
        checkIntentionIsAvailable("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}");
    }

    public void testIntentionIsAvailableOnSpyField() {
        checkIntentionIsAvailable("ConvertFieldTest.java",
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}");
    }

    //@Mock -> Mockito.mock(<type>.class)

    public void testConvertsMockFieldToMockCallFirstInCodeBlockOfManyStatements() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
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
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "        int number = 10;\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToMockCallFirstInCodeBlockOfNoStatement() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToMockCallIntoSelectedMethod() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldtest {\n" +
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
                "public class ConvertFieldtest {\n" +
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

    public void testConvertsSpyFieldToSpyCallFirstInCodeBlockOfManyStatements() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
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
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.spy(Object.class);\n" +
                "        int number = 10;\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsSpyFieldToSpyCallFirstInCodeBlockOfNoStatement() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Spy\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {}\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.spy(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsSpyFieldToSpyCallIntoSelectedMethod() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldtest {\n" +
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
                "public class ConvertFieldtest {\n" +
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

    public void testConvertsSpyFieldWithGenericsToSpyCall() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
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
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        MockObject<String> mock = Mockito.spy(MockObject.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class MockObject<T> { }\n" +
                "}");
    }

    public void testConvertsMockFieldWithGenericsToMockCall() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
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
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        MockObject<String> mock = Mockito.mock(MockObject.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class MockObject<T> { }\n" +
                "}");
    }
}
