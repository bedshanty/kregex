@file:JvmName("UnicodeBuilderJvm")

package io.github.bedshanty.kregex

/**
 * Unicode script and block support - JVM version.
 *
 * JVM uses Java's `\p{Is...}` and `\p{In...}` syntax.
 *
 * @since 0.3.0
 */

// =============================================================================
// Unicode Script & Block (JVM Only)
// =============================================================================

/**
 * Appends a Unicode script class (`\p{Is...}`).
 *
 * @param script The Unicode script name (e.g., "Greek", "Cyrillic", "Han").
 */
public fun CharacterRangeCapable.unicodeScript(script: String): Unit =
    appendRaw("\\p{Is$script}")

/**
 * Appends a Unicode block class (`\p{In...}`).
 *
 * @param block The Unicode block name (e.g., "BasicLatin", "CJKUnifiedIdeographs").
 */
public fun CharacterRangeCapable.unicodeBlock(block: String): Unit =
    appendRaw("\\p{In$block}")

// =============================================================================
// UnicodeBuilder Extensions (JVM Only)
// =============================================================================

/**
 * Matches characters in the specified Unicode script (`\p{Is...}`).
 *
 * Example:
 * ```kotlin
 * unicode {
 *     script("Han")  // \p{IsHan} - Chinese characters
 * }
 * ```
 */
public fun UnicodeBuilder.script(script: String): Unit =
    delegate.unicodeScript(script)

/**
 * Matches characters in the specified Unicode block (`\p{In...}`).
 *
 * Example:
 * ```kotlin
 * unicode {
 *     block("CJKUnifiedIdeographs")  // \p{InCJKUnifiedIdeographs}
 * }
 * ```
 */
public fun UnicodeBuilder.block(block: String): Unit =
    delegate.unicodeBlock(block)
