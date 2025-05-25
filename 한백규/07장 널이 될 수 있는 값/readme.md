# 7장 널이 될 수 있는 값

## 7.1 NullPointerException을 피하고 값이 없는 경우 처리: 널 가능성

- `nullability` : kotlin 내 npe 피할 수 있는 타입 시스템 특성

## 7.2 널이 될 수 있는 타입으로 널이 될 수 있는 변수 명시

> Type? = `Type` 또는 `null`

- 기본적으로 모두 null이 아닌 type
- null이 될 수 있는 값을 null이 될 수 없는 타입 변수 할당 불가능

```kotlin
fun main() {
    val x: String? = null
    var y: String = x
    // ERROR: Type mismatch:
    // infered type is String? but String was expected
}
```

- npe 발생 가능성 코드가 들어가면 compile error
- npe 방어 로직을 넣어야지 compile success

## 7.3 타입의 의미 자세히 살펴보기

- 타입은 가능한 값의 집합과 그런 값들에 대해 수행할 수 있는 연산의 집합으로 정의
- in Java : String 타입 정의 &rarr; String, Null 정의 가능
- in Kotlin : String 타입 정의 &rarr; String, Null 정의 구분 가능

## 7.4 안전한 호출 연산자로 null 검사와 메서드 호출 합치기: ?.

> 호출 하려는 값이 null이 아니라면 `?.`는 일반 메서드 호출처럼 작동

- str?.uppercase()
    - if not null: str.uppercase()
    - it null: null

- null chaining 연쇄해서도 사용 가능

```kotlin
val country = this.company?.address?.country
```

## 7.5 엘비스 연산자로 null에 대한 기본값 제공: ?:

> `엘비스 연산자`: null 대신 기본값을 지정할 때 사용 가능한 연산자

```kotlin
fun greet(name: String?) {
    val recepient: String = name ?: "unnamed" // name이 null이면 결과는 unnamed
    println("Hello, $receipient!")
}
```

- 엘비스 연산자도 chaining 가능

```kotlin
fun Person.countryName() = company?.address?.country ?: "UnKnown"
```

## 7.6 예외를 발생시키지 않고 안전하게 타입을 캐스트하기: as?

- other as? Person
    - if not Person: null
    - if Person: other as Person

- java에서는 null check와 함께 instanceOf를 구현해야함
- kotlin에선 불편함 해소

## 7.7 널 아님 단언: !!

- str!! 
    - if not null: str
    - if null: NullPointerException

- !! 의미 : 근본적으로 null이 아님을 컴파일러에게 알림
- 어떤 값이 null임을 알게 하기 위해 !! chaining은 피하기를 권장

```kotlin
person.company!!.address!!.country // bad case
```

## 7.8 let 함수

> let &rarr; 자기 자신 참조, lambda 결과 반환

- email?.let { ...it... }
    - if email not null: lambda 식 내 not null
    - if email null: 아무일 x

<table>
<tr>
<td align="center">
not using let
</td>
<td align="center">
using let
</td>
</tr>
<tr>
<td>

```kotlin
if (email != null) sendEmailTo(email)
```
</td>
<td>

```kotlin
email?.let { email -> sendEmailTo(email) }

email?.let { sendEmailTo(it) } // 위와 동일
```
</td>
</tr>
</table>

## 7.9 직접 초기화하지 않는 널이 아닌 타입: 지연 초기화 프로퍼티

- lateinit : var 인 경우에만 사용 가능
- 초기화 전에 접근시 uninitializedPropertyAccessException 발생
- Kotlin 1.2.0 부터 lateinit var 이 초기화 되었는지 판단 가능한 isInitialized 추가

## 7.10 안전한 호출 연산자 없이 타입 확장: 널이 될 수 있는 타입에 대한 확장

```kotlin
fun verifyUserInput(input: String?) {
    if (input.isNullOrBlank()) { // do not need null check
        println("please fill in the required fields")
    }
}
```

- null chaining 없이 null safety한 function으로 대체 가능

## 7.11 타입 파라미터의 널 가능성

```kotlin
fun <T> printHashCode(t: T) {   // ← T는 Any? 
    println(t?.hashCode())
}
````

- Non-Null 정의를 위해서 타입 상한(upper bound) 을 지정

```kotlin
fun <T: Any> printHashCode(t: T) {   // ← T는 Any
    println(t?.hashCode())
}
```

## 7.12 널 가능성과 자바

### 7.12.1 플랫폼 타입

> `플랫폼 타입 (Platform Types)`, Kotlin이 널 관련 정보를 알 수 없는 타입<br>
> `자바(Type)` = `코틀린(Type? or Type)`

- ! 표기는 해당 타입이 널 가능성에 대해 아무 정보도 없다라는 뜻
- Kotlin는 (`@nullable`과 `@NotNull` 이 붙지 않은) Java에서 온 참조형은 모두 특별하게 Platform Type으로 처리

```java
public class TestPlatform {
    public static String getData() {
        return null;
    }
}
```

- java method를 아래 코틀린에서 호출

```kotlin
fun main(args: Array<String>) {
    val data1: String = TestPlatform.getData()   // ← Success, Type = String
    val data2: String? = TestPlatform.getData()  // ← Success, Type = String?
}
```

