//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;

import com.intellij.testFramework.RunsInEdt;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link ConvertSpyCallToFieldIntention}
 */
@RunsInEdt
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
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForMockitoSpyCallWithNotOneArgument() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(new Object(), \"\");\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForNonNewExpressionOrNonClassObjectAccessExpression() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Object object = new Object();\n" +
                "        Mockito.sp<caret>y(object);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForNonMockableTypeNewExpression() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(new String());\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForNonMockableTypeClassObjectAccess() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(String.class);\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testNotAvailableForDoNotMockAnnotatedTypeNewExpression() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.DoNotMock;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(new NotMockable());\n" +
                "    }\n" +
                "\n" +
                "    @DoNotMock\n" +
                "    public static class NotMockable { }\n" +
                "}");
    }

    @Test
    void testNotAvailableForDoNotMockAnnotatedTypeClassObjectAccess() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "import org.mockito.DoNotMock;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(NotMockable.class);\n" +
                "    }\n" +
                "\n" +
                "    @DoNotMock\n" +
                "    public static class NotMockable { }\n" +
                "}");
    }

    @Test
    void testAvailableForNewExpression() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class Available {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.sp<caret>y(new Mockable());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }

    @Test
    void testNotAvailableForClassObjectAccessExpression() {
        checkIntentionIsAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class Available {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(Mockable.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }

    //Conversion - standalone spy call + new expression

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToExplicitDefaultConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable()));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToNonDefaultConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable()));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable = new Mockable();\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultMatchingConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable(\"\")));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultToNonMatchingConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable(\"\")));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "    }\n" +
                "}\n",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultToDefaultAndNonMatchingConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable(\"\")));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}");
    }

    //---

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToAutoGenDefaultConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable()));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionDefaultToMultipleNonDefaultConstructors() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable()));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable = new Mockable();\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsStandaloneSpyCallNewExpressionNonDefaultToDefaultAndMultipleNonDefaultConstructors() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable(\"\")));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}");
    }

    //--- Generics

    @Test
    void testConvertsStandaloneSpyCallNewExpressionGenerics() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(new Mockable<String>()));\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable<String> mockable;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}");
    }

    //Conversion - standalone spy call + class object access expression

    @Test
    void testConvertsStandaloneSpyCallClassObjectAccess() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        aMethod(Mockito.s<caret>py(Mockable.class));\n" +
                "    }\n" +
                "\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable mockable;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        aMethod(mockable);\n" +
                "    }\n" +
                "\n" +
                "    public void aMethod(Mockable mockable) { }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }

    //Conversion - local variable declaration + new expression

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToExplicitDefaultConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToNonDefaultConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy = new Mockable();\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultMatchingConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable(\"\"));\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToNonMatchingConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable(\"\"));\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "    }\n" +
                "}\n",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "    }\n" +
                "}\n");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToDefaultAndNonMatchingConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable(\"\"));\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}");
    }

    //---

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToAutoGenDefaultConstructor() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionDefaultToMultipleNonDefaultConstructors() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy = new Mockable();\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToDefaultAndMultipleNonDefaultConstructors() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(new Mockable(\"\"));\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy = new Mockable(\"\");\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { \n" +
                "        public Mockable() { }\n" +
                "        public Mockable(int number) { }\n" +
                "        public Mockable(String string) { }\n" +
                "    }\n" +
                "}");
    }

    //--- Generics

    @Test
    void testConvertsLocalVariableDeclarationNewExpressionGenerics() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable<String> spy = Mockito.s<caret>py(new Mockable<String>());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable<String> spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}");
    }

    //--- var keyword

    @Test
    void testConvertsLocalVariableDeclarationWithVarKeywordWithNewExpressionNonGenerics() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var spy = Mockito.s<caret>py(new Mockable());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }

    @Test
    void testConvertsLocalVariableDeclarationWithVarKeywordWithNewExpressionGenerics() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var spy = Mockito.s<caret>py(new Mockable<String>());\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable<String> spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}");
    }

    //Conversion - local variable declaration + class object access expression

    @Test
    void testConvertsLocalVariableDeclarationClassObjectAccessExpression() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable spy = Mockito.s<caret>py(Mockable.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }

    //--- Generics

    @Test
    void testConvertsLocalVariableDeclarationClassObjectAccessExpressionGenerics() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockable<String> spy = Mockito.s<caret>py(Mockable.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable<String> spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable<T> { }\n" +
                "}");
    }

    //--- var keyword

    @Test
    void testConvertsLocalVariableDeclarationWithVarKeywordWithClassObjectAccessExpression() {
        checkIntentionRun(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    public void testMethod() {\n" +
                "        var spy = Mockito.s<caret>py(Mockable.class);\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}",
            "import org.mockito.Mockito;\n" +
                "import org.mockito.Spy;\n" +
                "\n" +
                "public class ConversionTest {\n" +
                "    @Spy\n" +
                "    Mockable spy;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "    }\n" +
                "\n" +
                "    public static final class Mockable { }\n" +
                "}");
    }
}
