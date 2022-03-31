# Stubbing

## Invalid checked exception is passed into *Throw() methods

![](https://img.shields.io/badge/inspection-orange) ![](https://img.shields.io/badge/since-0.3.0-blue) [![](https://img.shields.io/badge/implementation-ThrowsCheckedExceptionStubbingInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/ThrowsCheckedExceptionStubbingInspection.java)

Reports exception references in <code>*Throw()</code> stubbing methods based on Mockito's rule on checked exceptions.
*If [the specified exception types] contain checked exceptions then they have to match one of the checked exceptions in the method signature.*
   
The following constructs are supported:
- `Mockito.when().thenThrow(...)`
- `BDDMockito.given().willThrow(...)`
- `Mockito.doThrow(...).when()`
- `BDDMockito.willThrow(...).given()`
   
In case of each way of stubbing, further chained <code>*Throw()</code> calls are supported too. In case of an empty list, no problem is reported.

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

## Consecutive `*Return()` and `*Throw()` calls can be merged

![](https://img.shields.io/badge/inspection-orange)

![](https://img.shields.io/badge/since-0.3.0-blue) [![](https://img.shields.io/badge/implementation-SimplifyConsecutiveStubbingCallsInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/consecutive/SimplifyConsecutiveStubbingCallsInspection.java)

![](https://img.shields.io/badge/since-0.4.0-blue) [![](https://img.shields.io/badge/implementation-SimplifyConsecutiveThrowCallsInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/consecutive/SimplifyConsecutiveThrowCallsInspection.java)

Reports multiple consecutive calls to `*Return()` and `*Throw()` methods, respectively, so that they may be merged into a single call.
   
Both `org.mockito.Mockito` and `org.mockito.BDDMockito` based stubbing chains are supported, including calls to
- `doReturn()`, `thenReturn()` and `willReturn()`,
- `doThrow`, `thenThrow()` and `willThrow()`.

If there are multiple sections of consecutive calls within the same call chain, they are reported separately for better notification,
but upon invoking the quick fix, all sections are merged respectively. It is always the last consecutive call that is registered.

### Return examples

![consecutive_return_calls](assets/consecutive_return_calls.png)

```java
From: Mockito.when(mockObject.invoke()).thenReturn(1).thenReturn(2);
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

### Throw examples

When merging `*Throw()` calls, there are a few more cases than in case of `*Return()` ones.

| Argument type combination                               | Quick fix available                                                                                | Type after conversion                      |
|---------------------------------------------------------|----------------------------------------------------------------------------------------------------|--------------------------------------------|
| `Class` + `Class`                                       | Merge *Throw calls                                                                                 | `Class`                                    |
| `Throwable` + `Throwable`                               | Merge *Throw calls                                                                                 | `Throwable`                                |
| `Class` + 'new' expression with default constructor     | Merge calls, convert parameters to Class objects<br/>Merge calls, convert parameters to Throwables | `Class`<br/>`Throwable`                    |
| `Class` + 'new' expression with non-default constructor | Merge *Throw calls                                                                                 | `Throwable` to keep constructor parameters |

```java
From: Mockito.when(mockObject.invoke()).thenThrow(IllegalArgumentException.class).thenThrow(IOException.class);
  to: Mockito.when(mockObject.invoke()).thenThrow(IllegalArgumentException.class, IOException.class);

From: Mockito.when(mockObject.invoke()).thenThrow(new IllegalArgumentException()).thenThrow(new IOException());
  to: Mockito.when(mockObject.invoke()).thenThrow(new IllegalArgumentException(), new IOException());

From: Mockito.when(mockObject.invoke()).thenThrow(IllegalArgumentException.class).thenThrow(new IOException());
  to (when selecting conversion to Classes):    Mockito.when(mockObject.invoke()).thenThrow(IllegalArgumentException.class, IOException.class);
  to (when selecting conversion to Throwables): Mockito.when(mockObject.invoke()).thenThrow(new IllegalArgumentException(), new IOException());

From: Mockito.when(mockObject.invoke()).thenThrow(IllegalArgumentException.class).thenThrow(new IOException("reason"));
  to: Mockito.when(mockObject.invoke()).thenThrow(new IllegalArgumentException(), new IOException("reason"));
```
