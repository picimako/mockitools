import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Answers;

class ClasslessMockCreation {

    void noHighlights() {
        MockObject mock = Mockito.mock();
        var mockVar = Mockito.mock();
        MockObject mockWithAnswer = Mockito.mock(Answers.CALLS_REAL_METHODS);
        var mockWithAnswerVar = Mockito.mock(Answers.CALLS_REAL_METHODS);
        MockObject mockWithSettings = Mockito.mock(Mockito.withSettings());
        var mockWithSettingsVar = Mockito.mock(Mockito.withSettings());
        MockObject mockWithName = Mockito.mock("name");
        var mockWithNameVar = Mockito.mock("name");

        Mockito.mock();
        Mockito.mock(Answers.CALLS_REAL_METHODS);
        Mockito.mock(Mockito.withSettings());
        Mockito.mock("name");

        MockObject spy = Mockito.spy();
        var spyVar = Mockito.spy(new MockObject());

        Mockito.spy();
        Mockito.spy(new MockObject());
    }

    void highlights() {
        MockObject mockFromObject = Mockito.mock(<error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);
        MockObject mockWithAnswer = Mockito.mock(Answers.CALLS_REAL_METHODS, <error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);
        var mockWithAnswerVar = Mockito.mock(Answers.CALLS_REAL_METHODS, <error descr="This type of mock creation must not have any value passed in.">new MockObject(), new MockObject(), new MockObject()</error>);
        MockObject mockWithSettings = Mockito.mock(Mockito.withSettings(), <error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);
        var mockWithSettingsVar = Mockito.mock(Mockito.withSettings(), <error descr="This type of mock creation must not have any value passed in.">new MockObject(), new MockObject(), new MockObject()</error>);
        MockObject mockWithName = Mockito.mock("name", <error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);
        var mockWithNameVar = Mockito.mock("name", <error descr="This type of mock creation must not have any value passed in.">new MockObject(), new MockObject(), new MockObject()</error>);

        Mockito.mock(<error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);
        Mockito.mock(Answers.CALLS_REAL_METHODS, <error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);
        Mockito.mock(Mockito.withSettings(), <error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);
        Mockito.mock("name", <error descr="This type of mock creation must not have any value passed in.">new MockObject()</error>);

        var spyVar = Mockito.spy(<error descr="This type of mock creation must not have any value passed in.">new MockObject(), new MockObject()</error>);
        Mockito.spy(<error descr="This type of mock creation must not have any value passed in.">new MockObject(), new MockObject()</error>);
    }

    private static class MockObject {
    }
}
