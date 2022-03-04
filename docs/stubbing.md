# Stubbing

## Invalid checked exception is passed into *throw() methods

![](https://img.shields.io/badge/inspection-orange) ![](https://img.shields.io/badge/since-0.3.0-blue) [![](https://img.shields.io/badge/implementation-ThrowsCheckedExceptionStubbingInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/ThrowsCheckedExceptionStubbingInspection.java)

Reports exception references in <code>*throw()</code> stubbing methods based on Mockito's rule on checked exceptions.
*If [the specified exception types] contain a checked exception then it has to match one of the checked exceptions of method signature.*
   
The following constructs are supported:
- `Mockito.when().thenThrow(...)`
- `BDDMockito.given().willThrow(...)`
- `Mockito.doThrow(...).when()`
- `BDDMockito.willThrow(...).given()`
   
In case of each way of stubbing, further chained <code>*throw()</code> calls are supported too. In case of an empty list, no problem is reported.

**Example:**

```java
void testMethod() {
    MockObject mock = new MockObject();
    // IOException is NOT reported because it is in MockObject#doSomething()'s throws list
    // IllegalArgumentException is NOT reported because it is not a checked exception
    // SqlException IS reported because it is not is in the throws list
    Mockito.doThrow(IOException.class, IllegalArgumentException.class, SqlException.class).when(mock).doSomething();
}

class MockObject {
    void doSomething() throws IOException, ClassNotFoundException {
    }
}
```
