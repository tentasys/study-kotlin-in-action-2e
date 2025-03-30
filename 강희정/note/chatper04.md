# 4장. 클래스, 객체, 인터페이스

# 4.1 클래스 계층 정의

## 4.1.1 코틀린 인터페이스

- 추상 메서드 정의 가능
- 구현이 있는 메서드 정의 가능
- 상태는 들어갈 수 없음

인터페이스의 정의

```kotlin
interface Clickable {
		fun click()
}
```

인터페이스 구현

```kotlin
class Button: Clickable {
    override fun click() = println("I was Clicked")
}
```

- 뒤에 콜론을 붙여 구현
- 여러 개의 인터페이스 구현 가능 (클래스는 하나만 확장 가능)

자바에서는 @Override 선택 → 코틀린에서는 필수

→ 상위 메서드와 시그니처가 같은 메서드를 하위 클래스에서 선언할 경우 컴파일이 되지 않음

인터페이스에서는 디폴트 구현 제공

- 두 인터페이스가 같은 메서드 시그니처에 대한 디폴트 구현이 존재
- 그걸 구현하는 클래스 → 반드시 메서드 오버라이드 해야 함. 그렇지 않으면 컴파일 에러 발생
    - 자바에서도 똑같다.

```kotlin
class Button: Clickable, Focusable {
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
    // 생략
}
```

- super<상위클래스>로 어떤 타입의 상위 메서드를 호출할지 정할 수 있음

### 코틀린 인터페이스 default vs. 자바 인터페이스 default

코틀린 인터페이스 디폴트 구현 → 자바와 바이트코드 수준에서 다름

→ 자바 클래스에서는 코틀린의 모든 디폴트 메서드를 직접 다시 구현해야 함

자바

- 바이트코드 상 - JVM 수준 인터페이스 안에 디폴트 메서드로 존재

```kotlin
interface JavaInterface {
    default void sayHello() {
        System.out.println("Hello from Java");
    }
}
```

코틀린

- 인터페이스에는 본문 없는 메서드로 생성
- 정적 헬퍼 클래스를 만들고 여기에 구현이 들어감

자바 입장에서 코틀린 인터페이스 디폴트 메서드는 아래와 같이 보임

```kotlin
public interface KotlinInterface {
    void greet(); // 추상 메서드로만 보임
}
```

코틀린 → 자바로는 default 메서드 사용이 어렵지만

자바 → 코틀린은 사용 가능

- 코틀린은 JVM의 default method 지원함

### 그렇다면 왜? 이렇게 설계했을까?

Java → Kotlin은 쉽게 만들었으면서 Kotlin → Java는 왜 어렵게 했는지?

에 대해 궁금해서 GPT에 질문함..

- 이유 1. **Kotlin의 철학: 모든 함수는 기본적으로 멤버 함수가 아님**
    - Kotlin 인터페이스의 default 메서드도 단순한 구현이 아니라, **“함수와 구현을 분리하려는 의도”**가 있습니다.
- 이유 2. **JVM의 `default method`는 Java 8 이상에서만 지원**
    - Kotlin은 **Java 6, 7과도 호환되어야 했던 역사적 이유**가 있습니다.
    - JS, Native에서도 똑같이 동작할 수 있게 해줍니다.
- 이유 3. **명시적인 구현을 강제해서 명확한 설계를 유도**

이렇다고는 하는데.. 믿거나 말거나..

## 4.1.2 open, final, abstract 변경자: 기본적으로 final

코틀린에서의 모든 클래스와 메서드

- 기본적으로 final
- 상속을 하려면 open 키워드 필요

취약한 기반 클래스 문제

- 기반 클래스 구현을 변경함으로써 하위 클래스가 잘못된 동작을 하게 되는 경우

```kotlin
open class RichButton: Clickable {  // 열려 있는 클래스, 상속 가능
    fun disable() { /* ... */ }     // 파이널 메소드 -> 오버라이드 불가
    open fun animate() { /* ... */ }    // 오버라이드 가능 메서드
    override fun click() { /* ... */ }  // 열려 있는 메서드를 오버라이드하며 열려 있음
    final override fun doubleClick() { /* ... */ }  // 열려 있는 메서드를 오버라이드하며 하위에서 상속 불가하게 막음
}
```

final 키워드와 스마트 캐스트

- final은 스마트 캐스트를 가능하게 함
- 스마트 캐스트 → 타입 검사 뒤에 변경될 수 없는 변수에 적용 가능하기 때문
- 클래스 프로퍼티 → val이면서 커스텀 접근자가 없는 경우에만 사용 가능
    - 프로퍼티가 final이어야 한다

abstract 클래스

- 인스턴스화 불가
- 추상 멤버는 항상 열려 있음

## 4.1.3 가시성 변경자: 기본적으로 공개

클래스 외부 접근을 제어하는 역할

|  | 클래스 멤버 | 최상위 선언 |
| --- | --- | --- |
| public (기본 가시성) | 모든 곳 | 모든 곳 |
| internal | 같은 모듈 내 | 같은 모듈 내 |
| protected | 하위 클래스 안에서만 | 적용 불가능 |
| private | 같은 클래스 안에서만 | 같은 파일 내 |

모듈?

- 컴파일러가 하나의 컴파일 단위로 처리하는 코드 묶음
- internal 가시성의 범위를 결정하는 기본 단위
- 보통 하나의 Gradle subproject, Maven module, 또는 IntelliJ IDEA module이 모듈 1개

internal

- 개발자 입장에서 공개 API와 내부 구현을 나눌 수 있는 수단

클래스나 함수에서 사용되는 타입은 그것을 **사용하는 위치보다 가시성 수준이 같거나 더 public해야 한다**.

- internal 클래스는 public 함수의 시그니처에 등장할 수 없음
    
    ```kotlin
    internal open class InternalBase
    public class MyClass : InternalBase()  // 오류 발생
    ```
    
- private 타입은 internal, public 함수의 시그니처에 등장할 수 없음
- 반대로 public 타입은 어떤 곳에도 사용 가능 (가시성이 가장 넓기 때문)
- 이렇게 하지 않으면..
    - 외부에서 접근할 수 없는 타입을 포함한 함수나 클래스가 노출되고,
    - 사용자 입장에서 컴파일 에러 또는 모호한 API를 보게 됨

### 코틀린의 가시성 변경자와 자바

private

- 코틀린 private는 자바의 패키지 전용 클래스로 컴파일

internal

- 바이트코드상 public으로 됨
- 코틀린에서는 접근할 수 없는 대상을 자바에서 접근할 수 있는 경우가 생김

mangling

- 컴파일러가 함수나 클래스 등의 이름을 고유하게 바꾸는 과정
- internal 멤버가 공개될 경우 이름 충돌을 방지하기 위해 사용
- 그 외에 여러 과정에서 사용됨.. (충돌을 피하기 위함)

## 4.1.4 내부 클래스와 내포된 클래스: 기본적으로 내포 클래스

내포 클래스(Nested Class)

- 클래스 안에 클래스가 있음
- 코틀린의 기본 클래스 → static nested 클래스 → 바깥 클래스와 상관 없음

```kotlin
class Outer {
    class Nested {
        fun greet() = "Hello from Nested"
    }
}

fun main() {
    val nested = Outer.Nested()  // static하기에 바깥 클래스 인스턴스 없이 사용 가능
    println(nested.greet())
}
```

내부 클래스(Inner Class)

- 바깥 클래스를 참조할 때가 있을 때 사용
- inner를 붙이면 내부 클래스가 됨
- 내포 클래스가 static한 반면 내부 클래스는 static하지 않음

```kotlin
class Outer(val outerName: String) {
    inner class Inner {
        fun greet() = "Hello from Inner, outer name is $outerName"
    }
}

fun main() {
    val outer = Outer("Kotlin")
    val inner = outer.Inner()  // 바깥 클래스 인스턴스 필요
    println(inner.greet())     // outerName에 접근 가능
}
```

## 4.1.5 봉인된 클래스: 확장이 제한된 클래스 계층 정의

sealed class

- 상위 클래스를 상속한 하위 클래스의 가능성을 제한
- 하위 클래스들은 컴파일 시점에 알려져야 함
- 하위 클래스는 같은 패키지에 속해야 함, 같은 모듈 안에 위치해야 함
- 데이터 설계와 표현, 타입 안정성, 제한된 상속 구조를 표현할 때 유용함
- when 식에서 sealed class의 모든 하위 클래스를 처리한다면 else 분기가 필요 없음
- 추상 클래스임을 의미하므로 abstract를 붙일 필요가 없음

# 4.2 뻔하지 않은 생성자나 프로퍼티를 갖는 클래스 선언

## 4.2.1 클래스 초기화: 주 생성자와 초기화 블록

주 생성자(primary constructor)

- 클래스 이름 옆에 선언되는 기본 생성자

```kotlin
class Person(val name: String, var age: Int)
```

- init 블록을 통해 초기화 코드 작성 가능

```kotlin
class Person(val name: String, var age: Int) {
    init {
        println("Person created: $name, $age")
    }
}
```

## 4.2.2 부 생성자: 상위 클래스를 다른 방식으로 초기화

부 생성자(secondary constructor)

- 클래스 본문 안에 정의되는 보조 생성자

```kotlin
class Person {
    var name: String = "unknown"
    var age: Int = 0

    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }
}
```

super 키워드를 통해 상위 클래스 생성자 호출 가능

## 4.2.3 인터페이스에 선언된 프로퍼티 구현

```kotlin
interface User {
		val nickname: String
}
```

- 인터페이스를 구현하는 클래스가 프로퍼티의 값을 얻을 수 있는 방법을 제공해야 함
- 커스텀 게터, 초기화 식 등등

## 4.2.4 게터와 세터에서 backing field에 접근

어떤 값을 저장하되 그 값을 변경하거나 읽을 때 마다 정해진 로직을 실행하는 유형의 프로퍼티에 사용

```kotlin
class User(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("Address was changed for $name: $field -> $value")
            field = value
        }
}
```

- 프로퍼티에 저장된 값의 변경 이력을 로그에 남김

backing field

- 코틀린에서 프로퍼티를 선언하면 → 그 값을 저장하기 위한 내부 필드
- 프로퍼티의 값을 저장하기 위한 내부 변수
- 코틀린의 프로퍼티는 필드가 아닌 get/set 메서드를 통해 접근 → 값 저장은 backing field에 실질적으로 진행됨

## 4.2.5 접근자의 가시성 변경

기본적으로 프로퍼티의 가시성과 같음 → 다르게 설정할 수 있음

예) public 프로퍼티에 getter는 public, setter는 private

→ 그런데 그렇게 하면 그냥 val로 선언하는 것과 다를 점이 있을까?

- val
    - 한 번 초기화되면 절대 바뀌면 안 되는 값
- var + private set
    - 클래스 내부에서만 값이 바뀌고, 외부에서는 읽기만 허용해야 하는 값

# 4.3 컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임

## 4.3.1 모든 클래스가 정의해야 하는 메서드

toString()

- 클래스의 문자열 표현

equals()

- 객체의 동등성 검사
- ==는 객체의 동등성 검사 → equals 호출
- ===는 참조의 동일성 검사

hashCode()

- equals를 오버라이드하려면 hashCode도 반드시 오버라이드 해야 함
    - hashCode가 같으면 → 그 다음 equals가 같은지 비교하기 때문 (HashSet 비교)

copy()

- 객체를 복사하면서 일부 프로퍼티를 바꿀 수 있게 해 줌
- 불변성을 유지하게 해 줌

## 4.3.2 데이터 클래스: 모든 클래스가 정의해야 하는 메서드를 자동으로 생성

```kotlin
data class Customer(val name: String, val postalCode: Int)
```

- toString()
- equals()
- hashCode()
- copy()
- componentN() - 구조 분해 할당에 사용

Java의 Lombok @Data와 다른 점

- 자바에서는 getter/setter 생성
- copy, componentN 생성하지 않음
- NoArgsConstructor, AllArgsConstructor

Java의 record와 다른 점

- copy가 없음
- componentN 없음

데이터 클래스에서 생성되는 메서드들은 주 생성자에 선언된 프로퍼티만 대상으로 함

```kotlin
data class User(val name: String) {
    var age: Int = 0  // 주 생성자 밖에서 정의된 프로퍼티
}
```

- equals()는 name만을 비교 대상으로 삼는다.
- hashCode()도 name만 기준으로 생성된다.
- copy()도 name만 복사 대상

### 클래스 위임: by 키워드 사용

데코레이터 패턴

- 상속을 허용하지 않는 클래스에게 새로운 동작을 추가
- 기존 클래스와 같은 인터페이스를 데코레이더가 제공하고 기존 클래스를 데코레이터 내부 필드로 유지

```kotlin
class DelegatingCollection<T>: Collection<T> {
    private val innerList = arrayListOf<T>()
    override val size: Int get() = innerList.size
    override fun isEmpty(): Boolean = innerList.isEmpty()
    override fun iterator(): Iterator<T> = innerList.iterator()
    override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements)
    override fun contains(element: T): Boolean = innerList.contains(element)
}
```

복잡한 코드 → by 키워드 사용

```kotlin
class DelegatingCollection<T>: Collection<T> (
    innerList: Collection<T> = mutableListOf<T>()
): Collection<T> by innerList
```

- 동작을 변경하고 싶은 일부 메서드는 오버라이드 사용

# 4.4 object 키워드: 클래스 선언과 인스턴스 생성을 한꺼번에 하기

## 4.4.1 객체 선언: 싱글턴을 쉽게 만들기

객체 선언(object)

- 언어에서 지원하는 싱글턴
- 클래스 선언 + 클래스에 속한 단일 인스턴스의 선언을 합친 선언

```kotlin
object Payroll {
    val allEmployees = arrayListOf<Person>()
    
    fun calculateSalary() {
        for (person in allEmployees)
        {
            /* ... */       
        }
    }
}
```

- 인스턴스 선언이 포함 → 생성자 호출 없이 직접 만들어짐 → 생성자 정의 필요 없음
- 자바로 컴파일 시 → 유일한 인스턴스에 대한 static 필드가 있는 자바 클래스로 컴파일
    - 필드의 이름은 항상 INSTANCE

## 4.4.2 동반 객체: 팩토리 메서드와 정적 멤버가 들어갈 장소

어떤 클래스와 관련이 있지만 호출하기 위해 그 클래스의 객체가 필요하지는 않은 메서드와 팩토리 메서드를 담을 때 사용

```kotlin
class MyClass {
    companion object {
        fun callMe() {
            println("Companion object called")
        }
    }
}

fun main() {
    MyClass.callMe()
}
```

- companion object 사용
- 객체 멤버에 접근할 때 자신을 감싸는 클래스의 이름을 통해 직접 사용
- 자바의 static 구문과 같아짐

클래스의 인스턴스는 동반 객체의 멤버에 접근할 수 없다.

```kotlin
fun main() {
    val myObject = MyClass()
    myObject.callMe()   // 불가능
}
```

동반 객체 → 팩토리 메서드 담기 좋음

자바로 컴파일 시 Companion이라는 이름으로 컴파일 → 참조에 접근 가능

## 4.4.4 객체 식: 익명 내부 클래스를 다른 방식으로 작성

자바의 익명 내부 클래스 대신 사용

```kotlin
fun main() {
    Button(object: MouseListener {
        override fun onEnter() { /* ... */}
        override fun onClick() { /* ... */}
    })
}
```

- 객체 식 안의 코드도 그 식이 포함된 함수의 변수에 접근 가능
- 객체 안에서 여러 메서드를 오버라이드 해야 하는 경우에 유용

## 4.4.5 인라인 클래스

```kotlin
@JvmInline
value class UsdCent(val amount: Int)
```

- @JvmInline 키워드 붙임
- 실행 시점의 인스턴스는 감싸진 프로퍼티로 대체
- 클래스의 데이터가 사용되는 시점에 인라인
- 클래스가 프로퍼티를 하나만 가져야 하며, 그 프로퍼티는 주 생성자에서 초기화 되어야 함
- 다른 클래스 상속 불가, 다른 클래스도 인라인 클래스 상속 불가
- 인터페이스 상속 가능, 메서드 정의 가능
- 함수를 호출하는 쪽에서 실수로 잘못된 의미로 값을 전달하는 경우를 막을 수 있음