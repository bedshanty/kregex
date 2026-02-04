package io.github.bedshanty.kregex

/**
 * Unicode script support - JavaScript (ES2018+) version.
 *
 * JavaScript uses `\p{Script=...}` syntax (not `\p{Is...}` like JVM).
 *
 * The 'u' (unicode) flag is automatically included by Kotlin/JS,
 * so Unicode property escapes work out of the box.
 *
 * Note: Unicode Block (`\p{Block=...}`) is NOT supported in ES2018.
 * Use external libraries like XRegExp if you need block support on JavaScript.
 *
 * @since 0.3.0
 */

// =============================================================================
// Unicode Script (JS ES2018+ Only)
// =============================================================================

/**
 * Appends a Unicode script class (`\p{Script=...}`).
 *
 * JavaScript ES2018+ uses `\p{Script=...}` syntax (not `\p{Is...}` like JVM).
 * The 'u' flag is automatically included by Kotlin/JS.
 *
 * @param script The Unicode script name (e.g., "Greek", "Cyrillic", "Han").
 */
public fun CharacterRangeCapable.unicodeScript(script: String): Unit =
    appendRaw("\\p{Script=$script}")

// =============================================================================
// UnicodeBuilder Extensions (JS ES2018+ Only)
// =============================================================================

/**
 * Matches characters in the specified Unicode script (`\p{Script=...}`).
 *
 * JavaScript ES2018+ uses `\p{Script=...}` syntax (not `\p{Is...}` like JVM).
 *
 * Example:
 * ```kotlin
 * val pattern = regex {
 *     unicode {
 *         script("Han")  // \p{Script=Han} - Chinese characters
 *     }
 * }
 * ```
 */
public fun UnicodeBuilder.script(script: String): Unit =
    delegate.unicodeScript(script)
