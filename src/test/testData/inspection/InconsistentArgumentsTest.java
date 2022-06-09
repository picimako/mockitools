import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.Mockito;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.ArgumentMatchers;
import org.mockito.AdditionalMatchers;

public class InconsistentArgumentsTest {

    @Mock
    public MockObject mock;

    public void testMethod() {
        //No-highlight cases
        Mockito.when(mock.method()).thenReturn(10);
        Mockito.when(mock.methodWithParam(ArgumentMatchers.eq("some string"))).thenReturn(10);
        Mockito.when(mock.methodWithParam("some string")).thenReturn(10);
        Mockito.when(mock.methodWithParams(ArgumentMatchers.eq("some string"), ArgumentMatchers.eq(Integer.class))).thenReturn(10);
        Mockito.when(mock.methodWithParams("some string", Integer.class)).thenReturn(10);

        Mockito.when(mock.methodWithParam(AdditionalMatchers.geq(ArgumentMatchers.eq(5)))).thenReturn(10);
        Mockito.when(mock.methodWithParam(AdditionalMatchers.geq(5))).thenReturn(10);
        Mockito.when(mock.methodWithParam(AdditionalMatchers.and(ArgumentMatchers.eq("some string"), ArgumentMatchers.anyString()))).thenReturn(10);
        Mockito.when(mock.methodWithParam(AdditionalMatchers.and("some string", "another string"))).thenReturn(10);

        Mockito.doReturn(10).when(mock).method();
        Mockito.doReturn(10).when(mock).methodWithParam("some string");
        Mockito.doReturn(10).when(mock).methodWithParams("some string", Integer.class);
        Mockito.doReturn(10).when(mock).methodWithParam(Mockito.anyString());
        Mockito.doReturn(10).when(mock).methodWithParams(Mockito.anyString(), eq(Integer.class));

        BDDMockito.given(mock.method()).willReturn(10);
        BDDMockito.given(mock.methodWithParam(anyString())).willReturn(10);
        BDDMockito.given(mock.methodWithParams(anyString(), eq(Integer.class))).willReturn(10);
        BDDMockito.given(mock.methodWithParam("some string")).willReturn(10);
        BDDMockito.given(mock.methodWithParams("some string", Integer.class)).willReturn(10);

        //Highlight cases
        Mockito.when(mock.methodWithParams<error descr="Mockitools: Both matcher and non-matcher arguments are used.">(ArgumentMatchers.eq("some string"), Integer.class)</error>).thenReturn(10);
        Mockito.when(mock.methodWithParams<error descr="Mockitools: Both matcher and non-matcher arguments are used.">("some string", eq(Integer.class))</error>).thenReturn(10);

        Mockito.when(mock.methodWithParam(AdditionalMatchers.and<error descr="Mockitools: Both matcher and non-matcher arguments are used.">(ArgumentMatchers.eq("some string"), "another string")</error>)).thenReturn(10);
        Mockito.when(mock.methodWithParam(AdditionalMatchers.and<error descr="Mockitools: Both matcher and non-matcher arguments are used.">("some string", eq("another string"))</error>)).thenReturn(10);

        Mockito.doReturn(10).when(mock).methodWithParams<error descr="Mockitools: Both matcher and non-matcher arguments are used.">(Mockito.anyString(), Integer.class)</error>;
        Mockito.doReturn(10).when(mock).methodWithParams<error descr="Mockitools: Both matcher and non-matcher arguments are used.">("some string", eq(Integer.class))</error>;

        BDDMockito.given(mock.methodWithParams<error descr="Mockitools: Both matcher and non-matcher arguments are used.">(anyString(), Integer.class)</error>).willReturn(10);
        BDDMockito.given(mock.methodWithParams<error descr="Mockitools: Both matcher and non-matcher arguments are used.">("some string", eq(Integer.class))</error>).willReturn(10);
    }

    private static final class MockObject {

        public int method() {
            return 0;
        }

        public int methodWithParam(String s) {
            return 0;
        }

        public int methodWithParam(int i) {
            return 0;
        }

        public int methodWithParams(String s, Class<? extends Object> clazz) {
            return 0;
        }
    }
}
