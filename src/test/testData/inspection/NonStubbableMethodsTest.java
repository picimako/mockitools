import org.mockito.Mockito;
import org.mockito.BDDMockito;

class NonStubbableMethodsTest {

    void aTestMethod() {
        MockObject mock = Mockito.mock();
        SubMockObject subMock = Mockito.mock();
        SubMockObject m = Mockito.mock();

        //final method
        Mockito.when(mock.aFinalMethod()).thenReturn(10);
        Mockito.doReturn(10).when(mock).aFinalMethod();
        BDDMockito.given(mock.aFinalMethod()).willReturn(10);
        BDDMockito.willReturn(10).given(mock).aFinalMethod();

        //private method
        Mockito.when(<error descr="private/native methods cannot be stubbed by Mockito.">mock.aPrivateMethod()</error>).thenReturn(10);
        Mockito.doReturn(10).when(mock).<error descr="private/native methods cannot be stubbed by Mockito.">aPrivateMethod</error>();
        BDDMockito.given(<error descr="private/native methods cannot be stubbed by Mockito.">mock.aPrivateMethod()</error>).willReturn(10);
        BDDMockito.willReturn(10).given(mock).<error descr="private/native methods cannot be stubbed by Mockito.">aPrivateMethod</error>();

        //native method
        Mockito.when(<error descr="private/native methods cannot be stubbed by Mockito.">mock.aNativeMethod()</error>).thenReturn(10);
        Mockito.doReturn(10).when(mock).<error descr="private/native methods cannot be stubbed by Mockito.">aNativeMethod</error>();
        BDDMockito.given(<error descr="private/native methods cannot be stubbed by Mockito.">mock.aNativeMethod()</error>).willReturn(10);
        BDDMockito.willReturn(10).given(mock).<error descr="private/native methods cannot be stubbed by Mockito.">aNativeMethod</error>();

        //equals()
        Mockito.when(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">mock.equals(m)</error>).thenReturn(false);
        Mockito.doReturn(false).when(mock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">equals</error>(m);
        BDDMockito.given(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">mock.equals(m)</error>).willReturn(false);
        BDDMockito.willReturn(false).given(mock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">equals</error>(m);

        //Overridden equals()
        Mockito.when(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">subMock.equals(m)</error>).thenReturn(false);
        Mockito.doReturn(false).when(subMock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">equals</error>(m);
        BDDMockito.given(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">subMock.equals(m)</error>).willReturn(false);
        BDDMockito.willReturn(false).given(subMock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">equals</error>(m);

        //native hashCode()
        Mockito.when(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">mock.hashCode()</error>).thenReturn(10);
        Mockito.doReturn(10).when(mock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">hashCode</error>();
        BDDMockito.given(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">mock.hashCode()</error>).willReturn(10);
        BDDMockito.willReturn(10).given(mock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">hashCode</error>();

        //Overridden hashCode()
        Mockito.when(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">subMock.hashCode()</error>).thenReturn(10);
        Mockito.doReturn(10).when(subMock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">hashCode</error>();
        BDDMockito.given(<error descr="equals()/hashCode() cannot be stubbed by Mockito.">subMock.hashCode()</error>).willReturn(10);
        BDDMockito.willReturn(10).given(subMock).<error descr="equals()/hashCode() cannot be stubbed by Mockito.">hashCode</error>();
    }

    public static class SubMockObject {
        @Override
        public int hashCode() {
            return 100;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    public static class MockObject {

        final int aFinalMethod() {
            return 1;
        }

        private int aPrivateMethod() {
            return 1;
        }

        native int aNativeMethod();
    }
}
