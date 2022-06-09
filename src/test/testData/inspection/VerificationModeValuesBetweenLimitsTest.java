import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.timeout;

import org.mockito.Mockito;
import org.mockito.InOrder;

public class VerificationModeValuesBetweenLimitsTest {
    
    public void testMethod() {
        Object mockObject = Mockito.mock(Object.class);
        
        //No-highlight cases
        Mockito.verify(mockObject, times(0)).toString();
        Mockito.verify(mockObject, atLeast(0)).toString();
        Mockito.verify(mockObject, atMost(0)).toString();

        Mockito.verify(mockObject, times(10)).toString();
        Mockito.verify(mockObject, atLeast(10)).toString();
        Mockito.verify(mockObject, atMost(10)).toString();

        InOrder inOrderNoHighlight = Mockito.inOrder(mockObject);
        inOrderNoHighlight.verify(mockObject, calls(10)).toString();

        Mockito.verify(mockObject, after(1000).never()).toString();
        Mockito.verify(mockObject, timeout(1000)).toString();

        //Highlight cases
        Mockito.verify(mockObject, times(<error descr="The argument value of a(n) 'times()' call must be 0 or greater.">-1</error>)).toString();
        Mockito.verify(mockObject, atLeast(<error descr="The argument value of a(n) 'atLeast()' call must be 0 or greater.">-1</error>)).toString();
        Mockito.verify(mockObject, atMost(<error descr="The argument value of a(n) 'atMost()' call must be 0 or greater.">-1</error>)).toString();
        Mockito.verify(mockObject, times(<error descr="The argument value of a(n) 'times()' call must be 0 or greater.">-1</error>).description("desc")).toString();
        Mockito.verify(mockObject, atLeast(<error descr="The argument value of a(n) 'atLeast()' call must be 0 or greater.">-1</error>).description("desc")).toString();
        Mockito.verify(mockObject, atMost(<error descr="The argument value of a(n) 'atMost()' call must be 0 or greater.">-1</error>).description("desc")).toString();

        InOrder inOrderHighlight = Mockito.inOrder(mockObject);
        inOrderHighlight.verify(mockObject, calls(<error descr="The argument value of a(n) 'calls()' call must be 1 or greater.">-1</error>)).toString();
        inOrderHighlight.verify(mockObject, calls(<error descr="The argument value of a(n) 'calls()' call must be 1 or greater.">0</error>)).toString();
        inOrderHighlight.verify(mockObject, calls(<error descr="The argument value of a(n) 'calls()' call must be 1 or greater.">-1</error>).description("desc")).toString();
        inOrderHighlight.verify(mockObject, calls(<error descr="The argument value of a(n) 'calls()' call must be 1 or greater.">0</error>).description("desc")).toString();
        
        Mockito.verify(mockObject, after(<error descr="The argument value of a(n) 'after()' call must be 0 or greater.">-1000</error>)).toString();
        Mockito.verify(mockObject, timeout(<error descr="The argument value of a(n) 'timeout()' call must be 0 or greater.">-1000</error>)).toString();
        Mockito.verify(mockObject, timeout(<error descr="The timeout value must be lower than the user-defined max threshold: 5,000.">6000</error>)).toString();
        Mockito.verify(mockObject, after(<error descr="The argument value of a(n) 'after()' call must be 0 or greater.">-1000</error>).never()).toString();
        Mockito.verify(mockObject, timeout(<error descr="The argument value of a(n) 'timeout()' call must be 0 or greater.">-1000</error>).description("desc")).toString();
        Mockito.verify(mockObject, timeout(<error descr="The timeout value must be lower than the user-defined max threshold: 5,000.">6000</error>).description("desc")).toString();
    } 
}
