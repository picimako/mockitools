//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito3;
import static com.picimako.mockitools.ThirdPartyLibraryLoader.loadMockito4Latest;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.MockitoolsTestBase;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base test class for Mockitools inspection unit testing.
 * <p>
 * Loads the Java 11 mock JDK and the Mockito binary for testing.
 */
public abstract class MockitoolsInspectionTestBase extends MockitoolsTestBase {

    @Nullable
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection";
    }

    /**
     * Override this to configure the inspection to be tested.
     */
    protected abstract InspectionProfileEntry getInspection();

    /**
     * Tests highlighting for the pre-configured inspection against the java file matching the test method's name
     * without the 'test' prefix.
     */
    protected void doJavaTest() {
        doJavaTest(getInspection());
    }

    /**
     * Tests highlighting for the argument inspection against the java file matching the test method's name
     * without the 'test' prefix.
     */
    protected void doJavaTest(InspectionProfileEntry inspection) {
        getFixture().configureByFile(getTestName(false) + ".java");
        getFixture().enableInspections(inspection);
        getFixture().testHighlighting(true, false, false);
    }

    protected void doJavaTest(String filename, String text) {
        getFixture().configureByText(filename, text);
        getFixture().enableInspections(getInspection());
        getFixture().testHighlighting(true, false, false);
    }

    /**
     * Tests highlighting and quick fix for the pre-configured inspection, applying the argument quick fix against the
     * java file matching the test method's name without the 'test' prefix (e.g. SomeTest.java) as the before state,
     * and e.g. SomeTest.after.java as the expected state after applying the quick fix.
     *
     * @param quickFixName the name/text of the quick fix
     */
    protected void doQuickFixTest(String quickFixName) {
        getFixture().configureByFile(getTestName(false) + ".java");
        launchQuickFix(quickFixName);
        getFixture().checkResultByFile(getTestName(false) + ".after.java");
    }

    /**
     * Tests highlighting and quick fix for the pre-configured inspection, applying the argument quick fix against the
     * provided beforeText, and the after state against the argument afterText.
     *
     * @param quickFixName the name/text of the quick fix
     * @param filename     the filename in which the before text will be configured
     * @param beforeText   the code before applying the quick fix
     * @param afterText    the code after applying the quick fix
     */
    protected void doQuickFixTest(String quickFixName, String filename, String beforeText, String afterText) {
        getFixture().configureByText(filename, beforeText);
        launchQuickFix(quickFixName);
        getFixture().checkResult(afterText);
    }
    
    private void launchQuickFix(String quickFixName) {
        getFixture().enableInspections(getInspection());
        getFixture().doHighlighting();
        getFixture().launchAction(getFixture().findSingleIntention(quickFixName));
    }

    /**
     * Base class for testing Mockito 3 specific inspections.
     */
    public static abstract class MockitoV3 extends MockitoolsInspectionTestBase {

        //This must be overridden, otherwise the library load doesn't seem to take effect
        @BeforeEach
        @Override
        protected void setUp() {
            loadMockito3(getFixture().getProjectDisposable(), getFixture().getModule());
        }
    }

    /**
     * Base class for testing Mockito 4 specific inspections.
     */
    public static abstract class MockitoV4 extends MockitoolsInspectionTestBase {

        @Override
        protected void loadLibs() {
            loadMockito4Latest(getFixture().getProjectDisposable(), getFixture().getModule());
        }
    }
}
