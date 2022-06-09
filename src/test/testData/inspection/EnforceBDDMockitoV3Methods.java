import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.Mock;

public class EnforceBDDMockitoV3Methods {

    @Mock
    MockObject mockObject;

    public void verificationMethods() {
        Mockito.<error descr="Mockitools: Stubbing/verification must be performed via org.mockito.BDDMockito">verifyZeroInteractions</error>(mockObject);
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }

        public void voidMethod() {
        }
    }
}
