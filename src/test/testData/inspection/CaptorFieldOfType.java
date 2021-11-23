import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class CaptorFieldOfType {

    //No-highlight cases
    
    @Captor
    public ArgumentCaptor<String> stringCaptorNoInit;

    @Captor
    public ArgumentCaptor<?> captorNoInit;

    @Captor
    public ArgumentCaptor<String> stringCaptorInit = ArgumentCaptor.forClass(String.class);
    
    //Highlight cases

    @Captor
    public String stringNoInit;

    @Captor
    public String stringInit = "";

    @Captor
    public int intCaptor;

    @Captor
    public byte[] byteCaptor;

    @Captor
    public byte byteCCaptor[];

    @Captor
    public int intCaptorInit = 2;

    @Captor
    public byte[] byteCaptorInit = new byte[2];

    @Captor
    public byte byteCaptorCInit[] = new byte[2];
}
