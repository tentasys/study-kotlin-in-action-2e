# 11주 11장 제네릭스 : 2025-05-12 ~ 2025-05-18
# 제네릭스
- 전체적으로 자바와 동일
---

## 타입 인자를 받는 타입 만들기: 제네릭 타입 파라미터
- 타입 파라미터를 받는 타입을 정의
- 빈 리스트 생성 시 타입을 알 수 없기때문에 명시해야 
```kotlin
val authors: List<String> = listOf ("Dmitry", "Svet lana")

// 컴파일러가 추론
val authors = listOf ("Dmitry", "Svet lana")

val readers: MutableList<String> = mutableListof()
```

### 제네릭 타입과 함께 동작하는 함수와 프로퍼티
- 타입 파라미터 선언 후 입력 파라미터와 리턴 파라미터에 사용 함
```kotlin
fun <T> List<T>.slice (indices: IntRange): List<T> { }
```
- 확장 프로퍼티만 제네릭하게 만들 수 있다 (클래스 프로퍼티는 안됨)
- 타입 파라미터를 넣은 `<>`를 클래스나 인터페이스 이름 뒤에 붙이면 해당 클래스나 인터페이스를 제네릭하게 만들 수 있음

### 타입 파라미터 제약
- 클래스나 함수에 사용할 수 있는 타입 인자를 제약
```kotlin
// Number를 상속받은 클래스만 가능
fun <T : Number> List<T>.sum(): T {}
```
- 아무런 제약을 두지 않으면 `Any?`로 설정되며 `null`이 될 수 있음
- `null`이 될수 없게 하려면 `Any`로 타입을 설정해야 함

## 실행 시점 제네릭스 동작: 소거된 타입 파라미터와 실체화된 타입 파라미터
- 자바와 마찬가지로 코틀린 제네릭 타입 인자 정보는 런타임에 지워진다.
- 타입 파라미터가 2개 이상이라면 모든 타입 파라미터에 `* (스타 프로젝션)`를 포함
  - 타입 정보를 알 수 없을 때 안전하게 처리
```kotlin
fun printSum(c: Collection<*>) {
    val intList = c as? List<Int>
        ?: throw IllegalArgumentException("List is expected")
    println(intList.sum())
}
```

## 실행 시점 제네릭: 소거와 실체화

- JVM에서는 제네릭 타입이 실행 시점에 **소거**되어 타입 정보를 사용할 수 없음
- `reified` 키워드를 사용하면 **실행 시점 타입 정보 유지** 가능 (`inline` 함수만 가능)

### 타입 소거의 한계
- `value is List<String>` : 실행 시점에 타입 없음
- `value is List<*>` : 타입 정보 없이 체크 가능

### `reified` 실체화 타입 파라미터
- `inline fun <reified T> isOfType(value: Any) = value is T`

### Class 객체 없이 타입 참조
- Java의 `Class<T>` 없이 Kotlin에서는 `loadService<MyService>()`처럼 타입 전달 가능

### 접근자에서는 `reified` 사용 불가
- `reified`는 `inline` 함수 내부에서만 사용 가능 → 프로퍼티 접근자에는 사용 불가

### 제약사항
- `reified`는 일반 함수, 인터페이스, 추상 클래스에서는 사용 불가


## 변성: 제네릭 타입 간 하위 타입 관계

- Kotlin에서는 제네릭 타입 사이의 하위 타입 관계를 명시적으로 지정해야 함

### 변성의 필요성
- `List<String>` ≠ `List<Any>` → 타입 안전성 보장

### 공변성 (`out`)
- `out T`: 읽기 전용
- `Producer<Cat>`은 `Producer<Animal>`에 대입 가능

### 반공변성 (`in`)
- `in T`: 쓰기 전용
- `Consumer<Animal>`은 `Consumer<Cat>`에 대입 가능

### 사용 지점 변성
- 함수 파라미터에 `out` 또는 `in` 붙여 일시적으로 지정 가능
  ```kotlin
  fun copy(from: Array<out Any>, to: Array<Any>) { ... }
  ```

### 타입 별명
- 타입에 별칭 부여
  ```kotlin
  typealias StringMap = Map<String, String>
  ```