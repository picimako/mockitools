//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;

import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;

/**
 * Functional test for {@link UsageOfDeprecatedPluginClassesInspection}.
 */
public class UsageOfDeprecatedPluginClassesInspectionInstantiatorProviderTest extends MockitoolsInspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfDeprecatedPluginClassesInspection();
    }

    public void testReplacesInstantiatorProviderImport() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            "import org.mockito.plugins.InstantiatorProvider;\n" +
                "\n" +
                "public class ReplaceInstantiatorProviderTest {\n" +
                "    public void testMethod() {\n" +
                "        Instanti<caret>atorProvider provider;\n" +
                "    }\n" +
                "}",
            "import org.mockito.plugins.InstantiatorProvider;\n" +
                "import org.mockito.plugins.InstantiatorProvider2;\n" +
                "\n" +
                "public class ReplaceInstantiatorProviderTest {\n" +
                "    public void testMethod() {\n" +
                "        InstantiatorProvider2 provider;\n" +
                "    }\n" +
                "}");
    }

    public void testReplacesInstantiatorProviderImportInterfaceDeclaration() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            "import org.mockito.plugins.InstantiatorProvider;\n" +
                "\n" +
                "public interface CustomInstantiatorProvider extends Instanti<caret>atorProvider {\n" +
                "}",
            "import org.mockito.plugins.InstantiatorProvider;\n" +
                "import org.mockito.plugins.InstantiatorProvider2;\n" +
                "\n" +
                "public interface CustomInstantiatorProvider extends InstantiatorProvider2 {\n" +
                "}");
    }

    public void testReplacesInstantiatorProviderFullyQualifiedNameWithoutNameCollision() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            "public class ReplaceInstantiatorProviderTest {\n" +
                "    public void testMethod() {\n" +
                "        org.mockito.plugins.Instanti<caret>atorProvider provider;\n" +
                "    }\n" +
                "}",
            "import org.mockito.plugins.InstantiatorProvider2;\n" +
                "\n" +
                "public class ReplaceInstantiatorProviderTest {\n" +
                "    public void testMethod() {\n" +
                "        InstantiatorProvider2 provider;\n" +
                "    }\n" +
                "}");
    }

    public void testReplacesAllNonFqnOccurrencesOfInstantiatorProvider() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            "import org.mockito.plugins.InstantiatorProvider;\n" +
                "\n" +
                "public class ReplaceInstantiatorProviderTest {\n" +
                "    private Instanti<caret>atorProvider engineField;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        InstantiatorProvider engineVar;\n" +
                "    }\n" +
                "\n" +
                "    private void method(org.mockito.plugins.InstantiatorProvider eng) {\n" +
                "    }\n" +
                "}",
            "import org.mockito.plugins.InstantiatorProvider;\n" +
                "import org.mockito.plugins.InstantiatorProvider2;\n" +
                "\n" +
                "public class ReplaceInstantiatorProviderTest {\n" +
                "    private InstantiatorProvider2 engineField;\n" +
                "\n" +
                "    public void testMethod() {\n" +
                "        InstantiatorProvider engineVar;\n" +
                "    }\n" +
                "\n" +
                "    private void method(org.mockito.plugins.InstantiatorProvider eng) {\n" +
                "    }\n" +
                "}");
    }
}
