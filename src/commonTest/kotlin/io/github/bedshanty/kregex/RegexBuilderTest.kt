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
    fun regexFunctionCreatesValidRegexObject() {
        val pattern = regex {
            literal("hello")
        }
        assertTrue(pattern.matches("hello"))
        assertFalse(pattern.matches("world"))
    }

    @Test
    fun patternPropertyReturnsGeneratedPattern() {
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
    fun startOfLineAndEndOfLineAnchors() {
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
    fun wordBoundaryMatchesWordBoundaries() {
        val pattern = regex {
            wordBoundary()
            literal("cat")
            wordBoundary()
        }
        assertTrue(pattern.containsMatchIn("the cat sat"))
        assertFalse(pattern.containsMatchIn("concatenate"))
    }

    @Test
    fun nonWordBoundaryMatchesNonWordBoundaries() {
        val pattern = regex {
            nonWordBoundary()
            literal("at")
        }
        assertTrue(pattern.containsMatchIn("cat"))
        assertFalse(pattern.containsMatchIn("at the"))
    }

    @Test
    fun lineBlockWrapsWithStartAndEndOfLineAnchors() {
        val pattern = regex {
            line { digit() }
        }
        assertEquals("^\\d$", pattern.pattern)
        assertTrue(pattern.matches("5"))
        assertFalse(pattern.matches("55"))
        assertFalse(pattern.matches("a"))
    }

    // =========================================================================
    // Character Classes Tests
    // =========================================================================

    @Test
    fun anyCharMatchesAnyCharacter() {
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
    fun digitAndNonDigitCharacterClasses() {
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
    fun whitespaceAndNonWhitespaceCharacterClasses() {
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
    fun wordCharAndNonWordCharCharacterClasses() {
        val wordPattern = regex {
            startOfLine()
            oneOrMore { wordChar() }
            endOfLine()
        }
        assertTrue(wordPattern.matches("hello_123"))
        assertFalse(wordPattern.matches("hello world"))
    }

    @Test
    fun specialCharacterClassesTabNewlineEtc() {
        val pattern = regex {
            tab()
            newline()
            carriageReturn()
        }
        assertEquals("\\t\\n\\r", pattern.pattern)
    }

    // =========================================================================
    // ASCII Character Ranges Tests
    // =========================================================================

    @Test
    fun asciiLowercaseMatchesLowercaseLetters() {
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
    fun asciiUppercaseMatchesUppercaseLetters() {
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
    fun asciiLetterMatchesAllAsciiLetters() {
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
    fun asciiAlphanumericMatchesLettersAndDigits() {
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
    fun hexDigitMatchesHexadecimalCharacters() {
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
    fun asciiMethodsGenerateCorrectPatterns() {
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
    fun asciiBlockWithLowerGeneratesCorrectPattern() {
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
    fun asciiBlockWithUpperGeneratesCorrectPattern() {
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
    fun asciiBlockWithLowerAndUpperGeneratesCorrectPattern() {
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
    fun asciiBlockWithLetterGeneratesCorrectPattern() {
        val pattern = regex {
            ascii { letter() }
        }
        assertEquals("[a-zA-Z]", pattern.pattern)
    }

    @Test
    fun asciiBlockWithDigitGeneratesCorrectPattern() {
        val pattern = regex {
            ascii { digit() }
        }
        assertEquals("[0-9]", pattern.pattern)
        assertTrue(pattern.matches("5"))
        assertFalse(pattern.matches("a"))
    }

    @Test
    fun asciiBlockWithAlphanumericGeneratesCorrectPattern() {
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
    fun asciiBlockWithHexDigitGeneratesCorrectPattern() {
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
    fun asciiBlockInCharClassGeneratesCorrectPattern() {
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
    fun asciiBlockCombinedWithQuantifiers() {
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
    fun literalEscapesSpecialCharacters() {
        val pattern = regex {
            literal("a.b*c?")
        }
        assertTrue(pattern.matches("a.b*c?"))
        assertFalse(pattern.matches("aXbYc"))
    }

    @Test
    fun charMatchesSingleCharacter() {
        val pattern = regex {
            char('.')
        }
        assertTrue(pattern.matches("."))
        assertFalse(pattern.matches("a"))
    }

    @Test
    fun anyOfCreatesCharacterSetWithProperEscaping() {
        val pattern = regex {
            anyOf("abc")
        }
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("b"))
        assertFalse(pattern.matches("d"))
    }

    @Test
    fun anyOfEscapesSpecialCharactersInsideCharacterClass() {
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
    fun noneOfCreatesNegatedCharacterSet() {
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
    fun anyOfBlockCreatesCharacterClass() {
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
    fun noneOfBlockCreatesNegatedCharacterClass() {
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
    fun anyOfVarargCharCreatesCharacterSet() {
        val pattern = regex {
            line { anyOf('a', 'b', 'c') }
        }
        assertEquals("^[abc]$", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("b"))
        assertTrue(pattern.matches("c"))
        assertFalse(pattern.matches("d"))
    }

    @Test
    fun anyOfVarargCharEscapesSpecialCharacters() {
        val pattern = regex {
            line { anyOf('.', '*', '?', '[', ']') }
        }
        assertTrue(pattern.matches("."))
        assertTrue(pattern.matches("*"))
        assertTrue(pattern.matches("?"))
        assertTrue(pattern.matches("["))
        assertTrue(pattern.matches("]"))
        assertFalse(pattern.matches("a"))
    }

    @Test
    fun anyOfMultipleStringsCombinesAllCharacters() {
        val pattern = regex {
            line { anyOf("abc", "xyz") }
        }
        assertEquals("^[abcxyz]$", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("x"))
        assertTrue(pattern.matches("z"))
        assertFalse(pattern.matches("d"))
    }

    @Test
    fun noneOfVarargCharCreatesNegatedCharacterSet() {
        val pattern = regex {
            line { noneOf('a', 'b', 'c') }
        }
        assertEquals("^[^abc]$", pattern.pattern)
        assertTrue(pattern.matches("d"))
        assertTrue(pattern.matches("x"))
        assertFalse(pattern.matches("a"))
        assertFalse(pattern.matches("b"))
    }

    @Test
    fun noneOfMultipleStringsCombinesAllCharacters() {
        val pattern = regex {
            line { noneOf("abc", "xyz") }
        }
        assertEquals("^[^abcxyz]$", pattern.pattern)
        assertTrue(pattern.matches("d"))
        assertTrue(pattern.matches("m"))
        assertFalse(pattern.matches("a"))
        assertFalse(pattern.matches("z"))
    }

    @Test
    fun charClassCharsVarargCharAddsCharacters() {
        val pattern = regex {
            line {
                charClass {
                    chars('a', 'b', 'c')
                    chars('.', '-')
                }
            }
        }
        // Note: . doesn't need escaping inside character class, only - does
        assertEquals("^[abc.\\-]$", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("."))
        assertTrue(pattern.matches("-"))
        assertFalse(pattern.matches("d"))
    }

    @Test
    fun charClassCharsMultipleStringsAddsAllCharacters() {
        val pattern = regex {
            line {
                charClass {
                    chars("abc", "123")
                }
            }
        }
        assertEquals("^[abc123]$", pattern.pattern)
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("1"))
        assertFalse(pattern.matches("d"))
    }

    @Test
    fun rangeCreatesCharacterRange() {
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
    fun rangeValidationThrowsOnInvalidRange() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                range('z', 'a')
            }
        }
    }

    @Test
    fun notInRangeCreatesNegatedCharacterRange() {
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
    fun orCreatesAlternation() {
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
    fun eitherCreatesAlternationGroup() {
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
    fun eitherRequiresAtLeastOneAlternative() {
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
    fun charClassCombinesMultipleRangesAndCharacters() {
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
    fun negatedCharClassCreatesNegatedClass() {
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
    fun charClassWithPredefinedClasses() {
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
    fun charClassWithAsciiLowercaseShortcut() {
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
    fun charClassWithAsciiUppercaseShortcut() {
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
    fun charClassWithAsciiLetterShortcut() {
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
    fun charClassWithAsciiAlphanumericShortcut() {
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
    fun charClassWithHexDigitShortcut() {
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
    fun charClassShortcutsCanBeCombined() {
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
    fun captureCreatesCapturingGroup() {
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
    fun captureWithNameCreatesNamedCapturingGroup() {
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
    fun captureNameValidation() {
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
    fun groupCreatesNonCapturingGroup() {
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
    fun backReferenceByNumber() {
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
    fun backReferenceByName() {
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
    fun backReferenceValidation() {
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
    fun optionalMatchesZeroOrOneTimes() {
        val pattern = regex {
            literal("colo")
            optional { literal("u") }
            literal("r")
        }
        assertTrue(pattern.matches("color"))
        assertTrue(pattern.matches("colour"))
    }

    @Test
    fun zeroOrMoreMatchesZeroOrMoreTimes() {
        val pattern = regex {
            startOfLine()
            zeroOrMore { digit() }
            endOfLine()
        }
        assertTrue(pattern.matches(""))
        assertTrue(pattern.matches("123"))
    }

    @Test
    fun oneOrMoreMatchesOneOrMoreTimes() {
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
    fun repeatExactCount() {
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
    fun repeatValidation() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                repeat(-1) { digit() }
            }
        }
    }

    @Test
    fun repeatRange() {
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
    fun repeatRangeValidation() {
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
    fun atLeastMatchesMinimumOrMoreTimes() {
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
    fun atLeastValidation() {
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
    fun lazyQuantifiersPreferFewerMatches() {
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
    fun zeroOrMoreLazyGeneratesCorrectPattern() {
        val pattern = regex {
            zeroOrMoreLazy { anyChar() }
        }
        assertEquals(".*?", pattern.pattern)
    }

    @Test
    fun optionalLazyGeneratesCorrectPattern() {
        val pattern = regex {
            optionalLazy { digit() }
        }
        assertEquals("\\d??", pattern.pattern)
    }

    // =========================================================================
    // Lookaround Tests
    // =========================================================================

    @Test
    fun lookAheadPositiveAssertion() {
        val pattern = regex {
            oneOrMore { digit() }
            lookAhead { literal("px") }
        }
        assertTrue(pattern.containsMatchIn("100px"))
        assertFalse(pattern.containsMatchIn("100em"))
        assertEquals("100", pattern.find("100px")?.value)
    }

    @Test
    fun negativeLookAheadAssertion() {
        val pattern = regex {
            startOfLine()
            oneOrMore { digit() }
            negativeLookAhead { literal("px") }
            endOfLine()
        }
        assertFalse(pattern.matches("100px"))
        assertTrue(pattern.matches("100"))
    }

    // =========================================================================
    // Lookbehind Tests
    // =========================================================================

    @Test
    fun lookBehindPositiveAssertion() {
        val pattern = regex {
            lookBehind { literal("$") }
            oneOrMore { digit() }
        }
        assertTrue(pattern.containsMatchIn("\$100"))
        assertFalse(pattern.containsMatchIn("100"))
        assertEquals("100", pattern.find("\$100")?.value)
    }

    @Test
    fun negativeLookBehindAssertion() {
        val pattern = regex {
            negativeLookBehind { literal("$") }
            oneOrMore { digit() }
        }
        assertTrue(pattern.containsMatchIn("100"))
        // Note: "$100" still matches because "00" doesn't have $ before it
    }

    @Test
    fun lookBehindGeneratesCorrectPattern() {
        val pattern = regex {
            lookBehind { literal("@") }
            word()
        }
        assertTrue(pattern.containsMatchIn("user@domain"))
    }

    @Test
    fun negativeLookBehindGeneratesCorrectPattern() {
        val pattern = regex {
            negativeLookBehind { digit() }
            asciiLetter()
        }
        assertEquals("(?<!\\d)[a-zA-Z]", pattern.pattern)
    }

    // =========================================================================
    // Raw Pattern Tests
    // =========================================================================

    @Test
    fun appendRawAppendsPatternWithoutEscaping() {
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
    fun phoneNumberPattern() {
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
    fun urlPattern() {
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
    fun htmlTagPatternWithBackReference() {
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
    fun charClassWithNonDigit() {
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
    fun charClassWithNonWordChar() {
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
    fun charClassWithNonWhitespace() {
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
    fun singleAtomOptimizationDigit() {
        val pattern = regex {
            oneOrMore { digit() }
        }
        assertEquals("\\d+", pattern.pattern)
    }

    @Test
    fun singleAtomOptimizationAnyChar() {
        val pattern = regex {
            zeroOrMore { anyChar() }
        }
        assertEquals(".*", pattern.pattern)
    }

    @Test
    fun singleAtomOptimizationCharClass() {
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
    fun multiElementStillGetsGrouped() {
        // Pattern string test moved to JVM (uses \Q...\E escaping)
        // Here we just verify functional behavior
        val pattern = regex {
            oneOrMore {
                digit()
                literal("-")
            }
        }
        assertTrue(pattern.matches("1-2-3-"))
    }

    @Test
    fun alreadyGroupedPatternStaysOptimized() {
        // Pattern string test moved to JVM (uses \Q...\E escaping)
        // Here we just verify functional behavior
        val pattern = regex {
            oneOrMore {
                group {
                    literal("ab")
                }
            }
        }
        assertTrue(pattern.matches("abab"))
    }

    // =========================================================================
    // Patterns Extension Functions Tests
    // =========================================================================

    @Test
    fun emailPattern() {
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
    fun httpUrlPattern() {
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
    fun httpUrlWithCaptureExtractsGroups() {
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
    fun ipv4Pattern() {
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
    fun ipv4StrictValidatesOctetRange() {
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
    fun uuidPattern() {
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
    fun hexColorPattern() {
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
    fun isoDatePattern() {
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
    fun timePattern() {
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
    fun semverPattern() {
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
    fun integerPattern() {
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
    fun decimalPattern() {
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
    fun slugPattern() {
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
    fun phoneNumberPatternExtension() {
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
    fun usPhoneNumberPattern() {
        val pattern = regex {
            startOfLine()
            usPhoneNumber()
            endOfLine()
        }
        assertTrue(pattern.matches("(123) 456-7890"))
        assertTrue(pattern.matches("123-456-7890"))
        assertFalse(pattern.matches("1234567890"))
    }

}
