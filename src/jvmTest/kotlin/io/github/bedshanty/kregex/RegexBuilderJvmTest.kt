package io.github.bedshanty.kregex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

/**
 * JVM-specific tests for RegexBuilder.
 *
 * Tests for features that only work on JVM:
 * - Input anchors (\A, \z, \Z)
 * - POSIX character classes
 * - Unicode script and block
 * - Possessive quantifiers
 * - Inline modifiers
 * - Atomic groups
 */
class RegexBuilderJvmTest {

    // =========================================================================
    // Input Anchors Tests (JVM Only)
    // =========================================================================

    @Test
    fun `startOfInput and endOfInput anchors`() {
        val pattern = regex {
            startOfInput()
            digit()
        }
        assertEquals("\\A\\d", pattern.pattern)
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

    @Test
    fun `endOfInputBeforeNewline generates correct pattern`() {
        val pattern = regex {
            digit()
            endOfInputBeforeNewline()
        }
        assertEquals("\\d\\Z", pattern.pattern)
    }

    @Test
    fun `alert generates correct pattern`() {
        val pattern = regex {
            alert()
        }
        assertEquals("\\a", pattern.pattern)
    }

    @Test
    fun `escape generates correct pattern`() {
        val pattern = regex {
            escape()
        }
        assertEquals("\\e", pattern.pattern)
    }

    // =========================================================================
    // Unicode Property Tests (JVM)
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

    @Test
    fun `single atom optimization - unicode property`() {
        val pattern = regex {
            oneOrMore { unicodeLetter() }
        }
        assertEquals("\\p{L}+", pattern.pattern)
    }

    // =========================================================================
    // Possessive Quantifiers Tests (JVM Only)
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
    // Inline Modifiers Tests (JVM Only)
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

    @Test
    fun `comments modifier pattern`() {
        val pattern = regex {
            comments {
                digit()
            }
        }
        assertEquals("(?x:\\d)", pattern.pattern)
    }

    // =========================================================================
    // Atomic Group Tests (JVM Only)
    // =========================================================================

    @Test
    fun `atomicGroup generates correct pattern`() {
        val pattern = regex {
            atomicGroup {
                oneOrMore { digit() }
            }
        }
        assertEquals("(?>\\d+)", pattern.pattern)
    }

    // =========================================================================
    // POSIX Character Class Tests (JVM Only)
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

    @Test
    fun `posixAscii matches ASCII characters`() {
        val pattern = regex {
            posixAscii()
        }
        assertEquals("\\p{ASCII}", pattern.pattern)
    }

    @Test
    fun `posixBlank matches blank characters`() {
        val pattern = regex {
            posixBlank()
        }
        assertEquals("\\p{Blank}", pattern.pattern)
    }

    @Test
    fun `posixCntrl matches control characters`() {
        val pattern = regex {
            posixCntrl()
        }
        assertEquals("\\p{Cntrl}", pattern.pattern)
    }

    @Test
    fun `posixGraph matches visible characters`() {
        val pattern = regex {
            posixGraph()
        }
        assertEquals("\\p{Graph}", pattern.pattern)
    }

    @Test
    fun `posixPrint matches printable characters`() {
        val pattern = regex {
            posixPrint()
        }
        assertEquals("\\p{Print}", pattern.pattern)
    }

    // =========================================================================
    // POSIX Builder Tests (JVM Only)
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

    // =========================================================================
    // Unicode Builder Tests (JVM Only)
    // =========================================================================

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

    // =========================================================================
    // Combined Builders Tests (JVM Only - uses Unicode script)
    // =========================================================================

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

    // =========================================================================
    // Korean (Hangul) Pattern Tests (JVM - verified with Unicode)
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
    // Hangul Builder Tests (JVM)
    // =========================================================================

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

    // =========================================================================
    // Password Pattern Tests (JVM)
    // =========================================================================

    @Test
    fun `password with all requirements`() {
        val pattern = regex {
            line {
                password(
                    minLength = 8,
                    maxLength = 20,
                    requireUppercase = true,
                    requireLowercase = true,
                    requireDigit = true,
                    requireSpecialChar = true
                )
            }
        }
        assertTrue(pattern.matches("Password1!"))
        assertTrue(pattern.matches("Str0ng@Pass"))
        assertTrue(pattern.matches("Aa1!Aa1!"))
        assertFalse(pattern.matches("password1!"))    // no uppercase
        assertFalse(pattern.matches("PASSWORD1!"))    // no lowercase
        assertFalse(pattern.matches("Password!!"))    // no digit
        assertFalse(pattern.matches("Password1"))     // no special
        assertFalse(pattern.matches("Pass1!"))        // too short
        assertFalse(pattern.matches("Password1!TooLongPassword123"))  // too long
    }

    @Test
    fun `password with custom special chars`() {
        val pattern = regex {
            line {
                password(
                    minLength = 8,
                    requireSpecialChar = true,
                    allowedSpecialChars = "!@#"
                )
            }
        }
        assertTrue(pattern.matches("password!"))
        assertTrue(pattern.matches("password@"))
        assertTrue(pattern.matches("password#"))
        assertFalse(pattern.matches("password\$"))  // $ not in allowedSpecialChars
        assertFalse(pattern.matches("password%"))   // % not in allowedSpecialChars
    }

    @Test
    fun `password rejects non-allowed characters`() {
        val pattern = regex {
            line { password(minLength = 8) }
        }
        assertTrue(pattern.matches("pass word"))      // space IS allowed (OWASP set includes space)
        assertFalse(pattern.matches("비밀번호12345678"))  // Korean not allowed
        assertFalse(pattern.matches("pass\tword"))    // tab not allowed
        assertFalse(pattern.matches("pässwörd"))      // non-ASCII letters not allowed
    }

    @Test
    fun `password with length only`() {
        val pattern = regex {
            line { password(minLength = 6) }
        }
        assertTrue(pattern.matches("abcdef"))
        assertTrue(pattern.matches("123456"))
        assertTrue(pattern.matches("abc123"))
        assertFalse(pattern.matches("abc"))  // too short
        assertFalse(pattern.matches("ab"))   // too short
    }

    @Test
    fun `password with no max length`() {
        val pattern = regex {
            line { password(minLength = 8, maxLength = null) }
        }
        assertTrue(pattern.matches("a".repeat(100)))  // long password OK
        assertTrue(pattern.matches("a".repeat(500)))  // very long password OK when maxLength = null
    }

    @Test
    fun `password with default max length`() {
        val pattern = regex {
            line { password(minLength = 8) }
        }
        assertTrue(pattern.matches("a".repeat(256)))   // exactly 256 chars OK (default maxLength)
        assertFalse(pattern.matches("a".repeat(257)))  // 257 chars exceeds default maxLength
    }

    @Test
    fun `password with minLength 1 works`() {
        val pattern = regex {
            line { password(minLength = 1, maxLength = 10) }
        }
        assertTrue(pattern.matches("a"))
        assertTrue(pattern.matches("!"))
        assertTrue(pattern.matches("1"))
        assertFalse(pattern.matches(""))  // empty not allowed
    }

    @Test
    fun `password with only uppercase requirement`() {
        val pattern = regex {
            line {
                password(
                    minLength = 8,
                    requireUppercase = true
                )
            }
        }
        assertTrue(pattern.matches("Password"))
        assertTrue(pattern.matches("AAAAAAAA"))
        assertFalse(pattern.matches("password"))  // no uppercase
        assertFalse(pattern.matches("12345678"))  // no uppercase
    }

    @Test
    fun `password with only lowercase requirement`() {
        val pattern = regex {
            line {
                password(
                    minLength = 8,
                    requireLowercase = true
                )
            }
        }
        assertTrue(pattern.matches("Password"))
        assertTrue(pattern.matches("aaaaaaaa"))
        assertFalse(pattern.matches("PASSWORD"))  // no lowercase
        assertFalse(pattern.matches("12345678"))  // no lowercase
    }

    @Test
    fun `password with only digit requirement`() {
        val pattern = regex {
            line {
                password(
                    minLength = 8,
                    requireDigit = true
                )
            }
        }
        assertTrue(pattern.matches("password1"))
        assertTrue(pattern.matches("12345678"))
        assertFalse(pattern.matches("password"))  // no digit
        assertFalse(pattern.matches("PASSWORD"))  // no digit
    }

    @Test
    fun `password with only special char requirement`() {
        val pattern = regex {
            line {
                password(
                    minLength = 8,
                    requireSpecialChar = true
                )
            }
        }
        assertTrue(pattern.matches("!@#\$%^&*"))   // only special chars
        assertTrue(pattern.matches("aaaaaa!a"))   // has special char
        assertFalse(pattern.matches("aaaaaaaa"))  // no special char
        assertFalse(pattern.matches("12345678"))  // no special char
    }

    @Test
    fun `password validates minLength parameter`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                password(minLength = 0)
            }
        }
        assertFailsWith<IllegalArgumentException> {
            regex {
                password(minLength = -1)
            }
        }
    }

    @Test
    fun `password validates maxLength parameter`() {
        assertFailsWith<IllegalArgumentException> {
            regex {
                password(minLength = 10, maxLength = 5)  // maxLength < minLength
            }
        }
    }

    // =========================================================================
    // Pattern String Tests (JVM uses \Q...\E escaping)
    // =========================================================================

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

}
