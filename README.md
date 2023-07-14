# Mockitools IntelliJ plugin

[![Version](https://img.shields.io/jetbrains/plugin/v/18117-mockitools.svg)](https://plugins.jetbrains.com/plugin/18117-mockitools)
![Build](https://github.com/picimako/mockitools/workflows/Build/badge.svg)

<!-- Plugin description -->
Mockitools is a plugin for IntelliJ-based IDEs that provides framework integration for [Mockito](https://site.mockito.org),
one of the (if not the) most popular mocking frameworks for unit testing in Java.

It provides features in the following areas of Mockito:

- [Mock creation](docs/mock_creation.md)
- [Stubbing](docs/stubbing.md)
- [Mockito verifications](docs/verifications.md)
- [Argument matching and capturing](docs/argument_matching_capture.md)
- [Version migration aids](docs/migration_aids_v4.md)
- [Framework integration](docs/framework_integration.md)
<!-- Plugin description end -->

In the Intellij Inspections settings, all inspections are available under the <kbd>Java</kbd> / <kbd>Mockito</kbd> folder.

## Plugin setup

### Mockito 4 migration aids

There is a handful of inspections available to help migrate users from Mockito 2.x/3.x to 4.x.

Since Mockito 5 has already been available for a while, these inspection are disabled by default. However, if you are still on
Mockito 2.x or 3.x, you can enable them under <kbd>Java</kbd> > <kbd>Mockito</kbd> > <kbd>Migration aids (v4)</kbd> in the Inspection settings.

See the list of those inspections in [Mockito v4 migration aids](docs/migration_aids_v4.md).

## Licensing

This project and the plugin logo are licensed under the terms of Apache Licence Version 2.0.

## Acknowledgments

Thank you to my good friend, [Thubakabra](https://www.facebook.com/Thubakabra), for creating the logo.

## Reading

- [DZone: IntelliJ Integration for Mockito](https://dzone.com/articles/intellij-mockito-integration)

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
