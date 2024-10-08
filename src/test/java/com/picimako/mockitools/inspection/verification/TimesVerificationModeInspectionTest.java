//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.verification;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import com.picimako.mockitools.inspection.verification.TimesVerificationModeInspection;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link TimesVerificationModeInspection}.
 */
class TimesVerificationModeInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new TimesVerificationModeInspection();
    }

    @Test
    void testTimesArgumentsTest() {
        doJavaTest();
    }

    @Test
    void testTimesArgumentsReportTimesZeroOnlyTest() {
        TimesVerificationModeInspection inspection = new TimesVerificationModeInspection();
        inspection.reportTimesOneCanBeOmitted = false;
        doJavaTest(inspection);
    }

    @Test
    void testTimesArgumentsReportTimesOneOnlyTest() {
        TimesVerificationModeInspection inspection = new TimesVerificationModeInspection();
        inspection.reportTimesZeroToNever = false;
        doJavaTest(inspection);
    }

    @Test
    void testTimesOneDeletionBDDMockitoShouldIsNotAvailableWithFurtherSettings() {
        doJavaTest("TimesOneDeletionTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        BDDMockito.then(mockObject).should(<caret>times(1).description("description")).toString();
                    }
                }""");
    }

    //Mockito.verify

    @Test
    void testTimesZeroReplacement() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), <caret>times(0)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), Mockito.never()).toString();
                    }
                }""");
    }

    @Test
    void testTimesZeroWithSettingsReplacement() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), <caret>times(0).description("")).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), Mockito.never().description("")).toString();
                    }
                }""");
    }

    @Test
    void testTimesOneDeletion() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.Mockito;

                public class TimesOneDeletionTest {
                    public void testMethod() {
                        Mockito.verify(new Object(), <caret>times(1)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.Mockito;

                public class TimesOneDeletionTest {
                    public void testMethod() {
                        Mockito.verify(new Object()).toString();
                    }
                }""");
    }

    //InOrder.verify

    @Test
    void testTimesZeroReplacementInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementInOrderTest.java",
            """
                import static org.mockito.Mockito.times;

                    import org.mockito.InOrder;
                    import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementInOrderTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        inOrder.verify(mockObject, <caret>times(0)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                    import org.mockito.InOrder;
                    import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementInOrderTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        inOrder.verify(mockObject, Mockito.never()).toString();
                    }
                }""");
    }

    @Test
    void testTimesZeroWithSettingsReplacementInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementInOrderTest.java",
            """
                import static org.mockito.Mockito.times;

                    import org.mockito.InOrder;
                    import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementInOrderTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        inOrder.verify(mockObject, <caret>times(0).description("message")).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                    import org.mockito.InOrder;
                    import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementInOrderTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        inOrder.verify(mockObject, Mockito.never().description("message")).toString();
                    }
                }""");
    }

    @Test
    void testTimesOneDeletionInOrder() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        inOrder.verify(mockObject, <caret>times(1)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        inOrder.verify(mockObject).toString();
                    }
                }""");
    }

    //BDDMockito.should()

    @Test
    void testTimesZeroReplacementBDDMockitoShould() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        BDDMockito.then(mockObject).should(<caret>times(0)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        BDDMockito.then(mockObject).should(Mockito.never()).toString();
                    }
                }""");
    }

    @Test
    void testTimesZeroWithSettingsReplacementBDDMockitoShould() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementBDDMockitoShouldTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        BDDMockito.then(mockObject).should(<caret>times(0).description("description")).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        BDDMockito.then(mockObject).should(Mockito.never().description("description")).toString();
                    }
                }""");
    }

    @Test
    void testTimesOneDeletionBDDMockitoShould() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        BDDMockito.then(mockObject).should(<caret>times(1)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.Mockito;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        BDDMockito.then(mockObject).should().toString();
                    }
                }""");
    }

    @Test
    void testTimesZeroReplacementBDDMockitoShouldInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(inOrder, <caret>times(0)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(inOrder, Mockito.never()).toString();
                    }
                }""");
    }

    @Test
    void testTimesZeroWithSettingsReplacementBDDMockitoShouldInOrder() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementBDDMockitoShouldTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(inOrder, <caret>times(0).description("description")).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(inOrder, Mockito.never().description("description")).toString();
                    }
                }""");
    }

    @Test
    void testTimesOneDeletionBDDMockitoShouldInOrder() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(inOrder, <caret>times(1)).toString();
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.BDDMockito;
                import org.mockito.InOrder;
                import org.mockito.Mockito;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                        Object mockObject = Mockito.mock(Object.class);
                        InOrder inOrder = Mockito.inOrder(mockObject);
                        BDDMockito.then(mockObject).should(inOrder).toString();
                    }
                }""");
    }

    //MockedStatic.verify()

    @Test
    void testTimesZeroReplacementMockedStaticVerify() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.List;

                public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {
                            listMockedStatic.verify(List::of, <caret>times(0));
                        }
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.List;

                public class TimesZeroWithoutSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {
                            listMockedStatic.verify(List::of, Mockito.never());
                        }
                    }
                }""");
    }

    @Test
    void testTimesZeroWithSettingsReplacementMockedStaticVerify() {
        doQuickFixTest("Replace with Mockito.never()", "TimesZeroWithSettingsReplacementBDDMockitoShouldTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.List;

                public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {
                            listMockedStatic.verify(List::of, <caret>times(0).description("description"));
                        }
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.List;

                public class TimesZeroWithSettingsReplacementBDDMockitoShouldTest {

                    public void testMethod() {
                       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {
                            listMockedStatic.verify(List::of, Mockito.never().description("description"));
                        }
                    }
                }""");
    }

    @Test
    void testTimesOneDeletionMockedStaticVerify() {
        doQuickFixTest("Delete call to Mockito.times(1)", "TimesOneDeletionTest.java",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.List;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {
                            listMockedStatic.verify(List::of, <caret>times(1));
                        }
                    }
                }""",
            """
                import static org.mockito.Mockito.times;

                import org.mockito.MockedStatic;
                import org.mockito.Mockito;
                import java.util.List;

                public class TimesOneDeletionTest {

                    public void testMethod() {
                       try (MockedStatic<List> listMockedStatic = Mockito.mockStatic(List.class)) {
                            listMockedStatic.verify(List::of);
                        }
                    }
                }""");
    }
}
