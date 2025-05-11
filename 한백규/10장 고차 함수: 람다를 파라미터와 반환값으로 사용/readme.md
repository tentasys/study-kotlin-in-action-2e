# 10장 고차 함수: 람다를 파라미터와 반환값으로 사용

## 10.1 다른 함수를 인자로 받거나 반환하는 함수 정의: 고차 함수

> 고차 함수란, 다른 함수를 인자로 받거나, 함수를 반환하는 함수

```kotlin
list.filter { it > 0 }
```

- 함수를 인자로 받아 `filter` 도 고차 함수

### 10.1.1 함수 타입은 람다의 파라미터 타입과 반환 타입을 지정한다

```kotlin
val sum = { x: Int, y: Int -> x + y } 
```

- int 파라미터 2개 받아 int 반환하는 함수
- int 생략 가능 &rarr; 컴파일러가 자동 지정

### 10.1.2 인자로 전달 받은 함수 호출

```kotlin
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("$result")
}

fun main() {
    twoAndThree { a, b -> a + b } // 5
    twoAndThree { a, b -> a * b } // 6
    
    
    println("ab1c".filter { it in 'a' .. 'z' }) // filter 함수 내 predicate 파라미터 전달 가능
}
```

### 10.1.3 자바에서 코틀린 함수 타입 사용

```kotlin
fun processTheAnswer(f: (Int) -> Int) {
    println(f(42))
}
```

- kotlin에서 고차 함수 정의
- java에서 호출하여 사용 가능

```java
processTheAnswer(number -> number + 1); // 43
```

### 10.1.4 함수 타입의 파라미터에 대해 기본값을 지정할 수 있고, 널이 될 수도 있다

```kotlin
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

- `toString` 메서드로 항상 문자열로 변환
- `joinToString` 을 호출할 때마다 매번 람다를 넘기게 만들면 함수 호출 불편
- 함수 타입의 파라미터에 디폴트 값 지정으로 해결

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
```

### 10.1.5 함수를 함수에서 반환

```kotlin
fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    return { order -> 6 + 2.1 * order.itemCount} // 함수에서 람다 반환 
}

fun main() {
    val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
    println("Shipping costs ${calculator(Order(3))}") // 반환 받은 함수 호출
}
```

### 10.1.6 람다를 활용해 중복을 줄여 코드 재사용성 높이기

```kotlin
fun List<SiteVisit>.averageDurationFor(os: OS) = 
    filter { it.os == os }.map(SiteVisit::duration).average() // 중복 코드 별도 함수 정의

fun main() {
    println(log.averageDurationFor(OS.WINDOWS))
    println(log.averageDurationFor(OS.MAC))
}
```

- 특정 함수에 람다식 할당 가능

## 10.2 인라인 함수를 사용해 람다의 부가 비용 없애기


- 람다가 변수 받으면 람다 생성 시점마다 새로운 무명 클래스 객체 생성 &rarr; 무명 클래스 생성 부가 비용 발생 &rarr; 일반 함수 구현보다 비효율
- inline 변경자로 해결 가능 &rarr; 함수 호출하는 모든 문장을 바이트 코드로 변환

### 10.2.1 인라이닝이 작동하는 방식

> inline 선언 시 함수 호출부를 함수 번역한 바이트 코드로 컴파일

```kotlin
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
	lock.lock()
    try {
		return action()
	}
	finally {
		lock.unlock()
	}
}

val l = Lock()

synchronized(l) {
	// ... 	
}
```

- `synchronized` 함수를 **inline** 으로 선언
- `synchronized` 호출하는 코드 &rarr; 자바의 synchronized 문과 동일 
- 코틀린 컴파일러 &rarr; 그 람다를 함수 인터페이스를 구현하는 무명 클래스 wrapping x
  - 람다의 본문에 의해 만들어지는 바이트코드는 그 람다를 호출하는 코드(synchronized) 정의의 일부분으로 간주

### 10.2.2 인라인 함수의 제약

- 람다 사용 모든 함수 인라이닝 어려움
- 람다가 본문에 직접 펼쳐짐
- 파라미터로 받는 함수를 다른 변수에 저장하고 사용할 때 람다를 표현하는 객체가 어딘가에 존재해야함 &rarr; 람다 인라이닝 불가

```kotlin
class FunctionStorage {
    var myStoredFunction: ((Int) -> Unit)? = null
    inline fun storeFunction(f: (Int) -> Unit) {
            myStoredFunction = f // 전달된 파라미터를 저장, 다른 호출 지점에서 할당 불가 &rarr; illegal usage of inline parameter	
    }
}
```

- 전달 받은 함수 인자 프로퍼티 할당 x

### 10.2.3 컬렉션 연산 인라이닝

- sequence는 람다를 저장해야해 **람다 inline 불가능**
- 지연 계산을 통해 성능을 향상시키려는 이유로 모든 컬렉션 연산에 asSequence 를 붙여서는 안됨
- 시퀀스를 통해 성능을 향상시킬 수 있는 경우는 컬렉션 크기가 큰 경우뿐이다.

### 10.2.4 언제 함수를 인라인으로 선언할지 결정

- jvm &rarr; 이미 일반 함수 호출 inlining 지원
  - 코드 실행을 분석해 가장 이익이 되는 방향으로 호출 &rarr; 바이트 코드에서 기계어 번역 과정에서 발생
- 람다를 인자로 받는 함수를 인라이닝 하면 이득 &uarr;
- jvm &rarr; 함수 호출과 람다를 인라이닝 불가

### 10.2.5 withLock, use, useLines로 자원 관리를 위해 인라인된 람다 사용

> kotlin &rarr; try-with-resources 불필요

- `withLock`, `use`, `useLines` 가 인라인 람다로 최적화 되어 있어 굳이 `try-with-resources` 사용 x

## 10.3 람다에서 반환: 고차 함수에서 흐름 제어

### 10.3.1 람다 안의 return 문: 람다를 둘러싼 함수에서 반환

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return // 함수 자체가 종료
        }
    }
    
    println("Alice is not found")
}
```

- `비로컬 return` : 자신을 둘러싼 블록 보다 더 바깥에 있는 블록을 반환

### 10.3.2 람다로부터 반환: 레이블을 사용한 return

> 람다 식 내 `@label`로 로컬 return 사용 가능

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach label@{
        if (it.name == "Alice") return@label // 앞에서 정의한 레이블 참조
        println("Found Alice!")
    }
}
```

- 람다를 인자로 받는 인라인 함수의 이름을 return 뒤에 레이블로 사용 가능

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") return@forEach
        println("Found Alice!")
    }
}
```

### 10.3.3 익명 함수: 기본적으로 로컬 return

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach(fun (person) {
        if (person.name == "Alice") return
        println("${person.name} is not Alice")
    })
}
```

- 함수 이름이나 파라미터 타입을 생략할 수 있다는 차이만 존재