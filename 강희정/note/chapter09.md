# 9장. 연산자 오버로딩과 다른 관례

코틀린 → 언어 기능이 특정 함수 이름과 연관됨

⇒ 관례!

자바 → 언어 기능을 타입에 의존

코틀린 → 기능을 관례에 의존 → 기존 자바 클래스를 코틀린 언어에 적용하기 위함

- 기존 자바 클래스에 대해 확장 함수를 구현하면서 관례에 따라 이름을 붙이면 → 자바 코드를 바꾸지 않아도 새로운 기능 부여 가능

# 9.1 산술 연산자 오버로드

## 9.1.1 plus, times, divide 등: 이항 산술 연산 오버로딩

operator 키워드

- 산술 연산자 오버로딩 시 필요

```kotlin
// 멤버 함수 정의
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

// 확장 함수 정의
operator fun Point.plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
}
```

- 두 개의 Point를 +를 통해 더할 수 있게 됨 (plus 함수 호출)

- 프로그래머가 직접 연산자를 만들어 사용할 수 없고, 언어에서 미리 정해 둔 연산자만 오버로딩 가능.
    - 오버로딩 가능한 이항 산술 연산자
        
        
        | 식 | 함수 이름 |
        | --- | --- |
        | a * b | times |
        | a / b | div |
        | a % b | mod |
        | a + b | plus |
        | a - b | minus |
- 연산자 우선순위는 표준 숫자 타입 연산자 우선순위와 같다.
- 자바를 코틀린에서 호출하는 경우 - 자바 함수 이름이 코틀린의 관례에 맞아떨어지면 연산자를 통해 호출 가능
    - operator 안 붙여도 되고 이름과 파라미터만 같으면 됨
- 두 피연산자가 같은 타입일 필요는 없음
- 코틀린 연산자가 자동으로 교환법칙을 지원하지는 않음
- 연산 결과가 꼭 두 피연산자 중 하나와 일치할 필요도 없음
- operator함수도 오버로딩 가능
    - 이름은 같지만 파라미터 타입이 서로 다른 함수 생성 가능

코틀린의 비트 연산자

- 비트 연산자가 따로 없음 - 함수로 수행
- 비트 연산 함수 목록
    - shl: 왼쪽 시프트
    - shr: 오른쪽 시프트
    - ushr: 오른쪽 시프트(0으로 부호 비트 설정)
    - and: 비트 곱
    - or: 비트 합
    - xor: 비트 배타 합
    - inv: 비트 반전

## 9.1.2 연산을 적용한 다음에 그 결과를 바로 대입: 복합 대입 연산자 오버로딩

복합 대입 연산자 (+=, -= 등)

- plusAssign, minusAssign, timesAssign 등
- 이론적으로는 +=를 plus와 plusAssign 모두 컴파일 가능
    - 그래서 plus와 plusAssign 모두 정의하면 컴파일 오류 발생
    - plus: 변경 불가능한 경우 정의. 새로운 값을 반환하는 연산만 추가
    - plusAssign: 빌더와 같이 변경 가능한 클래스 설계 시 사용
- +, - : 항상 새로운 컬렉션을 반환
- +=, -=
    - 항상 변경 가능한 컬렉션에 적용하여 메모리에 있는 객체 상태를 변환
    - 읽기 전용 컬렉션에서는 복사본을 반환

## 9.1.3 피연산자가 1개뿐인 연션자: 단항 연산자 오버로딩

단한 연산자 오버로딩 함수는 인자를 취하지 않음

| 식 | 함수 이름 |
| --- | --- |
| +a | unaryPlus |
| -a | unaryMinus |
| !a | not |
| ++a, a++ | inc |
| —a, a— | dec |

# 9.2 비교 연산자를 오버로딩해서 객체들 사이의 관계를 쉽게 검사

## 9.2.1 동등성 연산자: equals

==, ≠ 연산자 호출 → equals 메소드 호출로 컴파일

- null인지 아닌지 판단 & 동등성 검사 수행

===

- 서로 같은 객체를 가리키는지 검사
- ===를 오버로딩 할 수는 없음

equals

- Any에 정의된 메서드 → override가 필요
- Any에서 상속받은 equals가 확장 함수보다 우선순위가 높음 → equals를 확장함수로 정의할 수 없음
- ≠는 equals의 값을 반전시켜 돌려줌

## 9.2.2 순서 연산자: compareTo (<, >, ≤, ≥)

비교 연산자를 사용하는 코드 → compareTo로 컴파일 (Int 반환)

```kotlin
class Person (
    val firstName: String, val lastName: String
): Comparable<Person> {
    override fun compareTo(other: Person): Int {
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}
```

- Comparable 인터페이스 구현  → Java 컬렉션 정렬 메서드에서도 사용 가능
- Comparable의 compareTo에도 operator가 있기에 오버라이드 시 operator를 붙일 필요가 없음

# 9.3 컬렉션과 범위에 대해 쓸 수 있는 관례

## 9.3.1 인덱스로 원소 접근: get과 set

- get
    - 인덱스 접근 연산자를 사용해 원소를 읽는 연산
    
    ```kotlin
    operator fun Point.get(index: Int): Int {
        return when(index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException()
        }
    }
    ```
    
    - 파라미터로 Int가 아닌 타입도 사용할 수 있음
    - 여러 파라미터를 사용하는 get 정의 가능
    - 여러 타입을 오버로딩한 get 메서드를 여럿 정의 가능
- set
    - 원소를 쓰는 연산
    
    ```kotlin
    operator fun MutablePoint.set(index: Int, value: Int) {
        when(index) {
            0 -> x = value
            1 -> y = value
            else -> IndexOutOfBoundsException();
        }
    }
    ```
    

## 9.3.2 어떤 객체가 컬렉션에 들어있는지 검사: in 관례

in

- 객체가 컬렉션에 들어있는지 검사
- contains 함수가 대응

```kotlin
operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x ..< lowerRight.x &&
            p.y in upperLeft.y ..< lowerRight.y
}
```

- ..< : 열린 범위를 만듦 (코틀린 버전을 탄다)

코틀린의 범위

- 10..20
    - 닫힌 범위
    - 10 이상 20 이하
- 10 until 20
    - 10 이상 19 이하
- 10 ..< 20
    - 10 초과 20 미만

## 9.3.3 객체로부터 범위 만들기: rangeTo와 rangeUntil 관례
..

- 범위를 반들기 위해 사용하는 구문
- rangeTo 함수 호출을 간략하게 표현하는 방법
    - rangeTo는 범위를 반환
    - Comparable 인터페이스 구현 시, 정의할 필요 없음

범위 연산자 → 우선순위가 낮기에 범위의 메서드를 호출하려면 범위를 괄호로 둘러싸야 함

rangeUntil

- rangeTo는 닫힌 범위 생성
- rangeTo는 열린 범위 생성

## 9.3.4 자신의 타입에 대해 루프 수행: iterator 관례

for (x in list) { … }

- 이터레이터를 얻은 다음 이터레이터에 대해 hasNext와 next 호출을 반복
- 이터레이터 메소드드를 확장 함수로 정의 가능

```kotlin
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> = 
    object : Iterator<LocalDate> {
        var current = start
        override fun hasNext() = current <= endInclusive
        override fun next(): LocalDate {
            val thisDate = current
            current = current.plusDays(1)
            return thisDate
        }
    }
```

# 9.4 component 함수를 사용해 구조 분해 선언 제공

구조 분해

- 복합적인 값을 분해해서 별도의 여러 지역 변수를 한꺼번에 초기화 가능
- 각 변수 초기화 시 componentN 호출
- data class는 componentN을 자동으로 생성해 줌
    - 코틀린 표준 라이브러리에서는 맨 앞의 다섯 원소에 대해 componentN 제공

## 9.4.1 구조 분해 선언과 루프

변수 선언이 들어갈 수 있는 장소라면 어디든 구조 분해 선언 사용 가능

```kotlin
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}
```

- 코틀린에서는 맵을 직접 이터레이션 가능

## 9.4.2 _문자를 사용해 구조 분해 값 무시

변수 중 일부가 필요 없을 때 유용

```kotlin
fun introducePerson(p: Person) {
    val (firstName, _, age) = p
    println("This is $firstName, aged $age")
}
```

- 구조 분해 선언은 순서 기반임
- 컴포넌트를 무시하기 위해 _를 사용할 수 있음
- 아직은 이름 기반이 아니라 순서 기반으로 동작하기 때문에 구조분해 할당을 사용할 때는 조심해야 한다.

# 9.5 프로퍼티 접근자 로직 재활용: 위임 프로퍼티

프로퍼티는 위임을 활용해 자신의 값을 필드가 아니라 데이터베이스 테이블, 브라우저 세션, 맵 등에 저장 가능

위임

- 객체가 직접 작업을 수행하지 않고 다른 도우미 객체가 그 작업을 처리하도록 맡기는 디자인 패턴

위임 객체

- 위임 작업을 처리하는 도우미 객체

## 9.5.1 위임 프로퍼티의 기본 문법과 내부 동작

기본 문법

```kotlin
val p: Type by Delegate()
```

- p 프로퍼티는 접근자 로직을 다른 객체에 위임
    - Delegate 클래스의 인스턴스를 위임 객체로 사용
- by 뒤에 있는 식을 계산해 위임에 쓰일 객체를 얻음
- 컴파일러는 숨겨진 도우미 프로퍼티를 만들고 → 프로퍼티를 위임 객체의 인스턴스로 초기화
- p 프로퍼티는 위임 객체에게 자신의 작업을 위임

위임 관례

- Delegate 클래스는 getValue, setValue 메소드를 제공해야 함
    - getValue : 게터를 구현하는 로직
    - setValue : 세터를 구현하는 로직. 변경 가능한 프로퍼티에만 가능
- 선택적으로 provideDelegate 함수 구현을 제공할 수도 있음
    - 최초 생성 시 검증 로직 수행 / 위임이 인스턴스화 되는 방식을 변경
        - 위임 객체에 provideDelegate가 있음면 그 함수를 호출해 위임 객체를 생성
    - 위임 객체를 생성하거나 제공하는 로직
- by
    - 위임 객체와 프로퍼티를 연결

## 9.5.2 위임 프로퍼티 사용: by lazy()를 사용한 지연 초기화

지연 초기화

- 객체의 일부분을 초기화하지 않고 남겨뒀다가 실제로 그 부분의 값이 필요한 경우 초기화 할 때 쓰이는 패턴

backing property 기법

```kotlin
class Person(val name: String) {
    
    // 데이터를 저장하고 emails의 위임 객체 역할을 하는 _emails 프로퍼티
    private var _emails: List<Email>? null
    
    val emails: List<Email>
        get() {
            if(_emails == null) {
                _emails = loadEmails(this) // 최초 접근 시 이메일 가져오기
            }
        }
    return _emails!! // 저장해 둔 데이터가 있으면 그 데이터를 반환
}
```

- 클래스에 같은 개념을 포함하는 프로퍼티가 2개 있을 때 → 비공개 프로퍼티 앞에 밑줄을 붙이며 공개 프로퍼티에는 아무것도 붙이지 않음
- 지연 초기화해야 하는 프로퍼티가 많아지면 → thred safe하지 않기 때문 / 구현해야 할 것이 많아서 복잡해짐

```kotlin
class Person(val name: String) {
    val emails by lazy { loadEmails(this) }
}
```

- lazy
    - 코틀린 관례에 맞는 시크니처의 getValue 메서드가 들어있는 객체를 반환
    - 인자: 값을 초기화할 때 호출할 람다
    - thrade-safe함
    - 동기화에 사용할 락을 lazy 함수에 전달할 수 있음
    - 다중 스레드 환경에서 사용하지 않을 프로퍼티를 위해 lazy 함수가 동기화를 생략하게 할 수 도 있음

## 9.5.3 위임 프로퍼티 구현

옵저버블

- 객체의 프로퍼티가 변경되었을 때 통지하는 경우
- Delegates.observable을 사용하면 간편하게 프로퍼티 변경 통지를 구현할 수 있음

## 9.5.4 위임 프로퍼티는 커스텀 접근자가 있는 감춰진 프로퍼티로 변환

```kotlin
class C {
	var prop: Type by MyDelegate()
}
```

- MyDelegate 클래스의 인스턴스는 감춰진 프로퍼티에 저장
- 컴파일러는 프로퍼티를 표현하기 위해 KProperty 타입의 객체를 사용

컴파일러는 위 코드를 아래와 같이 생성

```kotlin
class C {
	private val <delegate> = MyDelegate()
	
	var prop: Type
		get() = <delegate>.getValue(this, <property>)
		set(value: Type) = <delegate>.setValue(this, <property>, value)
}
```

→ 컴파일러는 모든 프로퍼티 접근자 안에 getValue와 setValue 호출 코드를 생성함