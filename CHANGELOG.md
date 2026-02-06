# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.4.0] - 2026-02-06

### Added
- Kotlin Multiplatform support (JVM, JS, Native)
- Unit tests for JavaScript regex support
- Vararg overloads for `anyOf`, `noneOf`, and `chars`

### Changed
- Migrated build to Gradle Version Catalog
- Renamed `RegexBuilderJvm` to `RegexBuilder`
- Updated CI/CD configuration for multiplatform builds

## [0.3.0] - 2026-02-03

### Added
- `posix` block builder for POSIX character classes (`alnum`, `alpha`, `digit`, `lower`, `upper`, `space`, `punct`, `xdigit`)
- `unicode` block builder for Unicode categories and scripts (`letter`, `number`, `punctuation`, `symbol`, `script`, `block`)
- `hangul` block builder for Korean character ranges (`syllable`, `jamo`, `consonant`, `vowel`)
- Pre-built `password()` pattern for password validation

### Changed
- Enforce explicit API visibility with `@PublishedApi` annotations

## [0.2.0] - 2026-02-02

### Added
- `ascii` block builder for ASCII character ranges (`lower`, `upper`, `digit`, `letter`, `alphanumeric`, `hexDigit`)
- `anyOf` and `noneOf` aliases for `charClass` and `negatedCharClass`
- `line { }` block for matching entire lines (`^...$`)
- `input { }` block for matching entire input (`\A...\z`)

### Changed
- Renamed `capture` to `captureAs` for named capture groups
- Downgraded Kotlin version for broader compatibility

## [0.1.0] - 2026-02-01

Initial release.

- Core `RegexBuilder` DSL with fluent API
- Pre-built patterns for email, URL, IP, phone, date/time, identifiers
- POSIX character classes support
- Maven Central publishing with CI/CD

[0.4.0]: https://github.com/bedshanty/kregex/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/bedshanty/kregex/compare/0.2.0...0.3.0
[0.2.0]: https://github.com/bedshanty/kregex/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/bedshanty/kregex/releases/tag/0.1.0
