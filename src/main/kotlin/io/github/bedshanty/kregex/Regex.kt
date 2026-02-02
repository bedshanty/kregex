package io.github.bedshanty.kregex

/**
 * Creates a [Regex] instance using the Kregex DSL.
 *
 * This is the main entry point for the library. It allows you to construct a regular expression
 * in a readable, type-safe manner using the [RegexBuilder].
 *
 * Example:
 * ```kotlin
 * val emailPattern = regex(RegexOption.IGNORE_CASE) {
 *     startOfLine()
 *     oneOrMore { wordChar() }
 *     literal("@")
 *     oneOrMore { wordChar() }
 *     literal(".")
 *     repeat(2, 4) { range('a', 'z') }
 *     endOfLine()
 * }
 *
 * // Access the pattern string via Regex.pattern property
 * println(emailPattern.pattern)
 * ```
 *
 * @param options Variable number of [RegexOption]s to be applied to the resulting Regex (e.g., [RegexOption.IGNORE_CASE]).
 * @param block The DSL block where the pattern is defined.
 * @return A compiled [Regex] object ready for matching.
 * @since 0.1.0
 */
public inline fun regex(
    vararg options: RegexOption,
    block: RegexBuilder.() -> Unit
): Regex {
    val builder = RegexBuilder()
    builder.block()
    return Regex(builder.build(), options.toSet())
}
