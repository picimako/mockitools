//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.captor;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link CaptorFieldOfTypeArgumentCaptorInspection}.
 */
class CaptorFieldOfTypeArgumentCaptorInspectionTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new CaptorFieldOfTypeArgumentCaptorInspection();
    }

    @Test
    void testCaptorFieldOfType() {
        doJavaTest();
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorArrayTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorArrayTest.java",
            """
                import org.mockito.Captor;

                public class CaptorFieldConvertToArgumentCaptorArrayTest {

                    @Captor
                    public String[] <caret>captor;
                }
                """,
            """
                import org.mockito.ArgumentCaptor;
                import org.mockito.Captor;

                public class CaptorFieldConvertToArgumentCaptorArrayTest {

                    @Captor
                    public ArgumentCaptor<String[]> captor;
                }
                """);
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorPrimitiveTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorPrimitiveTest.java",
            """
                import org.mockito.Captor;

                public class CaptorFieldConvertToArgumentCaptorPrimitiveTest {

                    @Captor
                    public char <caret>captor;
                }
                """,
            """
                import org.mockito.ArgumentCaptor;
                import org.mockito.Captor;

                public class CaptorFieldConvertToArgumentCaptorPrimitiveTest {

                    @Captor
                    public ArgumentCaptor<Character> captor;
                }
                """);
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorGenericsTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorGenericsTest.java",
            """
                import org.mockito.Captor;
                import java.util.List;

                public class CaptorFieldConvertToArgumentCaptorGenericsTest {

                    @Captor
                    public List<List<String>> <caret>captor;
                }
                """,
            """
                import org.mockito.ArgumentCaptor;
                import org.mockito.Captor;
                import java.util.List;

                public class CaptorFieldConvertToArgumentCaptorGenericsTest {

                    @Captor
                    public ArgumentCaptor<List<List<String>>> captor;
                }
                """);
    }

    @Test
    void testCaptorFieldConvertToArgumentCaptorWildcardGenericsTest() {
        doQuickFixTest("Convert field type to ArgumentCaptor<>", "CaptorFieldConvertToArgumentCaptorWildcardGenericsTest.java",
            """
                import org.mockito.Captor;
                import java.util.List;
                import java.util.Map;

                public class CaptorFieldConvertToArgumentCaptorWildcardGenericsTest {

                    @Captor
                    public Map<List<String>, List<?>> <caret>captor;
                }
                """,
            """
                import org.mockito.ArgumentCaptor;
                import org.mockito.Captor;
                import java.util.List;
                import java.util.Map;

                public class CaptorFieldConvertToArgumentCaptorWildcardGenericsTest {

                    @Captor
                    public ArgumentCaptor<Map<List<String>, List<?>>> captor;
                }
                """);
    }
}
