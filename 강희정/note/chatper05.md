# 5장. 람다를 사용한 프로그래밍

<aside>
💡

람다식/람다

- 다른 함수에 넘길 수 있는 작은 코드 조각
- 공통 코드 구조를 라이브러리 함수로 쉽게 뽑아 낼 수 있음
</aside>

# 5.1 람다식과 멤버 참조

## 5.1.1 람다 소개: 코드 블록을 값으로 다루기

익명 내부 클래스 사용 → 코드를 함수에 넘기거나 변수에 저장

함수를 값처럼 다루기 → 함수를 직접 다른 함수에 전달할 수 있음

람다식 → 간편 전달. 실질적으로 코드 블록을 직접 함수의 인자로 전달할 수 있음

람다는 메서드가 하나뿐인 익명 객체 대신 사용할 수 있다!

**함수형 프로그래밍의 특성**

- first-class citizen
    - 함수는 값으로써 저장되고/반환되며/파라미터로 전달될 수 있다.
- 불변성
    - 객체를 만들 때 일단 만들어진 다음에는 내부 상태가 변하지 않음을 보장한다.
- 부수 효과 없음
    - 순수 함수: 함수의 입력만이 함수의 결과에 영향을 줌. 함수는 다른 객체나 외부 세계의 상태를 변하지 않게 구성함

## 5.1.2 람다와 컬렉션

람다를 사용하지 않고 for문으로 구현한 기능

Before

```kotlin
fun findTheOldest(people: List<Person>) {
    var maxAge = 0
    var theOldest: Person? = null
    for (person in people) {
        if (person.age > maxAge) {
            maxAge = person.age
            theOldest = person
        }
    }
    println(theOldest)
}

fun main() {
    val people = listOf(Person("Alice", 29), Person("Bob", 31))
    findTheOldest(people)
}
```

After

```kotlin
fun main() {
    val people = listOf(Person("Alice", 29), Person("Bob", 31))
    println(people.maxByOrNull { it.age })   // 람다식
    println(people.maxByOrNull(Person::age)) // 멤버 참조
}
```

## 5.1.3 람다식의 문법

```kotlin
{ x: Int, y: Int -> x + y}
```

- 람다는 중괄호로 둘러싸여 있음
- `x: Int, y: Int` : 파라미터
- `x + y` : 람다 본문
- `→` : 인자 목록과 람다 본문을 구분해줌

run

```kotlin
run { println(42) }
```

- 인자로 받은 람다를 실행해주는 라이브러리 함수
- 식이 필요한 부분에서 코드 블록을 실행하고 싶을 때 사용

람다의 추가 문법

- 람다가 어떤 함수의 유일한 인자이고 괄호 뒤에 람다를 썼다면 호출 시 빈 괄호를 없애도 됨
    - 둘 이상의 람다를 인자로 받는 함수의 경우, 둘 이상의 람다를 괄호 밖으로 빼낼 수는 없음
    
    ```kotlin
    // 괄호 있음
    people.maxByOrNull() {p: Person -> p.age}
    
    // 괄호 제거
    people.maxByOrNull {p: Person -> p.age}
    ```
    
- 컴파일러가 문맥으로부터 유추할 수 있는 타입은 굳이 적을 필요 없음
    
    ```kotlin
    // 파라미터 타입 명시
    people.maxByOrNull {p: Person -> p.age}
    
    // 파라미터 타입 추론
    people.maxByOrNull {p -> p.age}
    ```
    
    - mayByOrNull의 파라미터 타입은 항상 컬렉션 원소 타입과 같음
- 인자가 단 하나뿐인 경우 굳이 인자에 이름을 붙이지 않아도 됨
    - 람다의 파라미터가 하나뿐이고 타입을 컴파일러가 추론할 수 있는 경우 it 사용 가능
    
    ```kotlin
    // 파라미터 타입 명시
    people.maxByOrNull { it.age }
    ```
    
- 람다를 변수에 저장할 때 → 파라미터의 타입을 추론할 문맥이 존재하지 않음 → 파라미터 타입 명시 필요
    
    ```kotlin
    val getAge = { p: Person -> p.age }
    people.maxByOrNull(getAge)
    ```
    
- 본문이 여러줄로 이루어진 경우 본문의 맨 마지막에 있는 식이 람다의 결과값 (명시적 return 필요하지 않음)

## 5.1.4 현재 영역에 있는 변수 접근

함수 안에 익명 내부 클래스 선언 → 클래스 안에서 / 함수의 파라미터와 로컬 변수 참조 가능

람다 정의 → 클래스 안에서 / 함수의 파라미터, 로컬 변수, **람다 정의 앞에 선언된 로컬 변수** 참조 가능

람다 안에서 람다 정의 밖에 선언된 로컬 변수에 참조

```kotlin
fun printCounts(responses: Collection<String>) {
    var errors = 0  // 람다 밖에 정의
    responses.forEach {
        errors++   // 참조 가능
    }
}
```

- 람다가 캡쳐한 변수: 람다 안에서 접근할 수 있는 외부 변수
- 로컬 변수 → 함수가 반환되면 → 생명주기 종료
- 함수가 로컬 변수를 캡쳐한 람다를 반환 or 다른 변수에 저장 → 로컬 변수의 생명주기와 함수의 생명주기 달라질 수 있음
    - 캡쳐한 변수가 있는 람다를 저장 → 함수가 끝난 뒤에 실행 → 캡쳐한 변수 접근 가능

### 변수 캡쳐 원리

val를 캡쳐한 경우

- 람다 코드를 변수 값과 함께 저장
- 변수의 값이 복사

var를 캡쳐한 경우

- 변수를 래퍼로 감싸서 - 래퍼에 대한 참조를 람다 코드와 함께 저장
- 변수를 Ref 클래스 인스턴스에 넣고 - Ref 참조를 파이널로

주의할 점!

- 람다를 이벤트 핸들러나 비동기적으로 실행되는 코드로 활용하는 경우 → 로컬 변수 변경은 람다가 실행될 때에만 일어남
- 람다는 지금 실행되는 코드가 아니라 **이벤트 발생 시 실행될 코드**이다.

## 5.1.5 멤버 참조

- 함수나 프로퍼티를 이름으로 참조해 함수처럼 전달하거나 호출할 수 있도록 하는 기능
- 함수를 값으로 바꿈
- :: 사용 - 클래스 이름과 참조하려는 멤버 이름 사이에 위치
    
    ```kotlin
    val getAge = Person::age
    val getAge = { person: Person -> person.age }
    ```
    
    - 둘은 같은 코드!
- 멤버 참조 뒤에는 괄호를 넣지 않음
- 멤버 참조는 그 멤버를 호출하는 람다와 같은 타입
- 최상위 함수나 프로퍼티 참조 → 클래스 이름을 생략하고 참조
    
    ```kotlin
    fun main {
    		run (::salute)
    }
    ```
    

생성자 참조

- 클래스 생성 작업을 연기하거나 저장
- :: 뒤에 클래스 이름을 넣으면 생성자 참조 만들 수 있음

```kotlin
fun main() {
		val createPerson = ::Person
}
```

확장 함수 → 멤버 함수와 똑같은 방식으로 참조 가능

## 5.1.6 값과 엮인 호출 가능 참조

같은 멤버 참조 구문을 사용해 특정 객체 인스턴스에 대한 메서드 호출에 대한 참조를 만들 수 있음

- 인자를 받지 않고 자신과 엮인 객체의 멤버의 값을 반환

```kotlin
fun main() {
    val seb = Person("Sebastian", 26)
    val personsAgeFunction = Person::age
    println(personsAgeFunction(seb))       // 인자 전달
    
    val sebsAgeFunction = seb::age
    println(sebsAgeFunction())              // 인자 전달 X
}
```

# 5.2 자바의 함수형 인터페이스 사용: 단일 추상 메서드

코틀린 람다 → 자바 API와 완전히 호환됨

단일 추상 메서드(SAM, Single Abstract Method) 인터페이스

- 인터페이스 안에 추상 메서드가 단 하나뿐
- 함수형 인터페이스라고도 함

## 5.2.1 람다를 자바 메서드의 파라미터로 전달

함수형 인터페이스를 파라미터로 받는 모든 자바 메서드에 람다 전달

```java
void computation(int delay, Runnable computation);
```

코틀린에서 인자 전달

```kotlin
computation(1000) { print(42)}
```

- 컴파일러는 자동으로 람다를 Runnable 인터페이스로 변환
    - 익명 클래스 인스턴스를 만들고
    - 람다를 그 인스턴스의 유일한 추상 메서드의 본문으로 만들어줌

명시적 객체 선언 → 호출할 때 마다 새로운 인스턴스 생성

람다 → 자신이 정의된 함수의 변수에 접근하지 않는다면 → 함수가 호출될 때마다 람다에 해당하는 익명 객체가 재사용

람다가 자신을 둘러싼 환경의 변수를 캡쳐하면 → 인스턴스 재사용 불가

람다를 inline 코틀린 함수에 전달하면 → 익명 클래스가 생성되지 않음

## 5.2.2 SAM 변환: 람다를 함수형 인터페이스로 명시적 변환

SAM 생성자

- 컴파일러가 생성한 함수
- 람다를 단일 추상 메서드 인터페이스와 인스턴스로 명시적 변환
- 컴파일러가 변환을 자동으로 수행하지 못하는 맥락에서 사용
- 함수형 이넡페이스의 인스턴스를 반환해야 하는 경우 → 람다를 직접 반환할 수 없으니 → 람다를 SAM 생성자로 감싼다
- 생성자의 이름은 사용하려는 함수형 인터페이스의 이름과 같다
- 하나의 인자만을 받아 함수형 인터페이스를 구현하는 클래스의 인스턴스를 반환
- 람다로 생성한 함수형 인터페이스 인스턴스를 변수에 저장해야 하는 경우에도 사용 가능

```kotlin
fun createAllDoneRunnable(): Runnable {
		return Runnable { println("All done!") }
}

fun main() {
		createAllDoneRunnable().run()
}
```

### 람다와 리스너 등록/해제하기

- 람다에는 인스턴스 자신을 가리키는 this가 없다.
- 람다를 변환한 익명 클래스의 인스턴스를 참조할 방법이 없음
- 람다 안애서의 this → 람다를 둘러싼 클래스의 인스턴스

# 5.3 코틀린에서 SAM 인터페이스 정의: fun interface

fun interface

- 하나의 추상 메서드만 포함
- 다른 비추상 메서드를 여럿 가질 수 있음
- Java의 @FunctionalInterface와 동일함
- fun interface라고 정의된 타입의 파라미터를 받는 함수가 있을 때 → 람다 구현이나 람다에 대한 참조를 직접 넘길 수 있음 → 동적으로 인터페이스 구현을 인스턴스화

# 5.4 수신 객체 지정 람다: with, apply, also

수신 객체 지정 람다

수신 객체 - 함수 안에서 내가 어떤 객체에 대해 동작하고 있는지를 나타내는 객체

- 수신 객체를 명시하지 않고 람다의 본문 안에서 다른 객체의 메서드를 호출

## 5.4.1 with

```kotlin
fun alphabet(): String {
    val stringBuilder = StringBuilder()
    return with(stringBuilder) {
        for(letter in 'A' .. 'Z') {
            this.append(letter)
        }
        this.toString()
    }
}
```

- 첫 번째 인자로 받은 객체를 → 두 번째 인자로 받은 람다의 수신 객체로 만듦
- 람다 안에서는 명시적인 this 참조를 사용해 수신 객체에 접근
    - this 제거 가능
- 반환하는 값 - 람다 코드를 실행한 결과 → 식 본문의 마지막 값

### 메서드 이름 충돌

with에 인자로 넘긴 객체의 클래스와 / with를 사용하는 코드가 들어있는 클래스 안에 이름이 같은 메서드가 있다면? → 레이블 필요

```kotlin
this@OuterClass.toString()
```

→ 바깥쪽에 정의한 toString을 호출하고 싶을 때

## 5.4.2 apply

```kotlin
fun alphabet(): String {
    val stringBuilder = StringBuilder().apply{
        for(letter in 'A' .. 'Z') {
            append(letter)
        }
		}.toString()
```

- 람다의 결과 대신 수신 객체가 필요한 경우
- 항상 자신에게 전달된 객체를 반환한다
- 임의의 타입의 확장 함수로 호출 가능
- apply를 호출한 객체 → apply에 전달된 람다의 수신 객체가 됨
- 인스턴스를 만들면서 프로퍼티를 초기화 할때 유용하게 사용(Builder 역할)

buildXXX

- List, Set, Map
- 읽기 전용 컬렉션을 생성하지만 생성 과정에서는 가변 컬렉션인 것처럼 다루고 싶을 때 사용

```kotlin
val fibonacci = buildList {
		addAll(listOf(1, 1, 2))
		add(3)
		add(index=0, element=3)
}
```

## 5.4.3 also

```kotlin
val x = listof(1, 2, 3).also {
		println(it)
}
```

- 수신 객체를 받으며 - 수신 객체에 동작을 수행한 후 수신 객체를 돌려줌
- 람다 안에서는 수신 객체를 인자로 함조함 → 파라미터 이름을 부여하거나 디폴트 이름 사용해야 함
- 원래의 수신 객체를 인자로 받는 동작을 수행할 때 사용
- 어떤 효과를 추가로 수행함