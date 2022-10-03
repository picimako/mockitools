package com.picimako.mockitools;

import java.util.List;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;

public class StubOnlyMockInVerification {

    @Mock(stubOnly = true)
    SomeType stubOnly;
    @Mock(stubOnly = false)
    SomeType notStubOnlyExplicit;
    @Mock
    SomeType notStubOnly;

    @Spy
    SomeType spy;

    void testHighlight() {
        SomeType localStubOnly = Mockito.mock(SomeType.class, Mockito.withSettings().stubOnly());

        Mockito.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>).doesSomething();
        Mockito.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>).doesSomething();

        Mockito.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>, Mockito.times(2)).doesSomething();
        Mockito.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>, Mockito.times(2)).doesSomething();

        InOrder inOrder = Mockito.inOrder(stubOnly, localStubOnly);
        inOrder.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>).doesSomething();
        inOrder.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>).doesSomething();
        inOrder.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>, Mockito.times(2)).doesSomething();
        inOrder.verify(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>, Mockito.times(2)).doesSomething();

        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>).should().doesSomething();
        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>).should().doesSomething();

        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>).should(inOrder).doesSomething();
        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>).should(inOrder).doesSomething();

        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>).should(Mockito.times(2)).doesSomething();
        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>).should(Mockito.times(2)).doesSomething();

        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">stubOnly</error>).should(inOrder, Mockito.times(2)).doesSomething();
        BDDMockito.then(<error descr="This mock is stub-only, thus cannot be used in verification.">localStubOnly</error>).should(inOrder, Mockito.times(2)).doesSomething();
    }

    void testNoHighlight() {
        SomeType localNotStubOnly = Mockito.mock(SomeType.class, Mockito.withSettings().lenient());
        SomeType localSpy = Mockito.spy(SomeType.class);

        Mockito.verify(notStubOnly).doesSomething();
        Mockito.verify(localNotStubOnly).doesSomething();
        Mockito.verify(spy).doesSomething();
        Mockito.verify(localSpy).doesSomething();

        InOrder inOrder = Mockito.inOrder(notStubOnly, localNotStubOnly, spy, localSpy);
        inOrder.verify(notStubOnly).doesSomething();
        inOrder.verify(localNotStubOnly).doesSomething();
        inOrder.verify(spy).doesSomething();
        inOrder.verify(localSpy).doesSomething();

        inOrder.verify(notStubOnly, Mockito.times(2)).doesSomething();
        inOrder.verify(localNotStubOnly, Mockito.times(2)).doesSomething();
        inOrder.verify(spy, Mockito.times(2)).doesSomething();
        inOrder.verify(localSpy, Mockito.times(2)).doesSomething();

        BDDMockito.then(notStubOnly).should().doesSomething();
        BDDMockito.then(localNotStubOnly).should().doesSomething();
        BDDMockito.then(spy).should().doesSomething();
        BDDMockito.then(localSpy).should().doesSomething();

        BDDMockito.then(notStubOnly).should(inOrder).doesSomething();
        BDDMockito.then(localNotStubOnly).should(inOrder).doesSomething();
        BDDMockito.then(spy).should(inOrder).doesSomething();
        BDDMockito.then(localSpy).should(inOrder).doesSomething();

        BDDMockito.then(notStubOnly).should(Mockito.times(2)).doesSomething();
        BDDMockito.then(localNotStubOnly).should(Mockito.times(2)).doesSomething();
        BDDMockito.then(spy).should(Mockito.times(2)).doesSomething();
        BDDMockito.then(localSpy).should(Mockito.times(2)).doesSomething();

        BDDMockito.then(notStubOnly).should(inOrder, Mockito.times(2)).doesSomething();
        BDDMockito.then(localNotStubOnly).should(inOrder, Mockito.times(2)).doesSomething();
        BDDMockito.then(spy).should(inOrder, Mockito.times(2)).doesSomething();
        BDDMockito.then(localSpy).should(inOrder, Mockito.times(2)).doesSomething();

        try (MockedStatic<List> mock = Mockito.mockStatic(List.class)) {
            InOrder inOrderStatic = Mockito.inOrder(List.class);
            inOrderStatic.verify(mock, () -> List.of());
            inOrderStatic.verify(mock, () -> List.of(), Mockito.times(2));
        }
    }

    private class SomeType {
        void doesSomething() {
        }
    }
}
