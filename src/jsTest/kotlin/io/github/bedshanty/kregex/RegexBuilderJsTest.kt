package io.github.bedshanty.kregex

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * JS-specific tests for RegexBuilder.
 *
 * Tests for features that have JS-specific behavior:
 * - Unicode script with ES2018 syntax (\p{Script=...})
 * - Unicode property matching (automatically enabled via Kotlin/JS 'u' flag)
 *
 * Note: Unicode Block is NOT supported in ES2018. Use XRegExp if needed.
 */
class RegexBuilderJsTest {

    // =========================================================================
    // Unicode Script Pattern Generation Tests (JS ES2018 syntax)
    // =========================================================================

    @Test
    @JsName("unicodeScript_generates_ES2018_syntax")
    fun unicodeScriptGeneratesES2018Syntax() {
        val pattern = regex { unicodeScript("Greek") }
        assertEquals("\\p{Script=Greek}", pattern.pattern)
    }

    @Test
    @JsName("unicodeScript_with_Han_script")
    fun unicodeScriptWithHanScript() {
        val pattern = regex { unicodeScript("Han") }
        assertEquals("\\p{Script=Han}", pattern.pattern)
    }

    @Test
    @JsName("unicodeScript_with_Hangul_script")
    fun unicodeScriptWithHangulScript() {
        val pattern = regex { unicodeScript("Hangul") }
        assertEquals("\\p{Script=Hangul}", pattern.pattern)
    }

    @Test
    @JsName("unicode_builder_script_generates_ES2018_syntax")
    fun unicodeBuilderScriptGeneratesES2018Syntax() {
        val pattern = regex {
            unicode { script("Greek") }
        }
        assertEquals("[\\p{Script=Greek}]", pattern.pattern)
    }

    @Test
    @JsName("unicode_builder_with_multiple_scripts")
    fun unicodeBuilderWithMultipleScripts() {
        val pattern = regex {
            unicode {
                script("Han")
                script("Hangul")
            }
        }
        assertEquals("[\\p{Script=Han}\\p{Script=Hangul}]", pattern.pattern)
    }

    // =========================================================================
    // Unicode Script Matching Tests (Kotlin/JS auto-includes 'u' flag)
    // =========================================================================

    @Test
    @JsName("unicodeScript_matches_Han_characters")
    fun unicodeScriptMatchesHanCharacters() {
        val pattern = regex {
            line { oneOrMore { unicodeScript("Han") } }
        }
        assertTrue(pattern.matches("中文"))
        assertTrue(pattern.matches("漢字"))
        assertTrue(pattern.matches("日本"))  // Pure Kanji
        assertFalse(pattern.matches("Hello"))
        assertFalse(pattern.matches("한글"))
        assertFalse(pattern.matches("ひらがな"))  // Hiragana, not Han
    }

    @Test
    @JsName("unicodeScript_matches_Hangul_characters")
    fun unicodeScriptMatchesHangulCharacters() {
        val pattern = regex {
            line { oneOrMore { unicodeScript("Hangul") } }
        }
        assertTrue(pattern.matches("한글"))
        assertTrue(pattern.matches("안녕하세요"))
        assertTrue(pattern.matches("ㄱㄴㄷ"))
        assertTrue(pattern.matches("ㅏㅓㅗ"))
        assertFalse(pattern.matches("Hello"))
        assertFalse(pattern.matches("漢字"))
    }

    @Test
    @JsName("unicodeScript_matches_Greek_characters")
    fun unicodeScriptMatchesGreekCharacters() {
        val pattern = regex {
            line { oneOrMore { unicodeScript("Greek") } }
        }
        assertTrue(pattern.matches("αβγδ"))
        assertTrue(pattern.matches("ΑΒΓΔ"))
        assertFalse(pattern.matches("abc"))
        assertFalse(pattern.matches("абвг"))
    }

    @Test
    @JsName("unicodeScript_matches_Cyrillic_characters")
    fun unicodeScriptMatchesCyrillicCharacters() {
        val pattern = regex {
            line { oneOrMore { unicodeScript("Cyrillic") } }
        }
        assertTrue(pattern.matches("абвгд"))
        assertTrue(pattern.matches("АБВГД"))
        assertTrue(pattern.matches("Привет"))
        assertFalse(pattern.matches("Hello"))
        assertFalse(pattern.matches("αβγδ"))
    }

    @Test
    @JsName("unicodeScript_matches_Hiragana_characters")
    fun unicodeScriptMatchesHiraganaCharacters() {
        val pattern = regex {
            line { oneOrMore { unicodeScript("Hiragana") } }
        }
        assertTrue(pattern.matches("ひらがな"))
        assertTrue(pattern.matches("あいうえお"))
        assertFalse(pattern.matches("カタカナ"))
        assertFalse(pattern.matches("漢字"))
    }

    @Test
    @JsName("unicodeScript_matches_Katakana_characters")
    fun unicodeScriptMatchesKatakanaCharacters() {
        val pattern = regex {
            line { oneOrMore { unicodeScript("Katakana") } }
        }
        assertTrue(pattern.matches("カタカナ"))
        assertTrue(pattern.matches("アイウエオ"))
        assertFalse(pattern.matches("ひらがな"))
        assertFalse(pattern.matches("漢字"))
    }

    // =========================================================================
    // Unicode Category Matching Tests
    // =========================================================================

    @Test
    @JsName("unicodeLetter_matches_various_scripts")
    fun unicodeLetterMatchesVariousScripts() {
        val pattern = regex {
            line { oneOrMore { unicodeLetter() } }
        }
        assertTrue(pattern.matches("Hello"))
        assertTrue(pattern.matches("한글"))
        assertTrue(pattern.matches("中文"))
        assertTrue(pattern.matches("αβγδ"))
        assertTrue(pattern.matches("абвгд"))
        assertTrue(pattern.matches("ひらがな"))
        assertFalse(pattern.matches("123"))
        assertFalse(pattern.matches("Hello123"))
    }

    @Test
    @JsName("unicodeNumber_matches_various_numerals")
    fun unicodeNumberMatchesVariousNumerals() {
        val pattern = regex {
            line { oneOrMore { unicodeNumber() } }
        }
        assertTrue(pattern.matches("123"))
        assertTrue(pattern.matches("١٢٣"))  // Arabic-Indic numerals
        assertFalse(pattern.matches("abc"))
    }

    @Test
    @JsName("unicode_builder_matches_letters_and_numbers")
    fun unicodeBuilderMatchesLettersAndNumbers() {
        val pattern = regex {
            line {
                oneOrMore {
                    charClass {
                        unicode {
                            letter()
                            number()
                        }
                    }
                }
            }
        }
        assertTrue(pattern.matches("Hello123"))
        assertTrue(pattern.matches("한글456"))
        assertTrue(pattern.matches("中文789"))
        assertFalse(pattern.matches("Hello_123"))
    }

    // =========================================================================
    // Combined Scripts Pattern Tests
    // =========================================================================

    @Test
    @JsName("multiple_scripts_in_charClass")
    fun multipleScriptsInCharClass() {
        val pattern = regex {
            line {
                oneOrMore {
                    charClass {
                        unicodeScript("Han")
                        unicodeScript("Hangul")
                        unicodeScript("Hiragana")
                        unicodeScript("Katakana")
                    }
                }
            }
        }
        assertTrue(pattern.matches("漢字한글ひらがなカタカナ"))
        assertTrue(pattern.matches("中文"))
        assertTrue(pattern.matches("안녕"))
        assertFalse(pattern.matches("Hello"))
    }

    @Test
    @JsName("script_with_ascii_in_charClass")
    fun scriptWithAsciiInCharClass() {
        val pattern = regex {
            line {
                oneOrMore {
                    charClass {
                        unicodeScript("Han")
                        asciiAlphanumeric()
                    }
                }
            }
        }
        assertTrue(pattern.matches("Hello中文123"))
        assertTrue(pattern.matches("漢字ABC"))
        assertFalse(pattern.matches("한글"))  // Hangul not included
    }

    // =========================================================================
    // Unicode Script with Quantifiers
    // =========================================================================

    @Test
    @JsName("unicodeScript_with_quantifiers")
    fun unicodeScriptWithQuantifiers() {
        val optional = regex {
            line {
                literal("Hello")
                optional { unicodeScript("Han") }
            }
        }
        assertTrue(optional.matches("Hello"))
        assertTrue(optional.matches("Hello中"))

        val repeat = regex {
            line { repeat(2) { unicodeScript("Hangul") } }
        }
        assertTrue(repeat.matches("한글"))
        assertFalse(repeat.matches("한"))
        assertFalse(repeat.matches("한글한"))
    }

    // =========================================================================
    // Unicode Script with Lookaround
    // =========================================================================

    @Test
    @JsName("unicodeScript_with_lookAhead")
    fun unicodeScriptWithLookAhead() {
        val pattern = regex {
            oneOrMore { unicodeScript("Han") }
            lookAhead { digit() }
        }
        assertTrue(pattern.containsMatchIn("漢字123"))
        assertFalse(pattern.containsMatchIn("漢字abc"))
    }

    @Test
    @JsName("unicodeScript_with_lookBehind")
    fun unicodeScriptWithLookBehind() {
        val pattern = regex {
            lookBehind { literal("#") }
            oneOrMore { unicodeScript("Hangul") }
        }
        assertTrue(pattern.containsMatchIn("#한글"))
        assertFalse(pattern.containsMatchIn("한글"))
    }
}
