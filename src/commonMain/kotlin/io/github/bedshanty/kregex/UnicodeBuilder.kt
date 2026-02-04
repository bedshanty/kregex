package io.github.bedshanty.kregex

/**
 * Unicode character classes - common implementation.
 *
 * Provides Unicode property escapes (`\p{...}`) that work across platforms.
 *
 * Platform support:
 * - JVM: Full support
 * - JS (ES2018+): Full support (Kotlin/JS auto-includes 'u' flag)
 * - Apple Native (macOS, iOS): Full support via ICU
 * - Linux/Windows Native: Limited support (POSIX regex)
 *
 * @since 0.4.0
 */

// =============================================================================
// Unicode Property Methods (Common)
// =============================================================================

/**
 * Appends a Unicode property class (`\p{...}`).
 * Matches any character with the specified Unicode property.
 *
 * @param property The Unicode property name (e.g., "L", "N", "P").
 */
public fun CharacterRangeCapable.unicodeProperty(property: String): Unit =
    appendRaw("\\p{$property}")

/**
 * Appends a negated Unicode property class (`\P{...}`).
 * Matches any character that does NOT have the specified Unicode property.
 *
 * @param property The Unicode property name.
 */
public fun CharacterRangeCapable.notUnicodeProperty(property: String): Unit =
    appendRaw("\\P{$property}")

// =============================================================================
// Unicode Convenience Methods (Common)
// =============================================================================

/**
 * Matches any Unicode letter character (`\p{L}`).
 */
public fun CharacterRangeCapable.unicodeLetter(): Unit = unicodeProperty("L")

/**
 * Matches any Unicode uppercase letter (`\p{Lu}`).
 */
public fun CharacterRangeCapable.unicodeUppercaseLetter(): Unit = unicodeProperty("Lu")

/**
 * Matches any Unicode lowercase letter (`\p{Ll}`).
 */
public fun CharacterRangeCapable.unicodeLowercaseLetter(): Unit = unicodeProperty("Ll")

/**
 * Matches any Unicode numeric character (`\p{N}`).
 */
public fun CharacterRangeCapable.unicodeNumber(): Unit = unicodeProperty("N")

/**
 * Matches any Unicode punctuation character (`\p{P}`).
 */
public fun CharacterRangeCapable.unicodePunctuation(): Unit = unicodeProperty("P")

/**
 * Matches any Unicode symbol character (`\p{S}`).
 */
public fun CharacterRangeCapable.unicodeSymbol(): Unit = unicodeProperty("S")

// =============================================================================
// Unicode Builder (Common)
// =============================================================================

/**
 * Builder for Unicode character classes within a character class.
 *
 * This builder provides a convenient DSL for adding Unicode character classes.
 * Platform-specific methods like `script()` and `block()` are available
 * as extension functions in platform source sets.
 *
 * Example:
 * ```kotlin
 * charClass {
 *     unicode {
 *         letter()       // \p{L}
 *         number()       // \p{N}
 *     }
 * }
 * ```
 *
 * @since 0.4.0
 */
@KregexDsl
public class UnicodeBuilder internal constructor(
    @PublishedApi internal val delegate: CharacterRangeCapable
) {

    /**
     * Matches characters with the specified Unicode property (`\p{...}`).
     */
    public fun property(property: String): Unit = delegate.unicodeProperty(property)

    /**
     * Matches characters WITHOUT the specified Unicode property (`\P{...}`).
     */
    public fun notProperty(property: String): Unit = delegate.notUnicodeProperty(property)

    /**
     * Matches any Unicode letter character (`\p{L}`).
     */
    public fun letter(): Unit = delegate.unicodeLetter()

    /**
     * Matches any Unicode uppercase letter (`\p{Lu}`).
     */
    public fun uppercaseLetter(): Unit = delegate.unicodeUppercaseLetter()

    /**
     * Matches any Unicode lowercase letter (`\p{Ll}`).
     */
    public fun lowercaseLetter(): Unit = delegate.unicodeLowercaseLetter()

    /**
     * Matches any Unicode numeric character (`\p{N}`).
     */
    public fun number(): Unit = delegate.unicodeNumber()

    /**
     * Matches any Unicode punctuation character (`\p{P}`).
     */
    public fun punctuation(): Unit = delegate.unicodePunctuation()

    /**
     * Matches any Unicode symbol character (`\p{S}`).
     */
    public fun symbol(): Unit = delegate.unicodeSymbol()
}

/**
 * Adds Unicode character classes using a builder.
 *
 * Example:
 * ```kotlin
 * charClass {
 *     unicode {
 *         letter()       // \p{L}
 *         number()       // \p{N}
 *     }
 * }
 * ```
 *
 * @param block The builder block for specifying Unicode classes.
 * @since 0.4.0
 */
public fun CharacterRangeCapable.unicode(block: UnicodeBuilder.() -> Unit) {
    val builder = UnicodeBuilder(this)
    builder.block()
}

/**
 * Creates a character class with Unicode character classes using a builder.
 *
 * Example:
 * ```kotlin
 * regex {
 *     unicode {
 *         letter()       // \p{L}
 *         number()       // \p{N}
 *     }
 * }
 * // Result: [\p{L}\p{N}]
 * ```
 *
 * @param block The builder block for specifying Unicode classes.
 * @since 0.4.0
 */
public fun RegexBuilder.unicode(block: UnicodeBuilder.() -> Unit) {
    val charClassBuilder = CharClassBuilder()
    val unicodeBuilder = UnicodeBuilder(charClassBuilder)
    unicodeBuilder.block()
    appendRaw(charClassBuilder.build())
}
