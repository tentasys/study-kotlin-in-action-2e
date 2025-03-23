# 3장 함수 정의와 호출

## 3.1 코틀린에서 컬렉션 만들기

- setof, listof, mapof 같은 키워드로 컬렉션 생성 가능
- 코틀린도 표준 자바 컬렉션 클래스 사용
- 코틀린 컬렉션 인터페이스 default read-only
- 자바 대비 더 많은 기본 함수 제공

## 3.2 함수를 호출하기 쉽게 만들기

```kotlin

fun main() {
    val list = listof(1, 2, 3)
    println(list)  // [1, 2, 3] toString() 호출
}
```

- default toString 구현체 포함

#### 1. joinToString() 초기 함수 구현

```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    seperator: String,
    prefix: String,
    postfix: String
): String {
    
    val result = StringBuilder(prefix)
    
    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(seperator)
        result.append(element)
    }
    
    result.append(postfix)
    return result.toString()
}

fun main() {
    val list = list(1, 2, 3)
    println(joinToString(list, "; ", "(", ")")) // (1; 2; 3) 출력
}
```

- 4개 parameter 전달 하는 비효율
- 각 parameter가 어떤 역할 하는지 알기 어려움

### 3.2.1 이름 붙인 인자

```kotlin
joinToString(collection, separator = " ", prefix = " ", postfix = ".")
```

- parameter에 이름 할당 가능
- parameter 순서도 변경 가능

### 3.2.2 디폴트 파라미터 값

```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    seperator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String
```

- 디폴트 값 지정
- 함수 디폴트 값 &rarr; 함수 선언하는 쪽에서 인코딩됨(*호출하는 쪽 x*)

#### @JvmOverloads

> `@JvmOverloads` 함수에 선언하면,<br> 
> 코틀린 컴파일러가 자동으로 맨 마지막 파라미터로부터<br> 파라미터를 하나씩 생략한 
> 오버로딩한 자바 메서드 추가

- 생성자 오버로딩 역할
- 보일러 플레이트 많이 줄여줌

ref)
- [[내 맘대로 정리한 Kotlin] @JvmOverloads: constructor를 일일이 상속받아 만들기 귀찮다면!](https://holika.tistory.com/entry/내-맘대로-정리한-Kotlin-JvmOverloads-constructor를-일일이-상속받아-만들기-귀찮다면)

### 3.2.3 정적인 유틸리티 클래스 없애기: 최상위 함수와 프로퍼티

```kotlin
// 파일명 : Join.kt

package strings

fun joinToString( /* .. */ ): String { /* .. */ }
```

- jvm &rarr; 클래스 내에 있는 코드만 실행 가능
- 컴파일 하는 과정에 위 코드 담은 새로운 클래스 만들어야함

```java
// java version

package strings;

public class JoinKt {
    public static String joinToString ( /* ... */ ) { /* ... */ }
}

import strings.JoinKt;

JoinKt.joinToString(list, ", ", "", "");

```

- java와 코틀린을 함께 쓰면 위와 같이 컴파일

#### 최상위 프로퍼티

```kotlin
var opCount = 0

fun perform() {
    opCount++ // 최상위 프로퍼티 값 변경
}

fun reportOperationCount(
    println(opCount) // 최상위 프로퍼티 값 조회
)
```

- 프로퍼티 값 정적 필드에 저장
- java static final 과 유사

## 3.3 메서드를 다른 클래스에 추가: 확장 함수와 확장 프로퍼티

> 확장 함수 : 클래스 밖에 선언된 함수로 어떤 클래스의 멤버 메서드인거처럼 호출 가능

```kotlin
package strings

fun String.lastChar(): Char = this.get(this.length - 1)
// 수신 객체 타입             수신 객체

fun main() {
    println("Kotlin".lastChar()) // 확장 함수 호출
}
```

- 확장 함수 안에서는 private, protected 멤버 사용 불가

### 3.3.2 자바에서 확장 함수 호출

```java
char c = StringUtilKt.lastChar("Java");
```

- java에서도 코틀린에서 정의한 확장 함수 사용 가능
- 최상위 함수 &rarr; 정적 메서드로 컴파일

### 3.3.3 확장 함수로 유틸리티 함수 정의

```kotlin

fun <T> Collection<T>.joinToString(
    seperator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String  {
    
    val result = StringBuilder(prefix)

    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(seperator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

fun main() {
    val list = list(1, 2, 3)
    println(list.joinToString(separator = "; ", prefix = "(", postfix = ")"))  // (1; 2; 3) 출력
}
```

### 3.3.4 확장 함수는 오버라이드할 수 없다

- 확장 함수 &rarr; override 불가

<table>
<tr>
<td align="center">멤버 함수</td>
<td align="center">확장 함수</td>
</tr>
<tr>
<td>

```kotlin

open class View {
    open fun click() = println("View clicked") // open 키워드로 override 허용
}

class Button: View() {
    override fun click() = println("Button clicked") // click 구현 override
}

fun main() {
    
    val view: View = Button()
    view.click() // Button clicked
}
```
</td>

<td>

```kotlin

fun View.showOff() = println("I'm a View!")
fun Button.showOff() = println("I'm a Button!")

fun main() {
    val view: View = Button()
    view.showOff() // 확장 함수 정적으로 결정, I'm a View!
}
```
</td>
</tr>
</table>


- 우선 순위 : 멤버 함수 > 확장 함수 
  - 확장 함수와 멤버 함수 이름, 시그니처가 같다면 멤버 함수 호출

### 3.3.5 확장 프로퍼티

```kotlin
val String.lastChar: Char 
    get() = get(length - 1)
    
    set(value: Char) {
        this.setCharAt(length - 1, value)
    }

fun main() {
    
    val sb = StringBuilder("Kotlin?")
    println(sb.lastChar) // ?
    
    sb.lastChar = "!"
    println(sb) // Kotlin!
}
```

- 확장 함수 대신 확장 프로퍼티로 변경
  - `"Kotlin".lastChar()` &rarr; `"Kotlin.lastChar` 
- 확장 프로퍼티 내 getter, setter로 접근 가능

## 3.4 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원

### 3.4.1 자바 컬렉션 API 확장

- java collection 클래스 기본 라이브러리 확장 함수로 추가

```kotlin
fun <T> List<T>.last(): T // 마지막 원소 반환
fun Collection<Int>.max(): Int // 컬렉션 최대값 조회
```

### 3.4.2 가변 인자 함수: 인자의 개수가 달라질 수 있는 함수 정의

```kotlin
fun listof<T>(vararg values: T): List<T>
```

- `가변 길이 인자(= varargs)` : 원하는 개수만큼 여러 인자를 넘기면 배열에 그 값 넣어줌

```kotlin
fun main(args: Array<String>) {
    val list = listof("args: ", *args)
    println(list)
}
```

### 3.4.3 쌍(튜플) 다루기: 중위 호출과 구조 분해 선언

```kotlin
val map = mapof(1 to "one", 7 to "seven")
```

- `중위 호출`: 수신 객체 뒤에 메서드 이름 넣고 그 뒤 유일한 메서드 인자 넣음
- 함수를 중위 호출 허용하고 싶으면 infix 선언

```kotlin
infix fun Any.to(other: Any) = Pair(this, other)

val (number, name) = 1 to "one"
```

## 3.5 문자열과 정규식 다루기

- 코틀린 문자열 = 자바 문자열 &rarr; 변환도 없고 자바 문자열 감싸는 wrapper 객체 생성 x

### 3.5.1 문자열 나누기

```kotlin
fun main() {
    
    println("12.345-6.A".split("\\.|-".toRegex())) // 정규식 지정, 여러 구분 문자열 지정
    // [12, 345, 6, A]
  
    println("12.345-6.A".split('.', '-'))
    // [12, 345, 6, A]
}
```

### 3.5.2 정규식과 3중 따옴표로 묶은 문자열

- 3중 따옴표 문자열 쓰면
  - 이스케이프 사용 x
  - 문자열 템플릿 사용 가능

### 3.6 코드 깔끔하게 다듬기: 로컬 함수와 확장

- 코틀린 &rarr; 함수 내 함수 정의 가능

<table>
<tr>
<td align="center">as-is</td>
<td align="center">to-be</td>
</tr>
<tr>
<td>

```kotlin
fun saveUser(user: User) {
    if (user.name.isEmpty()) {
        throw IllegalArgumentException("cant save user")
    }
  
    if (user.address.isEmpty()) {
        throw IllegalArgumentException("cant save user")
    }
  
  // save user
}
```
</td>
<td>

```kotlin
fun saveUser() {
    
  // local 함수 정의
  fun validate(user:User, value: String) {

    if (user.name.isEmpty()) {
      throw IllegalArgumentException("cant save user")
    } 
  }
}

validate(user, user.name)
validate(user, user.address)
```
</td>
</tr>
</table>

#### 확장 함수 사용

```kotlin
fun User.validateBeforeSave() {
  fun validate(user:User, value: String) {

    if (user.name.isEmpty()) {
      throw IllegalArgumentException("cant save user")
    }
  }
}

validate(name, "name")
validate(adderss, "address")

fun saveUser(user: User) {
    user.validateBeforeSave()
}
```

- validation 로직을 user class에서 제외 &rarr; user를 사용하는 다른 곳에서는 쓰이지 않기 때문
  - 그래서 확장 함수로 추출