import org.mockito.Mockito;
import org.mockito.BDDMockito;

class SimplifyConsecutiveStubbingCalls {
    public void testMethod() {
        MockObject mockObject = Mockito.mock(MockObject.class);

        //Mockito.when()

        Mockito.when(mockObject.didSomething()).thenReturn(1);
        Mockito.when(mockObject.didSomething()).thenReturn(1).<warning descr="Mockitools: This call can be merged with previous consecutive 'thenReturn' calls.">thenReturn</warning>(2);
        Mockito.when(mockObject.didSomething()).thenReturn(1)
            .thenCallRealMethod()
            .thenReturn(2)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'thenReturn' calls.">thenReturn</warning>(3);
        Mockito.when(mockObject.didSomething()).thenReturn(1)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'thenReturn' calls.">thenReturn</warning>(2)
            .thenCallRealMethod()
            .thenReturn(3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'thenReturn' calls.">thenReturn</warning>(4);
        Mockito.when(mockObject.didSomething()).thenReturn(1, 2, 3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'thenReturn' calls.">thenReturn</warning>(4)
            .thenCallRealMethod()
            .thenReturn(5)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'thenReturn' calls.">thenReturn</warning>(6, 7);
        
        //Mockito.doReturn()
        
        Mockito.doReturn(1).when(mockObject).didSomething();
        Mockito.doReturn(1).<warning descr="Mockitools: This call can be merged with previous consecutive 'doReturn' calls.">doReturn</warning>(2).when(mockObject).didSomething();
        Mockito.doReturn(1)
            .doCallRealMethod()
            .doReturn(2)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'doReturn' calls.">doReturn</warning>(3)
            .when(mockObject).didSomething();
        Mockito.doReturn(1)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'doReturn' calls.">doReturn</warning>(2)
            .doCallRealMethod()
            .doReturn(3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'doReturn' calls.">doReturn</warning>(4)
            .when(mockObject).didSomething();
        Mockito.doReturn(1, 2, 3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'doReturn' calls.">doReturn</warning>(4)
            .doCallRealMethod()
            .doReturn(5)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'doReturn' calls.">doReturn</warning>(6, 7)
            .when(mockObject).didSomething();

        //BDDMockito.given()

        BDDMockito.given(mockObject.didSomething()).willReturn(1);
        BDDMockito.given(mockObject.didSomething()).willReturn(1).<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(2);
        BDDMockito.given(mockObject.didSomething()).willReturn(1)
            .willCallRealMethod()
            .willReturn(2)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(3);
        BDDMockito.given(mockObject.didSomething()).willReturn(1)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(2)
            .willCallRealMethod()
            .willReturn(3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(4);
        BDDMockito.given(mockObject.didSomething()).willReturn(1, 2, 3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(4)
            .willCallRealMethod()
            .willReturn(5)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(6, 7);
        
        //BDDMockito.willReturn()
        
        BDDMockito.willReturn(1).given(mockObject).didSomething();
        BDDMockito.willReturn(1).<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(2).given(mockObject).didSomething();
        BDDMockito.willReturn(1)
            .willCallRealMethod()
            .willReturn(2)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(3)
            .given(mockObject).didSomething();
        BDDMockito.willReturn(1)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(2)
            .willCallRealMethod()
            .willReturn(3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(4)
            .given(mockObject).didSomething();
        BDDMockito.willReturn(1, 2, 3)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(4)
            .willCallRealMethod()
            .willReturn(5)
            .<warning descr="Mockitools: This call can be merged with previous consecutive 'willReturn' calls.">willReturn</warning>(6, 7)
            .given(mockObject).didSomething();
    }

    private static class MockObject {
        public int didSomething() {
            return 0;
        }
    }
}
