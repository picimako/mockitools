# Mockito verifications

## Verification mode arguments must be between limits

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-VerificationModeValuesBetweenLimitsInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/VerificationModeValuesBetweenLimitsInspection.java)

This inspection validates the followin time and occurrence based methods for `VerificationMode` whether their arguments are out of the allowed bounds:
`Mockito.times`, `Mockito.atLeast`, `Mockito.atMost`, `Mockito.calls`, `Mockito.after`, `Mockito.timeout`

None of these calls are allowed negative values as arguments. Additionally, `Mockito.calls()` doesn't allow 0 as argument either,
and `Mockito.timeout()` doesn't allow values above a user-defined threshold (with 5000 as its default value - see inspection settings).

The following are all non-compliant examples:

```java
Mockito.verify(mockObject, times(-10)).method(); //negative value
Mockito.verify(mockObject, atLeast(-10)).method(); //negative value
Mockito.verify(mockObject, atMost(-10)).method(); //negative value

InOrder inOrder = Mockito.inOrder(mockObject);
inOrder.verify(mockObject, calls(-10)).method(); //negative value
inOrder.verify(mockObject, calls(0)).method(); //0 is not allowed either for calls

Mockito.verify(mockObject, after(-1000).never()).method(); //negative value
Mockito.verify(mockObject, timeout(-1000)).method(); //negative value
Mockito.verify(mockObject, timeout(6000)).method(); //over threshold, since 5000 is the default value
```

## Mockito.times(0) and Mockito.times(1) calls may be optimized or removed

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-TimesVerificationModeInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/TimesVerificationModeInspection.java)

This inspection reports `Mockito.times()` calls whether they can be optimized or deleted based on their argument values.

- `times(0)` calls can be replaced with `Mockito.never()` for better readability,
- `times(1)` calls can be removed since 1 is the default value when using `times()` verification. See [Mockito exact verification](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#exact_verification) documentation.
  - `Mockito.times(1)` calls are reported only when they have no subsequent calls on them, thus for instance `Mockito.times(1).description("message")` would not be reported.

Both of them can be enabled/disabled on the inspection's settings panel.

Quick fixes are also provided for the replacement and removal of these calls.

```java
From: Mockito.verify(mock, times(1))... //times(1) can be omitted
  to: Mockito.verify(mock)...
    
Mockito.verify(mock, times(1).description("message"))... //no quick fix, left untouched

From: Mockito.verify(mock, times(0))... //times(0) can be replaced with never()
  to: Mockito.verify(mock, never())...

From: Mockito.verify(mock, times(0).description("message"))... //times(0) can be replaced with never()
  to: Mockito.verify(mock, never().description("message"))...
```

## No method call argument is provided

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-NoMethodCallArgumentSpecifiedInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/NoMethodCallArgumentSpecifiedInspection.java)

There are a couple of methods in Mockito that must be passed at least one argument (usually these methods take varargs as argument),
so this inspection reports when there is no argument specified in those calls:

- `Mockito.withSettings().extraInterfaces()`
- `Mockito.verifyNoInteractions()`
- `Mockito.verifyNoMoreInteractions()`
- `Mockito.verifyZeroInteractions()`
- `Mockito.ignoreStubs()`
- `Mockito.inOrder()`

The following are all non-compliant examples:

```java
@Test
void testMethod() {
    Object mock = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces());
    Mockito.verifyNoInteractions();
    Mockito.verifyNoMoreInteractions();
    Mockito.verifyZeroInteractions();
    Mockito.ignoreStubs();  //marked with only warning level since this wouldn't block test execution
    Mockito.inOrder();
}
```

## Convert between various verification approaches

![](https://img.shields.io/badge/intention-orange) ![](https://img.shields.io/badge/since-0.4.0-blue)
[![](https://img.shields.io/badge/impl-ConvertMockitoVerifyToBDDMockitoThenIntention-blue)](../src/main/java/com/picimako/mockitools/intention/convert/verification/ConvertMockitoVerifyToBDDMockitoThenIntention.java)
[![](https://img.shields.io/badge/impl-ConvertBDDMockitoThenToMockitoVerifyIntention-blue)](../src/main/java/com/picimako/mockitools/intention/convert/verification/ConvertBDDMockitoThenToMockitoVerifyIntention.java)

There are a couple of ways one can approach verification in Mockito: via `org.mockito.Mockito` and `org.mockito.BDDMockito`.

These intentions can convert between the `Mockito.verify()` and `BDDMockito.then()` call chains if they satisfy the following criteria:
- if the ['Enforce conventions' inspection](conventions.md#enforce-orgmockitomockito-over-orgmockitobddmockito-and-vice-versa) doesn't enforce
the verification the user converts from,
- in case of `Mockito.verify()`, a call on the mock object after `verify()` must be present,
- while in case of `BDDMockito.then()`, both the `should()` call and a call on the mock object after that must be present.

Conversion of `InOrder` verification is not supported at the moment.

**Examples for Mockito.verify() -> BDDMockito.then() direction:**

```java
//Without verification mode
From: Mockito.verify(mock).doSomething();
  to: BDDMockito.then(mock).should().doSomething();

//With verification mode
From: Mockito.verify(mock, times(2)).doSomething();
  to: BDDMockito.then(mock).should(times(2)).doSomething();
```
