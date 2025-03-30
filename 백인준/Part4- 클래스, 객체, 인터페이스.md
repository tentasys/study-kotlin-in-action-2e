# 📚 4장 클래스, 객체, 인터페이스

- 코틀린의 클래스, 인터페이스는 자바의 생성자와는 다르다.
    - 인터페이스에 프로퍼티 선언이 들어갈수 있다.
    - 기본적으로 final 이며 public 이다.
- 내포 클래스는 기본적으로 내부 클래스가 아니다.
- 코틀린 컴파일러는 유용한 메서드를 자동으로 생성, 클래스를 data 로 선언시 컴파일러가 일부 표준 메서드를 생성해 준다.

___

## 📖 4.1 클래스 계층 정의

- 코틀린 가시성/접근 변경자는 자바와 비슷하지만 아무것도 지정하지 않은 경우의 기본 가시성은 다르다.
- sealed는 클래스 상속이나 인터페이스 구현을 제한 한다.

### 🔖 4.1.1 코틀린 인터페이스

- 추상 메서드 뿐만 아니라 구현 메서드도 정의 할수 있다.
- 인터페이스에는 아무런 상태도 들어갈 수 없다.

```kotlin
interface Clickable {
    fun click()
}

class Button : Clickable {
    override fun click() {
        println("click")
    }
}
```

- 코틀린에서는 상속이나 구성에서 모두 클래스 이름뒤에 콜론을 붙이고 인터페이스나 클래스를 적음
- 클래스는 인터페이스를 원하는 만큼 구현할수 있지만 클래스는 오직 하나만 확장 가능
- 코틀린에서는 메서드 오버라이드시 반드시 override를 붙여야 한다.
    - 실수로 상위 메서드를 오버라드 하는 경우를 방지

```kotlin
interface Clickable {
    fun click()
    fun showOff() = println("show off") // 디폴트 구현이 있는 메서드
}
interface Focusable {
    fun showOff() = println("show off 2")
}

class Button : Clickable, Focusable {
    override fun click() {
        println("click")
    }
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}
```

- 같은 메서드가 있는 두 인터페이스를 함께 구현하면 컴파일 오류 발생
- super를 사용하여 상위 타입의 이름을 넣는다.

### 🔖 4.1.2 open, final, abstract 변경자 : 기본적으로 final

- 코틀린에서 모든 클래스와 메서드는 기본적으로 final 이다.
    - 자바와 다름 자바는 명시적으로 final 을 붙이지 않는한 모든 메서드를 오버라이드 할수 있다.
- 취약한 기반 클래스 라는 문제 발생
    - 기반 클래스 구현을 변경함으로써 하위 클래스가 잘못된 동작을 하게 되는 경우를 뜻한다.
- 이펙티브 자바에서는 상속을 위한 설계와 문서를 갖춰라 그럴수 없다면 상속을 금지하게 한다. (코틀린도 마찬가지로 같은 철학을 따름)
- 어떤 클래스의 상속을 허용하려면 클래스 앞에 open 변경자를 붙인다.(메서드나 프로퍼티도 동일)

```kotlin
interface Clickable {
    fun click()
    fun showOff() = println("show off") // 디폴트 구현이 있는 메서드
}
open class RichButton : Clickable {
    fun disable() {} // 상속 못함
    open fun animate() {} // 상속 가능 
    override fun click() {}
}

class ThemedButton : RichButton() {
    override fun animate() {}
    override fun click() {
        super.click()
    }
    override fun showOff() {
        super.showOff()
    }
}

open class RichButton : Clickable {
    final override fun click() {} // 오버라이드 금지 
}
```

- abstract 로 선언한 추상 클래스는 인스턴스화 할수 없다.
- 추상 클래스에는 구현이 없어 하위 클래스에서 오버라이드해야만 하는 추상 멤버가 있는것이 보통이므로 추상멤버는 항상열려있어 open 을 붙일 필요가 없다.

```kotlin
abstract class Animated {
    abstract val animationSpeed: Double // 추상 프로퍼티 값이 없어 클래스는 반드시 값이나 접근자를 제공해야함
}
```

- 인터페이스의 멤버의 경우 final, open, abstarct 를 사용하지 않는다.
    - 항상 open 이며 final 로 변경불가능

- final
    - 오버라이드 불가능
    - 클래스 멤버의 기본 변경자
- open
    - 오버라이드 가능
    - 반드시 open 을 명시해야 오버라이드 가능
- abstract
    - 반드시 오버라이드 해야함
    - 추상 클래스의 멤버에만 이 변경자를 붙일 수 있고 추상 멤버에는 구현이 있으면 안된다.
- override
    - 상위 클래스나 인스턴스의 멤버를 오버라이드 하는 중
    - 오버라이드 멤버는 기본적으로 열려있고 하위 클래스의 오버라이드를 금지하려면 final 을 붙여야함

### 🔖 4.1.3 가시성 변경자 : 기본적으로 공개

- 가시성 변경자는 코드 기반에 있는 선언에 대한 클래스 외부 접근을 제어
    - 구현에 대한 접근을 제어 함으로 클래스에 의존하는 외부 코드를 깨지않고 클래스 내부 구현을 변경할수 있음
- 코틀린에서는 아무 변경자도 없는 선언은 모두 public 이다.
- internal 은 모듈 안으로만 제한
- 최상위 선언에 대해 private 가시성 허용
  ![Image](https://github.com/user-attachments/assets/663f4424-d225-4a65-a800-5ad14fbc8d2a)

```kotlin
internal open class TalkativeButton : Focusable {
    private fun yell() = println("Hey~")
    protected fun wishper() = println("Let's talk!")
}

fun TalkativeButton.giveSpeech() {
    yell()

    whisper()
}
```

- 자바는 같은 패키지 안에서 protected 멤버에 접근할수 있지만 코틀린은 그렇지 않다.
- 코틀린의 protected 멤버는 오직 어떤 클래스나 그 클래스를 상속한 클래스 안에서만 보인다.

### 🔖 4.1.4 내부 클래스와 내포된 클래스: 기본적으로 내포 클래스

- 코틀린은 내포 클래스를 명시적으로 요청 하지 않는 한 바깥쪽 클래스 인스턴스에 대한 접근 권한이 없다.

```kotlin
interface State : Serializable

interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) {}
}

class Button : View {
    override fun getCurrentState(): State = ButtonState()

    override fun restoreState(state: State) {
        ...
    }

    class ButtonState : State {
        ...
    }
    // 내부 클래스
    inner ButtonState: State
    {

    }
```

- 자바에서 내포 클래스를 static 으로 선언하면 그 클래스를 둘러싼 바깥쪽 클래스에 대한 암시적인 참조가 사라진다.
- 코틀린에서 내포된 클래스에 아무런 변경자도 없으면 자바 static 내포 클래스와 같다
- 이를 내부 클래스로 변경해서 바깥쪽 클래스에 대한 참조를 포함하게 만들고 싶다면 inner 변경자를 붙여야 한다.
  ![Image](https://github.com/user-attachments/assets/40748960-f094-4570-b95e-b6df5a39da1f)
- 내부 클래스 inner 안에서 바깥쪽 클래스 Outer의 참조에 접근하려면 this@Outer 표기

### 🔖 4.1.5 봉인된 클래스 : 확장이 제한된 클래스 계층 정의

- 상위 클래스에 sealed 변경자를 붙이면 그 상위 클래스를 상속한 하위 클래스의 가능성을 제한할 수 있다.
- sealed 클래스의 직접적인 하위 클래스들은 반드시 컴파일 시점에 알려져야 하며, 봉인된 클래스가 정의된 패키지와 같은 패키지에 속해야하며 모든 하위 클래스가 같은 모듈안에 위치해야한다.

```kotlin
sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int = // else 분기가 없어도 모든식을 검사함 
    when (e) {
        is Expr.Num -> e.value
        is Expr.Sum -> eval(e.left) + eval(e.right)
    }
```

- sealed 변경자는 클래스가 추상 클래스임을 명시한다는 점
- sealed 클래스에 abstract 를 붙일필요가 없고 추상 멤버를 선언할수 있다.

---

## 📖 4.2 뻔하지 않은 생성자나 프로퍼티를 갖는 클래스 선언

- 코틀린은 주 생성자와 부 생성자를 구분한다.
- 초기화블록을 통해서 초기화 로직을 추가 할수 있다.

### 🔖 4.2.1 클래스 초기화: 주 생성자와 초기화 블록

```kotlin
class User constructor(val _nickname: String) { // _ 프로퍼티와 생성자 프로퍼티를 구분해준다.
    val nickname: String

    init {
        nickname = _nickname
    }
}
```

- 클래스 이름뒤에 오는 괄호로 둘러 싸인 코드를 주 생성자라고 한다.
- constructor
    - 주 생성자나 부 생성자 정의를 시작할때 사용한다.
- init 키워드는 초기화 블록을 시작 - 클래스의 객체가 만들어질 때 실행될 초기화 코드가 들어간다.

```kotlin
class User(val nickname: String) // val 은 파라미터에 상응하는 프로퍼티가 생성된다는 뜻

open class User(val name: String) { ... }

class TwitterUser(name: String) : User(name) {
    ...
}
```

- 주 생성자의 파라미터로 프로퍼티를 초기화한다면 그 주 생성자 파라미터 이름 앞에 val을 추가하는 방식으로 프로퍼티 정의와 초기화를 간략히 쓸 수 있다.
- 함수 파라미터와 마찬가지로 생성자 파라미터에도 기본값을 정의할수 있다.
- 클래스를 정의할때 별도로 생성자를 정의하지 않으면 컴파일러가 자동으로 아무일도 하지않는 디폴트 생성자 생성

```kotlin
class Secretive private constructor(private val agentName: String) {}
```

- 어떤 클래스를 외부에서 인스턴스화 하지 못하게 막고싶을때 생성자에 private 을 붙이면된다.

### 🔖 4.2.2 부 생성자 : 상위 클래스를 다른 방식으로 초기화

- 코틀린은 디폴트 파라미터 값과 이름 붙은 인자 문법을 사용해 해결

#### 인자에 대한 기본값을 제공하기 위해 부 생성자를 여럿 만들지 말라. 대신 파라미터의 기본값을 생성자 시그니처에 직접 명시하라

```kotlin
import java.net.URI

open class Downloader {
    constructor(url: String?) {}
    constructor(url: URI?) {}
}

class MyDownloader : Downloader {
    constructor(url: String?) : super(url)
    constructor(url: URI?) : super(url)
}
```

- 클래스에 주 생성자가 없다면 모든 부 생성자는 반드시 상위 클래스를 초기화하거나 다른 생성자에게 생성을 위임해야 한다.

### 🔖 4.2.3 인퍼페이스에 선언된 프로퍼티 구현

- 인터페이스에 추상 프로퍼티 선언을 넣을수 있음

```kotlin
interface User {
    val nickname: String
}

class PrivateUser(override val nickname: String) : User // 주생성자에 있는 프로퍼티 
class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore("@")
}
class socialUser(val accountId: Int) : User {
    override val nickname: String = getNameFromSocialNetwork(accountId) // 프로퍼티 초기화식 
}

fun getNameFromSocialNetwork(accountId: Int) = "kodee$accountId"
```

- 인터페이스에 추상 프로퍼티뿐 아니라 케터와 세터가 있는 프로퍼티를 선언할수 있다.
    - 물론 그런 게터와 세터는 뒷받침하는 필드를 참조할수 없음
- 인터페이스에 선언된 프로퍼티와 달리 클래스에 구현된 프로퍼티는 뒷받침하는 필드를 원하는대로 사용가능

### 🔖 4.2.4 게터와 세터에서 뒷받침하는 필드에 접근

```kotlin
class User(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println(
                """
                Address was changed for $name:
                "$field" -> "$value".""".trimIndent()
            )
            field = value
        }
}

fun main(args: Array<String>) {
    val user = User("Alice")
    user.address = "Elsenheimerstrasse 47, 80687 Muenchen"
}
```

- 게터에서는 field 값을 읽을수만 있고 세터에서는 field 값을 읽거나 쓸수 있다.

### 🔖 4.2.5 접근자의 가시성 변경

```kotlin
class LengthCounter {
    var counter: Int = 0
        private set

    fun addWord(word: String) {
        counter += word.length
    }
}

fun main(args: Array<String>) {
    val lengthCounter = LengthCounter()
    lengthCounter.addWord("Hi!")
    println(lengthCounter.counter)
}
```

- 기본 가시성을 가지는 getter를 컴파일러가 생성하게 냅두고 setter의 가시성을 private으로 지정하여 외부 코드에서 단어 길이의 합을 마음대로 바꾸지 못하게 하였다.

--- 

## 📖 4.3 컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임

### 🔖 4.3.1 모든 클래스가 정의해야 하는 메서드

- 자바와 마찬가지로 코틀린 클래스도 toString, equals, hashCode 등을 오버라이드 해야한다.
- 코틀린에서는 이런코드를 자동으로 생성 해준다.

### 🔖 4.3.2 데이터 클래스: 모든 클래스가 정의해야 하는 메서드를 자동으로 생성

- 어떤 클래스가 데이터를 저장하는 역할만을 수행한다면 toString, equals , hashCode 를 반드시 오버라이드 해야만 한다.
- 코틀린은 data 라는 키워드를 붙이면 컴파일러가 자동으로 만들어줌
- 이런 클래스를 데이터 클래스라고 한다.

```kotlin
data class Customer(val name: String, val postalCode: Int)
```

#### 데이터 클래스와 불변성: copy() 메서드

- 데이터 클래스의 프로퍼티가 모두 val일 필요는 없다. var여도 된다.
- 하지만 데이터 클래스의 모든 프로퍼티를 읽기 전용으로 만들어 불변 클래스로 만들라고 권장한다.
- 불변의 장점 : 다중 스레드에서 동기화를 고려하지 않아도 됨.
- Copy() : 객체를 복사하면서 일부 프로퍼티를 바꿀 수 있게 해준다.
- 객체를 메모리 상에서 직접 바꾸는 대신 복사본을 만드는 편이 더 낫다. 복사본은 원본과 다른 생명주기를 가지며, 복사를 하면서 일부 프로퍼티 값을 바꾸거나 복사본을 제거해도 프로그램에서 원본을 참조하는 다른
  부분에 전혀 영향을 끼치지 않는다.

#### 클래스 위임 : by 키워드 사용

- 인터페이스를 구현할 때, by 키워드를 통해 그 인터페이스에 대한 구현을 다른 객체에 위임 중이라는 사실을 명시할 수 있다.

```kotlin
  class DelegatingCollection<T>(innerList: Collection<T> = mutableListOf<T>()) : Collection<T> by innerList {

}
```

- Collection의 구현을 innerList에게 위임한다.
- 메소드 중 일부의 동작을 변경하고 싶을 때는 메소드를 오버라이드 하면 컴파일러가 오버라이드한 메소드를 쓴다.

---

## 📖 4.4 Object 키워드: 클래스 선언과 인스턴스 생성을 한꺼번에 하기

- 클래스를 정의하는 동시에 인스턴스를 생성한다는 공통점이 있다.

- 객체 선언: 싱글턴을 정의하는 한가지 방법
- 동반 객체: 어떤 클래스와 관련이 있지만 호출하기 위해 그 클래스의 객체가 필요하지는 않은 메서드와 팩토리 메서드를 담을때 사용
  동반 객체의 멤버에 접근할 때는 동반객체가 포함된 클래스의 이름을 사용한다.
- 객체 식: 자바의 익명 내부 클래스 대신 쓰인다.

### 🔖 4.4.1 객체 선언 : 싱글턴 쉽게 만들기

- 자바에서는 보통 클래스의 생성자를 private 으로 제한하고 정적인 필드에 그 클래스의 유일한 객체를 저장하는 싱글턴 패턴을 통해 이를 구현
- 코틀린은 객체 선언 기능을 통해 싱글턴을 언어에서 기본 지원한다.

```kotlin
object Payroll {
    val allEmployees = arrayListOf<String>()
    fun calculateSalary() {
        for (person in allEmployees) {

        }
    }
}
```

- 객체 선언
    - object 로 시작
    - 클래스를 정의하고 그 클래스의 인스턴스를 만들어 변수에 저장하는 모든 작업을 한 문장으로 처리한다.
    - 생성자를 쓸 수 없음
    - 싱글턴 객체는 객체 선언 문이 있는 위치에서 생성자 호출 없이 즉시 만들어진다.
    - 클래스 안에 객체를 선언 가능 하고 그런 객체에서도 인스턴스는 단 하나뿐이다.

```kotlin
data class Person(val name: String) {
    object NameComparator : Comparable<Person> {
        override fun compare(p1: Person, p2: Person): Int = p1.name.compareTo(p2.name)
    }
}
```

### 🔖 4.4.2 동반 객체 : 팩토리 메서드와 정적 멤버가 들어갈 장소

- 코틀린 클래스 안에는 정적인 멤버가 없다.
- 자바 static 키워드를 지원하지 않고 그대신 패키지 수준의 최상위 함수와 객체 선언을 활용한다.
- 하지만 최상위 함수는 private 으로 표시된 메서드에 접근 불가능
- 팩토리 메서드는 객체 생성을 책임지기 떄문에 클래스의 private 멤버에 접근 할수 있어야 한다.
- 클래스 안에 정의된 객체 중 하나에 compainon 이라는 특별한 표시를 붙이면 객체 멤버에 접근시 자신을 감싸는 클래스의 이름을 통해 직접 사용가능하다.

```kotlin
class Myclass {
    companion object {
        fun callMe() {
            println("test")
        }
    }
}

fun main() {
    Myclass.callMe()
}
```

- 동반 객체는 자신을 둘러싼 클래스의 모든 private 멤버에 접근할 수 있다.

```kotlin
class User private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) {
            User(email.substringBefore("@"))
        }
    }
}

fun main() {
    User.newSubscribingUser("test")
}

```

- 팩토리 메서드는 생성할 필요가 없는 객체를 생성하지 않을수도 있다.

### 🔖 4.4.3 동반 객체를 일반 객체처럼 사용

- 동반 객체는 클래스 안에 정의된 일반 객체이므로 다른 객체 선언처럼 동반 객체에 이름을 붙이거나 , 동반 객체 인터페이스를 상속하거나 동반 객체안에 확장 함수와
  프로퍼티를 정의 할수 있다.

```kotlin
class Person(val name: String) {
    companion object Loader {
        fun fromJson(json: String): Person {
            ...
        }
    }
}

fun main(args: Array<String>) {
    Person.Loader.fromJson("{name: 'Lee'}")
    Person.fromJson("{name: 'Lee'}")
}
```

#### 동반 객체에서 인터페이스 구현

- 다른 객체 선언과 마찬가지로 동반 객체도 인터페이스 구현 가능
- 인터페이스를 구현하는 동반 객체를 참조할 때도 객체를 둘러싼 클래스 이름을 바로 사용할수 있다.

```kotlin
interface JSONFactory<T> {
    fun fromJSON(json: String): T
}

class Person(val name: String) {
    companion object : JSONFactory<Person> {
        override fun fromJSON(json: String): Person {
            return Person("Lee")
        }
    }
}

fun <T> loadFromJSON(factory: JSONFactory<T>): T? {
    return null
}

fun main() {
    loadFromJSON(Person)
}
```

- 동반 객체에 대한 확장 함수를 작성 할수 있으려면 원래 클래스에 동반 객체를 꼭 선언 해야한다.

### 🔖 4.4.4 객체 식: 익명 내부 클래스를 다른 방식으로 작성

- 익명 객체를 정의 할때도 object 키워드를 사용
- 자바의 익명 내부 클래스를 대신한다.

```kotlin
interface MouseListener {
    fun onEnter()
    fun onClick()
}
class Button(private val listener: MouseListener) {
    /**/
}

fun main() {
    Button(object : MouseListener {
        override fun onEnter() {
        }
        override fun onClick() {
        }
    })
    var listener = object : MouseListener {
        override fun onClick() {
        }
        override fun onEnter() {
        }
    }
}
```

- 객체 식은 클래스를 정의하고 그 클래스에 속한 인스턴스를 생성하지만 그 클래스나 인스턴스에 이름을 붙이지 않는다.
- 보통 함수를 호출 하면서 익명 객체를 넘기기 때문에 클래스의 인스턴스 모두 이름이 필요하지 않다.
- 하지만 객체에 이름을 붙이려면 변수에 익명 객체를 대입하면 된다.

### 🔖 4.4.5 부가 비용 없이 타입 안정성 추가: 인라인 클래스

```kotlin
fun addExpense(expense: Int) {

}
addExpense(200) //함수 만든 사람의 의도는 센트 하지만 엔으로 사용

class UsdCent(val amount: Int) // int 대신 클래스를 사용

fun addExpenseUsdCent(expense: UsdCent) {
    // 함수에 잘못된 의미의 값을 전달할 위험이 준다.
}
```

- 성능 상 고려할 점이 있음
- 함수를 호출 할떄마다 객체를 생성하고 버랴야 하여 가비지 컬렉터에 의해 제거해야 하는 객체를 수없이 만들게 된다.
- 이럴때 인라인 클래스를 사용하면 성능을 희생하지 않고 타입 안정성을 얻을수 있다.

```kotlin
@JvmInline
value class UsdCent(val amount: Int)
```

- 래퍼 타입이 제공 하는 타입 안전성을 포기하지 않으면서 불필요하게 객체를 생성하는 비용을 줄일수 있다.
- 실행시점에 인스턴스는 감싸진 프로퍼티로 대체된다.
- 클래스의 데이터가 사용되는 시점에 인라인됨
- 인라인으로 표시하려면 클래스가 프로퍼티를 하나만 가져야 하고 주 생성자에서 초기화 돼야 한다.
- 계층에 참여하지않고 다른 클래스를 상속할수 없고 다른 클래스가 인라인 클래스를 상속할수도 없다.
- 하지만 인터페이스를 상속하거나 메서드를 정의하거나 게산된 프로퍼티를 제공 할수 있다.

```kotlin
interface PrettyPrintable {
    fun prettyPrint()
}

@JvmInline
value class UsdCent(val amount: Int) : PrettyPrintable {
    val salesTax get() = amount * 0.06
    override fun prettyPrint() {}
}
```

- 기본 타입 값의 의미를 명확하게 하고 싶을 때 인라인 클래스를 사용