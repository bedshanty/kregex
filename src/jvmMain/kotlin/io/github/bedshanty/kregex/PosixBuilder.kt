package io.github.bedshanty.kregex

/**
 * POSIX character classes - JVM only.
 *
 * These use Java's `\p{...}` POSIX syntax which is not available on JS/Native.
 *
 * @since 0.3.0
 */

// =============================================================================
// Direct POSIX Methods (JVM Only)
// =============================================================================

/**
 * Matches alphanumeric characters (`\p{Alnum}`).
 * Equivalent to `[a-zA-Z0-9]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixAlnum(): Unit = appendRaw("\\p{Alnum}")

/**
 * Matches alphabetic characters (`\p{Alpha}`).
 * Equivalent to `[a-zA-Z]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixAlpha(): Unit = appendRaw("\\p{Alpha}")

/**
 * Matches ASCII characters (`\p{ASCII}`).
 * Equivalent to `[\x00-\x7F]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixAscii(): Unit = appendRaw("\\p{ASCII}")

/**
 * Matches blank characters (`\p{Blank}`).
 * Equivalent to `[ \t]` (space and tab).
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixBlank(): Unit = appendRaw("\\p{Blank}")

/**
 * Matches control characters (`\p{Cntrl}`).
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixCntrl(): Unit = appendRaw("\\p{Cntrl}")

/**
 * Matches digit characters (`\p{Digit}`).
 * Equivalent to `[0-9]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixDigit(): Unit = appendRaw("\\p{Digit}")

/**
 * Matches visible characters (`\p{Graph}`).
 * Printable characters excluding space.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixGraph(): Unit = appendRaw("\\p{Graph}")

/**
 * Matches lowercase letters (`\p{Lower}`).
 * Equivalent to `[a-z]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixLower(): Unit = appendRaw("\\p{Lower}")

/**
 * Matches printable characters (`\p{Print}`).
 * Visible characters plus space.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixPrint(): Unit = appendRaw("\\p{Print}")

/**
 * Matches punctuation characters (`\p{Punct}`).
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixPunct(): Unit = appendRaw("\\p{Punct}")

/**
 * Matches whitespace characters (`\p{Space}`).
 * Equivalent to `[ \t\n\r\f\v]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixSpace(): Unit = appendRaw("\\p{Space}")

/**
 * Matches uppercase letters (`\p{Upper}`).
 * Equivalent to `[A-Z]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixUpper(): Unit = appendRaw("\\p{Upper}")

/**
 * Matches hexadecimal digits (`\p{XDigit}`).
 * Equivalent to `[0-9a-fA-F]`.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.posixXDigit(): Unit = appendRaw("\\p{XDigit}")

// =============================================================================
// POSIX Builder (JVM Only)
// =============================================================================

/**
 * Builder for POSIX character classes within a character class.
 *
 * This builder provides a convenient DSL for adding POSIX character classes.
 * It can be used within [posix] blocks.
 *
 * Example:
 * ```kotlin
 * charClass {
 *     posix {
 *         alnum()    // \p{Alnum}
 *         punct()    // \p{Punct}
 *     }
 * }
 * ```
 *
 * JVM only - not available on JS or Native platforms.
 *
 * @since 0.3.0
 */
@KregexDsl
public class PosixBuilder internal constructor(private val delegate: CharacterRangeCapable) {

    /** Matches alphanumeric characters (`\p{Alnum}`). */
    public fun alnum(): Unit = delegate.posixAlnum()

    /** Matches alphabetic characters (`\p{Alpha}`). */
    public fun alpha(): Unit = delegate.posixAlpha()

    /** Matches ASCII characters (`\p{ASCII}`). */
    public fun ascii(): Unit = delegate.posixAscii()

    /** Matches blank characters (`\p{Blank}`). */
    public fun blank(): Unit = delegate.posixBlank()

    /** Matches control characters (`\p{Cntrl}`). */
    public fun cntrl(): Unit = delegate.posixCntrl()

    /** Matches digit characters (`\p{Digit}`). */
    public fun digit(): Unit = delegate.posixDigit()

    /** Matches visible characters (`\p{Graph}`). */
    public fun graph(): Unit = delegate.posixGraph()

    /** Matches lowercase letters (`\p{Lower}`). */
    public fun lower(): Unit = delegate.posixLower()

    /** Matches printable characters (`\p{Print}`). */
    public fun print(): Unit = delegate.posixPrint()

    /** Matches punctuation characters (`\p{Punct}`). */
    public fun punct(): Unit = delegate.posixPunct()

    /** Matches whitespace characters (`\p{Space}`). */
    public fun space(): Unit = delegate.posixSpace()

    /** Matches uppercase letters (`\p{Upper}`). */
    public fun upper(): Unit = delegate.posixUpper()

    /** Matches hexadecimal digits (`\p{XDigit}`). */
    public fun xdigit(): Unit = delegate.posixXDigit()
}

/**
 * Adds POSIX character classes using a builder.
 *
 * This provides a convenient DSL for combining POSIX classes.
 *
 * Example:
 * ```kotlin
 * charClass {
 *     posix {
 *         alnum()    // \p{Alnum}
 *         punct()    // \p{Punct}
 *     }
 * }
 * ```
 *
 * JVM only - not available on JS or Native platforms.
 *
 * @param block The builder block for specifying POSIX classes.
 * @since 0.3.0
 */
public fun CharacterRangeCapable.posix(block: PosixBuilder.() -> Unit) {
    val builder = PosixBuilder(this)
    builder.block()
}

/**
 * Creates a character class with POSIX character classes using a builder.
 *
 * Example:
 * ```kotlin
 * regex {
 *     posix {
 *         alnum()    // \p{Alnum}
 *         punct()    // \p{Punct}
 *     }
 * }
 * // Result: [\p{Alnum}\p{Punct}]
 * ```
 *
 * JVM only - not available on JS or Native platforms.
 *
 * @param block The builder block for specifying POSIX classes.
 * @since 0.3.0
 */
public fun RegexBuilder.posix(block: PosixBuilder.() -> Unit) {
    val charClassBuilder = CharClassBuilder()
    val posixBuilder = PosixBuilder(charClassBuilder)
    posixBuilder.block()
    appendRaw(charClassBuilder.build())
}
