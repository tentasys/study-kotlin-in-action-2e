# 2장 코틀린 기초

## 2.1 기본 요소 : 함수와 변수

- 타입 선언 생략 가능
- 불변 데이터 사용 권장

### 2.1.1 첫 번째 코틀린 프로그램 작성 : Hello, World!

- 함수 &rarr; 모든 코틀린 파일의 최상위 수준 정의 가능
  - 최상위 수준이란, 모든 다른 클래스의 밖에 위치하는 함수
  - ex) util class, 확장 함수
- 최상위 main 함수 : 애플리케이션 진입점

### 2.1.2 파라미터와 반환값이 있는 함수 선언

<img src="./imgs/1.jpeg" alt="">

- main 함수 &rarr; 아무값도 반환 x

### 2.1.3 식 본문을 사용해 함수를 더 간결하게 정의

```kotlin
fun max(a: Int, b: Int): Int = if (a > b) a else b
```

- 중괄호 없앤 후 return 문 생략 가능

```kotlin
fun max(a: Int, b: Int) = if (a > b) a else b
```

- 반환 타입도 생략 가능
- `타입 추론` : 컴파일러가 함수 본문 식 분석하여 식 결과 타입 추론

### 2.1.4 데이터를 저장하기 위해 변수 선언

```kotlin
val question: String = "kotlin"
```

- 타입 명시

```kotlin
val question = "kotlin"
val answer = 42
```

- 타입 지정 x
- 컴파일러가 초기화 식 분석하여 변수 타입 지정

```kotlin
fun main() {
    
    val answer: Int
    answer = 42
}
```

- 변수 선언 시 **즉시 초기화 하지 않고 나중에 값 대입** 하고 싶을 때 컴파일러 **타입 추론 불가**
- 변수 선언 시 타입 명시 필요

### 2.1.5 변수를 읽기 전용 변순 재대입 가능 변수로 표시

#### val(= value)

- 읽기 전용 참조
- 한번만 대입 가능
- java의 final과 동일

```kotlin
fun main() { 
    val languages = mutableListOf("java")  // 읽기 전용 참조 선언
    languages.add("kotlin")                // 원소 하나 추가 가능
}
```

#### var(= variable)

- 재대입 가능 참조
- 초기화 이뤄진 다음에도 다른 값 대입 가능
- 변수 타입은 고정

```kotlin
fun main() {
    var answer = 42
    answer = "no answer" // type mismatch compile error occur 
}
```

- 컴파일러가 기대하는 타입(= int) 다름 &rarr; 컴파일 에러 발생
- 변수 선언 이후 재대입 시에는 이미 추론한 변수 타입으로 검사

## 2.2 행동과 데이터 캡슐화: 클래스와 프로퍼티

<table>
<tr>
<td align="center">java</td>
<td align="center">kotlin</td>
</tr>
<tr>
<td>

```java
public class Person {
    private final String name;
    
    public Person(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
```

</td>
<td>

```kotlin
class Person(val name: String)
```

</td>
</tr>
</table>

- kotlin 기본 접근자 : public

### 2.2.1 클래스와 데이터를 연관시키고, 접근 가능하게 만들기: 프로퍼티

- property in java : 필드, 접근자
- property in kotlin : 필드와 접근자(= getter, setter) 메서드를 자동으로 생성해주는 문법

```kotlin
class Person(
    val name: String,           // 읽기 전용 프로퍼티, getter만 생성
    var isStudent: Boolean      // 읽기/쓰기 프로퍼티, getter, setter 생성
)
```

```kotlin
fun main() {
    val person = Person("Bob", true) // new 선언 x
    println(person.name) // getter 자동 호출
  
    person.isStudent = false // setter 자동 호출
}
```

### 2.2.2 프로퍼티 값을 저장하지 않고 계산: 커스텀 접근자

```kotlin
class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
      get() {                        // property getter 선언
          return height == width
      }
}
```

- property getter나 클래스 내 파라미터 없는 함수 정의 &rarr; 의미는 같지만 성능 차이 x
  - 클래스 특성 표현 목적 &rarr; 프로퍼티
  - 클래스 행동 표현 목적 &rarr; 멤버 함수

## 2.3 선택 표현과 처리: 이넘과 when

### 2.3.2 when으로 이넘 클래스 다루기

```kotlin
fun getMnemonic(color: Color) {
    when (color) {
        Color.Red -> "Red"
        Color.Orange -> "Orange"
        Color.Blue -> "Blue"
    }
}
```

```kotlin
fun getWarmthFromSensor(): String {
    val color = measureColor()
    return when (color) {
      Color.Red, Color.Orange, Color.Blue -> "warm(red = ${color.r})"
  }
}
```

- 하나의 when 절 안에 여러 값 사용 가능

```kotlin
fun getWarmthFromSensor() = when (val color = measureColor()) {
      Color.Red, Color.Orange, Color.Blue -> "warm(red = ${color.r})"
  }
}
```

- when 절 내에서 프로퍼티 접근 가능

### 2.3.6 스마트 캐스트: 타입 검사와 타입 캐스트 조합

> 스마트 캐스트란, 이 원하는 타입으로 캐스팅 하지 않더라도, 컴파일러가 알아서 캐스팅

```kotlin
fun eval(e: Expr): Int{
    if (e is Num){
        val n = e as Num // 불필요한 타입 변환
        return n.value
    }

    if (e is Sum){
        return eval(e.left) + eval(e.right) // 변수 e에 대한 스마트 캐스트
    }

    throw IllegalArgumentException("Unknown expression")
}
```

<table>
<tr>
<td ALIGN="CENTER">if문</td>
<td ALIGN="CENTER">when문</td>
</tr>
<tr>
<td>

```kotlin
fun eval2(e: Expr): Int =
    if (e is Num) {
        e.value
    } else if (e is Sum) {
        eval2(e.left) + eval2(e.right)
    } else {
        throw java.lang.IllegalArgumentException("Unknown expression")
    }

fun main(arg: Array<String>){
    println(eval2(Sum(Sum(Num(3), Num(3)), Num(4))))
}
```

</td>

<td>

```kotlin
fun eval3(e: Expr): Int =
        when(e) {
            is Num -> e.value
            is Sum -> eval3(e.left) + eval3(e.right)
            else -> throw IllegalArgumentException("Unknown expression")
        }
fun main(arg: Array<String>){
    println(eval3(Sum(Sum(Num(10), Num(7)), Num(3))))
}
```
</td>
</tr>
</table>

## 2.4 대상 이터레이션: while과 for 루프

### 2.4.2 수에 대한 이터레이션: 범위와 순열

```kotlin
val oneToTen = 1..10

fun main() {
    for (i in 1..100) {
        print(i)
    }
}
```

- 코틀린에선 범위 사용 &rarr; `..` 연산자 사용

```kotlin
fun main() {
    
    for (i in 100 downTo 1 step 2) {
        print(i) // 100, 98, 96, ...
    }
}
```

## 2.5 코틀린에서 예외 던지고 잡아내기

```kotlin
val percentage = if (number in 0..100) 
    number
else throw IllegalArgumentException("exception : $number")
```

- throw는 식임으로 다른 식에 포함 가능

### 2.5.1 try, catch, finally를 사용한 예외 처리와 오류 복구

<img src="https://velog.velcdn.com/images/ekxk1234/post/fea34902-f2d4-4801-adba-19b362185677/image.png" alt="" />

- kotlin &rarr; checked exception, unchecked exception 구별 x
- checked exception 잡아내도록 강제하지 않아 컴파일 에러 발생 x
- run catching 같은 stdlib 사용하여 예외 처리

```kotlin
// requireNotNull() 예시

enum class Operator(val op: String) {
	PLUS("+"), MINUS("-")
}

// 적용 전
fun convertToOperator(op: String): Operator {
    return values().firstOrNull { it.op == op } ?: throw IllegalArgumentException("사칙연산 기호가 아닙니다.")
}

// 적용 후
fun convertToOperator(op: String): Operator {
    return requireNotNull(values().firstOrNull { it.op == op }) { "사칙연산 기호가 아닙니다." }
}
```

```kotlin
class BleDevice {
    var connectedState: String? = null

    fun connect(connectJob: () -> Unit) {
        // 적용 전
        val state = if (connectedState == null) {
            throw IllegalStateException("State is null")
        } else {
            connectedState
        }
        // 적용 후
        val state2 = checkNotNull(connectedState)
        
        connectJob()
    }
}
```

- `require` : argument 제한 
- `check` : 상태와 관련된 동작 제한 
- `assert` : 어떤 것이 true인지 확인, assert 테스트 모드에서만 작동 
- return 또는 throw와 함께 활용하는 Elvis 연산자