import java.io.IOException;

import org.mockito.Mockito;
import org.mockito.BDDMockito;

class SimplifyConsecutiveThrowCalls {
    public void testMethod() {
        MockObject mockObject = Mockito.mock(MockObject.class);

        //Mockito.when().thenThrow()

        Mockito.when(mockObject.doSomething()).thenThrow(); //no-no
        Mockito.when(mockObject.doSomething()).thenThrow().thenThrow(NoSuchMethodError.class); //no-no
        Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodError.class); //no-no
        Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodException.class).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(NoSuchMethodException.class);
        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, NoSuchMethodException.class); //no-no
        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class, NoSuchMethodException.class).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(IllegalArgumentException.class);
        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(IllegalArgumentException.class, NoSuchMethodException.class);

        Mockito.when(mockObject.doSomething()).thenThrow(new IOException()); //no-no
        Mockito.when(mockObject.doSomething()).thenThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(new IllegalArgumentException());
        Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(new IllegalArgumentException());
        Mockito.when(mockObject.doSomething()).thenThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(new IllegalArgumentException())
            .thenReturn(10)
            .thenThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(new IllegalArgumentException());

        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(new IllegalArgumentException());
        Mockito.when(mockObject.doSomething()).thenThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(new IllegalArgumentException("message"));
        Mockito.when(mockObject.doSomething()).thenThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'thenThrow' calls.">thenThrow</warning>(new IllegalArgumentException("message"));

        //Mockito.doThrow() + Mockito.do...().doThrow()

        Mockito.doThrow().when(mockObject).doSomething(); //no-no
        Mockito.doThrow().doThrow(NoSuchMethodError.class).when(mockObject).doSomething(); //no-no
        Mockito.doThrow(NoSuchMethodError.class).when(mockObject).doSomething(); //no-no
        Mockito.doThrow(NoSuchMethodError.class).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(NoSuchMethodException.class).when(mockObject).doSomething();
        Mockito.doThrow(NoSuchMethodError.class, NoSuchMethodException.class).when(mockObject).doSomething(); //no-no
        Mockito.doThrow(NoSuchMethodError.class, NoSuchMethodException.class).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(IOException.class).when(mockObject).doSomething();
        Mockito.doThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(NoSuchMethodError.class, NoSuchMethodException.class).when(mockObject).doSomething();

        Mockito.doThrow(new IOException()).when(mockObject).doSomething(); //no-no
        Mockito.doThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException()).when(mockObject).doSomething();
        Mockito.doThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException()).when(mockObject).doSomething();
        Mockito.doThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException())
            .doReturn(10)
            .doThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException())
            .when(mockObject).doSomething();
        Mockito.doReturn(5)
            .doThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException())
            .doReturn(10)
            .doThrow(new IOException(), new NoSuchMethodException()).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException())
            .when(mockObject).doSomething();

        Mockito.doThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException()).when(mockObject).doSomething();
        Mockito.doThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException("message")).when(mockObject).doSomething();
        Mockito.doThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'doThrow' calls.">doThrow</warning>(new IllegalArgumentException("message")).when(mockObject).doSomething();

        //BDDMockito.given().willThrow()

        BDDMockito.given(mockObject.doSomething()).willThrow(); //no-no
        BDDMockito.given(mockObject.doSomething()).willThrow().willThrow(NoSuchMethodError.class); //no-no
        BDDMockito.given(mockObject.doSomething()).willThrow(NoSuchMethodException.class); //no-no
        BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(NoSuchMethodException.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class); //no-no
        BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class, NoSuchMethodException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IOException.class, NoSuchMethodException.class);

        BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()); //no-no
        BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(new IOException(), new IllegalArgumentException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class)
            .willReturn(10)
            .willThrow(new IOException(), new IllegalArgumentException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class);

        BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(new IllegalArgumentException());
        BDDMockito.given(mockObject.doSomething()).willThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(new IllegalArgumentException("message"));
        BDDMockito.given(mockObject.doSomething()).willThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(new IllegalArgumentException("message"));

        //BDDMockito.willThrow()

        BDDMockito.willThrow().given(mockObject).doSomething(); //no-no
        BDDMockito.willThrow().willThrow(NoSuchMethodError.class).given(mockObject).doSomething(); //no-no
        BDDMockito.willThrow(NoSuchMethodException.class).given(mockObject).doSomething(); //no-no
        BDDMockito.willThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(NoSuchMethodException.class).given(mockObject).doSomething();
        BDDMockito.willThrow(IOException.class, NoSuchMethodException.class).given(mockObject).doSomething(); //no-no
        BDDMockito.willThrow(IOException.class, NoSuchMethodException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IOException.class, NoSuchMethodException.class).given(mockObject).doSomething();

        BDDMockito.willThrow(new IOException()).given(mockObject).doSomething(); //no-no
        BDDMockito.willThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.willThrow(new IOException(), new IllegalArgumentException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class)
            .willReturn(10)
            .willThrow(new IOException(), new IllegalArgumentException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(IllegalArgumentException.class)
            .given(mockObject).doSomething();

        BDDMockito.willThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(new IllegalArgumentException()).given(mockObject).doSomething();
        BDDMockito.willThrow(new IOException()).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(new IllegalArgumentException("message")).given(mockObject).doSomething();
        BDDMockito.willThrow(IOException.class).<warning descr="This call can be merged with previous consecutive 'willThrow' calls.">willThrow</warning>(new IllegalArgumentException("message")).given(mockObject).doSomething();
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }
    }
}
