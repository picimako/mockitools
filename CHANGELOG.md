<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Mockitools Changelog

## [Unreleased]

## [0.12.0]
### Added
- [#52](https://github.com/picimako/mockitools/issues/52): Added code completion for parameters of methods in the `Mockito` class that need mock objects to be passed in (e.g. `reset()` or `verifyNoMoreInteractions()`).
The list only shows `@Mock`, `@Spy` and `@InjectMocks` annotated fields, as well as local variables created with `Mockito.mock()` or `Mockito.spy()`.

### Fixed
- Fixed an issue that stubbed checked exceptions were marked mistakenly as having no matching checked exception in the stubbed method's `throws` clause,
even if the throws clause contained `Exception` or `Throwable`.

## [0.11.0]
### Added
- [#53](https://github.com/picimako/mockitools/issues/53): Added an inspection to report when the mocked type and the type of the spied instance don't match in a `mock(Type.class, withSettings().spiedInstance(...));`-type mock creation.
- [#56](https://github.com/picimako/mockitools/issues/56): Added an inspection to report spying on mock objects, i.e. `Mockito.spy(Mockito.mock(...))` and `Mockito.spy(<object annotated with @Mock>)`.
- [#57](https://github.com/picimako/mockitools/issues/57): Added an inspection to report arguments passed into `Mockito.mock()` and `Mockito.spy()` which are designed to determine the mock type based on
the variable's type they are assigned to, and not by the type passed into them. See [Mocking/spying without specifying class](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#54).
- [#58](https://github.com/picimako/mockitools/issues/58): Added an inspection to report `MockSettings`-based mock creations that have convenience methods or simpler variants to create the mock.
- [#59](https://github.com/picimako/mockitools/issues/59): Added an intention action to expand simple mock and spy creation calls to use specific `MockSettings`,
for example `Mockito.mock(<type>, <answer>)` is converted to `Mockito.mock(<type>, Mockito.withSettings().defaultAnswer(<answer>))`.

### Changed 
- Plugin configuration updates, and code improvements specific to migrating from Java 11 to 17.

## [0.10.0]
### Changed
- New support range of IDEs: 2022.2-2023.2
- Disabled the v3 to v4 migration aids inspections since Mockito 4.x has already been released for 1.5 years.
- [#49](https://github.com/picimako/mockitools/issues/49): Added support for the `withoutAnnotations` attribute of the `@Mock` annotation.
- Underlying plugin configuration updates, and test optimizations.
- Update inspection description documents to enable code snippet syntax highlighting in them starting from IJ 2023.2.

## [0.9.0]
### Changed
- Dropped support for IJ-2021.3.
- Added support for IJ-2023.1.
- Minor simplifications under the hood.

## [0.8.0]
### Added
- [#37](https://github.com/picimako/mockitools/issues/37): Added inspection to report stub-only mocks when they are used verifications.
- [#43](https://github.com/picimako/mockitools/issues/43): Added support for converting the `mockMaker` mock setting between the `@Mock` annotation and `MockSettings`.
- Added missing support for the conversion of the `strictness` mock setting from `@Mock` to `MockSettings`.

### Changed
- Dropped support for IJ-2021.2.
- Added support for IJ-2022.3 EAPs.

## [0.7.0]
### Added
- [#36](https://github.com/picimako/mockitools/issues/36): Added inspection to report `toString()` calls in verifications, since Mockito cannot verify `toString()`.
- [#39](https://github.com/picimako/mockitools/issues/39): Added inspection to report `*Return()` stubbing calls, if the stubbed method's return type is void.
- [#40](https://github.com/picimako/mockitools/issues/40): Added inspection to report `doNothing()` and `willDoNothing()` stubbing calls,
  if the stubbed method's return type is not void.

### Changed
- [#41](https://github.com/picimako/mockitools/issues/41): Cleaned up the class and package name, and modified the icons in the target method selection
  popup during mock field conversion. The new icons can help distinguish between before hooks, test methods, and the rest of the methods.
- Refactorings and housekeeping in the mock stubbing and verification areas.

## [0.6.0]
### Added
- [#29](https://github.com/picimako/mockitools/issues/29): Added intentions to convert verifications between `InOrder.verify()` and `MockedStatic.verify()`.
- [#30](https://github.com/picimako/mockitools/issues/30): Added inspection reporting for usages of `MockedStatic.reset()`.
- [#32](https://github.com/picimako/mockitools/issues/32): Added support for `MockedStatic` stubbing to report mergeable `*Return()` and `*Throw()` method calls.
- [#33](https://github.com/picimako/mockitools/issues/33): Added bulk conversion for stubbing call chains. It works the same way as bulk conversion works for verifications.

### Changed
- [#31](https://github.com/picimako/mockitools/issues/31): Modified the analysis process of `times(1)` calls to specifically check for the verification methods they are used at.
  Also optimized the whole `times(X)` analysis.
- [#33](https://github.com/picimako/mockitools/issues/33): Reorganized the stubbing conversion intention actions.
  Now, only the **Convert stubbing(s) to...** intention is displayed, and after selecting it, the actual target options become visible.

### Fixed
- Fixed an issue when the editor selection was suitable enough, but it didn't actually contain a statement, the stubbing and verification
  intentions were still available.

## [0.5.0]
### Added
- [#19](https://github.com/picimako/mockitools/issues/19): Added bulk options to convert verifications from `BDDMockito.then()` and `Mockito.verify()` by selecting one or more
  verifications in the editor.
- [#20](https://github.com/picimako/mockitools/issues/20): Extended the conversion of `InOrder.verify()` call chains with a bulk mode,
so that if multiple (or just a single) such call chain is selected/highlighted in the editor, they all can be converted to the same approach at once.
- [#21](https://github.com/picimako/mockitools/issues/21): Added an inspection to report InOrder local variables that are used in only one verification call.
Besides `MockedStatic`, it can report for both `InOrder.verify()` and `BDDMockito.then().should(InOrder)` verifications.
- [#22](https://github.com/picimako/mockitools/issues/22): Added a new action to convert from and to `InOrder.verify()` based verification.
Along with it, extended the possible conversion options from and to `Mockito.verify()` and `BDDMockito.then()` as well.
Details are available in the [Verifications](https://github.com/picimako/mockitools/blob/main/docs/verifications.md) document.
- [#23](https://github.com/picimako/mockitools/issues/23): Added a quick fix, so that checked exceptions can be added to the stubbed method's `throws` clause 
when they are specified in the stubbing, but not in the mentioned `throws` clause.
- [#24](https://github.com/picimako/mockitools/issues/24): Strictness configuration is now supported by the conversion between `@Mock` and `Mockito.mock(Class, MockSettings)` in both directions.
- `EnforceConventionInspection` can now report `InOrder.verify()` calls too, as part of enforcing `BDDMockito.then()` verification.
- [#27](https://github.com/picimako/mockitools/issues/27): Added an inspection that can report mock objects used in misconfigured `InOrder` verifications.

### Changed
- Removed support for IJ2021.1. From now on 2021.2 is the earliest version supported.

### Fixed
- Fixed an issue that when converting from `BDDMockito.then().should(InOrder)` to `Mockito.verify()` the InOrder variable remained.

## [0.4.1]
### Changed
- Added support for IJ2022.2.
- Removed **Mockitools:** prefix from inspection messages. This might help make them more comprehensible, and it removes
  the clutter when they are displayed in the code analysis results tool window.
- Refactored some intention classes to prevent EDT slow operations errors, and other housekeeping.

## [0.4.0]
### Added
- [#4](https://github.com/picimako/mockitools/issues/4): Added an intention action that can convert `Mockito.mock()` calls to `@Mock` annotated fields.
- [#8](https://github.com/picimako/mockitools/issues/8): Added a new inspection that reports multiple consecutive calls on `*Throw()` stubbing calls.
They can be merged into a single such call.
- [#8](https://github.com/picimako/mockitools/issues/8): Merging of consecutive `*Return()` and `*Throw()` stubbing calls can happen separately if
there are multiple such sections within a stubbing call chain. They, from now on, also keep line wrapping and indentation after applying the quick fix.
- [#12](https://github.com/picimako/mockitools/issues/12): Added a new intention action that can convert arguments of `*Throw()` stubbing methods
from `Type.class` expressions to `new Type()` expressions and vice versa.
- [#13](https://github.com/picimako/mockitools/issues/13): Added 4 new intention actions that can convert between the various stubbing approaches.
- [#15](https://github.com/picimako/mockitools/issues/15): Added 2 new intention actions that can convert between the `Mockito.verify()`
and `BDDMockito.then()` approaches, and vice versa.
- [#16](https://github.com/picimako/mockitools/issues/16): Added a new inspection that can enforce `org.mockito.Mockito` or `org.mockito.BDDMockito` based
stubbing and verification.

## [0.3.0]
- **IntelliJ version support: versions prior to 2021.1 are no longer supported.**

### Added
- Added a new inspection to report checked exceptions in `*Throw()` stubbing calls that are not specified in the stubbed method's `throws` clause.
See [documentation](/docs/stubbing.md#invalid-checked-exception-is-passed-into-throw-methods).
- [#8](https://github.com/picimako/mockitools/issues/8): Added a new inspection that reports multiple consecutive calls on `*Return()` stubbing calls.
These can be merged into a single such call. See [documentation](docs/stubbing.md#consecutive-return-and-throw-calls-can-be-merged).

### Changed
- Updated Gradle IntelliJ plugin to 1.4.0, gradle to 7.4, and qodana-action to 4.2.5.
- Replaced unit test file checks with a less restrictive, test source root content check, because unit test file names don't necessarily end with the word *Test*.
  This will allow certain functionality to run in files whose names don't end with *Test*.
  
## [0.2.0]
### Added
- [#2](https://github.com/picimako/mockitools/issues/2): Extended `MockTypeInspection` to validate and report types annotated with `@DoNotMock` annotation.
  - You can find more information about this at
    - [MockTypeInspection documentation](docs/mock_creation.md#donotmock-annotated-types) 
    - [@DoNotMock javadoc](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/DoNotMock.html)
    - [Mockito pull request: Add annotation to mark a type as DoNotMock](https://github.com/mockito/mockito/pull/1833/files)
- Extended `MockTypeInspection` to validate new expressions (i.e. `new Clazz()`) against non-mockable types.
- [#4](https://github.com/picimako/mockitools/issues/4): Added intention to convert `Mockito.spy()` calls to `@Spy` annotated fields.
See [documentation](docs/mock_creation.md#convert-mockitomockspy-calls-to-mockspy-fields).
- [#4](https://github.com/picimako/mockitools/issues/4): Added intention to convert `@Mock` and `@Spy` annotated fields to `Mockito.mock`  and `Mockito.spy()`
calls, respectively. See [documentation](docs/mock_creation.md#convert-mockspy-fields-to-mockitomockspy-calls).

### Changed
- Updated gradle-intellij-plugin version to 1.3.1.
- Updated github workflows configuration to match the changes in the IJ platform plugin template.
- Plugin is now compatible with IJ 2022.1 EAP.

## [0.1.1]
### Added
- Plugin is now compatible with IJ 2021.3.
- Upgraded gradle to 7.3 and some library versions.

## [0.1.0]
### Added
- Initial set of inspections for Mockito, including migration aids for Mockito 4.0.0.
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)