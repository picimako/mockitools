//Copyright 2021 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import com.picimako.mockitools.MockitoolsTestBase;

/**
 * Base test class for Mockitools inspection unit testing.
 * <p>
 * Loads the Java 11 mock JDK and the Mockito binary for testing.
 */
public abstract class MockitoolsInspectionTestBase extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/inspection";
    }

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return MockitoolsTestBase.getRealJdkHomeOrCommunityMockJdk();
    }

    /**
     * To load libraries other than Mockito, so that the setUp method doesn't have to be overridden every time.
     */
    protected void loadLibs() {
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
        myFixture.configureByFile(getTestName(false) + ".java");
        myFixture.enableInspections(inspection);
        myFixture.testHighlighting(true, false, false);
    }

    protected void doJavaTest(String filename, String text) {
        myFixture.configureByText(filename, text);
        myFixture.enableInspections(getInspection());
        myFixture.testHighlighting(true, false, false);
    }

    /**
     * Tests highlighting and quick fix for the pre-configured inspection, applying the argument quick fix against the
     * java file matching the test method's name without the 'test' prefix (e.g. SomeTest.java) as the before state,
     * and e.g. SomeTest.after.java as the expected state after applying the quick fix.
     *
     * @param quickFixName the name/text of the quick fix
     */
    protected void doQuickFixTest(String quickFixName) {
        myFixture.configureByFile(getTestName(false) + ".java");
        launchQuickFix(quickFixName);
        myFixture.checkResultByFile(getTestName(false) + ".after.java");
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
        myFixture.configureByText(filename, beforeText);
        launchQuickFix(quickFixName);
        myFixture.checkResult(afterText);
    }
    
    private void launchQuickFix(String quickFixName) {
        myFixture.enableInspections(getInspection());
        myFixture.doHighlighting();
        myFixture.launchAction(myFixture.findSingleIntention(quickFixName));
    }
}
