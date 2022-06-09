import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.Mock;

public class EnforceMockitoMethods {

    @Mock
    MockObject mockObject;

    public void stubbingMethods() {
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">given</error>(mockObject.doSomething()).willReturn(10);
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">willReturn</error>(10).given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">willThrow</error>(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">willAnswer</error>(Answers.CALLS_REAL_METHODS).given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">willCallRealMethod</error>().given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">willDoNothing</error>().given(mockObject).voidMethod();
    }

    public void verificationMethods() {
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">then</error>(mockObject).should(Mockito.times(2)).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">then</error>(mockObject).shouldHaveNoMoreInteractions();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito">then</error>(mockObject).shouldHaveNoInteractions();
    }

    public void dontEnforceBDDMockitoMethod() {
        Mockito.when(mockObject.doSomething()).thenReturn(10);
        Mockito.doReturn(10).when(mockObject).doSomething();
        Mockito.doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
        Mockito.doAnswer(Answers.CALLS_REAL_METHODS).when(mockObject).doSomething();
        Mockito.doCallRealMethod().when(mockObject).doSomething();
        Mockito.doNothing().when(mockObject).voidMethod();

        Mockito.verify(mockObject, Mockito.times(2)).doSomething();
        Mockito.verifyNoMoreInteractions();
        Mockito.verifyNoInteractions();
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }

        public void voidMethod() {
        }
    }
}
