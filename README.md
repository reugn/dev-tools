# dev-tools ![Build](https://github.com/reugn/dev-tools/workflows/Build/badge.svg)

A collection of developer utilities in a single desktop application.

* [JSON Editor](#json-editor)
* [Image Editor](#image-editor)
* [Rest API Tester](#rest-api-tester)
* [JWT Decoder](#jwt-decoder)
* [UUID/Password Generator](#uuid-password-generator)
* [Hash Calculator](#hash-calculator)
* [Epoch Converter](#epoch-converter)
* [Regular Expression Tester](#regular-expression-tester)
* [ASCII Graphics](#ascii-graphics)
* [Log Generator](#log-generator)

## Installation

`dev-tools` is a [JavaFX](https://openjfx.io/) desktop application built with Maven.
Obtain a binary using one of the following methods:

* Build an executable jar from the source.

```
./mvnw clean package
```

* Build a native application using [GluonFX Maven plugin](https://github.com/gluonhq/gluonfx-maven-plugin).
  [GraalVM](https://www.graalvm.org/) installation is required. After obtaining the GraalVM distribution, the
  environment variable `GRAALVM_HOME` should be set to point to it.

```
./mvnw clean gluonfx:build
```

* Download a build from [releases](https://github.com/reugn/dev-tools/releases).

## Prerequisites

* Java 11 or later

## Getting Started

* Launch the application. For the executable jar use `java -jar target/dev-tools-<version>.jar`.
* Explore the main menu and its options.
* Select a tool from the `Tools` dropdown menu. See below for a complete list with screenshots.
* Switch between light and dark themes using the `View -> Theme` menu option.
* Utilize keyboard shortcuts for faster navigation.

## Tool List

### JSON Editor

Edit and analyze JSON using syntax highlighting, formatting, and validation.
Use the built-in search (Ctrl+F) to find items. Navigate between documents using the multi-tab interface.

![](docs/images/json_editor.png)

### Image Editor

This tool incorporates an essential suite of functions for creating and editing images.

![](docs/images/image_editor.png)

### Rest API Tester

This tool provides a core set of functionalities for REST API testing.
Manage and share your testing process via the export and import of HTTP request history.
Access history features by Ctrl-clicking the history pane. Navigate between requests using the multi-tab interface.

![](docs/images/rest_api.png)

### JWT Decoder

Paste your JSON Web Token (JWT) into the input area to decode it. Use the provided buttons to
decode, encode, and verify the JWT signature. For signature verification, select the correct algorithm and provide
the necessary secrets.

![](docs/images/jwt_decoder.png)

### UUID-Password Generator

Generate a series of UUIDs (Universally Unique Identifiers) or passwords according to the provided configuration
controls.

![](docs/images/generator.png)

### Hash Calculator

This utility performs cryptographic hashing and various encoding/decoding operations on input data.

![](docs/images/hash_calculator.png)

### Epoch Converter

Epoch Converter is a utility for converting between Unix epoch time (seconds since January 1, 1970, 00:00:00 UTC)
and human-readable dates. Features include displaying the current Unix epoch, timestamp to date/time
conversion, and date/time to timestamp conversion.

![](docs/images/epoch_converter.png)

### Regular Expression Tester

Regular Expression Tester is a tool for testing and debugging regular expressions (regex) featuring full regex flag
support (i, m, u, etc.), comprehensive capturing group visualization and match highlighting.

![](docs/images/regex_tester.png)

### ASCII Graphics

Easily turn your text into ASCII art. Customize the look with adjustable settings.

![](docs/images/ascii_graphics.png)

### Log Generator

Log Generator creates customizable synthetic log data for testing and development.
Choose from a selection of pre-defined standard log formats.
Write the generated logs directly to the output pane, or save them to a file for later use.

![](docs/images/log_generator.png)

## License

Licensed under the Apache 2.0 license.
