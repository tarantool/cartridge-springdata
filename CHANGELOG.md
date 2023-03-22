# Changelog

## [Unreleased]

## [0.6.0] - 2023-04-13
- Bump cartridge-java version to 0.11.0 ([#123](https://github.com/tarantool/cartridge-springdata/issues/123))

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
