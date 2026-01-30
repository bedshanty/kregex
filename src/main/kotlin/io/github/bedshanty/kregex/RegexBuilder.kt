package io.github.bedshanty.kregex

/**
 * DslMarker annotation to control the scope of the DSL.
 * This prevents accidental access to outer scopes when DSL blocks are nested.
 */
@DslMarker
annotation class KregexDsl

/**
 * Common interface for classes that support character pattern operations.
 *
 * This interface allows shared methods and extension functions (like Korean character patterns)
 * to work with both [RegexBuilder] and [CharClassBuilder].
 *
 * @since 0.1.0
 */
interface CharacterRangeCapable {
    /**
     * Adds a character range.
     * In [RegexBuilder], this creates `[start-end]`.
     * In [CharClassBuilder], this adds `start-end` to the class.
     *
     * @param start The starting character.
     * @param end The ending character.
     * @throws IllegalArgumentException if start > end.
     */
    fun range(start: Char, end: Char)

    /**
     * Appends a raw pattern string to the buffer.
     * This is used internally by default implementations.
     *
     * @param pattern The pattern string to append.
     */
    fun appendRaw(pattern: String)

    // =========================================================================
    // Predefined Character Classes (shared implementations)
    // =========================================================================

    /**
     * Appends a digit character class (`\d`).
     * Matches any digit (0-9).
     */
    fun digit() {
        appendRaw("\\d")
    }

    /**
     * Appends a non-digit character class (`\D`).
     * Matches any character that is not a digit.
     */
    fun nonDigit() {
        appendRaw("\\D")
    }

    /**
     * Appends a whitespace character class (`\s`).
     * Matches any whitespace character (spaces, tabs, line breaks).
     */
    fun whitespace() {
        appendRaw("\\s")
    }

    /**
     * Appends a non-whitespace character class (`\S`).
     * Matches any character that is not a whitespace.
     */
    fun nonWhitespace() {
        appendRaw("\\S")
    }

    /**
     * Appends a word character class (`\w`).
     * Matches any word character (alphanumeric plus underscore).
     */
    fun wordChar() {
        appendRaw("\\w")
    }

    /**
     * Appends a non-word character class (`\W`).
     * Matches any character that is not a word character.
     */
    fun nonWordChar() {
        appendRaw("\\W")
    }

    /**
     * Appends a Unicode property class (`\p{...}`).
     * Matches any character with the specified Unicode property.
     *
     * @param property The Unicode property name.
     */
    fun unicodeProperty(property: String) {
        appendRaw("\\p{$property}")
    }

    /**
     * Appends a negated Unicode property class (`\P{...}`).
     * Matches any character that does NOT have the specified Unicode property.
     *
     * @param property The Unicode property name.
     */
    fun notUnicodeProperty(property: String) {
        appendRaw("\\P{$property}")
    }

    /**
     * Appends a Unicode script class (`\p{Is...}`).
     *
     * @param script The Unicode script name (e.g., "Greek", "Cyrillic", "Han").
     */
    fun unicodeScript(script: String) {
        appendRaw("\\p{Is$script}")
    }

    /**
     * Appends a Unicode block class (`\p{In...}`).
     *
     * @param block The Unicode block name (e.g., "BasicLatin", "CJKUnifiedIdeographs").
     */
    fun unicodeBlock(block: String) {
        appendRaw("\\p{In$block}")
    }

    // =========================================================================
    // Unicode Convenience Methods
    // =========================================================================

    /**
     * Matches any Unicode letter character (`\p{L}`).
     */
    fun unicodeLetter() = unicodeProperty("L")

    /**
     * Matches any Unicode uppercase letter (`\p{Lu}`).
     */
    fun unicodeUppercaseLetter() = unicodeProperty("Lu")

    /**
     * Matches any Unicode lowercase letter (`\p{Ll}`).
     */
    fun unicodeLowercaseLetter() = unicodeProperty("Ll")

    /**
     * Matches any Unicode numeric character (`\p{N}`).
     */
    fun unicodeNumber() = unicodeProperty("N")

    /**
     * Matches any Unicode punctuation character (`\p{P}`).
     */
    fun unicodePunctuation() = unicodeProperty("P")

    /**
     * Matches any Unicode symbol character (`\p{S}`).
     */
    fun unicodeSymbol() = unicodeProperty("S")

    // =========================================================================
    // POSIX Character Classes
    // =========================================================================

    /**
     * Matches alphanumeric characters (`\p{Alnum}`).
     * Equivalent to `[a-zA-Z0-9]`.
     */
    fun posixAlnum() {
        appendRaw("\\p{Alnum}")
    }

    /**
     * Matches alphabetic characters (`\p{Alpha}`).
     * Equivalent to `[a-zA-Z]`.
     */
    fun posixAlpha() {
        appendRaw("\\p{Alpha}")
    }

    /**
     * Matches ASCII characters (`\p{ASCII}`).
     * Equivalent to `[\x00-\x7F]`.
     */
    fun posixAscii() {
        appendRaw("\\p{ASCII}")
    }

    /**
     * Matches blank characters (`\p{Blank}`).
     * Equivalent to `[ \t]` (space and tab).
     */
    fun posixBlank() {
        appendRaw("\\p{Blank}")
    }

    /**
     * Matches control characters (`\p{Cntrl}`).
     */
    fun posixCntrl() {
        appendRaw("\\p{Cntrl}")
    }

    /**
     * Matches digit characters (`\p{Digit}`).
     * Equivalent to `[0-9]`.
     */
    fun posixDigit() {
        appendRaw("\\p{Digit}")
    }

    /**
     * Matches visible characters (`\p{Graph}`).
     * Printable characters excluding space.
     */
    fun posixGraph() {
        appendRaw("\\p{Graph}")
    }

    /**
     * Matches lowercase letters (`\p{Lower}`).
     * Equivalent to `[a-z]`.
     */
    fun posixLower() {
        appendRaw("\\p{Lower}")
    }

    /**
     * Matches printable characters (`\p{Print}`).
     * Visible characters plus space.
     */
    fun posixPrint() {
        appendRaw("\\p{Print}")
    }

    /**
     * Matches punctuation characters (`\p{Punct}`).
     */
    fun posixPunct() {
        appendRaw("\\p{Punct}")
    }

    /**
     * Matches whitespace characters (`\p{Space}`).
     * Equivalent to `[ \t\n\r\f\v]`.
     */
    fun posixSpace() {
        appendRaw("\\p{Space}")
    }

    /**
     * Matches uppercase letters (`\p{Upper}`).
     * Equivalent to `[A-Z]`.
     */
    fun posixUpper() {
        appendRaw("\\p{Upper}")
    }

    /**
     * Matches hexadecimal digits (`\p{XDigit}`).
     * Equivalent to `[0-9a-fA-F]`.
     */
    fun posixXDigit() {
        appendRaw("\\p{XDigit}")
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
     * regex { oneOrMore { hangul() } }  // [가-힣]+
     * charClass { hangul(); digit() }   // [가-힣\d]
     * ```
     */
    fun hangul() {
        range('가', '힣')
    }

    /**
     * Appends a pattern matching a single Hangul Jamo (한글 자모).
     * Matches both consonants (ㄱ-ㅎ) and vowels (ㅏ-ㅣ).
     *
     * Unicode range: U+3131 to U+3163 (ㄱ-ㅣ)
     */
    fun hangulJamo() {
        range('ㄱ', 'ㅣ')
    }

    /**
     * Appends a pattern matching a single Hangul consonant (한글 자음).
     * Matches Korean consonants: ㄱ, ㄴ, ㄷ, ㄹ, ㅁ, ㅂ, ㅅ, ㅇ, ㅈ, ㅊ, ㅋ, ㅌ, ㅍ, ㅎ, etc.
     *
     * Unicode range: U+3131 to U+314E (ㄱ-ㅎ)
     */
    fun hangulConsonant() {
        range('ㄱ', 'ㅎ')
    }

    /**
     * Appends a pattern matching a single Hangul vowel (한글 모음).
     * Matches Korean vowels: ㅏ, ㅑ, ㅓ, ㅕ, ㅗ, ㅛ, ㅜ, ㅠ, ㅡ, ㅣ, etc.
     *
     * Unicode range: U+314F to U+3163 (ㅏ-ㅣ)
     */
    fun hangulVowel() {
        range('ㅏ', 'ㅣ')
    }
}

/**
 * Escapes special characters for use inside a character class.
 * Characters that need escaping: ] \ ^ -
 */
private fun escapeForCharClass(chars: String): String {
    val result = StringBuilder()
    for (c in chars) {
        when (c) {
            '\\' -> result.append("\\\\")
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
enum class QuantifierMode {
    /** Greedy mode (default) - matches as many characters as possible */
    GREEDY,
    /** Lazy/Reluctant mode - matches as few characters as possible */
    LAZY,
    /** Possessive mode - like greedy but doesn't backtrack */
    POSSESSIVE
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
class RegexBuilder : CharacterRangeCapable {
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
     * Appends a raw regex pattern without escaping.
     * Use with caution - the pattern is added exactly as provided.
     *
     * @param pattern The raw regex pattern string.
     */
    fun raw(pattern: String) = append(pattern)

    /**
     * Implementation of [CharacterRangeCapable.appendRaw].
     */
    override fun appendRaw(pattern: String) = append(pattern)

    // =========================================================================
    // Anchors & Boundaries
    // =========================================================================

    /**
     * Appends the start-of-line anchor (`^`).
     * Asserts that the current position is at the beginning of the line.
     */
    fun startOfLine() = append("^")

    /**
     * Appends the end-of-line anchor (`$`).
     * Asserts that the current position is at the end of the line.
     */
    fun endOfLine() = append("$")

    /**
     * Appends the start-of-input anchor (`\A`).
     * Asserts that the current position is at the very beginning of the input.
     */
    fun startOfInput() = append("\\A")

    /**
     * Appends the end-of-input anchor (`\z`).
     * Asserts that the current position is at the very end of the input.
     */
    fun endOfInput() = append("\\z")

    /**
     * Appends the end-of-input anchor (`\Z`).
     * Asserts that the current position is at the end of the input, but before any final line terminator.
     */
    fun endOfInputBeforeNewline() = append("\\Z")

    /**
     * Appends a word boundary (`\b`).
     * Matches the position where a word character is not followed or preceded by another word-character.
     */
    fun wordBoundary() = append("\\b")

    /**
     * Appends a non-word boundary (`\B`).
     * Matches the position where the previous and next characters are both word characters or both non-word characters.
     */
    fun nonWordBoundary() = append("\\B")

    // =========================================================================
    // Character Classes (Predefined)
    // =========================================================================

    /**
     * Appends the dot character (`.`).
     * Matches any single character except line terminators.
     */
    fun anyChar() = append(".")

    // digit(), nonDigit(), whitespace(), nonWhitespace(), wordChar(), nonWordChar()
    // are inherited from CharacterRangeCapable interface

    /**
     * Appends a tab character (`\t`).
     */
    fun tab() = append("\\t")

    /**
     * Appends a newline character (`\n`).
     */
    fun newline() = append("\\n")

    /**
     * Appends a carriage return character (`\r`).
     */
    fun carriageReturn() = append("\\r")

    /**
     * Appends a form feed character (`\f`).
     */
    fun formFeed() = append("\\f")

    /**
     * Appends an alert/bell character (`\a`).
     */
    fun alert() = append("\\a")

    /**
     * Appends an escape character (`\e`).
     */
    fun escape() = append("\\e")

    // =========================================================================
    // Unicode Support
    // =========================================================================

    // Unicode support methods are inherited from CharacterRangeCapable interface:
    // - unicodeProperty(), notUnicodeProperty()
    // - unicodeScript(), unicodeBlock()
    // - unicodeLetter(), unicodeUppercaseLetter(), unicodeLowercaseLetter()
    // - unicodeNumber(), unicodePunctuation(), unicodeSymbol()

    // =========================================================================
    // ASCII Character Ranges
    // =========================================================================

    /**
     * Matches ASCII lowercase letters (`[a-z]`).
     */
    fun asciiLowercase() = charClass { asciiLowercase() }

    /**
     * Matches ASCII uppercase letters (`[A-Z]`).
     */
    fun asciiUppercase() = charClass { asciiUppercase() }

    /**
     * Matches ASCII letters (`[a-zA-Z]`).
     */
    fun asciiLetter() = charClass { asciiLetter() }

    /**
     * Matches ASCII alphanumeric characters (`[a-zA-Z0-9]`).
     */
    fun asciiAlphanumeric() = charClass { asciiAlphanumeric() }

    /**
     * Matches hexadecimal digit characters (`[0-9a-fA-F]`).
     */
    fun hexDigit() = charClass { hexDigit() }

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
    fun literal(text: String) = append(Regex.escape(text))

    /**
     * Appends a single character, automatically escaping if it's a special regex character.
     *
     * @param char The character to match.
     */
    fun char(char: Char) = append(Regex.escape(char.toString()))

    /**
     * Appends a character set (`[...]`).
     * Special characters inside the set are automatically escaped.
     *
     * @param chars The characters to include in the set (e.g., "abc" -> "[abc]").
     */
    fun anyOf(chars: String) = append("[${escapeForCharClass(chars)}]")

    /**
     * Appends a negated character set (`[^...]`).
     * Matches any character NOT in the given set.
     * Special characters inside the set are automatically escaped.
     *
     * @param chars The characters to exclude (e.g., "abc" -> "[^abc]").
     */
    fun noneOf(chars: String) = append("[^${escapeForCharClass(chars)}]")

    /**
     * Appends a character range (`[a-z]`).
     *
     * @param start The starting character.
     * @param end The ending character.
     * @throws IllegalArgumentException if start > end.
     */
    override fun range(start: Char, end: Char) {
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
    fun notInRange(start: Char, end: Char) {
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
    fun charClass(block: CharClassBuilder.() -> Unit) {
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
    fun negatedCharClass(block: CharClassBuilder.() -> Unit) {
        val builder = CharClassBuilder(negated = true)
        builder.block()
        append(builder.build())
    }

    /**
     * Appends a logical OR operator (`|`).
     * Note: Use this carefully. It's often safer to use `anyOf` for characters or strict grouping for blocks.
     */
    fun or() = append("|")

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
    fun either(vararg alternatives: RegexBuilder.() -> Unit) {
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
    fun capture(block: RegexBuilder.() -> Unit) {
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
    fun capture(name: String, block: RegexBuilder.() -> Unit) {
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
    fun group(block: RegexBuilder.() -> Unit) {
        append("(?:")
        this.block()
        append(")")
    }

    /**
     * Creates an atomic group `(?>...)`.
     * Once the pattern inside matches, the regex engine won't backtrack into it.
     * Useful for performance optimization.
     */
    fun atomicGroup(block: RegexBuilder.() -> Unit) {
        append("(?>")
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
    fun backReference(groupNumber: Int) {
        require(groupNumber >= 1) { "Group number must be >= 1: $groupNumber" }
        append("\\$groupNumber")
    }

    /**
     * Appends a back reference to a named capturing group (`\k<name>`).
     *
     * @param name The name of the capturing group.
     * @throws IllegalArgumentException if name is empty.
     */
    fun backReference(name: String) {
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
        QuantifierMode.POSSESSIVE -> "+"
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
    private fun isSingleAtom(pattern: String): Boolean {
        if (pattern.isEmpty()) return false

        // Single character (not a special regex character that needs grouping)
        if (pattern.length == 1) return true

        // Escaped single character: \d, \w, \s, \n, \t, \., etc.
        if (pattern.length == 2 && pattern[0] == '\\') return true

        // Unicode property \p{...} or \P{...}
        if (pattern.startsWith("\\p{") || pattern.startsWith("\\P{")) {
            return pattern.endsWith("}") && pattern.count { it == '{' } == 1
        }

        // Unicode script \p{Is...} or block \p{In...}
        if (pattern.matches(Regex("^\\\\p\\{(Is|In)[^}]+}$"))) return true

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
    private fun buildBlock(block: RegexBuilder.() -> Unit): String {
        val inner = RegexBuilder()
        inner.block()
        return inner.build()
    }

    /**
     * Appends a pattern with optional grouping.
     * If the pattern is a single atom, it's appended directly.
     * Otherwise, it's wrapped in a non-capturing group.
     */
    private fun appendWithOptionalGroup(pattern: String) {
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
    fun optional(mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
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
    fun zeroOrMore(mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
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
    fun oneOrMore(mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
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
    fun repeat(n: Int, block: RegexBuilder.() -> Unit) {
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
    fun repeat(min: Int, max: Int, mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
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
    fun atLeast(min: Int, mode: QuantifierMode = QuantifierMode.GREEDY, block: RegexBuilder.() -> Unit) {
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
    fun optionalLazy(block: RegexBuilder.() -> Unit) = optional(QuantifierMode.LAZY, block)

    /**
     * Lazy version of zeroOrMore - matches 0 or more times, preferring fewer.
     */
    fun zeroOrMoreLazy(block: RegexBuilder.() -> Unit) = zeroOrMore(QuantifierMode.LAZY, block)

    /**
     * Lazy version of oneOrMore - matches 1 or more times, preferring fewer.
     */
    fun oneOrMoreLazy(block: RegexBuilder.() -> Unit) = oneOrMore(QuantifierMode.LAZY, block)

    // =========================================================================
    // Possessive Quantifiers (Convenience methods)
    // =========================================================================

    /**
     * Possessive version of optional - matches 0 or 1 time, no backtracking.
     */
    fun optionalPossessive(block: RegexBuilder.() -> Unit) = optional(QuantifierMode.POSSESSIVE, block)

    /**
     * Possessive version of zeroOrMore - matches 0 or more times, no backtracking.
     */
    fun zeroOrMorePossessive(block: RegexBuilder.() -> Unit) = zeroOrMore(QuantifierMode.POSSESSIVE, block)

    /**
     * Possessive version of oneOrMore - matches 1 or more times, no backtracking.
     */
    fun oneOrMorePossessive(block: RegexBuilder.() -> Unit) = oneOrMore(QuantifierMode.POSSESSIVE, block)

    // =========================================================================
    // Lookaround Assertions (Zero-width assertions)
    // =========================================================================

    /**
     * Appends a Positive Lookahead `(?=...)`.
     * Asserts that the given pattern matches next, but does not consume characters.
     *
     * Example: `literal("q").lookAhead { literal("u") }` matches "q" only if followed by "u".
     */
    fun lookAhead(block: RegexBuilder.() -> Unit) {
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
    fun negativeLookAhead(block: RegexBuilder.() -> Unit) {
        append("(?!")
        this.block()
        append(")")
    }

    /**
     * Appends a Positive Lookbehind `(?<=...)`.
     * Asserts that the given pattern matches immediately before the current position.
     *
     * Example: `lookBehind { literal("$") }.digit()` matches a digit only if preceded by "$".
     */
    fun lookBehind(block: RegexBuilder.() -> Unit) {
        append("(?<=")
        this.block()
        append(")")
    }

    /**
     * Appends a Negative Lookbehind `(?<!...)`.
     * Asserts that the given pattern does NOT match immediately before the current position.
     *
     * Example: `negativeLookBehind { literal("-") }.digit()` matches a digit only if NOT preceded by "-".
     */
    fun negativeLookBehind(block: RegexBuilder.() -> Unit) {
        append("(?<!")
        this.block()
        append(")")
    }

    // =========================================================================
    // Inline Modifiers
    // =========================================================================

    /**
     * Enables case-insensitive matching for the enclosed pattern.
     */
    fun caseInsensitive(block: RegexBuilder.() -> Unit) {
        append("(?i:")
        this.block()
        append(")")
    }

    /**
     * Enables multiline mode for the enclosed pattern.
     * In this mode, ^ and $ match at line boundaries.
     */
    fun multiline(block: RegexBuilder.() -> Unit) {
        append("(?m:")
        this.block()
        append(")")
    }

    /**
     * Enables dotall mode for the enclosed pattern.
     * In this mode, . matches any character including newlines.
     */
    fun dotAll(block: RegexBuilder.() -> Unit) {
        append("(?s:")
        this.block()
        append(")")
    }

    /**
     * Enables comments mode for the enclosed pattern.
     * Whitespace is ignored and # starts a comment.
     */
    fun comments(block: RegexBuilder.() -> Unit) {
        append("(?x:")
        this.block()
        append(")")
    }
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
class CharClassBuilder(private val negated: Boolean = false) : CharacterRangeCapable {
    private val buffer = StringBuilder()

    /**
     * Adds a range of characters (e.g., a-z).
     *
     * @param start The starting character.
     * @param end The ending character.
     * @throws IllegalArgumentException if start > end.
     */
    override fun range(start: Char, end: Char) {
        require(start <= end) { "Range start must be <= end: '$start' > '$end'" }
        buffer.append("$start-$end")
    }

    /**
     * Adds individual characters to the class.
     * Special characters are automatically escaped.
     *
     * @param chars The characters to add.
     */
    fun chars(chars: String) {
        buffer.append(escapeForCharClass(chars))
    }

    /**
     * Adds a single character to the class.
     * Special characters are automatically escaped.
     */
    fun char(c: Char) = chars(c.toString())

    /**
     * Implementation of [CharacterRangeCapable.appendRaw].
     */
    override fun appendRaw(pattern: String) {
        buffer.append(pattern)
    }

    /**
     * Adds a raw pattern to the class without escaping.
     * Use with caution.
     */
    fun raw(pattern: String) = appendRaw(pattern)

    // digit(), nonDigit(), whitespace(), nonWhitespace(), wordChar(), nonWordChar(),
    // unicodeProperty(), notUnicodeProperty() are inherited from CharacterRangeCapable interface

    // =========================================================================
    // ASCII Character Range Shortcuts
    // =========================================================================

    /**
     * Adds ASCII lowercase letters range (a-z).
     */
    fun asciiLowercase() = range('a', 'z')

    /**
     * Adds ASCII uppercase letters range (A-Z).
     */
    fun asciiUppercase() = range('A', 'Z')

    /**
     * Adds all ASCII letters (a-zA-Z).
     */
    fun asciiLetter() {
        asciiLowercase()
        asciiUppercase()
    }

    /**
     * Adds ASCII alphanumeric characters (a-zA-Z0-9).
     */
    fun asciiAlphanumeric() {
        asciiLetter()
        digit()
    }

    /**
     * Adds hexadecimal digit characters (0-9a-fA-F).
     */
    fun hexDigit() {
        digit()
        range('a', 'f')
        range('A', 'F')
    }

    /**
     * Builds and returns the character class string.
     */
    internal fun build(): String {
        val prefix = if (negated) "[^" else "["
        return "$prefix$buffer]"
    }
}