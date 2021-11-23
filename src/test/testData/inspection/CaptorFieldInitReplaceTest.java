import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class CaptorFieldInitReplaceTest {

    @Captor
    public ArgumentCaptor<String> captor = ArgumentCaptor.<caret>forClass(String.class);
}
