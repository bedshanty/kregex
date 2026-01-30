# Kregex

[English](README.md) | [한국어](README.ko.md)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.21-blue.svg?logo=kotlin)](http://kotlinlang.org)

**Kregex**는 정규 표현식을 읽기 쉽고, 타입 안전(Type-safe)하게 작성할 수 있는 Kotlin DSL 라이브러리입니다.

`^(?:[a-zA-Z0-9._%-]+)@(?:[a-zA-Z0-9-]+)\.(?:[a-zA-Z]{2,6})$` 처럼 암호 같은 정규식 대신, Kotlin 코드로 표현할 수 있습니다.

## 특징

- **타입 안전 DSL** - Kotlin의 DSL 마커를 활용한 컴파일 타임 안전성
- **가독성 높은 패턴** - 자체 문서화되는 정규식 구성
- **완전한 정규식 지원** - 앵커, 문자 클래스, 수량자, 전후방 탐색, 역참조
- **유니코드 지원** - 유니코드 속성, 스크립트, 블록
- **한글(Hangul) 지원** - 완성형 한글, 자모 매칭 내장 지원
- **Lazy & Possessive 수량자** - 세밀한 매칭 제어
- **패턴 디버깅** - 생성된 패턴 문자열 검사 가능
- **의존성 없음** - 순수 Kotlin, 외부 의존성 없음

## 설치

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.bedshanty:kregex:0.1.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'io.github.bedshanty:kregex:0.1.0'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.bedshanty</groupId>
    <artifactId>kregex</artifactId>
    <version>0.1.0</version>
</dependency>
```

## 빠른 시작

```kotlin
val 숫자패턴 = regex {
    startOfLine()
    oneOrMore { digit() }
    endOfLine()
}

println(숫자패턴.matches("12345")) // true
println(숫자패턴.matches("abc"))   // false
```

## 한글 지원

Kregex는 한글 처리를 위한 전용 함수를 제공합니다.

### 한글 문자 클래스

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `hangul()` | `[가-힣]` | 완성형 한글 (가~힣) |
| `hangulJamo()` | `[ㄱ-ㅣ]` | 한글 자모 (ㄱ~ㅣ) |
| `hangulConsonant()` | `[ㄱ-ㅎ]` | 한글 자음만 (ㄱ~ㅎ) |
| `hangulVowel()` | `[ㅏ-ㅣ]` | 한글 모음만 (ㅏ~ㅣ) |

### 한글 텍스트 매칭

```kotlin
// 완성형 한글만 매칭
val 한글패턴 = regex {
    startOfLine()
    oneOrMore { hangul() }
    endOfLine()
}

println(한글패턴.matches("안녕하세요"))  // true
println(한글패턴.matches("Hello"))       // false
println(한글패턴.matches("안녕123"))     // false
```

### 한글 자모 매칭

```kotlin
// 자음/모음 매칭 (ㅋㅋㅋ, ㅎㅎㅎ 등)
val 자모패턴 = regex {
    startOfLine()
    oneOrMore { hangulJamo() }
    endOfLine()
}

println(자모패턴.matches("ㅋㅋㅋ"))  // true
println(자모패턴.matches("ㅎㅎㅎ"))  // true
println(자모패턴.matches("ㅠㅠ"))    // true
```

### 한글과 다른 문자 조합

```kotlin
// 한글 + 숫자 조합
val 혼합패턴 = regex {
    oneOrMore {
        charClass {
            hangul()
            hangulJamo()
            digit()
        }
    }
}

println(혼합패턴.matches("가격1000원"))   // true
println(혼합패턴.matches("ㅋㅋ123ㅎㅎ"))  // true
// 생성된 패턴: [가-힣ㄱ-ㅣ\d]+
```

## 사용 예제

### 휴대폰 번호

```kotlin
val 휴대폰번호 = regex {
    startOfLine()
    literal("01")
    digit()
    literal("-")
    repeat(3, 4) { digit() }
    literal("-")
    repeat(4) { digit() }
    endOfLine()
}

println(휴대폰번호.matches("010-1234-5678"))  // true
println(휴대폰번호.matches("011-123-4567"))   // true
println(휴대폰번호.matches("010.1234.5678"))  // false
```

### 주민등록번호 형식 검증

```kotlin
val 주민번호형식 = regex {
    startOfLine()
    // 생년월일 (YYMMDD)
    repeat(2) { digit() }  // 년도
    either(
        { literal("0"); range('1', '9') },  // 01-09월
        { literal("1"); range('0', '2') }   // 10-12월
    )
    either(
        { literal("0"); range('1', '9') },  // 01-09일
        { range('1', '2'); digit() },       // 10-29일
        { literal("3"); range('0', '1') }   // 30-31일
    )
    literal("-")
    // 뒷자리 (성별 + 지역코드 + 일련번호 + 검증번호)
    range('1', '4')  // 성별 (1,2: 1900년대, 3,4: 2000년대)
    repeat(6) { digit() }
    endOfLine()
}

println(주민번호형식.matches("901231-1234567"))  // true
println(주민번호형식.matches("000101-3234567"))  // true
println(주민번호형식.matches("901331-1234567"))  // false (13월 없음)
```

### 사업자등록번호

```kotlin
val 사업자번호 = regex {
    startOfLine()
    repeat(3) { digit() }
    literal("-")
    repeat(2) { digit() }
    literal("-")
    repeat(5) { digit() }
    endOfLine()
}

println(사업자번호.matches("123-45-67890"))  // true
```

### 금액 표시 (원화)

```kotlin
val 원화금액 = regex {
    startOfLine()
    optional { literal("₩") }
    oneOrMore { digit() }
    zeroOrMore {
        literal(",")
        repeat(3) { digit() }
    }
    optional { literal("원") }
    endOfLine()
}

println(원화금액.matches("1,000,000"))    // true
println(원화금액.matches("₩50,000원"))    // true
println(원화금액.matches("10000"))        // true
```

### 캡처 그룹으로 URL 파싱

```kotlin
val URL패턴 = regex {
    startOfLine()
    capture("프로토콜") {
        either(
            { literal("https") },
            { literal("http") }
        )
    }
    literal("://")
    capture("도메인") {
        oneOrMore { charClass { wordChar(); chars(".-") } }
    }
    optional {
        capture("포트") {
            literal(":")
            oneOrMore { digit() }
        }
    }
    optional {
        capture("경로") {
            zeroOrMore { charClass { wordChar(); chars("/.-") } }
        }
    }
    endOfLine()
}

val 매치결과 = URL패턴.find("https://example.com:8080/api/v1")!!
println(매치결과.groups["프로토콜"]?.value)  // https
println(매치결과.groups["도메인"]?.value)    // example.com
println(매치결과.groups["포트"]?.value)      // :8080
```

### HTML 태그 매칭 (역참조 사용)

```kotlin
val HTML태그 = regex {
    literal("<")
    capture("태그") { oneOrMore { wordChar() } }
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

### 전후방 탐색

```kotlin
// "원" 앞의 숫자만 매칭
val 금액숫자 = regex {
    oneOrMore { digit() }
    lookAhead { literal("원") }
}

val 텍스트 = "가격은 5000원입니다"
val 결과 = 금액숫자.find(텍스트)
println(결과?.value)  // 5000

// "$" 뒤가 아닌 숫자만 매칭
val 원화숫자 = regex {
    negativeLookBehind { literal("$") }
    oneOrMore { digit() }
}
```

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

### 문자 클래스

| 메서드 | 패턴 | 설명                      |
|--------|------|-------------------------|
| `anyChar()` | `.` | 모든 문자 (줄바꿈 제외)          |
| `digit()` | `\d` | 숫자 (0-9)                |
| `nonDigit()` | `\D` | 숫자 제외                   |
| `whitespace()` | `\s` | 공백 문자                   |
| `nonWhitespace()` | `\S` | 공백 문자 제외                |
| `wordChar()` | `\w` | 단어 문자 (a-z, A-Z, 0-9, _) |
| `nonWordChar()` | `\W` | 단어 문자 제외                |
| `tab()` | `\t` | 탭 문자                    |
| `newline()` | `\n` | 줄바꿈 문자                  |

### 유니코드 지원

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `unicodeProperty("L")` | `\p{L}` | 유니코드 속성 |
| `notUnicodeProperty("L")` | `\P{L}` | 부정 유니코드 속성 |
| `unicodeScript("Hangul")` | `\p{IsHangul}` | 유니코드 스크립트 |
| `unicodeLetter()` | `\p{L}` | 모든 유니코드 문자 |
| `unicodeNumber()` | `\p{N}` | 모든 유니코드 숫자 |

### 리터럴 & 문자 집합

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `literal("text")` | (이스케이프됨) | 텍스트 그대로 매칭 |
| `char('x')` | (이스케이프됨) | 단일 문자 매칭 |
| `anyOf("abc")` | `[abc]` | 이 문자들 중 하나 |
| `noneOf("abc")` | `[^abc]` | 이 문자들 제외 |
| `range('a', 'z')` | `[a-z]` | 문자 범위 |
| `raw("pattern")` | (그대로) | Raw 패턴 (이스케이프 없음) |

### 그룹 & 캡처

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `capture { }` | `(...)` | 캡처 그룹 |
| `capture("이름") { }` | `(?<이름>...)` | 명명된 캡처 그룹 |
| `group { }` | `(?:...)` | 비캡처 그룹 |
| `atomicGroup { }` | `(?>...)` | 원자적 그룹 (백트래킹 없음) |

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

### Possessive 수량자

백트래킹하지 않음:

```kotlin
oneOrMorePossessive { digit() }     // (?:\d)++
zeroOrMorePossessive { anyChar() }  // (?:.)*+
```

### 전후방 탐색

| 메서드 | 패턴 | 설명 |
|--------|------|------|
| `lookAhead { }` | `(?=...)` | 긍정 전방 탐색 |
| `negativeLookAhead { }` | `(?!...)` | 부정 전방 탐색 |
| `lookBehind { }` | `(?<=...)` | 긍정 후방 탐색 |
| `negativeLookBehind { }` | `(?<!...)` | 부정 후방 탐색 |

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

### 이메일 & URL

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `email()` | 기본 이메일 패턴 | `user@example.com` |
| `httpUrl()` | HTTP/HTTPS URL | `https://example.com/path` |
| `httpUrlWithCapture()` | 캡처 그룹이 있는 URL (protocol, domain, port, path) | `https://example.com:8080/api` |

```kotlin
val pattern = regex {
    startOfLine()
    email()
    endOfLine()
}
println(pattern.matches("user@example.com"))  // true
```

### IP 주소

| 메서드 | 설명 | 매칭 예시 |
|--------|------|----------|
| `ipv4()` | IPv4 주소 (형식만 검증) | `192.168.1.1` |
| `ipv4Strict()` | IPv4 주소 (0-255 범위 검증) | `192.168.1.1` |
| `ipv6()` | IPv6 주소 (8개 그룹) | `2001:0db8:85a3:0000:0000:8a2e:0370:7334` |

```kotlin
val pattern = regex {
    startOfLine()
    ipv4Strict()
    endOfLine()
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
    startOfLine()
    isoDateTime()
    endOfLine()
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
    startOfLine()
    hexColor()
    endOfLine()
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
    startOfLine()
    decimal()
    endOfLine()
}
println(pattern.matches("123.456"))  // true
println(pattern.matches("-0.5"))     // true
println(pattern.matches(".25"))      // true
```

## 패턴 디버깅

`Regex.pattern` 속성으로 생성된 패턴을 확인할 수 있습니다:

```kotlin
val 정규식 = regex {
    startOfLine()
    oneOrMore { hangul() }
    literal("님")
    endOfLine()
}

println("생성된 패턴: ${정규식.pattern}")
// 출력: ^(?:[가-힣])+\Q님\E$
```

## 입력 검증

라이브러리는 입력을 검증하고 잘못된 매개변수에 대해 `IllegalArgumentException`을 발생시킵니다:

```kotlin
range('z', 'a')           // 오류: 범위 시작은 끝보다 작거나 같아야 함
repeat(-1) { digit() }    // 오류: 반복 횟수는 음수가 될 수 없음
capture("123") { }        // 오류: 이름은 문자로 시작해야 함
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
    startOfLine()
    repeat(2, 5) { hangul() }
    endOfLine()
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
