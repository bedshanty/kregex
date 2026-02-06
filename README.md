# Kregex

[English](README.md) | [한국어](README.ko.md)

[![CI](https://github.com/bedshanty/kregex/actions/workflows/ci.yml/badge.svg)](https://github.com/bedshanty/kregex/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.bedshanty/kregex)](https://central.sonatype.com/artifact/io.github.bedshanty/kregex)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Kregex** is a Kotlin Multiplatform DSL library for building regular expressions in a readable, type-safe, and maintainable way.

Instead of writing cryptic regex patterns like `^(?:[a-zA-Z0-9._%-]+)@(?:[a-zA-Z0-9-]+)\.(?:[a-zA-Z]{2,6})$`, you can express them as clear, self-documenting Kotlin code.

## Features

- **100% regex syntax support** - All standard regex features work on every platform
- **Kotlin Multiplatform** - Supports JVM, JS, and Native (macOS, iOS, Linux, Windows)
- **Pre-built patterns** - Ready-to-use patterns for email, password, URL, date, and more
- **Type-safe DSL** - Compile-time safety with Kotlin's DSL markers
- **Readable patterns** - Self-documenting regex construction
- **Full regex support** - Anchors, character classes, quantifiers, lookarounds, back references
- **Unicode support** - Unicode properties, scripts (JVM/JS), and blocks (JVM only)
- **Lazy & Possessive quantifiers** - Fine-grained matching control (Possessive: JVM only)
- **Pattern debugging** - Get the generated pattern string for inspection
- **Zero dependencies** - Pure Kotlin, no external dependencies

## Platform Support

| Feature | JVM | JS | Native |
|---------|-----|----|----|
| Core DSL | ✅ | ✅ | ✅ |
| Unicode Categories (`\p{L}`) | ✅ | ✅ | ✅ |
| Unicode Script | ✅ `\p{IsHan}` | ✅ `\p{Script=Han}` | ❌ |
| Unicode Block (`\p{InBasicLatin}`) | ✅ | ❌ | ❌ |
| POSIX Classes (`\p{Alnum}`) | ✅ | ❌ | ❌ |
| Possessive Quantifiers | ✅ | ❌ | ❌ |

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("io.github.bedshanty:kregex-jvm:0.4.0")
```

### Gradle (Groovy)

```groovy
implementation 'io.github.bedshanty:kregex-jvm:0.4.0'
```

### Maven

```xml
<dependency>
    <groupId>io.github.bedshanty</groupId>
    <artifactId>kregex-jvm</artifactId>
    <version>0.4.0</version>
</dependency>
```

### Kotlin Multiplatform

```kotlin
// commonMain
implementation("io.github.bedshanty:kregex:0.4.0")

// Or platform-specific
implementation("io.github.bedshanty:kregex-js:0.4.0")               // JS
implementation("io.github.bedshanty:kregex-macosx64:0.4.0")         // macOS x64
implementation("io.github.bedshanty:kregex-macosarm64:0.4.0")       // macOS ARM64
implementation("io.github.bedshanty:kregex-iosx64:0.4.0")           // iOS x64
implementation("io.github.bedshanty:kregex-iosarm64:0.4.0")         // iOS ARM64
implementation("io.github.bedshanty:kregex-iossimulatorarm64:0.4.0") // iOS Simulator ARM64
implementation("io.github.bedshanty:kregex-linuxx64:0.4.0")         // Linux x64
implementation("io.github.bedshanty:kregex-linuxarm64:0.4.0")       // Linux ARM64
implementation("io.github.bedshanty:kregex-mingwx64:0.4.0")         // Windows x64
```

## Quick Start

```kotlin
// Simple pattern
val digitPattern = regex {
    line {
        oneOrMore { digit() }
    }
}

println(digitPattern.matches("12345")) // true
println(digitPattern.matches("abc"))   // false
```

## Usage Examples

### Hex Color

```kotlin
// #[0-9A-Fa-f]{6}
val hexColorPattern = regex {
    literal("#")
    repeat(6) { hexDigit() }
}

println(hexColorPattern.matches("#FF5733"))  // true
println(hexColorPattern.matches("#abc123"))  // true
println(hexColorPattern.matches("#GHIJKL"))  // false
```

### Phone Number

```kotlin
// ^(?:\+(?:\d){1,3}[ -])?(?:\d){3}[ -](?:\d){3,4}[ -](?:\d){4}$
val phonePattern = regex {
    line {
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
    }
}

println(phonePattern.matches("123-456-7890"))     // true
println(phonePattern.matches("123 456 7890"))     // true
println(phonePattern.matches("+1-123-456-7890"))  // true
println(phonePattern.matches("+82-123-456-7890")) // true
```

### Password Validation

```kotlin
// ^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{2,16}$
// 2-16 chars, requires: lowercase, uppercase, digit, special char (!@#$%^&*)
val passwordPattern = regex {
    line {
        lookAhead { zeroOrMore { anyChar() }; asciiLowercase() }
        lookAhead { zeroOrMore { anyChar() }; asciiUppercase() }
        lookAhead { zeroOrMore { anyChar() }; asciiDigit() }
        lookAhead { zeroOrMore { anyChar() }; anyOf("!@#$%^&*") }
        repeat(2, 16) {
            anyOf {
                asciiAlphanumeric()
                chars("!@#$%^&*")
            }
        }
    }
}

println(passwordPattern.matches("aA1!"))        // true
println(passwordPattern.matches("MyP@ssw0rd"))  // true
println(passwordPattern.matches("abcd1234"))    // false (no uppercase, no special char)
println(passwordPattern.matches("a"))           // false (too short)
```

### URL Parsing with Named Captures

```kotlin
// ^(?<protocol>https|http)://(?<domain>[\w.-]+)(?<port>:\d+)?(?<path>[\w/.-]*)?$
val urlPattern = regex {
    line {
        captureAs("protocol") {
            either(
                { literal("https") },
                { literal("http") }
            )
        }
        literal("://")
        captureAs("domain") {
            oneOrMore { anyOf { wordChar(); chars(".-") } }
        }
        optional {
            captureAs("port") {
                literal(":")
                oneOrMore { digit() }
            }
        }
        optional {
            captureAs("path") {
                zeroOrMore { anyOf { wordChar(); chars("/.-") } }
            }
        }
    }
}

val match = urlPattern.find("https://example.com:8080/api/v1")!!
println(match.groups["protocol"]?.value)  // https
println(match.groups["domain"]?.value)    // example.com
println(match.groups["port"]?.value)      // :8080
```

### HTML Tag Matching with Back References

```kotlin
// <(?<tag>\w+).*>.*?</\k<tag>>
val htmlTag = regex {
    literal("<")
    captureAs("tag") { oneOrMore { wordChar() } }
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
| `line { }` | `^...$` | Wraps with start/end of line anchors |
| `input { }` | `\A...\z` | Wraps with start/end of input anchors |

#### Line & Input Blocks

Instead of manually adding anchors, use the convenience blocks:

```kotlin
// Before
regex {
    startOfLine()
    oneOrMore { digit() }
    endOfLine()
}

// After
regex {
    line { oneOrMore { digit() } }
}
// Result: ^\d+$

regex {
    input { oneOrMore { digit() } }
}
// Result: \A\d+\z
```

### Character Classes

| Method | Pattern | Description |
|--------|---------|-------------|
| `anyChar()` | `.` | Any character (except newline) |
| `digit()` | `\d` | Digit (may include unicode digits) |
| `asciiDigit()` | `[0-9]` | ASCII digit only (0-9) |
| `nonDigit()` | `\D` | Non-digit |
| `whitespace()` | `\s` | Whitespace |
| `nonWhitespace()` | `\S` | Non-whitespace |
| `wordChar()` | `\w` | Word character (a-z, A-Z, 0-9, _) |
| `nonWordChar()` | `\W` | Non-word character |
| `tab()` | `\t` | Tab character |
| `newline()` | `\n` | Newline character |
| `carriageReturn()` | `\r` | Carriage return |
| `formFeed()` | `\f` | Form feed |
| `alert()` | `\a` | Alert/bell character |
| `escape()` | `\e` | Escape character |

> **Note**: `digit()` uses `\d` which may match unicode digits depending on regex flags. Use `asciiDigit()` for strict ASCII digits (0-9).

### Unicode Support

| Method | Pattern | Description | Platform |
|--------|---------|-------------|----------|
| `unicodeProperty("L")` | `\p{L}` | Unicode property | All |
| `notUnicodeProperty("L")` | `\P{L}` | Negated Unicode property | All |
| `unicodeScript("Greek")` | `\p{IsGreek}` (JVM) / `\p{Script=Greek}` (JS) | Unicode script | JVM, JS |
| `unicodeBlock("BasicLatin")` | `\p{InBasicLatin}` | Unicode block | JVM only |
| `unicodeLetter()` | `\p{L}` | Any Unicode letter | All |
| `unicodeUppercaseLetter()` | `\p{Lu}` | Unicode uppercase letter | All |
| `unicodeLowercaseLetter()` | `\p{Ll}` | Unicode lowercase letter | All |
| `unicodeNumber()` | `\p{N}` | Any Unicode numeric character | All |
| `unicodePunctuation()` | `\p{P}` | Unicode punctuation | All |
| `unicodeSymbol()` | `\p{S}` | Unicode symbol | All |

> **Note**: On JavaScript, the `u` flag is automatically included by Kotlin/JS, so Unicode features work out of the box.

#### Unicode Block

Use `unicode { }` block for combining Unicode classes:

```kotlin
regex {
    unicode {
        letter()       // \p{L}
        number()       // \p{N}
        script("Han")  // \p{IsHan} (JVM) / \p{Script=Han} (JS)
    }
}
// Result: [\p{L}\p{N}\p{IsHan}] (JVM) / [\p{L}\p{N}\p{Script=Han}] (JS)
```

Available methods inside `unicode { }`:
- `property(name)` - Unicode property (`\p{...}`)
- `notProperty(name)` - Negated property (`\P{...}`)
- `script(name)` - Unicode script (`\p{Is...}`) - **JVM/JS only**
- `block(name)` - Unicode block (`\p{In...}`) - **JVM only**
- `letter()`, `uppercaseLetter()`, `lowercaseLetter()`
- `number()`, `punctuation()`, `symbol()`

### ASCII Character Ranges

| Method | Pattern | Description |
|--------|---------|-------------|
| `asciiLowercase()` | `[a-z]` | ASCII lowercase letters |
| `asciiUppercase()` | `[A-Z]` | ASCII uppercase letters |
| `asciiDigit()` | `[0-9]` | ASCII digits |
| `asciiLetter()` | `[a-zA-Z]` | ASCII letters |
| `asciiAlphanumeric()` | `[a-zA-Z0-9]` | ASCII alphanumeric |
| `hexDigit()` | `[0-9a-fA-F]` | Hexadecimal digits |

#### ASCII Block

Use `ascii { }` block for combining ASCII ranges:

```kotlin
regex {
    ascii {
        lower()        // a-z
        upper()        // A-Z
        digit()        // 0-9
    }
}
// Result: [a-zA-Z0-9]

regex {
    ascii { hexDigit() }
}
// Result: [0-9a-fA-F]
```

Available methods inside `ascii { }`:
- `lower()` - ASCII lowercase (a-z)
- `upper()` - ASCII uppercase (A-Z)
- `digit()` - ASCII digits (0-9)
- `letter()` - ASCII letters (a-zA-Z)
- `alphanumeric()` - ASCII alphanumeric (a-zA-Z0-9)
- `hexDigit()` - Hexadecimal digits (0-9a-fA-F)

### Korean (Hangul) Support

| Method | Pattern | Description |
|--------|---------|-------------|
| `hangulSyllable()` | `[가-힣]` | Complete Hangul syllable |
| `hangulJamo()` | `[ㄱ-ㅣ]` | Hangul Jamo - consonants and vowels |
| `hangulConsonant()` | `[ㄱ-ㅎ]` | Hangul consonants only |
| `hangulVowel()` | `[ㅏ-ㅣ]` | Hangul vowels only |

These patterns work in both `RegexBuilder` and `CharClassBuilder` contexts:

```kotlin
// Match Korean text
val koreanPattern = regex {
    line { oneOrMore { hangulSyllable() } }
}
println(koreanPattern.matches("안녕하세요"))  // true

// Combine with other characters using hangul block
val mixed = regex {
    oneOrMore {
        anyOf {
            hangul {
                syllable()  // 가-힣
                jamo()      // ㄱ-ㅣ
            }
            digit()
        }
    }
}
println(mixed.matches("가격1000ㅋㅋ"))  // true
// Generated pattern: [가-힣ㄱ-ㅣ\d]+
```

#### Hangul Block

Use `hangul { }` block for combining Hangul ranges:

```kotlin
regex {
    hangul {
        syllable()    // 가-힣 (완성형)
        consonant()   // ㄱ-ㅎ (자음)
    }
}
// Result: [가-힣ㄱ-ㅎ]
```

Available methods inside `hangul { }`:
- `syllable()` - Complete syllables (가-힣)
- `jamo()` - All Jamo (ㄱ-ㅣ)
- `consonant()` - Consonants only (ㄱ-ㅎ)
- `vowel()` - Vowels only (ㅏ-ㅣ)

### POSIX Character Classes (JVM Only)

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

> **Note**: POSIX classes are only available on JVM.

#### POSIX Block

Use `posix { }` block for combining POSIX classes:

```kotlin
regex {
    posix {
        alnum()    // \p{Alnum}
        punct()    // \p{Punct}
    }
}
// Result: [\p{Alnum}\p{Punct}]
```

Available methods inside `posix { }`:
- `alnum()`, `alpha()`, `ascii()`, `blank()`, `cntrl()`
- `digit()`, `graph()`, `lower()`, `print()`, `punct()`
- `space()`, `upper()`, `xdigit()`

### Literals & Character Sets

| Method | Pattern | Description |
|--------|---------|-------------|
| `literal("text")` | (escaped) | Match text literally |
| `char('x')` | (escaped) | Match single character |
| `anyOf("abc")` | `[abc]` | Any of these characters |
| `anyOf("abc", "xyz")` | `[abcxyz]` | Multiple strings combined |
| `anyOf('a', 'b', 'c')` | `[abc]` | Vararg characters |
| `anyOf { }` | `[...]` | Character class builder (alias for `charClass`) |
| `noneOf("abc")` | `[^abc]` | None of these characters |
| `noneOf("abc", "xyz")` | `[^abcxyz]` | Multiple strings combined (negated) |
| `noneOf('a', 'b', 'c')` | `[^abc]` | Vararg characters (negated) |
| `noneOf { }` | `[^...]` | Negated character class builder (alias for `negatedCharClass`) |
| `range('a', 'z')` | `[a-z]` | Character range |
| `notInRange('a', 'z')` | `[^a-z]` | Negated character range |
| `appendRaw("pattern")` | (as-is) | Raw pattern (no escaping) |

### Character Class Builder

```kotlin
anyOf {
    range('a', 'z')
    range('A', 'Z')
    chars("_-")          // String
    chars('!', '@')      // Vararg Char
    chars("abc", "123")  // Multiple strings
    digit()
}
// Results in: [a-zA-Z_\-!@abc123\d]

noneOf {
    range('0', '9')
}
// Results in: [^0-9]
```

#### ASCII Range Shortcuts

| Method | Pattern | Description |
|--------|---------|-------------|
| `asciiLowercase()` | `a-z` | ASCII lowercase letters |
| `asciiUppercase()` | `A-Z` | ASCII uppercase letters |
| `asciiDigit()` | `0-9` | ASCII digits |
| `asciiLetter()` | `a-zA-Z` | All ASCII letters |
| `asciiAlphanumeric()` | `a-zA-Z0-9` | ASCII letters and digits |
| `hexDigit()` | `0-9a-fA-F` | Hexadecimal digits |

```kotlin
// Using shortcuts directly
anyOf { hexDigit() }
// Result: [0-9a-fA-F]

// Using ascii block inside charClass
anyOf {
    ascii {
        lower()
        digit()
    }
    chars("_")
}
// Result: [a-z0-9_]
```

### Groups & Capturing

| Method | Pattern | Description |
|--------|---------|-------------|
| `capture { }` | `(...)` | Capturing group |
| `captureAs("name") { }` | `(?<name>...)` | Named capturing group |
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

### Possessive Quantifiers (JVM Only)

Possessive quantifiers don't backtrack:

```kotlin
oneOrMorePossessive { digit() }     // (?:\d)++
zeroOrMorePossessive { anyChar() }  // (?:.)*+
optionalPossessive { digit() }      // (?:\d)?+
```

> **Note**: Possessive quantifiers are only available on JVM.

### Quantifier Mode Parameter

You can also specify the mode as a parameter:

```kotlin
oneOrMore(QuantifierMode.LAZY) { digit() }
repeat(2, 5, QuantifierMode.POSSESSIVE) { wordChar() }  // JVM only
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

### Email

| Method | Description | Example Match |
|--------|-------------|---------------|
| `email()` | Basic email pattern | `user@example.com` |

```kotlin
val pattern = regex {
    line { email() }
}
println(pattern.matches("user@example.com"))  // true
```

### Password

| Method | Description | Example Match |
|--------|-------------|---------------|
| `password(...)` | Configurable password validation | `Password1!` |

**`password()` Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `minLength` | `Int` | `8` | Minimum length |
| `maxLength` | `Int?` | `256` | Maximum length (null = unlimited) |
| `requireUppercase` | `Boolean` | `false` | Require uppercase letter |
| `requireLowercase` | `Boolean` | `false` | Require lowercase letter |
| `requireDigit` | `Boolean` | `false` | Require digit |
| `requireSpecialChar` | `Boolean` | `false` | Require special character |
| `allowedSpecialChars` | `String` | OWASP recommended set | Allowed special characters |

**Default special characters (OWASP recommended)**: ` !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~` (includes space)

**Allowed characters**: Only ASCII letters (a-z, A-Z), digits (0-9), and characters specified in `allowedSpecialChars` are allowed. Korean, emojis, etc. are rejected.

```kotlin
val pattern = regex {
    line {
        password(
            minLength = 8,
            maxLength = 20,
            requireUppercase = true,
            requireLowercase = true,
            requireDigit = true,
            requireSpecialChar = true
        )
    }
}
println(pattern.matches("Password1!"))  // true
println(pattern.matches("password"))    // false (requirements not met)
```

### URL

| Method | Description | Example Match |
|--------|-------------|---------------|
| `httpUrl()` | HTTP/HTTPS URL | `https://example.com/path` |
| `httpUrlWithCapture()` | URL with named captures (protocol, domain, port, path) | `https://example.com:8080/api` |

```kotlin
val pattern = regex {
    line { httpUrl() }
}
println(pattern.matches("https://example.com/path"))  // true
```

### IP Addresses

| Method | Description | Example Match |
|--------|-------------|---------------|
| `ipv4()` | IPv4 address (format only) | `192.168.1.1` |
| `ipv4Strict()` | IPv4 with octet validation (0-255) | `192.168.1.1` |
| `ipv6()` | IPv6 address (8 groups) | `2001:0db8:85a3:0000:0000:8a2e:0370:7334` |

```kotlin
val pattern = regex {
    line { ipv4Strict() }
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
    line { isoDateTime() }
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
    line { hexColor() }
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
    line { decimal() }
}
println(pattern.matches("123.456"))  // true
println(pattern.matches("-0.5"))     // true
println(pattern.matches(".25"))      // true
```

## Debugging Patterns

Use the standard `Regex.pattern` property to inspect the generated pattern:

```kotlin
val regex = regex {
    line {
        oneOrMore { digit() }
        literal("@")
        oneOrMore { wordChar() }
    }
}

println("Generated pattern: ${regex.pattern}")
// Output: ^(?:\d)+\Q@\E(?:\w)+$
```

## Input Validation

The library validates inputs and throws `IllegalArgumentException` for invalid parameters:

```kotlin
range('z', 'a')           // Error: Range start must be <= end
repeat(-1) { digit() }    // Error: Repeat count must be non-negative
captureAs("123") { }      // Error: Name must start with a letter
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
    line {
        oneOrMore {
            anyOf {
                asciiAlphanumeric()
                chars("._%-")
            }
        }
        literal("@")
        oneOrMore {
            anyOf {
                asciiAlphanumeric()
                chars("-")
            }
        }
        literal(".")
        repeat(2, 6) {
            anyOf { asciiLowercase() }
        }
    }
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
