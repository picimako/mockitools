import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class CaptorFieldOfTypeTest {

    //No-highlight cases
    
    @Captor
    public ArgumentCaptor<String> stringCaptorNoInit;

    @Captor
    public ArgumentCaptor<?> captorNoInit;

    @Captor
    public ArgumentCaptor<String> stringCaptorInit = ArgumentCaptor.forClass(String.class);
    
    //Highlight cases

    @Captor
    public String <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">stringNoInit</error>;

    @Captor
    public String <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">stringInit</error> = "";

    @Captor
    public int <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">intCaptor</error>;

    @Captor
    public byte[] <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">byteCaptor</error>;

    @Captor
    public byte <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">byteCCaptor</error>[];

    @Captor
    public int <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">intCaptorInit</error> = 2;

    @Captor
    public byte[] <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">byteCaptorInit</error> = new byte[2];

    @Captor
    public byte <error descr="Mockitools: A @Captor field must be of the type ArgumentCaptor.">byteCaptorCInit</error>[] = new byte[2];
}
