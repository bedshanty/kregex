package io.github.bedshanty.kregex

/**
 * JVM-specific extension functions for [RegexBuilder].
 *
 * These functions use regex constructs only supported by java.util.regex.Pattern.
 * They are not available on JS or Native platforms.
 *
 * @since 0.3.0
 */

// =============================================================================
// Input Anchors (JVM Only)
// =============================================================================

/**
 * Appends the start-of-input anchor (`\A`).
 * Unlike [RegexBuilder.startOfLine], this matches only at the very beginning of input.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.startOfInput(): Unit = appendRaw("\\A")

/**
 * Appends the end-of-input anchor (`\z`).
 * Unlike [RegexBuilder.endOfLine], this matches only at the very end of input.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.endOfInput(): Unit = appendRaw("\\z")

/**
 * Appends the end-of-input anchor (`\Z`).
 * Matches at the end of input, but before any final line terminator.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.endOfInputBeforeNewline(): Unit = appendRaw("\\Z")

/**
 * Wraps the block with start-of-input (`\A`) and end-of-input (`\z`) anchors.
 * Use this to match the entire input.
 *
 * Example:
 * ```kotlin
 * regex {
 *     input { literal("hello") }
 * }
 * // Results in: \Ahello\z
 * ```
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.input(block: RegexBuilder.() -> Unit) {
    startOfInput()
    this.block()
    endOfInput()
}

// =============================================================================
// Control Characters (JVM Only)
// =============================================================================

/**
 * Appends an alert/bell character (`\a`).
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.alert(): Unit = appendRaw("\\a")

/**
 * Appends an escape character (`\e`).
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun CharacterRangeCapable.escape(): Unit = appendRaw("\\e")

// =============================================================================
// Atomic Groups (JVM Only)
// =============================================================================

/**
 * Creates an atomic group `(?>...)`.
 * Once the pattern inside matches, the regex engine won't backtrack into it.
 * Useful for performance optimization.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.atomicGroup(block: RegexBuilder.() -> Unit) {
    appendRaw("(?>")
    this.block()
    appendRaw(")")
}

// =============================================================================
// Possessive Quantifiers (JVM Only)
// =============================================================================

/**
 * Possessive version of optional - matches 0 or 1 time, no backtracking.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.optionalPossessive(block: RegexBuilder.() -> Unit) {
    val pattern = buildBlock(block)
    appendWithOptionalGroup(pattern)
    appendRaw("?+")
}

/**
 * Possessive version of zeroOrMore - matches 0 or more times, no backtracking.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.zeroOrMorePossessive(block: RegexBuilder.() -> Unit) {
    val pattern = buildBlock(block)
    appendWithOptionalGroup(pattern)
    appendRaw("*+")
}

/**
 * Possessive version of oneOrMore - matches 1 or more times, no backtracking.
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.oneOrMorePossessive(block: RegexBuilder.() -> Unit) {
    val pattern = buildBlock(block)
    appendWithOptionalGroup(pattern)
    appendRaw("++")
}

// =============================================================================
// Inline Modifiers (JVM Only)
// =============================================================================

/**
 * Enables case-insensitive matching for the enclosed pattern.
 * Result: `(?i:...)`
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.caseInsensitive(block: RegexBuilder.() -> Unit) {
    appendRaw("(?i:")
    this.block()
    appendRaw(")")
}

/**
 * Enables multiline mode for the enclosed pattern.
 * In this mode, ^ and $ match at line boundaries.
 * Result: `(?m:...)`
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.multiline(block: RegexBuilder.() -> Unit) {
    appendRaw("(?m:")
    this.block()
    appendRaw(")")
}

/**
 * Enables dotall mode for the enclosed pattern.
 * In this mode, . matches any character including newlines.
 * Result: `(?s:...)`
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.dotAll(block: RegexBuilder.() -> Unit) {
    appendRaw("(?s:")
    this.block()
    appendRaw(")")
}

/**
 * Enables comments mode for the enclosed pattern.
 * Whitespace is ignored and # starts a comment.
 * Result: `(?x:...)`
 *
 * JVM only - not available on JS or Native platforms.
 */
public fun RegexBuilder.comments(block: RegexBuilder.() -> Unit) {
    appendRaw("(?x:")
    this.block()
    appendRaw(")")
}
