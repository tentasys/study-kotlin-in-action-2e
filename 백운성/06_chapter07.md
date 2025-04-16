# 6주 7장 널이 될 수 있는 값 : 2025-04-07 ~ 2025-04-13

---

## Null Safety

- 코틀린에서는 기본적으로 모든 변수는 non-null
- 변수나 프로퍼티에 null을 허용하려면 타입 뒤에 ?를 붙여야 함
- null을 허용한 타입은 메서드를 직접 호출 할 수 없음
- null을 허용한 타입은 non-null 타입에 대입할 수 없음

```kotlin
var name: String = "Alice"   // null 불가능
var nickname: String? = null // null 가능
```

## null 처리 방법

### 1. Safe Call (`?.`)

- 메소드 호출이나 프로퍼티를 읽을때 안전하게 호출 가능

```kotlin
val length = nickname?.length
// null = null
// "abc" = 3
```

### 2. Elvis 연산자 (`?:`)

- null일 때 기본값 지정할 수 있음 
- return, throws 도 사용 가능

```kotlin
val length = nickname?.length ?: 0
// null = 0
// "abc" = 3

val length = nickname?.length ?: throw IllegalArgumentException("") 
```

### 3. Safe Cast (`as?`)

- 안전하게 타입 캐스팅하는데 사용
- 타입이 맞지 않을 경우 null을 반환
- 일반적인 사용 방법은 캐스팅 후 elvis 연산자 사용

```kotlin
val str: String? = obj as? String

val str = obj as? String ?: return 0
return str.length
```

### 4. Non-null 단정 (`!!`)

- 널이 아니라고 단언할때 사용
- null인 경우를 처리 않아도 되며, 이때는 null이면 `NPE` 발생함
  > !!를 null에 대해 사용해서 발생하는 예외는 stack trace에는 어떤 파일의 몇번째 줄인지에 정보는 들어있지만 어떤 식에서 예외가 발생했는지 정보가 없어 한줄에 하나씩만 사용할 것! 

```kotlin
val length = nickname!!.length
```

### 5. let 함수

- null을 허용한 값을 null아닌 값에 넘길때 사용
- null이면 함수는 실행되지 않음 

```kotlin
fun sendEmail(email: String) {
    println("send email: $email")
}

fun main() {
    val email: String? = "wsbaek@bithumbcorp.com"
    sendEmail(email) // error
    email?.let { sendEmail(it) }
}
```

| expression | description  |
|------------|--------------|
| `?`        | null 허용 변수 선언 |
| `?.`       | null-safe 호출 |
| `?:`       | null이면 기본값   |
| `!!`       | null 아님을 보장 |
| `let`      | null이 아닐 때 블록 실행 |


### 6. 지연 초기화 프로퍼티 

- non-null 프로퍼티 전용 키워드
- var로 선언된 객체 타입 프로퍼티에 사용
- 생성자에서 초기화하지 않고, 나중에 초기화해야 하는 경우 사용
- 초기화 전에 접근 시 에러 `UninitializedPropertyAccessException` 발생

```kotlin
class User {
    lateinit var name: String

    fun initName() {
        name = "Alice"
    }

    fun printNameLength() {
        println(name.length)
    }
}
```

### 7. 타입 파라미터의 널 가능성

- 타입 파라미터 T를 클래스나 함수 안에서 타입 이름으로 사용하면 이름 끝에 물음표가 없더라도 T는 null이 될 수 있음
- Any? 타입으로 추론됨

```kotlin
fun <T> printHashCode(t: T) {}

fun main() {
    printHashCode(null) 
    // null 가능 
}
```

### 8. 상속

- 자바 메소드를 override 할때 파라미터나 리턴타입은 null이 될수 있음

```java
interface StringProcessor {
    void process(String value);
}
```
```kotlin
class StringPrinter : StringProcessor {
  override fun process(value: String) {
      println(value)
  }
}

class NullableStringPrinter : StringProcessor {
  override fun process(value: String?) {
    println(value ?: "")
  }
}
```