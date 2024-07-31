[![GPL LICENSE][license-shield]][license-url]
[![GitHub tag (with filter)][tag-shield]][tag-url]
[![GitHub contributors][contributors-shield]][contributors-url]
[![GitHub Workflow Status (with event)][dev-build-shield]][dev-build-url]
[![GitHub Workflow Status (with event)][build-shield]][build-url]

# GCM Pipeline
A Pipeline to read heterogenous raw data files and translate them into GCM PluginDatas for use with [GCM](https://github.com/HHS/ASPR-8).
As of v1.0.0, this project is in Maven Central

## License
Distributed under the GPLv3 License. See [LICENSE](LICENSE) for more information.

Please read the [HHS vulnerability disclosure](https://www.hhs.gov/vulnerability-disclosure-policy/index.html).

## Usage 
To use this project in your project, simply add the following dependency to your `dependencies` section of your pom.xml file.
```
<dependency>
    <groupId>gov.hhs.aspr.ms.gcm</groupId>
    <artifactId>pipeline</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Overview
This pipeline implementation is extremely basic for v1.0.0, only containing a couple of interfaces and a set of common ContractException Errors.
This pipeline project is in active development.

## Building from Source

### Requirements
- Maven 3.8.x
- Java 17
- Favorite IDE for Java development
- Modeling Util located [here](https://github.com/HHS/ASPR-ms-util)
- GCM located [here](https://github.com/HHS/ASPR8)
- Taskit located [here](https://github.com/HHS/ASPR-ms-taskit)
- GCM Taskit located [here](https://github.com/HHS/ASPR-ms-gcm-taskit)

*NOTE that Modeling Utils, GCM Taskit and GCM Taskit are in maven central, so there is no need to clone and build those repos

### Building
To build this project:
- Clone the repo
- open a command line terminal
- navigate to the root folder of this project
- run the command: `mvn clean install`

## Documentation
Documentation has yet to be created. In the interim, the code is mostly commented and the javadocs do provide good detail with regards to method and class expectations. 

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/HHS/ASPR-ms-gcm-pipeline
[contributors-url]: https://github.com/HHS/ASPR-ms-gcm-pipeline/graphs/contributors
[tag-shield]: https://img.shields.io/github/v/tag/HHS/ASPR-ms-gcm-pipeline
[tag-url]: https://github.com/HHS/ASPR-ms-gcm-pipeline/releases/latest
[license-shield]: https://img.shields.io/github/license/HHS/ASPR-ms-gcm-pipeline
[license-url]: LICENSE
[dev-build-shield]: https://img.shields.io/github/actions/workflow/status/HHS/ASPR-ms-gcm-pipeline/dev_build.yml?label=dev-build
[dev-build-url]: https://github.com/HHS/ASPR-ms-gcm-pipeline/actions/workflows/dev_build.yml
[build-shield]: https://img.shields.io/github/actions/workflow/status/HHS/ASPR-ms-gcm-pipeline/release_build.yml?label=release-build
[build-url]: https://github.com/HHS/ASPR-ms-gcm-pipeline/actions/workflows/release_build.yml.yml

