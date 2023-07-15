import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.Spy;

public class SpyingOnMock {

    @Mock
    MockObject mockField;

    @Spy
    MockObject spyField;

    Object nonMockField;

    public void spyOnMock() {
        MockObject localMock = Mockito.mock();

        //Highlighting

        Mockito.spy(<error descr="Spying is not allowed on mocks.">Mockito.mock(MockObject.class)</error>);
        Mockito.spy(<error descr="Spying is not allowed on mocks.">mockField</error>);

        //No highlighting

        int localNonMock = 5;
        Mockito.spy(localMock); //variables with Mockito.mock() initializers are not recognized as mocks yet
        Mockito.spy(localNonMock);
        Mockito.spy(nonMockMethod());
        Mockito.spy(nonMockField);

        Mockito.spy(Mockito.spy(MockObject.class));
        Mockito.spy(spyField);
    }

    private Object nonMockMethod() {
        return new Object();
    }

    private static class MockObject {
    }
}
