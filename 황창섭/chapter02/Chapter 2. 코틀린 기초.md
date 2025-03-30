# Chapter 2. 코틀린 기초

- 함수, 변수, 클래스, 이넘, 프로퍼티를 선언하는 방법
- 코틀린 제어 구조
- 스마트 캐스트
- 예외 던지기와 예외 잡기

## 2.1 기본 요소: 함수와 변수

### 2.1.1 첫 번째 코틀린 프로그램 작성: Hello, World!

```kotlin
fun main() {
    println("Hello, world!")
}
```

- 함수 선언: fun
- 코틀린의 표준 라이브러리는 수많은 자바 표준 라이브러리에 대해 간결한 구분 제공: println
- 줄 끝 세미콜론 비권장

### 2.1.2 파라미터와 반환값이 있는 함수 선언

```kotlin
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}
```

- 코틀린에서 if는 결과를 만드는 식
- 함수 반환 타입은 파라미터 목록을 닫는 괄호 다음에 옴

> 문과 식의 구분
>
> 코틀린에서는 루프(for, while, do/while)를 제외한 대부분의 제어 구조가 식이다.
>
> 반면 대입은 항상 문으로 취급한다. 아래는 코틀린에서 틀린 문법.
>
> val number = i = getNumber()

### 2.1.3 식 본문을 사용해 함수를 더 간결하게 정의

```kotlin
fun max(a: Int, b: Int): Int = if (a > b) a else b
```

- 함수 본문이 중괄호로 둘러싸인 것을 block body function 이라 부르고 등호와 식으로 이뤄진 함수를 expression body function 이라 함.
- 코틀린에서는 식 본문 함수를 자주 사용.

- 식 본문 함수는 아래와 같이 반환 타입을 생략할 수 있음.

```kotlin
fun max(a: Int, b: Int) = if (a > b) a else b
```

- 코틀린은 정적 타입 지정 언어이지만 반환 타입을 생략할 수 있음 => 컴파일러가 타입 추론(type inference)를 해주기 때문
- 블록 본문 함수는 블록 내 여러 번 return 할 수 있으므로 반환타입 지정 필수

### 2.1.4 데이터를 저장하기 위해 변수 선언

```kotlin
// 반환 타입 지정
val question: String = "스트링"
val answer: Int = 42

// 타입 추론
val question = "스트링"
val answer = 42
```

```kotlin
/**
 * 변수를 선언하면서 초기화하지 않을 경우 추론이 불가능하기에 변수 타입 지정이 필요
 */
fun main() {
    val answer: Int
    answer = 42
}
```

### 2.1.5 변수를 읽기 전용 변수나 재대입 가능 변수로 표시

- **val**은 읽기 전용 참조(read-only reference)를 선언한다. val로 선언된 변수는 단 한 번만 대입될 수 있다.(final과 동일)
- **var**는 재대입 가능한 참조(reassignable reference)를 선언한다(. 초기화가 이뤄진 다음에도 다른 값을 대입할 수 있다.
- 기본적으로 모든 변수를 val로 선언하고 필요할 때에만 var로 변경.
- var 키워드로 변수의 값은 변경 가능하지만 타입은 고정된다.

### 2.1.6 더 쉽게 문자열 형식 지정: 문자열 템플릿

```kotlin
fun main() {
    val input = readln()
    val name = if (input.isNotBlank()) input else "Kotlin"
    println("Hello, $name!")
}
```

- 스크립트 언어와 비슷하게 변수 이름 앞에 $를 붙여 변수를 문자열 안에 참조할 수 있다.
- $ 문자를 넣고 싶으면 백슬래시(\) 로 이스케이프 처리 필요.

```kotlin
fun main() {
    val input = readln()
    val name = if (input.isNotBlank()) input else "Kotlin"
    println("Hello, ${name.length}-letter person!")
}
```

- 중괄호로 복잡한 식도 사용가능하다.
- 그리고 length 사용 방법이 java와는 다르다.

> **한글을 문자열 템플릿에서 사용할 경우 주의점**
>
> 중괄호를 사용해 다음과 같이 표현해야함. "${name}님 반가워요!"

```kotlin
fun main() {
    val input = readln()
    println("Hello, ${if (input.isNotBlank()) input else "Kotlin"}-letter person!")
}
```

- 위와 같이도 사용 가능.

## 2.2 행동과 데이터 캡슐화: 클래스와 프로퍼티

```java
/**
 * 간단한 자바 클래스 Person
 */
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

```kotlin
/**
 * 코틀린으로 변환한 Person 클래스
 */
class Person(val name: String)
```

- 위에서 보듯 코틀린의 기본 가시성은 **public** 이다!
- lombok을 사용하거나 record를 사용할 경우 큰 차이는 없는 것 같기도 함.

### 2.2.1 클래스와 데이터를 연관시키고, 접근 가능하게 만들기: 프로퍼티

- 자바에서 멤버 필드는 보통 private이고 필드 접근을 위해 접근자 메서드(getter, setter)를 제공
- 자바에서 필드와 접근자를 한데 묶어 **프로퍼티**라 표현.
- 코틀린은 언어 기본 기능으로 제공하며 var, val에 따라 접근자가 달라짐

```kotlin
class Person(
    val name: String, // (비공개) 필드, (공개) getter 생성
    var isStudent: Boolean // (비공개) 필드, (공개) getter, (공개) setter 생성
)
```

```java
/**
 * 자바에서 Person 클래스를 사용하는 방법
 */
public class Demo {
    public static void main(String[] args) {
        Person person = new Person("Bob", true);
        System.out.println(person.getName());
        System.out.println(person.isStudent());
        person.setStudent(false);
    }
}
```

- java와 kotlin에서 정의한 Person 클래스 중 어느 쪽을 사용해도 코드를 바꿀 필요는 없다.
- name 프로퍼티는 getName이라는 메소드로 노출
- is로 시작할 경우 get이 붙지 않고 getter에선 그대로 이용(ex. isStudent())
- is로 시작할 경우 is를 set으로 바꿔서 사용(ex. setStudent())

```kotlin
/**
 * 코틀린에서 Person 클래스를 사용하는 방법
 */
fun main() {
    val person = Person("Bob", true)
    println(person.name)
    println(person.isStudent)
    person.isStudent = false
}
```

### 2.2.2 프로퍼티 값을 저장하지 않고 계산: 커스텀 접근자

```kotlin
class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() { // 프로퍼티 getter 선언
            return height == width
        }
    // get() = height == width 와 같은 형식으로도 사용 가능
}
```

- 커스텀 게터와 멤버 함수를 정의하는 방식 중 우열은 없음.
- 일반적으로 클래스의 특성을 기술하고 싶다면 프로퍼티로 정의하고 행동을 기술하고 싶다면 멤버 함수를 선택

### 2.2.3 코틀린 소스코드 구조: 디렉터리와 패키지

```kotlin
/**
 * 클래스와 함수 선언을 패키지 안에 넣기
 */
package geometry.shapes

class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() = height == width
}

fun createUnitSquare() = Rectangle(1, 1)
```

```kotlin
/**
 * 다른 패키지에 있는 함수 임포트 하기
 */
package geometry.example

import geometry.shapes.Rectangle
import geometry.shapes.createUnitSquare

fun main() {
    println(Rectangle(3, 4).isSquare)
    println(createUnitSquare.isSquare)
}
```

- 자바에서는 패키지의 구조와 일치하는 디렉터리 계층구조가 필요.
- 또한 클래스와 동일한 파일명을 지정해야하고 함수만 따로 둘 수 없음
- 하지만 코틀린에서는 여러 클래스를 같은 파일에 넣을 수 있고 파일의 이름도 원하는 대로 정할 수 있다.
- 하지만 대부분의 경우 자바와 같이 구성하는 편이 나음(자바와 함께 있는 프로젝트일 경우 더더욱)

### 2.3 선택 표현과 처리: enum과 when

### 2.3.1 enum 클래스와 enum 상수 정의

```kotlin
/**
 * 간단한 이넘 클래스 정의
 */
enum class Color {
    RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
}
```

- 프로퍼티가 있는 이넘 클래스 선언

```kotlin
/**
 * 프로퍼티가 있는 이넘 클래스 선언
 */
enum class Color(
    val r: Int,
    val g: Int,
    val b: Int
) {
    RED(255, 0, 0),
    ORANGE(255, 165, 0),
    YELLOW(255, 255, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255),
    INDIGO(75, 0, 130),
    VIOLET(238, 130, 238); // 반드시 세미콜론 사용 필요

    fun rgb() = (r * 256 + g) * 256 + b
    fun printColor() = println("$this is ${rgb()}")
}
```

### 2.3.2 when으로 이넘 클래스 다루기

- 자바의 switch에 대응
- if와 마찬가지로 값을 만드는 식

```kotlin
/**
 * when을 사용해 올바른 이넘 값 찾기
 */
fun getMnemonic(color: Color) =
    when (color) {
        Color.RED -> "Richard"
        Color.ORANCE -> "Of"
        Color.YELLOW -> "York"
        Color.GREEN -> "Gave"
        else -> "Else"
    }
```

```kotlin
/**
 * 하나의 when 분기 안에 여러 값 사용하기
 */
fun measureColor() = Color.ORANCE

fun getWarmthFromSensor(): String {
    val color = measureColor()
    return when (color) {
        RED, ORANGE, YELLOW -> "warm (red = ${color.r})"
        GRREN -> "neutral (green = ${color.g})"
        BLUE, INDIGO, VIOLET -> "cold (blue = ${color.b})"
    }
}
```

### 2.3.3 when식의 대상을 변수에 캡처

```kotlin
fun measureColor() = Color.ORANCE

fun getWarmthFromSensor(): String {
    return when (val color = measureColor()) {
        Color.RED, Color.ORANGE, Color.YELLOW -> "warm (red = ${color.r})"
        Color.GRREN -> "neutral (green = ${color.g})"
        Color.BLUE, Color.INDIGO, Color.VIOLET -> "cold (blue = ${color.b})"
    }
}
```

- 변수의 영역이 when 식의 본문으로 제한
- 컴파일러는 when 모든 가능한 경로에서 값을 만드는지 체크(exhaustive)

### 2.3.4 when의 분기 조건에 임의의 객체 사용

```kotlin
/**
 * when의 분기 조건에 다른 여러 객체 사용하기
 */
fun mix(c1: Color, c2: Color) =
    when (setOf(c1, c2)) {
        setOf(RED, YELLOW) -> ORANGE
        setOf(YELLOW, BLUE) -> GREEN
        setOf(BLUE, VIOLET) -> INDIGO
        else -> throw Exception("Dirty color")
    }
```

- setOf = Set 생성
- 해당 예제에서 조건은 Set의 동등성 검사

### 2.3.5 인자 없는 when 사용

```kotlin
/**
 * 코드의 가독성은 떨어지지만 성능(불필요한 객체 생성 방지)을 위해 사용
 */
fun mixOptimized(c1: Color, c2: Color) =
    when {
        (c1 == RED && c2 == YELLOW) || (c1 == YELLOW && c2 == RED) -> ORANGE

        (c1 == YELLOW && c2 == BLUE) || (c1 == BLUE && c2 == YELLOW) -> GREEN

        (c1 == VIOLET && c2 == BLUE) || (c1 == BLUE && c2 == VIOLET) -> INDIGO

        else -> throw Exception("Dirty color")
    }
```

### 2.3.6 스마트 캐스트: 타입 검사와 타입 캐스트 조합

- 트리 구조로 합산 설계
    - 노드는 sum 또는 num
    - num은 leaf 노드
    - sum은 자식이 둘 있는 중간 노드

- 식을 위한 Expr interface가 있고 sum, num이 이를 구현
- Expr 은 아무 메서드도 선언하지 않음 -> 여러 타입의 식 객체를 아우르는 공통 타입 역할만 수행하는 마커 인터페이스

```kotlin
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr
```

- (1+2) + 4 라는 식은 Sum(Sum(Num(1), Num(2)), Num(4))
- Expr 인터페이스의 2가지 구현체
    - 어떤 식이 수라면 그에 해당하는 값을 반환한다.
    - 어떤 식이 합계라면 좌항 값을 재귀적으로 계산하고 우항 값도 재귀적으로 계산한 다음 두 값을 합한 값을 반환한다.

```kotlin
/**
 * if 연쇄를 사용해 식을 계산하기
 */
fun eval(e: Expr): Int {
    if (e is Num) {
        val n = e as Num
        return n.value
    }

    if (e is Sum) {
        return eval(e.right) + eval(e.left)
    }

    throw IllegalArgumentException("Unknown expression")
}
```

- 코틀린의 is 검사는 약간의 편의를 추가로 제공
- 어떤 변수의 타입을 확인하였으면 해당 타입의 멤버 접근을 명시적으로 변수 타입을 변환하지 않아도 된다! 컴파일러가 대신 변환(**스마트 캐스트**)
- 스마트 캐스트는 is로 변수에 든 값의 타입을 검사한 다음, 값이 바뀔 수 없는 경우에만 작동

### 2.3.7 리팩터링: if를 when으로 변경

```kotlin
/**
 * 값을 만들어내는 if 식
 */
fun eval(e: Expr): Int =
    if (e is Num) {
        e.value
    } else if (e is Sum) {
        eval(e.right) + eval(e.left)
    } else {
        throw IllegalArgumentException("Unknown expression")
    }
```

```kotlin
/**
 * if 연쇄 대신 when 사용하기
 */
fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
        else -> throw IllegalArgumentException("Unknown expression")
    }
```

### 2.3.8 if와 when의 분기에서 블록 사용

- 블록의 마지막 문장이 블록 전체의 결과가 된다.

```kotlin
/**
 * 분기에 복잡한 동작이 들어가 있는 when 사용하기
 */
fun evalWithLogging(e: Expr): Int =
    when (e) {
        is Num -> {
            println("num: ${e.value}")
            e.value
        }
        is Sum -> {
            val left = evalWithLogging(e.left)
            val right = evalWithLogging(e.right)
            println("num: $left + $right")
            left + right
        }
        else -> throw IllegalArgumentException("Unknown expression")
    }
```

## 2.4 대상 이터레이션: while과 for 루프

### 2.4.1 조건이 참인 동안 코드 반복: while 루프

- while 문은 자바와 동일
- nested loop 일 경우 레이블 지정 가능

```kotlin
/**
 * 레이블 예제
 */

outer@ while (outerCondition) {
    while (innerCondition) {
        if (shouldExitInner) break
        if (shouldSkipInner) break
        if (shouldExit) break@outer
        if (shouldSkip) break@outer
    }
}
```

### 2.4.2 수에 대해 이터레이션: 범위와 순열

- 코틀린에서 범위라는 개념이 있음 -> val oneToTen = 1..10
- 코틀린의 범위는 양끝을 포함하는 구간

```kotlin
/**
 * when을 사용해 피즈버즈 게임 구현하기
 */
fun fizzBuzz(i: Int) = when {
        i % 15 == 0 -> "피즈버즈"
        i % 3 == 0 -> "피즈"
        i % 5 == 0 -> "버즈"
        else -> "$i"
    }

fun main() {
    for (i in 1..100) {
        print(fizzBuzz(i))
    }
}
```

```kotlin
/**
 * 증가 값으로 범위 이터레이션하기
 */
fun main() {
    for (i in 100 downTo 1 step 2)
        print(fizzBuzz(i))
}
```

- 100 downTo 1은 역방향 순열을 만듬
- step 2 를 붙이면 증가 값의 방향은 그대로 유지하면서 절대값만 2로 바꿈
- 끝 값을 포함하지 않는 범위를 만들고 싶으면 **..<** 연산을 사용

### 2.4.3 맴에 대해 이터레이션

```kotlin
/**
 * 컬렉션에 대해 이터레이션하기
 */
fun main() {
    val collection = listOf("red", "green", "blue")
    for (color in collection) {
        print("$color ")
    }
}
```

```kotlin
/**
 * 맵을 초기화하고 이터레이션하기
 */

fun main() {
    val binaryReps = mutableMapOf<Char, String>()
    for (char in 'A'..'F') {
        val binary = char.code.toString(radix = 2) // 아스키 코드를 2진 표현으로 바꾼다
        binaryReps[char] = binary
    }

    for ((letter, binary) in binaryReps) {
        println("$letter = $binary")
    }
}
```

```kotlin
/**
 * 인덱스와 함께 컬렉션을 이터레이션 하는 방법
 */
fun main() {
    val collection = listOf("red", "green", "blue")
    for ((index, element) in collection.withIndex()) {
        print("$index: $element ")
    }
}
```

### 2.4.4 in으로 컬렉션이나 범위의 원소 검사

```kotlin
/**
 * in과 !in 사용해 값이 범위에 속하는지 검사하기
 */

fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
fun isNotDigit(c: Char) = c !in '0'..'9'

fun main() {
    println(isLetter('q')) // true
    println(isNotDigit('x')) // true
}
```

```kotlin
/**
 * when 가지에서 in 검사 사용하기
 */
fun recognize(c: Char) = when (c) {
        in 'a'..'z', in 'A'..'Z' -> "letter"
        !in '0'..'9' -> "not digit"
        else -> "I don't know"
    }
```

- 범위는 문자에만 국한되지 않고 비교가 가능한 클래스라면 모두 가능
- 비교가 가능한 클래스 범위라면, 범위 내의 모든 객체를 이터레이션 할 수는 없으나, in 연산자로 범위 안에 속하는지는 항상 결정할 수 있음

```kotlin
println("Kotlin" in "Java".."Scala") // true
```

- String에서 두 문자열의 알파벳 순서로 비교했을 때 true가 나옴

## 2.5 코틀린에서 예외 던지고 잡아내기

- 코틀린에는 new 키워드가 없는 점을 제외하고 자바의 try-catch와 매우 유사
- 자바와 달리 코틀린의 throw는 식으므로 다른 식에 포함될 수 있다.

```kotlin
val percentage =
    if (number in 0..100)
        number
    else
        throw IllegalArgumentException("A percentage value must be between 0 and 100: $number")
```

### 2.5.1 try, catch, finally를 사용한 예외 처리와 오류 복구

```kotlin
/**
 * 자바와 마찬가지로 try 사용하기
 */

fun readNumber(reader: BufferedReader): Int? {
    try {
        val line = reader.readLine()
        return Integer.parseInt(line)
    } catch (e: NumberFormatException) {
        return null
    } finally {
        reader.close()
    }
}
```

- 자바 코드와 가장 큰 차이는 throws 절이 코틀린에 없다는 점
    - Integer readNumber(BufferedReader reader) throws IOException
    - 자바에서는 체크 예외가 메서드 시그니처의 일부

- 코틀린은 checked exception과 unchecked exception을 구분하지 않음
- 즉, 코틀린에서는 컴파일러가 예외 처리를 강제하지 않음

### 2.5.2 try를 식으로 사용

```kotlin
/**
 * try를 식으로 사용하기
 */
fun readNumber(reader: BufferedReader) {
    val number = try {
        Integer.parseInt(reader.readLine())
    } catch (e: NumberFormatException) {
        return
    }
    println(number)
}
```

```kotlin
/**
 * catch에서 값 반환하기
 */
fun readNumber(reader: BufferedReader) {
    val number = try {
        Integer.parseInt(reader.readLine())
    } catch (e: NumberFormatException) {
        null
    }

    println(number)
}
```

- 첫 번째 예제에서는 에러가 발생되면 바로 종료되고 두 번째 예제에서는 null이 출력
