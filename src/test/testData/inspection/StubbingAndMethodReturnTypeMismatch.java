import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.BDDMockito;

public class DoNothingStubbing {

    @Mock
    MockObject mock;

    void nothingHighlight() {
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

    void nothingNoHighlight() {
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

    void returnHighlight() {
        Mockito.<error descr="The stubbed method, 'voidMethod()', has void return type, but it is stubbed with a return value.">doReturn</error>(10)
            .doThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'voidMethod()', has void return type, but it is stubbed with a return value.">doReturn</error>(10)
            .when(mock).voidMethod();
        Mockito.doThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'voidMethod()', has void return type, but it is stubbed with a return value.">doReturn</error>(10)
            .when(mock).voidMethod();

        Mockito.when<error descr="'when(T)' in 'org.mockito.Mockito' cannot be applied to '(void)'">(mock.voidMethod())</error>
            .thenReturn<error descr="Cannot resolve method 'thenReturn(int)'">(10)</error>
            .thenThrow(IllegalArgumentException.class)
            .thenReturn<error descr="Cannot resolve method 'thenReturn(int)'">(10)</error>;

        BDDMockito.willThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'voidMethod()', has void return type, but it is stubbed with a return value.">willReturn</error>(10)
            .given(mock).voidMethod();
        BDDMockito.<error descr="The stubbed method, 'voidMethod()', has void return type, but it is stubbed with a return value.">willReturn</error>(10)
            .willThrow(IllegalArgumentException.class)
            .<error descr="The stubbed method, 'voidMethod()', has void return type, but it is stubbed with a return value.">willReturn</error>(10)
            .given(mock).voidMethod();

        BDDMockito.given<error descr="'given(T)' in 'org.mockito.BDDMockito' cannot be applied to '(void)'">(mock.voidMethod())</error>
            .willReturn<error descr="Cannot resolve method 'willReturn(int)'">(10)</error>
            .willThrow(IllegalArgumentException.class)
            .willReturn<error descr="Cannot resolve method 'willReturn(int)'">(10)</error>;
    }

    void returnNoHighlight() {
        Mockito.doReturn(10)
            .doThrow(IllegalArgumentException.class)
            .doReturn(10)
            .when(mock).doSomething();

        Mockito.when(mock.doSomething())
            .thenReturn(10)
            .thenThrow(IllegalArgumentException.class)
            .thenReturn(10);

        BDDMockito.willReturn(10)
            .willThrow(IllegalArgumentException.class)
            .willReturn(10)
            .given(mock).doSomething();

        BDDMockito.given(mock.doSomething())
            .willReturn(10)
            .willThrow(IllegalArgumentException.class)
            .willReturn(10);

        Mockito.doThrow(IllegalArgumentException.class).when(mock).doSomething();
        Mockito.when(mock.doSomething()).thenThrow(IllegalArgumentException.class);
        BDDMockito.willThrow(IllegalArgumentException.class).given(mock).doSomething();
        BDDMockito.given(mock.doSomething()).willThrow(IllegalArgumentException.class);
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }
        public void voidMethod() {
        }
    }
}
