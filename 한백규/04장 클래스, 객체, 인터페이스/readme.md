# 4장 클래스, 객체, 인터페이스

## 4.1 클래스 계층 정의

- kotlin default final, public method 정의
- sealed class implements, extends 불가능

### 4.1.1 코틀린 인터페이스

```kotlin
interface Clickable {
    fun click()
}

class Button : Clickable {
    override fun click() = println("I was clicked")
}

fun main() {
    Button().click() // I was clicked
}
```

- 상위 클래스, 인터페이스 내 프로퍼티나 메서드 사용 시 override 사용 필요
- 인터페이스 멤버 항상 열려 있으며 final 변경 불가
- 인터페이스 멤버의 경우 final, open, abstract 사용 x

##### default method 구현

```kotlin
interface Clickable {
    fun click() // 일반 메소드 선언
    fun showOff() = println("I'm clickable")    // 디폴트 구현이 있는 메소드
}

interface Focusable {
    fun showOff() = println("I'm focusable")    // 디폴트 구현이 있는 메소드
}
```

##### 상속한 인터페이스의 메소드 구현 호출하기

```kotlin
class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")

    // 동일한 정보의 둘 이상의 디폴트 구현 있으면
    // 인터페이스를 구현하는 하위 클래스에서 명시적으로 새로운 구현 제공 필요
    override fun showOff() {    
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}
```

### 4.1.2 open, final, abstract 변경자: 기본적으로 final

- 모든 클래스, 메서드 기본적으로 final
  - 하위 클래스 생성 x, 하위 클래스가 override x
- 상속 허용하려면 클래스 앞에 open 필요
- 오버라이드를 허용하고 싶은 메소드, 프로퍼티 앞에도 open 필요

##### override 금지

```kotlin
open class RichButton : Clickable {
    final override fun click()  // final이 없는 override 메소드, 프로퍼티는 기본적으로 열려있어 final로 금지
}
```

##### 추상 클래스 정의

```kotlin
abstract class Animated {
    abstract fun animate()  // 추상 함수 항상 open
    open fun stopAnimating() {} // 비추상 함수 default, final 근데 open 으로 override 허용
    fun animateTwice() {}
}
```

- abstract로 선언한 추상 클래스 &rarr; 인스턴스화 x
- 추상 멤버 앞에 open 변경자 명시 x

### 4.1.3 가시성 변경자: 기본적으로 공개

- kotlin &rarr; public, protected, private accessor
- `public` : 누구나 접근, default accessor
- `protected` : 하위 클래스
- `private` : 그 선언이 포함된 클래스 내부
- `internal` : 같은 모듈 내부

### 4.1.4 내부 클래스와 내포된 클래스: 기본적으로 내포 클래스

- nested class &rarr; 기본적으로 외부에서 접근 권한 x
- kotlin nested class에 아무런 변경자가 붙지 않으면 java static inner class 동일 
  - 이를 내부 클래스로 변경해서 바깥쪽 클래스에 대한 참조를 포함하게 만들고 싶다면 inner 변경자 붙여야함 
  - 내부 클래스 Inner 안에서 바깥쪽 클래스 Outer 참조에 접근 하려면 `this@Outer` 명시

```kotlin
class Button : View {
    override fun getCurrentState(): State = ButtonState()
    class ButtonState : State {}    // similar with java static inner class 
}
```

```kotlin
class Outer {
    inner class Inner { 
        fun getOuterRef(): Outer = this@Outer
    }
}
```

### 4.1.5 봉인된 클래스: 확장이 제한된 클래스 계층 정의

- `sealed 변경자` : 상위 클래스 상속한 하위 클래스 정의 제한

##### sealed 클래스로 식 표현하기

```kotlin
sealed class Expr { // sealed로 봉인
    class Num(val value: Int) : Expr()  // 모든 하위 클래스를 중첩 클래스로 나열
    class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int =
    when (e) {  // else(= default) case 불필요
        is Expr.Num -> e.value
        is Expr.Sum -> eval(e.right) + eval(e.left)
    }
```

## 4.2 뻔하지 않은 생성자나 프로퍼티를 갖는 클래스 선언

- primary constructor, secondary constructor 구분
- init block 제공 for 데이터 초기화 로직

### 4.2.1 클래스 초기화: 주 생성자와 초기화 블록

##### 다양한 초기화 생성 방법

```kotlin
class User(val nickname: String)    // 기본 생성자

class User(val nickname: String, val isSubscribed: Boolean = true)  // default 값 존재하는 생성자

class TwitterUser(nickname: String) : User(nickname) {} // 기반 클래스를 초기화하려면 기반 클래스 이름 뒤에 괄호 치고 생성자 전달

open class Button   // 인자 없는 디폴트 생성자 생성

class RadioButton: Button() // Button 클래스를 상속한 하위 클래스는 반드시 Button 클래스의 생성자 호출

class Secretive private constructor() {} // 비공개 생성자 - 클래스 외부에서 인스턴스화 못하게 막을려면 private 선언
```

### 4.2.2 부 생성자: 상위 클래스를 다른 방식으로 초기화

##### 부 생성자 생성 방법

```kotlin
open class Downloader {
    constructor(url: String?) {}
}

open class MyDownloader : Downloader {
    constructor(url: String?) : super(url) {}   // 상위 클래스 생성자 호출
    
    constructor(uri: URI?): super(uri) {}
}

class MyDownloader: Downloader {
    constructor(url: String?): this(URI(url)) // 같은 클래스 다른 생성자에 위임
    constructor(uri: URI?): super(uri)
}
```

### 4.2.3 인터페이스에 선언된 프로퍼티 구현

##### 추상 프로퍼티 선언 들어있는 인터페이스 선언 예시

```kotlin
interface User {
    val nickname: String
}

class PrivateUser(override val nickname: String) : User // 주 생성자 프로퍼티

class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore('@')  // custom getter
}

class FacebookUser(accountId: Int) : User {
    override val nickname = getFacebookName(accountId)  // property initialization
}
```

#### 함수 대신 프로퍼티 사용하는 경우

- not throw exception
- 계산 비용 &darr;
- 여러 번 호출해도 항상 같은 결과 반환하는 경우

### 4.2.4 게터와 세터에서 뒷받침 하는 필드에 접근

##### 세터에서 뒷받침하는 필드 접근하기

```kotlin
class User(val name: String) {
    val address: String = "unspecified"
        set(value: String) {
            println(
                """
                Adress was changed for $name:
                "$field" -> "$value".
                """.trimIndent())  // 뒷받침하는 필드 값 읽기
            field = value   // field라는 특별한 식별자를 통해 뒷받침하는 필드 값 변경
        }
}

fun main() {
    val user = User("Alice")
    user.address = "sam-one tower" // "unspecified" -> "sam-one tower"
}
```

- 접근자 &rarr; `field` 식별자를 통해 field 접근 가능 
- getter &rarr; field value read only
- setter &rarr; field value read or write available

### 4.2.5 접근자의 가시성 변경

- 접근자의 가시성 = 기본적으로 프로퍼티 가시성 
- get 이나 set 앞에 가시성 변경자를 추가해서 접근자의 가시성 변경 가능

##### 비공개 세터가 있는 프로퍼티 선언하기

```kotlin
class LengthCounter {
    var counter: Int = 0
        private set // cannot update counter value
    
    fun addWord(word: String) {
        counter += word.length
    }
}
```

## 4.3 컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임

- `data class` : `toString()`, `hashcode()`, `equals()`, `copy()` 메서드 자동 생성 by kotlin compiler
  - 상속 불가 
  - val 또는 var 선언 
  - abstract, open, sealed, inner 추가 x 
  - 1개 이상의 프로퍼티 존재

### 4.3.2 데이터 클래스: 모든 클래스가 정의해야 하는 메서드를 자동으로 생성

#### copy() 메서드

- 배열 내 하나의 데이터를 단순 필드의 값만 변경해서 추가적으로 사용하고 싶을 때 
- 이 때 data class의 copy() 이용

```kotlin
fun UserProcess() {
    val user1 = User("Kenneth", "https://store.image/profile_1", 30)
    val user2 = user1.copy(name = "scarlet")
    // D/user1: User(name=Kenneth, profileImg=https://store.image/profile_1, age=30)
    // D/user2: User(name=scarlet, profileImg=https://store.image/profile_1, age=30)
}
```

- `copy()` 를 이용해서 name만 변경하여 쉽게 객체를 복사
- `copy()` 는 생성자에 정의된 프로퍼티에 한해서 값을 변경하여 복사 가능

#### by 키워드 사용

- `데코레이터 패턴` : 상속을 허용하지 않는 클래스 대신 사용할 수 있는 새로운 클래스를 만들되 기존 클래스와 같은 인터페이스를 데코레이더가 제공하게 만들고, 기존 클래스를 데이레이터 내부에 필드로 유지하는 것
- by 키워드로 간단하게 구현 가능

```kotlin
class DelegatingCollection<T>(
    innerList: Collection<T> = ArrayList<T>()
) : Collection<T> by innerList()
```

## 4.4 object 키워드: 클래스 선언과 인스턴스 생성을 한꺼번에 하기

- `companion object` : 클래스의 인스턴스가 아니라 클래스 자체에 속하는 객체 
- 자바의 static 키워드를 사용하여 정의하는 정적 멤버를 대체

### 4.4.1 객체 선언: 싱글턴을 쉽게 만들기

```kotlin
object Payroll{
  val allEmployees = arrayListOf<Person>()
  fun calculateSalary(){
    for(person in allEmployees){
      ...
    }
  }
}
```

- object를 통해 기본적으로 싱글톤 기능을 언어 레벨 제공
- 객체 선언 = 클래스 선언 + 그 클래스에 속한 단일 인스턴스의 선언
- 생성자 사용 불가
- 싱글톤 객체는 객체 선언문이 있는 위치에서 생성자 호출 없이 즉시 만들어지기 때문에 생성자 정의 필요 x
- object 선언도 클래스나 인터페이스 상속 가능
- 클래스 안에 object 선언 가능
- 인스턴스 한개

### 4.4.2 동반 객체: 팩토리 메서드와 정적 멤버가 들어갈 장소

- 코틀린 &rarr; 자바 static 키워드 지원 x
- companion 표시를 붙이면 그 클래스의 동반 객체로 생성
- 동반 객체의 멤버를 사용하는 구문은 자바의 정적 메소드 호출이나 정적 필드 사용 구문과 동일

```kotlin
class A {
    companion object {
        fun bar() {
            println("Companion object called")
        }
    }
}

fun main() {
    A.bar() // Companion object called
}
```

- 동반 객체는 팩토리 패턴을 구현하기 가장 적합
- 바깥쪽 클래스의 private 생성자도 호출 가능

##### 부 생성자를 팩토리 메소드로 대신하기

```kotlin
class User private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) = User(email.substringBefore('@'))
    }
}

fun main() {
    val newSubscribingUser = User.newSubscribingUser("bob@gmail.com")
    println(newSubscribingUser.nickname) // bob
}
```

### 4.4.3 동반 객체를 일반 객체처럼 사용

##### 동반 객체에 이름 붙이기

```kotlin
class Person(val name: String) {
    companion object Loader {
        fun fromJSON(jsonText: String): Person = ...
    }
}

// 아래 두가지 방법 모두 가능
fun main() {
    Person.Loader.fromJSON("{name: 'Dmitry')}")
    Person.fromJSON("{name: 'Dmitry')}")
}
```

##### 동반 객체에서 인터페이스 구현

```kotlin
interface JONSFactory<T> {
    fun fromJSON(jsonText: String) : T
}

class Person(val name: String) {
    companion object : JSONFactory<Person> {
        override fun fromJSON(jsonText: String) : Person = ... 
    }
}
```

##### 동반 객체 확장

```kotlin
// 비즈니스 로직 모듈
class Person(val firstName: String, val lastName: String) {
    companion object {  // 비어있는 동반 객체 선언
    }
}

// 클라이언트/서버 통신 모듈
fun Person.Companion.fromJSON(json: String) : Person { // 확장 함수 선언
}

fun main() {
    Person.fromJSON(json)
}
```

- 동반 객체 안에서 fromJSON 함수를 정의한 것처럼 fromJSON을 호출 가능
- 실제로는 클래스 밖에서 정의한 확장 함수

### 4.4.4 객체 식: 익명 내부 클래스를 다른 방식으로 작성

- `anonymous object` : 익명 클래스로부터 생성되는 객체
- `anonymous class` : 다른 클래스들과 달리 이름을 가지지 않는 클래스

#### anonymous class 사용 이유

ref) [Kotlin object 이용한 무명 객체(Anonymous Object) 생성 : 익명 클래스(Anonymous Class)의 구현과 활용 방법](https://kotlinworld.com/225)

- 무명 객체 정의할 때도 object 키워드 사용
- 자바와 달리 여러 인터페이스를 구현하거나 클래스를 확장하면서 인터페이스 구현 가능

```kotlin
val listener = object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {...}
    override fun mouseEntered(e: MouseEvent) {...}
}
```

##### 무명 객체 안에서 로컬 변수 사용하기

```kotlin
fun countClicks(window: Window) {
    var clickCount = 0  // 로켤 변수 정의
    window.addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            clickCount++    // 로컬 변수의 값을 변경
        }
    })
}
```

## 4.5 부가 비용 없이 타입 안정성 추가: 인라인 클래스

```kotlin
@JvmInline
value class UsdCent(val amount: Int)
```

- 다른 변수와 혼동되지 않기 위해 값을 래핑할 수 있는 클래스
- value로 선언함으로써 컴파일러가 객체를 생성하지 않고 값 mapping

ref) [[Kotlin] inline class](https://velog.io/@evergreen_tree/Kotlin-inline-class)