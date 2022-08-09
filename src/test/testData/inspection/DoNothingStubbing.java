import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.BDDMockito;

public class DoNothingStubbing {

    @Mock
    MockObject mock;

    void highlight() {
        Mockito.<error descr="The stubbed method, 'doSomething()', has a non-void return type. Only void methods can do nothing.">doNothing</error>()
            .doThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'doSomething()', has a non-void return type. Only void methods can do nothing.">doNothing</error>()
            .when(mock).doSomething();
        Mockito.doThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'doSomething()', has a non-void return type. Only void methods can do nothing.">doNothing</error>()
            .when(mock).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'doSomething()', has a non-void return type. Only void methods can do nothing.">willDoNothing</error>()
            .given(mock).doSomething();
        BDDMockito.<error descr="The stubbed method, 'doSomething()', has a non-void return type. Only void methods can do nothing.">willDoNothing</error>()
            .willThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'doSomething()', has a non-void return type. Only void methods can do nothing.">willDoNothing</error>()
            .given(mock).doSomething();
    }

    void noHighlight() {
        Mockito.doNothing()
            .doThrow(IllegalArgumentException.class)
            .doNothing()
            .when(mock).voidMethod();
        BDDMockito.willDoNothing()
            .willThrow(IllegalArgumentException.class)
            .willDoNothing()
            .given(mock).voidMethod();

        Mockito.doThrow(IllegalArgumentException.class).when(mock).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).given(mock).doSomething();
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }
        public void voidMethod() {
        }
    }
}
