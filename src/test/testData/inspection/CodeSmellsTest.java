import org.mockito.Mockito;

public class CodeSmellsTest {
    public void testMethod() {
        Object mock = Mockito.mock(Object.class);
        Mockito.<warning descr="Mockitools: Mockito.reset() is called. It is recommended to create new mocks instead.">reset</warning>(mock);
    }
}
