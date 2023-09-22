//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.junit5.RunInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertMockSpyFieldToCallIntention}.
 */
@RunInEdt
class ConvertMockSpyFieldToCallWithSettingsIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockSpyFieldToCallIntention();
    }

    //@Mock -> Mockito.mock(<type>.class) - empty config

    @Test
    void testConvertsMockFieldToCallWithEmptyConfig() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock()
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                    }
                }""");
    }

    //@Spy -> Mockito.spy(<type>.class) - empty config

    @Test
    void testConvertsSpyFieldToCallWithEmptyConfig() {
        checkIntentionRun(
            """
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy()
                    Object s<caret>py;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {

                    public void method() {
                        Object spy = Mockito.spy(Object.class);
                    }
                }""");
    }

    //@Spy -> Mockito.spy(new <type>())

    @Test
    void testConvertsSpyFieldWithInitializerToCall() {
        checkIntentionRun(
            """
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Object s<caret>py = new Object();

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {

                    public void method() {
                        Object spy = Mockito.spy(new Object());
                    }
                }""");
    }

    //@Mock -> Mockito.mock(<type>.class, String)

    @Test
    void testConvertsMockFieldToCallWithNameSpecificOverride() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(name = "some name")
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, "some name");
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithEmptyNameSpecificOverride() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(name = "")
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                    }
                }""");
    }

    //@Mock -> Mockito.mock(<type>.class, Answer)

    @Test
    void testConvertsMockFieldToCallWithAnswerSpecificOverride() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(answer = Answers.CALLS_REAL_METHODS)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Answers;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Answers.CALLS_REAL_METHODS);
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithDefaultAnswerSpecificOverride() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(answer = Answers.RETURNS_DEFAULTS)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Answers;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                    }
                }""");
    }

    //TODO: @Mock -> Mockito.mock(<type>.class, MockSettings)

    //extraInterfaces

    @Test
    void testConvertsMockFieldToCallWithEmptyExtraInterface() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(extraInterfaces = {})
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithSingleExtraInterface() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import java.util.List;

                public class ConversionTest {
                    @Mock(extraInterfaces = List.class)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                import java.util.List;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class));
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithMultipleExtraInterfaces() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import java.util.List;
                import java.util.Set;

                public class ConversionTest {
                    @Mock(extraInterfaces = {List.class, Set.class})
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                import java.util.List;
                import java.util.Set;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class, Set.class));
                    }
                }""");
    }

    //booleans

    @Test
    void testConvertsMockFieldToCallWithStubOnly() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(stubOnly = true)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().stubOnly());
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithStubOnlyFalse() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(stubOnly = false)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithEmptyName() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(stubOnly = true, name = "")
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().stubOnly());
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithSerializable() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(serializable = true)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().serializable());
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithLenient() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(lenient = true)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().lenient());
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithWithoutAnnotations() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock(withoutAnnotations = true)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().withoutAnnotations());
                    }
                }""");
    }

    //Complex settings

    @Test
    void testConvertsMockFieldToCallWithMockSettings1() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import java.util.List;

                public class ConversionTest {
                    @Mock(extraInterfaces = List.class, name = "some name")
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                import java.util.List;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().name("some name").extraInterfaces(List.class));
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithMockSettings2() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import java.util.List;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(name = "some name", extraInterfaces = List.class, answer = Answers.CALLS_REAL_METHODS)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import java.util.List;
                import org.mockito.Answers;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().name("some name").defaultAnswer(Answers.CALLS_REAL_METHODS).extraInterfaces(List.class));
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithMockSettings3() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import java.util.List;
                import java.util.Set;
                import org.mockito.Answers;

                public class ConversionTest {
                    @Mock(lenient = true, extraInterfaces = {List.class, Set.class}, name = "some name", answer = Answers.CALLS_REAL_METHODS)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import java.util.List;
                import java.util.Set;
                import org.mockito.Answers;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().lenient().name("some name").defaultAnswer(Answers.CALLS_REAL_METHODS).extraInterfaces(List.class, Set.class));
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithMockSettingsStrictnessDefault() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import java.util.List;

                public class ConversionTest {
                    @Mock(strictness = Mock.Strictness.TEST_LEVEL_DEFAULT)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                import java.util.List;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithMockSettingsStrictness() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import java.util.List;

                public class ConversionTest {
                    @Mock(strictness = Mock.Strictness.WARN)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                import org.mockito.quality.Strictness;

                import java.util.List;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().strictness(Strictness.WARN));
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToCallWithMockSettingsMockMaker() {
        checkIntentionRun(
            """
                import org.mockito.Mock;
                import org.mockito.MockMakers;
                import java.util.List;

                public class ConversionTest {
                    @Mock(mockMaker = MockMakers.INLINE)
                    Object mo<caret>ck;

                    public void method() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.MockMakers;
                import org.mockito.Mockito;

                import java.util.List;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class, Mockito.withSettings().mockMaker(MockMakers.INLINE));
                    }
                }""");
    }
}
