# 7주 8장 기본 타입, 컬렉션, 배열 : 2025-04-14 ~ 2025-04-20

---

## 원시 타입과 기본 타입

- 래퍼 타입을 따로 구분하지 않고 컴파일러가 알아서 변경해줌
- Int (primitive type)을 사용하더라도 어떻게 사용하냐에 따라 알아서 변경해줌

### 원시타입 (primitive type)

| Type        |                        |
|-------------|------------------------|
| 정수 타입       | Byte, Short, Int, Long |
| 부동소수점 숫자 타입 | Float, Double          |
| 문자 타입       | Char                   |
| 불리언 타입      | Boolean                |

### 부호 없는 타입

- UByte, UShort, UInt, ULong

### 널이 될수 있는 기본 타입

- Int?, Boolean?
- 컴파일 시 wrapper class로 변경됨

### 수의 변환

- 자동으로 다른 타입의 형변환을 지원하지 않음
- 타입을 표현하는 문자를 붙일 경우에는 보통 변환 함수를 호출할 필요가 없음

```kotlin
val i = 1
val l: Long = i // error

val i = 1
val l: Long = i.toLong() // 직접적으로 형변환을 해줘야함

fun printALong(l: Long) = println(1)
fun main() {
    val b: Byte = 1
    val l = b + 1L
    printALong(42)
}
```

### Any와 Any?

- 자바 Object와 같이 모든 타입에 대한 최상위 타입
- null을 허용하지 않음
- 원시 타입 값을 `Any` 타입의 변수에 대입하면 자동으로 값을 객체로 감싼다 (boxing)

### Unit 타입: 코틀린의 void

- void 기능
- 반환이 없는 함수에 unit을 사용할 수 있음

```kotlin
fun f(): Unit { /* ... */
}

// 명시 하지 않아도 사용 가능 
fun f() {/* ...*/
}
```

### Nothing 타입

- Nothing 타입은 아무 값도 포함하지 않음
- Nothing은 함수의 반환 타입이나 반환 타입으로 쓰일 타입 파라미터로만 쓸 수 있음

```kotlin
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}
fun main() {
    fail"Error occurred")
    // java. lang. IllegalStateException: Error occurred
}
```

## 컬렉션과 배열

### null이 될 수 있는 값의 컬렉션과 null이 될 수 있는 컬렉션

- `?` 위치에 따라 다르게 null이 처리 됨

```kotlin
List<Int?> // 리스트 안의 각 값이 널이 될 수 있다.
List<Int>? // 전체 리스트가 널이 될 수 있다.
List<Int?>? // 전체 리스트와 각 원소가 널이 될 수 있다.
```

- null이 요소에 들어가는 경우 null check를 피하기 위해 `filterNotNull` 이라는 함수를 제공함

```kotlin
var numbers = listOf(1, 2, null)
// null을 제외한 요소로만 구성된 배열을 생성할 수 있음
val validNumbers = numbers.filterNotNull()
```

### 읽기 전용과 변경 가능한 컬렉션

- MutableCollection: 변경 가능 collection
- Collection: 읽기 전용 collection
- 자바와 코틀린의 collection 생성 함수

| 컬렉션 타입 | 읽기 전용 타입     | 변경 가능 타입                                                    |
|--------|--------------|-------------------------------------------------------------|
| List   | listOf, List | mutableListOf, MutableList, arrayListOf, buildList          |
| Set    | setOf        | mutableSetOf, hashSetOf, linkedSetOf, sortedSetOf, buildSet |
| Map    | mapOf        | mutableMapOf, hashMapOf, linkedMapOf, sortedMapOf, buildMap |
