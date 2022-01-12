# Mock creation

## Non-interface type(s) passed into extraInterfaces

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-ExtraInterfacesInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/ExtraInterfacesInspection.java)

The `extraInterfaces` functionality of Mockito has some criteria in order to create proper mocks.

In these cases Mockito would stop test execution and fail with an exception letting you know about one of these issues.

**Types specified in the `@Mock` annotation's `extraInterfaces` attribute must all be interfaces**

```java
@Mock(extraInterfaces = {List.class, Set.class, Object.class}) //Object is not an interface
public Object mock;
```

**Types specified in `Mockito.withSettings().extraInterfaces()` must be interfaces**

```java
//None of the arguments is an interface
Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(Object.class, ArrayList.class, AbstractMap.class));
```

The respective Mockito exceptions are thrown in Mockito's [Reporter.java](https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java),
look for the `extraInterfacesAcceptsOnlyInterfaces(Class)` method.

## No argument is provided for the extraInterfaces() call

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-ExtraInterfacesInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/ExtraInterfacesInspection.java)

One of the criteria for the `extraInterfaces()` call is that it must be provided at least one argument.
In this case Mockito would stop test execution and fail with an exception letting you know about the problem.

```java
Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces()); //no argument specified
```

You can find the related Mockito exception handling in its [Reporter.java](https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/exceptions/Reporter.java),
look for the `extraInterfacesRequiresAtLeastOneInterface()` method.

## Mockito cannot mock certain types

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-MockTypeInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/MockTypeInspection.java)

Based on the logic in Mockito's [InlineDelegateByteBuddyMockMaker#isTypeMockable(Class)](https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineDelegateByteBuddyMockMaker.java) method
and [InlineBytecodeGenerator#EXCLUDES](https://github.com/mockito/mockito/blob/main/src/main/java/org/mockito/internal/creation/bytebuddy/InlineBytecodeGenerator.java) field,
when attempting to create mocks for primitives, primitive wrapper types, `String` and `Class`, Mockito throws an exception that mock creation cannot happen.

This inspection validates `@Mock` and `@Spy` annotated fields' types and the types specified as the arguments of `Mockito.mock()` and `Mockito.spy()` calls.
The following examples are all non-compliant ones:

```java
class MockTypesTest {
    @Mock
    String mock;

    @Spy
    int spy; //primitive

    @Test
    public void shouldInspectMockTypes() {
        Mockito.mock(Short.class); //wrapper
        Mockito.spy(Class.class);
        Mockito.mock(String.class, Mockito.withSettings().name("name"));
    }
}
```

### @DoNotMock annotated types

![](https://img.shields.io/badge/since-0.2.0-blue)

In Mockito 4.1.0 a new `@DoNotMock` annotation was introduced adopted from Google. It has a `reason` attribute to inform users why the type marked with the annotation should not be mocked.

This inspection also marks types in whose type hierarchy there is at least one type annotated with either Mockito's `org.mockito.DoNotMock` annotation or any custom annotation whose fully qulified name
ends with `org.mockito.DoNotMock`.

When constructing the inspection message, the inspection looks for the annotation's `reason` attribute value.

```java
class MockTypesTest {
    @Mock
    NotMockable mock; //message: ... The reason: Create a real instance instead.
    @Mock
    NotMockableWithCustomReason mockCustom; //message: ... The reason: You are doing it wrong.
    @Mock
    NotMockableWithEmptyReason mockEmpty; //message: ... No reason provided.

    @DoNotMock
    private static class NotMockable {
    }

    @DoNotMock(reason = "You are doing it wrong.")
    private static class NotMockableWithCustomReason {
    }

    @DoNotMock(reason = "") //This is just for demonstration purposes. Either specify an actual reason or use the default one if the annotation has one.
    private static class NotMockableWithEmptyReason {
    }
}
```

Additional resources:
- [@DoNotMock javadoc](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/DoNotMock.html)
- [Mockito pull request: Add annotation to mark a type as DoNotMock](https://github.com/mockito/mockito/pull/1833/files)

## Mockito.reset() is used

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-CallOnMockitoResetInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/CallOnMockitoResetInspection.java)

Based on Mockito's documentation on [resetting mocks](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#resetting_mocks)

> Smart Mockito users hardly use this feature because they know it could be a sign of poor tests. Normally, you don't need to reset your mocks, just create new mocks for each test method.

This inspection will report any call to `Mockito.reset()`, regardless of its location in the test method.
Although there may be cases when calling `reset()` is acceptable or even necessary, this inspection doesn't take into account those cases.

```java
@Test
void testMethod() {
    //mock setup
    MockObject mock = Mockito.mock(MockObject.class);
    when(mock.doSomething())...
    reset(mock); //reset() is called
    //another mock setup
    when(mock.doSomethingElse())...
}
```

Additional resources:
- [Reflectoring.io - Clean Unit Tests with Mockito](https://reflectoring.io/clean-unit-tests-with-mockito/) (**Avoid Mockito.reset() for Better Unit Tests** section)
- [Stack Exchange - Is this an appropriate use of Mockito's reset method?](https://softwareengineering.stackexchange.com/questions/188299/is-this-an-appropriate-use-of-mockitos-reset-method)
