# Kregex

[English](README.md) | [한국어](README.ko.md)

[![CI](https://github.com/bedshanty/kregex/actions/workflows/ci.yml/badge.svg)](https://github.com/bedshanty/kregex/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.bedshanty/kregex)](https://central.sonatype.com/artifact/io.github.bedshanty/kregex)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.21-blue.svg?logo=kotlin)](http://kotlinlang.org)

**Kregex** is a Kotlin DSL library for building regular expressions in a readable, type-safe, and maintainable way.

Instead of writing cryptic regex patterns like `^(?:[a-zA-Z0-9._%-]+)@(?:[a-zA-Z0-9-]+)\.(?:[a-zA-Z]{2,6})$`, you can express them as clear, self-documenting Kotlin code.

## Features

- **Type-safe DSL** - Compile-time safety with Kotlin's DSL markers
- **Readable patterns** - Self-documenting regex construction
- **Full regex support** - Anchors, character classes, quantifiers, lookarounds, back references
- **Unicode support** - Unicode properties, scripts, and blocks
- **Lazy & Possessive quantifiers** - Fine-grained matching control
- **Pattern debugging** - Get the generated pattern string for inspection
- **Zero dependencies** - Pure Kotlin, no external dependencies

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.bedshanty:kregex:0.1.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'io.github.bedshanty:kregex:0.1.0'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.bedshanty</groupId>
    <artifactId>kregex</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Quick Start

```kotlin
// Simple pattern
val digitPattern = regex {
    startOfLine()
    oneOrMore { digit() }
    endOfLine()
}

println(digitPattern.matches("12345")) // true
println(digitPattern.matches("abc"))   // false
```

## Usage Examples

### Email Validation

```kotlin
val emailPattern = regex(RegexOption.IGNORE_CASE) {
    startOfLine()
    oneOrMore {
        charClass {
            wordChar()
            chars(".-")
        }
    }
    literal("@")
    oneOrMore {
        charClass {
            wordChar()
            chars("-")
        }
    }
    literal(".")
    repeat(2, 6) { range('a', 'z') }
    endOfLine()
}

println(emailPattern.matches("user@example.com"))  // true
println(emailPattern.matches("invalid"))           // false
```

### Phone Number

```kotlin
// Phone number with optional country code (1-3 digits)
val phonePattern = regex {
    startOfLine()
    optional {
        literal("+")
        repeat(1, 3) { digit() }
        anyOf(" -")
    }
    repeat(3) { digit() }
    anyOf(" -")
    repeat(3, 4) { digit() }
    anyOf(" -")
    repeat(4) { digit() }
    endOfLine()
}

println(phonePattern.matches("123-456-7890"))     // true
println(phonePattern.matches("123 456 7890"))     // true (with spaces)
println(phonePattern.matches("+1-123-456-7890"))  // true (US)
println(phonePattern.matches("+82-123-456-7890")) // true (Korea)
println(phonePattern.matches("+1 234-5678"))      // false (wrong format)
```

### URL Parsing with Named Captures

```kotlin
val urlPattern = regex {
    startOfLine()
    capture("protocol") {
        either(
            { literal("https") },
            { literal("http") }
        )
    }
    literal("://")
    capture("domain") {
        oneOrMore { charClass { wordChar(); chars(".-") } }
    }
    optional {
        capture("port") {
            literal(":")
            oneOrMore { digit() }
        }
    }
    optional {
        capture("path") {
            zeroOrMore { charClass { wordChar(); chars("/.-") } }
        }
    }
    endOfLine()
}

val match = urlPattern.find("https://example.com:8080/api/v1")!!
println(match.groups["protocol"]?.value)  // https
println(match.groups["domain"]?.value)    // example.com
println(match.groups["port"]?.value)      // :8080
```

### HTML Tag Matching with Back References

```kotlin
val htmlTag = regex {
    literal("<")
    capture("tag") { oneOrMore { wordChar() } }
    zeroOrMore { anyChar() }
    literal(">")
    zeroOrMoreLazy { anyChar() }
    literal("</")
    backReference("tag")
    literal(">")
}

println(htmlTag.containsMatchIn("<div>content</div>"))   // true
println(htmlTag.containsMatchIn("<div>content</span>"))  // false
```

## API Reference

### Entry Point

| Function | Description |
|----------|-------------|
| `regex { }` | Creates a `Regex` object from the DSL |
| `regex(options) { }` | Creates a `Regex` with options (e.g., `IGNORE_CASE`) |

Use the standard `Regex.pattern` property to inspect the generated pattern string.

### Anchors & Boundaries

| Method | Pattern | Description |
|--------|---------|-------------|
| `startOfLine()` | `^` | Start of line |
| `endOfLine()` | `$` | End of line |
| `startOfInput()` | `\A` | Start of input |
| `endOfInput()` | `\z` | End of input |
| `wordBoundary()` | `\b` | Word boundary |
| `nonWordBoundary()` | `\B` | Non-word boundary |

### Character Classes

| Method | Pattern | Description |
|--------|---------|-------------|
| `anyChar()` | `.` | Any character (except newline) |
| `digit()` | `\d` | Digit (0-9) |
| `nonDigit()` | `\D` | Non-digit |
| `whitespace()` | `\s` | Whitespace |
| `nonWhitespace()` | `\S` | Non-whitespace |
| `wordChar()` | `\w` | Word character (a-z, A-Z, 0-9, _) |
| `nonWordChar()` | `\W` | Non-word character |
| `tab()` | `\t` | Tab character |
| `newline()` | `\n` | Newline character |

### Unicode Support

| Method | Pattern | Description |
|--------|---------|-------------|
| `unicodeProperty("L")` | `\p{L}` | Unicode property |
| `notUnicodeProperty("L")` | `\P{L}` | Negated Unicode property |
| `unicodeScript("Greek")` | `\p{IsGreek}` | Unicode script |
| `unicodeBlock("BasicLatin")` | `\p{InBasicLatin}` | Unicode block |
| `unicodeLetter()` | `\p{L}` | Any Unicode letter |
| `unicodeUppercaseLetter()` | `\p{Lu}` | Unicode uppercase letter |
| `unicodeLowercaseLetter()` | `\p{Ll}` | Unicode lowercase letter |
| `unicodeNumber()` | `\p{N}` | Any Unicode numeric character |
| `unicodePunctuation()` | `\p{P}` | Unicode punctuation |
| `unicodeSymbol()` | `\p{S}` | Unicode symbol |

### ASCII Character Ranges

| Method | Pattern | Description |
|--------|---------|-------------|
| `asciiLowercase()` | `[a-z]` | ASCII lowercase letters |
| `asciiUppercase()` | `[A-Z]` | ASCII uppercase letters |
| `asciiLetter()` | `[a-zA-Z]` | ASCII letters |
| `asciiAlphanumeric()` | `[a-zA-Z0-9]` | ASCII alphanumeric |
| `hexDigit()` | `[0-9a-fA-F]` | Hexadecimal digits |

### Korean (Hangul) Support

| Method | Pattern | Description |
|--------|---------|-------------|
| `hangul()` | `[가-힣]` | Complete Hangul syllable (완성형 한글) |
| `hangulJamo()` | `[ㄱ-ㅣ]` | Hangul Jamo - consonants and vowels (한글 자모) |
| `hangulConsonant()` | `[ㄱ-ㅎ]` | Hangul consonants only (한글 자음) |
| `hangulVowel()` | `[ㅏ-ㅣ]` | Hangul vowels only (한글 모음) |

These patterns work in both `RegexBuilder` and `CharClassBuilder` contexts:

```kotlin
// Match Korean text
val koreanPattern = regex {
    startOfLine()
    oneOrMore { hangul() }
    endOfLine()
}
println(koreanPattern.matches("안녕하세요"))  // true

// Combine with other characters in charClass
val mixed = regex {
    oneOrMore {
        charClass {
            hangul()      // Works inside charClass too!
            hangulJamo()  // Include Jamo as well
            digit()
        }
    }
}
println(mixed.matches("가격1000ㅋㅋ"))  // true
// Generated pattern: [가-힣ㄱ-ㅣ\d]+
```

### POSIX Character Classes

| Method | Pattern | Description |
|--------|---------|-------------|
| `posixAlnum()` | `\p{Alnum}` | Alphanumeric `[a-zA-Z0-9]` |
| `posixAlpha()` | `\p{Alpha}` | Alphabetic `[a-zA-Z]` |
| `posixAscii()` | `\p{ASCII}` | ASCII characters `[\x00-\x7F]` |
| `posixBlank()` | `\p{Blank}` | Space and tab `[ \t]` |
| `posixCntrl()` | `\p{Cntrl}` | Control characters |
| `posixDigit()` | `\p{Digit}` | Digits `[0-9]` |
| `posixGraph()` | `\p{Graph}` | Visible characters (no space) |
| `posixLower()` | `\p{Lower}` | Lowercase `[a-z]` |
| `posixPrint()` | `\p{Print}` | Printable characters |
| `posixPunct()` | `\p{Punct}` | Punctuation |
| `posixSpace()` | `\p{Space}` | Whitespace `[ \t\n\r\f\v]` |
| `posixUpper()` | `\p{Upper}` | Uppercase `[A-Z]` |
| `posixXDigit()` | `\p{XDigit}` | Hex digits `[0-9a-fA-F]` |

### Literals & Character Sets

| Method | Pattern | Description |
|--------|---------|-------------|
| `literal("text")` | (escaped) | Match text literally |
| `char('x')` | (escaped) | Match single character |
| `anyOf("abc")` | `[abc]` | Any of these characters |
| `noneOf("abc")` | `[^abc]` | None of these characters |
| `range('a', 'z')` | `[a-z]` | Character range |
| `notInRange('a', 'z')` | `[^a-z]` | Negated character range |
| `raw("pattern")` | (as-is) | Raw pattern (no escaping) |

### Character Class Builder

```kotlin
charClass {
    range('a', 'z')
    range('A', 'Z')
    chars("_-")
    digit()
}
// Results in: [a-zA-Z_\-\d]

negatedCharClass {
    range('0', '9')
}
// Results in: [^0-9]
```

#### ASCII Range Shortcuts

| Method | Pattern | Description |
|--------|---------|-------------|
| `asciiLowercase()` | `a-z` | ASCII lowercase letters |
| `asciiUppercase()` | `A-Z` | ASCII uppercase letters |
| `asciiLetter()` | `a-zA-Z` | All ASCII letters |
| `asciiAlphanumeric()` | `a-zA-Z0-9` | ASCII letters and digits |
| `hexDigit()` | `0-9a-fA-F` | Hexadecimal digits |

```kotlin
// Before
charClass {
    range('0', '9')
    range('a', 'f')
    range('A', 'F')
}

// After
charClass { hexDigit() }
```

### Groups & Capturing

| Method | Pattern | Description |
|--------|---------|-------------|
| `capture { }` | `(...)` | Capturing group |
| `capture("name") { }` | `(?<name>...)` | Named capturing group |
| `group { }` | `(?:...)` | Non-capturing group |
| `atomicGroup { }` | `(?>...)` | Atomic group (no backtracking) |

### Back References

| Method | Pattern | Description |
|--------|---------|-------------|
| `backReference(1)` | `\1` | Reference to group 1 |
| `backReference("name")` | `\k<name>` | Reference to named group |

### Quantifiers

| Method | Pattern | Description |
|--------|---------|-------------|
| `optional { }` | `(?:...)?` | 0 or 1 time |
| `zeroOrMore { }` | `(?:...)*` | 0 or more times |
| `oneOrMore { }` | `(?:...)+` | 1 or more times |
| `repeat(n) { }` | `(?:...){n}` | Exactly n times |
| `repeat(min, max) { }` | `(?:...){min,max}` | Between min and max times |
| `atLeast(n) { }` | `(?:...){n,}` | At least n times |

### Lazy Quantifiers

Lazy quantifiers match as few characters as possible:

```kotlin
oneOrMoreLazy { anyChar() }     // (?:.)+?
zeroOrMoreLazy { anyChar() }    // (?:.)*?
optionalLazy { digit() }        // (?:\d)??
```

### Possessive Quantifiers

Possessive quantifiers don't backtrack:

```kotlin
oneOrMorePossessive { digit() }     // (?:\d)++
zeroOrMorePossessive { anyChar() }  // (?:.)*+
optionalPossessive { digit() }      // (?:\d)?+
```

### Quantifier Mode Parameter

You can also specify the mode as a parameter:

```kotlin
oneOrMore(QuantifierMode.LAZY) { digit() }
repeat(2, 5, QuantifierMode.POSSESSIVE) { wordChar() }
```

### Lookaround Assertions

| Method | Pattern | Description |
|--------|---------|-------------|
| `lookAhead { }` | `(?=...)` | Positive lookahead |
| `negativeLookAhead { }` | `(?!...)` | Negative lookahead |
| `lookBehind { }` | `(?<=...)` | Positive lookbehind |
| `negativeLookBehind { }` | `(?<!...)` | Negative lookbehind |

```kotlin
// Match digits followed by "px"
regex {
    oneOrMore { digit() }
    lookAhead { literal("px") }
}

// Match digits NOT preceded by "$"
regex {
    negativeLookBehind { literal("$") }
    oneOrMore { digit() }
}
```

### Inline Modifiers

| Method | Pattern | Description |
|--------|---------|-------------|
| `caseInsensitive { }` | `(?i:...)` | Case-insensitive matching |
| `multiline { }` | `(?m:...)` | Multiline mode |
| `dotAll { }` | `(?s:...)` | Dot matches newlines |
| `comments { }` | `(?x:...)` | Comments mode |

### Alternation

```kotlin
// Using or()
regex {
    literal("cat")
    or()
    literal("dog")
}

// Using either()
regex {
    either(
        { literal("cat") },
        { literal("dog") },
        { literal("bird") }
    )
}
```

## Pre-built Patterns

Kregex provides pre-built patterns for common use cases. These are extension functions on `RegexBuilder` that you can use directly in your regex definitions.

### Email & URL

| Method | Description | Example Match |
|--------|-------------|---------------|
| `email()` | Basic email pattern | `user@example.com` |
| `httpUrl()` | HTTP/HTTPS URL | `https://example.com/path` |
| `httpUrlWithCapture()` | URL with named captures (protocol, domain, port, path) | `https://example.com:8080/api` |

```kotlin
val pattern = regex {
    startOfLine()
    email()
    endOfLine()
}
println(pattern.matches("user@example.com"))  // true
```

### IP Addresses

| Method | Description | Example Match |
|--------|-------------|---------------|
| `ipv4()` | IPv4 address (format only) | `192.168.1.1` |
| `ipv4Strict()` | IPv4 with octet validation (0-255) | `192.168.1.1` |
| `ipv6()` | IPv6 address (8 groups) | `2001:0db8:85a3:0000:0000:8a2e:0370:7334` |

```kotlin
val pattern = regex {
    startOfLine()
    ipv4Strict()
    endOfLine()
}
println(pattern.matches("192.168.1.1"))  // true
println(pattern.matches("256.1.1.1"))    // false (invalid octet)
```

### Phone Numbers

| Method | Description | Example Match |
|--------|-------------|---------------|
| `phoneNumber()` | Flexible phone format | `+1-123-456-7890`, `(123) 456-7890` |
| `usPhoneNumber()` | US phone format | `(123) 456-7890`, `123-456-7890` |

### Date & Time

| Method | Description | Example Match |
|--------|-------------|---------------|
| `isoDate()` | ISO 8601 date | `2026-01-15` |
| `time()` | Time (HH:MM or HH:MM:SS) | `14:30`, `14:30:59` |
| `isoDateTime()` | ISO 8601 datetime | `2026-01-15T14:30:00Z` |

```kotlin
val pattern = regex {
    startOfLine()
    isoDateTime()
    endOfLine()
}
println(pattern.matches("2026-01-15T14:30:00Z"))       // true
println(pattern.matches("2026-01-15T14:30:00+09:00"))  // true
```

### Identifiers

| Method | Description | Example Match |
|--------|-------------|---------------|
| `uuid()` | UUID format | `550e8400-e29b-41d4-a716-446655440000` |
| `hexColor()` | Hex color (#RGB, #RRGGBB) | `#fff`, `#FF5733` |
| `slug()` | URL-friendly identifier | `my-blog-post-123` |
| `semver()` | Semantic version | `1.0.0`, `2.1.3-alpha.1` |

```kotlin
val pattern = regex {
    startOfLine()
    hexColor()
    endOfLine()
}
println(pattern.matches("#FF5733"))  // true
println(pattern.matches("#fff"))     // true
```

### Numbers & Text

| Method | Description | Example Match |
|--------|-------------|---------------|
| `word()` | One or more word characters | `Hello` |
| `integer()` | Integer with optional sign | `123`, `-456`, `+789` |
| `decimal()` | Decimal number | `123.456`, `-0.5`, `.25` |
| `quotedString()` | Double-quoted string | `"hello"` |
| `singleQuotedString()` | Single-quoted string | `'hello'` |

```kotlin
val pattern = regex {
    startOfLine()
    decimal()
    endOfLine()
}
println(pattern.matches("123.456"))  // true
println(pattern.matches("-0.5"))     // true
println(pattern.matches(".25"))      // true
```

## Debugging Patterns

Use the standard `Regex.pattern` property to inspect the generated pattern:

```kotlin
val regex = regex {
    startOfLine()
    oneOrMore { digit() }
    literal("@")
    oneOrMore { wordChar() }
    endOfLine()
}

println("Generated pattern: ${regex.pattern}")
// Output: ^(?:\d)+\Q@\E(?:\w)+$
```

## Input Validation

The library validates inputs and throws `IllegalArgumentException` for invalid parameters:

```kotlin
range('z', 'a')           // Error: Range start must be <= end
repeat(-1) { digit() }    // Error: Repeat count must be non-negative
capture("123") { }        // Error: Name must start with a letter
backReference(0)          // Error: Group number must be >= 1
```

## Comparison

### Traditional Regex

```kotlin
val pattern = Regex("^(?:[a-zA-Z0-9._%-]+)@(?:[a-zA-Z0-9-]+)\\.(?:[a-zA-Z]{2,6})$")
```

### Kregex DSL

```kotlin
val pattern = regex {
    startOfLine()
    oneOrMore {
        charClass {
            asciiAlphanumeric()
            chars("._%-")
        }
    }
    literal("@")
    oneOrMore {
        charClass {
            asciiAlphanumeric()
            chars("-")
        }
    }
    literal(".")
    repeat(2, 6) {
        charClass { asciiLowercase() }
    }
    endOfLine()
}
```

## License

```
Copyright 2026 bedshanty

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

Inspired by similar DSL libraries in other languages and the need for more readable regex patterns in Kotlin.  
Assisted by AI tools for boilerplate generation and documentation.