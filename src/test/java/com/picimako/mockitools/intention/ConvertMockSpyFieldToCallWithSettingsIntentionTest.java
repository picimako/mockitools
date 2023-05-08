//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito4Latest;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.RunsInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertMockSpyFieldToCallIntention}.
 */
@RunsInEdt
class ConvertMockSpyFieldToCallWithSettingsIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockSpyFieldToCallIntention();
    }

    @Override
    protected void loadLibs() {
        loadMockito4Latest(getFixture().getProjectDisposable(), getFixture().getModule());
    }

    //@Mock -> Mockito.mock(<type>.class) - empty config

    @Test
    void testConvertsMockFieldToCallWithEmptyConfig() {
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

    @Test
    void testConvertsSpyFieldToCallWithEmptyConfig() {
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

    @Test
    void testConvertsSpyFieldWithInitializerToCall() {
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

    @Test
    void testConvertsMockFieldToCallWithNameSpecificOverride() {
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

    @Test
    void testConvertsMockFieldToCallWithEmptyNameSpecificOverride() {
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

    @Test
    void testConvertsMockFieldToCallWithAnswerSpecificOverride() {
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

    @Test
    void testConvertsMockFieldToCallWithDefaultAnswerSpecificOverride() {
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

    //TODO: @Mock -> Mockito.mock(<type>.class, MockSettings)

    //extraInterfaces

    @Test
    void testConvertsMockFieldToCallWithEmptyExtraInterface() {
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

    @Test
    void testConvertsMockFieldToCallWithSingleExtraInterface() {
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

    @Test
    void testConvertsMockFieldToCallWithMultipleExtraInterfaces() {
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

    @Test
    void testConvertsMockFieldToCallWithStubOnly() {
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

    @Test
    void testConvertsMockFieldToCallWithStubOnlyFalse() {
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

    @Test
    void testConvertsMockFieldToCallWithEmptyName() {
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

    @Test
    void testConvertsMockFieldToCallWithSerializable() {
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

    @Test
    void testConvertsMockFieldToCallWithLenient() {
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

    @Test
    void testConvertsMockFieldToCallWithMockSettings1() {
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

    @Test
    void testConvertsMockFieldToCallWithMockSettings2() {
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

    @Test
    void testConvertsMockFieldToCallWithMockSettings3() {
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

    @Test
    void testConvertsMockFieldToCallWithMockSettingsStrictnessDefault() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(strictness = Mock.Strictness.TEST_LEVEL_DEFAULT)\n" +
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
                "        Object mock = Mockito.mock(Object.class);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsMockFieldToCallWithMockSettingsStrictness() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(strictness = Mock.Strictness.WARN)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.quality.Strictness;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().strictness(Strictness.WARN));\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsMockFieldToCallWithMockSettingsMockMaker() {
        checkIntentionRun(
            "import org.mockito.Mock;\n" +
                "import org.mockito.MockMakers;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(mockMaker = MockMakers.INLINE)\n" +
                "    Object mo<caret>ck;\n" +
                "\n" +
                "    public void method() {\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.MockMakers;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "\n" +
                "    public void method() {\n" +
                "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().mockMaker(MockMakers.INLINE));\n" +
                "    }\n" +
                "}");
    }
}
