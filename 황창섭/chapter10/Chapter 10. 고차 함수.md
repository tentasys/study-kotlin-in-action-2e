# Chapter 10. 고차 함수: 람다를 파라미터와 반환값으로 사용

- 함수 타입
- 고차 함수와 코드를 구조화할 때 고차 함수를 사용하는 방법
- 인라인 함수
- 비로컬 return과 레이블
- 익명 함수

## 10.1 다른 함수를 인자로 받거나 반환하는 함수 정의: 고차 함수

- 고차함수: 다른 함수를 인자로 받거나 반환하는 함수

### 10.1.1 함수 타입은 람다의 파라미터 타입과 반환 타입을 지정한다

```kotlin
val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }

// 구체적인 타입 선언
val sum: (Int, Int) -> Int = { x, y -> x + y }
val action: () -> Unit = { println(42) }
```

- 함수의 반환 타입이 아니라 함수 타입 전체가 널이 될 수 있는 타입 선언: var funOrNull: `((Int, Int) -> Int)? = null`

### 10.1.2 인자로 저낟ㄹ 받은 함수 호출

```kotlin
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

fun main() {
    twoAndThree { a, b -> a + b }
    twoAndThree { a, b -> a * b }
}
```

- fun String.filter(predicate: (Char) -> Boolean) : String
    - String: 수신 객체 타입
    - predicate: 파라미터 이름
    - (Char) -> Boolean : 파라미터 함수 타입
    - (Char): 파라미터로 받는 함수의 파라미터 타입
    - Boolean:  파라미터로 받는 함수의 반환 타입

### 10.1.4 함수 타입의 파라미터에 대해 기본값을 지정할 수 있고, 널이 될 수도 있다

```kotlin
/**
 * 하드 코딩한 toString 사용 관례를 따르는 joinToString
 */
fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = ""
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}
```

```kotlin
fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = "",
        transform: (T) -> String = { it.toString() }
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element))
    }

    result.append(postfix)
    return result.toString()
}

fun main() {
    val letters = listOf("Alpha", "Beta")

    println(letters.joinToString()) // Alpha, Beta
    println(letters.joinToString { it.lowercase() }) // alpha, beta
    println(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.uppercase() })) // ALPHA! BETA!
}
```

```kotlin
/**
 * 널이 될 수 있는 함수 타입 파라미터를 사용하기
 */
fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = "",
        transform: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        val str = transform?.invoke(element) ?: element.toString()
        result.append(transform(element))
    }

    result.append(postfix)
    return result.toString()
}
```

### 10.1.5 함수를 함수에서 반환

```kotlin
enum class Delivery { STANDARD, EXPEDITE D }

class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITE) {
        return { order -> 6 + 2.1 * order.itemCount }
    }

    return { order -> 1.2 * order.itemCount }
}
```

- 위처럼 특정 조건에 따라 달라질 수 있는 로직 구현 시 유용할 수 있음

### 10.1.6 람다를 활용해 중복을 줄여 코드 재사용성 높이기

- 기존 로직

```kotlin
data class SiteVisit(val path: String, val duration: Double, val os: OS)
enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
        SiteVisit("/", 34.0, OS.WINDOWS),
        SiteVisit("/", 22.0, OS.MAC),
        SiteVisit("/login", 12.0, OS.WINDOWS),
)

// 하드코딩 필터 사용
val averageWindowsDuration = log
        .filter { it.os == OS.WINDOWS }
        .map(SiteVisit::duration)
        .average()

// 일반 함수 이용
fun List<SiteVisit>.averageWindowsDurationFor(os: OS) = filter { it.os == os }.map(SiteVisit::duration).average()

// 고차 함수 이용
fun List<SiteVisit>.averageWindowsDurationFor(predicate: (SiteVisit) -> Boolean) = filter { predicate }.map(SiteVisit::duration).average()
```

- 필터 조건을 쉽게 대체 가능하다

## 10.2 인라인 함수를 사용해 람다의 부가 비용 없애기

- 람다를 사용하는 것은 약간의 부가 비용이 든다. 이를 극복하기 위한 정리.

### 10.2.1 인라이닝이 작동하는 방식

- 어떤 함수를 inline으로 선언하면 그 함수의 본문이 인라인된다. 즉, 함수를 호출하는 코드를 함수 본문을 번역한 바이트코드로 컴파일한다는 뜻

```kotlin
import java.util.concurrent.locks.Lock

inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

fun foo(l: lock) {
    println("Before sync")
    synchronized(l) {
        println("Action")
    }
    println("After sync")
}

// 위 함수는 아래와 동등
fun foo(l: lock) {
  println("Before sync")
  l.lock()
  try {
    return println("Action")
  } finally {
    l.unlock()
  }
  println("After sync")
}
```

- 다만 다음의 경우 람다 본문은 인라이닝 되지 않음

```kotlin
class LockOwner(val lock: Lock) {
    fun runUnderLock(body: () -> Unit) {
        synchronized(lock, body)
    }
}

// 동등 코드
class LockOwner(val lock: Lock) {
  fun __runUnderLock__(body: () -> Unit) {
      lock.lock()
      try {
        body()
      } finally {
        lock.unlock()
      }
  }

}
```

### 10.2.2 인라인 함수의 제약

- 일반적으로 인라인 함수의 본문에서 람다식을 바로 호출하거나 다른 인라인 함수의 인자로 전달하는 경우에는 그 람다를 인라이닝 할 수 없다

### 10.2.3 컬렉션 연산 인라이닝

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(Person("Alice", 29), Person("Bob", 31))

fun main() {

    // 람다 사용
    println(people.filter { it.age < 30 })
  
    // 직접 거르기
    val result = mutableListOf<Person>()
    for(person in people) {
        if(person.age < 30) result.add(person)
    }
}
```

- filter는 인라인함수이기에 위의 두 코드는 거의 동등하다. 하지만 추가 체이닝 연산이 들어가면 이야기가 달라진다


### 10.2.4 언제 함수를 인라인으로 선언할지 결정

- inline 키워드가 만능처럼 보이지만 람다를 인자로 받는 함수만 성능이 좋아질 가능성이 높다.
- 일반 함수 호출의 경우 JVM은 이미 강력하게 인라이닝을 지원하고 있음.

### 10.2.5 withLock, use, useLines 로 자원 관리를 위해 인라인된 람다 사용

- 앞선 예제에서 구현한 synchronized 함수와 동일한 withLock을 지원

```kotlin
fun <T> Lock.withLock(action: () -> T): T {
  lock()
  try {
    return action()
  } finally {
    unlock()
  }
}
```

- 이후 챕터에서 코틀린 코루틴과 동시성 프로그래밍을 다룰 때 Mutex가 위의 withLock 과 비슷한 함수를 제공

- use: 닫을 수 있는 자원에 대해 확실히 닫게 해주는 코틀린 함수

```kotlin
fun readFirstLineFromFile(fileName: String): String {
    BufferedReader(FileReader(fileName)).use{ br -> // BufferedReader 객체를 만들고 use 함수를 호출하면서 파일에 대한 연산을 실행할 람다를 넘김
      return br.readLine() } 
}
```

## 10.3 람다에서 반환: 고차 함수에서 흐름 제어

### 10.3.1 람다 안의 return 문: 람다를 둘러싼 함수에서 반환

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(Person("Alice", 29), Person("Bob", 31))

// 일반 함수
fun lookForAlice(people: List<Person>) {
    for(person in people) {
        if(person.name == "Alice") {
            println("Found!")
            return
        }
    }
}

// forEach
fun lookForAlice(people: List<Person>) {
  people.forEach {
    if(person.name == "Alice") {
      println("Found!")
      return
    }
  }
}
```

- 람다 안에서 return을 사용하면 **람다에서만 반환되는 것이 아니라 그 람다를 호출하는 함수가 실행을 끝**내고 반환된다/
- 자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 return 문을 비로컬 return이라 부름
- 인라인 함수에서만 return이 가능
- 인라이닝되지 않는 함수는 람다를 변수에 저장할 수 있기 때문

### 10.3.2 람다로부터 반환: 레이블을 사용한 return

- 람다에서 로컬 return을 하려면 레이블을 사용해야 함

```kotlin
fun lookForAlice(people: List<Person>) {
  people.forEach label@{
    if(person.name == "Alice") {
      println("Found!")
      return@label
    }
  }
}

// 함수 이름을 return 레이블로 사용

fun lookForAlice(people: List<Person>) {
  people.forEach {
    if(person.name == "Alice") {
      println("Found!")
      return@forEach
    }
  }
}
```

### 10.3.3 익명 함수: 기본적으로 로컬 return

```kotlin
fun lookForAlice(people: List<Person>) {
  people.forEach {
    fun(person) {
      if (person.name == "Alice") {
        println("Found!")
        return
      }
    }
  }
}
```

- 익명 함수의 경우 기본적으로 로컬 return(return이 가장 가까운 함수를 가리킨다.)