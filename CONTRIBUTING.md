# Developer Documentation

## Technology stack

This project builds on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template) that uses
Gradle as its build system, GitHub Actions for the CI/CD pipeline, and Java 17.

## Environment setup

### JDK

Install Java JDK 17 and configure the project to use that JDK if not automatically configured.

### Setup Mockitools

To start development, just clone this repository to your machine, import the project, let it build, and you should be good to go.

There are predefined run configurations coming from the platform plugin template you can use to build, test and run the plugin.

### Suggested plugins for development

the following plugins are recommended to help with the plugin development:
- [PsiViewer](https://plugins.jetbrains.com/plugin/227-psiviewer) for inspecting the syntax tree (PSI) of files,
- [Concise AssertJ Optimizing Nitpicker (Cajon)](https://plugins.jetbrains.com/plugin/12195-concise-assertj-optimizing-nitpicker-cajon-) for working with AssertJ assertions.

## Base classes

There are a few base classes that can be used to implement the different plugin features:
- [MockitoolsBaseInspection](src/main/java/com/picimako/mockitools/inspection/MockitoolsBaseInspection.java) for inspections (not all inspections use this as base class)
- [MigrationAidBase](src/main/java/com/picimako/mockitools/inspection/migrationaids/v4/MigrationAidBase.java) for migration aids inspections

## Mockito DSL types

Functionality is organized into DSL specific classes:
- classes in the `com.picimako.mockitools.dsl` package
- [StubbingApproach](src/main/java/com/picimako/mockitools/StubbingApproach.java)
- [VerificationApproach](src/main/java/com/picimako/mockitools/VerificationApproach.java)
- [StubType](src/main/java/com/picimako/mockitools/StubType.java)

## Message bundle

Most messages that appear on the UI, in settings, in inspection messages, are stored in a message bundle called `MockitoolsBundle.properties`.
Some messages, like inspection titles are not yet included, and simply are in the plugin.xml.

## Since-version

The following markers are placed on extension point implementations and more important types, to help identify certain aspects of the code:
- `@since <version number>` in javadocs
- `@HasSonarLintAlternative` annotation if an inspection has an alternative in SonarLint

## CI/CD

For CI/CD, GitHub Actions is integrated, and workflows are available in the `.github/workflows` folder.

## Functional tests

Functional tests build mostly on JUnit3-based platform test classes. For assertions, either the IntelliJ platform's underlying logic is used, or AssertJ
where applicable.

Mockitools base test classes:
- [MockitoolsTestBase](src/test/java/com/picimako/mockitools/MockitoolsTestBase.java) as the common base test class
- [MockitoolsInspectionTestBase](src/test/java/com/picimako/mockitools/inspection/MockitoolsInspectionTestBase.java) for inspections
- [MockitoolsIntentionTestBase](src/test/java/com/picimako/mockitools/intention/MockitoolsIntentionTestBase.java) for intention actions
- [MockitoolsCodeCompletionTestBase](src/test/java/com/picimako/mockitools/completion/MockitoolsCodeCompletionTestBase.java) for completion contributors

Other resources: [IntelliJ Platform Plugin SDK - Testing Overview](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)

### Load 3rd-party libs

#### JDK

Java file based tests require either a mock or a real JDK to be available. This project is configured to always use a real JDK via
`com.picimako.mockitools.MockitoolsTestBase#getJdkHome()`, so that modification of the
[`idea.home.path` system property](https://plugins.jetbrains.com/docs/intellij/code-inspections.html#inspection-unit-test)
is not necessary for running tests locally. And, using the JAVA_HOME based JDK also works on GitHub Actions.

#### Other libs

In order for tests to recognize code from other libraries, those libraries have to be added to the classpath too. You can use the various
`load*()` methods of `com.picimako.mockitools.ThirdPartyLibraryLoader`.

These libraries are located in the `lib` directory under the project root.

### testData

The `src/test/testData` folder is used to store test data. They can be configured as project roots when setting the test data path in functional tests.

## JetBrains resources
- [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- [JetBrains Community Support](https://intellij-support.jetbrains.com/hc/en-us/community/topics)
- [Request invitation for JetBrains Slack](https://plugins.jetbrains.com/slack)
