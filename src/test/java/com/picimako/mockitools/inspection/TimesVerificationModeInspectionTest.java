//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;

/**
 * Functional test for {@link TimesVerificationModeInspection}.
 */
public class TimesVerificationModeInspectionTest extends MockitoolsV3InspectionTestBase {

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
                "        inOrder.verify(mockObject, <caret>times(0));\n" +
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
                "        inOrder.verify(mockObject, Mockito.never());\n" +
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
                "        inOrder.verify(mockObject, <caret>times(0).description(\"message\"));\n" +
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
                "        inOrder.verify(mockObject, Mockito.never().description(\"message\"));\n" +
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
                "public class TimesOneInOrder {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject, <caret>times(1));\n" +
                "    }\n" +
                "}",
            "import static org.mockito.Mockito.times;\n" +
                "\n" +
                "import org.mockito.InOrder;\n" +
                "import org.mockito.Mockito;\n" +
                "\n" +
                "public class TimesOneInOrder {\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        Object mockObject = Mockito.mock(Object.class);\n" +
                "        InOrder inOrder = Mockito.inOrder(mockObject);\n" +
                "        inOrder.verify(mockObject);\n" +
                "    }\n" +
                "}");
    }
}
