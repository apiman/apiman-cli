# Change Log

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [0.4.0] - 2020-11-01
### Added
- Adds the ability to specify policies and configuration that should be present on every API.

## [0.3.2] - 2020-02-17
### Changed
- Upgrades log4j2, jackson and modelmapper dependencies to latest versions.
- Upgrades Gradle to 6.1.1.

## [0.3.1] - 2018-03-24
### Added
- Adds support for applying multiple API declarations at once.
- Improves null safety of headless configuration generator.

## [0.3.0] - 2018-03-08
### Added
- Allow gateway to be driven directly using declarations (Marc Savy). See _Changes_ section for details of changes to 
  the command syntax.
- Adds the ability to specify a definition for an API (Raleigh Pickard).
- Adds the ability to wait for the server to start before attempting to run commands (Pete Cornish).
- Improves documentation (Pete Cornish).

### Fixed
- Fixes #14. Fetches added plugin and uses generated ID in fetch test (Pete Cornish).

### Changed
- **Breaking change:** the syntax for administering the manager has changed. Where previously you would have written 
  `./apiman [command]` you would now write `./apiman manager [command]`.
- Factors out declarative application logic into separate services (Pete Cornish).
- Reorganises command package structure (Pete Cornish).

## [0.2.4] - 2017-11-29
### Added
- Adds the ability to wait for the server to start before attempting to run commands.
- Improvements to internal code structure.

## [0.2.3] - 2016-09-22
### Added
- Adds support for multiple versions of an API in the same declaration file.

### Changed
- Use of 'initialVersion' in declaration files is deprecated and will be removed in future - use 'version' instead.

## [0.2.2] - 2016-09-20
### Added
- Adds support for XML format Java properties files.
- Adds support for specifying multiple properties files.

## [0.2.1] - 2016-09-20
### Added
- Adds the ability to resolve placeholder properties using a file.

## [0.2.0] - 2016-04-23
### Added
- Adds support for shared properties within declaration files, which can be reused throughout the file.
- Adds support for APIs with HTTP Basic Authorization.
- Updates versions of various dependencies.

## [0.1.9] - 2016-03-01
### Added
- Adds support for shared policy definitions, allowing you to define a policy configuration once and reuse it in multiple APIs.
- Adds shared policy examples.

## [0.1.8] - 2016-02-07
### Added
- Fixed Bourne shell compatibility.
- Improved documentation.

## [0.1.7] - 2016-01-31
### Added
- Internal naming changes and unit test improvements.

## [0.1.6] - 2016-01-31
### Added
- Declarative Mode now supports both v1.1.x and v1.2.x management server APIs
- Declarative Mode will attempt to configure published APIs on v1.2.x
- Declarative Mode will attempt to republish APIs on v1.2.x
- Management server API v1.2.x is now the default. Use the `--serverVersion` flag to change.

## [0.1.5] - 2016-01-23
### Added
- Declarative API environment definitions (using YAML or JSON) with the new `apiman apply` command
- Declarations can use placeholders, passed from the command line with the `-P key=value` or `-P "key=value"` syntax

## [0.1.4] - 2016-01-11
### Added
- Support for adding policies to APIs
- Policy configuration can be passed from file or from STDIN

## [0.1.3] - 2016-01-11
### Added
- Improved JavaDocs
- Adds Apache 2.0 license and file headers
- Renamed 'service' references to 'API'
- Support for both apiman v1.1.x and v1.2.x servers, using `--serverVersion` flag (default is v1.2.x)

## [0.1.2] - 2016-01-11
### Added
- Support for publishing services
- Changelog (this file!)

## [0.1.1] - 2016-01-11
### Added
- Support for adding services

## [0.1.0] - 2016-01-10
### Added
- Support for adding and showing organisations
- Support for adding, listing, testing and showing gateways
- Support for adding, listing, and showing plugins
