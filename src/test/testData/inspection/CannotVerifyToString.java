import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

public class CannotVerifyToString {

    @Mock
    MockObject mockObject;
    @Mock
    MockObjectWithToString mockObjectWithToString;

    void testHighlight() {
        Mockito.verify(mockObject).<error descr="Mockito cannot verify 'toString()'.">toString</error>();
        BDDMockito.then(mockObject).should().<error descr="Mockito cannot verify 'toString()'.">toString</error>();

        Mockito.verify(mockObjectWithToString).<error descr="Mockito cannot verify 'toString()'.">toString</error>();
        BDDMockito.then(mockObjectWithToString).should().<error descr="Mockito cannot verify 'toString()'.">toString</error>();

        InOrder inOrder = Mockito.inOrder(mockObject, mockObjectWithToString);
        inOrder.verify(mockObject).<error descr="Mockito cannot verify 'toString()'.">toString</error>();
        BDDMockito.then(mockObject).should(inOrder).<error descr="Mockito cannot verify 'toString()'.">toString</error>();
        inOrder.verify(mockObjectWithToString).<error descr="Mockito cannot verify 'toString()'.">toString</error>();
        BDDMockito.then(mockObjectWithToString).should(inOrder).<error descr="Mockito cannot verify 'toString()'.">toString</error>();
    }

    void testNoHighlight() {
        Mockito.verify(mockObject).doSomething();
        BDDMockito.then(mockObject).should().doSomething();

        Mockito.verify(mockObjectWithToString).doSomething();
        BDDMockito.then(mockObjectWithToString).should().doSomething();

        InOrder inOrder = Mockito.inOrder(mockObject, mockObjectWithToString);
        inOrder.verify(mockObject).doSomething();
        BDDMockito.then(mockObject).should(inOrder).doSomething();
        inOrder.verify(mockObjectWithToString).doSomething();
        BDDMockito.then(mockObjectWithToString).should(inOrder).doSomething();
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }
    }

    private static class MockObjectWithToString extends MockObject {
        @Override
        public String toString() {
            return "overridden toString()";
        }
    }
}
