# Changelog

## [Unreleased]

- Bump spring version version to 5.3.31 ([#136](https://github.com/orgs/tarantool/projects/75/views/4?pane=issue&itemId=44971242))
- Bump spring-boot version version to 2.7.5 ([#136](https://github.com/orgs/tarantool/projects/75/views/4?pane=issue&itemId=44971242))
- Bump jackson-annotations version to 2.16.0 ([#136](https://github.com/orgs/tarantool/projects/75/views/4?pane=issue&itemId=44971242))
- Bump snakeyaml version to 2.2 ([#136](https://github.com/orgs/tarantool/projects/75/views/4?pane=issue&itemId=44971242))
- Bump testcontainers version to 1.18.0 ([#136](https://github.com/orgs/tarantool/projects/75/views/4?pane=issue&itemId=44971242))

## [0.6.1] - 2023-11-17
- Bump cartridge-driver version to 0.13.0 ([#133](https://github.com/tarantool/cartridge-springdata/issues/133))
- Bump testcontainers-java-tarantool version to 1.0.1 ([#133](https://github.com/tarantool/cartridge-springdata/issues/133))
- Fix naming for Tarantool threads

## [0.6.0] - 2023-06-14
- Bump cartridge-java version to 0.12.0 ([#123](https://github.com/tarantool/cartridge-springdata/issues/123))
- Add update method for SimpleTarantoolRepository class ([#122](https://github.com/tarantool/cartridge-springdata/issues/122))
- Use isEmpty instead of compare size

## [0.5.3] - 2022-11-17
- Add dependency management
- Add slf4j-api 1.7.36 ([#118](https://github.com/tarantool/cartridge-springdata/issues/119))
- Bump cartridge-java to 0.9.2 ([#118](https://github.com/tarantool/cartridge-springdata/issues/118))
- Bump testcontainers-java-tarantool to 0.5.3 ([#118](https://github.com/tarantool/cartridge-springdata/issues/118))
- Bump logback-classic to 1.2.11 ([#118](https://github.com/tarantool/cartridge-springdata/issues/118))
- Bump lombok to 1.8.24 ([#118](https://github.com/tarantool/cartridge-springdata/issues/118))
- Remove org.junit.jupiter:junit-jupiter ([#118](https://github.com/tarantool/cartridge-springdata/issues/118))

## [0.5.2] - 2022-10-31

### Security
- Bump cartridge-java version to 0.9.1 ([#113](https://github.com/tarantool/cartridge-springdata/issues/113))
- Bump testcontainers-java-tarantool to 0.5.1 ([#113](https://github.com/tarantool/cartridge-springdata/issues/113))
- Bump spring-data-commons to 2.7.3 ([#113](https://github.com/tarantool/cartridge-springdata/issues/113))
- Bump spring to 5.3.23 ([#113](https://github.com/tarantool/cartridge-springdata/issues/113))
- Bump logback-classic to 1.2.9 ([#113](https://github.com/tarantool/cartridge-springdata/issues/113))

## [0.5.1] - 2022-05-20

### Bugfixes
- Remove redundant canGetMap in getPropertyValue for performance improvement ([#101](https://github.com/tarantool/cartridge-springdata/issues/101))
- Allow methods without Query in Repository ([#94](https://github.com/tarantool/cartridge-springdata/issues/94))
- Fix accepting crud response with AUTO ([#97](https://github.com/tarantool/cartridge-springdata/issues/97))
- Fix access denied when using AUTO ([#95](https://github.com/tarantool/cartridge-springdata/issues/95))

## [0.5.0] - 2022-02-24

### Features
 - **Breaking change** Change Query methods API:
   - ```@Tuple``` can't be applied to Repository interface
   - Added output parameter to ```@Query``` which allows to serialization options
 - Improved performance for large entities
 - Added support for returning non entity objects in ```@Query``` methods

### Bugfixes
 - Fix truncate for RetryingTarantoolClient using implementation from cartridge-java
