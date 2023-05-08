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
            "import org.mockito.configuration.AnnotationEngine;\n" +
                "\n" +
                "public class ReplaceAnnotationEngineTest {\n" +
                "    public void testMethod() {\n" +
                "        Annotation<caret>Engine engine;\n" +
                "    }\n" +
                "}",
            "import org.mockito.plugins.AnnotationEngine;\n" +
                "\n" +
                "public class ReplaceAnnotationEngineTest {\n" +
                "    public void testMethod() {\n" +
                "        AnnotationEngine engine;\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testReplacesAnnotationEngineImportInterfaceDeclaration() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            "import org.mockito.configuration.AnnotationEngine\n" +
                "\n" +
                "public interface CustomAnnotationEngine extends Annotatio<caret>nEngine {\n" +
                "}",
            "import org.mockito.plugins.AnnotationEngine\n" +
                "\n" +
                "public interface CustomAnnotationEngine extends AnnotationEngine {\n" +
                "}");
    }

    @Test
    void testReplacesAnnotationEngineFullyQualifiedNameWithoutNameCollision() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            "public class ReplaceAnnotationEngineTest {\n" +
                "    public void testMethod() {\n" +
                "        org.mockito.configuration.Annotation<caret>Engine engine;\n" +
                "    }\n" +
                "}",
            "import org.mockito.plugins.AnnotationEngine;\n" +
                "\n" +
                "public class ReplaceAnnotationEngineTest {\n" +
                "    public void testMethod() {\n" +
                "        AnnotationEngine engine;\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testReplacesAnnotationEngineFullyQualifiedNameWithNameCollision() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            "import org.mockito.configuration.AnnotationEngine;\n" +
                "\n" +
                "public class ReplaceAnnotationEngineTest {\n" +
                "    public void testMethod() {\n" +
                "        org.mockito.configuration.Annotation<caret>Engine engine;\n" +
                "        AnnotationEngine eng;\n" +
                "    }\n" +
                "}",
            "import org.mockito.configuration.AnnotationEngine;\n" +
                "\n" +
                "public class ReplaceAnnotationEngineTest {\n" +
                "    public void testMethod() {\n" +
                "        org.mockito.plugins.AnnotationEngine engine;\n" +
                "        AnnotationEngine eng;\n" +
                "    }\n" +
                "}");
    }

    @Test
    void testReplacesAllNonFqnOccurrencesOfAnnotationEngine() {
        doQuickFixTest("Replace with org.mockito.plugins.AnnotationEngine", "ReplaceAnnotationEngineTest.java",
            "import org.mockito.configuration.AnnotationEngine;\n" +
                "\n" +
                "public class ReplaceAnnotationEngineTest {\n" +
                "    private Annotatio<caret>nEngine engineField;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        AnnotationEngine engineVar;\n" +
                "    }\n" +
                "\n" +
                "    private void method(org.mockito.configuration.AnnotationEngine eng) {\n" +
                "    }\n" +
                "}",
            "import org.mockito.plugins.AnnotationEngine;\n" +
                "\n" +
                "public class ReplaceAnnotationEngineTest {\n" +
                "    private AnnotationEngine engineField;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        AnnotationEngine engineVar;\n" +
                "    }\n" +
                "\n" +
                "    private void method(org.mockito.configuration.AnnotationEngine eng) {\n" +
                "    }\n" +
                "}");
    }
}
