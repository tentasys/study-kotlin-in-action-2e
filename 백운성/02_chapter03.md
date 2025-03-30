# 2주 3장 함수 정의와 호출 : 2025-03-10 ~ 2025-03-16

- 컬렉션, 문자열, 정규식을 다루기 위한 함수
- 이름 붙인 인자, 디폴트 파라미터 값, 중위 호출 문법 사용
- 확장 함수와 확장 프로퍼티를 사용해 자바 라이브러리를 코틀린에 맞게 통합
- 최상위 및 로컬 함수와 프로퍼티를 사용해 코드 구조화

## 코틀린 컬렉션

---
```kotlin
val set = setOf(1, 7, 53)
val list = listOf(1, 7, 53)
val map = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")
```
- 자바와 똑같은 클래스
- 자바보다는 더 많은 기능을 사용할 수 있음


## 함수를 호출하기 쉽게 만들기

---

### 이름붙인 인자
- 함수 파라미터에 이름을 사용하여 호출 시 파라미터 순서에 상관없이 호출 가능 (like python)
  ```kotlin
  fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
  ) : String { /* implement */ }
  
  // call by using parameter name
  joinToString(listOf(1, 3, 7), separator=";", prefix="(", postfir=")")
  ```
  
### 디폴트 파라미터 값
- 함수 파라미터에 default 값을 작성해 두면 값을 넣지 않아도 사용 가능 (like python)
  ```kotlin
  fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ",",
    prefix: String = "",
    postfix: String = ""
  ) : String { /* implement */ }
  
  // call
  joinToString(listOf(1, 3, 7), separator=";", prefix="(", postfir=")")
  // (1;3;7)
  joinToString(listOf(1, 3, 7), separator=">")
  // 1>3>7
  joinToString(listOf(1, 3, 7))
  // 1,3,7
  ```
  > 자바에서 호출하는 경우 함수 위에 `@JvmOverloads`를 추가하면 코틀린 컴파일러가 알아서 오버로드 함수를 생성함 

### 최상위 함수와 프로퍼티
- 
- 최상위 함수
  - 코틀린 특징으로 class에 함수를 만들지 않고 소스파일 최상위에 작성해서 사용할 수 있음
  - 자바처럼 별도의 불필요한 util class를 만들지 않아도 됨

- 최상위 프로퍼티
  - 함수와 마찬가지로 프로퍼티도 최상위 수준에 놓을 수 있음
  - java static 변수 값은 것
   
```kotlin
// file name: join.kt
package strings

// 최상위 프로퍼티
var callCount = 0

fun <T> joinToString(
  collection: Collection<T>,
  separator: String = ",",
  prefix: String = "",
  postfix: String = ""
) : String {
  callCount++  
}
```

## 확장 함수와 확장 프로퍼티

---

### 확장 함수
- 어떤 클래스의 멤버 메서드인 것처럼 호출할 수 있지만 그 클래스 밖에 선언된 함수
- 내부적으로 확장 함수는 수신 객체를 첫번째 인자로 받는 정적 메서드
- 문법적인 편의를 위한 기능이며, 클래스 또는 구체적인 타입을 수신 객체로 지정 가능
  - 예를 들어 String class에서 제공하는 메서드가 아지만 String class 밖에서 함수를 정의하여 String 메소드인 것처럼 사용 하는 기능
  ```kotlin
  package strings
  
  // String: 수신 객체 타입
  // this.get, this.length는 수신 객체
  fun String.lastChar(): Char = this.get(this.length - 1)
    
  // String class에서 제공하는 것 처럼 사용할 수 있음 
  // 기본 제공 함수
  prlntln("kotlin".length)
  // print => 6
    
  // 확장 함수
  println("kotlin".lastChar())
  // print => n
  ```
- final로 상속을 할 수 없게 선언된 경우에도 문제가 되지 않음
- import해서 사용해야 함
- this 생략 가능
- 자바에서 확장 함수를 호출 시 추가 비용 없이 호출 가능하며 파일명으로 클래스에 생성됨
  - StringUtil.kt 내 lastChar() -> StringUtilKt.lastChar()
- 확장 함수는 정적메소드 이므로 override 할수 없음

### 확장 프로퍼티
- 확장 프로퍼티는 함수가 아니라 프로퍼티 형식의 구분으로 사용가능하게 해주는 문법
- 커스텀 접근자를 통해 정의 
  - 자바에서 클래스내 메소드를 통해 외부에 내부 데이터를 제공하는 방법과 동일 (getter, setter)
  ```kotlin
  var StringBuilder.lastChar: Char
    get() = get(length - 1)
    set(value: Char) {
        this.setCharAt(length -1, value)
    }
  
  // use
  fun main() {
    val sb = StringBuilder("Kotlin?")
    println(sb.lastChar)
    // print => ?
    sb.lastChar = '!'
    println(sb.lastChar)
    // print => !
    println(sb)
    // print => Kotlin! 
  }
  ``` 

## 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원
- 자바 라이브러리 클래스의 인스턴스인 컬렉션에 대해 코틀린이 새로운 기능을 추가하여 사용할 수 있는건 last, max와 같은 추가 함수는 모두 확장 함수로 정의 되어 있고 항상 코틀린 파일에서 디폴트로 임포트 된다.
---

### 가변 인자 함수
- 자바의 메소드 파라미터에 ... 과 같은 기능
- 사용하고자 하는 메소드 앞에 keyword `vararg` 추가하여 사용
  ```kotlin
  // kotlin
  fun listOf<T>(vararg valus:T) List<T> { /* implement */ }
  
  // java
  public <T> List<T> listOf(T ... values) {
    return List.of(values);
  } 
  ```

### 쌍(튜플) 다루기: 중위 호출과 구조 분해 선언
- 중위 호출
  - 인자가 하나뿐인 일반 메서드나 인자가 하나뿐인 확장 함수에만 중위 호출을 사용 가능
  - 함수 앞에 keyword `infix`를 추가하여 중위 호출 가능
    ```kotlin
    infix fun Any.to(other: Any) = Pair(this, other)
  
    // use 
    // 아래 두가지 호출은 동일함 
    1.to("one")
    1 to "one"
    ```
- 구조 분해
  - Pair와 같은 코틀린 표준 라이브러리 클래스 및 map의 항목 key, value와 같은 경우 두 변수에 즉시 초기화 할 수 있음
    ```kotlin
    infix fun Any.to(other: Any) = Pair(this, other)
  
    // use 
    val (number, name) = 1 to "one"
    // number = 1
    // name = one
    
    // 이런게 withIndex리턴 값을 구조 분해를 통해 index, element로 초기화 함 
    for ((index, element) in collection.withIndex()) {
        println("$index: $element")
    }
    ```
    
## 문자열과 정규식 다루기
- 코틀린과 자바사이에 문자열 전달은 래퍼 객체도 생기지 않고 전혀 문제 없이 사용 가능함 
---

### 문자열 나누기
- split 메소드의 경우 자바와 다르게 명확하게 정규식과 문자열을 구분하여 사용 가능 함

### 정규식과 3중 따옴표로 묶은 문자열 
- `""" """`를 사용하면 어떤 문자도 이스케이프할 필요가 없음
  ```kotlin
  // kotlin
  var regx = """(.+)/(.+)\.(.+)""".toRegex()
  // java
  String regx = "(.+)/(.+)\\.(.+)";
  ```

## 로컬 함수와 확장
- DRY 원칙을 지키며 코드를 작성할때 코드를 리펙토링하면 클래스 안에 작은 메소드들이 많아지고 파악이 힘들어진다.
- 함수에서 추출한 함수를 원래의 함수 내부에 내포시켜 깔끔하게 조직화 할 수 있음
- 자바의 function이나 consumer FunctionalInterface를 통해 비슷하게 구현가능함 
 
---

## Conclusion

- 전반적으로 확장함수가 인상적이기 했지만 반드시 필요할까에 의문점이 듬 
- 이름붙인 인자 같은 경우 간략하게 name을 적어야해서 오히려 더 늘어남 
- final로 상속을 할 수 없게 선언된 경우에도 문제가 되지 않는데 java에서 final class의 경우 보안이나 설계 안정성이 중요한 경우 사용되는데 이걸 확장함수로 사용할 수 있으면 문제 있는거 아닌가?
- 대부분의 내용이 줄이거나 간략하게 사용할 수 있게 도와주는건데 요즘과 같은 좋은 IDE 사용 시대에 코드 한줄 덜 작성한다는게 큰 장점일까??
- `""" """`를 이용해서 이스케이프를 회피할수 있는건 괜찮았음