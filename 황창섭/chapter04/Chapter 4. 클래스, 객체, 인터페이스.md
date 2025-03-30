# Chapter 4. 클래스, 객체, 인터페이스

### 4장 요약
- 클래스와 인터페이스
- 뻔하지 않은 생성자와 프로퍼티
- 데이터 클래스
- 클래스 위임
- object 키워드 사용

## 4.1 클래스 계층 정의

### 4.1.1 코틀린 인터페이스

```kotlin
/**
 * 간단한 인터페이스 선언하기
 */
interface Clickable { 
    fun click()
}
```

```kotlin
class Button : Clickable { 
    override fun click() = println("I was clicked")
}
```

- 코틀린에서 extends, implement 는 모두 클래스 이름 뒤에 콜론을 붙이고 이름을 적음
- 상속과 구현의 원칙은 자바와 동일
- 코틀린에서 **override** 키워드는 필수적

```kotlin
/**
 * 인터페이스 디폴트 구현 방법
 */
interface Clickable { 
    fun click()
    
    fun showOff() = println("I'm clickable!")
}
```

```kotlin
/**
 * 같은 메서드를 구현하는 다른 인터페이스 정의
 */
interface Focusable {
    fun setFocus(b: Boolean) = println()
    
    fun showOff() = println("I'm focusable!")
}
```

- 한 클래스에서 위 두 인터페이스를 함께 구현하면 어떤 showOff 를 사용할지 모르기에 컴파일러 오류가 발생
- 두 메서드를 아루느는 구현을 하위 클래스에서 직접 구현이 필요하다

```kotlin
/**
 * 상속한 인터페이스의 메서드 구현 호출하기
 */
class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")
    
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}
```

- 상속한 구현 중 단 하나만 호출할 필요가 있다면 `override fun showOff() = super<Clickable>.showOff()` 이런 형태로 사용

### 4.1.2 open, final, abstract 변경자: 기본적으로 final

- 기본적으로 코틀린 클래스에 대해 하위 클래스를 만들 수 없고, 기반 클래스의 메서드를 하위 클래스가 오버라이드 할 수 없음 -> 모든 클래스와 메서드가 기본적으로 final이기 때문
- 취약한 기반 클래스(fragile base class)
  - 정의: 기반 클래스 구현을 변경함으로써 하위 클래스가 잘못된 동작을 하게 되는 경우
  - 특정 클래스가 자신을 상속하는 방법에 대해 정확한 규칙을 제공하지 않으면 하위 클래스가 기반 클래스를 생성한 사람의 의도와 맞지 않게 오버라이드 할 위험이 있음
  - 이로 인해 기반 클래스를 변경하는 경우 하위 클래스의 동작이 예기치 않게 바뀔 수도 있다는 면에서 '취약'하다는 표현 사용
  - 해결방법: 이펙티브 자바 -> "상속을 위한 설계와 문서를 갖춰라. 그럴 수 없다면 상속을 금지하라". 이는 즉 하위 클래스에서 오버라이드 하도록 의도되지 않았다면 모두 final로 만들라는 의미
- 클래스 상속을 허용하거나 메서드 오버라이드를 허용하려면 open 변경자를 붙여야 함

```kotlin
/**
 * 열린 메서드를 포함하는 열린 클래스 정의하기
 */
open class RichButton: Clickable {
    fun disable() { /*......*/ } // 오버라이드 불가능
    open fun animate() { /*......*/ } // 오버라이드 가능
    override fun click() { /*......*/ } // 오버라이드 가능
}
```
- 기반 클래스나 인터페이스의 멤버를 오버라이드 한 경우 기본적으로 **open**으로 간주
- 이를 막으려면 final 키워드를 별도로 붙여야 함. `final override fun click() { /*......*/ }`

- 추상 클래스
  - abstract class 는 기본적으로 open
  - abstract method 는 기본적으로 open
  - 구현된 프로퍼티와 메서드는 open이 필요함

```kotlin
abstract class Animated {
    abstract val anmationSpeed: Double
    val keyframes: Int = 20
    open val frames: Int = 60
    
    abstract fun animate()
    open fun stopAnimating() { /* ... */ }
    fun animateTwice() { /* ... */ }
}
```

### 4.1.3 가시성 변경자: 기본적으로 공개

- 코틀린은 public, protected, private 변경자를 제공하고 기본적인 역할은 자바와 동일
- 코틀린의 기본은 모두 공개 public 이다
- 최상위 선언의 경우 조금 다름

| 변경자            | 클래스 멤버              | 최상위 선언             |
|----------------|---------------------|--------------------|
| public(기본 가시성) | 모든 곳에서 볼 수 있다.      | 모든 곳에서 볼 수 있다.     |
| internal       | 같은 모듈 안에서만 볼 수 있다.  | 같은 모듈 안에서만 볼 수 있다. |
| protected      | 하위 클래스 안에서만 볼 수 있다. | (적용 불가능)           |
| private        | 같은 클래스 안에서만 볼 수 있따. | 같은 파일 안에서만 볼 수 있다. |

- 단, 코틀린에서 외부 클래스가 내부 클래스의 private 멤버에 접근할 수 없음

### 4.1.4 내부 클래스와 내포된 클래스: 기본적으로 내포 클래스

```kotlin
/**
 * 직렬화 할 수 있는 상태가 있는 뷰 선언
 */
interface State: Serializable

interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) { /*....*/ }
}
```

```java
/**
 * 자바에서 내부 클래스를 사용해 View 구현하기
 */
public class Button implements View {
    
    @Override
    public State getCurrentState() {
        return new ButtonState();
    }
    
    @Override
    public void restoreState(State state) { /*....*/ }
    
    public static class ButtonState implements State { /*....*/ }
}
```

```kotlin
/**
 * 내포 클래스를 사용해 코틀린에서 View 구현하기
 */
class Button: View {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) { /*....*/ }
    class ButtonState : State { /*....*/ }    
}
```

### 4.1.5 sealed 클래스: 확장이 제한된 클래스 계층 정의

```kotlin
/**
 * 인터페이스 구현을 통해 식 표현하기
 */
interface Expr
class Num(val value: Int): Expr
class Sum(val left: Expr, val right: Expr): Expr

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
        else -> throw IllegalArgumentException("Unknown expression")
    }
```

- when 에서 Expr 타입을 검사할 때 디폴트 분기가 필수적 -> 하지만 이는 항상 편하지 않음
- 디폴트 분기가 있으면 신규 하위 클래스가 추가되더라도 when이 모든 경우를 처리하는지 제대로 검사를 할 수 없음 -> 버그 가능성 유발
- 이를 해결할 방법이 **sealed 클래스**
- sealed 클래스의 직접적인 하위 클래스들은 컴파일 시점에 알려지고, 같은 패키지에 속하고, 같은 모듈 안에 위치해야 함

```kotlin
/**
 * sealed 클래스로 식 표현하기
 */
sealed class Expr
class Num(val value: Int): Expr()
class Sum(val left: Expr, val right: Expr): Expr()

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
    }
```

- sealed 클래스의 상속 계층에 새로운 하위 클래스를 추가하면 when 식이 컴파일이 되지 않고 변경해야하는 코드를 알려줌
- 클래스가 아니라 interface 앞에 sealed 변경자를 붙일 수 있음

## 4.2 뻔하지 않은 생성자나 프로퍼티를 갖는 클래스 선언

- 코틀린은 주 생성자(primary constructor)와 부 생성자(secondary constructor)를 구분함
- 코틀린에서는 초기화 블록을 통해 초기화 로직을 추가할 수 있음

### 4.2.1 클래스 초기화: 주 생성자와 초기화 블록

- 간단한 클래스 선언 `class User(val nickname: String)`
  - 클래스 이름 뒤에 오는 괄호로 둘러싸인 코드를 **주 생성자** 라고 부른다

```kotlin
/**
 * 위의 코드를 풀어쓰면 아래와 같음
 */
class User constructor(_nickname: String) {
    val nickname: String
    
    init {
        nickname = _nickname
    }
}
```

- init 키워드가 **초기화 블록**을 시작함

```kotlin
/**
 * 주 생성자에 디폴트 값 추가
 */
class User(val nickname: String,
    val isSubscribed: Boolean = true)
```

- 클래스 인스턴스를 만드려면 new 와 같은 키워드 없이 생성자를 직접 호출해서 사용

```kotlin
/**
 * 다양한 인스턴스 생성 방법
 */
fun main() {
    val alice = User("Alice")
    val bob = User("Bob", false)
    val carol = User("Carol", isSubscribed = false)
    val dave = User(nickname = "Dave", isSubscribed = true)
}
```

- 기반 클래스는 다음과 같이 초기화
```kotlin
open class User(val nickname: String) { /* ... */ }

class SocialUser(nickname: String) : User(nickname) { /* ... */ }

// 클래스를 정의할 때 별도로 생성자를 정의하지 않은 경우 인자가 없는 디폴트 생성자를 만듬
open class Button

class RadioButton: Button() // 이렇게 빈 괄호 이용
```

- 인터페이스는 생성자가 없기 때문에 괄호가 필요 없음. 이를 통해 쉽게 인터페이스와 클래스 구분 가능

```kotlin
/**
 * 클래스 외부에서 인스턴스화하지 못하게 막기
 */
class Secretive private constructor(private val agentName: String) {}
```

### 4.2.2 부 생성자: 상위 클래스를 다른 방식으로 초기화

- 코틀린에서 생성자가 여럿 있는 경우는 자바보다 적음 -> 자바에서 오버로드 생성자가 필요한 상황 중 상당수는 **디폴트 파라미터 값과 이름 붙은 인자 문법을 사용해서 해결**할 수 있으므로
- 여러 가지 방법으로 인스턴스를 초기화할 수 있어야 할 때 코틀린에서도 필요

```java
/**
 * 여러 가지 방법으로 인스턴스를 초기화 제공 자바 예제
 */
public class Downloader {
    public Downloader(String url) {
        
    }
    
    public Downloader(URI uri) {
        
    }
}
```

```kotlin
/**
 * 여러 가지 방법으로 인스턴스를 초기화 제공 코틀린 예제
 */
open class Downloader {
    constructor(url: String?) {
        
    }
  
    constructor(uri: URI?) {
        
    }
}
```
- 위 클래스는 주 생성자를 선언하지 않고 부 생성자만 2가지 선언

```kotlin
/**
 * 여러 가지 방법으로 인스턴스를 초기화 제공 코틀린 예제
 */
class MyDownloader : Downloader {
    constructor(url: String?) : this(URI(url))
  
    constructor(uri: URI?) : super(uri)
}
```

### 4.2.3 인터페이스에 선언도니 프로퍼티 구현

- 코틀린에서는 인터페이스에 **추상 프로퍼티** 선언을 넣을 수 있다. -> `interface User { val nickname: String }`
- 이는 User 인터페이스를 구현하는 클래스가 nickname 의 값을 얻을 수 있는 방법을 제공해야한다는 뜻

```kotlin
/**
 * 인터페이스의 프로퍼티 구현하기
 */
class PrivateUser(override val nickname: String) : User // 주 생성자에 있는 프로퍼티

class SubscribingUser(val email: String) : User {
    override val nickname: String
      get() = email.substringBefore('@') // 커스텀 게터
}

fun getNameFromSocialNetwork(accountId: Int) = "kodee$accountId"

class SocialUser(val accountId: Int) : User {
    override val nickname = getNameFromSocialNetwork(accountId) // 프로퍼티 초기화 식
}
```

> **언제 함수 대신 프로퍼티를 사용할까?**
> 
> 클래스의 특성은 프로퍼티, 클래스의 행동은 메서드로 선언
> 
> 또는 코드가 다음을 만족할 경우 사용
> 
> 1. 예외를 던지지 않는다 
> 2. 계산 비용이 적게 든다 
> 3. 객체 상태가 바뀌지 않으면 여러 번 호출해도 항상 같은 결과를 반환

## 4.3 컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임

- 자바에서 제공하는 기계적으로 구현할 수 있는 메서드들(equals, hashCode, toString 등)
- 코틀린에서는 생성자나 프로퍼티 접근자와 마찬가지로 자동으로 생성함

### 4.3.1 모든 클래스가 정의해야 하는 메서드

```kotlin
/**
 * Customer 클래스의 초기 정의
 */
class Customer(val name: String, val postalCode: Int)
```

- toString: 디버깅, 로깅 시 자주 사용
- 객체의 문자열 표현은 Customer@5e9f23b4 와 같은 클래스명+객체주소 로 표현되어 그다지 유용하지 못함
```kotlin
/**
 * Customer toString() 구현
 */
class Customer(val name: String, val postalCode: Int) {
    override fun toString() = "Customer(name=$name, postalCode=$postalCode)"
}
```

- equals: 객체의 동등성

> 코틀린에서는 참조 타입도 동등성 비교시 == 사용
> 
> 동등성 비교 ==, !=
> 
> 참조값 비교 ===, !==

```kotlin
/**
 * Customer equals() 구현
 */
class Customer(val name: String, val postalCode: Int) {
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Customer)
            return false
      
        return name == other.name && postalCode = other.postalCode
    }
}
```

- hasCode: equals()가 true를 반환하는 두 객체는 반드시 같은 hashCode()를 반환해야 함
```kotlin
/**
 * Customer hasCode() 구현
 */
class Customer(val name: String, val postalCode: Int) {
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode
}
```
- 코틀린에서는 이 모든걸 자동으로 생성해줌

### 4.3.2 데이터 클래스: 모든 클래스가 정의해야 하는 메서드를 자동으로 생성

```kotlin
/**
 * Customer를 데이터 클래스로 선언하기
 */
data class Customer(val name: String, val postalCode: Int)
```

- 인스턴스 간 비교를 위한 equals
- 해시 기반 컨테이너에서 키로 사용할 수 있는 hashCode
- 클래스의 각 필드를 선언 순서대로 표시하는 문자열 표현을 만들어 주는 toString
- 위 3개를 자동으로 생성

### 데이터 클래스: copy() 메서드

- 복사를 하면서 일부 프로퍼티 값만 바꿀 수 있는 편의 메서드 제공

```kotlin
/**
 * copy를 직접 구현하는 모습
 */
class Customer(val name: String, val postalCode: Int) {
    
  fun copy(name: String = this.name, postalCode: Int = this.postalCode) = Customer(name, postalCode)
}

/**
 * 사용
 */
fun main() {
    val lee = Customer("이", 4122)
    println(lee.copy(postalCode = 4000)) // Customer(name=이, postalCode=4000)
}
```

## 4.4 object 키워드: 클래스 선언과 인스턴스 생성을 한꺼번에 하기

- object 키워드를 사용하는 상황
  - 객체 선언(object declaration): 싱글턴을 정의하는 한 가지 방법
  - 동반 객체(companion object): 팩토리 메서드 등
  - 객체 식: 자바의 익명 내부 클래스 대신 사용

### 4.4.1 객체 선언: 싱글턴을 쉽게 만들기

```kotlin
/**
 * 싱글턴 예제
 */
object Payroll {
    val allEmployees = arrayListOf<Person>()
  
  fun calculateSalary() {
      for(person in allEmployees) {
          /* ... */
      }
  }
}
```

- 위와 같이 사용하면 생성자를 사용할 수 없음

```kotlin
/**
 * 객체 선언을 사용해 Comparator 구현하기
 */
object CaseInsensitiveFileComparator: Comparable<File> {
  override fun compare(file1: File, file2: File): Int {
      return file1.path.compareTo(file2.path, ignoreCase = true)
  }
}
```

### 4.4.2 동반 객체: 팩토리 메서드와 정적 멤버가 들어갈 장소

- 코틀린 클래스 안에는 static 멤버가 없지만, 패키지 수준의 최상위 함수와 객체 선언을 활용하면 된다.
- 대부분의 경우 최상위 함수를 활용하지만 private인 비공개 멤버 접근을 위해 object를 활용할 수 있다.
- 참고로 private인 비공개 멤버를 접근하는 대표 케이스는 팩토리 메서드

```kotlin
/**
 * 클래스의 인스턴스와 관계없이 호출해야 하지만, 클래스 내부 정보에 접근하는 함수가 필요한 케이스
 * companion 키워드 사용
 */
class MyClass {
    companion object {
      fun callMe() {
          println("Companion object called")
      }
    }
}
```

```kotlin
/**
 * 부 생성자가 여럿 있는 클래스
 */
class User {
  val nickname: String
  
  constructor(email: String) {
      nickname = email.substringBefore('@')
  }
  
  constructor(socialAccountId: Int) {
      nickname = getNameFromSocialNetwork(socialAccountId) // 프로퍼티 초기화 식
  }
}
```

```kotlin
/**
 * 부 생성자를 팩토리 메서드로 대체
 */
class User private constructor(val nickname: String){
    companion object {
      fun newSubscribingUser(email: String) = User(email.substringBefore('@'))
      
      fun newSocialUser(accountId: Int) = User(getNameFromSocialNetwork(socialAccountId))
    }
}

/**
 * 사용
 */
fun main() {
    val subscribingUser = User.newSubscribingUser("bob@gmail.com")
  
    val socialUser = User.newSocialUser(4)
}
```

### 4.4.3 동반 객체를 일반 객체처럼 사용

```kotlin
/**
 * 동반 객체에 이름 붙이기
 */
class Person (val name: String){
  companion object Loader {
    fun fromJSON(jsonText: String): Person = /* ... */
  }
}

fun main() {
    val person = Person.Loader.fromJSON("""{"name": "B"} """)
    val person2 = Person.fromJSON("""{"name": "B"} """)
}
```

### 4.4.4 객체 식: 익명 내부 클래스를 다른 방식으로 작성
```kotlin
interface MouseListener { 
    fun onEnter()
    fun onClick()
}

class Button(private val listener: MouseListener)

/**
 * 익명 객체로 이벤트 리스터 구현하기
 */
fun main() {
    Button(object: MouseListener {
      override fun onEnter() { /* ... */ }
      override fun onClick() { /* ... */ }
    })
}
```

- 자바와 같이 객체 식 안의 코드도 그 식이 포함된 함수의 변수에 접근 가능
- 하지만 자바와 달리 final이 아닌 변수도 사용할 수 있음

```kotlin
/**
 * 익명 객체 안에서 로컬 변수 사용하기
 */
fun main() {
    var clickCount = 0
    Button(object: MouseListener {
      override fun onEnter() { /* ... */ }
      override fun onClick() { 
          clickCount++ // 이것이 가능
      }
    })
}
```

> 자바와 달리 코틀린의 익명 함수에서는 `final`이 아닌 변수도 사용할 수 있는 이유 
> 
> 자바의 람다는 변수를 **값으로 복사**하여 캡처하기 때문에 변경할 수 없도록 `final` 또는 **effectively final** 제한이 필요하다.
> 
> 반면, 코틀린에서는 **클로저(Closure)**를 활용해 변수 자체를 **객체(Reference 타입)**로 감싸기 때문에 익명 객체 내부에서도 사용이 가능.

## 4.5 부가 비용 없이 타입 안정성 추가: 인라인 클래스

- 인라인 클래스를 사용하면 수명이 짧은 객체를 많이 할당함으로써 발생할 수 있는 성능 저하를 피하면서도 프로그램에 새로운 타입 안정성 계층을 추가할 수 있다.

```kotlin
/**
 * 인라인 클래스 정의
 */
@JvmInline
value class UsdCent(val amount: Int)
```