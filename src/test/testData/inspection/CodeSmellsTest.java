import org.mockito.Mockito;
import org.mockito.MockedStatic;

public class CodeSmellsTest {
    public void testMethod() {
        Object mock = Mockito.mock(Object.class);
        Mockito.<warning descr="Mockito.reset() is called. It is recommended to create new mocks instead.">reset</warning>(mock);

        try (MockedStatic<Util> util = Mockito.mockStatic(Util.class)) {
            util.<warning descr="MockedStatic.reset() is called. It is recommended to create new mocks instead.">reset</warning>();
        }
    }

    private static final class Util {
    }
}
