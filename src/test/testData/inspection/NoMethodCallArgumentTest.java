import java.util.List;

import org.mockito.Mockito;

public class NoMethodCallArgumentTest {

    public void testMethod() {
        //Highlight cases
        Mockito.mock(Object.class, Mockito.withSettings().<error descr="Mockitools: extraInterfaces() is not provided any argument. At least one object must be passed.">extraInterfaces</error>());
        Mockito.<error descr="Mockitools: verifyNoInteractions() is not provided any argument. At least one object must be passed.">verifyNoInteractions</error>();
        Mockito.<error descr="Mockitools: verifyNoMoreInteractions() is not provided any argument. At least one object must be passed.">verifyNoMoreInteractions</error>();
        Mockito.<error descr="Mockitools: verifyZeroInteractions() is not provided any argument. At least one object must be passed.">verifyZeroInteractions</error>();
        Mockito.<error descr="Mockitools: inOrder() is not provided any argument. At least one object must be passed.">inOrder</error>();
        Mockito.<warning descr="Mockitools: ignoreStubs() is not provided any argument. At least one object must be passed.">ignoreStubs</warning>();

        //No-highlight cases
        Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class));
        Mockito.verifyNoInteractions(mock);
        Mockito.verifyNoMoreInteractions(mock);
        Mockito.verifyZeroInteractions(mock);
        Mockito.inOrder(mock);
        Mockito.ignoreStubs(mock);
    }
}
