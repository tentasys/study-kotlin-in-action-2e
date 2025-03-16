# 1주 2장 코틀린 기초 : 2025-03-03 ~ 2025-03-11

- Kotlin 이란 무엇인가?
- 나는 Java 개발자니까 java와 비교해서 장/단점을 생각해보자.
  - 진정 간결한지, type safe한지 등...
- 코틀린스러운 (idiomatic Kotlin) 이라는 말의 의미를 생각해보자.

## 기본요소

---

- 세미콜론 (;) 생략 가능
- class 내 선언할 필요없이 파일 최상위 수준에 정의 가능
- if 문은 자바에서 삼항다항식과 같은 식이며 문이 아니라서 값을 만들어 내는 역활로 사용 가능
  - 문법이 아니라 단순 식이라 그냥 함수같은거라고 생각하면 좋을 것 같음
  - 그자체를 이용해서 값을 리턴해서 변수 또는 함수에 값을 전달할 수 있음
- 삼항 연산자가 없음

### 함수와 변수

- 함수 (메소드)
  ```
  fun main() {
    println("Hello, world!)
  }
  ```
  - 선언시 `fun` 사용
  - main 함수는 parameter가 있어도 되고 없어도 됨
    - 최상위에 있는 main 함수를 진입점으로 지정
  - parameter가 있는 함수는 () 안에 name: type 으로 정의 
    ```
    fun print(a: String, b: Int) { ... }
    ```
  - return이 있는 함수일 경우 () 뒤에 콜론 (:)으로 구분하고 return type을 정의
    ```
    fun max(a: Int, b: Int) : Int { return if (a > b) a else b }
    ```
    > 단, 식을 이용해서 함수를 정의할 경우 type 정의 생략 가능 (컴파일러가 알아서 타입 추론)
    > 
    > fun max(a: Int, b: Int) = if (a > b) a else b
- 변수
  ``` 
  val question: String = "what is your name?"
  var answer: String = "bbaek"
  
  var age: Int
  age = 40
  
  var age = 40 // Int
  ```
  - `val` : value의 약자로 readonly, Java의 final과 동일
  - `var` : variable의 약자로 수정 가능한 일반적인 변수
  - type을 지정하지 않으면 초기화식의 type을 보고 알아서 지정
  - 문자열 템플릿 기능을 이용해 문자열에 변수를 쉽게 사용할 수 있음 `python이랑 비슷`
    - `$`로 변수 사용
      ```
      val name: String = "bbaek"
      println("Hello, $name!) // Hello, bbaek! 
      
      val name: String = "bbaek"
      println("Hello, ${name}!) // Hello, bbaek!
      ```
    - `${}`를 사용하여 변수의 값 또는 함수 이용 가능 (식도 사용할 수 있음)
      ```
      val name: String = "bbaek"
      println("Hello, ${name.length}!) // Hello, 5!
  
      val name: String
      println("Hello, ${if (name.isBlank()) "anon" else name}!) // Hello, anon!
      ```

### 클래스와 프로퍼티

```
// Java
public class Person {
  private final String name;
  // Constructor
  // Getter
}
  
// Kotlin
class Person(val name: String)
```
- new 키워드를 사용하지 않고 class instance 생성 가능 
  ``` 
  val person = Person("bbaek") 
  ```
- 기본 modifier가 public이라서 생략 가능
- Java record와 비슷함 
- 변수를 val로 정의하면 getter만 제공하고 var로 정의하면 getter, setter 모두 제공
- 값을 저장하지 않는 property 정의 가능 `c#과 비슷`

### 디랙토리와 패키지
```
package com.b2soft

import com.b2soft.Person // import class
import com.b2soft.isPerson // import method
```
- Java와 동일하게 파일의 맨 앞에 package 문의 올 수 있음
- 다른 package이 있는 클래스, 함수등을 사용하려면 import를 사용해야함 
- package 이름 뒤에 .*를 추가하면 패키지내 모든 선언을 import 할 수 있음 
  > Kotlin은 package와 디렉토리 구조가 맞지 않아도 되지만 그냥 Java처럼 구조를 맞춰서 사용하는게 나아보임

### enum 과 when
- enum
  ```
  enum class Color {
    RED, GREEN, BLUE
  }
  
  enum class Color(
    val r: Int,
    val g: Int,
    val b: Int
  ) {
    RED(255, 0, 0), 
    GREEN(0, 255, 0), 
    BLUE(0, 0, 255);
    
    fun rgb() = (r * 256 * g) * 256 + b
    fun printColor() = println("$this is $rgb")
  }
  ```
  - `enum class`로 사용 함
    - enum의 soft keyword이며 변수명으로 사용 가능
    - Java랑 비슷함
    > property가 있는 enum 정의 시 상수 마지막에 반드시 세미콜론 추가 
    
- when
  ```
  fun measureColor() = RED
  
  // parameter
  fun getWarmthFromSensor() = 
    when (val color = measureColor()) {
      Color.RED -> "warm (red = ${color.r})"
      Color.GREEN -> "neutral (green = ${color.g})"
      Color.BLUE -> "cold (blue = ${color.b})" 
    } 
  
  // no parameter
  fun getWarmthFromSensor() =
    when {
      measureColor() == Color.RED -> "warm (red = ${color.r})"
      measureColor() == Color.GREEN -> "neutral (green = ${color.g})"
      measureColor() == Color.BLUE -> "cold (blue = ${color.b})" 
    } 
    when (val color = measureColor()) {
      Color.RED -> "warm (red = ${color.r})"
      Color.GREEN -> "neutral (green = ${color.g})"
      Color.BLUE -> "cold (blue = ${color.b})" 
    } 
  ```
  - Java switch와 비슷
  - break를 넣지 않아도 됨
  - when도 if와 같은 식
  - when 인자 없이도 사용가능

### 타입 검사와 스마트 캐스트 조합

- Java의 instanceof와 같은 c#의 `is`를 사용
- 타입 검사 후 명시적으로 cast을 하지 않아도 컴파일러가 알아서 cast해주는데 이걸 스마트 cast라고 함
  ```
    if (e is Num) {
    val n = e as Num // 불필요, 검사한 type으로 자동 cast 해줌 
    return n.value
  }
  ```
  > 스마트 캐스트 사용시 변수는 반드시 `val` 을 사용해야 함 

### while과 for 루프
- 기본적으로 java와 c#과 비슷함
- 레이블 `@를 사용`을 지정할 수 있고 `break`나 `continue`를 사용할 때 레이블을 참조할 수 있음
  ```
  outer@ while (outerCondition) {
    while (innerCondition) {
      break
      continue
      // 레이블을 사용하면 지정한 루프를 바로 빠져나갈 수 있음 
      break@outer
      continue@outer
    }
  }
  ```
- 특정 값을 증가시켜 조건에 맞을때 종료하는 고전적인 for 문
  ```
  // java
  for (int i = 1; i <= 10; i++) {
  }
  // kotlin
  for (i in 1..10) { // 1 ~ 10
  }
  
  // java
  for (int i = 10; i > 0; i-2) {
  }
  // kotlin
  for (i in 10 downTo 1 step 2) { // 10, 8, 6, 4
  }
  ```
- map의 key, value도 사용 가능 
  ```
  val map = mutableMapOf<Char, String>()
  for ((key, value) in map) {
  }
  ```
- list를 사용할때 withIndex()를 이용하여 index 사용 가능
  ```
  val list = listOf("10", "100", "1000")
  for ((index, element) in list.withIndex()) {
  }
  ```
- Collection에서 java의 contains와 같은 함수는 존재하지 않음

### 예외처리

- 자바와 비슷함
- try, catch, finally
- 함수에 throws를 명시할 필요가 없음
- try, catch도 if 처럼 식으로 사용 가능, 단 if와 달리 {}가 반드시 필요함

## Conclusion

- 전체적으로 변수와 함수 구분없이 사용하는 것 같음
- Java에서 대부분 되는 것이지만 간단하게 여러줄 할것을 한줄또는 간단하게 줄여줌 
- 비슷한 것들이 많아 오히려 혼란스러움
- 안되면 그냥 안되고 되면 되야하는데 사용자에게 선택권을 주어 개발자의 성향에 따라 가속성이 떨어질 수 있을듯 함
- 잘쓰면 타이핑이 줄어 편할것 같음
- 아직 처음이라 잘은 모르지만 기본은 java인데 python과 javascript들을 교묘하게 섞어놓은듯함 느낌 