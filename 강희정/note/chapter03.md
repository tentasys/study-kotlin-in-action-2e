# 3장. 함수 정의와 호출

## 3.1 코틀린에서 컬렉션 만들기

setOf, listOf, mapOf으로 컬렉션 생성

```kotlin
val set = setOf(1, 7, 53)
val list = listOf(1, 7, 53)
val map = mapOf(1 to "one", 7 to "seven")
val map2 = listOf(1 to "one", 7 to "seven")
```

- 자바 컬렉션 클래스를 사용
- 기본적으로 immutable함
- 확장 함수들이 추가되어 다른 많은 연산 가능

## 3.2 함수를 호출하기 쉽게 만들기

### 3.2.1 이름 붙인 인자 (Named Argument)

```kotlin
joinToString(collection, separator = " ", prefix = " ", postfix = ".")
```

- 함수에 전달하는 인자 중 일부의 이름 명시 가능
- 모든 인자의 이름을 지정하는 경우, 순서 변경도 가능

### 3.2.2. 디폴트 파라미터

```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ",",
    prefix: String = " ",
    postfix: String = ""
): String { /* ... */ }
```

- 코틀린은 함수 선언에서 파라미터의 기본값 설정 가능
- 일반 호출 시
    - 함수를 선언할 때와 같은 순서로 인자 지정
    - 뒤쪽의 인자들을 연속적으로 일부 생략 가능
- 이름 붙은 인자 사용 시
    - 중간 인자 생략 가능
    - 순서 상관 없이 지정 가능
- 자바에서 호출하려면 모든 인자를 명시해야 함
    - @JvmOverloads 사용하면 코틀린 컴파일러가 자동으로 맨 마지막 파라미터로부터 파라미터를 하나씩 생략해서 오버로딩한 자바 메서드를 추가해줌

### 3.2.3 최상위 함수와 프로퍼티

- 코틀린에서는 클래스 밖에 함수와 프로퍼티 위치 가능
    - 컴파일 과정에서 새로운 클래스가 만들어진다
    - 코틀린 파일의 모든 최상위 함수는 이 클래스의 정적 메서드
    - @file:JvmName 어노테이션 사용하면 생성되는 클래스 이름 변경 가능
- 임포트 시에는 함수와 프로퍼티가 정의된 패키지 임포트

public static final 필드로 노출하고 싶으면 const modifier를 추가하면 된다.

→ 그러면 const를 추가하지 않으면 어떻게 되는걸까?

- 코틀린에서 val는 private 필드 + public getter로 변환됨

const를 추가하지 않았을 경우

```kotlin
val greeting = "Hello, World!"
```

```java
public final class MyKt {
    private static final String greeting = "Hello, World!";

    public static final String getGreeting() {
        return greeting;
    }
}
```

- 필드가 private이기 때문에 Getter를 통해 접근해야 함
    - Java에서 `MyKt.greeting`처럼 직접 접근할 수 없고, 반드시 `MyKt.getGreeting()`을 호출해야 함.

const를 추가했을 경우

```kotlin
const val greeting = "Hello, World!"
```

```java
public final class MyKt {
    public static final String greeting = "Hello, World!";
}
```

- Java에서 `MyKt.greeting`으로 직접 접근 가능

→ const가 있으면 static variable이 되고, 없으면 static getter가 있는 프로퍼티가 된다.

최상위 함수/프로퍼티의 예제

- kotlin.math 패키지의 max
- println(max(PI, E))

## 3.3 확장 함수와 확장 프로퍼티

<aside>
💡

확장 함수

- 클래스의 멤버 메서드인 것 처럼 호출
- 클래스 밖에 선언된 함수
- 수신 객체 : 확장 함수 호출 시 호출하는 대상 객체
- 수신 객체 타입 : 확장할 클래스의 이름
</aside>

```kotlin
fun String.lastChar(): Char = this.get(this.length - 1)

fun main() {
	println("Kotlin".lastChar())
}
```

- String은 수신 객체 타입
- this는 수신 객체

외부에서 구현된 라이브러리, 다른 JVM 언어로 작성된 클래스에 확장을 추가하기 편리하다!

호출하는 쪽 → 확장 함수와 멤버 메서드 구분하기 어려움

확장 함수는 클래스 내부가 아닌 클래스 외부에서 정의되는 개념

→ 클래스를 확장하는 것처럼 보이지만, 클래스의 멤버에 직접 접근할 수 없다.

확장 함수는 수신객체를 자신의 멤버인 것처럼 다루지만, 사실상 **외부 함수**이다.

```kotlin
class Person(private val name: String) {
    private fun secret() = "This is a secret"
}

fun Person.getName(): String {
    // return name  // ❌ private 멤버에 접근 불가능 (컴파일 오류)
    // return secret()  // ❌ private 함수에도 접근 불가능 (컴파일 오류)
    return "Cannot access private members"
}

```

### 3.3.1 임포트와 확장 함수

```kotlin
import strings.lastChar
val c = "Kotlin".lastChar()
```

as를 사용하면 이름 바꿔 임포트 가능

```kotlin
import strings.lastChar as last
val c = "Kotlin".last()
```

### 3.3.2 자바에서 확장 함수 호출

정적 메서드 호출 시 첫 번째 인자로 수신 객체 넘기기

```java
char c = StringUtilKt.lastChar("Java");
```

확장 함수 → 수신 객체를 첫 번째 인자로 받는 정적 메서드

- 확장 함수는 static 메서드로 컴파일
- 실행 시점에 새로운 객체를 만들지 않음
- 어댑터 클래스를 생성하지 않음

→ 확장 함수를 호출하더라도 부가 비용이 들지 않는다.

→ 이 특징은 최상위 함수로 선언될 때만 해당함. 클래스 내부 등 정적 메소드로 컴파일되지 않는 위치에 선언되면 이러한 특징이 사라진다.

| **최상위 확장 함수** | ✅ `"수신 객체를 첫 번째 인자로 받는 정적 메서드"`로 변환 | `MyKt.addHello("Kotlin")` |
| --- | --- | --- |
| **클래스 내부 확장 함수** | ❌ 정적이 아닌 **인스턴스 메서드** | `new Greeting().addHello("Kotlin")` |

### 3.3.4 확장 함수 - 오버라이드 불가능

확장 함수는 클래스의 일부 X

클래스 외부에 새로 만든 함수 O

Button이 View를 상속한 클래스일 때

```kotlin
fun View.showOff() = println("view")
fun Button.showOff() = println("button")

fun main() {
	val view: View = Button()
	view.showOff()
	// view
}
```

- 호출될 함수는 확장 함수를 호출할 때 수신 객체로 지정한 변수의 컴파일 시점의 타입에 의해 결정
- 실행 시간에 저장된 객체의 타입에 의해 결정되지 않음
- 확장함수와 멤버함수의 시그니처가 같다면 → 멤버 함수의 우선순위가 더 높아 멤버 함수 호출

### 3.3.5 확장 프로퍼티

확장 프로퍼티는 클래스 내부에 정의되는 것이 아닌 외부에 정의되는 것임 → 값을 저장할 backing field가 존재하지 않음 → 게터 정의는 꼭 필요함

```kotlin
val String.firstChar: Char
    get() = this[0
```

자바에서 확장 프로퍼티 사용 → 게터나 세터 명시적 호출 필요

```java
StringUtilKt.getLastChar("Java")
StringUtilKt.setLastChar(sb, '!')
```

## 3.4 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원

### 3.4.2 가변 인자 함수

가변 길이 인자

- vargars
- 호출할 때 원하는 개수 만큼 여러 값을 넘기면 → 배열에 그 값들을 넣어 줌

스프레드 연산자

- 코틀린은 배열을 명시적으로 풀어서 배열의 각 원소가 전달되게 해야 함
- 배열 앞에 * 필요

```kotlin
fun main(args: Array<String>){
	val list = listOf("args": *args)
	println(list)
}
```

### 3.4.3 중위 호출과 구조 분해 선언

중위 호출(infix call)

```kotlin
val map = mapOf(1 to "one", 7 to "seven")
```

- 수신 객체 뒤에 메서드 이름 위치
- 그 뒤에 메서드 인자 넣음
- 인자가 하나뿐인 일반 메서드나 확장 함수에만 중위 호출 사용 가능

```kotlin
infix fun Any.to(other: Any) = Pair(this, other)
```

- 함수를 중위 호출에 사용하게 허용하고 싶으면 infix 변경자 사용

```kotlin
val (number, name) = 1 to "one"

for ((index, element) in collection.withIndex()) {
		println("$index: $element")
}
```

- 구조 분해 선언(destructuring declaration)

## 3.5 문자열과 정규식 다루기

코틀린 문자열은 자바 문자열과 같다 → 코틀린 코드가 만들어낸 문자열을 자바 메서드에 넘겨도 된다.

### 3.5.1 문자열 나누기

split

- java에서는 정규식을 인자로 받음
- 코틀린에서는 String/정규식 따로 받을 수 있음

### 3.5.2 3중 따옴표로 묶은 문자열

3중 따옴표

- 이 안에 있는 것은 이스케이프 처리 필요 없음
- 줄 바꿈 포함하여 아무 문자열이나 그대로 들어감
- 문자열 템플릿 사용 가능
    - $나 유니코드 이스케이프를 사용하고자 할 때는 → 내포 식 사용
    - val think = `“””Hmm ${\uD83E \uDD14}”””`
- HTML, JSON, XML같은 formatted text 표시할 때 유용하게 사용

## 3.6 로컬 함수와 확장

로컬 함수

- 함수에서 추출한 함수를 원래 함수에 내포
- 특정 함수 안에서 사용되는 로직을 함수화

```kotlin
class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    fun validate (user: User,
                  value: String,
                  fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can't save user")
        }
    }
    
    validate(user, user.name, "Name")
    validate(user, user.address, "Address")
}
```

- 일반적으로는 한 단계만 내포시킬 것을 권장