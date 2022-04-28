//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention;

import com.intellij.codeInsight.intention.IntentionAction;

import com.picimako.mockitools.ThirdPartyLibraryLoader;

/**
 * Functional test for {@link ConvertSpyCallToFieldIntention}
 */
public class ConvertSpyCallToFieldIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ConvertSpyCallToFieldIntention();
    }

    @Override
    protected void loadLibs() {
        ThirdPartyLibraryLoader.loadMockito4Latest(myFixture.getProjectDisposable(), getModule());
    }

    //Availability

    public void testNotAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotJava.xml", "<tag><caret></tag>");
    }

    public void testNotAvailableForNonMethodCallIdentifier() {
        checkIntentionIsNotAvailable(
            "public class NotAvaila<caret>ble { }");
    }

    public void testNotAvailableForNonMockitoSpyCall() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.mo<caret>ck(Object.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForMockitoSpyCallWithNotOneArgument() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(new Object(), \"\");\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForNonNewExpressionOrNonClassObjectAccessExpression() {
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

    public void testNotAvailableForNonMockableTypeNewExpression() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(new String());\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForNonMockableTypeClassObjectAccess() {
        checkIntentionIsNotAvailable(
            "import org.mockito.Mockito;\n" +
                "\n" +
                "public class NotAvailable {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.sp<caret>y(String.class);\n" +
                "    }\n" +
                "}");
    }

    public void testNotAvailableForDoNotMockAnnotatedTypeNewExpression() {
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

    public void testNotAvailableForDoNotMockAnnotatedTypeClassObjectAccess() {
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

    public void testAvailableForNewExpression() {
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

    public void testNotAvailableForClassObjectAccessExpression() {
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

    public void testConvertsStandaloneSpyCallNewExpressionDefaultToExplicitDefaultConstructor() {
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

    public void testConvertsStandaloneSpyCallNewExpressionDefaultToNonDefaultConstructor() {
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

    public void testConvertsStandaloneSpyCallNewExpressionNonDefaultMatchingConstructor() {
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

    public void testConvertsStandaloneSpyCallNewExpressionNonDefaultToNonMatchingConstructor() {
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

    public void testConvertsStandaloneSpyCallNewExpressionNonDefaultToDefaultAndNonMatchingConstructor() {
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

    public void testConvertsStandaloneSpyCallNewExpressionDefaultToAutoGenDefaultConstructor() {
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

    public void testConvertsStandaloneSpyCallNewExpressionDefaultToMultipleNonDefaultConstructors() {
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

    public void testConvertsStandaloneSpyCallNewExpressionNonDefaultToDefaultAndMultipleNonDefaultConstructors() {
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

    public void testConvertsStandaloneSpyCallNewExpressionGenerics() {
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

    public void testConvertsStandaloneSpyCallClassObjectAccess() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionDefaultToExplicitDefaultConstructor() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionDefaultToNonDefaultConstructor() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionNonDefaultMatchingConstructor() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToNonMatchingConstructor() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToDefaultAndNonMatchingConstructor() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionDefaultToAutoGenDefaultConstructor() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionDefaultToMultipleNonDefaultConstructors() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionNonDefaultToDefaultAndMultipleNonDefaultConstructors() {
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

    public void testConvertsLocalVariableDeclarationNewExpressionGenerics() {
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

    public void testConvertsLocalVariableDeclarationWithVarKeywordWithNewExpressionNonGenerics() {
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

    public void testConvertsLocalVariableDeclarationWithVarKeywordWithNewExpressionGenerics() {
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

    public void testConvertsLocalVariableDeclarationClassObjectAccessExpression() {
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

    public void testConvertsLocalVariableDeclarationClassObjectAccessExpressionGenerics() {
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

    public void testConvertsLocalVariableDeclarationWithVarKeywordWithClassObjectAccessExpression() {
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
