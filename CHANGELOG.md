<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Mockitools Changelog

## [Unreleased]

## [0.4.0]
### Added
- [#8](https://github.com/picimako/mockitools/issues/8): Added a new inspection that reports multiple consecutive calls on `*Throw()` stubbing calls.
They can be merged into a single such call.
- [#8](https://github.com/picimako/mockitools/issues/8): Merging of consecutive `*Return()` and `*Throw()` stubbing calls can happen separately if
there are multiple such sections within a stubbing call chain. They, from now on, also keep line wrapping and indentation after applying the quick fix.
- [#13](https://github.com/picimako/mockitools/issues/13): Added 4 new intention actions that can convert between the various stubbing approaches.
- [#15](https://github.com/picimako/mockitools/issues/15): Added 2 new intention actions that can convert between the `Mockito.verify()`
and `BDDMockito.then()` approaches, and vice versa.

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