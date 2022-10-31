# Changelog

## [Unreleased]

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
