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
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock()\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
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
                "}");
    }

    //@Spy -> Mockito.spy(<type>.class) - empty config

    public void testConvertsSpyFieldToCallWithEmptyConfig() {
        checkIntentionRun(
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy()\n" +
                "    Object s<caret>py;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object spy = Mockito.spy(Object.class);\n" +
                "    }\n" +
                "}");
    }

    //@Spy -> Mockito.spy(new <type>())

    public void testConvertsSpyFieldWithInitializerToCall() {
        checkIntentionRun(
            "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Object s<caret>py = new Object();\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object spy = Mockito.spy(new Object());\n" +
                "    }\n" +
                "}");
    }

    //@Mock -> Mockito.mock(<type>.class, String)

    public void testConvertsMockFieldToCallWithNameSpecificOverride() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(name = \"some name\")\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, \"some name\");\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithEmptyNameSpecificOverride() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(name = \"\")\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
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
                "}");
    }

    //@Mock -> Mockito.mock(<type>.class, Answer)

    public void testConvertsMockFieldToCallWithAnswerSpecificOverride() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
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
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Answers.CALLS_REAL_METHODS);\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithDefaultAnswerSpecificOverride() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(answer = Answers.RETURNS_DEFAULTS)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Answers;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class);\n" +
                "    }\n" +
                "}");
    }

    //@Mock -> Mockito.mock(<type>.class, MockSettings)

    //extraInterfaces

    public void testConvertsMockFieldToCallWithEmptyExtraInterface() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(extraInterfaces = {})\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
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
                "}");
    }

    public void testConvertsMockFieldToCallWithSingleExtraInterface() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
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
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class));\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithMultipleExtraInterfaces() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "public class ConversionTest {\n" +
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
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class, Set.class));\n" +
                "    }\n" +
                "}");
    }

    //booleans

    public void testConvertsMockFieldToCallWithStubOnly() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(stubOnly = true)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().stubOnly());\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithStubOnlyFalse() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(stubOnly = false)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
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
                "}");
    }

    public void testConvertsMockFieldToCallWithEmptyName() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(stubOnly = true, name = \"\")\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().stubOnly());\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithSerializable() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(serializable = true)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().serializable());\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithLenient() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(lenient = true)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().lenient());\n" +
                "    }\n" +
                "}");
    }

    //Complex settings

    public void testConvertsMockFieldToCallWithMockSettings1() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
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
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().name(\"some name\").extraInterfaces(List.class));\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithMockSettings2() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
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
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().name(\"some name\").defaultAnswer(Answers.CALLS_REAL_METHODS).extraInterfaces(List.class));\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockFieldToCallWithMockSettings3() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
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
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().lenient().name(\"some name\").defaultAnswer(Answers.CALLS_REAL_METHODS).extraInterfaces(List.class, Set.class));\n" +
                "    }\n" +
                "}");
    }

}
