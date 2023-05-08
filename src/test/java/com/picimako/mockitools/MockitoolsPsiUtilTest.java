//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.testFramework.RunsInEdt;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * Functional test for {@link MockitoolsPsiUtil}.
 */
@RunsInEdt
class MockitoolsPsiUtilTest extends MockitoolsTestBase {

    @Test
    void testIsASpecificMethod() {
        TestData[] testData = new TestData[]{
            new TestData("IsMockitoMockTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsMockitoMockTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.<caret>mock(Object.class);\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isMockitoMock(getMethodCall())),
            new TestData("IsMockitoSpyTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsMockitoSpyTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.<caret>spy(Object.class);\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isMockitoSpy(getMethodCall())),
            new TestData("IsAdditionalMatchersTest.java",
                "import org.mockito.BDDMockito;\n" +
                    "import org.mockito.AdditionalMatchers;\n" +
                    "\n" +
                    "public class IsAdditionalMatchersTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        BDDMockito.given(mock.equals(AdditionalMatchers.<caret>cmpEq(new Object()))).willReturn(10);\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isAdditionalMatchers(getMethodCall())),
            new TestData("IsTimesTest.java",
                "import org.mockito.Mockito;\n" +
                    "import static org.mockito.Mockito.times;\n" +
                    "\n" +
                    "public class IsTimesTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, <caret>times(1)).toString();\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isTimes(getMethodCall())),
            new TestData("IsCallsTest.java",
                "import org.mockito.Mockito;\n" +
                    "import static org.mockito.Mockito.calls;\n" +
                    "\n" +
                    "public class IsCallsTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, <caret>calls(1)).toString();\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isCalls(getMethodCall())),
            new TestData("IsAfterTest.java",
                "import org.mockito.Mockito;\n" +
                    "import static org.mockito.Mockito.after;\n" +
                    "\n" +
                    "public class IsAfterTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, <caret>after(1)).toString();\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isAfter(getMethodCall())),
            new TestData("IsTimeoutTest.java",
                "import org.mockito.Mockito;\n" +
                    "import static org.mockito.Mockito.timeout;\n" +
                    "\n" +
                    "public class IsTimeoutTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, <caret>timeout(1)).toString();\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isTimeout(getMethodCall())),
            new TestData("IsExtraInterfacesTest.java",
                "import org.mockito.Mockito;\n" +
                    "import java.util.List;\n" +
                    "\n" +
                    "public class IsExtraInterfacesTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class, Mockito.withSettings().<caret>extraInterfaces(List.class));\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isExtraInterfaces(getMethodCall())),
            new TestData("IsResetTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsResetTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Mockito.re<caret>set(Mockito.mock(Object.class));\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isReset(getMethodCall())),
            new TestData("IsIgnoreStubsTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsIgnoreStubsTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Mockito.ignore<caret>Stubs(Mockito.mock(Object.class));\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isIgnoreStubs(getMethodCall())),
            new TestData("IsMatchersTest.java",
                "import org.mockito.Matchers;\n" +
                    "\n" +
                    "public class IsMatchersTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Matchers.anyS<caret>tring();\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isMatchers(getMethodCall()))
        };

        for (TestData data : testData) {
            getFixture().configureByText(data.fileName, data.fileContent);
            assertThat(data.isSpecificMethod.get())
                .describedAs("Failed during the assertion of " + data.fileName)
                .isTrue();
        }
    }

    @Test
    void testIsNotASpecificMethod() {
        String fileContent = "import org.mockito.Mockito;\n" +
            "\n" +
            "public class %s {\n" +
            "    public void testMethod() {\n" +
            "        String toString = new Object().<caret>toString();\n" +
            "    }\n" +
            "}";
        TestData[] testData = new TestData[]{
            new TestData("IsNotMockitoMockTest.java", String.format(fileContent, "IsNotMockitoMockTest"), () -> MockitoolsPsiUtil.isMockitoMock(getMethodCall())),
            new TestData("IsNotMockitoSpyTest.java", String.format(fileContent, "IsNotMockitoSpyTest"), () -> MockitoolsPsiUtil.isMockitoSpy(getMethodCall())),
            new TestData("IsNotAdditionalMatchersTest.java", String.format(fileContent, "IsNotAdditionalMatchersTest"), () -> MockitoolsPsiUtil.isAdditionalMatchers(getMethodCall())),
            new TestData("isNotTimesTest.java", String.format(fileContent, "isNotTimesTest"), () -> MockitoolsPsiUtil.isTimes(getMethodCall())),
            new TestData("isNotCallsTest.java", String.format(fileContent, "isNotCallsTest"), () -> MockitoolsPsiUtil.isCalls(getMethodCall())),
            new TestData("isNotAfterTest.java", String.format(fileContent, "isNotAfterTest"), () -> MockitoolsPsiUtil.isAfter(getMethodCall())),
            new TestData("isNotTimeoutTest.java", String.format(fileContent, "isNotTimeoutTest"), () -> MockitoolsPsiUtil.isTimeout(getMethodCall())),
            new TestData("isNotExtraInterfacesTest.java", String.format(fileContent, "isNotExtraInterfacesTest"), () -> MockitoolsPsiUtil.isExtraInterfaces(getMethodCall())),
            new TestData("isNotResetTest.java", String.format(fileContent, "isNotResetTest"), () -> MockitoolsPsiUtil.isReset(getMethodCall())),
            new TestData("isNotIgnoreStunsTest.java", String.format(fileContent, "isNotIgnoreStubsTest"), () -> MockitoolsPsiUtil.isIgnoreStubs(getMethodCall()))
        };

        for (TestData data : testData) {
            getFixture().configureByText(data.fileName, data.fileContent);
            assertThat(data.isSpecificMethod.get())
                .describedAs("Failed during the assertion of " + data.fileName)
                .isFalse();
        }
    }

    @Test
    void testIsCalledTheSameButNotTheSpecificMethod() {
        TestData[] testData = new TestData[]{
            new TestData("IsMockNotMockitoMockTest.java",
                "\n" +
                    "public class IsMockNotMockitoMockTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Mock.<caret>mock(Object.class);\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class Mock {\n" +
                    "        public static Mock mock(Object object) {\n" +
                    "            return new Mock();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isMockitoMock(getMethodCall())),
            new TestData("IsSpyNotMockitoSpyTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsSpyNotMockitoSpyTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Spy.<caret>spy(Object.class);\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class Spy {\n" +
                    "        public static Spy spy(Object object) {\n" +
                    "            return new Spy();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isMockitoSpy(getMethodCall())),
            new TestData("IsTimesNotMockitoTimesTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsTimesNotMockitoTimesTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, Times.<caret>times(1)).toString();\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class Times {\n" +
                    "        public static Times times(Object object) {\n" +
                    "            return new Times();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isTimes(getMethodCall())),
            new TestData("IsCallsNotMockitoCallsTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsCallsNotMockitoCallsTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, Times.<caret>calls(1)).toString();\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class Calls {\n" +
                    "        public static Calls calls(Object object) {\n" +
                    "            return new Calls();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isCalls(getMethodCall())),
            new TestData("IsAfterNotMockitoAfterTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsAfterNotMockitoAfterTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, Times.<caret>after(1)).toString();\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class After {\n" +
                    "        public static After after(Object object) {\n" +
                    "            return new After();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isAfter(getMethodCall())),
            new TestData("IsTimeoutNotMockitoTimeoutTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsTimeoutNotMockitoTimeoutTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, Times.<caret>timeout(1)).toString();\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class Timeout {\n" +
                    "        public static Timeout timeout(Object object) {\n" +
                    "            return new Timeout();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isTimeout(getMethodCall())),
            new TestData("IsExtraInterfacesNotMockitoSettingsExtraInterfacesTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsExtraInterfacesNotMockitoSettingsExtraInterfacesTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Mockito.verify(mock, ExtraInterfaces.<caret>extraInterfaces(1)).toString();\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class ExtraInterfaces {\n" +
                    "        public static ExtraInterfaces extraInterfaces(Object object) {\n" +
                    "            return new ExtraInterfaces();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isExtraInterfaces(getMethodCall())),
            new TestData("IsResetNotMockitoResetTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsResetNotMockitoResetTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        Reset.re<caret>set(mock);\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class Reset {\n" +
                    "        public static Reset reset(Object object) {\n" +
                    "            return new Reset();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isReset(getMethodCall())),
            new TestData("IsIgnoreStubsNotMockitoIgnoreStubsTest.java",
                "import org.mockito.Mockito;\n" +
                    "\n" +
                    "public class IsIgnoreStubsNotMockitoIgnoreStubsTest {\n" +
                    "    public void testMethod() {\n" +
                    "        Object mock = Mockito.mock(Object.class);\n" +
                    "        IgnoreStubs.ignore<caret>Stubs(mock);\n" +
                    "    }\n" +
                    "\n" +
                    "    private static final class IgnoreStubs {\n" +
                    "        public static IgnoreStubs ignoreStubs(Object object) {\n" +
                    "            return new IgnoreStubs();\n" +
                    "        }\n" +
                    "    }\n" +
                    "}", () -> MockitoolsPsiUtil.isIgnoreStubs(getMethodCall()))
        };

        for (TestData data : testData) {
            getFixture().configureByText(data.fileName, data.fileContent);
            assertThat(data.isSpecificMethod.get())
                .describedAs("Failed during the assertion of " + data.fileName)
                .isFalse();
        }
    }

    //isOfTypeArgumentCaptor

    @Test
    void testIsArgumentCaptor() {
        getFixture().configureByText("IsArgumentCaptorTest.java",
            "import org.mockito.ArgumentCaptor;\n" +
                "import org.mockito.Captor;\n" +
                "\n" +
                "public class IsArgumentCaptorTest {\n" +
                "    @Captor\n" +
                "    public ArgumentCaptor<String> <caret>captor;\n" +
                "}\n");

        assertThat(MockitoolsPsiUtil.isOfTypeArgumentCaptor(getField())).isTrue();
    }

    @Test
    void testIsNotArgumentCaptor() {
        getFixture().configureByText("IsNotArgumentCaptorTest.java",
            "import org.mockito.ArgumentCaptor;\n" +
                "import org.mockito.Captor;\n" +
                "\n" +
                "public class IsNotArgumentCaptorTest {\n" +
                "    @Captor\n" +
                "    public String <caret>captor;\n" +
                "}\n");

        assertThat(MockitoolsPsiUtil.isOfTypeArgumentCaptor(getField())).isFalse();
    }

    //isMatchers

    @Test
    void testIsNotMatchers() {
        getFixture().configureByText("isNotMatchersTest.java",
            "import org.mockito.ArgumentMatchers;\n" +
                "\n" +
                "public class isNotMatchersTest {\n" +
                "    public void testMethod() {\n" +
                "        ArgumentMatchers.anyS<caret>tring();\n" +
                "    }\n" +
                "}");

        assertThat(MockitoolsPsiUtil.isMatchers(getMethodCall())).isFalse();
    }

    private PsiMethodCallExpression getMethodCall() {
        return (PsiMethodCallExpression) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent().getParent();
    }

    private PsiField getField() {
        return (PsiField) getFixture().getFile().findElementAt(getFixture().getCaretOffset()).getParent();
    }

    private static final class TestData {
        final String fileName;
        final String fileContent;
        final Supplier<Boolean> isSpecificMethod;

        public TestData(String fileName, String fileContent, Supplier<Boolean> isSpecificMethod) {
            this.fileName = fileName;
            this.fileContent = fileContent;
            this.isSpecificMethod = isSpecificMethod;
        }
    }
}
