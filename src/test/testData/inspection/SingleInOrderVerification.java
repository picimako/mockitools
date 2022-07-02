import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.InOrder;

public class SingleInOrderVerification {

    @Mock
    MockObject mockObject;

    void testHighlight() {
        //Single verify call on InOrder
        InOrder <warning descr="This InOrder variable is used in only one verification. A single verification is always in order.">order</warning> = Mockito.inOrder(mockObject);
        order.verify(mockObject, Mockito.times(2).description("some description")).doSomething();

        //Single InOrder usage in BDDMockito.then()
        InOrder <warning descr="This InOrder variable is used in only one verification. A single verification is always in order.">bddOrder</warning> = Mockito.inOrder(mockObject);
        BDDMockito.then(mockObject).should(bddOrder).doSomething();
    }

    void testNoHighlight() {
        //Multiple verify calls on InOrder
        InOrder order = Mockito.inOrder(mockObject);
        order.verify(mockObject).doSomething();
        order.verify(mockObject, Mockito.times(2).description("some description")).doSomething();

        //Multiple InOrder usage in BDDMockito.then()
        InOrder bddOrder = Mockito.inOrder(mockObject);
        BDDMockito.then(mockObject).should(bddOrder).doSomething();
        BDDMockito.then(mockObject).should(bddOrder, Mockito.times(2)).doSomething();

        //Single InOrder usage as method argument
        InOrder paramOrder = Mockito.inOrder(mockObject);
        method(paramOrder);
    }

    private void method(InOrder inorder) {
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }
    }
}