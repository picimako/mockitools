//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.MavenDependencyUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Base test class for non-inspection unit tests.
 * <p>
 * Configures the Java 11 JDK and loads the Mockito binary.
 */
public abstract class MockitoolsTestBase extends LightJavaCodeInsightFixtureTestCase {

    public static final String MOCKITO_3 = "org.mockito:mockito-core:3.11.2";
    public static final String MOCKITO_4 = "org.mockito:mockito-core:4.3.1";
    public static final String JUNIT_4 = "junit:junit:4.13.2";
    public static final String[] MOCKITO_3_LIB = new String[]{MOCKITO_3};
    public static final String[] MOCKITO_4_LIB = new String[]{MOCKITO_4};

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return getRealJdkHomeOrCommunityMockJdk(libsToLoad());
    }

    protected String[] libsToLoad() {
        return MOCKITO_3_LIB;
    }

    /**
     * Returns a descriptor with Java 11 Mock JDK from intellij-community if the {@code idea.home.path} system property is defined,
     * otherwise uses the JAVA_HOME environment variable to identify the JDK to use.
     * <p>
     * JAVA_HOME based JDK is used mainly in CI/CD environment.
     */
    public static LightProjectDescriptor getRealJdkHomeOrCommunityMockJdk(String[] libsToLoad) {
        return new LightJavaCodeInsightFixtureTestCase.ProjectDescriptor(LanguageLevel.JDK_11) {
            @Override
            public Sdk getSdk() {
                return System.getProperty("idea.home.path") != null
                    ? super.getSdk()
                    : JavaSdk.getInstance().createJdk("Real JDK", System.getenv("JAVA_HOME"), false);
            }

            @Override
            public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
                super.configureModule(module, model, contentEntry);

//            contentEntry.clearSourceFolders();
//
//            String entryUrl = contentEntry.getUrl();
//            contentEntry.addSourceFolder(entryUrl + "/main/java", JavaSourceRootType.SOURCE);
//            contentEntry.addSourceFolder(entryUrl + "/main/resources", JavaResourceRootType.RESOURCE);
//            contentEntry.addSourceFolder(entryUrl + "/test/java", JavaSourceRootType.TEST_SOURCE);
//            contentEntry.addSourceFolder(entryUrl + "/test/resources", JavaResourceRootType.TEST_RESOURCE);
                
                for (String lib : libsToLoad) {
                    MavenDependencyUtil.addFromMaven(model, lib);
                }
            }
        };
    }
}
