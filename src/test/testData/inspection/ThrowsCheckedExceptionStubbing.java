import org.mockito.BDDMockito;
import org.mockito.Mockito;

public class ThrowsCheckedExceptionStubbing {
    private MockObject mockObject = Mockito.mock(MockObject.class);

    public void testWithNoThrowsClause() {
        //Mockito.when().thenThrow()
        Mockito.when(mockObject.doSomething()).thenThrow();
        Mockito.when(mockObject.doSomething()).thenThrow(NoSuchMethodError.class);
        Mockito.when(mockObject.doSomething()).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>);
        Mockito.when(mockObject.doSomething()).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>);
        Mockito.when(mockObject.doSomething()).thenThrow(IllegalArgumentException.class);
        Mockito.when(mockObject.doSomething()).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException());

        //Mockito.when().thenThrow().thenThrow()
        Mockito.when(mockObject.doSomething()).thenThrow().thenThrow();
        Mockito.when(mockObject.doSomething()).thenThrow(IllegalArgumentException.class).thenThrow(NoSuchMethodError.class);
        Mockito.when(mockObject.doSomething()).thenThrow(IllegalArgumentException.class).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>);
        Mockito.when(mockObject.doSomething()).thenThrow(IllegalArgumentException.class).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>);
        Mockito.when(mockObject.doSomething()).thenThrow(IllegalArgumentException.class).thenThrow(IllegalArgumentException.class);
        Mockito.when(mockObject.doSomething()).thenThrow(IllegalArgumentException.class).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException());

        //Mockito.doThrow().when()
        Mockito.doThrow().when(mockObject).doSomething();
        Mockito.doThrow(NoSuchMethodError.class).when(mockObject).doSomething();
        Mockito.doThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>).when(mockObject).doSomething();
        Mockito.doThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>).when(mockObject).doSomething();
        Mockito.doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
        Mockito.doThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException()).when(mockObject).doSomething();

        //Mockito.doThrow().doThrow().when()
        Mockito.doThrow().doThrow().when(mockObject).doSomething();
        Mockito.doThrow(IllegalArgumentException.class).doThrow(NoSuchMethodError.class).when(mockObject).doSomething();
        Mockito.doThrow(IllegalArgumentException.class).doThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>).when(mockObject).doSomething();
        Mockito.doThrow(IllegalArgumentException.class).doThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>).when(mockObject).doSomething();
        Mockito.doThrow(IllegalArgumentException.class).doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
        Mockito.doThrow(IllegalArgumentException.class).doThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException()).when(mockObject).doSomething();

        //BDDMockito.given().willThrow()
        BDDMockito.given(mockObject.doSomething()).willThrow();
        BDDMockito.given(mockObject.doSomething()).willThrow(NoSuchMethodError.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>);
        BDDMockito.given(mockObject.doSomething()).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>);
        BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException());

        //BDDMockito.given().willThrow().willThrow()
        BDDMockito.given(mockObject.doSomething()).willThrow().willThrow();
        BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).willThrow(NoSuchMethodError.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>);
        BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>);
        BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).willThrow(IllegalArgumentException.class);
        BDDMockito.given(mockObject.doSomething()).willThrow(IllegalArgumentException.class).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException());

        //BDDMockito.willThrow().given()
        BDDMockito.willThrow().given(mockObject).doSomething();
        BDDMockito.willThrow(NoSuchMethodError.class).given(mockObject).doSomething();
        BDDMockito.willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>).given(mockObject).doSomething();
        BDDMockito.willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>).given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException()).given(mockObject).doSomething();

        //BDDMockito.willThrow().willThrow().given()
        BDDMockito.willThrow().willThrow().given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).willThrow(NoSuchMethodError.class).given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, <error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">NoSuchMethodException.class</error>).given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>).given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).willThrow(IllegalArgumentException.class).given(mockObject).doSomething();
        BDDMockito.willThrow(IllegalArgumentException.class).willThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException()).given(mockObject).doSomething();
    }

    public void testWithThrowsClause() throws NoSuchMethodException, InstantiationException, Exception, Throwable {
            //Mockito.when().thenThrow()
            Mockito.when(mockObject.doAnotherThing()).thenThrow();
            Mockito.when(mockObject.doAnotherThing()).thenThrow(NoSuchMethodError.class);
            Mockito.when(mockObject.doAnotherThingWithBaseException()).thenThrow(ClassNotFoundException.class);
            Mockito.when(mockObject.doAnotherThingWithThrowable()).thenThrow(ClassNotFoundException.class);
            Mockito.when(mockObject.doAnotherThing()).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>, IllegalArgumentException.class, NoSuchMethodException.class);
            Mockito.when(mockObject.doAnotherThing()).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">ClassNotFoundException.class</error>);
            Mockito.when(mockObject.doAnotherThing()).thenThrow(IllegalArgumentException.class);
            Mockito.when(mockObject.doAnotherThing()).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">new ClassNotFoundException()</error>, new IllegalArgumentException());
    }
    
    public void testWithNotSupportedParameterTypes() throws NoSuchMethodException, InstantiationException {
        Mockito.when(mockObject.doAnotherThing()).thenThrow(<error descr="This checked exception is invalid for the stubbed method.
Each checked exception in a thenThrow()/doThrow()/willThrow() call must match one of the exceptions in the stubbed method's 'throws' clause.">getCNFE()</error>, getIAE());
        Mockito.when(mockObject.doAnotherThing()).thenThrow(getNSME(), getIAE());
    }

    private NoSuchMethodException getNSME() {
        return new NoSuchMethodException();
    }
    
    private ClassNotFoundException getCNFE() {
        return new ClassNotFoundException();
    }

    private IllegalArgumentException getIAE() {
        return new IllegalArgumentException();
    }

    private static class MockObject {
        public int doSomething() {
            return 0;
        }
        
        public int doAnotherThing() throws NoSuchMethodException, InstantiationException {
            return 1;
        }

        public int doAnotherThingWithBaseException() throws Exception {
            return 1;
        }

        public int doAnotherThingWithThrowable() throws Throwable {
            return 1;
        }
    }
}
