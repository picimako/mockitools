import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.InjectMocks;

public class MockFieldInit {

    @Captor
    public ArgumentCaptor<String> stringCaptorNoInit;

    @Captor
    public ArgumentCaptor<String> stringCaptorInit = ArgumentCaptor.forClass(String.class);

    @Mock
    public MockObject mock;

    @Mock
    public MockObject mockInit = <warning descr="Explicit initialization of a @Mock or @InjectMocks field can be omitted.">Mockito.mock()</warning>;

    @InjectMocks
    public MockObject injectMocksNoInit;

    @InjectMocks
    public MockObject injectMocksInit = <warning descr="Explicit initialization of a @Mock or @InjectMocks field can be omitted.">mockInit</warning>;

    public MockObject nonMockNoInit;

    public MockObject nonMockInit = new MockObject();

    public static class MockObject {
    }
}
