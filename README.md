# dev-tools ![Build](https://github.com/reugn/dev-tools/workflows/Build/badge.svg)

The most popular software developer tools in one app.
* [Json Editor](#json_editor)
* [UUID/Password Generator](#generator)
* [Hash Calculator](#hash_calculator)
* [Epoch Converter](#epoch_converter)
* [Regular Expression Tester](#regex)
* [Rest API Tester](#rest_api)
* [ASCII Graphics](#ascii)
* [Logs Generator](#logs)

## Installation
`dev-tools` is a Maven JavaFX application.  
Build an executable jar from the source:
```
mvn clean package -U
```
Build a native application using [Gluon Client plugin](https://github.com/gluonhq/client-maven-plugin):
```
mvn clean client:build
```
or download the latest release.

***JavaFX is not a part of Java SDK as of JDK 11.***

## Features
* Dark/Light mode.

## Tools List

<a name="json_editor"/>

### Json Editor
* JSON pretty print with highlighting.
* JSON validation.
* Search Bar (Ctrl+F).

![](docs/images/json_editor.png)

<a name="generator"/>

### UUID/Password Generator
* UUID Generator.
* Password Generator.

![](docs/images/generator.png)

<a name="hash_calculator"/>

### Hash Calculator
* Hash functions.
* URL Encode/Decode.
* Base64 Encode/Decode.

![](docs/images/hash_calculator.png)

<a name="epoch_converter"/>

### Epoch Converter
* Current Unix epoch time.
* Timestamp to human date.
* Human date to timestamp.

![](docs/images/epoch_converter.png)

<a name="regex"/>

### Regular Expression Tester
* Regex flags.
* Capturing groups.

![](docs/images/regex.png)

<a name="rest_api"/>

### Rest API Tester
* Rest API testing client.

![](docs/images/rest_api.png)

<a name="ascii"/>

### ASCII Graphics
* Convert text to ASCII art.

![](docs/images/ascii.png)

<a name="logs"/>

### Logs Generator
* Generate fake log workloads using a specified format.
* Write to console.
* Write to file.

![](docs/images/logs.png)

## Contributing
If you find this project useful and want to contribute, please open an issue or create a PR.

## License
Licensed under the Apache 2.0 License.
