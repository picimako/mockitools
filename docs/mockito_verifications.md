# Mockito verifications

## Verification mode arguments must be between limits

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-VerificationModeValuesBetweenLimitsInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/VerificationModeValuesBetweenLimitsInspection.java)

This inspection validates certain time and occurrence based methods for `VerificationMode` whether their arguments are out of the allowed bounds.

Supported verification modes: `Mockito.times`, `Mockito.atLeast`, `Mockito.atMost`, `Mockito.calls`, `Mockito.after`, `Mockito.timeout`

None of these calls are allowed negative values as argument. Additionally, `Mockito.calls()` doesn't allow 0 as argument either,
and `Mockito.timeout()` doesn't allow values above a user-defined threshold (with 5000 as its default value - see inspection settings).

The following are non-compliant examples:

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
//before
Mockito.verify(mock, times(1))... //times(1) can be omitted
//after
Mockito.verify(mock)...
    
Mockito.verify(mock, times(1).description("message"))... //no quick fix, left untouched

//before
Mockito.verify(mock, times(0))... //times(0) can be replaced with never()
//after
Mockito.verify(mock, never())...

//before
Mockito.verify(mock, times(0).description("message"))... //times(0) can be replaced with never()
//after
Mockito.verify(mock, never().description("message"))...
```

## No method call argument is provided

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-NoMethodCallArgumentSpecifiedInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/NoMethodCallArgumentSpecifiedInspection.java)

There are a couple of methods in Mockito that must be passed at least one argument (usually these methods take varargs as argument),
so this inspection reports when there is no argument specified in those calls.

Method calls validated:
- `Mockito.withSettings().extraInterfaces()`
- `Mockito.verifyNoInteractions()`
- `Mockito.verifyNoMoreInteractions()`
- `Mockito.verifyZeroInteractions()`
- `Mockito.ignoreStubs()`
- `Mockito.inOrder()`

All the example below are non-compliant ones:

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
