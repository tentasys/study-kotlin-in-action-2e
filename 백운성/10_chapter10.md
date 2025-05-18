# 10주 10장 고차 함수: 람다를 파라미터와 반환값으로 사용 : 2025-05-05 ~ 2025-05-11

---

## 고차 함수

- 고차 함수는 다른 함수를 인자로 받거나 함수를 반환하는 함수
- 자바에서 코틀린 함수 타입 사용 가능
- 람다를 활용해 추상화 하여 중복을 줄여 코드 재사용성 높일 수 있음

### 함수 타입은 람다의 파라미터 타입과 반환 타입을 지정

```kotlin
val sum: (Int, Int) -> Int = { x, y -> x + y } // Int 파라미터를 2개 받아서 Int 값을 반환하는 함수
val action: () -> Unit = { println(42) } // 아무 인자도 받지 않고 아무 값도 반환하지 않는 함수
```

- 함수 또는 return을 null로 설정할 수 있음

```kotlin
// 리턴 null
var canReturnNull: (Int, Int) -> Int? = { x, y -> null }
// 함수 null
var funOrNull: ((Int, Int) -> Int)? = null
```

### 인자로 전달 받은 함수 호출

```kotlin
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    // 함수 타입인 파라미터를 선언한다.
    printin("The result is result")
    // 함수 타입인 파라미터를 호출한다.
}

fun main() {
    twoAndThree { a, b -> a + b }
    // The result is 5
    twoAndThree { a, b -> a * b }
    // The result is 6
}
```

### 함수 타입의 파라미터에 대해 기본값을 지정할 수 있고, 널이 될 수도 있다

- 파라미터를 함수 타입으로 선언할 때도 기본값 지정 가능
- 함수 타입에 대한 기본값을 선언할 때 다른 디폴트 파라미터 값과 마찬가지로 합수 타입에 대한 기본값 선언도 `=`뒤에 람다를 넣으면 됨
- 이름 붙은 인자로 람다를 전달할 수 있음

```kotlin
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: (T) -> String = { it.toString() } // default
): String {
    // ...
    return ""
}
```

### 함수를 함수에서 반환

```kotlin
fun makeMultiplier(factor: Int): (Int) -> Int {
    return { number -> number * factor }
}

fun main() {
    val timesThree = makeMultiplier(3)
    println(timesThree(5)) // 출력: 15

    val timesTen = makeMultiplier(10)
    println(timesTen(2)) // 출력: 20
}
```

## 인라인 함수를 사용해 람다의 부가 비용 없애기

- 보통 람다는 익명 클래스로 컴파일되며 이는 비용이 듬
- `inline` 변경자를 어떤 함수에 붙이면 컴파일러가 함수를 구현하는 코드로 바꿔치기 하여 비용을 없앰
- 주로 고차 함수에서 람다를 인자로 받을 때 사용됨
- 인라인 함수를 호출하면서 랍다를 넘기는 대신에 함수 타입의 변수를 넘길 수 있는데 이때는 람다 본문은 인라이닝되지 않음

```kotlin
inline fun withLogging(block: () -> Unit) {
    println("시작")
    block()
    println("끝")
}

fun main() {
    withLogging {
        println("작업 중...")
    }
}

// 컴파일 시 withLogging() 함수가 코드에 그대로 삽입됨 (inlined)
// 함수 호출 비용 없음, block()도 함수 객체 생성 없이 직접 호출됨
fun main() {
    println("시작")
    println("작업 중...")
    println("끝")
}
```

- 주의사항
    - 작고 자주 호출되는 고차 함수에만 inline 권장
    - 무분별하게 사용시 바이트 크기가 늘어남
    - 람다를 저장/전달/비동기 실행하려면 noinline이나 crossinline 필요
    - 라이브러리의 public API에는 남용 금지 (호환성 문제)
-

### Kotlin에서 자원(락, 스트림 등)을 안전하게 다루기 위한 고차 함수

- withLock
  - 동기화 블록 안에서 락을 안전하게 사용
  - lock.lock()과 finally { lock.unlock() }를 자동 처리해줌
  - 예외 발생해도 락을 반드시 해제함
```kotlin
val lock = ReentrantLock()

fun doThreadSafeWork() {
    lock.withLock {
        // 락이 걸린 안전한 블록
        println("동기화된 작업 수행")
    }
}
```
- use
  - Closable 자원 자동 해제 (예: File, InputStream)
  - reader.close()를 자동으로 호출
  - try-finally 없이 안전한 자원 사용 가능
  - AutoCloseable/Closeable 인터페이스 구현체에서 사용
```kotlin
File("example.txt").bufferedReader().use { reader ->
  val text = reader.readLine()
  println(text)
}
```
- useLines
  - 파일의 라인을 순차적으로 안전하게 처리
  - 한 줄씩 스트림처럼 처리 (메모리 절약)
  - 내부에서 BufferedReader를 열고, 다 쓰면 자동으로 닫음 (use 포함됨)
  
```kotlin
File("example.txt").useLines { lines ->
    lines.filter { it.isNotBlank() }
        .forEach { println(it) }
}
```

## 람다에서 반환: 고차 함수에서 흐름 제어

### 일반적인 return (non-local return)
- 자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 return 문
```kotlin
fun foo() {
    listOf(1, 2, 3).forEach {
        if (it == 2) return  // foo() 전체 종료!
        println(it)
    }
    println("끝") // 실행 안 됨
}

fun main() = foo()
```

### 레이블을 사용한 return
- `label@`로 명시적 레이블 부여 후 `return@label` 사용 
- 실무에서는 return@함수명 형식을 더 자주 사용
```kotlin
fun test() {
    val list = listOf(1, 2, 3)
    list.forEach label@{
        if (it == 2) return@label
        println(it)
    }
    println("Done")
}
```

### 익명 함수 사용 시 항상 로컬 return
- 익명 함수는 기본적으로 non-local return 불가 
- 항상 람다 내부 로직에서만 리턴됨
```kotlin
fun test() {
    listOf(1, 2, 3).forEach(fun(it: Int) {
        if (it == 2) return  // forEach 루프에서만 탈출
        println(it)
    })
    println("Done")  // 항상 실행됨
}
```