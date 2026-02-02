package io.github.bedshanty.kregex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class RegexBuilderTest {

    // =========================================================================
    // Basic Pattern Building Tests
    // =========================================================================

    @Test
    fun `regex function creates valid Regex object`() {
        val pattern = regex {
            literal("hello")
        }
        assertTrue(pattern.matches("hello"))
        assertFalse(pattern.matches("world"))
    }

    @Test
    fun `pattern property returns generated pattern`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { digit() }
            endOfLine()
        }
        assertEquals("^\\d+$", pattern.pattern)
        assertTrue(pattern.matches("123"))
    }

    // =========================================================================
    // Anchors & Boundaries Tests
    // =========================================================================

    @Test
    fun `startOfLine and endOfLine anchors`() {
        val pattern = regex {
            startOfLine()
            literal("hello")
            endOfLine()
        }
        assertTrue(pattern.matches("hello"))
        assertFalse(pattern.matches("say hello"))
        assertFalse(pattern.matches("hello world"))
    }

    @Test
    fun `startOfInput and endOfInput anchors`() {
        val pattern = regex {
            startOfInput()
            digit()
        }
        assertEquals("\\A\\d", pattern.pattern)
    }

    @Test
    fun `wordBoundary matches word boundaries`() {
        val pattern = regex {
            wordBoundary()
            literal("cat")
            wordBoundary()
        }
        assertTrue(pattern.containsMatchIn("the cat sat"))
        assertFalse(pattern.containsMatchIn("concatenate"))
    }

    @Test
    fun `nonWordBoundary matches non-word boundaries`() {
        val pattern = regex {
            nonWordBoundary()
            literal("at")
        }
        assertTrue(pattern.containsMatchIn("cat"))
        assertFalse(pattern.containsMatchIn("at the"))
    }

    @Test
    fun `line block wraps with start and end of line anchors`() {
        val pattern = regex {
            line { digit() }
        }
        assertEquals("^\\d$", pattern.pattern)
        assertTrue(pattern.matches("5"))
        assertFalse(pattern.matches("55"))
        assertFalse(pattern.matches("a"))
    }

    @Test
    fun `input block wraps with start and end of input anchors`() {
        val pattern = regex {
            input { digit() }
        }
        assertEquals("\\A\\d\\z", pattern.pattern)
        assertTrue(pattern.matches("5"))
        assertFalse(pattern.matches("5\n"))
    }

    // =========================================================================
    // Character Classes Tests
    // =========================================================================

    @Test
    fun `anyChar matches any character`() {
        val pattern = regex {
            anyChar()
            anyChar()
            anyChar()
        }
        assertTrue(pattern.matches("abc"))
        assertTrue(pattern.matches("123"))
        assertTrue(pattern.matches("!@#"))
        assertFalse(pattern.matches("ab"))
    }

    @Test
    fun `digit and nonDigit character classes`() {
        val digitPattern = regex {
            startOfLine()
            digit()
            endOfLine()
        }
        assertTrue(digitPattern.matches("5"))
        assertFalse(digitPattern.matches("a"))

        val nonDigitPattern = regex {
            startOfLine()
            nonDigit()
            endOfLine()
        }
        assertTrue(nonDigitPattern.matches("a"))
        assertFalse(nonDigitPattern.matches("5"))
    }

    @Test
    fun `whitespace and nonWhitespace character classes`() {
        val wsPattern = regex {
            literal("hello")
            whitespace()
            literal("world")
        }
        assertTrue(wsPattern.matches("hello world"))
        assertTrue(wsPattern.matches("hello\tworld"))
        assertFalse(wsPattern.matches("helloworld"))

        val nonWsPattern = regex {
            nonWhitespace()
            nonWhitespace()
        }
        assertTrue(nonWsPattern.matches("ab"))
        assertFalse(nonWsPattern.matches("a "))
    }

    @Test
    fun `wordChar and nonWordChar character classes`() {
        val wordPattern = regex {
            startOfLine()
            oneOrMore { wordChar() }
            endOfLine()
        }
        assertTrue(wordPattern.matches("hello_123"))
        assertFalse(wordPattern.matches("hello world"))
    }

    @Test
    fun `special character classes - tab, newline, etc`() {
        val pattern = regex {
            tab()
            newline()
            carriageReturn()
        }
        assertEquals("\\t\\n\\r", pattern.pattern)
    }

    // =========================================================================
    // Unicode Support Tests
    // =========================================================================

    @Test
    fun `unicodeProperty matches unicode categories`() {
        val pattern = regex {
            unicodeProperty("L")
        }
        assertEquals("\\p{L}", pattern.pattern)
    }

    @Test
    fun `notUnicodeProperty matches non-unicode categories`() {
        val pattern = regex {
            notUnicodeProperty("L")
        }
        assertEquals("\\P{L}", pattern.pattern)
    }

    @Test
    fun `unicodeLetter convenience method`() {
        val pattern = regex {
            unicodeLetter()
        }
        assertEquals("\\p{L}", pattern.pattern)
    }

    @Test
    fun `unicodeScript generates correct pattern`() {
        val pattern = regex {
            unicodeScript("Greek")
        }
        assertEquals("\\p{IsGreek}", pattern.pattern)
    }

    @Test
    fun `unicodeBlock generates correct pattern`() {
        val pattern = regex {
            unicodeBlock("BasicLatin")
        }
        assertEquals("\\p{InBasicLatin}", pattern.pattern)
    }

    // =========================================================================
    // ASCII Character Ranges Tests
    // =========================================================================

    @Test
    fun `asciiLowercase matches lowercase letters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { asciiLowercase() }
            endOfLine()
        }
        assertTrue(pattern.matches("hello"))
        assertTrue(pattern.matches("abc"))
        assertFalse(pattern.matches("Hello"))
        assertFalse(pattern.matches("ABC"))
        assertFalse(pattern.matches("123"))
    }

    @Test
    fun `asciiUppercase matches uppercase letters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { asciiUppercase() }
            endOfLine()
        }
        assertTrue(pattern.matches("HELLO"))
        assertTrue(pattern.matches("ABC"))
        assertFalse(pattern.matches("hello"))
        assertFalse(pattern.matches("Hello"))
        assertFalse(pattern.matches("123"))
    }

    @Test
    fun `asciiLetter matches all ASCII letters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { asciiLetter() }
            endOfLine()
        }
        assertTrue(pattern.matches("hello"))
        assertTrue(pattern.matches("HELLO"))
        assertTrue(pattern.matches("HelloWorld"))
        assertFalse(pattern.matches("hello123"))
        assertFalse(pattern.matches("123"))
    }

    @Test
    fun `asciiAlphanumeric matches letters and digits`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { asciiAlphanumeric() }
            endOfLine()
        }
        assertTrue(pattern.matches("hello"))
        assertTrue(pattern.matches("HELLO"))
        assertTrue(pattern.matches("hello123"))
        assertTrue(pattern.matches("123"))
        assertFalse(pattern.matches("hello_world"))
        assertFalse(pattern.matches("hello-world"))
    }

    @Test
    fun `hexDigit matches hexadecimal characters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { hexDigit() }
            endOfLine()
        }
        assertTrue(pattern.matches("0123456789"))
        assertTrue(pattern.matches("abcdef"))
        assertTrue(pattern.matches("ABCDEF"))
        assertTrue(pattern.matches("DeadBeef"))
        assertTrue(pattern.matches("ff00ff"))
        assertFalse(pattern.matches("ghijkl"))
        assertFalse(pattern.matches("0x123"))
    }

    @Test
    fun `ASCII methods generate correct patterns`() {
        assertEquals("[a-z]", regex { asciiLowercase() }.pattern)
        assertEquals("[A-Z]", regex { asciiUppercase() }.pattern)
        assertEquals("[a-zA-Z]", regex { asciiLetter() }.pattern)
        assertEquals("[a-zA-Z0-9]", regex { asciiAlphanumeric() }.pattern)
        assertEquals("[0-9a-fA-F]", regex { hexDigit() }.pattern)
    }

    // =========================================================================
    // ASCII Block Tests
    // =========================================================================

    @Test
    fun `ascii block with lower generates correct pattern`() {
        val pattern = regex {
            ascii { lower() }
        }
        assertEquals("[a-z]", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("z"))
        assertFalse(pattern.matches("A"))
        assertFalse(pattern.matches("1"))
    }

    @Test
    fun `ascii block with upper generates correct pattern`() {
        val pattern = regex {
            ascii { upper() }
        }
        assertEquals("[A-Z]", pattern.pattern)
        assertTrue(pattern.matches("A"))
        assertTrue(pattern.matches("Z"))
        assertFalse(pattern.matches("a"))
        assertFalse(pattern.matches("1"))
    }

    @Test
    fun `ascii block with lower and upper generates correct pattern`() {
        val pattern = regex {
            ascii {
                lower()
                upper()
            }
        }
        assertEquals("[a-zA-Z]", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("Z"))
        assertFalse(pattern.matches("1"))
    }

    @Test
    fun `ascii block with letter generates correct pattern`() {
        val pattern = regex {
            ascii { letter() }
        }
        assertEquals("[a-zA-Z]", pattern.pattern)
    }

    @Test
    fun `ascii block with digit generates correct pattern`() {
        val pattern = regex {
            ascii { digit() }
        }
        assertEquals("[0-9]", pattern.pattern)
        assertTrue(pattern.matches("5"))
        assertFalse(pattern.matches("a"))
    }

    @Test
    fun `ascii block with alphanumeric generates correct pattern`() {
        val pattern = regex {
            ascii { alphanumeric() }
        }
        assertEquals("[a-zA-Z0-9]", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("Z"))
        assertTrue(pattern.matches("5"))
        assertFalse(pattern.matches("_"))
    }

    @Test
    fun `ascii block with hexDigit generates correct pattern`() {
        val pattern = regex {
            ascii { hexDigit() }
        }
        assertEquals("[0-9a-fA-F]", pattern.pattern)
        assertTrue(pattern.matches("0"))
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("F"))
        assertFalse(pattern.matches("g"))
    }

    @Test
    fun `ascii block in charClass generates correct pattern`() {
        val pattern = regex {
            charClass {
                ascii {
                    lower()
                    upper()
                }
                chars("_")
            }
        }
        assertEquals("[a-zA-Z_]", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("Z"))
        assertTrue(pattern.matches("_"))
        assertFalse(pattern.matches("1"))
    }

    @Test
    fun `ascii block combined with quantifiers`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { ascii { letter() } }
            endOfLine()
        }
        assertEquals("^[a-zA-Z]+$", pattern.pattern)
        assertTrue(pattern.matches("HelloWorld"))
        assertFalse(pattern.matches("Hello123"))
    }

    // =========================================================================
    // Literals & Custom Sets Tests
    // =========================================================================

    @Test
    fun `literal escapes special characters`() {
        val pattern = regex {
            literal("a.b*c?")
        }
        assertTrue(pattern.matches("a.b*c?"))
        assertFalse(pattern.matches("aXbYc"))
    }

    @Test
    fun `char matches single character`() {
        val pattern = regex {
            char('.')
        }
        assertTrue(pattern.matches("."))
        assertFalse(pattern.matches("a"))
    }

    @Test
    fun `anyOf creates character set with proper escaping`() {
        val pattern = regex {
            anyOf("abc")
        }
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("b"))
        assertFalse(pattern.matches("d"))
    }

    @Test
    fun `anyOf escapes special characters inside character class`() {
        val pattern = regex {
            startOfLine()
            anyOf("]-^\\")
            endOfLine()
        }
        assertTrue(pattern.matches("]"))
        assertTrue(pattern.matches("-"))
        assertTrue(pattern.matches("^"))
        assertTrue(pattern.matches("\\"))
    }

    @Test
    fun `noneOf creates negated character set`() {
        val pattern = regex {
            startOfLine()
            noneOf("abc")
            endOfLine()
        }
        assertTrue(pattern.matches("d"))
        assertTrue(pattern.matches("x"))
        assertFalse(pattern.matches("a"))
        assertFalse(pattern.matches("b"))
    }

    @Test
    fun `anyOf block creates character class`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                anyOf {
                    range('a', 'z')
                    digit()
                }
            }
            endOfLine()
        }
        assertEquals("^[a-z\\d]+$", pattern.pattern)
        assertTrue(pattern.matches("hello123"))
        assertTrue(pattern.matches("abc"))
        assertTrue(pattern.matches("456"))
        assertFalse(pattern.matches("ABC"))
        assertFalse(pattern.matches("hello_world"))
    }

    @Test
    fun `noneOf block creates negated character class`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                noneOf {
                    range('0', '9')
                    whitespace()
                }
            }
            endOfLine()
        }
        assertEquals("^[^0-9\\s]+$", pattern.pattern)
        assertTrue(pattern.matches("hello"))
        assertTrue(pattern.matches("ABC"))
        assertFalse(pattern.matches("hello123"))
        assertFalse(pattern.matches("hello world"))
    }

    @Test
    fun `range creates character range`() {
        val pattern = regex {
            startOfLine()
            range('a', 'z')
            endOfLine()
        }
        assertTrue(pattern.matches("m"))
        assertFalse(pattern.matches("A"))
        assertFalse(pattern.matches("5"))
    }

    @Test
    fun `range validation throws on invalid range`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                range('z', 'a')
            }
        }
    }

    @Test
    fun `notInRange creates negated character range`() {
        val pattern = regex {
            startOfLine()
            notInRange('a', 'z')
            endOfLine()
        }
        assertTrue(pattern.matches("A"))
        assertTrue(pattern.matches("5"))
        assertFalse(pattern.matches("m"))
    }

    @Test
    fun `or creates alternation`() {
        val pattern = regex {
            literal("cat")
            or()
            literal("dog")
        }
        assertTrue(pattern.matches("cat"))
        assertTrue(pattern.matches("dog"))
        assertFalse(pattern.matches("bird"))
    }

    @Test
    fun `either creates alternation group`() {
        val pattern = regex {
            startOfLine()
            either(
                { literal("cat") },
                { literal("dog") },
                { literal("bird") }
            )
            endOfLine()
        }
        assertTrue(pattern.matches("cat"))
        assertTrue(pattern.matches("dog"))
        assertTrue(pattern.matches("bird"))
        assertFalse(pattern.matches("fish"))
    }

    @Test
    fun `either requires at least one alternative`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                either()
            }
        }
    }

    // =========================================================================
    // CharClassBuilder Tests
    // =========================================================================

    @Test
    fun `charClass combines multiple ranges and characters`() {
        val pattern = regex {
            startOfLine()
            charClass {
                range('a', 'z')
                range('A', 'Z')
                chars("_")
            }
            endOfLine()
        }
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("Z"))
        assertTrue(pattern.matches("_"))
        assertFalse(pattern.matches("5"))
    }

    @Test
    fun `negatedCharClass creates negated class`() {
        val pattern = regex {
            startOfLine()
            negatedCharClass {
                range('0', '9')
            }
            endOfLine()
        }
        assertTrue(pattern.matches("a"))
        assertFalse(pattern.matches("5"))
    }

    @Test
    fun `charClass with predefined classes`() {
        val pattern = regex {
            charClass {
                digit()
                wordChar()
                whitespace()
            }
        }
        assertEquals("[\\d\\w\\s]", pattern.pattern)
    }

    @Test
    fun `charClass with asciiLowercase shortcut`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass { asciiLowercase() }
            }
            endOfLine()
        }
        assertEquals("^[a-z]+$", pattern.pattern)
        assertTrue(pattern.matches("hello"))
        assertFalse(pattern.matches("Hello"))
    }

    @Test
    fun `charClass with asciiUppercase shortcut`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass { asciiUppercase() }
            }
            endOfLine()
        }
        assertEquals("^[A-Z]+$", pattern.pattern)
        assertTrue(pattern.matches("HELLO"))
        assertFalse(pattern.matches("Hello"))
    }

    @Test
    fun `charClass with asciiLetter shortcut`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass { asciiLetter() }
            }
            endOfLine()
        }
        assertEquals("^[a-zA-Z]+$", pattern.pattern)
        assertTrue(pattern.matches("HelloWorld"))
        assertFalse(pattern.matches("Hello123"))
    }

    @Test
    fun `charClass with asciiAlphanumeric shortcut`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass { asciiAlphanumeric() }
            }
            endOfLine()
        }
        assertEquals("^[a-zA-Z0-9]+$", pattern.pattern)
        assertTrue(pattern.matches("Hello123"))
        assertFalse(pattern.matches("Hello_123"))
    }

    @Test
    fun `charClass with hexDigit shortcut`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass { hexDigit() }
            }
            endOfLine()
        }
        assertEquals("^[0-9a-fA-F]+$", pattern.pattern)
        assertTrue(pattern.matches("DeadBeef"))
        assertTrue(pattern.matches("123abc"))
        assertFalse(pattern.matches("xyz"))
    }

    @Test
    fun `charClass shortcuts can be combined`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    asciiLowercase()
                    digit()
                    chars("-")
                }
            }
            endOfLine()
        }
        assertEquals("^[a-z\\d\\-]+$", pattern.pattern)
        assertTrue(pattern.matches("hello-123"))
        assertFalse(pattern.matches("Hello-123"))
    }

    // =========================================================================
    // Groups & Capturing Tests
    // =========================================================================

    @Test
    fun `capture creates capturing group`() {
        val pattern = regex {
            capture {
                oneOrMore { digit() }
            }
            literal("-")
            capture {
                oneOrMore { digit() }
            }
        }
        val match = pattern.find("123-456")!!
        assertEquals("123", match.groupValues[1])
        assertEquals("456", match.groupValues[2])
    }

    @Test
    fun `capture with name creates named capturing group`() {
        val pattern = regex {
            captureAs("year") {
                repeat(4) { digit() }
            }
            literal("-")
            captureAs("month") {
                repeat(2) { digit() }
            }
        }
        val match = pattern.find("2026-01")!!
        assertEquals("2026", match.groups["year"]?.value)
        assertEquals("01", match.groups["month"]?.value)
    }

    @Test
    fun `capture name validation`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                captureAs("") { literal("test") }
            }
        }

        assertFailsWith<IllegalArgumentException> {
            regex {
                captureAs("123invalid") { literal("test") }
            }
        }

        assertFailsWith<IllegalArgumentException> {
            regex {
                captureAs("invalid-name") { literal("test") }
            }
        }
    }

    @Test
    fun `group creates non-capturing group`() {
        val pattern = regex {
            group {
                literal("ab")
            }
            oneOrMore {
                group {
                    literal("cd")
                }
            }
        }
        val match = pattern.find("abcdcd")!!
        assertEquals(1, match.groupValues.size) // Only group 0 (full match)
    }

    // =========================================================================
    // Back References Tests
    // =========================================================================

    @Test
    fun `backReference by number`() {
        val pattern = regex {
            capture {
                oneOrMore { wordChar() }
            }
            whitespace()
            backReference(1)
        }
        assertTrue(pattern.containsMatchIn("hello hello"))
        assertFalse(pattern.containsMatchIn("hello world"))
    }

    @Test
    fun `backReference by name`() {
        val pattern = regex {
            captureAs("word") {
                oneOrMore { wordChar() }
            }
            whitespace()
            backReference("word")
        }
        assertTrue(pattern.containsMatchIn("hello hello"))
        assertFalse(pattern.containsMatchIn("hello world"))
    }

    @Test
    fun `backReference validation`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                backReference(0)
            }
        }

        assertFailsWith<IllegalArgumentException> {
            regex {
                backReference("")
            }
        }
    }

    // =========================================================================
    // Quantifiers Tests
    // =========================================================================

    @Test
    fun `optional matches 0 or 1 times`() {
        val pattern = regex {
            literal("colo")
            optional { literal("u") }
            literal("r")
        }
        assertTrue(pattern.matches("color"))
        assertTrue(pattern.matches("colour"))
    }

    @Test
    fun `zeroOrMore matches 0 or more times`() {
        val pattern = regex {
            startOfLine()
            zeroOrMore { digit() }
            endOfLine()
        }
        assertTrue(pattern.matches(""))
        assertTrue(pattern.matches("123"))
    }

    @Test
    fun `oneOrMore matches 1 or more times`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { digit() }
            endOfLine()
        }
        assertFalse(pattern.matches(""))
        assertTrue(pattern.matches("1"))
        assertTrue(pattern.matches("123"))
    }

    @Test
    fun `repeat exact count`() {
        val pattern = regex {
            startOfLine()
            repeat(3) { digit() }
            endOfLine()
        }
        assertFalse(pattern.matches("12"))
        assertTrue(pattern.matches("123"))
        assertFalse(pattern.matches("1234"))
    }

    @Test
    fun `repeat validation`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                repeat(-1) { digit() }
            }
        }
    }

    @Test
    fun `repeat range`() {
        val pattern = regex {
            startOfLine()
            repeat(2, 4) { digit() }
            endOfLine()
        }
        assertFalse(pattern.matches("1"))
        assertTrue(pattern.matches("12"))
        assertTrue(pattern.matches("123"))
        assertTrue(pattern.matches("1234"))
        assertFalse(pattern.matches("12345"))
    }

    @Test
    fun `repeat range validation`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                repeat(-1, 5) { digit() }
            }
        }

        assertFailsWith<IllegalArgumentException> {
            regex {
                repeat(5, 3) { digit() }
            }
        }
    }

    @Test
    fun `atLeast matches minimum or more times`() {
        val pattern = regex {
            startOfLine()
            atLeast(2) { digit() }
            endOfLine()
        }
        assertFalse(pattern.matches("1"))
        assertTrue(pattern.matches("12"))
        assertTrue(pattern.matches("123456789"))
    }

    @Test
    fun `atLeast validation`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                atLeast(-1) { digit() }
            }
        }
    }

    // =========================================================================
    // Lazy Quantifiers Tests
    // =========================================================================

    @Test
    fun `lazy quantifiers prefer fewer matches`() {
        val greedy = regex {
            literal("<")
            oneOrMore { anyChar() }
            literal(">")
        }
        val lazy = regex {
            literal("<")
            oneOrMoreLazy { anyChar() }
            literal(">")
        }

        val input = "<a><b>"
        assertEquals("<a><b>", greedy.find(input)?.value)
        assertEquals("<a>", lazy.find(input)?.value)
    }

    @Test
    fun `zeroOrMoreLazy generates correct pattern`() {
        val pattern = regex {
            zeroOrMoreLazy { anyChar() }
        }
        assertEquals(".*?", pattern.pattern)
    }

    @Test
    fun `optionalLazy generates correct pattern`() {
        val pattern = regex {
            optionalLazy { digit() }
        }
        assertEquals("\\d??", pattern.pattern)
    }

    // =========================================================================
    // Possessive Quantifiers Tests
    // =========================================================================

    @Test
    fun `possessive quantifiers generate correct pattern`() {
        val pattern = regex {
            oneOrMorePossessive { digit() }
        }
        assertEquals("\\d++", pattern.pattern)
    }

    @Test
    fun `zeroOrMorePossessive generates correct pattern`() {
        val pattern = regex {
            zeroOrMorePossessive { anyChar() }
        }
        assertEquals(".*+", pattern.pattern)
    }

    @Test
    fun `optionalPossessive generates correct pattern`() {
        val pattern = regex {
            optionalPossessive { digit() }
        }
        assertEquals("\\d?+", pattern.pattern)
    }

    // =========================================================================
    // Lookaround Tests
    // =========================================================================

    @Test
    fun `lookAhead positive assertion`() {
        val pattern = regex {
            oneOrMore { digit() }
            lookAhead { literal("px") }
        }
        assertTrue(pattern.containsMatchIn("100px"))
        assertFalse(pattern.containsMatchIn("100em"))
        assertEquals("100", pattern.find("100px")?.value)
    }

    @Test
    fun `negativeLookAhead assertion`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { digit() }
            negativeLookAhead { literal("px") }
            endOfLine()
        }
        assertFalse(pattern.matches("100px"))
        assertTrue(pattern.matches("100"))
    }

    @Test
    fun `lookBehind positive assertion`() {
        val pattern = regex {
            lookBehind { literal("$") }
            oneOrMore { digit() }
        }
        assertTrue(pattern.containsMatchIn("$100"))
        assertFalse(pattern.containsMatchIn("100"))
        assertEquals("100", pattern.find("$100")?.value)
    }

    @Test
    fun `negativeLookBehind assertion`() {
        val pattern = regex {
            negativeLookBehind { literal("$") }
            oneOrMore { digit() }
        }
        assertTrue(pattern.containsMatchIn("100"))
        // Note: "$100" still matches because "00" doesn't have $ before it
    }

    // =========================================================================
    // Inline Modifiers Tests
    // =========================================================================

    @Test
    fun `caseInsensitive modifier`() {
        val pattern = regex {
            caseInsensitive {
                literal("hello")
            }
        }
        assertTrue(pattern.matches("hello"))
        assertTrue(pattern.matches("HELLO"))
        assertTrue(pattern.matches("HeLLo"))
    }

    @Test
    fun `multiline modifier pattern`() {
        val pattern = regex {
            multiline {
                startOfLine()
                digit()
            }
        }
        assertEquals("(?m:^\\d)", pattern.pattern)
    }

    @Test
    fun `dotAll modifier pattern`() {
        val pattern = regex {
            dotAll {
                anyChar()
            }
        }
        assertEquals("(?s:.)", pattern.pattern)
    }

    // =========================================================================
    // Raw Pattern Tests
    // =========================================================================

    @Test
    fun `appendRaw appends pattern without escaping`() {
        val pattern = regex {
            appendRaw("[a-z]+")
        }
        assertTrue(pattern.matches("hello"))
        assertFalse(pattern.matches("123"))
    }

    // =========================================================================
    // Complex Pattern Tests
    // =========================================================================

    @Test
    fun `phone number pattern`() {
        val phone = regex {
            startOfLine()
            optional { literal("+") }
            optional {
                repeat(1, 3) { digit() }
                optional { anyOf(" -") }
            }
            repeat(3) { digit() }
            anyOf(" -")
            repeat(3, 4) { digit() }
            anyOf(" -")
            repeat(4) { digit() }
            endOfLine()
        }
        assertTrue(phone.matches("123-456-7890"))
        assertTrue(phone.matches("123 456 7890"))
        assertTrue(phone.matches("+1 123-456-7890"))
    }

    @Test
    fun `URL pattern`() {
        val url = regex(RegexOption.IGNORE_CASE) {
            startOfLine()
            captureAs("protocol") {
                either(
                    { literal("https") },
                    { literal("http") }
                )
            }
            literal("://")
            captureAs("domain") {
                oneOrMore {
                    charClass {
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
                    zeroOrMore {
                        charClass {
                            wordChar()
                            chars("/.%-")
                        }
                    }
                }
            }
            endOfLine()
        }

        val match = url.find("https://www.example.com:8080/path/to/page")!!
        assertEquals("https", match.groups["protocol"]?.value)
        assertEquals("www.example.com", match.groups["domain"]?.value)
        assertEquals(":8080", match.groups["port"]?.value)
    }

    @Test
    fun `HTML tag pattern with back reference`() {
        val htmlTag = regex {
            literal("<")
            captureAs("tag") {
                oneOrMore { wordChar() }
            }
            zeroOrMore { anyChar() }
            literal(">")
            zeroOrMoreLazy { anyChar() }
            literal("</")
            backReference("tag")
            literal(">")
        }

        assertTrue(htmlTag.containsMatchIn("<div>content</div>"))
        assertTrue(htmlTag.containsMatchIn("<span class=\"test\">text</span>"))
        assertFalse(htmlTag.containsMatchIn("<div>content</span>"))
    }

    // =========================================================================
    // CharClassBuilder Extended Tests
    // =========================================================================

    @Test
    fun `charClass with nonDigit`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    nonDigit()
                }
            }
            endOfLine()
        }
        assertTrue(pattern.matches("abc"))
        assertTrue(pattern.matches("!@#"))
        assertFalse(pattern.matches("123"))
        assertFalse(pattern.matches("a1b"))
    }

    @Test
    fun `charClass with nonWordChar`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    nonWordChar()
                }
            }
            endOfLine()
        }
        assertTrue(pattern.matches("!@#"))
        assertTrue(pattern.matches("..."))
        assertFalse(pattern.matches("abc"))
        assertFalse(pattern.matches("a!b"))
    }

    @Test
    fun `charClass with nonWhitespace`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    nonWhitespace()
                }
            }
            endOfLine()
        }
        assertTrue(pattern.matches("abc123"))
        assertTrue(pattern.matches("!@#"))
        assertFalse(pattern.matches("a b"))
        assertFalse(pattern.matches(" "))
    }

    // =========================================================================
    // Quantifier Optimization Tests
    // =========================================================================

    @Test
    fun `single atom optimization - digit`() {
        val pattern = regex {
            oneOrMore { digit() }
        }
        assertEquals("\\d+", pattern.pattern)
    }

    @Test
    fun `single atom optimization - anyChar`() {
        val pattern = regex {
            zeroOrMore { anyChar() }
        }
        assertEquals(".*", pattern.pattern)
    }

    @Test
    fun `single atom optimization - charClass`() {
        val pattern = regex {
            oneOrMore {
                charClass {
                    range('a', 'z')
                }
            }
        }
        assertEquals("[a-z]+", pattern.pattern)
    }

    @Test
    fun `single atom optimization - unicode property`() {
        val pattern = regex {
            oneOrMore { unicodeLetter() }
        }
        assertEquals("\\p{L}+", pattern.pattern)
    }

    @Test
    fun `multi element still gets grouped`() {
        val pattern = regex {
            oneOrMore {
                digit()
                literal("-")
            }
        }
        assertEquals("(?:\\d\\Q-\\E)+", pattern.pattern)
        assertTrue(pattern.matches("1-2-3-"))
    }

    @Test
    fun `already grouped pattern stays optimized`() {
        val pattern = regex {
            oneOrMore {
                group {
                    literal("ab")
                }
            }
        }
        assertEquals("(?:\\Qab\\E)+", pattern.pattern)
        assertTrue(pattern.matches("abab"))
    }

    // =========================================================================
    // Patterns Extension Functions Tests
    // =========================================================================

    @Test
    fun `email pattern`() {
        val pattern = regex {
            startOfLine()
            email()
            endOfLine()
        }
        assertTrue(pattern.matches("user@example.com"))
        assertTrue(pattern.matches("user.name+tag@example.org"))
        assertTrue(pattern.matches("test123@sub.domain.com"))
        assertFalse(pattern.matches("invalid"))
        assertFalse(pattern.matches("@example.com"))
        assertFalse(pattern.matches("user@"))
    }

    @Test
    fun `httpUrl pattern`() {
        val pattern = regex {
            startOfLine()
            httpUrl()
            endOfLine()
        }
        assertTrue(pattern.matches("http://example.com"))
        assertTrue(pattern.matches("https://example.com"))
        assertTrue(pattern.matches("https://www.example.com:8080/path"))
        assertTrue(pattern.matches("http://localhost:3000"))
        assertFalse(pattern.matches("ftp://example.com"))
    }

    @Test
    fun `httpUrlWithCapture extracts groups`() {
        val pattern = regex {
            startOfLine()
            httpUrlWithCapture()
            endOfLine()
        }
        val match = pattern.find("https://example.com:8080/path/to/page")!!
        assertEquals("https", match.groups["protocol"]?.value)
        assertEquals("example.com", match.groups["domain"]?.value)
        assertEquals(":8080", match.groups["port"]?.value)
        assertEquals("/path/to/page", match.groups["path"]?.value)
    }

    @Test
    fun `ipv4 pattern`() {
        val pattern = regex {
            startOfLine()
            ipv4()
            endOfLine()
        }
        assertTrue(pattern.matches("192.168.1.1"))
        assertTrue(pattern.matches("10.0.0.1"))
        assertTrue(pattern.matches("255.255.255.255"))
        assertFalse(pattern.matches("192.168.1"))
        assertFalse(pattern.matches("192.168.1.1.1"))
    }

    @Test
    fun `ipv4Strict validates octet range`() {
        val pattern = regex {
            startOfLine()
            ipv4Strict()
            endOfLine()
        }
        assertTrue(pattern.matches("192.168.1.1"))
        assertTrue(pattern.matches("0.0.0.0"))
        assertTrue(pattern.matches("255.255.255.255"))
        assertFalse(pattern.matches("256.1.1.1"))
        assertFalse(pattern.matches("1.256.1.1"))
    }

    @Test
    fun `uuid pattern`() {
        val pattern = regex {
            startOfLine()
            uuid()
            endOfLine()
        }
        assertTrue(pattern.matches("550e8400-e29b-41d4-a716-446655440000"))
        assertTrue(pattern.matches("ABCDEF12-3456-7890-ABCD-EF1234567890"))
        assertFalse(pattern.matches("550e8400-e29b-41d4-a716"))
        assertFalse(pattern.matches("not-a-uuid"))
    }

    @Test
    fun `hexColor pattern`() {
        val pattern = regex {
            startOfLine()
            hexColor()
            endOfLine()
        }
        assertTrue(pattern.matches("#fff"))
        assertTrue(pattern.matches("#FFF"))
        assertTrue(pattern.matches("#FF5733"))
        assertTrue(pattern.matches("#ff5733"))
        assertFalse(pattern.matches("fff"))
        assertFalse(pattern.matches("#gg0000"))
    }

    @Test
    fun `isoDate pattern`() {
        val pattern = regex {
            startOfLine()
            isoDate()
            endOfLine()
        }
        assertTrue(pattern.matches("2026-01-15"))
        assertTrue(pattern.matches("1999-12-31"))
        assertFalse(pattern.matches("2026/01/15"))
        assertFalse(pattern.matches("24-01-15"))
    }

    @Test
    fun `time pattern`() {
        val pattern = regex {
            startOfLine()
            time()
            endOfLine()
        }
        assertTrue(pattern.matches("14:30"))
        assertTrue(pattern.matches("14:30:59"))
        assertTrue(pattern.matches("00:00:00"))
        assertFalse(pattern.matches("14:30:"))
        assertFalse(pattern.matches("1430"))
    }

    @Test
    fun `semver pattern`() {
        val pattern = regex {
            startOfLine()
            semver()
            endOfLine()
        }
        assertTrue(pattern.matches("1.0.0"))
        assertTrue(pattern.matches("2.1.3"))
        assertTrue(pattern.matches("1.0.0-alpha"))
        assertTrue(pattern.matches("1.0.0-alpha.1"))
        assertTrue(pattern.matches("1.0.0+build.123"))
        assertTrue(pattern.matches("1.0.0-beta+build.456"))
        assertFalse(pattern.matches("1.0"))
        assertFalse(pattern.matches("v1.0.0"))
    }

    @Test
    fun `integer pattern`() {
        val pattern = regex {
            startOfLine()
            integer()
            endOfLine()
        }
        assertTrue(pattern.matches("123"))
        assertTrue(pattern.matches("-456"))
        assertTrue(pattern.matches("+789"))
        assertTrue(pattern.matches("0"))
        assertFalse(pattern.matches("12.34"))
        assertFalse(pattern.matches("abc"))
    }

    @Test
    fun `decimal pattern`() {
        val pattern = regex {
            startOfLine()
            decimal()
            endOfLine()
        }
        assertTrue(pattern.matches("123"))
        assertTrue(pattern.matches("123.456"))
        assertTrue(pattern.matches("-0.5"))
        assertTrue(pattern.matches(".25"))
        assertTrue(pattern.matches("+3.14"))
        assertFalse(pattern.matches("abc"))
    }

    @Test
    fun `slug pattern`() {
        val pattern = regex {
            startOfLine()
            slug()
            endOfLine()
        }
        assertTrue(pattern.matches("my-blog-post"))
        assertTrue(pattern.matches("post-123"))
        assertTrue(pattern.matches("a"))
        assertFalse(pattern.matches("My Blog Post"))
        assertFalse(pattern.matches("post_123"))
    }

    @Test
    fun `phoneNumber pattern`() {
        val pattern = regex {
            startOfLine()
            phoneNumber()
            endOfLine()
        }
        assertTrue(pattern.matches("123-456-7890"))
        assertTrue(pattern.matches("(123) 456-7890"))
        assertTrue(pattern.matches("1234567890"))
        assertTrue(pattern.matches("+1-123-456-7890"))
    }

    @Test
    fun `usPhoneNumber pattern`() {
        val pattern = regex {
            startOfLine()
            usPhoneNumber()
            endOfLine()
        }
        assertTrue(pattern.matches("(123) 456-7890"))
        assertTrue(pattern.matches("123-456-7890"))
        assertFalse(pattern.matches("1234567890"))
    }

    // =========================================================================
    // Korean (Hangul) Pattern Tests
    // =========================================================================

    @Test
    fun `hangulSyllable matches complete Korean syllables`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { hangulSyllable() }
            endOfLine()
        }
        assertTrue(pattern.matches("안녕하세요"))
        assertTrue(pattern.matches("가나다라마바사"))
        assertTrue(pattern.matches("한글"))
        assertFalse(pattern.matches("hello"))
        assertFalse(pattern.matches("ㄱㄴㄷ"))
        assertFalse(pattern.matches("ㅏㅓㅗ"))
        assertFalse(pattern.matches("한글123"))
    }

    @Test
    fun `hangulJamo matches Korean jamo characters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { hangulJamo() }
            endOfLine()
        }
        assertTrue(pattern.matches("ㄱㄴㄷㄹ"))
        assertTrue(pattern.matches("ㅏㅓㅗㅜ"))
        assertTrue(pattern.matches("ㄱㅏㄴㅏ"))
        assertFalse(pattern.matches("가나다"))
        assertFalse(pattern.matches("abc"))
    }

    @Test
    fun `hangulConsonant matches Korean consonants only`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { hangulConsonant() }
            endOfLine()
        }
        assertTrue(pattern.matches("ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎ"))
        assertTrue(pattern.matches("ㄱㄱㄱ"))
        assertFalse(pattern.matches("ㅏㅓㅗ"))
        assertFalse(pattern.matches("가나다"))
        assertFalse(pattern.matches("ㄱㅏ"))
    }

    @Test
    fun `hangulVowel matches Korean vowels only`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { hangulVowel() }
            endOfLine()
        }
        assertTrue(pattern.matches("ㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣ"))
        assertTrue(pattern.matches("ㅏㅏㅏ"))
        assertFalse(pattern.matches("ㄱㄴㄷ"))
        assertFalse(pattern.matches("가나다"))
        assertFalse(pattern.matches("ㅏㄱ"))
    }

    @Test
    fun `hangul patterns generate correct regex`() {
        assertEquals("[가-힣]", regex { hangulSyllable() }.pattern)
        assertEquals("[ㄱ-ㅣ]", regex { hangulJamo() }.pattern)
        assertEquals("[ㄱ-ㅎ]", regex { hangulConsonant() }.pattern)
        assertEquals("[ㅏ-ㅣ]", regex { hangulVowel() }.pattern)
    }

    @Test
    fun `hangulSyllable combined with other patterns`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { hangulSyllable() }
            whitespace()
            oneOrMore { digit() }
            endOfLine()
        }
        assertTrue(pattern.matches("가격 1000"))
        assertTrue(pattern.matches("번호 123"))
        assertFalse(pattern.matches("price 1000"))
    }

    @Test
    fun `hangulSyllable patterns work inside charClass`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    hangulSyllable()
                    digit()
                }
            }
            endOfLine()
        }
        assertEquals("^[가-힣\\d]+$", pattern.pattern)
        assertTrue(pattern.matches("안녕123"))
        assertTrue(pattern.matches("가나다"))
        assertTrue(pattern.matches("123"))
        assertFalse(pattern.matches("hello"))
        assertFalse(pattern.matches("ㄱㄴㄷ"))
    }

    @Test
    fun `hangulJamo works inside charClass`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    hangulJamo()
                    chars("!")
                }
            }
            endOfLine()
        }
        assertEquals("^[ㄱ-ㅣ!]+$", pattern.pattern)
        assertTrue(pattern.matches("ㄱㄴㄷ!"))
        assertTrue(pattern.matches("ㅋㅋㅋ"))
        assertFalse(pattern.matches("가나다"))
    }

    @Test
    fun `multiple hangul ranges in charClass`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    hangulSyllable()
                    hangulConsonant()
                    digit()
                }
            }
            endOfLine()
        }
        assertEquals("^[가-힣ㄱ-ㅎ\\d]+$", pattern.pattern)
        assertTrue(pattern.matches("가나다ㄱㄴㄷ123"))
        assertFalse(pattern.matches("ㅏㅓㅗ"))
    }

    // =========================================================================
    // POSIX Character Class Tests
    // =========================================================================

    @Test
    fun `posixAlnum matches alphanumeric characters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { posixAlnum() }
            endOfLine()
        }
        assertEquals("^\\p{Alnum}+$", pattern.pattern)
        assertTrue(pattern.matches("Hello123"))
        assertTrue(pattern.matches("abc"))
        assertTrue(pattern.matches("123"))
        assertFalse(pattern.matches("Hello World"))
        assertFalse(pattern.matches("hello!"))
    }

    @Test
    fun `posixAlpha matches alphabetic characters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { posixAlpha() }
            endOfLine()
        }
        assertEquals("^\\p{Alpha}+$", pattern.pattern)
        assertTrue(pattern.matches("Hello"))
        assertTrue(pattern.matches("abc"))
        assertFalse(pattern.matches("Hello123"))
        assertFalse(pattern.matches("123"))
    }

    @Test
    fun `posixDigit matches digits`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { posixDigit() }
            endOfLine()
        }
        assertEquals("^\\p{Digit}+$", pattern.pattern)
        assertTrue(pattern.matches("123"))
        assertFalse(pattern.matches("abc"))
    }

    @Test
    fun `posixLower matches lowercase letters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { posixLower() }
            endOfLine()
        }
        assertEquals("^\\p{Lower}+$", pattern.pattern)
        assertTrue(pattern.matches("hello"))
        assertFalse(pattern.matches("Hello"))
        assertFalse(pattern.matches("HELLO"))
    }

    @Test
    fun `posixUpper matches uppercase letters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { posixUpper() }
            endOfLine()
        }
        assertEquals("^\\p{Upper}+$", pattern.pattern)
        assertTrue(pattern.matches("HELLO"))
        assertFalse(pattern.matches("Hello"))
        assertFalse(pattern.matches("hello"))
    }

    @Test
    fun `posixXDigit matches hexadecimal digits`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { posixXDigit() }
            endOfLine()
        }
        assertEquals("^\\p{XDigit}+$", pattern.pattern)
        assertTrue(pattern.matches("0123456789"))
        assertTrue(pattern.matches("abcdef"))
        assertTrue(pattern.matches("ABCDEF"))
        assertTrue(pattern.matches("DeadBeef"))
        assertFalse(pattern.matches("xyz"))
    }

    @Test
    fun `posix classes work inside charClass`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                charClass {
                    posixAlpha()
                    posixDigit()
                }
            }
            endOfLine()
        }
        assertEquals("^[\\p{Alpha}\\p{Digit}]+$", pattern.pattern)
        assertTrue(pattern.matches("Hello123"))
    }

    @Test
    fun `posixSpace matches whitespace`() {
        val pattern = regex {
            posixAlpha()
            posixSpace()
            posixAlpha()
        }
        assertEquals("\\p{Alpha}\\p{Space}\\p{Alpha}", pattern.pattern)
        assertTrue(pattern.containsMatchIn("a b"))
        assertTrue(pattern.containsMatchIn("a\tb"))
        assertTrue(pattern.containsMatchIn("a\nb"))
    }

    @Test
    fun `posixPunct matches punctuation`() {
        val pattern = regex {
            startOfLine()
            oneOrMore { posixPunct() }
            endOfLine()
        }
        assertEquals("^\\p{Punct}+$", pattern.pattern)
        assertTrue(pattern.matches("!@#"))
        assertTrue(pattern.matches(".,;:"))
        assertFalse(pattern.matches("abc"))
    }

    // =========================================================================
    // Builder Block Tests (posix{}, unicode{}, hangul{})
    // =========================================================================

    @Test
    fun `posix builder in RegexBuilder creates character class`() {
        val pattern = regex {
            posix {
                alnum()
                punct()
            }
        }
        assertEquals("[\\p{Alnum}\\p{Punct}]", pattern.pattern)
    }

    @Test
    fun `posix builder in charClass does not double wrap`() {
        val pattern = regex {
            charClass {
                posix {
                    alpha()
                    digit()
                }
                chars("_")
            }
        }
        assertEquals("[\\p{Alpha}\\p{Digit}_]", pattern.pattern)
    }

    @Test
    fun `posix builder all methods`() {
        val pattern = regex {
            charClass {
                posix {
                    alnum()
                    alpha()
                    ascii()
                    blank()
                    cntrl()
                    digit()
                    graph()
                    lower()
                    print()
                    punct()
                    space()
                    upper()
                    xdigit()
                }
            }
        }
        val expected = "[\\p{Alnum}\\p{Alpha}\\p{ASCII}\\p{Blank}\\p{Cntrl}\\p{Digit}" +
                "\\p{Graph}\\p{Lower}\\p{Print}\\p{Punct}\\p{Space}\\p{Upper}\\p{XDigit}]"
        assertEquals(expected, pattern.pattern)
    }

    @Test
    fun `unicode builder in RegexBuilder creates character class`() {
        val pattern = regex {
            unicode {
                letter()
                number()
            }
        }
        assertEquals("[\\p{L}\\p{N}]", pattern.pattern)
    }

    @Test
    fun `unicode builder in charClass does not double wrap`() {
        val pattern = regex {
            charClass {
                unicode {
                    letter()
                    punctuation()
                }
                chars("-")
            }
        }
        assertEquals("[\\p{L}\\p{P}\\-]", pattern.pattern)
    }

    @Test
    fun `unicode builder all methods`() {
        val pattern = regex {
            charClass {
                unicode {
                    property("L")
                    notProperty("N")
                    script("Han")
                    block("BasicLatin")
                    letter()
                    uppercaseLetter()
                    lowercaseLetter()
                    number()
                    punctuation()
                    symbol()
                }
            }
        }
        val expected = "[\\p{L}\\P{N}\\p{IsHan}\\p{InBasicLatin}\\p{L}\\p{Lu}\\p{Ll}\\p{N}\\p{P}\\p{S}]"
        assertEquals(expected, pattern.pattern)
    }

    @Test
    fun `unicode builder with script matches Han characters`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                unicode {
                    script("Han")
                }
            }
            endOfLine()
        }
        assertTrue(pattern.matches("漢字"))
        assertTrue(pattern.matches("中文"))
        assertFalse(pattern.matches("한글"))
        assertFalse(pattern.matches("abc"))
    }

    @Test
    fun `hangul builder in RegexBuilder creates character class`() {
        val pattern = regex {
            hangul {
                syllable()
                consonant()
            }
        }
        assertEquals("[가-힣ㄱ-ㅎ]", pattern.pattern)
    }

    @Test
    fun `hangul builder in charClass does not double wrap`() {
        val pattern = regex {
            charClass {
                hangul {
                    syllable()
                    jamo()
                }
                digit()
            }
        }
        assertEquals("[가-힣ㄱ-ㅣ\\d]", pattern.pattern)
    }

    @Test
    fun `hangul builder all methods`() {
        val pattern = regex {
            charClass {
                hangul {
                    syllable()
                    jamo()
                    consonant()
                    vowel()
                }
            }
        }
        assertEquals("[가-힣ㄱ-ㅣㄱ-ㅎㅏ-ㅣ]", pattern.pattern)
    }

    @Test
    fun `hangul builder matches Korean text`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                hangul {
                    syllable()
                    consonant()
                }
            }
            endOfLine()
        }
        assertTrue(pattern.matches("안녕하세요"))
        assertTrue(pattern.matches("ㅋㅋㅋ"))
        assertTrue(pattern.matches("가나다ㄱㄴㄷ"))
        assertFalse(pattern.matches("ㅏㅓㅗ"))  // vowels only
        assertFalse(pattern.matches("hello"))
    }

    @Test
    fun `hangul builder vowel only`() {
        val pattern = regex {
            startOfLine()
            oneOrMore {
                hangul {
                    vowel()
                }
            }
            endOfLine()
        }
        assertTrue(pattern.matches("ㅏㅓㅗㅜ"))
        assertFalse(pattern.matches("ㄱㄴㄷ"))
        assertFalse(pattern.matches("가나다"))
    }

    @Test
    fun `combined builders in charClass`() {
        val pattern = regex {
            charClass {
                hangul {
                    syllable()
                }
                unicode {
                    script("Han")
                }
                ascii {
                    letter()
                }
            }
        }
        assertEquals("[가-힣\\p{IsHan}a-zA-Z]", pattern.pattern)
    }

    @Test
    fun `builders with quantifiers`() {
        val pattern = regex {
            oneOrMore {
                posix {
                    alnum()
                }
            }
            zeroOrMore {
                unicode {
                    punctuation()
                }
            }
            optional {
                hangul {
                    syllable()
                }
            }
        }
        assertEquals("[\\p{Alnum}]+[\\p{P}]*[가-힣]?", pattern.pattern)
    }
}
