//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito4Latest;

import com.intellij.codeInsight.intention.IntentionAction;

/**
 * Functional test for {@link ConvertMockSpyFieldToCallIntention}.
 */
public class ConvertMockSpyFieldToCallWithSettingsIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockSpyFieldToCallIntention();
    }

    @Override
    protected void loadLibs() {
        loadMockito4Latest(myFixture.getProjectDisposable(), getModule());
    }

    //@Mock -> Mockito.mock(<type>.class) - empty config

    public void testConvertsMockFieldToCallWithEmptyConfig() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock()\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
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

    //@Spy -> Mockito.spy(<type>.class) - empty config

    public void testConvertsSpyFieldToCallWithEmptyConfig() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Spy()\n" +
                "    Object s<caret>py;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object spy = Mockito.spy(Object.class);\n" +
                "    }\n" +
                "}");
    }

    //@Spy -> Mockito.spy(new <type>())

    public void testConvertsSpyFieldWithInitializerToCall() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Spy\n" +
                "    Object s<caret>py = new Object();\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object spy = Mockito.spy(new Object());\n" +
                "    }\n" +
                "}");
    }

    //@Mock -> Mockito.mock(<type>.class, String)

    public void testConvertsMockFieldToCallWithNameSpecificOverride() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(name = \"some name\")\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, \"some name\");\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithEmptyNameSpecificOverride() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(name = \"\")\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
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

    //@Mock -> Mockito.mock(<type>.class, Answer)

    public void testConvertsMockFieldToCallWithAnswerSpecificOverride() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(answer = Answers.CALLS_REAL_METHODS)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Answers;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Answers.CALLS_REAL_METHODS);\n" +
                "    }\n" +
                "}");
    }

    //@Mock -> Mockito.mock(<type>.class, MockSettings)

    //extraInterfaces

    public void testConvertsMockFieldToCallWithEmptyExtraInterface() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(extraInterfaces = {})\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
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

    public void testConvertsMockFieldToCallWithSingleExtraInterface() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(extraInterfaces = List.class)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class));\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithMultipleExtraInterfaces() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(extraInterfaces = {List.class, Set.class})\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class, Set.class));\n" +
                "    }\n" +
                "}");
    }

    //booleans

    public void testConvertsMockFieldToCallWithStubOnly() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(stubOnly = true)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().stubOnly());\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithStubOnlyFalse() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(stubOnly = false)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
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

    public void testConvertsMockFieldToCallWithEmptyName() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(stubOnly = true, name = \"\")\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().stubOnly());\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithSerializable() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(serializable = true)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().serializable());\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithLenient() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(lenient = true)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().lenient());\n" +
                "    }\n" +
                "}");
    }

    //Complex settings

    public void testConvertsMockFieldToCallWithMockSettings1() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(extraInterfaces = List.class, name = \"some name\")\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().name(\"some name\").extraInterfaces(List.class));\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithMockSettings2() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(name = \"some name\", extraInterfaces = List.class, answer = Answers.CALLS_REAL_METHODS)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import org.mockito.Answers;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().name(\"some name\").defaultAnswer(Answers.CALLS_REAL_METHODS).extraInterfaces(List.class));\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithMockSettings3() {
        checkIntentionRun("ConvertFieldTest.java",
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "    @Mock(lenient = true, extraInterfaces = {List.class, Set.class}, name = \"some name\", answer = Answers.CALLS_REAL_METHODS)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "import org.mockito.Answers;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConvertFieldTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().lenient().name(\"some name\").defaultAnswer(Answers.CALLS_REAL_METHODS).extraInterfaces(List.class, Set.class));\n" +
                "    }\n" +
                "}");
    }

}
