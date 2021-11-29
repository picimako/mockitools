//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito;

import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

/**
 * Base test class for non-inspection unit tests.
 * <p>
 * Configures the Java 11 mock JDK and loads the Mockito binary.
 */
public abstract class MockitoolsBaseTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return getJdkHomeBasedDescriptor();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadMockito(myFixture.getProjectDisposable(), getModule());
    }
    
    public static LightProjectDescriptor getJdkHomeBasedDescriptor() {
        if (System.getProperty("idea.home.path") != null) {
            return JAVA_11;
        }
        return new ProjectDescriptor(LanguageLevel.JDK_11) {
            @Override
            public Sdk getSdk() {
                return JavaSdk.getInstance().createJdk("Real JDK", System.getenv("JAVA_HOME"), false);
            }
        };
    }
}
