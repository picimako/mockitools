//Copyright 2026 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.testFramework.TestDataPath;
import com.picimako.mockitools.MockitoolsTestBase;
import com.picimako.mockitools.ThirdPartyLibrary;

/**
 * Base test class for inspection testing.
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
     * Returns the inspection instance that is the subject of this test.
     */
    protected abstract InspectionProfileEntry getInspection();

    //Highlighting

    /**
     * Configures a Java test file
     * <ul>
     *     <li>with the name of the current test method without the {@code test} prefix</li>
     *     <li>and the contents of the test data file with that name</li>
     * </ul>
     * and checks highlighting for the subject inspection without checking info-level highlights.
     */
    protected void doJavaTest() {
        doJavaTest(getInspection(), false);
    }

    /**
     * Configures a Java test file
     * <ul>
     *      <li>with the name of the current test method without the {@code test} prefix</li>
     *      <li>and the contents of the test data file with that name</li>
     * </ul>
     * and checks highlighting for the subject inspection.
     * Info-level highlights are included based on the {@code checkInfos} flag.
     */
    protected void doJavaTest(boolean checkInfos) {
        doJavaTest(getInspection(), checkInfos);
    }

    /**
     * Configures a Java test file
     * <ul>
     *      <li>with the name of the current test method without the {@code test} prefix</li>
     *      <li>and the contents of the test data file with that name</li>
     * </ul>
     * and checks highlighting for the provided inspection without checking info-level highlights.
     */
    protected void doJavaTest(InspectionProfileEntry inspection) {
        doJavaTest(inspection, false);
    }

    /**
     * Configures a Java test file
     * <ul>
     *      <li>with the name of the current test method without the {@code test} prefix</li>
     *      <li>and the contents of the test data file with that name</li>
     * </ul>
     * and checks highlighting for the provided inspection.
     * Info-level highlights are included based on the {@code checkInfos} flag.
     */
    protected void doJavaTest(InspectionProfileEntry inspection, boolean checkInfos) {
        getFixture().configureByFile(getTestName(false) + ".java");
        getFixture().enableInspections(inspection);
        getFixture().testHighlighting(true, checkInfos, true);
    }

    /**
     * Configures a Java test file with the given {@code filename} and {@code text}
     * and checks highlighting for the subject inspection.
     */
    protected void doJavaTest(String filename, String text) {
        getFixture().configureByText(filename, text);
        getFixture().enableInspections(getInspection());
        getFixture().testHighlighting(true, false, false);
    }

    //Quick fixes

    /**
     * Configures a Java test file (the before state)
     * <ul>
     *      <li>with the name of the current test method without the {@code test} prefix</li>
     *      <li>and the contents of the test data file with that name</li>
     * </ul>
     * launches the quick of the provided name and checks if the file text has changed to the after state
     * provided similarly via a corresponding {@code [testname].after.java} file.
     */
    protected void doQuickFixTest(String quickFixName) {
        getFixture().configureByFile(getTestName(false) + ".java");
        launchQuickFix(quickFixName);
        getFixture().checkResultByFile(getTestName(false) + ".after.java");
    }

    /**
     * Configures a test file with the provided {@code filename} and {@code beforeText},
     * launches the quick of the provided {@code quickFixName} and checks if the file text has changed to the after state
     * provided in {@code afterText}.
     */
    protected void doQuickFixTest(String quickFixName, String filename, String beforeText, String afterText) {
        getFixture().configureByText(filename, beforeText);
        launchQuickFix(quickFixName);
        getFixture().checkResult(afterText);
    }

    /**
     * Configures a test file with a fix name and {@code beforeText},
     * launches the quick of the provided {@code quickFixName} and checks if the file text has changed to the after state
     * provided in {@code afterText}.
     */
    protected void doQuickFixTest(String quickFixName, String beforeText, String afterText) {
        getFixture().configureByText("QuickFix.java", beforeText);
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
}
