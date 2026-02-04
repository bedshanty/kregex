# Kregex

[English](README.md) | [한국어](README.ko.md)

[![CI](https://github.com/bedshanty/kregex/actions/workflows/ci.yml/badge.svg)](https://github.com/bedshanty/kregex/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.bedshanty/kregex)](https://central.sonatype.com/artifact/io.github.bedshanty/kregex)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Kregex**는 복잡하고 난해한 정규식 패턴을 직관적이고 구조화된 Kotlin 코드로 만들어주는 DSL 라이브러리입니다. — 정규식을 **쉽게 사용**하고, **쉽게 읽고**, **쉽게 유지보수**할 수 있게 해줍니다.

`^(?:[a-zA-Z0-9._%-]+)@(?:[a-zA-Z0-9-]+)\.(?:[a-zA-Z]{2,6})$` 처럼 암호 같은 정규식 대신, Kotlin 코드로 표현할 수 있습니다.

## 특징

- **100% 정규식 문법 지원** - 모든 표준 정규식 기능이 모든 플랫폼에서 동작
- **Kotlin Multiplatform** - JVM, JS, Native (macOS, iOS, Linux, Windows) 지원
- **사전 정의 패턴** - 이메일, 비밀번호, URL, 날짜 등 바로 사용 가능한 패턴 제공
- **타입 안전 DSL** - Kotlin의 DSL 마커를 활용한 컴파일 타임 안전성
- **가독성 높은 패턴** - 자체 문서화되는 정규식 구성
- **완전한 정규식 지원** - 앵커, 문자 클래스, 수량자, 전후방 탐색, 역참조
- **유니코드 지원** - 유니코드 속성, 스크립트, 블록
- **한글(Hangul) 지원** - 완성형 한글, 자모 매칭 내장 지원
- **Lazy & Possessive 수량자** - 세밀한 매칭 제어
- **패턴 디버깅** - 생성된 패턴 문자열 검사 가능
- **의존성 없음** - 순수 Kotlin, 외부 의존성 없음

## 플랫폼 지원

| 기능 | JVM | JS | Native |
|------|-----|----|----|
| Core DSL | ✅ | ✅ | ✅ |
| Unicode Categories (`\p{L}`) | ✅ | ✅ | ✅ |
| Unicode Script | ✅ `\p{IsHan}` | ✅ `\p{Script=Han}` | ❌ |
| Unicode Block (`\p{InBasicLatin}`) | ✅ | ❌ | ❌ |
| POSIX Classes (`\p{Alnum}`) | ✅ | ❌ | ❌ |
| Possessive Quantifiers | ✅ | ❌ | ❌ |

## 설치

### Gradle (Kotlin DSL)

```kotlin
implementation("io.github.bedshanty:kregex-jvm:0.4.0")
```

### Gradle (Groovy)

```groovy
implementation 'io.github.bedshanty:kregex-jvm:0.4.0'
```

### Maven

```xml
<dependency>
    <groupId>io.github.bedshanty</groupId>
    <artifactId>kregex-jvm</artifactId>
    <version>0.4.0</version>
</dependency>
```

### Kotlin Multiplatform

```kotlin
// commonMain
implementation("io.github.bedshanty:kregex:0.4.0")

// 또는 플랫폼별
implementation("io.github.bedshanty:kregex-js:0.4.0")               // JS
implementation("io.github.bedshanty:kregex-macosx64:0.4.0")         // macOS x64
implementation("io.github.bedshanty:kregex-macosarm64:0.4.0")       // macOS ARM64
implementation("io.github.bedshanty:kregex-iosx64:0.4.0")           // iOS x64
implementation("io.github.bedshanty:kregex-iosarm64:0.4.0")         // iOS ARM64
implementation("io.github.bedshanty:kregex-iossimulatorarm64:0.4.0") // iOS Simulator ARM64
implementation("io.github.bedshanty:kregex-linuxx64:0.4.0")         // Linux x64
implementation("io.github.bedshanty:kregex-linuxarm64:0.4.0")       // Linux ARM64
implementation("io.github.bedshanty:kregex-mingwx64:0.4.0")         // Windows x64
```

## 빠른 시작

```kotlin
val 숫자패턴 = regex {
    line {
        oneOrMore { digit() }
    }
}

println(숫자패턴.matches("12345")) // true
println(숫자패턴.matches("abc"))   // false
```

## 사용 예제

### 색상 코드

```kotlin
// #[0-9A-Fa-f]{6}
val hexColorPattern = regex {
    literal("#")
    repeat(6) { hexDigit() }
}

println(hexColorPattern.matches("#FF5733"))  // true
println(hexColorPattern.matches("#abc123"))  // true
println(hexColorPattern.matches("#GHIJKL"))  // false
```

### 휴대폰 번호

```kotlin
// ^01\d-\d{3,4}-\d{4}$
val 휴대폰번호 = regex {
    line {
        literal("01")
        digit()
        literal("-")
        repeat(3, 4) { digit() }
        literal("-")
        repeat(4) { digit() }
    }
}

println(휴대폰번호.matches("010-1234-5678"))  // true
println(휴대폰번호.matches("011-123-4567"))   // true
println(휴대폰번호.matches("010.1234.5678"))  // false
```

### 비밀번호 검증

```kotlin
// ^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{2,16}$
// 2-16자, 필수: 소문자, 대문자, 숫자, 특수문자 (!@#$%^&*)
val 비밀번호패턴 = regex {
    line {
        lookAhead { zeroOrMore { anyChar() }; asciiLowercase() }
        lookAhead { zeroOrMore { anyChar() }; asciiUppercase() }
        lookAhead { zeroOrMore { anyChar() }; asciiDigit() }
        lookAhead { zeroOrMore { anyChar() }; anyOf("!@#$%^&*") }
        repeat(2, 16) {
            anyOf {
                asciiAlphanumeric()
                chars("!@#$%^&*")
            }
        }
    }
}

println(비밀번호패턴.matches("aA1!"))        // true
println(비밀번호패턴.matches("MyP@ssw0rd"))  // true
println(비밀번호패턴.matches("abcd1234"))    // false (대문자, 특수문자 없음)
println(비밀번호패턴.matches("a"))           // false (너무 짧음)
```

### 캡처 그룹으로 URL 파싱

```kotlin
// ^(?<프로토콜>https|http)://(?<도메인>[\w.-]+)(?<포트>:\d+)?(?<경로>[\w/.-]*)?$
val URL패턴 = regex {
    line {
        captureAs("프로토콜") {
            either(
                { literal("https") },
                { literal("http") }
            )
        }
        literal("://")
        captureAs("도메인") {
            oneOrMore { anyOf { wordChar(); chars(".-") } }
        }
        optional {
            captureAs("포트") {
                literal(":")
                oneOrMore { digit() }
            }
        }
        optional {
            captureAs("경로") {
                zeroOrMore { anyOf { wordChar(); chars("/.-") } }
            }
        }
    }
}

val 매치결과 = URL패턴.find("https://example.com:8080/api/v1")!!
println(매치결과.groups["프로토콜"]?.value)  // https
println(매치결과.groups["도메인"]?.value)    // example.com
println(매치결과.groups["포트"]?.value)      // :8080
```

### HTML 태그 매칭 (역참조 사용)

```kotlin
// <(?<태그>\w+).*>.*?</\k<태그>>
val HTML태그 = regex {
    literal("<")
    captureAs("태그") { oneOrMore { wordChar() } }
    zeroOrMore { anyChar() }
    literal(">")
    zeroOrMoreLazy { anyChar() }
    literal("</")
    backReference("태그")
    literal(">")
}

println(HTML태그.containsMatchIn("<div>내용</div>"))    // true
println(HTML태그.containsMatchIn("<div>내용</span>"))   // false
```

## 한글 지원

Kregex는 한글 처리를 위한 전용 함수를 제공합니다.

### 한글 문자 클래스

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `hangulSyllable()` | `[가-힣]` | 완성형 한글 (가~힣) |
| `hangulJamo()` | `[ㄱ-ㅣ]` | 한글 자모 (ㄱ~ㅣ) |
| `hangulConsonant()` | `[ㄱ-ㅎ]` | 한글 자음만 (ㄱ~ㅎ) |
| `hangulVowel()` | `[ㅏ-ㅣ]` | 한글 모음만 (ㅏ~ㅣ) |

### 한글 텍스트 매칭

```kotlin
// 완성형 한글만 매칭
val 한글패턴 = regex {
    line {
        oneOrMore { hangulSyllable() }
    }
}

println(한글패턴.matches("안녕하세요"))  // true
println(한글패턴.matches("Hello"))       // false
println(한글패턴.matches("안녕123"))     // false
```

### 한글 자모 매칭

```kotlin
// 자음/모음 매칭 (ㅋㅋㅋ, ㅎㅎㅎ 등)
val 자모패턴 = regex {
    line {
        oneOrMore { hangulJamo() }
    }
}

println(자모패턴.matches("ㅋㅋㅋ"))  // true
println(자모패턴.matches("ㅎㅎㅎ"))  // true
println(자모패턴.matches("ㅠㅠ"))    // true
```

### 한글과 다른 문자 조합

```kotlin
// 한글 + 숫자 조합 (hangul 블록 사용)
val 혼합패턴 = regex {
    oneOrMore {
        anyOf {
            hangul {
                syllable()  // 가-힣
                jamo()      // ㄱ-ㅣ
            }
            digit()
        }
    }
}

println(혼합패턴.matches("가격1000원"))   // true
println(혼합패턴.matches("ㅋㅋ123ㅎㅎ"))  // true
// 생성된 패턴: [가-힣ㄱ-ㅣ\d]+
```

### hangul 블록

`hangul { }` 블록으로 한글 범위를 조합하세요:

```kotlin
regex {
    hangul {
        syllable()    // 가-힣 (완성형)
        consonant()   // ㄱ-ㅎ (자음)
    }
}
// 결과: [가-힣ㄱ-ㅎ]
```

`hangul { }` 내에서 사용 가능한 메서드:
- `syllable()` - 완성형 한글 (가-힣)
- `jamo()` - 모든 자모 (ㄱ-ㅣ)
- `consonant()` - 자음만 (ㄱ-ㅎ)
- `vowel()` - 모음만 (ㅏ-ㅣ)

## API 레퍼런스

### 진입점

| 함수 | 설명 |
|------|------|
| `regex { }` | DSL로 `Regex` 객체 생성 |
| `regex(options) { }` | 옵션과 함께 `Regex` 생성 (예: `IGNORE_CASE`) |

생성된 패턴 문자열은 `Regex.pattern` 속성으로 확인할 수 있습니다.

### 앵커 & 경계

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `startOfLine()` | `^` | 줄의 시작 |
| `endOfLine()` | `$` | 줄의 끝 |
| `startOfInput()` | `\A` | 입력의 시작 |
| `endOfInput()` | `\z` | 입력의 끝 |
| `wordBoundary()` | `\b` | 단어 경계 |
| `nonWordBoundary()` | `\B` | 비단어 경계 |
| `line { }` | `^...$` | 줄 시작/끝 앵커로 감싸기 |
| `input { }` | `\A...\z` | 입력 시작/끝 앵커로 감싸기 |

#### line & input 블록

앵커를 수동으로 추가하는 대신 편의 블록을 사용하세요:

```kotlin
// 기존 방식
regex {
    startOfLine()
    oneOrMore { digit() }
    endOfLine()
}

// 새로운 방식
regex {
    line { oneOrMore { digit() } }
}
// 결과: ^\d+$

regex {
    input { oneOrMore { digit() } }
}
// 결과: \A\d+\z
```

### 문자 클래스

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `anyChar()` | `.` | 모든 문자 (줄바꿈 제외) |
| `digit()` | `\d` | 숫자 (유니코드 숫자 포함 가능) |
| `asciiDigit()` | `[0-9]` | ASCII 숫자만 (0-9) |
| `nonDigit()` | `\D` | 숫자 제외 |
| `whitespace()` | `\s` | 공백 문자 |
| `nonWhitespace()` | `\S` | 공백 문자 제외 |
| `wordChar()` | `\w` | 단어 문자 (a-z, A-Z, 0-9, _) |
| `nonWordChar()` | `\W` | 단어 문자 제외 |
| `tab()` | `\t` | 탭 문자 |
| `newline()` | `\n` | 줄바꿈 문자 |
| `carriageReturn()` | `\r` | 캐리지 리턴 |
| `formFeed()` | `\f` | 폼 피드 |
| `alert()` | `\a` | 경고/벨 문자 |
| `escape()` | `\e` | 이스케이프 문자 |

> **참고**: `digit()`는 `\d`를 사용하며 정규식 플래그에 따라 유니코드 숫자도 매칭할 수 있습니다. 정확히 ASCII 숫자(0-9)만 매칭하려면 `asciiDigit()`를 사용하세요.

### 유니코드 지원

| 메서드 | 패턴 | 설명 | 플랫폼 |
|--------|------|------|--------|
| `unicodeProperty("L")` | `\p{L}` | 유니코드 속성 | 전체 |
| `notUnicodeProperty("L")` | `\P{L}` | 부정 유니코드 속성 | 전체 |
| `unicodeScript("Han")` | `\p{IsHan}` (JVM) / `\p{Script=Han}` (JS) | 유니코드 스크립트 | JVM, JS |
| `unicodeBlock("BasicLatin")` | `\p{InBasicLatin}` | 유니코드 블록 | JVM 전용 |
| `unicodeLetter()` | `\p{L}` | 모든 유니코드 문자 | 전체 |
| `unicodeNumber()` | `\p{N}` | 모든 유니코드 숫자 | 전체 |

> **참고**: JS에서 'u' 플래그는 Kotlin/JS가 자동으로 포함하므로 유니코드 기능이 바로 동작합니다.

#### unicode 블록

`unicode { }` 블록으로 유니코드 클래스를 조합하세요:

```kotlin
regex {
    unicode {
        letter()       // \p{L}
        number()       // \p{N}
        script("Han")  // \p{IsHan} (JVM) / \p{Script=Han} (JS)
    }
}
// 결과: [\p{L}\p{N}\p{IsHan}] (JVM) / [\p{L}\p{N}\p{Script=Han}] (JS)
```

`unicode { }` 내에서 사용 가능한 메서드:
- `property(name)` - 유니코드 속성 (`\p{...}`)
- `notProperty(name)` - 부정 속성 (`\P{...}`)
- `script(name)` - 유니코드 스크립트 - **JVM/JS 전용**
- `block(name)` - 유니코드 블록 (`\p{In...}`) - **JVM 전용**
- `letter()`, `uppercaseLetter()`, `lowercaseLetter()`
- `number()`, `punctuation()`, `symbol()`

### POSIX 문자 클래스 (JVM 전용)

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `posixAlnum()` | `\p{Alnum}` | 영숫자 `[a-zA-Z0-9]` |
| `posixAlpha()` | `\p{Alpha}` | 알파벳 `[a-zA-Z]` |
| `posixAscii()` | `\p{ASCII}` | ASCII 문자 `[\x00-\x7F]` |
| `posixBlank()` | `\p{Blank}` | 공백과 탭 `[ \t]` |
| `posixCntrl()` | `\p{Cntrl}` | 제어 문자 |
| `posixDigit()` | `\p{Digit}` | 숫자 `[0-9]` |
| `posixGraph()` | `\p{Graph}` | 보이는 문자 (공백 제외) |
| `posixLower()` | `\p{Lower}` | 소문자 `[a-z]` |
| `posixPrint()` | `\p{Print}` | 인쇄 가능한 문자 |
| `posixPunct()` | `\p{Punct}` | 구두점 |
| `posixSpace()` | `\p{Space}` | 공백 문자 `[ \t\n\r\f\v]` |
| `posixUpper()` | `\p{Upper}` | 대문자 `[A-Z]` |
| `posixXDigit()` | `\p{XDigit}` | 16진수 숫자 `[0-9a-fA-F]` |

> **참고**: POSIX 클래스는 JVM에서만 지원됩니다.

#### posix 블록

`posix { }` 블록으로 POSIX 클래스를 조합하세요:

```kotlin
regex {
    posix {
        alnum()    // \p{Alnum}
        punct()    // \p{Punct}
    }
}
// 결과: [\p{Alnum}\p{Punct}]
```

`posix { }` 내에서 사용 가능한 메서드:
- `alnum()`, `alpha()`, `ascii()`, `blank()`, `cntrl()`
- `digit()`, `graph()`, `lower()`, `print()`, `punct()`
- `space()`, `upper()`, `xdigit()`

### 리터럴 & 문자 집합

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `literal("text")` | (이스케이프됨) | 텍스트 그대로 매칭 |
| `char('x')` | (이스케이프됨) | 단일 문자 매칭 |
| `anyOf("abc")` | `[abc]` | 이 문자들 중 하나 |
| `anyOf { }` | `[...]` | 문자 클래스 빌더 (`charClass`의 별칭) |
| `noneOf("abc")` | `[^abc]` | 이 문자들 제외 |
| `noneOf { }` | `[^...]` | 부정 문자 클래스 빌더 (`negatedCharClass`의 별칭) |
| `range('a', 'z')` | `[a-z]` | 문자 범위 |
| `appendRaw("pattern")` | (그대로) | Raw 패턴 (이스케이프 없음) |

### 문자 클래스 빌더

```kotlin
anyOf {
    range('a', 'z')
    range('A', 'Z')
    chars("_-")
    digit()
}
// 결과: [a-zA-Z_\-\d]

noneOf {
    range('0', '9')
}
// 결과: [^0-9]
```

### ASCII 문자 범위

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `asciiLowercase()` | `[a-z]` | ASCII 소문자 |
| `asciiUppercase()` | `[A-Z]` | ASCII 대문자 |
| `asciiDigit()` | `[0-9]` | ASCII 숫자 |
| `asciiLetter()` | `[a-zA-Z]` | ASCII 문자 |
| `asciiAlphanumeric()` | `[a-zA-Z0-9]` | ASCII 영숫자 |
| `hexDigit()` | `[0-9a-fA-F]` | 16진수 숫자 |

#### ascii 블록

`ascii { }` 블록으로 ASCII 범위를 조합하세요:

```kotlin
regex {
    ascii {
        lower()        // a-z
        upper()        // A-Z
        digit()        // 0-9
    }
}
// 결과: [a-zA-Z0-9]

regex {
    ascii { hexDigit() }
}
// 결과: [0-9a-fA-F]
```

`ascii { }` 내에서 사용 가능한 메서드:
- `lower()` - ASCII 소문자 (a-z)
- `upper()` - ASCII 대문자 (A-Z)
- `digit()` - ASCII 숫자 (0-9)
- `letter()` - ASCII 문자 (a-zA-Z)
- `alphanumeric()` - ASCII 영숫자 (a-zA-Z0-9)
- `hexDigit()` - 16진수 숫자 (0-9a-fA-F)

#### ASCII 범위 단축 메서드

charClass 내에서 사용할 수 있는 ASCII 범위 단축 메서드:

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `asciiLowercase()` | `a-z` | ASCII 소문자 |
| `asciiUppercase()` | `A-Z` | ASCII 대문자 |
| `asciiDigit()` | `0-9` | ASCII 숫자 |
| `asciiLetter()` | `a-zA-Z` | 모든 ASCII 문자 |
| `asciiAlphanumeric()` | `a-zA-Z0-9` | ASCII 영숫자 |
| `hexDigit()` | `0-9a-fA-F` | 16진수 숫자 |

```kotlin
// 단축 메서드 직접 사용
anyOf { hexDigit() }
// 결과: [0-9a-fA-F]

// charClass 내에서 ascii 블록 사용
anyOf {
    ascii {
        lower()
        digit()
    }
    chars("_")
}
// 결과: [a-z0-9_]
```

### 그룹 & 캡처

| 메서드                 | 패턴 | 설명 |
|---------------------|------|------|
| `capture { }`       | `(...)` | 캡처 그룹 |
| `captureAs("이름") { }` | `(?<이름>...)` | 명명된 캡처 그룹 |
| `group { }`         | `(?:...)` | 비캡처 그룹 |
| `atomicGroup { }`   | `(?>...)` | 원자적 그룹 (백트래킹 없음) |

### 역참조

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `backReference(1)` | `\1` | 그룹 1 참조 |
| `backReference("이름")` | `\k<이름>` | 명명된 그룹 참조 |

### 수량자

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `optional { }` | `(?:...)?` | 0 또는 1회 |
| `zeroOrMore { }` | `(?:...)*` | 0회 이상 |
| `oneOrMore { }` | `(?:...)+` | 1회 이상 |
| `repeat(n) { }` | `(?:...){n}` | 정확히 n회 |
| `repeat(min, max) { }` | `(?:...){min,max}` | min~max회 |
| `atLeast(n) { }` | `(?:...){n,}` | 최소 n회 |

### Lazy 수량자

최소한의 문자만 매칭:

```kotlin
oneOrMoreLazy { anyChar() }     // (?:.)+?
zeroOrMoreLazy { anyChar() }    // (?:.)*?
optionalLazy { digit() }        // (?:\d)??
```

### Possessive 수량자 (JVM 전용)

백트래킹하지 않음:

```kotlin
oneOrMorePossessive { digit() }     // (?:\d)++
zeroOrMorePossessive { anyChar() }  // (?:.)*+
optionalPossessive { digit() }      // (?:\d)?+
```

> **참고**: Possessive 수량자는 JVM에서만 지원됩니다.

### 수량자 모드 매개변수

모드를 매개변수로 지정할 수도 있습니다:

```kotlin
oneOrMore(QuantifierMode.LAZY) { digit() }
repeat(2, 5, QuantifierMode.POSSESSIVE) { wordChar() }
```

### 전후방 탐색

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `lookAhead { }` | `(?=...)` | 긍정 전방 탐색 |
| `negativeLookAhead { }` | `(?!...)` | 부정 전방 탐색 |
| `lookBehind { }` | `(?<=...)` | 긍정 후방 탐색 |
| `negativeLookBehind { }` | `(?<!...)` | 부정 후방 탐색 |

### 인라인 수정자

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `caseInsensitive { }` | `(?i:...)` | 대소문자 무시 매칭 |
| `multiline { }` | `(?m:...)` | 멀티라인 모드 |
| `dotAll { }` | `(?s:...)` | 점(.)이 줄바꿈도 매칭 |
| `comments { }` | `(?x:...)` | 주석 모드 |

### 선택

```kotlin
// or() 사용
regex {
    literal("고양이")
    or()
    literal("강아지")
}

// either() 사용
regex {
    either(
        { literal("고양이") },
        { literal("강아지") },
        { literal("새") }
    )
}
```

## 사전 정의 패턴

Kregex는 자주 사용되는 패턴을 미리 정의해 제공합니다. `RegexBuilder`의 확장 함수로, 정규식 정의 내에서 바로 사용할 수 있습니다.

### 이메일

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `email()` | 기본 이메일 패턴 | `user@example.com` |

```kotlin
val pattern = regex {
    line { email() }
}
println(pattern.matches("user@example.com"))  // true
```

### 비밀번호

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `password(...)` | 설정 가능한 비밀번호 검증 | `Password1!` |

**`password()` 파라미터:**

| 파라미터 | 타입 | 기본값 | 설명 |
|---------|------|--------|------|
| `minLength` | `Int` | `8` | 최소 길이 |
| `maxLength` | `Int?` | `256` | 최대 길이 (null = 무제한) |
| `requireUppercase` | `Boolean` | `false` | 대문자 필수 여부 |
| `requireLowercase` | `Boolean` | `false` | 소문자 필수 여부 |
| `requireDigit` | `Boolean` | `false` | 숫자 필수 여부 |
| `requireSpecialChar` | `Boolean` | `false` | 특수문자 필수 여부 |
| `allowedSpecialChars` | `String` | OWASP 권장 세트 | 허용할 특수문자 |

**기본 특수문자 (OWASP 권장)**: ` !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~` (공백 포함)

**허용 문자**: ASCII 영문자 (a-z, A-Z), 숫자 (0-9), `allowedSpecialChars`에 명시된 특수문자만 허용됩니다. 한글, 이모지 등은 거부됩니다.

```kotlin
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
println(pattern.matches("Password1!"))  // true
println(pattern.matches("password"))    // false (요구사항 미충족)
```

### URL

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `httpUrl()` | HTTP/HTTPS URL | `https://example.com/path` |
| `httpUrlWithCapture()` | 캡처 그룹이 있는 URL (protocol, domain, port, path) | `https://example.com:8080/api` |

```kotlin
val pattern = regex {
    line { httpUrl() }
}
println(pattern.matches("https://example.com/path"))  // true
```

### IP 주소

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `ipv4()` | IPv4 주소 (형식만 검증) | `192.168.1.1` |
| `ipv4Strict()` | IPv4 주소 (0-255 범위 검증) | `192.168.1.1` |
| `ipv6()` | IPv6 주소 (8개 그룹) | `2001:0db8:85a3:0000:0000:8a2e:0370:7334` |

```kotlin
val pattern = regex {
    line { ipv4Strict() }
}
println(pattern.matches("192.168.1.1"))  // true
println(pattern.matches("256.1.1.1"))    // false (잘못된 옥텟)
```

### 전화번호

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `phoneNumber()` | 유연한 전화번호 형식 | `+1-123-456-7890`, `(123) 456-7890` |
| `usPhoneNumber()` | 미국 전화번호 형식 | `(123) 456-7890`, `123-456-7890` |

### 날짜 & 시간

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `isoDate()` | ISO 8601 날짜 | `2026-01-15` |
| `time()` | 시간 (HH:MM 또는 HH:MM:SS) | `14:30`, `14:30:59` |
| `isoDateTime()` | ISO 8601 날짜시간 | `2026-01-15T14:30:00Z` |

```kotlin
val pattern = regex {
    line { isoDateTime() }
}
println(pattern.matches("2026-01-15T14:30:00Z"))       // true
println(pattern.matches("2026-01-15T14:30:00+09:00"))  // true
```

### 식별자

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `uuid()` | UUID 형식 | `550e8400-e29b-41d4-a716-446655440000` |
| `hexColor()` | 16진수 색상 (#RGB, #RRGGBB) | `#fff`, `#FF5733` |
| `slug()` | URL 친화적 식별자 | `my-blog-post-123` |
| `semver()` | 시맨틱 버전 | `1.0.0`, `2.1.3-alpha.1` |

```kotlin
val pattern = regex {
    line { hexColor() }
}
println(pattern.matches("#FF5733"))  // true
println(pattern.matches("#fff"))     // true
```

### 숫자 & 텍스트

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `word()` | 하나 이상의 단어 문자 | `Hello` |
| `integer()` | 부호 포함 정수 | `123`, `-456`, `+789` |
| `decimal()` | 소수 | `123.456`, `-0.5`, `.25` |
| `quotedString()` | 큰따옴표 문자열 | `"hello"` |
| `singleQuotedString()` | 작은따옴표 문자열 | `'hello'` |

```kotlin
val pattern = regex {
    line { decimal() }
}
println(pattern.matches("123.456"))  // true
println(pattern.matches("-0.5"))     // true
println(pattern.matches(".25"))      // true
```

## 패턴 디버깅

`Regex.pattern` 속성으로 생성된 패턴을 확인할 수 있습니다:

```kotlin
val 정규식 = regex {
    line {
        oneOrMore { hangulSyllable() }
        literal("님")
    }
}

println("생성된 패턴: ${정규식.pattern}")
// 출력: ^[가-힣]+\Q님\E$
```

## 입력 검증

라이브러리는 입력을 검증하고 잘못된 매개변수에 대해 `IllegalArgumentException`을 발생시킵니다:

```kotlin
range('z', 'a')           // 오류: 범위 시작은 끝보다 작거나 같아야 함
repeat(-1) { digit() }    // 오류: 반복 횟수는 음수가 될 수 없음
captureAs("123") { }        // 오류: 이름은 문자로 시작해야 함
backReference(0)          // 오류: 그룹 번호는 1 이상이어야 함
```

## 비교

### 기존 정규식

```kotlin
val pattern = Regex("^[가-힣]{2,5}$")
```

### Kregex DSL

```kotlin
val pattern = regex {
    line { repeat(2, 5) { hangulSyllable() } }
}
```

## 라이선스

```
Copyright 2026 bedshanty

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 기여하기

기여를 환영합니다! Pull Request를 자유롭게 제출해 주세요.

## 감사의 말

다른 언어의 유사한 DSL 라이브러리들과 Kotlin에서 더 읽기 쉬운 정규식 패턴의 필요성에서 영감을 받았습니다.   
반복적인 코드 생성과 문서화 과정에서 AI 도구의 도움을 받았습니다.
