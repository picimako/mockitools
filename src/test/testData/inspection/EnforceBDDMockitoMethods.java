import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.List;

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

        InOrder inOrder = Mockito.inOrder(mockObject);
        inOrder.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">verify</error>(mockObject).doSomething();
        inOrder.<error descr="Stubbing/verification must be performed via org.mockito.BDDMockito">verify</error>(mockObject, Mockito.times(2)).doSomething();
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

        InOrder inOrderBDD = Mockito.inOrder(mockObject);
        BDDMockito.then(mockObject).should(inOrderBDD).doSomething();
        BDDMockito.then(mockObject).should(inOrderBDD, Mockito.times(2)).doSomething();
    }

    public void dontEnforceAnyMethod() {
        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
            mock.verify(List::of);
            mock.verify(List::of, Mockito.times(2));

            InOrder inOrder = Mockito.inOrder(List.class);
            inOrder.verify(mock, () -> List.of());
            inOrder.verify(mock, List::of, Mockito.times(1));
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
