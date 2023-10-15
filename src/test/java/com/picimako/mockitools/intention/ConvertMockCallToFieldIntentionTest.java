//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.junit5.RunInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertMockCallToFieldIntention}.
 */
@RunInEdt
class ConvertMockCallToFieldIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockCallToFieldIntention();
    }

    //Availability

    @Test
    void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    @Test
    void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            "public class NotAvaila<caret>ble { }");
    }

    @Test
    void testNotAvailableForNotMockMethod() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.sp<caret>y(Object.class);
                    }
                }""");
    }

    @Test
    void testNotAvailableForNotClassObjectAccessExpression() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.sp<caret>y(new Object());
                    }
                }""");
    }

    @Test
    void testNotAvailableForNotMockableType() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.sp<caret>y(new String());
                    }
                }""");
    }

    @Test
    void testNotAvailableForNonMockitoWithSettings() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        var mockSettings = Mockito.withSettings();
                        Mockito.mo<caret>ck(Object.class, mockSettings);
                    }
                }""");
    }

    @Test
    void testNotAvailableForNotSupportedMockSettings() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().spiedInstance(new Object()));
                    }
                }""");
    }

    @Test
    void testNotAvailableForNotSupportedMockSettingsSerializableWithMode() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.mock.SerializableMode;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable(SerializableMode.ACROSS_CLASSLOADERS));
                    }
                }""");
    }

    @Test
    void testAvailableForMockitoMock() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                public class Available {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class);
                    }
                }""");
    }

    @Test
    void testAvailableForMockitoMockWithName() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                public class Available {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class, "some name");
                    }
                }""");
    }

    @Test
    void testAvailableForMockitoMockWithAnswer() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class Available {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class, Answers.CALLS_REAL_METHODS);
                    }
                }""");
    }

    @Test
    void testAvailableForMockitoMockWithSettings() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;
                import java.util.List;

                public class Available {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().extraInterfaces(List.class));
                    }
                }""");
    }

    @Test
    void testAvailableForNotSupportedMockSettingsSerializableWithoutMode() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                public class Available {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable());
                    }
                }""");
    }

    @Test
    void testAvailableForWithoutAnnotations() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                public class Available {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class, Mockito.withSettings().withoutAnnotations());
                    }
                }""");
    }

    //Conversions

    @Test
    void testConvertsMockitoMock() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        var mock = Mockito.mo<caret>ck(Object.class);
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock
                    Object mock;

                    public void testMethod() {
                    }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithName() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, "some name"));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock(name = "some name")
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithNameFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        var mock = Mockito.mo<caret>ck(Object.class, "some name");
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock(name = "some name")
                    Object mock;

                    public void testMethod() {
                    }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithDefaultAnswer() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Answers.RETURNS_DEFAULTS));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithDefaultAnswerFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        var mock = Mockito.mo<caret>ck(Object.class, Answers.RETURNS_DEFAULTS);
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock
                    Object mock;

                    public void testMethod() {
                    }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithNonDefaultAnswer() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Answers.RETURNS_SMART_NULLS));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(answer = Answers.RETURNS_SMART_NULLS)
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithNonDefaultAnswerFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        var mock = Mockito.mo<caret>ck(Object.class, Answers.RETURNS_SMART_NULLS);
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(answer = Answers.RETURNS_SMART_NULLS)
                    Object mock;

                    public void testMethod() {
                    }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithBooleanSettings() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable().stubOnly().withoutAnnotations()));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock(lenient = true, serializable = true, stubOnly = true, withoutAnnotations = true)
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithBooleanSettingsFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        var mock = Mockito.mo<caret>ck(Object.class, Mockito.withSettings().lenient().serializable().stubOnly().withoutAnnotations());
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock(lenient = true, serializable = true, stubOnly = true, withoutAnnotations = true)
                    Object mock;

                    public void testMethod() {
                    }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsName() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().name("some name")));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock(name = "some name")
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsNameFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        var mock = Mockito.mo<caret>ck(Object.class, Mockito.withSettings().name("some name"));
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock(name = "some name")
                    Object mock;

                    public void testMethod() {
                    }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsStrictness() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.quality.Strictness;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().strictness(Strictness.WARN)));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.quality.Strictness;

                public class ConversionTest {
                    @Mock(strictness = Mock.Strictness.WARN)
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsMockMaker() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().mockMaker("mock-maker-inline")));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock(mockMaker = "mock-maker-inline")
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsDefaultAnswer() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_DEFAULTS)));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsDefaultAnswerFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        var mock = Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_DEFAULTS));
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock
                    Object mock;

                    public void testMethod() {
                    }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsNonDefaultAnswer() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS)));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(answer = Answers.RETURNS_SMART_NULLS)
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsNonDefaultAnswerFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS)));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(answer = Answers.RETURNS_SMART_NULLS)
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsSingleExtraInterface() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import java.util.List;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().extraInterfaces(List.class)));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import java.util.List;

                public class ConversionTest {
                    @Mock(extraInterfaces = List.class)
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }

    @Test
    void testConvertsMockitoMockWithSettingsMultipleExtraInterfaces() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;
                import java.util.List;
                import java.util.Set;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.mo<caret>ck(Object.class, Mockito.withSettings().extraInterfaces(List.class, Set.class)));
                    }
                    public void aMethod(Object object) { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import java.util.List;
                import java.util.Set;

                public class ConversionTest {
                    @Mock(extraInterfaces = {List.class, Set.class})
                    Object object;

                    public void testMethod() {
                        aMethod(object);
                    }
                    public void aMethod(Object object) { }
                }""");
    }
    
    //Generics

    @Test
    void testConvertsMockitoMockWithGenericsFromVariable() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable<String> mock = Mockito.mo<caret>ck(Mockable.class);
                    }
                    public static final class Mockable<T> { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {
                    @Mock
                    Mockable<String> mock;

                    public void testMethod() {
                    }
                    public static final class Mockable<T> { }
                }""");
    }
}
