package io.github.bedshanty.kregex

/**
 * Common regex patterns as extension functions for RegexBuilder.
 *
 * These are pre-built patterns for frequently used validations.
 * Each function appends the corresponding pattern to the builder.
 *
 * @since 0.1.0
 */

// =============================================================================
// Email Patterns
// =============================================================================

/**
 * Appends a basic email pattern.
 * Matches most common email formats like `user@example.com`.
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     email()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("user@example.com"))
 * ```
 */
fun RegexBuilder.email() {
    // Local part: word chars, dots, and special chars
    oneOrMore {
        anyOf {
            wordChar()
            chars(".%+-")
        }
    }
    literal("@")
    // Domain part (including subdomains)
    oneOrMore {
        anyOf {
            wordChar()
            chars(".-")
        }
    }
    literal(".")
    // TLD (2-63 chars)
    repeat(2, 63) {
        anyOf { asciiLetter() }
    }
}

// =============================================================================
// URL Patterns
// =============================================================================

/**
 * Appends an HTTP/HTTPS URL pattern.
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     httpUrl()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("https://example.com"))
 * assertTrue(pattern.matches("http://sub.example.com/path?query=1"))
 * ```
 */
fun RegexBuilder.httpUrl() {
    // Protocol
    literal("http")
    optional { literal("s") }
    literal("://")
    // Domain
    oneOrMore {
        anyOf {
            wordChar()
            chars(".-")
        }
    }
    // Optional port
    optional {
        literal(":")
        oneOrMore { digit() }
    }
    // Optional path and query
    zeroOrMore {
        anyOf {
            wordChar()
            chars("/.-_~:?#@!$&'()+,;=%")
        }
    }
}

/**
 * Appends a URL pattern with named capture groups.
 *
 * Captured groups:
 * - `protocol`: http or https
 * - `domain`: the domain name
 * - `port`: optional port number (including colon)
 * - `path`: optional path and query string
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     httpUrlWithCapture()
 *     endOfLine()
 * }
 * val match = pattern.find("https://example.com:8080/path")!!
 * println(match.groups["protocol"]?.value) // https
 * println(match.groups["domain"]?.value)   // example.com
 * println(match.groups["port"]?.value)     // :8080
 * println(match.groups["path"]?.value)     // /path
 * ```
 */
fun RegexBuilder.httpUrlWithCapture() {
    captureAs("protocol") {
        literal("http")
        optional { literal("s") }
    }
    literal("://")
    captureAs("domain") {
        oneOrMore {
            anyOf {
                wordChar()
                chars(".-")
            }
        }
    }
    optional {
        captureAs("port") {
            literal(":")
            oneOrMore { digit() }
        }
    }
    optional {
        captureAs("path") {
            oneOrMore {
                anyOf {
                    wordChar()
                    chars("/.-_~:?#@!$&'()+,;=%")
                }
            }
        }
    }
}

// =============================================================================
// IP Address Patterns
// =============================================================================

/**
 * Appends an IPv4 address pattern.
 * Matches addresses like `192.168.1.1`.
 *
 * Note: This pattern matches the format but doesn't validate
 * that each octet is in the range 0-255.
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     ipv4()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("192.168.1.1"))
 * assertTrue(pattern.matches("10.0.0.1"))
 * ```
 */
fun RegexBuilder.ipv4() {
    // First octet
    repeat(1, 3) { digit() }
    // Remaining three octets
    repeat(3) {
        literal(".")
        repeat(1, 3) { digit() }
    }
}

/**
 * Appends a strict IPv4 address pattern that validates octet ranges (0-255).
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     ipv4Strict()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("192.168.1.1"))
 * assertFalse(pattern.matches("256.1.1.1"))
 * ```
 */
fun RegexBuilder.ipv4Strict() {
    val octet: RegexBuilder.() -> Unit = {
        either(
            { literal("25"); range('0', '5') },                    // 250-255
            { literal("2"); range('0', '4'); digit() },            // 200-249
            { literal("1"); digit(); digit() },                    // 100-199
            { range('1', '9'); digit() },                          // 10-99
            { digit() }                                            // 0-9
        )
    }
    octet()
    repeat(3) {
        literal(".")
        octet()
    }
}

/**
 * Appends an IPv6 address pattern (simplified).
 * Matches standard IPv6 format with 8 groups of 4 hex digits.
 *
 * Note: This is a simplified pattern that matches the basic format.
 * It doesn't handle all IPv6 shorthand notations (:: compression).
 */
fun RegexBuilder.ipv6() {
    // First group
    repeat(1, 4) {
        anyOf { hexDigit() }
    }
    // Remaining 7 groups
    repeat(7) {
        literal(":")
        repeat(1, 4) {
            anyOf { hexDigit() }
        }
    }
}

// =============================================================================
// Phone Number Patterns
// =============================================================================

/**
 * Appends a phone number pattern (flexible format).
 * Matches various phone number formats including:
 * - (123) 456-7890
 * - 123-456-7890
 * - 123.456.7890
 * - 1234567890
 * - +1 123 456 7890
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     phoneNumber()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("(123) 456-7890"))
 * assertTrue(pattern.matches("+1-123-456-7890"))
 * ```
 */
fun RegexBuilder.phoneNumber() {
    // Optional country code
    optional {
        literal("+")
        repeat(1, 3) { digit() }
        optional { anyOf(" -") }
    }
    // Optional area code with parentheses
    optional {
        literal("(")
        repeat(3) { digit() }
        literal(")")
        optional { anyOf(" -") }
    }
    // Or area code without parentheses
    optional {
        repeat(3) { digit() }
        optional { anyOf(" .-") }
    }
    // Main number
    repeat(3) { digit() }
    optional { anyOf(" .-") }
    repeat(4) { digit() }
}

/**
 * Appends a US phone number pattern.
 * Matches: (XXX) XXX-XXXX or XXX-XXX-XXXX format.
 */
fun RegexBuilder.usPhoneNumber() {
    either(
        {
            // (XXX) XXX-XXXX
            literal("(")
            repeat(3) { digit() }
            literal(") ")
            repeat(3) { digit() }
            literal("-")
            repeat(4) { digit() }
        },
        {
            // XXX-XXX-XXXX
            repeat(3) { digit() }
            literal("-")
            repeat(3) { digit() }
            literal("-")
            repeat(4) { digit() }
        }
    )
}

// =============================================================================
// Date & Time Patterns
// =============================================================================

/**
 * Appends an ISO 8601 date pattern (YYYY-MM-DD).
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     isoDate()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("2026-01-15"))
 * ```
 */
fun RegexBuilder.isoDate() {
    repeat(4) { digit() }  // Year
    literal("-")
    repeat(2) { digit() }  // Month
    literal("-")
    repeat(2) { digit() }  // Day
}

/**
 * Appends a time pattern (HH:MM:SS or HH:MM).
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     time()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("14:30"))
 * assertTrue(pattern.matches("14:30:59"))
 * ```
 */
fun RegexBuilder.time() {
    repeat(2) { digit() }  // Hours
    literal(":")
    repeat(2) { digit() }  // Minutes
    optional {
        literal(":")
        repeat(2) { digit() }  // Seconds
    }
}

/**
 * Appends an ISO 8601 datetime pattern (YYYY-MM-DDTHH:MM:SS).
 */
fun RegexBuilder.isoDateTime() {
    isoDate()
    literal("T")
    time()
    optional {
        either(
            { literal("Z") },
            {
                anyOf("+-")
                repeat(2) { digit() }
                literal(":")
                repeat(2) { digit() }
            }
        )
    }
}

// =============================================================================
// Identifier Patterns
// =============================================================================

/**
 * Appends a UUID pattern (version 4 format).
 * Matches: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     uuid()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("550e8400-e29b-41d4-a716-446655440000"))
 * ```
 */
fun RegexBuilder.uuid() {
    val hexChar: RegexBuilder.() -> Unit = {
        anyOf { hexDigit() }
    }
    repeat(8) { hexChar() }
    literal("-")
    repeat(4) { hexChar() }
    literal("-")
    repeat(4) { hexChar() }
    literal("-")
    repeat(4) { hexChar() }
    literal("-")
    repeat(12) { hexChar() }
}

/**
 * Appends a hexadecimal color code pattern (#RGB or #RRGGBB).
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     hexColor()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("#fff"))
 * assertTrue(pattern.matches("#FF5733"))
 * ```
 */
fun RegexBuilder.hexColor() {
    literal("#")
    either(
        {
            // #RRGGBB or #RRGGBBAA
            repeat(6) {
                anyOf { hexDigit() }
            }
            optional {
                repeat(2) {
                    anyOf { hexDigit() }
                }
            }
        },
        {
            // #RGB or #RGBA
            repeat(3) {
                anyOf { hexDigit() }
            }
            optional {
                anyOf { hexDigit() }
            }
        }
    )
}

/**
 * Appends a slug pattern (URL-friendly identifier).
 * Matches lowercase letters, numbers, and hyphens.
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     slug()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("my-blog-post-123"))
 * ```
 */
fun RegexBuilder.slug() {
    oneOrMore {
        anyOf {
            asciiLowercase()
            digit()
            chars("-")
        }
    }
}

/**
 * Appends a semantic version pattern (semver).
 * Matches: MAJOR.MINOR.PATCH with optional pre-release and build metadata.
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     semver()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("1.0.0"))
 * assertTrue(pattern.matches("2.1.3-alpha.1"))
 * assertTrue(pattern.matches("1.0.0-beta+build.123"))
 * ```
 */
fun RegexBuilder.semver() {
    // MAJOR.MINOR.PATCH
    oneOrMore { digit() }
    literal(".")
    oneOrMore { digit() }
    literal(".")
    oneOrMore { digit() }
    // Optional pre-release
    optional {
        literal("-")
        oneOrMore {
            anyOf {
                asciiAlphanumeric()
                chars(".-")
            }
        }
    }
    // Optional build metadata
    optional {
        literal("+")
        oneOrMore {
            anyOf {
                asciiAlphanumeric()
                chars(".-")
            }
        }
    }
}

// =============================================================================
// Text Content Patterns
// =============================================================================

/**
 * Appends a word pattern (one or more word characters).
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     word()
 * }
 * assertTrue(pattern.containsMatchIn("Hello world"))
 * ```
 */
fun RegexBuilder.word() {
    oneOrMore { wordChar() }
}

/**
 * Appends an integer pattern (optional sign, one or more digits).
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     integer()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("123"))
 * assertTrue(pattern.matches("-456"))
 * assertTrue(pattern.matches("+789"))
 * ```
 */
fun RegexBuilder.integer() {
    optional { anyOf("+-") }
    oneOrMore { digit() }
}

/**
 * Appends a decimal number pattern.
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     startOfLine()
 *     decimal()
 *     endOfLine()
 * }
 * assertTrue(pattern.matches("123.456"))
 * assertTrue(pattern.matches("-0.5"))
 * assertTrue(pattern.matches(".25"))
 * ```
 */
fun RegexBuilder.decimal() {
    optional { anyOf("+-") }
    either(
        {
            oneOrMore { digit() }
            optional {
                literal(".")
                zeroOrMore { digit() }
            }
        },
        {
            literal(".")
            oneOrMore { digit() }
        }
    )
}

/**
 * Appends a quoted string pattern (double quotes).
 * Handles escaped quotes inside the string.
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     quotedString()
 * }
 * assertTrue(pattern.containsMatchIn("say \"hello\""))
 * ```
 */
fun RegexBuilder.quotedString() {
    literal("\"")
    zeroOrMore {
        either(
            {
                literal("\\")
                anyChar()
            },
            { noneOf("\"\\") }
        )
    }
    literal("\"")
}

/**
 * Appends a single-quoted string pattern.
 * Handles escaped quotes inside the string.
 */
fun RegexBuilder.singleQuotedString() {
    literal("'")
    zeroOrMore {
        either(
            {
                literal("\\")
                anyChar()
            },
            { noneOf("'\\") }
        )
    }
    literal("'")
}

