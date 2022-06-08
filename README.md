# Mockitools IntelliJ plugin

[![Version](https://img.shields.io/jetbrains/plugin/v/18117-mockitools.svg)](https://plugins.jetbrains.com/plugin/18117-mockitools)
![Build](https://github.com/picimako/mockitools/workflows/Build/badge.svg)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18117-mockitools.svg)](https://plugins.jetbrains.com/plugin/18117-mockitools)
![](https://img.shields.io/badge/since-IJ2021.1-blue) ![](https://img.shields.io/badge/until-IJ2022.1-blue)
[![PayPal](https://img.shields.io/badge/donate-paypal-yellow.svg)](https://www.paypal.com/donate/?hosted_button_id=VVLWNZWPBRUDL)
[![BuyMeACoffee](https://img.shields.io/badge/donate-buymeacoffee-yellow.svg)](https://www.buymeacoffee.com/picimako)

<!-- Plugin description -->
Mockitools is a plugin for IntelliJ-based IDEs that provides framework integration for [Mockito](https://site.mockito.org), one of the (if not the) most popular mocking frameworks for unit testing in Java.
<!-- Plugin description end -->

It is available for IntelliJ Community Edition as well as IntelliJ IDEA Ultimate Edition.

## Plugin setup

### Mockito 4 migration aids

There is a handful of inspections available to help migrate users from Mockito 2.x/3.x to 4.x.

Since Mockito 4 has been released only recently, they are enabled by default, but if you are already
using Mockito 4, it is recommended to disable them in <kbd>Java</kbd> > <kbd>Mockito</kbd> > <kbd>Migration aids (v4)</kbd> in Inspection settings,
so they are not executed for your project. As users move to v4, eventually they will get disabled by default.

See the list of those inspections in [Mockito v4 migration aids](docs/migration_aids_v4.md).

### SonarLint

SonarLint also has a few inspections for Mockito that you can find under the [Java category](https://rules.sonarsource.com/java?search=mockito) of their rules.

If you use SonarLint to validate things that are available in Mockitools too, it is advised to adjust your settings,
and have only those ones enabled that you feel comfortable working with. Be it in SonarLint or in Mockitools.
This will help declutter your editor from duplicate highlights for the same problems, and will also improve the analysis performance in the IDE.

You can find the list of those inspections in the [Mockitools/SonarLint rules](docs/sonarlint_rules.md) document.

## Why Mockitools is tasty

If [*"Mockito ... tastes really good"* and *"doesn’t give you hangover"*](https://site.mockito.org/#why), this IDE plugin will only make it better. You can find out why, in the dedicated documentation below.

- [Mock creation](docs/mock_creation.md)
- [Stubbing](docs/stubbing.md)
- [Mockito verifications](docs/verifications.md)
- [Argument matching and capturing](docs/argument_matching_capture.md)
- [Version migration aids](docs/migration_aids_v4.md)
- [Framework integration](docs/framework_integration.md)

In the Intellij Inspections settings, all inspections are available under the <kbd>Java</kbd> > <kbd>Mockito</kbd> folder.

## Licensing

This project and the plugin logo are licensed under the terms of Apache Licence Version 2.0.

## Acknowledgments

Thank you to my good friend, [Thubakabra](https://www.facebook.com/Thubakabra), for creating the logo.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
