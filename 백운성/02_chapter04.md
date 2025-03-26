# 3주 4장 클래스, 객체, 인터페이스 : 2025-03-17 ~ 2025-03-23

-

## 클래스 계층 정의

### 코틀린 인터페이스

```kotlin
interface Clickable {
    // abstract method
    fun click()

    // default method
    fun showOff() = println("I'm clickable!")
}

class Button : Clickable {
    override fun click() = println("I was clicked")
}
```

- interface 사용
- 추상 및 구현메소드가 사용 가능
- 구현체 메소드에 `override` 필수
- 클래스에 인터페이스는 다수 클래스는 하나만 상속 가능

```kotlin
interface Clickable {
    // abstract method
    fun click()

    // default method
    fun showOff() = println("I'm clickable!")
}

interface Focusable {
    // abstract method
    fun setFocus(b: Boolean) = println("$b")

    // default method
    fun showOff() = println("I'm focusable!")
}

class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")

    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}
```

- 인터페이스에 default 메소드가 동일할 경우 override를 해야함 안하면 에러
- `super`를 사용하여 상위 타입의 멤버 메서드를 호출할지 지정할 수도 있음

### open, final, abstract 변경자: 기본적으로 final

- 코틀린의 모든 클래스와 메서드는 기본적으로 `final` (자바에서 final 명시한 것과 같음)
- 하위 클래스를 만들수도 없고, 하위 클래스가 override 할 수도 없음
- 작성한 사람의 의도와 다르게 동작함을 방지하기 위함
- 꼭 필요하면 명시적으로 정의
- 인터페이스의 멤버는 항상 `open`이며 final 변경 불가

| keyword  | description                                                                                                                |
|----------|----------------------------------------------------------------------------------------------------------------------------|
| final    | - 클래스 멤버의 기본 변경자<br/>- override 안됨                                                                                         |
| open     | - 붙여야 override 가능<br/>- 상속 가능 class, method에 각각 정의 가능<br/>- final이 없는 override 메서드나 프로퍼티는 기본적으로 열려있기 때문에 final을 명시적으로 해줘야함 |
| abstract | - 추상 클래스이며, 인스턴스화 할수 없음<br/>- 추상클래스의 멤버에만 사용 가능                                                                            |

### 가시성 변경자: 기본적으로 공개

- public, protected, private 제공
- 자바와 동일하지만 아무것도 붙이지 않으면 `public`
- 모듈안에서만 한정적으로 `internal`이라는 변경자를 제공
- `protected`는 자바와 다르게 오직 어떤 클래스나 그 클래스를 상속한 클래스 안에서만 볼수 있음

| keyword   | description                                                | Top-level 선언시    |
|-----------|------------------------------------------------------------|------------------|
| public    | 선언하지 않으면 기본                                                | 모듈 범위/전체 공개      |
| internal  | 같은 모듈안에서만 접근 가능<br/>-모듈:컴파일되는 코틀린 파일의 집합체(gradle, maven 등) | 모듈 범위/전체 공개      |
| protected | 하위 클래스 안에서 접근 가능                                           | 사용할 수 없음         |
| private   | 같은 클래스 또는 같은 파일 내에서만 접근 가능                                 | 같은 파일 내에서만 접근 가능 |

### 내부 클래스와 내포된 클래스: 기본적으로 내포 클래스

- 클래스 안에 클래스 선언 가능 (내포 클래스:nested class)
- 명시적으로 요청하지 않는 한 바깥쪽 클래스 인스턴스에 접근 권한이 없음
- 자바와 반대로 동작

| java           | kotlin        | B안에 정의된 A클래스  |
|----------------|---------------|---------------|
| static class A | class A       | class B 접근 불가 |       
| class A        | inner class A | class B 접근 가능 |

```kotlin
class B {
    inner class A {
        fun getB(): B = this@B
    }
}
```

### 봉인된 클래스: 확장이 제한된 클래스 계층 정의

- `sealed` 클래스는 상속 가능한 클래스 계층을 제한하는 기능
- 상속 가능한 하위 클래스들을 한정해서 컴파일러가 더 똑똑하게 판단할 수 있게 도와주는 기능
- when 표현식 사용할 때 else 없이 모든 경우 처리 가능
- 기본적으로 추상 클래스가 되며, 인스턴스화 불가
- 정의된 패키지와 같은 패키지에 속해야 하며, 모든 하위 클래스가 같은 모듈 안에 위치해야 함

```kotlin
sealed class Result

class Success(val data: String) : Result()
class Failure(val error: String) : Result()

fun handle(result: Result) {
    when (result) {
        is Success -> println("Success: ${result.data}")
        is Failure -> println("Error: ${result.error}")
        // else 필요 없음! 컴파일러가 모든 경우를 알기 때문
    }
}
```

## 뻔하지 않은 생성자나 프로퍼티를 갖는 클래스 선언

- 주생성자와 부생성자로 구분
- 초기화 블록을 통해 초기화 로직 추가 가능

### 클래스 초기화: 주 생성자와 초기화 블록

- 클래스 헤더에 정의된 생성자를 주생성자
- 초기화 블럭 `init`은 주생성자에만 사용 가능
- constructor 생략 가능
- 메소드 처럼 기본값도 사용가능하고 이름으로 파라미터 사용도 가능함
- 상속 시 상속 클래스 초기화하려면 생성자 인자를 넘기면 됨

```kotlin
open class Person constructor(val name: String, var age: Int = 20) {
    // 초기화 블록이나 프로퍼티 정의 가능
    init {
        println("이름: $name, 나이: $age")
    }
}

// 간략하게 name과 age를 받는 생성자가 자동으로 만들어짐
open class Person(val name: String, var age: Int = 20)

class BadPerson(name: String) : Person(name)
```

### 부생성자: 상위 클래스를 다른 방식으로 초기화

- 부생성자는 주생성자와 다르게 class 내부에 선언
- `constructor` 키워드 사용
- 갯수제한 없음
- `this` 키워드를 사용하면 같은 클래스내 다른 생성자 호출 가능

```kotlin
class Person(val name: String) {
    var age: Int = 0

    constructor(name: String, age: Int) : this(name) {
        this.age = age
    }
}
```

### 인터페이스에 선언된 프로퍼티 구현

- 인터페이스에 추상 프로퍼티 선언 가능
- override, getter로 구현가능
- 아래 케이스가 아니면 함수를 사용해라
    - 예외를 던지지 않음
    - 객체 상태가 바뀌지 않음
    - 적은 계산 비용이 필요할 때

```kotlin
interface Animal {
    val name: String   // 추상 프로퍼티
}

class Dog : Animal {
    override val name: String = "Buddy"
}

class Cat : Animal {
    override val name: String
        get() = "Kitty"
}
```

### 게터와 세터에서 뒷받침하는 필드에 접근

- getter/setter 안에서 프로퍼티 값을 저장하거나 참조할 때 사용하는 숨겨진 필드
- `field`라는 키워드로 접근
- Kotlin이 내부적으로 backing field가 필요하다고 판단할 때만 field를 사용할 수 있음
    - 기본값이 있음
    - field를 사용하는 getter/setter

```kotlin
var name: String = "Unknown"
    get() = field.uppercase()  // field = 실제 저장된 값
    set(value) {
        field = value.trim()   // 저장 전에 가공 가능
    }
```

### 접근자의 가시성 변경

- 외부에서 읽기만 허용하고, 변경은 제한하거나 특정 메서드로만 가능하게 할 수 있음
- `protected`와 `internal` 사용 가능
    - protected: setter는 하위 클래스에서만 접근 가능
    - internal: setter는 같은 모듈에서만 접근 가능

```kotlin
var name: String = "Unknown"
    private set
```

## 컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임

### 데이터 클래스

- 코틀린도 자바와 마찬가지로 toString, equals, hashCode가 필요함
- class에 `data` keyword 선언을 통해 컴파일 시 자동으로 생성

```kotlin
data class Person(val name: String, val age: Int)

// data class에 대해 컴파일러일 내부 메소드가 자동으로 생성됨 
class Person(val name: String, val age: Int) {

    override fun toString(): String {
        return "Person(name=$name, age=$age)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false
        return name == other.name && age == other.age
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + age
        return result
    }

    fun copy(name: String = this.name, age: Int = this.age): Person {
        return Person(name, age)
    }
}
```

| method     | 용도                                                                        |
|------------|---------------------------------------------------------------------------|
| toString() | java의 toString()과 동일                                                      |       
| equals()   | 값비교 (javascript와 비슷)<br/>- == 값<br/>- === 참조                              |
| hashCode() | - 객체를 HashSet, HashMap 등에 쓸 때 필요<br/>- equals()를 오버라이드하면 반드시 같이 오버라이드해야 함 |
| copy()     | 불변 클래스 생성, 일부만 바꿔서 새 객체 생성                                                |

### 클래스 위임

- 클래스나 프로퍼티의 기능을 다른 객체에 위임할 때 사용

```kotlin
interface Printer {
    fun print()
}

class RealPrinter : Printer {
    override fun print() = println("Printing...")
}

// Printer 기능을 RealPrinter에 위임
class PrinterProxy(printer: Printer) : Printer by printer
```

## object 키워드: 클래스 선언과 인스턴스 생성을 한꺼번에 하기

### 싱글턴
- object 선언만 하면 자동으로 싱글턴이 됨
- 생성자 없음
- 처음 사용될 때 자동으로 생성됨 (lazy)
- 클래스 밖에 선언

```kotlin
object AppConfig {
    val appName = "MyApp"

    fun printConfig() {
        println("App: $appName")
    }
}
```

### 동반객체: 팩토리 메서드와 정적 멤버가 들어갈 장소
- 클래스 안에서 static 멤버처럼 사용
- 클래스의 인스턴스를 생성하지 않아도 접근 가능
- 인터페이스 구현가능
- 확장함수로도 사용가능
- 이름 지정 가능
- 클래스 안에 선언하며, 클래스와 함께 사용 
- 자신을 툴러싼 클래스의 모든 private 멤버에 접근 가능

```kotlin
class User private constructor(val name: String) {
    // Factory 생략 가능
    companion object Factory {
        fun fromEmail(email: String): User {
            val name = email.substringBefore("@")
            return User(name)
        }

        fun fromId(id: String): User {
            val name = id
            return User(name)
        }
    }
}

// companion object의 확장함수
fun User.Factory.fromNickname(nickname: String): User {
    return User(nickname)
}

fun main() {
    val user1 = User.fromEmail("admin@test.com")
    val user2 = User.Factory.fromId("admin")

    println(user1.name)  // admin
    println(user2.name)  // admin
}
```

### 익명 내부 클래스
- 자바의 익명 클래스와 동일한 개념
- 인터페이스, 추상 클래스 구현 가능
```kotlin
interface ClickListener {
    fun onClick()
}

fun setClickListener(listener: ClickListener) {
    listener.onClick()
}

fun main() {
    setClickListener(object : ClickListener {
        override fun onClick() {
            println("Clicked!")
        }
    })
}
```

## 부가 비용 없이 타입 안전성 추가: 인라인 클래스
- 가볍고 효율적인 `타입 래퍼(wrapper)` 를 만들기 위한 기능
- 하나의 `값(value)` 만 감싸는 클래스
- 성능 희생을 하지 않고 타입 안전성을 가져갈 수 있음
- `@JvmInline`와 class에 `value` 키워드 사용
- 필드는 하나만 가질 수 있음
- 상속 불가, 초기화 불가_

```kotlin
@JvmInline
value class UserId(val id: String)

fun printUser(id: UserId) {
  println("User ID: ${id.id}")
}

fun main() {
  val userId = UserId("abc123")
  // 호출 시에는 실제로는 그냥 "abc123" 값만 전달되는 식으로 최적화됨
  printUser(userId)
}
```