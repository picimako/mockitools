<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Mockitools Changelog

## [Unreleased]
### Added
- [#2](https://github.com/picimako/mockitools/issues/2): Extended `MockTypeInspection` to validate and report types annotated with `@DoNotMock` annotation.
  - You can find more information about this at
    - [MockTypeInspection documentation](docs/mock_creation.md#donotmock-annotated-types) 
    - [@DoNotMock javadoc](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/DoNotMock.html)
    - [Mockito pull request: Add annotation to mark a type as DoNotMock](https://github.com/mockito/mockito/pull/1833/files)

## [0.1.1]
### Added
- Plugin is now compatible with IJ 2021.3.
- Upgraded gradle to 7.3 and some library versions.

## [0.1.0]
### Added
- Initial set of inspections for Mockito, including migration aids for Mockito 4.0.0.
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)