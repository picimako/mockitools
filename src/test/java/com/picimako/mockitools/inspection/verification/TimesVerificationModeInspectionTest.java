//Copyright 2022 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import com.picimako.mockitools.inspection.verification.TimesVerificationModeInspection;

/**
 * Functional test for {@link TimesVerificationModeInspection}.
 */
public class TimesVerificationModeInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new TimesVerificationModeInspection();
    }

    public void testTimesArgumentsTest() {
        doJavaTest();
    }

    public void testTimesArgumentsReportTimesZeroOnlyTest() {
        TimesVerificationModeInspection inspection = new TimesVerificationModeInspection();
        inspection.reportTimesOneCanBeOmitted = false;
        doJavaTest(inspection);
    }

    public void testTimesArgumentsReportTimesOneOnlyTest() {
        TimesVerificationModeInspection inspection = new TimesVerificationModeInspection();
        inspection.reportTimesZeroToNever = false;
        doJavaTest(inspection);
    }

    public void testTimesOneDeletionBDDMockitoShouldIsNotAvailableWithFurtherSettings() {
        doJavaTest("TimesOneDeletionTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        BDDMockito.then(mockObject).should(<caret>times(1).description(\"description\")).toString();\n" +
                "    }\n" +
                "}");
    }

    //Mockito.verify

    public void testTimesZeroReplacement() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>times(0)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), Mockito.never()).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesZeroWithSettingsReplacement() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>times(0).description(\"\")).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), Mockito.never().description(\"\")).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesOneDeletion() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object(), <caret>times(1)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "    public void testMethod() {\n" +
                "        Mockito.verify(new Object()).toString();\n" +
                "    }\n" +
                "}");
    }

    //InOrder.verify

    public void testTimesZeroReplacementInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementInOrderTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "    import org.mockito.InOrder;\n" +
                "    import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementInOrderTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject, <caret>times(0)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "    import org.mockito.InOrder;\n" +
                "    import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementInOrderTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject, Mockito.never()).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesZeroWithSettingsReplacementInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementInOrderTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "    import org.mockito.InOrder;\n" +
                "    import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementInOrderTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject, <caret>times(0).description(\"message\")).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "    import org.mockito.InOrder;\n" +
                "    import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementInOrderTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject, Mockito.never().description(\"message\")).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesOneDeletionInOrder() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject, <caret>times(1)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject).toString();\n" +
                "    }\n" +
                "}");
    }

    //BDDMockito.should()

    public void testTimesZeroReplacementBDDMockitoShould() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        BDDMockito.then(mockObject).should(<caret>times(0)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        BDDMockito.then(mockObject).should(Mockito.never()).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesZeroWithSettingsReplacementBDDMockitoShould() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementBDDMockitoShouldTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        BDDMockito.then(mockObject).should(<caret>times(0).description(\"description\")).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        BDDMockito.then(mockObject).should(Mockito.never().description(\"description\")).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesOneDeletionBDDMockitoShould() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        BDDMockito.then(mockObject).should(<caret>times(1)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        BDDMockito.then(mockObject).should().toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesZeroReplacementBDDMockitoShouldInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(inOrder, <caret>times(0)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(inOrder, Mockito.never()).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesZeroWithSettingsReplacementBDDMockitoShouldInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementBDDMockitoShouldTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(inOrder, <caret>times(0).description(\"description\")).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(inOrder, Mockito.never().description(\"description\")).toString();\n" +
                "    }\n" +
                "}");
    }

    public void testTimesOneDeletionBDDMockitoShouldInOrder() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(inOrder, <caret>times(1)).toString();\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.BDDMockito;\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        BDDMockito.then(mockObject).should(inOrder).toString();\n" +
                "    }\n" +
                "}");
    }

    //MockedStatic.verify()

    public void testTimesZeroReplacementMockedStaticVerify() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {\n" +
                "            listMockedStatic.verify(List::of, <caret>times(0));\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {\n" +
                "            listMockedStatic.verify(List::of, Mockito.never());\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testTimesZeroWithSettingsReplacementMockedStaticVerify() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementBDDMockitoShouldTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {\n" +
                "            listMockedStatic.verify(List::of, <caret>times(0).description(\"description\"));\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {\n" +
                "            listMockedStatic.verify(List::of, Mockito.never().description(\"description\"));\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    public void testTimesOneDeletionMockedStaticVerify() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {\n" +
                "            listMockedStatic.verify(List::of, <caret>times(1));\n" +
                "        }\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.MockedStatic;\n" +
                "import org.mockito.Mockito;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class TimesOneDeletionTest {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {\n" +
                "            listMockedStatic.verify(List::of);\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}
