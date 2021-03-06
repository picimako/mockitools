//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito3;

import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

/**
 * Base test class for non-inspection integration tests.
 * <p>
 * Configures the Java 11 JDK and loads the Mockito binary.
 */
public abstract class MockitoolsTestBase extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadLibs();
    }

    protected void loadLibs() {
        loadMockito3(myFixture.getProjectDisposable(), getModule());
    }

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return getRealJdkHomeOrCommunityMockJdk();
    }

    /**
     * Returns a descriptor with Java 11 Mock JDK from intellij-community if the {@code idea.home.path} system property is defined,
     * otherwise uses the JAVA_HOME environment variable to identify the JDK to use.
     * <p>
     * JAVA_HOME based JDK is used mainly in CI/CD environment.
     */
    public static LightProjectDescriptor getRealJdkHomeOrCommunityMockJdk() {
        return new ProjectDescriptor(LanguageLevel.JDK_11) {
            @Override
            public Sdk getSdk() {
                return JavaSdk.getInstance().createJdk("Real JDK", System.getenv("JAVA_HOME"), false);
            }
        };
    }
}
