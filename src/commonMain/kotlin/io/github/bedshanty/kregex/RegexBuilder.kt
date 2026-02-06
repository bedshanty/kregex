package io.github.bedshanty.kregex

/**
 * DslMarker annotation to control the scope of the DSL.
 * This prevents accidental access to outer scopes when DSL blocks are nested.
 */
@DslMarker
public annotation class KregexDsl

/**
 * Common interface for classes that support character pattern operations.
 *
 * This interface allows shared methods and extension functions (like Korean character patterns)
 * to work with both [RegexBuilder] and [CharClassBuilder].
 *
 * @since 0.1.0
 */
public interface CharacterRangeCapable {
    /**
     * Adds a character range.
     * In [RegexBuilder], this creates `[start-end]`.
     * In [CharClassBuilder], this adds `start-end` to the class.
     *
     * @param start The starting character.
     * @param end The ending character.
     * @throws IllegalArgumentException if start > end.
     */
    public fun range(start: Char, end: Char)

    /**
     * Appends a raw pattern string to the buffer.
     * This is used internally by default implementations.
     *
     * @param pattern The pattern string to append.
     */
    public fun appendRaw(pattern: String)

    // =========================================================================
    // Predefined Character Classes (shared implementations)
    // =========================================================================

    /**
     * Appends a digit character class (`\d`).
     * Matches any digit (0-9).
     */
    public fun digit() {
        appendRaw("\\d")
    }

    /**
     * Appends a non-digit character class (`\D`).
     * Matches any character that is not a digit.
     */
    public fun nonDigit() {
        appendRaw("\\D")
    }

    /**
     * Appends a whitespace character class (`\s`).
     * Matches any whitespace character (spaces, tabs, line breaks).
     */
    public fun whitespace() {
        appendRaw("\\s")
    }

    /**
     * Appends a non-whitespace character class (`\S`).
     * Matches any character that is not a whitespace.
     */
    public fun nonWhitespace() {
        appendRaw("\\S")
    }

    /**
     * Appends a word character class (`\w`).
     * Matches any word character (alphanumeric plus underscore).
     */
    public fun wordChar() {
        appendRaw("\\w")
    }

    /**
     * Appends a non-word character class (`\W`).
     * Matches any character that is not a word character.
     */
    public fun nonWordChar() {
        appendRaw("\\W")
    }

    // =========================================================================
    // Korean (Hangul) Character Classes
    // =========================================================================

    /**
     * Appends a pattern matching a single Hangul syllable (완성형 한글).
     * Matches complete Korean syllable blocks like 가, 나, 다, etc.
     *
     * Unicode range: U+AC00 to U+D7A3 (가-힣)
     *
     * Example:
     * ```kotlin
     * regex { oneOrMore { hangulSyllable() } }  // [가-힣]+
     * charClass { hangulSyllable(); digit() }   // [가-힣\d]
     * ```
     */
    public fun hangulSyllable() {
        range('\uAC00', '\uD7A3')  // 가-힣
    }

    /**
     * Appends a pattern matching a single Hangul Jamo (한글 자모).
     * Matches both consonants (ㄱ-ㅎ) and vowels (ㅏ-ㅣ).
     *
     * Unicode range: U+3131 to U+3163 (ㄱ-ㅣ)
     */
    public fun hangulJamo() {
        range('\u3131', '\u3163')  // ㄱ-ㅣ
    }

    /**
     * Appends a pattern matching a single Hangul consonant (한글 자음).
     * Matches Korean consonants: ㄱ, ㄴ, ㄷ, ㄹ, ㅁ, ㅂ, ㅅ, ㅇ, ㅈ, ㅊ, ㅋ, ㅌ, ㅍ, ㅎ, etc.
     *
     * Unicode range: U+3131 to U+314E (ㄱ-ㅎ)
     */
    public fun hangulConsonant() {
        range('\u3131', '\u314E')  // ㄱ-ㅎ
    }

    /**
     * Appends a pattern matching a single Hangul vowel (한글 모음).
     * Matches Korean vowels: ㅏ, ㅑ, ㅓ, ㅕ, ㅗ, ㅛ, ㅜ, ㅠ, ㅡ, ㅣ, etc.
     *
     * Unicode range: U+314F to U+3163 (ㅏ-ㅣ)
     */
    public fun hangulVowel() {
        range('\u314F', '\u3163')  // ㅏ-ㅣ
    }

    // =========================================================================
    // ASCII Character Range Shortcuts
    // =========================================================================

    /**
     * Adds ASCII lowercase letters range (a-z).
     */
    public fun asciiLowercase(): Unit = range('a', 'z')

    /**
     * Adds ASCII uppercase letters range (A-Z).
     */
    public fun asciiUppercase(): Unit = range('A', 'Z')

    /**
     * Adds ASCII digit range (0-9).
     * Unlike [digit] which uses `\d` (may include unicode digits),
     * this explicitly matches only ASCII digits 0-9.
     */
    public fun asciiDigit(): Unit = range('0', '9')

    /**
     * Adds all ASCII letters (a-zA-Z).
     */
    public fun asciiLetter() {
        asciiLowercase()
        asciiUppercase()
    }

    /**
     * Adds ASCII alphanumeric characters (a-zA-Z0-9).
     */
    public fun asciiAlphanumeric() {
        asciiLetter()
        asciiDigit()
    }

    /**
     * Adds hexadecimal digit characters (0-9a-fA-F).
     */
    public fun hexDigit() {
        asciiDigit()
        range('a', 'f')
        range('A', 'F')
    }

    // =========================================================================
    // Control Characters (Common)
    // =========================================================================

    /**
     * Appends a tab character (`\t`).
     */
    public fun tab(): Unit = appendRaw("\\t")

    /**
     * Appends a newline character (`\n`).
     */
    public fun newline(): Unit = appendRaw("\\n")

    /**
     * Appends a carriage return character (`\r`).
     */
    public fun carriageReturn(): Unit = appendRaw("\\r")

    /**
     * Appends a form feed character (`\f`).
     */
    public fun formFeed(): Unit = appendRaw("\\f")

    // =========================================================================
    // ASCII Builder
    // =========================================================================

    /**
     * Adds ASCII character ranges using a builder.
     *
     * This provides a convenient DSL for combining ASCII ranges.
     *
     * Example:
     * ```kotlin
     * charClass {
     *     ascii {
     *         lower()    // a-z
     *         upper()    // A-Z
     *     }
     *     chars("_")
     * }
     * // Result: [a-zA-Z_]
     * ```
     *
     * @param block The builder block for specifying ASCII ranges.
     * @since 0.2.0
     */
    public fun ascii(block: AsciiBuilder.() -> Unit) {
        val builder = AsciiBuilder(this)
        builder.block()
    }

    /**
     * Adds Hangul (Korean) character ranges using a builder.
     *
     * This provides a convenient DSL for combining Hangul ranges.
     *
     * Example:
     * ```kotlin
     * charClass {
     *     hangul {
     *         syllable()    // 가-힣 (완성형)
     *         consonant()   // ㄱ-ㅎ (자음)
     *     }
     * }
     * ```
     *
     * @param block The builder block for specifying Hangul ranges.
     * @since 0.3.0
     */
    public fun hangul(block: HangulBuilder.() -> Unit) {
        val builder = HangulBuilder(this)
        builder.block()
    }
}

/**
 * Escapes special characters for use inside a character class.
 * Characters that need escaping: [ ] \ ^ -
 */
private fun escapeForCharClass(chars: String): String {
    val result = StringBuilder()
    for (c in chars) {
        when (c) {
            '\\' -> result.append("\\\\")
            '[' -> result.append("\\[")
            ']' -> result.append("\\]")
            '^' -> result.append("\\^")
            '-' -> result.append("\\-")
            else -> result.append(c)
        }
    }
    return result.toString()
}

/**
 * Enum representing quantifier modes for regex patterns.
 */
public enum class QuantifierMode {
    /** Greedy mode (default) - matches as many characters as possible */
    GREEDY,
    /** Lazy/Reluctant mode - matches as few characters as possible */
    LAZY
}

/**
 * The core builder class for constructing regular expressions.
 *
 * This class provides methods to append various regex elements (literals, anchors, character classes)
 * to the pattern buffer.
 *
 * @since 0.1.0
 */
@KregexDsl
public class RegexBuilder : CharacterRangeCapable {
    private val buffer = StringBuilder()

    /**
     * Appends a raw string to the regex buffer.
     * Use this internally or for advanced cases where a DSL method is missing.
     */
    internal fun append(text: String) {
        buffer.append(text)
    }

    /**
     * Renders the final regular expression string.
     */
    @PublishedApi
    internal fun build(): String {
        return buffer.toString()
    }

    /**
     * Implementation of [CharacterRangeCapable.appendRaw].
     * Appends a raw regex pattern without escaping.
     * Use with caution - the pattern is added exactly as provided.
     */
    public override fun appendRaw(pattern: String): Unit = append(pattern)

    // =========================================================================
    // Anchors & Boundaries
    // =========================================================================

    /**
     * Appends the start-of-line anchor (`^`).
     * Asserts that the current position is at the beginning of the line.
     */
    public fun startOfLine(): Unit = append("^")

    /**
     * Appends the end-of-line anchor (`$`).
     * Asserts that the current position is at the end of the line.
     */
    public fun endOfLine(): Unit = append("$")

    /**
     * Appends a word boundary (`\b`).
     * Matches the position where a word character is not followed or preceded by another word-character.
     */
    public fun wordBoundary(): Unit = append("\\b")

    /**
     * Appends a non-word boundary (`\B`).
     * Matches the position where the previous and next characters are both word characters or both non-word characters.
     */
    public fun nonWordBoundary(): Unit = append("\\B")

    /**
     * Wraps the block with start-of-line (`^`) and end-of-line (`$`) anchors.
     * Use this to match an entire line.
     *
     * Example:
     * ```kotlin
     * regex {
     *     line { literal("hello") }
     * }
     * // Results in: ^hello$
     * ```
     */
    public fun line(block: RegexBuilder.() -> Unit) {
        startOfLine()
        this.block()
        endOfLine()
    }

    // =========================================================================
    // Character Classes (Predefined)
    // =========================================================================

    /**
     * Appends the dot character (`.`).
     * Matches any single character except line terminators.
     */
    public fun anyChar(): Unit = append(".")

    // =========================================================================
    // ASCII Character Ranges (Override for proper character class wrapping)
    // =========================================================================

    /**
     * Matches ASCII letters (`[a-zA-Z]`).
     * Overrides interface default to wrap in a single character class.
     */
    public override fun asciiLetter(): Unit = charClass { asciiLetter() }

    /**
     * Matches ASCII alphanumeric characters (`[a-zA-Z0-9]`).
     * Overrides interface default to wrap in a single character class.
     */
    public override fun asciiAlphanumeric(): Unit = charClass { asciiAlphanumeric() }

    /**
     * Matches hexadecimal digit characters (`[0-9a-fA-F]`).
     * Overrides interface default to wrap in a single character class.
     */
    public override fun hexDigit(): Unit = charClass { hexDigit() }

    /**
     * Creates a character class with ASCII character ranges using a builder.
     * Overrides interface default to wrap in a single character class.
     *
     * Example:
     * ```kotlin
     * regex {
     *     ascii {
     *         lower()    // a-z
     *         upper()    // A-Z
     *     }
     * }
     * // Result: [a-zA-Z]
     * ```
     *
     * @param block The builder block for specifying ASCII ranges.
     * @since 0.2.0
     */
    public override fun ascii(block: AsciiBuilder.() -> Unit) {
        val charClassBuilder = CharClassBuilder()
        val asciiBuilder = AsciiBuilder(charClassBuilder)
        asciiBuilder.block()
        append(charClassBuilder.build())
    }

    /**
     * Creates a character class with Hangul character ranges using a builder.
     * Overrides interface default to wrap in a single character class.
     *
     * Example:
     * ```kotlin
     * regex {
     *     hangul {
     *         syllable()    // 가-힣 (완성형)
     *         consonant()   // ㄱ-ㅎ (자음)
     *     }
     * }
     * // Result: [가-힣ㄱ-ㅎ]
     * ```
     *
     * @param block The builder block for specifying Hangul ranges.
     * @since 0.3.0
     */
    public override fun hangul(block: HangulBuilder.() -> Unit) {
        val charClassBuilder = CharClassBuilder()
        val hangulBuilder = HangulBuilder(charClassBuilder)
        hangulBuilder.block()
        append(charClassBuilder.build())
    }

    // =========================================================================
    // Literals & Custom Sets
    // =========================================================================

    /**
     * Appends a string literal, automatically escaping special characters.
     *
     * Use this method when you want to match a string exactly as it is.
     * For example, `literal("a.b")` will result in `a\.b`.
     *
     * @param text The string to be matched literally.
     */
    public fun literal(text: String): Unit = append(Regex.escape(text))

    /**
     * Appends a single character, automatically escaping if it's a special regex character.
     *
     * @param char The character to match.
     */
    public fun char(char: Char): Unit = append(Regex.escape(char.toString()))

    /**
     * Appends a character set (`[...]`).
     * All characters from all strings are combined into a single character class.
     * Special characters inside the set are automatically escaped.
     *
     * Example:
     * ```kotlin
     * anyOf("abc")          // [abc]
     * anyOf("abc", "xyz")   // [abcxyz]
     * ```
     *
     * @param strings The strings whose characters to include in the set.
     */
    public fun anyOf(vararg strings: String): Unit =
        append("[${escapeForCharClass(strings.joinToString(""))}]")

    /**
     * Appends a character set from individual characters (`[...]`).
     * Special characters inside the set are automatically escaped.
     *
     * Example:
     * ```kotlin
     * anyOf('a', 'b', 'c')  // [abc]
     * anyOf('.', '*', '?')  // [\.\*\?]
     * ```
     *
     * @param chars The characters to include in the set.
     */
    public fun anyOf(vararg chars: Char): Unit = anyOf(chars.concatToString())

    /**
     * Creates a character class using a builder.
     * Alias for [charClass].
     *
     * Example:
     * ```kotlin
     * anyOf {
     *     range('a', 'z')
     *     digit()
     * }
     * // Results in: [a-z\d]
     * ```
     */
    public fun anyOf(block: CharClassBuilder.() -> Unit): Unit = charClass(block)

    /**
     * Appends a negated character set (`[^...]`).
     * Matches any character NOT in the given set.
     * All characters from all strings are combined into a single negated character class.
     * Special characters inside the set are automatically escaped.
     *
     * Example:
     * ```kotlin
     * noneOf("abc")          // [^abc]
     * noneOf("abc", "xyz")   // [^abcxyz]
     * ```
     *
     * @param strings The strings whose characters to exclude from matching.
     */
    public fun noneOf(vararg strings: String): Unit =
        append("[^${escapeForCharClass(strings.joinToString(""))}]")

    /**
     * Appends a negated character set from individual characters (`[^...]`).
     * Matches any character NOT in the given set.
     * Special characters inside the set are automatically escaped.
     *
     * Example:
     * ```kotlin
     * noneOf('a', 'b', 'c')  // [^abc]
     * noneOf('0', '1')       // [^01]
     * ```
     *
     * @param chars The characters to exclude from matching.
     */
    public fun noneOf(vararg chars: Char): Unit = noneOf(chars.concatToString())

    /**
     * Creates a negated character class using a builder.
     * Alias for [negatedCharClass].
     *
     * Example:
     * ```kotlin
     * noneOf {
     *     range('0', '9')
     * }
     * // Results in: [^0-9]
     * ```
     */
    public fun noneOf(block: CharClassBuilder.() -> Unit): Unit = negatedCharClass(block)

    /**
     * Appends a character range (`[a-z]`).
     *
     * @param start The starting character.
     * @param end The ending character.
     * @throws IllegalArgumentException if start > end.
     */
    public override fun range(start: Char, end: Char) {
        require(start <= end) { "Range start must be <= end: '$start' > '$end'" }
        append("[$start-$end]")
    }

    /**
     * Appends a negated character range (`[^a-z]`).
     * Matches any character NOT in the given range.
     *
     * @param start The starting character.
     * @param end The ending character.
     * @throws IllegalArgumentException if start > end.
     */
    public fun notInRange(start: Char, end: Char) {
        require(start <= end) { "Range start must be <= end: '$start' > '$end'" }
        append("[^$start-$end]")
    }

    /**
     * Creates a custom character class using a builder.
     *
     * Example:
     * ```kotlin
     * charClass {
     *     range('a', 'z')
     *     range('A', 'Z')
     *     chars("_")
     * }
     * // Results in: [a-zA-Z_]
     * ```
     */
    public fun charClass(block: CharClassBuilder.() -> Unit) {
        val builder = CharClassBuilder()
        builder.block()
        append(builder.build())
    }

    /**
     * Creates a negated custom character class using a builder.
     *
     * Example:
     * ```kotlin
     * negatedCharClass {
     *     range('0', '9')
     * }
     * // Results in: [^0-9]
     * ```
     */
    public fun negatedCharClass(block: CharClassBuilder.() -> Unit) {
        val builder = CharClassBuilder(negated = true)
        builder.block()
        append(builder.build())
    }

    /**
     * Appends a logical OR operator (`|`).
     * Note: Use this carefully. It's often safer to use `anyOf` for characters or strict grouping for blocks.
     */
    public fun or(): Unit = append("|")

    /**
     * Creates an alternation group with multiple alternatives.
     *
     * Example:
     * ```kotlin
     * either(
     *     { literal("cat") },
     *     { literal("dog") },
     *     { literal("bird") }
     * )
     * // Results in: (?:cat|dog|bird)
     * ```
     */
    public fun either(vararg alternatives: RegexBuilder.() -> Unit) {
        require(alternatives.isNotEmpty()) { "At least one alternative is required" }
        append("(?:")
        alternatives.forEachIndexed { index, alt ->
            if (index > 0) append("|")
            this.alt()
        }
        append(")")
    }

    // =========================================================================
    // Groups & Capturing
    // =========================================================================

    /**
     * Creates a capturing group `(...)`.
     * The content inside the block will be grouped and captured.
     */
    public fun capture(block: RegexBuilder.() -> Unit) {
        append("(")
        this.block()
        append(")")
    }

    /**
     * Creates a named capturing group `(?<name>...)`.
     *
     * @param name The name of the group (must be alphanumeric).
     * @throws IllegalArgumentException if name is empty or contains invalid characters.
     */
    public fun captureAs(name: String, block: RegexBuilder.() -> Unit) {
        require(name.isNotEmpty()) { "Capture group name cannot be empty" }
        require(name.matches(Regex("^[a-zA-Z][a-zA-Z0-9]*$"))) {
            "Capture group name must start with a letter and contain only alphanumeric characters: '$name'"
        }
        append("(?<$name>")
        this.block()
        append(")")
    }

    /**
     * Creates a non-capturing group `(?:...)`.
     * Used for grouping elements without saving the match result.
     */
    public fun group(block: RegexBuilder.() -> Unit) {
        append("(?:")
        this.block()
        append(")")
    }

    // =========================================================================
    // Back References
    // =========================================================================

    /**
     * Appends a back reference to a numbered capturing group (`\n`).
     *
     * @param groupNumber The number of the capturing group (1-based).
     * @throws IllegalArgumentException if groupNumber < 1.
     */
    public fun backReference(groupNumber: Int) {
        require(groupNumber >= 1) { "Group number must be >= 1: $groupNumber" }
        append("\\$groupNumber")
    }

    /**
     * Appends a back reference to a named capturing group (`\k<name>`).
     *
     * @param name The name of the capturing group.
     * @throws IllegalArgumentException if name is empty.
     */
    public fun backReference(name: String) {
        require(name.isNotEmpty()) { "Group name cannot be empty" }
        append("\\k<$name>")
    }

    // =========================================================================
    // Quantifiers (Scope-based)
    // =========================================================================

    /**
     * Returns the quantifier suffix based on the mode.
     */
    private fun quantifierSuffix(mode: QuantifierMode): String = when (mode) {
        QuantifierMode.GREEDY -> ""
        QuantifierMode.LAZY -> "?"
    }

    /**
     * Checks if a pattern is a single atomic element that doesn't need grouping.
     * Single atoms include:
     * - Single characters (a, b, c)
     * - Escaped sequences (\d, \w, \s, \n, etc.)
     * - Character classes ([...])
     * - Unicode properties (\p{...}, \P{...})
     * - Already grouped patterns ((?:...), (...), etc.)
     * - Dot (.)
     */
    @PublishedApi
    internal fun isSingleAtom(pattern: String): Boolean {
        if (pattern.isEmpty()) return false

        // Single character (not a special regex character that needs grouping)
        if (pattern.length == 1) return true

        // Escaped single character: \d, \w, \s, \n, \t, \., etc.
        if (pattern.length == 2 && pattern[0] == '\\') return true

        // Unicode property \p{...} or \P{...}
        // This also handles Unicode script \p{Is...} and block \p{In...}
        if (pattern.startsWith("\\p{") || pattern.startsWith("\\P{")) {
            return pattern.endsWith("}") && pattern.count { it == '{' } == 1
        }

        // Character class [...] - check for balanced brackets
        if (pattern.startsWith("[") && pattern.endsWith("]")) {
            return isBalancedCharClass(pattern)
        }

        // Already grouped pattern (?:...), (...), (?=...), (?!...), (?<=...), (?<!...), (?>...)
        if (pattern.startsWith("(") && pattern.endsWith(")")) {
            return isBalancedGroup(pattern)
        }

        return false
    }

    /**
     * Checks if a character class pattern has balanced brackets.
     */
    private fun isBalancedCharClass(pattern: String): Boolean {
        if (!pattern.startsWith("[") || !pattern.endsWith("]")) return false

        var i = 1
        // Handle negation
        if (i < pattern.length && pattern[i] == '^') i++
        // Handle literal ] at start
        if (i < pattern.length && pattern[i] == ']') i++

        while (i < pattern.length - 1) {
            when {
                pattern[i] == '\\' && i + 1 < pattern.length - 1 -> i += 2 // Skip escaped char
                pattern[i] == '[' -> return false // Nested class not supported in simple check
                else -> i++
            }
        }
        return true
    }

    /**
     * Checks if a grouped pattern has balanced parentheses.
     */
    private fun isBalancedGroup(pattern: String): Boolean {
        if (!pattern.startsWith("(") || !pattern.endsWith(")")) return false

        var depth = 0
        var i = 0
        while (i < pattern.length) {
            when {
                pattern[i] == '\\' && i + 1 < pattern.length -> {
                    i += 2 // Skip escaped char
                }
                pattern[i] == '(' -> {
                    depth++
                    i++
                }
                pattern[i] == ')' -> {
                    depth--
                    if (depth == 0 && i < pattern.length - 1) return false // Closed too early
                    i++
                }
                else -> i++
            }
        }
        return depth == 0
    }

    /**
     * Executes a block and returns the resulting pattern string.
     */
    @PublishedApi
    internal fun buildBlock(block: RegexBuilder.() -> Unit): String {
        val inner = RegexBuilder()
        inner.block()
        return inner.build()
    }

    /**
     * Appends a pattern with optional grouping.
     * If the pattern is a single atom, it's appended directly.
     * Otherwise, it's wrapped in a non-capturing group.
     */
    @PublishedApi
    internal fun appendWithOptionalGroup(pattern: String) {
        if (isSingleAtom(pattern)) {
            append(pattern)
        } else {
            append("(?:$pattern)")
        }
    }

    /**
     * Appends the optional quantifier `?` to the block.
     * Matches the block 0 or 1 time.
     * Automatically optimizes by skipping the non-capturing group for single atoms.
     *
     * @param mode The quantifier mode (default: GREEDY).
     */
    public fun optional(mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
        val pattern = buildBlock(block)
        appendWithOptionalGroup(pattern)
        append("?${quantifierSuffix(mode)}")
    }

    /**
     * Appends the zero-or-more quantifier `*` to the block.
     * Automatically optimizes by skipping the non-capturing group for single atoms.
     *
     * @param mode The quantifier mode (default: GREEDY).
     */
    public fun zeroOrMore(mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
        val pattern = buildBlock(block)
        appendWithOptionalGroup(pattern)
        append("*${quantifierSuffix(mode)}")
    }

    /**
     * Appends the one-or-more quantifier `+` to the block.
     * Automatically optimizes by skipping the non-capturing group for single atoms.
     *
     * @param mode The quantifier mode (default: GREEDY).
     */
    public fun oneOrMore(mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
        val pattern = buildBlock(block)
        appendWithOptionalGroup(pattern)
        append("+${quantifierSuffix(mode)}")
    }

    /**
     * Appends an exact repetition quantifier `{n}`.
     * Matches the block exactly [n] times.
     * Automatically optimizes by skipping the non-capturing group for single atoms.
     *
     * @param n The exact number of repetitions.
     * @throws IllegalArgumentException if n < 0.
     */
    public fun repeat(n: Int, block: RegexBuilder.() -> Unit) {
        require(n >= 0) { "Repeat count must be non-negative: $n" }
        val pattern = buildBlock(block)
        appendWithOptionalGroup(pattern)
        append("{$n}")
    }

    /**
     * Appends a range repetition quantifier `{min, max}`.
     * Matches the block at least [min] times and at most [max] times.
     * Automatically optimizes by skipping the non-capturing group for single atoms.
     *
     * @param min The minimum number of repetitions.
     * @param max The maximum number of repetitions.
     * @param mode The quantifier mode (default: GREEDY).
     * @throws IllegalArgumentException if min < 0, max < min.
     */
    public fun repeat(min: Int, max: Int, mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
        require(min >= 0) { "Minimum repeat count must be non-negative: $min" }
        require(max >= min) { "Maximum must be >= minimum: $max < $min" }
        val pattern = buildBlock(block)
        appendWithOptionalGroup(pattern)
        append("{$min,$max}${quantifierSuffix(mode)}")
    }

    /**
     * Appends a minimal repetition quantifier `{min,}`.
     * Matches the block at least [min] times.
     * Automatically optimizes by skipping the non-capturing group for single atoms.
     *
     * @param min The minimum number of repetitions.
     * @param mode The quantifier mode (default: GREEDY).
     * @throws IllegalArgumentException if min < 0.
     */
    public fun atLeast(min: Int, mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
        require(min >= 0) { "Minimum repeat count must be non-negative: $min" }
        val pattern = buildBlock(block)
        appendWithOptionalGroup(pattern)
        append("{$min,}${quantifierSuffix(mode)}")
    }

    // =========================================================================
    // Lazy Quantifiers (Convenience methods)
    // =========================================================================

    /**
     * Lazy version of optional - matches 0 or 1 time, preferring 0.
     */
    public fun optionalLazy(block: RegexBuilder.() -> Unit): Unit = optional(QuantifierMode.LAZY, block)

    /**
     * Lazy version of zeroOrMore - matches 0 or more times, preferring fewer.
     */
    public fun zeroOrMoreLazy(block: RegexBuilder.() -> Unit): Unit = zeroOrMore(QuantifierMode.LAZY, block)

    /**
     * Lazy version of oneOrMore - matches 1 or more times, preferring fewer.
     */
    public fun oneOrMoreLazy(block: RegexBuilder.() -> Unit): Unit = oneOrMore(QuantifierMode.LAZY, block)

    // =========================================================================
    // Lookaround Assertions (Zero-width assertions)
    // =========================================================================

    /**
     * Appends a Positive Lookahead `(?=...)`.
     * Asserts that the given pattern matches next, but does not consume characters.
     *
     * Example: `literal("q").lookAhead { literal("u") }` matches "q" only if followed by "u".
     */
    public fun lookAhead(block: RegexBuilder.() -> Unit) {
        append("(?=")
        this.block()
        append(")")
    }

    /**
     * Appends a Negative Lookahead `(?!...)`.
     * Asserts that the given pattern does NOT match next.
     *
     * Example: `literal("q").negativeLookAhead { literal("u") }` matches "q" only if NOT followed by "u".
     */
    public fun negativeLookAhead(block: RegexBuilder.() -> Unit) {
        append("(?!")
        this.block()
        append(")")
    }

    /**
     * Appends a Positive Lookbehind `(?<=...)`.
     * Asserts that the given pattern matches immediately before the current position.
     *
     * Example:
     * ```kotlin
     * regex {
     *     lookBehind { literal("$") }
     *     digit()
     * }
     * // Matches a digit only if preceded by "$"
     * ```
     */
    public fun lookBehind(block: RegexBuilder.() -> Unit) {
        append("(?<=")
        this.block()
        append(")")
    }

    /**
     * Appends a Negative Lookbehind `(?<!...)`.
     * Asserts that the given pattern does NOT match immediately before the current position.
     *
     * Example:
     * ```kotlin
     * regex {
     *     negativeLookBehind { literal("-") }
     *     digit()
     * }
     * // Matches a digit only if NOT preceded by "-"
     * ```
     */
    public fun negativeLookBehind(block: RegexBuilder.() -> Unit) {
        append("(?<!")
        this.block()
        append(")")
    }
}

/**
 * Builder for ASCII character ranges within a character class.
 *
 * This builder provides a convenient DSL for adding ASCII character ranges.
 * It can be used within [RegexBuilder.ascii] or [CharClassBuilder.ascii] blocks.
 *
 * Example:
 * ```kotlin
 * regex {
 *     ascii {
 *         lower()    // a-z
 *         upper()    // A-Z
 *     }
 * }
 * // Result: [a-zA-Z]
 * ```
 *
 * @since 0.2.0
 */
@KregexDsl
public class AsciiBuilder internal constructor(private val delegate: CharacterRangeCapable) {

    /**
     * Adds ASCII lowercase letters range (a-z).
     */
    public fun lower(): Unit = delegate.asciiLowercase()

    /**
     * Adds ASCII uppercase letters range (A-Z).
     */
    public fun upper(): Unit = delegate.asciiUppercase()

    /**
     * Adds all ASCII letters (a-zA-Z).
     */
    public fun letter(): Unit = delegate.asciiLetter()

    /**
     * Adds ASCII digit characters (0-9).
     */
    public fun digit(): Unit = delegate.asciiDigit()

    /**
     * Adds ASCII alphanumeric characters (a-zA-Z0-9).
     */
    public fun alphanumeric(): Unit = delegate.asciiAlphanumeric()

    /**
     * Adds hexadecimal digit characters (0-9a-fA-F).
     */
    public fun hexDigit(): Unit = delegate.hexDigit()
}

/**
 * Builder for Hangul (Korean) character ranges within a character class.
 *
 * This builder provides a convenient DSL for adding Korean character ranges.
 * It can be used within [RegexBuilder.hangul] or [CharClassBuilder.hangul] blocks.
 *
 * Example:
 * ```kotlin
 * charClass {
 *     hangul {
 *         syllable()    // 가-힣 (완성형)
 *         consonant()   // ㄱ-ㅎ (자음)
 *         vowel()       // ㅏ-ㅣ (모음)
 *     }
 * }
 * ```
 *
 * @since 0.3.0
 */
@KregexDsl
public class HangulBuilder internal constructor(private val delegate: CharacterRangeCapable) {

    /**
     * Matches Hangul syllables (완성형 한글: 가-힣).
     */
    public fun syllable(): Unit = delegate.hangulSyllable()

    /**
     * Matches Hangul Jamo (한글 자모: ㄱ-ㅣ).
     */
    public fun jamo(): Unit = delegate.hangulJamo()

    /**
     * Matches Hangul consonants (한글 자음: ㄱ-ㅎ).
     */
    public fun consonant(): Unit = delegate.hangulConsonant()

    /**
     * Matches Hangul vowels (한글 모음: ㅏ-ㅣ).
     */
    public fun vowel(): Unit = delegate.hangulVowel()
}

/**
 * Builder for constructing complex character classes.
 *
 * This class allows you to combine multiple ranges, individual characters,
 * and predefined classes into a single character class.
 *
 * @param negated If true, creates a negated character class [^...].
 */
@KregexDsl
public class CharClassBuilder(private val negated: Boolean = false) : CharacterRangeCapable {
    private val buffer = StringBuilder()

    /**
     * Adds a range of characters (e.g., a-z).
     *
     * @param start The starting character.
     * @param end The ending character.
     * @throws IllegalArgumentException if start > end.
     */
    public override fun range(start: Char, end: Char) {
        require(start <= end) { "Range start must be <= end: '$start' > '$end'" }
        buffer.append("$start-$end")
    }

    /**
     * Adds individual characters to the class.
     * Special characters are automatically escaped.
     *
     * Example:
     * ```kotlin
     * charClass {
     *     chars("abc")          // abc
     *     chars("abc", "xyz")   // abcxyz
     * }
     * ```
     *
     * @param strings The strings whose characters to add.
     */
    public fun chars(vararg strings: String) {
        buffer.append(escapeForCharClass(strings.joinToString("")))
    }

    /**
     * Adds individual characters to the class from vararg.
     * Special characters are automatically escaped.
     *
     * Example:
     * ```kotlin
     * charClass {
     *     chars('a', 'b', 'c')  // abc
     *     chars('.', '*')       // \.\*
     * }
     * ```
     *
     * @param chars The characters to add.
     */
    public fun chars(vararg chars: Char): Unit = chars(chars.concatToString())

    /**
     * Adds a single character to the class.
     * Special characters are automatically escaped.
     */
    public fun char(c: Char): Unit = chars(c.toString())

    /**
     * Implementation of [CharacterRangeCapable.appendRaw].
     * Adds a raw pattern to the class without escaping.
     * Use with caution.
     */
    public override fun appendRaw(pattern: String) {
        buffer.append(pattern)
    }

    /**
     * Builds and returns the character class string.
     */
    internal fun build(): String {
        val prefix = if (negated) "[^" else "["
        return "$prefix$buffer]"
    }
}
