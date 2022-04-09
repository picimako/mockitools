# Mockitools IntelliJ plugin

![Build](https://github.com/picimako/mockitools/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/18117-mockitools.svg)](https://plugins.jetbrains.com/plugin/18117-mockitools)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18117-mockitools.svg)](https://plugins.jetbrains.com/plugin/18117-mockitools)

<!-- Plugin description -->
This is an IntelliJ-based plugin for [Mockito](https://site.mockito.org), one of the (if not the) most popular mocking framework for unit tests in Java.
<!-- Plugin description end -->

It is available for IntelliJ Community Edition as well as IntelliJ IDEA Ultimate Edition.

## Plugin setup

### Mockito 4 migration aids

There is a handful of inspections available to help to migrate users from Mockito 2.x/3.x to 4.x.

Since Mockito 4 has been released only recently, they are enabled by default. But, if you are already
using Mockito 4, it is recommended to disable them, so that IntelliJ doesn't execute inspection that don't make sense
in terms of your project(s). See <kbd>Java</kbd> > <kbd>Mockito</kbd> > <kbd>Migration aids (v4)</kbd> in Inspection settings.

Eventually, when a good amount of time passed and many users potentially moved to v4, these inspections will be disabled by default.

The list and details of those inspections can be found in [Mockito v4 migration aids](docs/migration_aids_v4.md).

### SonarLint

SonarLint also have a few inspections for Mockito, you can find them under the [Java category](https://rules.sonarsource.com/java?search=mockito).

If you use SonarLint or any other plugin that has checks for things provided by the Mockitools plugin, it is advised adjusting your settings,
so that only those versions of the inspections are enabled that you feel comfortable working with.
This will help declutter your editor from duplicate highlights for the same problems, and will also improve the analysis performance in the IDE.

You can find the list of those inspections in the [Mockitools/SonarLint rules](docs/mockitools_sonarlint_rules.md) document.

## Why Mockitools is tasty

If [*"Mockito ... tastes really good"* and *"doesn’t give you hangover"*](https://site.mockito.org/#why), this IDE plugin will only make it better. You can find out why, in the dedicated documentation below.

- [Mock creation](docs/mock_creation.md)
- [Stubbing](docs/stubbing.md)
- [Mockito verifications](docs/mockito_verifications.md)
- [Argument matching and capturing](docs/argument_matching_capture.md)
- [Version migration aids](docs/migration_aids_v4.md)
- [Framework integration](docs/framework_integration.md)

In the Intellij Inspections settings, all inspections are available under the <kbd>Java</kbd> > <kbd>Mockito</kbd> folder.

## Other plugins for Mockito

If you are interested in Mockito code generation, check out the [JUnit 5 Mockito code generator](https://plugins.jetbrains.com/plugin/12833-junit-5-mockito-code-generator).
For using postfix templates, try out [Mockito Postfix Completion](https://plugins.jetbrains.com/plugin/8150-mockito-postfix-completion).

## Licensing

This project and the plugin logo are licensed under the terms of Apache Licence Version 2.0.

## Acknowledgments

Thank you to my good friend, [Thubakabra](https://www.facebook.com/Thubakabra), for creating the logo.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
