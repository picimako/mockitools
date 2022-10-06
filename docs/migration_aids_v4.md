# Mockito v4 migration aids

<!-- TOC -->
* [Argument matchers are called via org.mockito.Matchers instead of org.mockito.ArgumentMatchers](#argument-matchers-are-called-via-orgmockitomatchers-instead-of-orgmockitoargumentmatchers)
* [Usage of deprecated anyX() matchers](#usage-of-deprecated-anyx-matchers)
* [Usage of parameterized variants of isNull(), isNotNull() and notNull()](#usage-of-parameterized-variants-of-isnull-isnotnull-and-notnull)
* [Usage of deprecated verify methods](#usage-of-deprecated-verify-methods)
* [Usage of deprecated plugins classes](#usage-of-deprecated-plugins-classes)
* [Usage of deprecated JUnit runners](#usage-of-deprecated-junit-runners)
<!-- TOC -->

Similarly to how IntelliJ provides inspections for **Java language level migration aids**, Mockitools also provides
some inspections to help migrate from one version to another; in the context of this document, to 4.0.0.

Check out the [Mockito 4.0.0 release notes](https://github.com/mockito/mockito/releases/tag/v4.0.0) for details.

## Argument matchers are called via org.mockito.Matchers instead of org.mockito.ArgumentMatchers

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-ArgumentMatchersCalledViaMatchersInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/ArgumentMatchersCalledViaMatchersInspection.java)
![](https://img.shields.io/badge/mockito-2.x-orange) ![](https://img.shields.io/badge/mockito-3.x-orange)

Since `org.mockito.Matchers` is removed in 4.0.0, it is only `org.mockito.ArgumentMatchers` that is left to use.

This inspection, besides highlighting matchers called via `Matchers`, also provides a quick fix to replace the call to
be via `ArgumentMatchers`.

```java
//from:
Mockito.when(mock.method(org.mockito.Matchers.anyString())).thenReturn(10);
//to: since Matchers was not referenced via import, ArgumentMatchers is kept that way too
Mockito.when(mock.method(org.mockito.ArgumentMatchers.anyString())).thenReturn(10);
```

```java
//from:
Mockito.when(mock.method(Matchers.anyString())).thenReturn(10);
//to: org.mockito.ArgumentMatchers is also imported, if necessary
Mockito.when(mock.method(ArgumentMatchers.anyString())).thenReturn(10);
```

## Usage of deprecated anyX() matchers

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-UsageOfAnyMatchersInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/UsageOfAnyMatchersInspection.java)
![](https://img.shields.io/badge/mockito-2.x-orange) ![](https://img.shields.io/badge/mockito-3.x-orange)

### anyObject/anyVararg -> any

`anyObject()` and `anyVararg()` are removed in 4.0.0, and it is advised to use `any()` instead.

The related quick fix can replace `anyObject()` and `anyVararg()` with `any()`.

```java
//from:
Mockito.when(mock.method(org.mockito.ArgumentMatchers.anyObject())).thenReturn(10);
Mockito.when(mock.method(ArgumentMatchers.anyVararg())).thenReturn(10);
//to:
Mockito.when(mock.method(org.mockito.ArgumentMatchers.any())).thenReturn(10);
Mockito.when(mock.method(ArgumentMatchers.any())).thenReturn(10);
```

### anyXOf -> anyX

`anyCollectionOf()`, `anyIterableOf()`, `anyListOf()`, `anyMapOf()` and `anySetOf()` are removed in 4.0.0, and it is advised to use their `anyX()` variants instead.

The related quick fix can replace these matchers with their proper `anyX()` variants (`anyCollection()`, `anyIterable()`, etc.).  

```java
//from:
Mockito.when(mock.method(ArgumentMatchers.anyCollectionOf(String.class))).thenReturn(10);
//to:
Mockito.when(mock.method(ArgumentMatchers.anyCollection())).thenReturn(10);
```

### Notes

If a matcher is not used with static import and referenced via `org.mockito.Matchers`, it is also replaced
with the `org.mockito.ArgumentMatchers` qualifier. See the corresponding logic in [ReplaceAnyXOfWithAnyXQuickFix](../src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/UsageOfAnyMatchersInspection.java).

## Usage of parameterized variants of isNull(), isNotNull() and notNull()

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-UsageOfAnyMatchersInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/UsageOfAnyMatchersInspection.java)
![](https://img.shields.io/badge/mockito-2.x-orange) ![](https://img.shields.io/badge/mockito-3.x-orange)

`isNull(Class)`, `isNotNull(Class)` and `notNull(Class)` are removed in 4.0.0, and it is advised to use their non-parameterized variants instead.

The related quick fix removes the method call argument, and updates the matcher to be called from `org.mockito.ArgumentMatchers` instead of `org.mockito.Matchers`, if necessary.

```java
//from:
Mockito.when(mock.method(ArgumentMatchers.isNull(String.class))).thenReturn(10);
Mockito.when(mock.method(ArgumentMatchers.isNotNull(String.class))).thenReturn(10);
Mockito.when(mock.method(ArgumentMatchers.notNull(String.class))).thenReturn(10);
//to:
Mockito.when(mock.method(ArgumentMatchers.isNull())).thenReturn(10);
Mockito.when(mock.method(ArgumentMatchers.isNotNull())).thenReturn(10);
Mockito.when(mock.method(ArgumentMatchers.notNull())).thenReturn(10);
```

## Usage of deprecated verify methods

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-UsageOfDeprecatedVerifyInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/UsageOfDeprecatedVerifyInspection.java)
![](https://img.shields.io/badge/mockito-3.x-orange)

### MockedStatic.verify()
In Mockito v3 there are two `verify()` methods in `MockedStatic` with different signatures.
`MockedStatic.verify(VerificationMode, Verification)` is removed in 4.0.0, thus it is advised to use verify with the other signature.

The related quick fix switches the two arguments of the `verify` call.

```java
//from:
try (MockedStatic<Util> util = Mockito.mockStatic(Util.class)) {
    util.verify(Mockito.times(1), () -> Util.method(List.of()));
}
//to:
try (MockedStatic<Util> util = Mockito.mockStatic(Util.class)) {
    util.verify(() -> Util.method(List.of()), Mockito.times(1));
}
```

### Mockito.verifyZeroInteractions()

`verifyZeroInteractions()` is deprecated since Mockito 3.0.1, and is removed in 4.0.0, thus it is advised to use `verifyNoMoreInteractions()` which it was an alias for.

The related quick fix replaces `verifyZeroInteractions` with `verifyZeroInteractions` keeping all its argument values.

```java
//from:
Mockito.verifyZeroInteractions(mock1, mock2);
//to:
Mockito.verifyNoMoreInteractions(mock1, mock2);
```

## Usage of deprecated plugins classes

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-UsageOfDeprecatedPluginClassesInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/UsageOfDeprecatedPluginClassesInspection.java)
![](https://img.shields.io/badge/mockito-2.x-orange) ![](https://img.shields.io/badge/mockito-3.x-orange)

`org.mockito.configuration.AnnotationEngine` and `org.mockito.plugins.InstantiatorProvider` are deprecated since Mockito v2, and are removed in 4.0.0.

### AnnotationEngine

This quick fix replaces either the whole import statement, if possible, to minimize migration work, or if the reference is qualified, then replaces only that single reference.

```java
//from:
import org.mockito.configuration.AnnotationEngine;

public interface CustomAnnotationEngine extends AnnotationEngine {
}

//to:
import org.mockito.plugins.AnnotationEngine;

public interface CustomAnnotationEngine extends AnnotationEngine {
}
```

### InstantiatorProvider

Due to no name collision (unlike in case of `AnnotationEngine`), it is only a single reference that is replaced by this quick fix.

```java
//from:
import org.mockito.plugins.InstantiatorProvider;

public interface CustomInstantiatorProvider extends InstantiatorProvider {
}

//to:
import org.mockito.plugins.InstantiatorProvider; //this import is left in for now
import org.mockito.plugins.InstantiatorProvider2;

public interface CustomInstantiatorProvider extends InstantiatorProvider2 {
}
```

## Usage of deprecated JUnit runners

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-UsageOfOldJUnitRunnerInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/UsageOfOldJUnitRunnerInspection.java)
![](https://img.shields.io/badge/mockito-2.x-orange) ![](https://img.shields.io/badge/mockito-3.x-orange)

All JUnit runners in the `org.mockito.runners` package, and the package itself are deleted in Mockito 4.0.0, and it is advised to use `org.mockito.junit.MockitJUnitRunner` instead,
to which the related quick fixes replace those runners.

```java
//from:
import org.junit.runner.RunWith;
import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;

@RunWith(ConsoleSpammingMockitoJUnitRunner.class)
public class JUnitRunnerTest {
}

//to:
import org.junit.runner.RunWith;
import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner; //this import is left in for now
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JUnitRunnerTest {
}
```
