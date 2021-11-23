# Framework integration

This document provides information on plugin features that are designed for Mockito framework integration, rather than
for Mockito features used by everyday users.

All these features are disabled by default, and they have to be enabled manually in case of Mockito integration.

## Class or interface extends class or interface annotated as @NotExtensible

![](https://img.shields.io/badge/since-0.1.0-blue) [![](https://img.shields.io/badge/implementation-CallOnMockitoResetInspection-blue)](../src/main/java/com/picimako/mockitools/inspection/CallOnMockitoResetInspection.java)

According to the javadoc of the [`@NotExtensible`](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/NotExtensible.html) annotation, it

> Indicates to the user that she should not provide custom implementations of given type.

```java
import org.mockito.MockSettings;
import java.util.List;

//MockSettings is annotated as @NotExtensible, so this interface is reported by the inspection.
interface SomeInterface extends MockSettings, List<String> {
}
```

The inspection is applied to interfaces and named classes that can inherit from other types.
