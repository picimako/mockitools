//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.migrationaids.v4;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Functional test for {@link UsageOfDeprecatedPluginClassesInspection}.
 */
class UsageOfDeprecatedPluginClassesInspectionInstantiatorProviderTest extends MockitoolsInspectionTestBase.MockitoV3 {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new UsageOfDeprecatedPluginClassesInspection();
    }

    @Test
    void testReplacesInstantiatorProviderImport() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            """
                import org.mockito.plugins.InstantiatorProvider;

                public class ReplaceInstantiatorProviderTest {
                    public void testMethod() {
                        Instanti<caret>atorProvider provider;
                    }
                }""",
            """
                import org.mockito.plugins.InstantiatorProvider;
                import org.mockito.plugins.InstantiatorProvider2;

                public class ReplaceInstantiatorProviderTest {
                    public void testMethod() {
                        InstantiatorProvider2 provider;
                    }
                }""");
    }

    @Test
    void testReplacesInstantiatorProviderImportInterfaceDeclaration() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            """
                import org.mockito.plugins.InstantiatorProvider;

                public interface CustomInstantiatorProvider extends Instanti<caret>atorProvider {
                }""",
            """
                import org.mockito.plugins.InstantiatorProvider;
                import org.mockito.plugins.InstantiatorProvider2;

                public interface CustomInstantiatorProvider extends InstantiatorProvider2 {
                }""");
    }

    @Test
    void testReplacesInstantiatorProviderFullyQualifiedNameWithoutNameCollision() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            """
                public class ReplaceInstantiatorProviderTest {
                    public void testMethod() {
                        org.mockito.plugins.Instanti<caret>atorProvider provider;
                    }
                }""",
            """
                import org.mockito.plugins.InstantiatorProvider2;

                public class ReplaceInstantiatorProviderTest {
                    public void testMethod() {
                        InstantiatorProvider2 provider;
                    }
                }""");
    }

    @Test
    void testReplacesAllNonFqnOccurrencesOfInstantiatorProvider() {
        doQuickFixTest("Replace with InstantiatorProvider2", "ReplaceInstantiatorProviderTest.java",
            """
                import org.mockito.plugins.InstantiatorProvider;

                public class ReplaceInstantiatorProviderTest {
                    private Instanti<caret>atorProvider engineField;

                    public void testMethod() {
                        InstantiatorProvider engineVar;
                    }

                    private void method(org.mockito.plugins.InstantiatorProvider eng) {
                    }
                }""",
            """
                import org.mockito.plugins.InstantiatorProvider;
                import org.mockito.plugins.InstantiatorProvider2;

                public class ReplaceInstantiatorProviderTest {
                    private InstantiatorProvider2 engineField;

                    public void testMethod() {
                        InstantiatorProvider engineVar;
                    }

                    private void method(org.mockito.plugins.InstantiatorProvider eng) {
                    }
                }""");
    }
}
