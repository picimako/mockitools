# Project Conventions

### Enforce org.mockito.Mockito over org.mockito.BDDMockito and vice versa

![](https://img.shields.io/badge/inspection-orange) ![](https://img.shields.io/badge/since-0.4.0-blue)
[![](https://img.shields.io/badge/implementation-EnforceConventionInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/stubbing/EnforceConventionInspection.java)

Projects might want to enforce certain coding conventions for Mockito. This inspection helps with that.

It reports calls to static stubbing and verification methods of `org.mockito.Mockito`, if `org.mockito.BDDMockito`
based stubbing and verification must be used, and vice versa.
Whether to enforce one or the other can be configured in the inspection settings.

Notes:
- It is not enabled by default since a project might not want to enforce these conventions.
- There are no dedicated quick fixes, since separate intention actions are available to convert between these approaches:
  - [conversion of stubbing](stubbing.md#convert-between-various-stubbing-approaches)
  - [conversion of verification](verifications.md#convert-between-various-verification-approaches)
- `MockedStatic` specific `InOrder.verify()` methods are excluded from the enforcement, since BDDMockito has no way of verifying MockedStatic.

**Example:**

Let's say we want to enforce `org.mockito.BDDMockito` based calls, then the following calls would all be reported: 

```java
//stubbing
Mockito.when(mockObject.doSomething()).thenReturn(10);
Mockito.doReturn(10).when(mockObject).doSomething();
Mockito.doThrow(IllegalArgumentException.class).when(mockObject).doSomething();
Mockito.doAnswer(Answers.CALLS_REAL_METHODS).when(mockObject).doSomething();
Mockito.doCallRealMethod().when(mockObject).doSomething();
Mockito.doNothing().when(mockObject).voidMethod();

//verification
Mockito.verify(mockObject, Mockito.times(2)).doSomething();
Mockito.verifyNoMoreInteractions(mockObject);
Mockito.verifyNoInteractions(mockObject);
Mockito.verifyZeroInteractions(mockObject); //only in Mockito 3
```
