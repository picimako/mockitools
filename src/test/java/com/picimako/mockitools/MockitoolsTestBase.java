//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito5;

import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base test class for non-inspection integration tests.
 * <p>
 * Configures the Java JDK and loads the Mockito binary.
 */
public abstract class MockitoolsTestBase extends LightJavaCodeInsightFixtureTestCase5 {

    private static final String BASE_PATH = "src/test/testData/";

    protected MockitoolsTestBase() {
        //Returns a descriptor with a real JDK defined by the JAVA_HOME environment variable.
        super(new DefaultLightProjectDescriptor(() -> JavaSdk.getInstance().createJdk("Real JDK", System.getenv("JAVA_HOME"), false)));
    }

    @Nullable
    @Override
    protected String getTestDataPath() {
        var testDataPathOnCurrentClass = getClass().getAnnotation(TestDataPath.class);

        //If the current test class is annotated as TestDataPath, return its value
        if (testDataPathOnCurrentClass != null)
            return testDataPathOnCurrentClass.value().replace("$CONTENT_ROOT/testData/", BASE_PATH);

        //If the current test class is not annotated as TestDataPath, find the first class in its class hierarchy
        // that is annotated. It stops looking when reaching LightJavaCodeInsightFixtureTestCase.
        var clazz = getClass().getSuperclass();
        while (!clazz.equals(LightJavaCodeInsightFixtureTestCase5.class) && clazz.getAnnotation(TestDataPath.class) == null) {
            clazz = clazz.getSuperclass();
        }

        return clazz.getAnnotation(TestDataPath.class).value().replace("$CONTENT_ROOT/testData/", BASE_PATH);
    }

    @BeforeEach //using BeforeEach because during BeforeAll, the fixture is not yet initialized to fetch it in 'loadLibs()'
    protected void setUp() {
        loadLibs();
    }

    protected void loadLibs() {
        loadMockito5(getFixture().getProjectDisposable(), getFixture().getModule());
    }
}
