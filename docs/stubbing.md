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

## Consecutive `*Return()` calls can be merged

![](https://img.shields.io/badge/inspection-orange) ![](https://img.shields.io/badge/since-0.3.0-blue) [![](https://img.shields.io/badge/implementation-SimplifyConsecutiveStubbingCallsInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/SimplifyConsecutiveStubbingCallsInspection.java)

Reports multiple consecutive calls to `*Return()` methods, so that they may be merge into a single call.
   
Both `org.mockito.Mockito` and `org.mockito.BDDMockito` based stubbing chains are supported, including calls to `doReturn()`, `thenReturn()` and `willReturn()`.

If there are multiple sections of consecutive calls within the same call chain, they are reported separately for better notification,
but upon invoking the quick fix, all sections are merged respectively. It is always the last consecutive call that is registered.

![consecutive_return_calls](assets/consecutive_return_calls.png)

**Examples:**

```java
From: Mockito.when(mockObject.invoke()).thenReturn(1).thenReturn(2)
  to: Mockito.when(mockObject.invoke()).thenReturn(1, 2);

From: Mockito.when(mockObject.invoke()).thenReturn(1).thenCallRealMethod().thenReturn(2).thenReturn(3);
  to: Mockito.when(mockObject.invoke()).thenReturn(1).thenCallRealMethod().thenReturn(2, 3);

From: Mockito.when(mockObject.invoke()).thenReturn(1).thenReturn(2).thenCallRealMethod().thenReturn(3);
  to: Mockito.when(mockObject.invoke()).thenReturn(1, 2).thenCallRealMethod().thenReturn(3);

From: Mockito.when(mockObject.invoke()).thenReturn(1).thenReturn(2).thenCallRealMethod().thenReturn(3).thenReturn(4);
  to: Mockito.when(mockObject.invoke()).thenReturn(1, 2).thenCallRealMethod().thenReturn(3, 4);

From: Mockito.when(mockObject.invoke()).thenReturn(1, 2, 3).thenReturn(4).thenCallRealMethod().thenReturn(5).thenReturn(6, 7);
  to: Mockito.when(mockObject.invoke()).thenReturn(1, 2, 3, 4).thenCallRealMethod().thenReturn(5, 6, 7);
```
