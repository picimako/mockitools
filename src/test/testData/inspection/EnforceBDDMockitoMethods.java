import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.Mock;

public class EnforceBDDMockitoMethods {

    @Mock
    MockObject mockObject;

    public void stubbingMethods() {
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">when</error>(mockObject.doSomething()).thenReturn(10);
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">doReturn</error>(10).when(mockObject).doSomething();
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">doThrow</error>(IllegalArgumentException.class).when(mockObject).doSomething();
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">doAnswer</error>(Answers.CALLS_REAL_METHODS).when(mockObject).doSomething();
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">doCallRealMethod</error>().when(mockObject).doSomething();
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">doNothing</error>().when(mockObject).voidMethod();
    }

    public void verificationMethods() {
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">verify</error>(mockObject, Mockito.times(2)).doSomething();
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">verifyNoMoreInteractions</error>(mockObject);
        Mockito.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">verifyNoInteractions</error>(mockObject);
    }

    public void dontEnforceBDDMockitoMethod() {
        BDDMockito.given(mockObject.doSomething()).willReturn(10);
        BDDMockito.willReturn(10).given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.willAnswer(Answers.CALLS_REAL_METHODS).given(mockObject).doSomething();
        BDDMockito.willCallRealMethod().given(mockObject).doSomething();
        BDDMockito.willDoNothing().given(mockObject).voidMethod();

        BDDMockito.then(mockObject).should(Mockito.times(2)).doSomething();
        BDDMockito.then(mockObject).shouldHaveNoMoreInteractions();
        BDDMockito.then(mockObject).shouldHaveNoInteractions();
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }

        public void voidMethod() {
        }
    }
}
