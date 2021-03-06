//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito4Latest;

import com.intellij.codeInsight.intention.IntentionAction;

/**
 * Functional test for {@link ConvertMockCallToFieldIntention}.
 */
public class ConvertMockCallToFieldIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockCallToFieldIntention();
    }

    @Override
    protected void loadLibs() {
        loadMockito4Latest(myFixture.getProjectDisposable(), getModule());
    }

    //Availability

    public void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    public void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            "public class NotAvaila<caret>ble { }");
    }

    public void testNotAvailableForNotMockMethod() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForNotClassObjectAccessExpression() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(new Object());\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForNotMockableType() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(new String());\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForNonMockitoWithSettings() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        var mockSettings = Mockito.withSettings();\n" +
                "        Mockito.mo<caret>ck(Object.class, mockSettings);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForNotSupportedMockSettings() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().spiedInstance(new Object()));\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForNotSupportedMockSettingsSerializableWithMode() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.mock.SerializableMode;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable(SerializableMode.ACROSS_CLASSLOADERS));\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoMock() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class Available {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoMockWithName() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class Available {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class, \"some name\");\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoMockWithAnswer() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class Available {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class, Answers.CALLS_REAL_METHODS);\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForMockitoMockWithSettings() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class Available {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().extraInterfaces(List.class));\n" +
                "    }\n" +
                "}");
    }

    public void testAvailableForNotSupportedMockSettingsSerializableWithoutMode() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class Available {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable());\n" +
                "    }\n" +
                "}");
    }

    //Conversions

    public void testConvertsMockitoMock() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var mock = Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithName() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, \"some name\"));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(name = \"some name\")\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithNameFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var mock = Mockito.mo<caret>ck(Object.class, \"some name\");\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(name = \"some name\")\n" +
                "    Object mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithDefaultAnswer() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Answers.RETURNS_DEFAULTS));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithDefaultAnswerFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var mock = Mockito.mo<caret>ck(Object.class, Answers.RETURNS_DEFAULTS);\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithNonDefaultAnswer() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Answers.RETURNS_SMART_NULLS));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(answer = Answers.RETURNS_SMART_NULLS)\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithNonDefaultAnswerFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var mock = Mockito.mo<caret>ck(Object.class, Answers.RETURNS_SMART_NULLS);\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(answer = Answers.RETURNS_SMART_NULLS)\n" +
                "    Object mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithBooleanSettings() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable().stubOnly()));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(lenient = true, serializable = true, stubOnly = true)\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithBooleanSettingsFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var mock = Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable().stubOnly());\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(lenient = true, serializable = true, stubOnly = true)\n" +
                "    Object mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsName() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().name(\"some name\")));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(name = \"some name\")\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsNameFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var mock = Mockito.mo<caret>ck(Object.class, Mockito.withSettings().name(\"some name\"));\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(name = \"some name\")\n" +
                "    Object mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsStrictness() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.quality.Strictness;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().strictness(Strictness.WARN)));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.quality.Strictness;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(strictness = Mock.Strictness.WARN)\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsDefaultAnswer() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_DEFAULTS)));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsDefaultAnswerFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var mock = Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_DEFAULTS));\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Object mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsNonDefaultAnswer() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS)));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(answer = Answers.RETURNS_SMART_NULLS)\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsNonDefaultAnswerFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS)));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import org.mockito.Answers;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(answer = Answers.RETURNS_SMART_NULLS)\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsSingleExtraInterface() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().extraInterfaces(List.class)));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(extraInterfaces = List.class)\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }

    public void testConvertsMockitoMockWithSettingsMultipleExtraInterfaces() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().extraInterfaces(List.class, Set.class)));\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "import java.util.Set;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock(extraInterfaces = {List.class, Set.class})\n" +
                "    Object object;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(object);\n" +
                "    }\n" +
                "    public void aMethod(Object object) { }\n" +
                "}");
    }
    
    //Generics

    public void testConvertsMockitoMockWithGenericsFromVariable() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable<String> mock = Mockito.mo<caret>ck(Mockable.class);\n" +
                "    }\n" +
                "    public static final class Mockable<T> { }\n" +
                "}",
            "import org.mockito.Mock;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Mock\n" +
                "    Mockable<String> mock;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "    public static final class Mockable<T> { }\n" +
                "}");
    }
}
