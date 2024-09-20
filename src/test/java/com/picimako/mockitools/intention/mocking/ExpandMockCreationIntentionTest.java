//Copyright 2024 Tamás Balog. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.picimako.mockitools.intention.mocking;

import com.intellij.codeInsight.intention.IntentionAction;
import com.picimako.mockitools.intention.MockitoolsIntentionTestBase;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link ExpandMockCreationIntention}.
 */
class ExpandMockCreationIntentionTest extends MockitoolsIntentionTestBase {

    @Override
    protected IntentionAction getIntention() {
        return new ExpandMockCreationIntention();
    }

    //Availability

    @Test
    void shouldNotBeAvailableForNonJavaFile() {
        checkIntentionIsNotAvailable("NotAvailable.xml", "<tag<caret>></tag>");
    }

    @Test
    void shouldNotBeAvailableForMockitoSpyWithSpiedType() {
        checkIntentionIsNotAvailable("""
            import org.mockito.Mockito;
            
            class Available {
                void testMethod() {
                    var spy = Mockito.spy<caret>y(SpiedType.class);
                }
            
                private static final class SpiedType {
                }
            }
            """);
    }

    @Test
    void shouldNotBeAvailableForMockitoMockWithSettings() {
        checkIntentionIsNotAvailable("""
            import org.mockito.Mockito;
            
            class Available {
                void testMethod() {
                    var spy = Mockito.mo<caret>ck(MockObject.class, Mockito.withSettings().serializable());
                }
            
                private static final class MockObject {
                }
            }
            """);
    }

    @Test
    void shouldBeAvailableForMockitoSpyWithConcreteSpiedInstance() {
        checkIntentionIsAvailable("""
            import org.mockito.Mockito;
            
            class Available {
                void testMethod() {
                    var spy = Mockito.sp<caret>y(new SpiedType());
                }
            
                private static final class SpiedType {
                }
            }
            """);
    }

    @Test
    void shouldBeAvailableForMockitoMockWithName() {
        checkIntentionIsAvailable("""
            import org.mockito.Mockito;
            
            class Available {
                void testMethod() {
                    var mock = Mockito.mo<caret>ck(MockObject.class, "some name");
                }
            
                private static final class MockObject {
                }
            }
            """);
    }

    @Test
    void shouldBeAvailableForMockitoMockWithAnswer() {
        checkIntentionIsAvailable("""
            import org.mockito.Answers;
            import org.mockito.Mockito;
            
            class Available {
                void testMethod() {
                    var mock = Mockito.mo<caret>ck(MockObject.class, Answers.RETURNS_MOCKS);
                }
            
                private static final class MockObject {
                }
            }
            """);
    }

    //Conversion

    @Test
    void shouldExpandMockitoSpyWithConcreteSpiedInstance() {
        checkIntentionRun("""
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod() {
                        var spy = Mockito.sp<caret>y(new SpiedType());
                    }
                
                    private static final class SpiedType {
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod() {
                        var spy = Mockito.mock(SpiedType.class, Mockito.withSettings().spiedInstance(new SpiedType()));
                    }
                
                    private static final class SpiedType {
                    }
                }
                """);
    }

    @Test
    void shouldExpandMockitoSpyWithConcreteSpiedInstanceWithGenericType() {
        checkIntentionRun("""
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod() {
                        var spy = Mockito.sp<caret>y(new SpiedType<Object>());
                    }
                
                    private static final class SpiedType<T> {
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod() {
                        var spy = Mockito.mock(SpiedType.class, Mockito.withSettings().spiedInstance(new SpiedType<Object>()));
                    }
                
                    private static final class SpiedType<T> {
                    }
                }
                """);
    }

    @Test
    void shouldExpandMockitoMockWithName() {
        checkIntentionRun("""
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod() {
                        var mock = Mockito.mo<caret>ck(MockObject.class, "some name");
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod() {
                        var mock = Mockito.mock(MockObject.class, Mockito.withSettings().name("some name"));
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }

    @Test
    void shouldExpandMockitoMockWithAnswer() {
        checkIntentionRun("""
                import static org.mockito.Mockito.mock;
                
                import org.mockito.Answers;
                
                class ConversionTest {
                    void testMethod() {
                        var mock = mo<caret>ck(MockObject.class, Answers.RETURNS_MOCKS);
                    }
                
                    private static final class MockObject {
                    }
                }
                """,
            """
                import static org.mockito.Mockito.mock;
                
                import org.mockito.Answers;
                import org.mockito.Mockito;
                
                class ConversionTest {
                    void testMethod() {
                        var mock = Mockito.mock(MockObject.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_MOCKS));
                    }
                
                    private static final class MockObject {
                    }
                }
                """);
    }
}
