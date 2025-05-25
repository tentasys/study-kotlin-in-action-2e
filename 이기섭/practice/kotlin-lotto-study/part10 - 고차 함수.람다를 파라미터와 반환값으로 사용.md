#10장 고차 함수: 람다를 파라미터와 반환값으로 사용

- 함수 타입
- 고차 함수와 코드를 구조화할 때 고차 함수를 사용하는 방법
- 인라인 함수
- 비로컬 return과 레이블
- 익명 함수

## 10.1 다른 함수를 인자로 받거나 반환하는 함수 정의: 고차 함수
고차함수 예 : filter , map, with

### 10.1.1 함수 타입은 람다의 파라미터 타입과 반환 타입을 지정한다

함수 타입을 정의하려면 함수 파라미터의 타입을 괄호 안에 넣고 그 뒤에 화살표를 추가한 다음, 함수의 반환 타입을 지정하면 된다

``
(Int, String) -> Unit
``

### 10.1.2 인자로 전달 받은 함수 호출
간단한 고차 함수 정의하기
```kotlin
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

fun main() {
    twoAndThree { a, b -> a + b}
    // The result is 5
    twoAndThree { a, b -> a * b}
    // The result is 6
}
```

filter 함수를 단순하게 만든 버전 구현하기 
```kotlin
fun String.filter(predicate: (Char) -> Boolean): String {
    return buildString {
        for (char in this@filter) {
            if (predicate(char)) append(char)
        }
    }
}

fun main() {
    println("ab1c".filter {it in 'a'..'z'})
}
```

### 10.1.3 자바에서 코틸린 함수 타입 사용
- 함수 타입: 자세한 구현
- 함수 타입의 변수 -> FuntionN 인터페이스를 구현
- FuntionN 인터페이스는 컴파일러가 생성한 합성 타입, 코틀린 표준 라이브러리에서 이들의 정의를 찾을 수 없음.

### 10.1.4 함수 타입의 파라미터에 대해 기본값을 지정할 수 있고, 널이 될 수도 있다.
함수 타입 파라미터의 기본값이 유용한 경우를 살펴보자.
하드코딩한 toString 사용 관례를 따르는 joinToString
```kotlin
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String ="",
    postfix: String = ""
): String {
    val result = Stringbuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if(index>0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}
```

함수 타입의 파라미터에 대한 기본값 지정하기
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
    println(letters.joinToString())
    // Alpha, Beta
    println(letters.joinToString{ it.lowercase()})
    // alpha, beta
    println(letters.joinToString(separator = "! ", postfix="! ", transform = {it.uppercase()}))
    // ALPHA! BETA!
}
```

### 10.1.5 함수를 함수에서 반환
ㅎ함수를 반환하는 함수 정의하기ㅎ

ex - 사용자가 선택한 배송 수단에 따라 배송비를 계산하는 방법
```kotlin
enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 *order.itemCount}
    }
    return {order -> 1.2 * order.itemCount}
}

fun main() {
    val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
    println("Shipping costs ${calculator(Order(3))}")
    // Shipping costs 12.3
}
```

### 10.1.6 람다를 활용해 중복을 줄여 코드 재사용성 높이기
함수 타입과 람다식은 재사용하기 좋은 코드를 만들 때 쓸 수 있는 훌륭한 도구.


사이트 방문 기록 분석 데이터 정의
```kotlin

class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
)

enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/",34.0, OS.WINDOWS),
    SiteVisit("/",22.0, OS.MAC),
    SiteVisit("/login",12.0, OS.WINDOWS),
    SiteVisit("/signup",8.0, OS.IOS),
    SiteVisit("/",16.3, OS.ANDROID)
)

//하드코딩
val averageWindowsDuration = log
    .filter { it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()

//일반함수로 중복제거
fun List<SiteVisit>.averageDurationFor(os: OS) =
    filter { it.os == os }.map(SiteVisit::duration).average()

//고차 함수를 사용해 중복 제거
fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) = 
    filter(predicate).map(SiteVisit::duration).average()
 
fun main() {
    println(averageWindowsDuration)
    
    print(log.averageDurationFor(OS.WINDOWS))
    // 23.0
    print(log.averageDurationFor(OS.MAC))
    // 22.0
    
    print(log.averageDurationFor{
        it.os in setOf(OS.ANDROID, OS.IOS)
    })
}

```

### 10.2.1 인라이닝이 작동하는 방식
- inline : 함수를 호출하는 코드를 함수를 호출하는 바이트코드 대신에 함수 본문을 번역한 바이트코드로 컴파일

인라인 함수 정의하기
```kotlin

inline fun<T> synchronized(lock: Lock, action: () -> T) T {
    lock.lock()
    try {
        return action()
    }
    finalliy {
        lock.unlock()
    }
}
fun main() {
    val l = ReentrantLock()
    synchronized(1) {
        // ...
    }
}
```

### 10,2,2 인라인 함수의 제약
```kotlin
class FuntionStorage {
    var myStoredFunction: ((Int)-> Unit)? = null
    inline fun storeFunction(f: (Int) -> Unit) {
        myStoredFuntion(f: (Int) -> Unit) {
            myStoredFuntion = f
        }
    }
}
```

```kotlin
fun <T,R> Sequence<T>.map(transform: (T) -> R) : Sequence<R> {
    return TransformingSequence(this, transform)
}
```
- map 함수 -> TransformingSequence라는 클래스의 생성자에게 함수 값 전달
- TransFormingSequence 생성자는 전달받은 람다를 프로퍼티로 저장
- 함수 인터페이스를 구현하는 익명 클래스 인스턴스로 만듦

### 10.2.3 컬렉션 연산 인라이닝
람다를 사용해 컬렉션 거르기
```kotlin
data class Person(val name: String, val age: Int)
val people = listOf(Person("Alice", 29), Person("Bob", 31))
fun main() {
    println(people.filter {it.age < 30})
    //[Person(name=Alice, age=29)]
}
```

컬렉션 직접 거르기
```kotlin
fun main() {
    val result = mutableListOf<Person>()
    for (person in people) {
        if (person.age < 30) result.add(person)
    }
}
println(result)
```

### 10.2.4 언제 함수를 인라인으로 선언할지 결정
- inline 키워드를 사용하는 경우 람다를 인자로 받는 함수만 성능이 좋아질 가능성이 높음.
- 인라이닝을 통해 없앨 수 있는 부가 비용이 상당함. 함수호출 비용을 줄일 수 있음
- 현재의 JVM은 함수 호출과 람다를 인라이닝해줄 정도로 똑똑하지 못함.
- inline함수는 보통 크기가 매우 작다.

### 10.2.5 withLock, use, useLines로 자원 관리를 위해 인라인된 람다 사용

withLock
```kotlin
fun <T> Lock.withLock(action: () -> T): T {
    lock()
    try{
        return action()
    } finally {
        unlock()
    }
}
```

use 함수를 자원 관리에 활용하기
```kotlin
fun readFirstFromFile(fileName: String): String {
    BufferedReader(FileReader(fileName)).use { br -> return br.readLine()}
}
```

특화된  userLines 확장 함수

```kotlin
fun readFirstLineFromFile(fileName: String) : String {
    Path(fileNmae).useLines {
        return it.first()
    }
}
```
``
코틀린에서는 try-with-resources를 사용하지 말라
``

## 10.3 람다에서 반환: 고차 함수에서 흐름 제어
루프와 같은 명령형 코드 - return

### 10.3.1 람다 안의 return 문: 람다를 둘러싼 함수에서 반환
일반 루프 안에서 return 사용하기
```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(Person("Alice", 29), Person("Bob", 31))
fun lookForAlice(people: List<Person>) {
    for (person in people) {
        if (person.name == "Alice") {
            println("Found!")
            return
        }
    }
    println("Alice is not found")
}
fun main() {
    lookForAlice(people)
    // Found!
}
```

forEach에 전달된 람다에서 return 사용하기

```kotlin
fun loockForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return
        }
    }
    println("Alice is not found")
}
```

### 10.3.2 람다로부터 반환: 레이블을 사용한 return
레이블을 통해 로컬 return 사용
```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach label@{
        if (it.name != "Alice") return@label
        print("Found Alice!")
    }
}
fun main() {
    lookForAlice(people)
}
```

레이블이 붙은 this 식
```kotlin
fun main() {
    println(StringBuilder().apply sb@{
        listOf(1,2,3).apply {
            this@sb.append(this.toString())
        }
    })
}
```

### 10.3.3 익명 함수: 기본적으로 로컬 return
익명함수 안에서 return 사용하기
```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach(fun (person) {
        if (person.name == "Alice") return
        println("${person.name} is not Alice")
    })
}
fun main() {
    lookForAlice(people)
}
```