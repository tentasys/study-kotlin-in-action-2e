# Chapter 07. 널이 될 수 있는 값

- nullable 타입
- 널이 될 가능성이 있는 값을 다루는 구문의 문법
- 널이 될 수 있는 타입과 널이 될 수 없는 타입의 변환
- 코틀린의 널 가능성 개념과 자바 코드 사이의 상호운용성

## 7.1 NullPointerException을 피하고 값이 없는 경우 처리: 널 가능성

- nullability는 NPE를 피할 수 있게 돕는 코틀린 타입 시스템의 특성

## 7.2 널이 될 수 있는 타입으로 널이 될 수 있는 변수 명시

```java
/**
 * null-safe하지 않은 자바 예시
 */
int strLen(String s) {
    return s.length();
}
```

- kotlin에서 이런 함수를 작성할 때 null을 인자로 받을 수 있는가? 를 확인

```kotlin
/**
 * null을 받을 수 없는 함수 예제. null을 넘기면 컴파일 에러
 */
fun strLen(s: String) = s.length
```

- null을 받게 하려면 타입 이름 뒤에 물음표(?) 명시
- 널이 될 수 있는 타입의 값이 있으면 여러 연산이나 대입 제한

```kotlin
fun strLen(s: String?) = s.length() // 불가능

fun main() {
    val x: String? = null
    var y: String = x // 불가능
}
```

- `if(s != null)` 과 같은 검사가 있어야 컴파일이 가능해짐

## 7.3 타입의 의미 자세히 살펴보기

- null과 String 타입은 엄연히 다른 타입임에도 String에 null을 할당할 수 있다.
- 이는 자바의 타입 시스템이 null을 제대로 다루지 못한다는 것을 뜻함(물론 Nullable NotNull과 같은 어노테이션으로 어느정도 해결 가능
- 코틀린은 이러한 상황을 해결하기 위한 여러 도구를 제공

## 7.4 안전한 호출 연산조로 null 검사와 메서드 호출 합치기: `?.`

- `?.`: null 검사와 메서드 호출을 한 연산으로 수행
- `str?.uppercase()` 의 의미는 `if(str != null) str.uppercase() else null` 과 같음

```kotlin
/**
 * 널이 될 수 있는 프로퍼티를 다루기 위해 안전한 호출 사용하기
 */
class Employee(val name: String, val manager: Employee?)

fun managerName(employee: Employee): String? = employee.manager?.name

fun main() {
    val ceo = Employee("Da Boss", null)
    val developer = Employee("Bob Smith", ceo)
    
    print(managerName(ceo)) // null
    
    print(managerName(developer)) // Da Boss
}
```

## 7.5 엘비스 연산자로 null에 대한 기본값 제공: `?:`

- orElse, orElseGet, orElseThrow와 비슷한 느낌
- `val recipient: String = name ?: "unnamed"`
- `fun strLenSafe(s: String?): Int = s?.length : 0 ` s가 null일 경우 0 출력

## 7.6 예외를 발생시키지 않고 안전하게 타입의 캐스트하기: as?

- 어떤 값을 지정된 타입으로 변환. 변환할 수 없을경우 null 반환

## 7.7 널 아님 단언

- null이 아니면 그대로 사용, null이면 NPE 발생

```kotlin
fun ignoreNulls(str: String?) {
    val strNotNull: String = str!! // null 주입시 NPE 발생
    println(strNotNull.length)
}
```

## let 함수

- let 사용의 가장 흔한 용례는 널이 될 수 ㅣㅇ슨ㄴ 값을 널이 아닌 값만 인자로 받는 함수에 넘기는 경우

```kotlin
fun sendEmailTo(email: String) {}

fun main() {
    val email: String? = "foo@bar.com"
    sendEmailTo(email) // Type mismatch Error
}
```

- email?.let { email -> sendEmailTo(email) } 와 같이 사용

```kotlin
fun sendEmailTo(email: String) {}

fun main() {
    val email: String? = "foo@bar.com"
    email?.let { sendEmailTo(it) }
}
```

- 인자가 null일 경우 let 함수는 호출되지 않음

## 7.9 직접 초기화하지 않는 널이 아닌 타입: 지연 초기화 프로퍼티

- JUnit @BeforeEach 처럼 별도 초기화 메서드가 제공되는 경우가 있지만, 코틀린에서 클래스 안의 널이 아닌 프로퍼티는 생성자 외 특별한 메서드 안에서 초기화할 수 없음
- 이를 극복하기 위해 lateinit 키워드를 사용하여 지연 초기화 기능을 제공

```kotlin
/**
 * 지연 초기화하는 프로퍼티 사용하기
 */
class MyService{
    fun performAction(): String = "Action Done!"
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTest {
    private lateinit var myService: MyService
    
    @BeforeAll fun setUp() {
        myService = MyService()
    }
    
    @Test fun testAction() {
        assertEquals("Action Done!", myService.performAction()) // MyService 널 검사 수행없이 프로퍼티 사용
    }
}
```

## 7.10 안전한 호출 연산자 없이 타입 확장: 널이 될 수 있는 타입에 대한 확장

```kotlin
/**
 * 널이 될 수 있는 수신 객체에 대해 확장 함수 호출하기
 */
fun verifyUserInput(input: String?) {
    if(input.isNullOrBlank()) {
        println("Please fill")
    }
}

fun main() {
    verifyUserInput(" ")
    
    verifyUserInput(null)
}
```

- isNullOrBlank() 가 널이 될 수 있는 타입의 확장함수이다.

```kotlin
fun String?.isNullOrBlank(): Boolean = this == null || this.isBlank()
```

- 자바에서 this는 항상 null이 아니지만 코틀린에서는 this가 null일 수 있다

## 7.11 타입 파라미터의 널 가능성

```kotlin
/**
 * 널이 될 수 있는 타입 파라미터 다루기
 */

fun <T> printHashCode(t: T) {
    println(t?.hashCode()) // t가 nullable이기에 안전한 호출 사용
}

fun main() {
    printHashCode(null) // T의 타입은 Any?로 추론
}
```

## 7.12 널 가능성과 자바

- @Nullable + Type = Type?
- @NotNull + Type = Type

### 7.12.1 플랫폼 타입

- 코틀린이 널 관련 정보를 알 수 없는 타입을 뜻함
- 아무런 경고를 표기하지 않고 null 방어는 오롯이 개발자의 몫