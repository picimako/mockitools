//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link UsageOfDeprecatedPluginClassesInspection}.
 */
class UsageOfDeprecatedPluginClassesInspectionAnnotationEngineTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfDeprecatedPluginClassesInspection();
    }

    @Test
    void testReplacesAnnotationEngineImport() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            """
                import org.mockito.configuration.AnnotationEngine;

                public class ReplaceAnnotationEngineTest {
                    public void testMethod() {
                        Annotation<caret>Engine engine;
                    }
                }""",
            """
                import org.mockito.plugins.AnnotationEngine;

                public class ReplaceAnnotationEngineTest {
                    public void testMethod() {
                        AnnotationEngine engine;
                    }
                }""");
    }

    @Test
    void testReplacesAnnotationEngineImportInterfaceDeclaration() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            """
                import org.mockito.configuration.AnnotationEngine

                public interface CustomAnnotationEngine extends Annotatio<caret>nEngine {
                }""",
            """
                import org.mockito.plugins.AnnotationEngine

                public interface CustomAnnotationEngine extends AnnotationEngine {
                }""");
    }

    @Test
    void testReplacesAnnotationEngineFullyQualifiedNameWithoutNameCollision() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            """
                public class ReplaceAnnotationEngineTest {
                    public void testMethod() {
                        org.mockito.configuration.Annotation<caret>Engine engine;
                    }
                }""",
            """
                import org.mockito.plugins.AnnotationEngine;

                public class ReplaceAnnotationEngineTest {
                    public void testMethod() {
                        AnnotationEngine engine;
                    }
                }""");
    }

    @Test
    void testReplacesAnnotationEngineFullyQualifiedNameWithNameCollision() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            """
                import org.mockito.configuration.AnnotationEngine;

                public class ReplaceAnnotationEngineTest {
                    public void testMethod() {
                        org.mockito.configuration.Annotation<caret>Engine engine;
                        AnnotationEngine eng;
                    }
                }""",
            """
                import org.mockito.configuration.AnnotationEngine;

                public class ReplaceAnnotationEngineTest {
                    public void testMethod() {
                        org.mockito.plugins.AnnotationEngine engine;
                        AnnotationEngine eng;
                    }
                }""");
    }

    @Test
    void testReplacesAllNonFqnOccurrencesOfAnnotationEngine() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            """
                import org.mockito.configuration.AnnotationEngine;

                public class ReplaceAnnotationEngineTest {
                    private Annotatio<caret>nEngine engineField;

                    public void testMethod() {
                        AnnotationEngine engineVar;
                    }

                    private void method(org.mockito.configuration.AnnotationEngine eng) {
                    }
                }""",
            """
                import org.mockito.plugins.AnnotationEngine;

                public class ReplaceAnnotationEngineTest {
                    private AnnotationEngine engineField;

                    public void testMethod() {
                        AnnotationEngine engineVar;
                    }

                    private void method(org.mockito.configuration.AnnotationEngine eng) {
                    }
                }""");
    }
}
