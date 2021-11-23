import org.mockito.Mock;
import org.mockito.Spy;
import java.util.List;
import org.mockito.Mockito;

public class NonMockableTypesTest {

    @Mock
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">String</error> mockString;

    @Mock
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Class</error> mockClass;

    @Mock
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Short</error> mockShort;

    @Mock
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">int</error> mockint;

    @Spy
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">String</error> spyString;

    @Spy
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Class</error> spyClass;

    @Spy
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Short</error> spyShort;

    @Spy
    private <error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">int</error> spyint;

    @Mock
    private List<String> mockList;

    @Spy
    private List<String> spyList;

    public void testMethod() {
        Mockito.mock(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">String.class</error>);
        Mockito.mock(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Class.class</error>);
        Mockito.mock(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Short.class</error>);
        Mockito.mock(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">int.class</error>);
        Mockito.spy(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">String.class</error>);
        Mockito.spy(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Class.class</error>);
        Mockito.spy(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">Short.class</error>);
        Mockito.spy(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">int.class</error>);

        Mockito.mock(<error descr="Mockitools: This type is not mockable by Mockito. Such types include primitive types, Class, String, and the wrapper types of primitives.">String.class</error>, Mockito.withSettings().name("name"));

        Mockito.mock(List.class);
        Mockito.spy(List.class);
    }
}
