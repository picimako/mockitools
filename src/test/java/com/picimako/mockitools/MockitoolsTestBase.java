//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito3;

import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base test class for non-inspection integration tests.
 * <p>
 * Configures the Java 11 JDK and loads the Mockito binary.
 */
public abstract class MockitoolsTestBase extends LightJavaCodeInsightFixtureTestCase5 {

    protected MockitoolsTestBase() {
        //Returns a descriptor with a real JDK defined by the JAVA_HOME environment variable.
        super(new DefaultLightProjectDescriptor(() -> JavaSdk.getInstance().createJdk("Real JDK", System.getenv("JAVA_HOME"), false)));
    }

    @Nullable
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @BeforeEach //using BeforeEach because during BeforeAll, the fixture is not yet initialized to fetch it in 'loadLibs()'
    protected void setUp() {
        loadLibs();
    }

    protected void loadLibs() {
        loadMockito3(getFixture().getProjectDisposable(), getFixture().getModule());
    }
}
