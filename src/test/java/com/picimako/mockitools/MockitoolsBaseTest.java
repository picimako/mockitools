//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito;

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
        return JAVA_11;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadMockito(myFixture.getProjectDisposable(), getModule());
    }
}
