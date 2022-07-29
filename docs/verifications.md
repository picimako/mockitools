# Mockito verifications

<!-- TOC -->
* [Verification mode arguments must be between limits](#verification-mode-arguments-must-be-between-limits)
* [Mockito.times(0) and Mockito.times(1) calls may be optimized or removed](#mockitotimes0-and-mockitotimes1-calls-may-be-optimized-or-removed)
* [No method call argument is provided](#no-method-call-argument-is-provided)
* [InOrder with a single verification](#inorder-with-a-single-verification)
* [Misconfigured InOrder verifications](#misconfigured-inorder-verifications)
* [Convert between various verification approaches](#convert-between-various-verification-approaches)
<!-- TOC -->

## Verification mode arguments must be between limits

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-VerificationModeValuesBetweenLimitsInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/VerificationModeValuesBetweenLimitsInspection.java)

This inspection validates the following time- and occurrence based methods for `VerificationMode` whether their arguments are out of the allowed bounds:
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

- `times(0)` can be replaced with `Mockito.never()` for better readability,
- `times(1)` can be removed since 1 is the default value when using `times()` verification. See [Mockito exact verification](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#exact_verification) documentation.
  - `Mockito.times(1)` calls are reported only when they have no sequent calls on them, thus `Mockito.times(1).description("message")` would not be reported.

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

## InOrder with a single verification

![](https://img.shields.io/badge/inspection-orange) ![](https://img.shields.io/badge/since-0.5.0-blue)
[![](https://img.shields.io/badge/impl-SingleInOrderVerificationInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/verification/SingleInOrderVerificationInspection.java)

This inspection reports `InOrder` local variables on which only one verification is called in the form of either
an `InOrder.verify()` or a `BDDMockito.then().should(InOrder)` call.

The report can be useful when someone starts implementing an `InOrder` verification to remind them that further verifications need to be implemented,
and also cases when:
- the user forgot to add further verification calls,
- a verification started out as `InOrder` but was forgotten to be converted to simple verification when he/she changed his/her mind

```java
InOrder inOrder = Mockito.inOrder(mock); //the variable name is highlighted
inOrder.verify(mock).doSomething();
```

## Misconfigured InOrder verifications

![](https://img.shields.io/badge/inspection-orange) ![](https://img.shields.io/badge/since-0.5.0-blue)
[![](https://img.shields.io/badge/impl-UnusedOrUnconfiguredMockInInOrderVerificationInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/verification/UnusedOrUnconfiguredMockInInOrderVerificationInspection.java)

This inspection reports mock objects in `InOrder` verifications in the following cases:

- The mock is added to the arguments of `Mockito.inOrder()` but is not used in any verification performed via that `InOrder` object.
  - `Type.class`-type arguments are excluded for MockedStatic verifications
- The mock is used in an `InOrder` verification, but it is not added to the arguments of `Mockito.inOrder()`.

It can report mocks both in `InOrder.verify()` and `BDDMockito.then().should(InOrder)`.

```java
InOrder unusedMock = Mockito.inOrder(mock, mock2); //mock2 is reported since it is not used in any of the verifications
unusedMock.verify(mock).doSomething();
unusedMock.verify(mock, Mockito.times(2)).doSomething();

InOrder unconfiguredMock = Mockito.inOrder(mock);
unconfiguredMock.verify(mock).doSomething();
unconfiguredMock.verify(mock2).doSomething(); //mock2 is reported since it is not added to 'Mockito.inOrder()'
```

## Convert between various verification approaches

![](https://img.shields.io/badge/intention-orange)

![](https://img.shields.io/badge/since-0.4.0/0.5.0/0.6.0-blue)
[![](https://img.shields.io/badge/impl-ConvertMockitoVerifyToBDDMockitoThenIntention-blue)](../src/main/java/com/picimako/mockitools/intention/convert/verification/mockitoverify/ConvertFromMockitoVerifyIntention.java)
[![](https://img.shields.io/badge/impl-ConvertBDDMockitoThenToMockitoVerifyIntention-blue)](../src/main/java/com/picimako/mockitools/intention/convert/verification/bddmockitothen/ConvertFromBDDMockitoThenIntention.java)
[![](https://img.shields.io/badge/impl-ConvertBDDMockitoThenToMockitoVerifyIntention-blue)](../src/main/java/com/picimako/mockitools/intention/convert/verification/inorderverify/ConvertFromInOrderVerifyIntention.java)

![](https://img.shields.io/badge/since-0.6.0-blue) [![](https://img.shields.io/badge/impl-ConvertFromMockedStaticVerifyIntention-blue)](../src/main/java/com/picimako/mockitools/intention/convert/verification/mockedstaticverify/ConvertFromMockedStaticVerifyIntention.java)

There are a couple of ways one can approach verification in Mockito: via `org.mockito.Mockito`, `org.mockito.BDDMockito`, `org.mockito.InOrder`, `org.mockito.MockedStatic`

These intentions can convert between those approaches if they satisfy some preconditions:
- in case of `Mockito.verify()` and `InOrder.verify()`, a call must be present on the mock object after `verify()` ,
- while in case of `BDDMockito.then()`, both the `should()` call and a call on the mock object after that must be present.

Below you can see the details of the conversion directions when converting single verifications. Bulk conversions are handled and detailed separately.

Also, reusing an existing `InOrder` instance is not possible when converting to an `InOrder` specific approach,
it always creates a new `InOrder` local variable.

| Conversion from                       | Options by default                                                                 | Options when `org.mockito.Mockito` is enforced | Options when `org.mockito.BDDMockito` is enforced           |
|---------------------------------------|------------------------------------------------------------------------------------|------------------------------------------------|-------------------------------------------------------------|
| `Mockito.verify()`                    | `InOrder.verify()`<br/>`BDDMockito.then()`<br/>`BDDMockito.then().should(InOrder)` | `InOrder.verify()`                             | `BDDMockito.then()`<br/>`BDDMockito.then().should(InOrder)` |
| `BDDMockito.then()`                   | `Mockito.verify()`<br/>`InOrder.verify()`<br/>`BDDMockito.then().should(InOrder)`  | `Mockito.verify()`<br/>`InOrder.verify()`      | `BDDMockito.then().should(InOrder)`                         |
| `BDDMockito.then().should(InOrder)`   | `Mockito.verify()`<br/>`InOrder.verify()`                                          | `Mockito.verify()`<br/>`InOrder.verify()`      | No action is available.                                     |
| `InOrder.verify()` (non-MockedStatic) | `Mockito.verify()`<br/>`BDDMockito.then()`<br/>`BDDMockito.then().should(InOrder)` | `Mockito.verify()`                             | `BDDMockito.then()`<br/>`BDDMockito.then().should(InOrder)` |
| `InOrder.verify()` (MockedStatic)     | `MockedStatic.verify()`                                                            | `MockedStatic.verify()`                        | `MockedStatic.verify()`                                     |
| `MockedStatic.verify()`               | `InOrder.verify()`                                                                 | `InOrder.verify()`                             | `InOrder.verify()`                                          |

**Example (Mockito.verify() -> BDDMockito.then()):**

```java
//Without verification mode
From: Mockito.verify(mock).doSomething();
  to: BDDMockito.then(mock).should().doSomething();

//With verification mode
From: Mockito.verify(mock, times(2)).doSomething();
  to: BDDMockito.then(mock).should(times(2)).doSomething();
```

**Example (BDDMockito.then() -> InOrder.verify()):**

```java
From: BDDMockito.then(mock).should().doSomething();
to:
      InOrder inOrder = Mockito.inOrder(mock);
      inOrder.verify(mock).doSomething();
```

### Selection based conversion

![](https://img.shields.io/badge/since-0.5.0-blue)

Conversion of one or more verification call chains is also available via selection in the editor.
It can convert between `org.mockito.Mockito`, `org.mockito.BDDMockito` and `org.mockito.InOrder` in any direction, with some nuances that should be taken into consideration.

The availability is the same, while the conversion logic is mostly the same, as for the single conversion options:
- when converting from `BDDMockito.then()`
  - `InOrder.verify()` as a target is available only when **all** `BDDMockito.then()` chains in the selection use an `InOrder` variable, and they use the same one,
  - adding an `InOrder` to the `should()` call is available only when **none** of the `BDDMockito.then()` chains use an `InOrder` variable.
- when a new `InOrder` local variable is created, it is used in all selected and converted verifications. If you want to use different ones for different verifications,
you can convert them one by one.

#### Examples

Selections are between [\[ and ]].

**InOrder.verify() -> BDDMockito.then()**

```java
From:
      InOrder inOrder = Mockito.inOrder(mock, mock2);
      [[inOrder.verify(mock).doSomething();
      inOrder.verify(mock2, times(2)).doSomething();]]
to:
      InOrder inOrder = Mockito.inOrder(mock, mock2);
      BDDMockito.then(mock).should().doSomething();
      BDDMockito.then(mock2).should(times(2)).doSomething();
```

**Mockito.verify() -> InOrder.verify()**

```java
From:
      [[Mockito.verify(mock).doSomething();
      Mockito.verify(mock2, times(2)).doSomething();]]
to:
      InOrder inOrder = Mockito.inOrder(mock, mock2);
      inOrder.verify(mock).doSomething();
      inOrder.verify(mock2, times(2)).doSomething();
```
