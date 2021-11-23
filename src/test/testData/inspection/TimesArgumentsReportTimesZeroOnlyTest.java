import static org.mockito.Mockito.times;

import org.mockito.Mockito;

public class TimesArgumentsReportTimesZeroOnlyTest {

    public void testMethod() {
        Object mockObject = Mockito.mock(Object.class);

        //No-highlight cases
        Mockito.verify(mockObject, times(10)).toString();
        Mockito.verify(mockObject, times(10).description("")).toString();

        //Highlight cases
        Mockito.verify(mockObject, <warning descr="Mockitools: This call can be replaced with Mockito.never().">times(0)</warning>).toString();
        Mockito.verify(mockObject, times(1)).toString();

        Mockito.verify(mockObject, <warning descr="Mockitools: This call can be replaced with Mockito.never().">times(0)</warning>.description("")).toString();
        Mockito.verify(mockObject, times(1).description("")).toString();
    }
}
