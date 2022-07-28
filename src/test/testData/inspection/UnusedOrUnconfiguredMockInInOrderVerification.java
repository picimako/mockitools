import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockedStatic;

import java.util.List;

public class UnusedOrUnconfiguredMockInInOrderVerification {

    @Mock
    MockObject mockObject;
    @Mock
    MockObject mockObject2;
    @Mock
    MockObject mockObject3;

    void testHighLight() {
        //Unused mocks
        InOrder unusedMock = Mockito.inOrder(mockObject, <warning descr="No InOrder verification is called on this mock.">mockObject2</warning>);
        unusedMock.verify(mockObject).doSomething();

        InOrder unusedMock2 = Mockito.inOrder(mockObject, <warning descr="No InOrder verification is called on this mock.">mockObject2</warning>);
        unusedMock2.verify(mockObject).doSomething();
        unusedMock2.verify(mockObject, Mockito.times(2)).doSomething();

        InOrder unusedMockBDD = Mockito.inOrder(mockObject, <warning descr="No InOrder verification is called on this mock.">mockObject2</warning>);
        BDDMockito.then(mockObject).should(unusedMockBDD).doSomething();

        InOrder unusedMockBDD2 = Mockito.inOrder(mockObject, <warning descr="No InOrder verification is called on this mock.">mockObject2</warning>);
        BDDMockito.then(mockObject).should(unusedMockBDD2).doSomething();
        BDDMockito.then(mockObject).should(unusedMockBDD2, Mockito.times(2)).doSomething();

        //Unconfigured mocks
        InOrder unconfiguredMock = Mockito.inOrder(<warning descr="No InOrder verification is called on this mock.">mockObject</warning>);
        unconfiguredMock.verify(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject2</warning>).doSomething();
        unconfiguredMock.verify(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject3</warning>).doSomething();

        InOrder unconfiguredMock2 = Mockito.inOrder(<warning descr="No InOrder verification is called on this mock.">mockObject</warning>, <warning descr="No InOrder verification is called on this mock.">mockObject2</warning>);
        unconfiguredMock2.verify(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject3</warning>).doSomething();
        unconfiguredMock2.verify(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject3</warning>, Mockito.times(2)).doSomething();

        InOrder unconfiguredMockBDD = Mockito.inOrder(<warning descr="No InOrder verification is called on this mock.">mockObject</warning>);
        BDDMockito.then(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject2</warning>).should(unconfiguredMockBDD).doSomething();
        BDDMockito.then(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject3</warning>).should(unconfiguredMockBDD).doSomething();

        InOrder unconfiguredMockBDD2 = Mockito.inOrder(<warning descr="No InOrder verification is called on this mock.">mockObject</warning>, <warning descr="No InOrder verification is called on this mock.">mockObject2</warning>);
        BDDMockito.then(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject3</warning>).should(unconfiguredMockBDD2).doSomething();
        BDDMockito.then(<warning descr="This mock is not configured in 'Mockito.inOrder()'.">mockObject3</warning>).should(unconfiguredMockBDD2, Mockito.times(2)).doSomething();
    }

    void testNoHighlight() {
        InOrder inOrder = Mockito.inOrder(mockObject, mockObject2);
        inOrder.verify(mockObject).doSomething();
        inOrder.verify(mockObject2).doSomething();

        InOrder inOrder2 = Mockito.inOrder(mockObject, mockObject2);
        inOrder2.verify(mockObject).doSomething();
        inOrder2.verify(mockObject2, Mockito.times(2)).doSomething();

        InOrder inOrderBDD = Mockito.inOrder(mockObject, mockObject2);
        BDDMockito.then(mockObject).should(inOrderBDD).doSomething();
        BDDMockito.then(mockObject2).should(inOrderBDD).doSomething();

        InOrder inOrderBDD2 = Mockito.inOrder(mockObject, mockObject2);
        BDDMockito.then(mockObject).should(inOrderBDD2).doSomething();
        BDDMockito.then(mockObject2).should(inOrderBDD2, Mockito.times(2)).doSomething();

        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
            InOrder order = Mockito.inOrder(List.class);
            order.verify(mock, () -> List.of());
        }
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }
    }
}
