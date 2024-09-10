//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.testFramework.TestDataPath;
import com.picimako.mockitools.MockitoolsTestBase;
import com.picimako.mockitools.ThirdPartyLibrary;

/**
 * Base test class for Mockitools inspection unit testing.
 * <p>
 * Loads the Java 11 mock JDK and the Mockito binary for testing.
 */
@TestDataPath("$CONTENT_ROOT/testData/inspection")
public abstract class MockitoolsInspectionTestBase extends MockitoolsTestBase {

    public MockitoolsInspectionTestBase() {
        super();
    }

    public MockitoolsInspectionTestBase(ThirdPartyLibrary... libraries) {
        super(libraries);
    }

    /**
     * Override this to configure the inspection to be tested.
     */
    protected abstract InspectionProfileEntry getInspection();

    protected void doJavaTest() {
        doJavaTest(getInspection(), false);
    }

    /**
     * Tests highlighting for the pre-configured inspection against the java file matching the test method's name
     * without the 'test' prefix.
     */
    protected void doJavaTest(boolean checkInfos) {
        doJavaTest(getInspection(), checkInfos);
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

    /**
     * Tests highlighting for the argument inspection against the java file matching the test method's name
     * without the 'test' prefix.
     */
    protected void doJavaTest(InspectionProfileEntry inspection, boolean checkInfos) {
        getFixture().configureByFile(getTestName(false) + ".java");
        getFixture().enableInspections(inspection);
        getFixture().testHighlighting(true, checkInfos, true);
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
        public MockitoV3() {
            super(ThirdPartyLibrary.MOCKITO_V3);
        }

        public MockitoV3(ThirdPartyLibrary... libraries) {
            super(libraries);
        }
    }

    /**
     * Base class for testing Mockito 4 specific inspections.
     */
    public static abstract class MockitoV4 extends MockitoolsInspectionTestBase {
        public MockitoV4() {
            super(ThirdPartyLibrary.MOCKITO_V4);
        }
    }
}
