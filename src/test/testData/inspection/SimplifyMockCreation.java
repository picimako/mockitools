import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import org.mockito.Answers;
import org.mockito.internal.stubbing.defaultanswers.ReturnsMocks;
import org.mockito.internal.stubbing.defaultanswers.GloballyConfiguredAnswer;

class SimplifyMockCreation {

    void testSpiedInstance() {
        MockObject spy = <weak_warning descr="Mock creation can be simplified with calling 'spy(<spiedInstance>)'.">mock(MockObject.class, withSettings().spiedInstance(new MockObject()))</weak_warning>;
        MockObject spiedInstance = new MockObject();
        MockObject spy2 = <weak_warning descr="Mock creation can be simplified with calling 'spy(<spiedInstance>)'.">mock(MockObject.class, withSettings().spiedInstance(spiedInstance))</weak_warning>;
    }

    void testName() {
        MockObject mockWithNameLiteral = <weak_warning descr="Mock creation can be simplified with calling 'mock(<class>, <name>)'.">mock(MockObject.class, withSettings().name("some name"))</weak_warning>;
        String name = "some name";
        MockObject mockWithNameVar = <weak_warning descr="Mock creation can be simplified with calling 'mock(<class>, <name>)'.">mock(MockObject.class, withSettings().name(name))</weak_warning>;
    }

    void testDefaultAnswer() {
        MockObject mockWithPredefinedAnswer = <weak_warning descr="Mock creation can be simplified with calling 'mock(<class>, <answer>)'.">mock(MockObject.class, withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS))</weak_warning>;
        MockObject mockWithInlineCreatedAnswer = <weak_warning descr="Mock creation can be simplified with calling 'mock(<class>, <answer>)'.">mock(MockObject.class, withSettings().defaultAnswer(new GloballyConfiguredAnswer()))</weak_warning>;
        ReturnsMocks answer = new ReturnsMocks();
        MockObject mockWithAnswerVar = <weak_warning descr="Mock creation can be simplified with calling 'mock(<class>, <answer>)'.">mock(MockObject.class, withSettings().defaultAnswer(answer))</weak_warning>;
    }

    void testNoHighlight() {
        MockObject spy = mock(MockObject.class, withSettings().spiedInstance(new MockObject()).name("some name"));
        MockObject mock = mock(MockObject.class, withSettings().name("some name").defaultAnswer(Answers.RETURNS_SMART_NULLS));
        MockObject mock2 = mock(MockObject.class, withSettings().serializable().defaultAnswer(new GloballyConfiguredAnswer()));
    }

    private static final class MockObject {
    }
}