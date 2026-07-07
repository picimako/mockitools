//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * Base test class for Mockitools tests.
 */
public abstract class MockitoolsTestBase extends LightJavaCodeInsightFixtureTestCase5 {

    protected MockitoolsTestBase() {
        super(projectDescriptorWithRealJdk().withRepositoryLibrary(ThirdPartyLibrary.MOCKITO_V5.getMavenCoordinate()));
    }

    protected MockitoolsTestBase(ThirdPartyLibrary... libraries) {
        super(createDescriptor(libraries));
    }

    /**
     * Returns a descriptor with a real JDK defined by the {@code JAVA_HOME} environment variable.
     */
    private static @NonNull DefaultLightProjectDescriptor projectDescriptorWithRealJdk() {
        return new DefaultLightProjectDescriptor(() -> JavaSdk.getInstance().createJdk("Real JDK", System.getenv("JAVA_HOME"), false));
    }

    /**
     * Returns a project descriptor that has the provided third-party {@code libraries} configured
     * as dependencies for the test project.
     */
    private static @NonNull DefaultLightProjectDescriptor createDescriptor(ThirdPartyLibrary... libraries) {
        var descriptor = projectDescriptorWithRealJdk();
        for (var library : libraries) {
            descriptor.withRepositoryLibrary(library.getMavenCoordinate(), false);
        }
        return descriptor;
    }

    /**
     * Analyzes the class hierarchy of the current test class and finds the {@link TestDataPath} annotation
     * on one of the classes in it. It stops at {@link LightJavaCodeInsightFixtureTestCase5} because it is certain
     * that it has the annotation specified. Then, it returns the value of the annotation.
     * <p>
     * By using the {@link TestDataPath} annotation on test classes, overriding this method in every test class
     * is no longer needed. It provides a more declarative approach.
     */
    @Nullable
    @Override
    protected String getTestDataPath() {
        var testDataPathOnCurrentClass = testDataPathOn(getClass());

        //If the current test class is annotated as TestDataPath, return its value
        if (testDataPathOnCurrentClass != null) return replaceContentRoot(testDataPathOnCurrentClass);

        //If the current test class is not annotated as TestDataPath, find the first class in its class hierarchy
        // that is annotated. It stops looking when reaching LightJavaCodeInsightFixtureTestCase.
        var clazz = getClass().getSuperclass();
        while (!clazz.equals(LightJavaCodeInsightFixtureTestCase5.class) && testDataPathOn(clazz) == null) {
            clazz = clazz.getSuperclass();
        }

        return replaceContentRoot(testDataPathOn(clazz));
    }

    /**
     * Returns a normalized version of the annotation's value by replacing the beginning of it with a path
     * referencing the test data directory in this project.
     * <p>
     * By starting the {@link TestDataPath} annotation value with {@code $CONTENT_ROOT}, it enables path reference
     * fragments injection into that path by the IDE, but has to be replaced with the actual relative path, so that it is
     * resolved by the parent test case.
     */
    private static String replaceContentRoot(TestDataPath testDataPath) {
        return testDataPath.value().replace("$CONTENT_ROOT/testData/", "src/test/testData/");
    }

    private static TestDataPath testDataPathOn(Class<?> clazz) {
        return clazz.getAnnotation(TestDataPath.class);
    }

    //Helpers

    protected Project getProject() {
        return getFixture().getProject();
    }

    protected Editor getEditor() {
        return getFixture().getEditor();
    }
}
