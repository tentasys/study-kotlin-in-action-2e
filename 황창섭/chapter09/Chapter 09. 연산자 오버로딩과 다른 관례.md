# Chapter 09. 연산자 오버로딩과 다른 관례

- 연산자 오버로딩
- 관례: 여러 연산을 지원하기 위해 특별한 이름이 붙은 메서드
- 위임 프로퍼티

## 9.1 산술 연산자를 오버로드해서 임의의 클래스에 대한 연산을 더 편리하게 만들기

### 9.1.1 plus, times, divide 등: 이항 산술 연산 오버로딩

```kotlin
/**
 * plus 연산자 구현하기
 */
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}
```

- 연산자를 오버로딩하는 함수 앞에는 반드시 operator가 있어야 함

```kotlin
/**
 * 연산자를 확장 함수로 정의하기
 */
operator fun Point.plus(other: Point): Point { 
  return Point(x + other.x, y + other.y)
}
```

- 오버로딩 가능한 이항 산술 연산자
  - a * b : times
  - a / b : div
  - a & b : mod
  - a + b : plus
  - a - b : minus

- 코틀린 연산자는 교환법칙이 자동으로 지원되지 않음

### 9.1.2 연산을 적용한 다음에 그 결과를 바로 대입: 복합 대입 연산자 오버로딩

- plus 연산자를 오버로딩하면 + 뿐 아니라 +=(복합 대입) 연산자도 함께 지원
- 컬렉션 객체의 상태를 변경하려면 -> plusAssign 사용
  - Unit을 반환하며, 피연산자를 변경
- 새로운 컬렉션을 생성하려면 -> plus 연산 사용
  - 새로운 컬렉션을 반환

### 9.1.3 피연산자가 1개뿐인 연산자: 단항 연산자 오버로딩

```kotlin
/**
 * 단항 산술 연산자 정의하기
 */
operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
}
```

- 오버로딩 가능한 단항 산술 연산자
  - +a : unaryPlus
  - -a : unaryMinus
  - !a : not
  - ++a, a++ : inc
  - --a, a-- : dec

## 9.2 비교 연산자를 오버로딩해서 객체들 사이의 관계를 쉽게 검사

### 9.2.1 동등성 연산자: equals

- 코틀린에서 == 연산자는 null 체크를 수행하고 null이 아닐경우 a.equals(b)를 호출해줌
- Point 클래스는 data 표시가 있기에 자동으로 equals를 생성해줌
- != 연산자는 == 연산자를 오버로드하면 자동으로 지원

### 9.2.2 순서 연산자: compareTo <, >, <=, >=

- 코틀린은 비교 연산자를 사용하는 코드는 compareTo 호출로 컴파일 해줌

## 9.3 컬렉션과 범위에 대해 쓸 수 있는 관례

### 9.3.1 인덱스로 원소 접근: get과 set

```kotlin
/**
 * get 관례 구현하기
 */
operator fun Point.get(index: Int): Int {
    return when(index) {
        0 -> x
        1 -> y
        else ->
          throw IndexOutOfBoundsException()
    }
}

fun main() {
    val p = Point(10, 20)
    println(p[1])
}
```

- x[a, b] -> x.get(a, b) 로 치환

### 9.3.2 어떤 객체가 컬렉션에 들어있는지 검사: in 관례

```kotlin
/**
 * in 관례 구현하기
 */
data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x..<lowerRight.x &&
            p.y in upperLeft.y..<lowerRight.y
}
```

- a in c -> c.conatains(a) 로 치환

### 9.3.3 객체로부터 범위 만들기: rangeTo와 rangeUntil 관례

- start..end -> start.rangeTo(end)

### 9.3.4 자신의 타입에 대해 루프 수행: iterator 관례

- 코틀린 표준 라이브러리는 String의 상위 클래스인 CharSequence에 대한 iterator 확장 함수를 제공

## 9.4 component 함수를 사용해 구조 부해 선언 제공

- 구조 분해 사용 방법
```kotlin
fun main() {
    val p = Point(10, 20)
    val (x, y) = p
    println(x) // 10
    println(y) // 20
}
```

```kotlin
/**
 * 구조 분해 선언을 사용해 여러 값 반환하기
 */
data class NameComponents(val name: String, 
                          val extension: String)

fun splitFileName(fullName: String): NameComponents {
    val result = fullName.split('.', limit = 2)
    return NameComponents(result[0], result[1]) // 함수에서 데이터 클래스의 인스턴스를 반환한다.
}

fun main() {
    val (name, ext) = splitFileName("example.kt") // 구조 분해 선언 구문을 사용해 데이터 클래스를 푼다.
    println(name) // example
    println(ext) // kt
}
```

### 9.4.1 구조 분해 선언과 루프


```kotlin
/**
 * 구조 분해 선언을 사용해 맵 이터레이션하기
 */
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

fun main() {
    val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
    printEntries(map) // Oracle -> Java, JetBrains -> Kotiln
}
```

### 9.4.2 _ 문자를 사용해 구조 분해 값 무시

- 컴포넌트가 여럿 있는 객체에 대해 변수 중 일부가 불필요할 때 사용

```kotlin
fun introducePerson(p: Person) {
    val (firstName, _, age) = p
  println("This is $firstName, aged $age.")
}
```

- 코틀린 구조 분해의 한계와 단점: 코틀린의 구조 분해 선언 구현은 위치에 의한 것 
- 즉, componentN 함수는 N의 순서에 따라 호출되며 이름이 아닌 순서가 중요

## 9.5 프로퍼티 접근자 로직 재활용: 위임 프로퍼티

- 위임 프로퍼티를 사용하면 값을 뒷받침하는 필드에 단순히 저장하는 것보다 더 복잡한 방식으로 작동하는 프로퍼티를 접근자 로직을 매번 재구현할 필요 없이 쉽게 구현할 수 있다.

### 9.5.1 위임 프로퍼티의 기본 문법과 내부 동작

```kotlin
class Foo {
    var p: Type by Delegate()
}
```

- 컴파일러는 숨겨진 도우미 프로퍼티를 만듬

```kotlin
class Foo {
    private val delegate: Delegate() // 컴파일러가 생성한 도우미 프로퍼티
            
    var p: Type // p 프로퍼티를 위해 컴파일러가 생성한 접근자는 delegate의 getValue와 setValue 메서드를 호출한다.
        set(value: Type) = delegate.setValue(/*...*/, value)
        get() = delegate.getValue(/* ... */)
}
```

### 9.5.2 위임 프로퍼티 사용: by lazt()를 사용한 지연 초기화

```kotlin
class Person(val name: String) {
    private var _emails: List<Email>? = null // 데이터를 저장하고 emails의 위임 객체 역할을 하는 _emails 프로퍼티

    val emails: List<Email>
        get() {
            if (_emails == null) {
                _emails = loadEamils(this) // 최초 접근 시 이메일을 가져온다
            }
            return _emails!! // 저장해둔 데이터가 있으면 그 데이터를 반환한다.
        }
}
```

- 표준 라이브러리 함수인 lazy를 통해 지연 초기화 프로퍼티를 쉽게 구현할 수 있다.

### 9.5.3 위임 프로퍼티 구현

```kotlin
fun interface Observer {
    fun onChange(name: String, oldValue: Any?, newValue: Any?)
}

open class Observable {
    val observers = mutableListOf<Observer>()
    fun notifyObservers(propName: String, oldValue: Any?, newValue: Any?) {
        for (obs in observers) {
            obs.onChange(propName, oldValue, newValue)
        }
    }
}
```

```kotlin
class Person(val name: String, age: Int, salary: Int) : Observable() {
    var age: Int = age
        set(newValue) {
            val oldValue = field
            field = newValue
            notifyObservers(
                    "age", oldValue, newValue
            )
        }
    var salary: Int = salary
        set(newValue) {
            val oldValue = field
            field = newValue
            notifyObservers(
                    "salary", oldValue, newValue
            )
        }
}

fun main() {
    val p = Person("Seb", 28, 1000)
  p.observers += Observer { propName, oldValue, newValue -> 
    println(
            """
            Property $propName changed from $oldValue to $newValue!
            """.trimIndent()
    )
  }
  
  p.age = 29
  p.salary = 1500
}
```

- Delegates.observable 함수를 사용하면 프로퍼티 변경을 관찰할 수 있는 옵저버를 쉽게 추가할 수 있다.