import org.mockito.Mock;

public class MockFieldInitReplaceTest {

    @Mock
    public MockObject mock = Mockito.mo<caret>ck();

    public static class MockObject {
    }
}
