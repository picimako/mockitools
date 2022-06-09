import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class CaptorFieldInit {

    @Captor
    public ArgumentCaptor<String> stringCaptorNoInit;

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

    @Captor
    public ArgumentCaptor<String> stringCaptorInit = <warning descr="Explicit initialization of a @Captor field can be omitted.">ArgumentCaptor.forClass(String.class)</warning>;
}
