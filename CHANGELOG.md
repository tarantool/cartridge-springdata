# Changelog

## [0.5.0] - 2022-02-24

### Features
 - **Breaking change** Change Query methods API:
   - ```@Tuple``` can't be applied to Repository interface
   - Added output parameter to ```@Query``` which allows to serialization options
 - Improved performance for large entities
 - Added support for returning non entity objects in ```@Query``` methods

### Bugfixes
 - Fix truncate for RetryingTarantoolClient using implementation from cartridge-java
