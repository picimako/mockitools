# Developer Documentation

## Technology Stack

This project builds on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
Gradle as its build system, GitHub Actions for the CI/CD pipeline, and Java/Kotlin for coding.

### Libraries

- Production: Lombok
- Testing: JUnit 5, AssertJ

## Environment Setup

### JDK

Install Java JDK 21 and configure the project to use that JDK if not automatically configured.

### Setup Mockitools

To start development, clone this repository to your machine, import the project, let it build, and you should be good to go.

There are predefined run configurations coming from the platform plugin template you can use to build, test and run the plugin:

| Run Configuration       | CLI command              | Purpose                                                          |
|-------------------------|--------------------------|------------------------------------------------------------------|
| **Run Build**           | `./gradlew buildPlugin`  | Compiles the project and builds the plugin .jar and .zip archive |
| **Run IDE with Plugin** | `./gradlew runIde`       | Runs a sandbox IDE with the Mockitools plugin installed in it    |
| **Run Tests**           | `./gradlew check`        | Runs all tests in the project                                    |
| **Run Verifications**   | `./gradlew verifyPlugin` | Runs the Plugin Verifier                                         |

### Suggested Plugins for Development

The following plugins can help with the plugin development:
- [PsiViewer](https://plugins.jetbrains.com/plugin/227-psiviewer) for inspecting the syntax tree (PSI) of files,
- [Concise AssertJ Optimizing Nitpicker (Cajon)](https://plugins.jetbrains.com/plugin/12195-concise-assertj-optimizing-nitpicker-cajon-) for working with AssertJ assertions in tests.
- [Just Kitting](https://plugins.jetbrains.com/plugin/21139-just-kitting) for extra utilities for plugin development,
and for easier comparison of configuration files with their versions in the upstream template.

## Project Structure

```
<root>/
    └── .github/
        └── workflows/          //GitHub Actions workflow files
    ├── .run/                   //IntelliJ Run Configurations
    ├── docs/                   //Project documentation
    ├── gradle/                 //Gradle wrapper files
        └── libs.versions.toml  //Gradle version catalog
    └── src/
        ├── ...
        └── test/
            ├── ...
            └── testData/       //Test data files for inspections, intentions, etc.
```

### Mockito DSL

Functionality is organized into and around DSL specific classes in the `com.picimako.mockitools.dsl` package.

### Base Classes

There are a few common base classes that can be used to implement the different plugin features. They are all named
consistently with their names ending with `Base`, e.g. `MockitoolsInspectionBase` or `QuickFixBase`.

### Message Bundle

Most messages that appear on the UI, in settings, in inspection messages, etc. are stored in a message bundle called `MockitoolsBundle.properties`.
Some messages, like inspection titles are not yet included, and simply are in the plugin.xml.

### Integration Tests

Integration tests build on JUnit 5-based platform test classes. There are a couple of base test classes that are
consistently named as `*TestBase`, e.g. `MockitoolsTestBase`.

#### JDK

Tests and test bases that are dedicated to testing primarily Java test data files require either a mock JDK from
the IntelliJ Platform or a real JDK to be available.

This project is configured to always use a real JDK due to former issues and test failures when using a mock JDK.

#### Load 3rd-party Libraries

Since the test data files are Java files, certain libraries have to be added to the tests' classpath.

The necessary libraries are collected in the `ThirdPartyLibrary` enum with their Maven coordinates,
and can be configured in tests via the constructor of `MockitoolsTestBase`.

#### Test Data

Test data files are stored in the `src/test/testData` folder.
They can be configured as project roots via the `@TestDataPath` annotation on test classes.

## CI/CD

For CI/CD, GitHub Actions is integrated, and workflows are available in `.github/workflows/build.yml`.

## Documentation

### General

General documentation is stored in the `/docs` root-level folder.

### Since-version

The `@since <version number>` Javadoc tag is placed on extension point implementations and more important types and methods
to help see which version introduced the feature.

## JetBrains resources
- [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- [JetBrains Platform - community forum](https://platform.jetbrains.com)
