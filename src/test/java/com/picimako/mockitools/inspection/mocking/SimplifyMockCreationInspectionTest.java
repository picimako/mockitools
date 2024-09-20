//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.inspection.mocking;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.picimako.mockitools.inspection.MockitoolsInspectionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link SimplifyMockCreationInspection}.
 */
class SimplifyMockCreationInspectionTest extends MockitoolsInspectionTestBase {

    @Override
    protected InspectionProfileEntry getInspection() {
        return new SimplifyMockCreationInspection();
    }

    //Highlighting

    @Test
    void testSimplifyMockCreation() {
        doJavaTest(true);
    }

    //Quick fix - spiedInstance

    @Test
    void shouldReplaceInlineCreatedSpiedInstance() {
        doQuickFixTest("Simplify mock creation", "SimplifyMockCreation.java",
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testSpiedInstance() {
                        var spy = mock(MockO<caret>bject.class, withSettings().spiedInstance(new MockObject()));
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testSpiedInstance() {
                        var spy = Mockito.spy(new MockObject());
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }

    @Test
    void shouldReplaceSpiedInstanceAsVariable() {
        doQuickFixTest("Simplify mock creation", "SimplifyMockCreation.java",
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testSpiedInstance() {
                        var spiedInstance = new MockObject();
                        var spy = mock(MockObj<caret>ect.class, withSettings().spiedInstance(spiedInstance));
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testSpiedInstance() {
                        var spiedInstance = new MockObject();
                        var spy = Mockito.spy(spiedInstance);
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }

    //Quick fix - name

    @Test
    void shouldReplaceNameAsLiteral() {
        doQuickFixTest("Simplify mock creation", "SimplifyMockCreation.java",
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testName() {
                        var mock = mock(MockObject.class, w<caret>ithSettings().name("some name"));
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testName() {
                        var mock = Mockito.mock(MockObject.class, "some name");
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }

    @Test
    void shouldReplaceNameAsVariable() {
        doQuickFixTest("Simplify mock creation", "SimplifyMockCreation.java",
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testName() {
                        var name = "some name";
                        var mock = mock(MockObject.class, w<caret>ithSettings().name(name));
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                class SimplifyMockCreation {
                    void testName() {
                        var name = "some name";
                        var mock = Mockito.mock(MockObject.class, name);
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }

    //Quick fix - defaultAnswer

    @Test
    void shouldReplaceDefaultAnswerAsAnswers() {
        doQuickFixTest("Simplify mock creation", "SimplifyMockCreation.java",
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                import org.mockito.Answers;
                
                class SimplifyMockCreation {
                    void testDefaultAnswer() {
                        var mock = mock(MockObject.class, w<caret>ithSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS));
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                import org.mockito.Answers;
                import org.mockito.Mockito;
                
                class SimplifyMockCreation {
                    void testDefaultAnswer() {
                        var mock = Mockito.mock(MockObject.class, Answers.RETURNS_SMART_NULLS);
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }

    @Test
    void shouldReplaceInlineCreatedDefaultAnswer() {
        doQuickFixTest("Simplify mock creation", "SimplifyMockCreation.java",
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                import org.mockito.internal.stubbing.defaultanswers.GloballyConfiguredAnswer;
                
                class SimplifyMockCreation {
                    void testDefaultAnswer() {
                        var mock = mock(MockObject.class, w<caret>ithSettings().defaultAnswer(new GloballyConfiguredAnswer()));
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                import org.mockito.Mockito;
                import org.mockito.internal.stubbing.defaultanswers.GloballyConfiguredAnswer;
                
                class SimplifyMockCreation {
                    void testDefaultAnswer() {
                        var mock = Mockito.mock(MockObject.class, new GloballyConfiguredAnswer());
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }

    @Test
    void shouldReplaceDefaultAnswerAsVariable() {
        doQuickFixTest("Simplify mock creation", "SimplifyMockCreation.java",
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                import org.mockito.internal.stubbing.defaultanswers.ReturnsMocks;
                
                class SimplifyMockCreation {
                    void testDefaultAnswer() {
                        var answer = new ReturnsMocks();
                        var mock = mock(MockObject.class, w<caret>ithSettings().defaultAnswer(answer));
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.withSettings;
                
                import org.mockito.Mockito;
                import org.mockito.internal.stubbing.defaultanswers.ReturnsMocks;
                
                class SimplifyMockCreation {
                    void testDefaultAnswer() {
                        var answer = new ReturnsMocks();
                        var mock = Mockito.mock(MockObject.class, answer);
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }
}