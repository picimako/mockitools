//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;

import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertSpyCallToFieldIntention}
 */
class ConvertSpyCallToFieldIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertSpyCallToFieldIntention();
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
    void testNotAvailableForNonMockitoSpyCall() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.mo<caret>ck(Object.class);
                    }
                }""");
    }

    @Test
    void testNotAvailableForMockitoSpyCallWithNotOneArgument() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.sp<caret>y(new Object(), "");
                    }
                }""");
    }

    @Test
    void testNotAvailableForNonNewExpressionOrNonClassObjectAccessExpression() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Object object = new Object();
                        Mockito.sp<caret>y(object);
                    }
                }""");
    }

    @Test
    void testNotAvailableForNonMockableTypeNewExpression() {
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
    void testNotAvailableForNonMockableTypeClassObjectAccess() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.sp<caret>y(String.class);
                    }
                }""");
    }

    @Test
    void testNotAvailableForDoNotMockAnnotatedTypeNewExpression() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.DoNotMock;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.sp<caret>y(new NotMockable());
                    }

                    @DoNotMock
                    public static class NotMockable { }
                }""");
    }

    @Test
    void testNotAvailableForDoNotMockAnnotatedTypeClassObjectAccess() {
        checkIntentionIsNotAvailable(
            """
                import org.mockito.Mockito;
                import org.mockito.DoNotMock;

                public class NotAvailable {
                    public void testMethod() {
                        Mockito.sp<caret>y(NotMockable.class);
                    }

                    @DoNotMock
                    public static class NotMockable { }
                }""");
    }

    @Test
    void testAvailableForNewExpression() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                public class Available {
                    public void testMethod() {
                        Mockable spy = Mockito.sp<caret>y(new Mockable());
                    }

                    public static final class Mockable { }
                }""");
    }

    @Test
    void testNotAvailableForClassObjectAccessExpression() {
        checkIntentionIsAvailable(
            """
                import org.mockito.Mockito;

                public class Available {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(Mockable.class);
                    }

                    public static final class Mockable { }
                }""");
    }

    //Conversion - standalone spy call + new expression

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToExplicitDefaultConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable()));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable() { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable;

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable() { }
                    }
                }""");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToNonDefaultConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable()));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable = new Mockable();

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """);
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultMatchingConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable("")));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable = new Mockable("");

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """);
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultToNonMatchingConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable("")));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable = new Mockable("");

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                    }
                }
                """);
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultToDefaultAndNonMatchingConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable("")));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(String string) { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable = new Mockable("");

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(String string) { }
                    }
                }""");
    }

    //---

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToAutoGenDefaultConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable()));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable;

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable { }
                }""");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToMultipleNonDefaultConstructors() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable()));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable = new Mockable();

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultToDefaultAndMultipleNonDefaultConstructors() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable("")));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable = new Mockable("");

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""");
    }

    //--- Generics

    @Test
    void testConvertsStandaloneSpyCallNewExpressionGenerics() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(new Mockable<String>()));
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable<T> { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable<String> mockable;

                    public void testMethod() {
                        aMethod(mockable);
                    }
                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable<T> { }
                }""");
    }

    //Conversion - standalone spy call + class object access expression

    @Test
    void testConvertsStandaloneSpyCallClassObjectAccess() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        aMethod(Mockito.s<caret>py(Mockable.class));
                    }

                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable mockable;

                    public void testMethod() {
                        aMethod(mockable);
                    }

                    public void aMethod(Mockable mockable) { }

                    public static final class Mockable { }
                }""");
    }

    //Conversion - local variable declaration + new expression

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToExplicitDefaultConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable());
                    }

                    public static final class Mockable {\s
                        public Mockable() { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy;

                    public void testMethod() {
                    }

                    public static final class Mockable {\s
                        public Mockable() { }
                    }
                }""");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToNonDefaultConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable());
                    }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy = new Mockable();

                    public void testMethod() {
                    }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """);
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultMatchingConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable(""));
                    }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy = new Mockable("");

                    public void testMethod() {
                    }

                    public static final class Mockable {\s
                        public Mockable(String string) { }
                    }
                }
                """);
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToNonMatchingConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable(""));
                    }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy = new Mockable("");

                    public void testMethod() {
                    }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                    }
                }
                """);
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToDefaultAndNonMatchingConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable(""));
                    }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(String string) { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy = new Mockable("");

                    public void testMethod() {
                    }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(String string) { }
                    }
                }""");
    }

    //---

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToAutoGenDefaultConstructor() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable());
                    }

                    public static final class Mockable { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy;

                    public void testMethod() {
                    }

                    public static final class Mockable { }
                }""");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToMultipleNonDefaultConstructors() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable());
                    }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy = new Mockable();

                    public void testMethod() {
                    }

                    public static final class Mockable {\s
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToDefaultAndMultipleNonDefaultConstructors() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(new Mockable(""));
                    }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy = new Mockable("");

                    public void testMethod() {
                    }

                    public static final class Mockable {\s
                        public Mockable() { }
                        public Mockable(int number) { }
                        public Mockable(String string) { }
                    }
                }""");
    }

    //--- Generics

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionGenerics() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable<String> spy = Mockito.s<caret>py(new Mockable<String>());
                    }

                    public static final class Mockable<T> { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable<String> spy;

                    public void testMethod() {
                    }

                    public static final class Mockable<T> { }
                }""");
    }

    //--- var keyword

    @Test
    void testConvertsLocalVariableDeclarationWithVarKeywordWithNewExpressionNonGenerics() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        var spy = Mockito.s<caret>py(new Mockable());
                    }

                    public static final class Mockable { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy;

                    public void testMethod() {
                    }

                    public static final class Mockable { }
                }""");
    }

    @Test
    void testConvertsLocalVariableDeclarationWithVarKeywordWithNewExpressionGenerics() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        var spy = Mockito.s<caret>py(new Mockable<String>());
                    }

                    public static final class Mockable<T> { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable<String> spy;

                    public void testMethod() {
                    }

                    public static final class Mockable<T> { }
                }""");
    }

    //Conversion - local variable declaration + class object access expression

    @Test
    void testConvertsLocalVariableDeclarationClassObjectAccessExpression() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable spy = Mockito.s<caret>py(Mockable.class);
                    }

                    public static final class Mockable { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy;

                    public void testMethod() {
                    }

                    public static final class Mockable { }
                }""");
    }

    //--- Generics

    @Test
    void testConvertsLocalVariableDeclarationClassObjectAccessExpressionGenerics() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        Mockable<String> spy = Mockito.s<caret>py(Mockable.class);
                    }

                    public static final class Mockable<T> { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable<String> spy;

                    public void testMethod() {
                    }

                    public static final class Mockable<T> { }
                }""");
    }

    //--- var keyword

    @Test
    void testConvertsLocalVariableDeclarationWithVarKeywordWithClassObjectAccessExpression() {
        checkIntentionRun(
            """
                import org.mockito.Mockito;

                public class ConversionTest {
                    public void testMethod() {
                        var spy = Mockito.s<caret>py(Mockable.class);
                    }

                    public static final class Mockable { }
                }""",
            """
                import org.mockito.Mockito;
                import org.mockito.Spy;

                public class ConversionTest {
                    @Spy
                    Mockable spy;

                    public void testMethod() {
                    }

                    public static final class Mockable { }
                }""");
    }
}
