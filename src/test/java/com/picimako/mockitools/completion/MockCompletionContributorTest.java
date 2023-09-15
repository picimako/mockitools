//Copyright 2023 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.completion;

import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link MockCompletionContributor};
 */
class MockCompletionContributorTest extends MockitoolsCodeCompletionTestBase {

    private static final String COMPLETION_TEST_CLASS = """
        %s
        import org.mockito.Mock;
        import org.mockito.Mockito;
        import org.mockito.Spy;
        import org.mockito.InjectMocks;
                        
        class CompletionTest {
            @Mock
            SomeMockType someMock;
            @Spy
            SomeMockType someSpy;
            @InjectMocks
            SomeMockType someInjectMocks;
                        
            Object notAMock;
            
            void testMethod() {
                SomeMockType mock = Mockito.mock(SomeMockType.class);
                SomeMockType spy = Mockito.spy(SomeMockType.class);
                %s
            }
            
            void testSomethingElse() {
                SomeMockType mockElse = Mockito.mock(SomeMockType.class);
                SomeMockType spyElse = Mockito.spy(SomeMockType.class);
            }
            
            private static final class SomeMockType {
                //...
            }
        }
        """;

    @Test
    void shouldNotCompleteNonMockParameterHolder() {
        doTestCodeCompletionContains("CompletionTest.java",
            """
                import org.mockito.Mock;
                import org.mockito.Mockito;
                                
                class CompletionTest {
                    @Mock
                    SomeMockType someMock;
                    Object notAMock;
                    
                    void testMethod() {
                        Object nonMock = new Object();
                        Mockito.reset(<caret>);
                    }
                    
                    void testSomethingElse() {
                        SomeMockType mock = Mockito.mock(SomeMockType.class);
                        SomeMockType spy = Mockito.spy(SomeMockType.class);
                    }
                    
                    private static final class SomeMockType {
                        //...
                    }
                }
                """,
            "someMock");
    }

    @Test
    void shouldCompleteFirstArgumentInEmptyArgumentListT() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("", "Mockito.reset(<caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteFirstArgumentInNonEmptyArgumentListT() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("", "Mockito.reset(someInjectMocks, <caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteFirstArgumentInEmptyArgumentListObject() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("", "Mockito.verifyNoMoreInteractions(<caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteFirstArgumentInNonEmptyArgumentListObject() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("", "Mockito.verifyNoMoreInteractions(someInjectMocks, <caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteLastArgument() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("", "Mockito.verifyNoMoreInteractions(someMock, someSpy, <caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteFirstArgumentInEmptyArgumentListObjectStaticImported() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("import static org.mockito.Mockito.verifyNoMoreInteractions;", "verifyNoMoreInteractions(<caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteFirstArgumentInNonEmptyArgumentListObjectStaticImported() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("import static org.mockito.Mockito.verifyNoMoreInteractions;", "verifyNoMoreInteractions(someInjectMocks, <caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteLastArgumentStaticImported() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("import static org.mockito.Mockito.verifyNoMoreInteractions;", "verifyNoMoreInteractions(someMock, someSpy, <caret>);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }

    @Test
    void shouldCompleteMiddleArgument() {
        doTestCodeCompletionContains("CompletionTest.java",
            COMPLETION_TEST_CLASS.formatted("", "Mockito.clearInvocations(someMock, <caret>someSpy);"),
            "mock", "spy", "someInjectMocks", "someMock", "someSpy");
    }
}
