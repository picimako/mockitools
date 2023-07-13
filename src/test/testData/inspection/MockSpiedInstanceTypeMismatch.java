import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class MockSpiedInstanceTypeMismatch {

    void testTypeMismatch() {
        //No highlight

        var matchesWithoutGenerics = mock(ArrayList.class, withSettings().spiedInstance(new ArrayList()));
        var matchesWithGenerics = mock(ArrayList.class, withSettings().spiedInstance(new ArrayList<>()));

        //Highlight

        var doesNotMatchWithSubType = mock(<error descr="The mocked type doesn't match the type of the spied instance.">List.class</error>, withSettings().spiedInstance(new ArrayList<>()));
        var doesNotMatchWithSuperType = mock(<error descr="The mocked type doesn't match the type of the spied instance.">SomeType.class</error>, withSettings().spiedInstance(new SuperType()));
        var doesNotMatchWithOtherType = mock(<error descr="The mocked type doesn't match the type of the spied instance.">List.class</error>, withSettings().spiedInstance(new HashSet<>()));
    }

    //No highlighting happens for these cases
    void testNotSupported() {
        var doesNotMatchTypeForStaticFactoryMethod = mock(List.class, withSettings().spiedInstance(List.of()));
        var matchesRuntimeType = mock(ArrayList.class, withSettings().spiedInstance(createSpiedInstance()));

        var matchesRawTypes = mock(new ArrayList<String>().getClass(), withSettings().spiedInstance(new ArrayList<Boolean>()));
        var matchesRawTypes2 = mock(getMockType(), withSettings().spiedInstance(new ArrayList<Boolean>()));
    }

    private List<?> createSpiedInstance() {
        return new ArrayList<>();
    }

    private Class<ArrayList<String>> getMockType() {
        var strings = new ArrayList<String>();
        return (Class<ArrayList<String>>) strings.getClass();
    }

    private static final class SomeType extends SuperType {
    }

    private static class SuperType {
    }
}
