import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.List;

public class EnforceMockitoMethods {

    @Mock
    MockObject mockObject;

    public void stubbingMethods() {
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">given</error>(mockObject.doSomething()).willReturn(10);
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">willReturn</error>(10).given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">willThrow</error>(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">willAnswer</error>(Answers.CALLS_REAL_METHODS).given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">willCallRealMethod</error>().given(mockObject).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">willDoNothing</error>().given(mockObject).voidMethod();
    }

    public void verificationMethods() {
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">then</error>(mockObject).should(Mockito.times(2)).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">then</error>(mockObject).shouldHaveNoMoreInteractions();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">then</error>(mockObject).shouldHaveNoInteractions();

        InOrder inOrderBDD = Mockito.inOrder(mockObject);
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">then</error>(mockObject).should(inOrderBDD).doSomething();
        BDDMockito.<error descr="Stubbing/verification must be performed via org.mockito.Mockito / org.mockito.InOrder">then</error>(mockObject).should(inOrderBDD, Mockito.times(2)).doSomething();
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

        InOrder inOrder = Mockito.inOrder(mockObject);
        inOrder.verify(mockObject).doSomething();
        inOrder.verify(mockObject, Mockito.times(2)).doSomething();
    }

    public void dontEnforceAnyMethod() {
        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
            mock.verify(List::of);
            mock.verify(List::of, Mockito.times(2));
        }
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }

        public void voidMethod() {
        }
    }
}
