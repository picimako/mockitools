//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertMockSpyFieldToCallIntention}.
 */
class ConvertMockSpyFieldToCallIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertMockSpyFieldToCallIntention();
    }

    //Intention availability

    @Test
    void testIntentionIsNotAvailableForNonMockNonSpyField() {
        checkIntentionIsNotAvailable(
            """
                public class NotAvailable {
                    Object mo<caret>ck;
                }""");
    }

    @Test
    void testIntentionIsNotAvailableWhenTheParentClassHasNoMethod() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mock;

                public class NotAvailable {
                    @Mock
                    Object mo<caret>ck;
                }""");
    }

    @Test
    void testIntentionIsNotAvailableWhenBothMockAndSpyAnnotationsAreOnField() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mock;
                import org.mockito.Spy;

                public class NotAvailable {
                    @Mock
                    @Spy
                    Object mo<caret>ck;
                }""");
    }

    @Test
    void testIntentionIsAvailableOnMockField() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mock;

                public class Available {
                    @Mock
                    Object mo<caret>ck;

                    public void method() {}
                }""");
    }

    @Test
    void testIntentionIsAvailableOnSpyField() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Spy;

                public class Available {
                    @Spy
                    Object mo<caret>ck;

                    public void method() {}
                }""");
    }

    //@Mock -> Mockito.mock(<type>.class)

    @Test
    void testConvertsMockFieldToMockCallFirstInCodeBlockOfManyStatements() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock
                    Object mo<caret>ck;

                    public void method() {
                        int number = 10;
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                        int number = 10;
                    }
                }""");
    }

    @Test
    void testConvertsMockFieldToMockCallFirstInCodeBlockOfNoStatement() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock
                    Object mo<caret>ck;

                    public void method() {}
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
    void testConvertsMockFieldToMockCallIntoSelectedMethod() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock
                    Object mo<caret>ck;

                    public void method() {
                    }

                    public void anotherMethod() {
                    }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.mock(Object.class);
                    }

                    public void anotherMethod() {
                    }
                }""");
    }

    //@Spy -> Mockito.spy(<type>.class)

    @Test
    void testConvertsSpyFieldToSpyCallFirstInCodeBlockOfManyStatements() {
        checkIntentionRun(
            """
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Object mo<caret>ck;

                    public void method() {
                        int number = 10;
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.spy(Object.class);
                        int number = 10;
                    }
                }""");
    }

    @Test
    void testConvertsSpyFieldToSpyCallFirstInCodeBlockOfNoStatement() {
        checkIntentionRun(
            """
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Object mo<caret>ck;

                    public void method() {}
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.spy(Object.class);
                    }
                }""");
    }

    @Test
    void testConvertsSpyFieldToSpyCallIntoSelectedMethod() {
        checkIntentionRun(
            """
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Object mo<caret>ck;

                    public void method() {
                    }

                    public void anotherMethod() {
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {

                    public void method() {
                        Object mock = Mockito.spy(Object.class);
                    }

                    public void anotherMethod() {
                    }
                }""");
    }

    //Generics: @Spy Type<type>; -> Mockito.spy(Type.class)

    @Test
    void testConvertsSpyFieldWithGenericsToSpyCall() {
        checkIntentionRun(
            """
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    MockObject<String> mo<caret>ck;

                    public void method() {
                    }

                    public static final class MockObject<T> { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {

                    public void method() {
                        MockObject<String> mock = Mockito.spy(MockObject.class);
                    }

                    public static final class MockObject<T> { }
                }""");
    }

    @Test
    void testConvertsMockFieldWithGenericsToMockCall() {
        checkIntentionRun(
            """
                import org.mockito.Mock;

                public class ConversionTest {
                    @Mock
                    MockObject<String> mo<caret>ck;

                    public void method() {
                    }

                    public static final class MockObject<T> { }
                }""",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;

                public class ConversionTest {

                    public void method() {
                        MockObject<String> mock = Mockito.mock(MockObject.class);
                    }

                    public static final class MockObject<T> { }
                }""");
    }
}
