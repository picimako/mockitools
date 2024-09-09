import java.util.List;

import org.mockito.Mockito;

public class NoMethodCallArgumentTest {

    public void testMethod() {
        //Highlight cases
        Mockito.mock(Object.class, Mockito.withSettings().<error descr="extraInterfaces() is not provided any argument. At least one type must be passed in.">extraInterfaces</error>());
        Mockito.<error descr="verifyNoInteractions() is not provided any argument. At least one type must be passed in.">verifyNoInteractions</error>();
        Mockito.<error descr="verifyNoMoreInteractions() is not provided any argument. At least one type must be passed in.">verifyNoMoreInteractions</error>();
        Mockito.<error descr="verifyZeroInteractions() is not provided any argument. At least one type must be passed in.">verifyZeroInteractions</error>();
        Mockito.<error descr="inOrder() is not provided any argument. At least one type must be passed in.">inOrder</error>();
        Mockito.<warning descr="ignoreStubs() is not provided any argument. At least one type must be passed in.">ignoreStubs</warning>();

        //No-highlight cases
        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class));
        Mockito.verifyNoInteractions(mock);
        Mockito.verifyNoMoreInteractions(mock);
        Mockito.verifyZeroInteractions(mock);
        Mockito.inOrder(mock);
        Mockito.ignoreStubs(mock);
    }
}
