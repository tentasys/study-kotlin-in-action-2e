# Chapter 3. 함수 정의와 호출

### 3장 요약
- 컬렉션, 문자열, 정규식을 다루기 위한 함수
- 이름 붙인 인자, 디폴트 파라미터 값, 중위 호출 문법 사용
- 확장 함수와 확장 프로퍼티를 사용해 자바 라이브러리를 코틀린에 맞게 통합
- 최상위 및 로컬 함수와 프로퍼티를 사용해 코드 구조화

## 3.1 코틀린에서 컬렉션 만들기

```kotlin
fun main() {
    val set = setOf(1, 7, 53) // java.util.LinkedHashSet
    val list = listOf(1, 7, 53) // java.util.Arrays$ArrayList
    val map = mapOf(1 to "one", 7 to "seven") // java.util.LinkedHashMap
}
```

- 코틀린은 표준 자바 컬렉션 클래스를 사용

## 3.2 함수를 호출하기 쉽게 만들기

```kotlin
/**
 * joinToString() 함수의 초기 구현
 */
fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {
    
    val result = StringBuilder(prefix)
    
    for((index, element) in collection.withIndex()) {
        if(index > 0) result.append(separator)
        result.append(element)
    }
    
    result.append(postfix)
    
    return result.toString()
}
```

### 3.2.1 이름 붙인 인자

- 코틀린에서는 다음과 같이 argument에 이름을 붙일 수 있다 `joinToString(collection, separator = " ", prefix = " ", postfix = ".")`
- 심지어 이름을 붙일 경우 순서를 변경할 수 있다

### 3.2.2 디폴트 파라미터 값

```kotlin
/**
 * 디폴트 파라미터 값을 사용해 joinToString() 정의하기
 */
fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    
    val result = StringBuilder(prefix)
    
    for((index, element) in collection.withIndex()) {
        if(index > 0) result.append(separator)
        result.append(element)
    }
    
    result.append(postfix)
    
    return result.toString()
}
```

> 자바에서 코틀린 함수를 호출할 때에는 기본적으로 모든 인자를 명시해야 함. 하지만 @JvmOverloads 어노테이션을 사용하면 
> 코틀린 컴파일러가 파라미터를 하나씩 생략한 자바 메서드를 추가함

### 3.2.3 정적인 유틸리티 클래스 없애기: 최상위 함수와 프로퍼티

- 자바에서는 함수만 따로 생성할 수 없기에 Util이라는 명칭의 정적 메서드를 모아두는 역할의 클래스를 종종 생성함. 코틀린에서는 불필요

```
/**
 * joinToString() 함수를 최상위 함수로 선언하기
 */
fun joinToString(/* ... */): String{/* ... */}

// 다른 JVM 언어에서 호출하기 위해 소스 파일 명칭으로 클래스를 생성
public class JoinKt {
    public static String joinToString(/* ... */): String{/* ... */}
}
```

- 예제와 같이 함수만 정의해도 다른 JVM 언어에서 사용가능하도록 변경

## 3.3 메서드를 다른 클래스에 추가: 확장 함수와 확장 프로퍼티

- 확장 함수: 어떤 클래스의 멤버 메서드인 것처럼 호출할 수 있지만 그 클래스의 밖에 선언된 함수

- 예제: `fun String.lastChar(): Char = this.get(this.length - 1)`
  - 수신 객체 타입(receiver type): String
  - 수신 객체(receiver object): this

- 호출 방법: `println("Kotlin".lastChar())`

- String 클래스의 소스코드를 소유하지 않았어도, String이 코틀린으로 쓰이지 않았더라도, final로 상속 할 수 없게 선언되었더라도 문제 없이 확장 가능
- 단, 확장 함수가 **캡슐화**를 깨뜨리는 것은 아님. 클래스 내부 private, protected 멤버는 사용할 수 없음

### 3.3.1 임포트와 확장 함수

- 확장 함수 역시 확장 함수가 정의된 패키지를 import 해서 사용해야 함

```kotlin
import strings.lastChar // 함수만 임포트

import strings.* // 와일드카드 임포트

import strings.lastChar as last // 임포트한 함수에 별칭 지정 가능
```

### 3.3.2 자바에서 확장 함수 호출

- 자바에서 코틀린 함수를 사용하기 위해 파일 명칭(클래스 명칭)과 함께 함수를 호출하면 됨

### 3.3.3 확장 함수로 유틸리티 함수 정의

```kotlin
/**
 * joinToString()을 확장으로 정의하기
 */
fun <T> Collection<T>.joinToString( // Collection<T>에 대한 확장 함수를 선언
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    
    val result = StringBuilder(prefix)
    
    for((index, element) in this.withIndex()) {
        if(index > 0) result.append(separator)
        result.append(element)
    }
    
    result.append(postfix)
    
    return result.toString()
}

fun main() {
    val list = listOf(1, 2, 3)
    println(
        list.joinToString(
            separator = "; ",
            prefix = "(",
            postfix = ")"
        )
    )
}
```

- Generic 표현이 아닌 String에 대해서만 확장 함수를 정의할 수 있음.
- 그럴 경우 String이 아닌 다른 타입에 이뤄진 Collection은 확장 함수를 호출할 수 없다

### 3.3.4 확장 함수는 오버라이드 할 수 없다

- 확장 함수는 곧 static이기에 컴파일 시점에 모든 것이 결정. 확장 함수를 **첫 번째 인자가 수신 객체인 정적 자바 메서드로 컴파일** 한다는 사실을 기억하자

### 3.3.5 확장 프로퍼티

- `"myText".lastChar()` 대신 `"myText".lastChar` 형태로 사용하기

```kotlin
/**
 * 확장 프로퍼티 선언하기
 */
val String.lastChar: Char
    get() = get(length - 1)
```

```kotlin
/**
 * 변경 가능한 확장 프로퍼티 선언하기
 */
val StringBuilder.lastChar: Char
  get() = get(length - 1)
  set(value: Char) {
    this.setCharAt(length - 1, value)
  }
```

## 3.4 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원

- 컬렉션 처리 시 사용가능한 코틀린 표준 라이브러리 소개
  - vararg 키워드를 사용하면 호출 시 인자 개수가 달라질 수 있는 함수를 정의 할 수 있다
  - 중위 함수 호출 구문을 사용하면 인자가 하나뿐인 메서드를 간편하게 호출 할 수 있다.
  - 구조 분해 선언을 사용하면 복합적인 값을 분해해서 여러 변수에 나눠 담을 수 있다.

### 3.4.1 자바 컬렉션 API 확장

```kotlin
/**
 * 코틀린 컬렉션 디폴트 확장 함수 예제
 */
fun <T> List<T>.last(): T { /* 마지막 원소를 반환 */}
fun Collection<Int>.max(): Int {/* 컬렉션의 최대값을 찾음*/}
```

### 3.4.2 가변 인자 함수

```kotlin
fun listOf<T>(vararg values: T): List<T> { /* 구현 */ }
```

- 가변인자함수에 원소를 넘길 때 자바는 그냥 배열을 넘겨도 되지만 코틀린은 스프레드 연산자로 배열을 풀어서 전달해야함

```kotlin
fun main(args: Array<String>) {
    val list = listOf("args: ", *args)
    println(list)
}
```

### 3.4.3 튜플 다루기: 중위 호출과 구조 분해 선언

- `val map = mapOf(1 to "one", 7 to "seven")` 과 같이 to라는 단어는 예약어가 아닌 중위 호출 방식을 사용한 것
- 중위 호출 시 수신 객체 뒤에 메서드 이름을 위치시키고 그 뒤에 유일한 메서드 인자가 있을 경우 가능
  - to("one") 과 to "one" 은 동일하다
- 중위 호출은 인자가 하나뿐인 메서드 또는 확장 함수에만 사용 가능하고 infix 변경자를 함수 선언 앞에 추가해야 함
- `infix fun Any.to(other: Any) = Pair(this, other)`
- Pair의 내용으로 다음과 같이 두 변수를 즉시 초기화 가능 `val (number, name) = 1 to "one"` 이를 구조 분해 선언이라 칭함
- 구조 분해는 Pair 뿐 아니라 map, 루프 등에서 사용할 수 있다 `for((index, element) in collection.withIndex())`

## 3.5 문자열과 정규식 다루기

- 확장 함수 외 자바와 기본적으로 동일하기에 연동에 문제 없음

### 3.5.1 문자열 나누기

- 자바 split의 인자는 정규표현식이기에 사용 시 실수하는 경우가 종종 발생. 코틀린에서는 split의 인자가 String이 아닌 Regex로 정규표현식을 받음을 명시함
- `"12.345-6.A".split("\\.|-".toRegex()) // 12, 345, 6, A`
- `"12.345-6.A".split('.', '-') // 12, 345, 6, A` 둘 다 가능하고 간단한 경우 이처럼 문자를 인자로 넘길 수 있음

### 3.5.2 정규식과 3중 따옴표로 묶은 문자열

```kotlin
/**
 * String 확장 함수를 사용해 경로 파싱하기
 * 파라미터: "Users/yole/kotlin-book/chapter.adoc"
 */
fun parsePath(path: String) {
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfterLast("/")
    val fileName = fullName.substringBeforeLast(".")
    val extension = fullName.substringAfterLast(".")
  
    println("Dir: $directory, name: $fileName, ext: $extension")
}
```

```kotlin
/**
 * 3중 따옴표와 정규 표현식을 사용한 경로 파싱
 */
fun parsePath(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if(matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
          println("Dir: $directory, name: $fileName, ext: $extension")
    }
}
```

- 3중 따옴표 문자열을 사용할 경우 이스케이프가 전혀 필요 없다.

### 3.5.3 여러 줄 3중 따옴표 문자열

- 3중 따옴표 문자열은 줄 바꿈을 포함해 아무 문자열이나 그대로 들어감.

## 3.6 코드 깔끔하게 다듬기: 로컬 함수와 확장

- 코드 중복을 방지하기 위해 코틀린에서 제공하는 기능을 설명하는 챕터

```kotlin
/**
 * 코드 중복을 보여주는 예제
 */
class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    if (user.name.isEmpty()) {
        throw IllegalArgumentException(
            "Can't save user ${user.id}: empty Name"
        )
    }
    if (user.address.isEmpty()) {
        throw IllegalArgumentException(
            "Can't save user ${user.id}: empty Address"
        )
    }
}
```

```kotlin
/**
 * 로컬 함수를 사용해 코드 중복 줄이기
 */
class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    
  fun validate(user: User,
               value: String,
               fieldName: String) { 
      if (value.isEmpty()) {
        throw IllegalArgumentException(
            "Can't save user ${user.id}: empty $fieldName"
        )
      }
  }
  
  validate(user, user.name, "Name")
  validate(user, user.address, "Address")
}
```

- 로컬 함수는 자신이 속한 바깥 함수의 모든 파라미터와 변수를 사용할 수 있다

```kotlin
/**
 * 로컬 함수에서 바깥 함수의 파라미터 접근하기
 */
class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    
  fun validate(value: String, fieldName: String) { 
      if (value.isEmpty()) {
        throw IllegalArgumentException(
            "Can't save user ${user.id}: empty $fieldName"
        )
      }
  }
  
  validate(user.name, "Name")
  validate(user.address, "Address")
}
```

- User 클래스 확장 함수 사용
- 
```kotlin
/**
 * 검증 로직을 확장 함수로 사용하기
 */
class User(val id: Int, val name: String, val address: String)

fun User.validateBeforeSave() {
    
  fun validate(value: String, fieldName: String) { 
      if (value.isEmpty()) {
        throw IllegalArgumentException(
            "Can't save user $id: empty $fieldName"
        )
      }
  }
  
  validate(user.name, "Name")
  validate(user.address, "Address")
}
```