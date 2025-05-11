# 10장. 고차 함수: 람다를 파라미터와 반환값으로 사용

람다 → 추상화를 하기 좋은 도구

고차 함수

- 람다를 인자로 받거나 반환하는 함수

인라인 함수

- 성능상 부가 비용을 없애고 람다 안에서 더 유연하게 흐름을 제어할 수 있음

# 10.1 고차 함수

고차 함수?

- 다른 함수를 인자로 받거나 반환하는 함수
- 람다나 함수 참조를 인자로 넘길 수 있거나, 람다나 함수 참조를 반환하는 함수
- 함수를 인자로 받는 동시에 함수를 반환하는 함수

## 10.1.1 함수 타입 - 람다의 파라미터 타입과 반환 타입을 지정

코틀린 함수 타입 문법

```kotlin
(Int, String) -> Unit
```

- Int, String은 파라미터 타입
- Unit은 반환 타입
    - 일반적인 메서드는 Unit을 생략해도 됨
    - 하지만 함수 타입 선언에서의 반환 타입에서는 Unit을 생략하면 안 된다.

```kotlin
val sum: (Int, Int) -> Int = { x, y -> x + y }
```

- 변수 선언 시 타입을 선언했기에, `{ x, y → x + y }` 의 타입은 생략이 가능하다.

```kotlin
// 반환 타입이 널이 될 수 있음
(Int, Int) -> Int?

// 함수 타입 전체가 널이 될 수 있음
((Int, Int) -> Int)?
```

→ 두 경우는 다르므로 혼동하지 않도록 주의할 것!

## 10.1.2 인자로 전달 받은 함수 호출

고차 함수 정의 및 호출

```kotlin
fun twoAndThree(operation: (Int, Int) -> Int {
		val result = operation(2, 3)
		println("The result if $result")
}

fun main() {
		twoAndThree { a, b -> a + b}
		twoAndThree { a, b -> a * b}
}
```

- 인자로 받은 함수를 호출하는 구문 → 일반 함수를 호출하는 구문과 같음

파라미터 이름과 함수 타입

```kotlin
fun twoAndThree(
    // 함수 타입의 각 파라미터에 이름 붙이기
		operation: (operandA: Int, operandB: Int) -> Int
) {
		val result = operation(2, 3)
		println("the result is $result")
}

fun main() {
		// API에서 지정한 이름을 람다에 사용 가능
		twoAndThree { operandA, operandB -> operandA + operandB }
		
		// 원하는 다른 이름 사용 가능
		twoAndThree { alpha, beta -> alpha + beta }
}
```

- 파라미터 이름은 타입 검사 시 무시 →  파라미터 이름이 함수 타입 선언의 파라미터 이름과 일치하지 않아도 됨
- 대신 코드 가독성이 좋아지고 IDE의 지원을 받을 수 있음

## 10.1.3 자바에서 코틀린 함수 타입 사용

자바 고차함수 → 코틀린에서 사용 가능

- 자바 람다는 코틀린 함수 타입으로 변함

코틀린 코드 → 자바에서 호출 가능

람다를 인자로 받는 코틀린의 확장 함수 사용 시

```java
List<String> strings = new ArrayList();
strings.add("42");
CollectionsKt.forEach(strings, s -> {
		System.out.println(s);
		return Unit.INSTANCE;
});
```

- string(수신객체)를 명시적으로 전달함
- Unit은 java의 void와는 다르게 Unit이라는 명시적인 값이 있으므로, Unit을 명시적으로 반환해줘야 함

함수 타입의 자세한 구현

- 코틀린 함수 타입은 일반 인터페이스
    - 함수 타입의 변수 → FunctionN 인터페이스 구현
    - FunctionN 인터페이스는 컴파일러가 생성한 합성 타입 (표준 라이브러리 정의는 아님)
- 각 인터페이스에는 invoke라는 유일한 메서드가 정의되어 있음
    - invoke 메서드에는 람다 본문이 들어감

```kotlin
// n = 1인 경우 FunctionN 인터페이스
interface Function1<P1, out R> {
		operator fun invoke(p1: P1): R
}
```

## 10.1.4 함수 타입의 파라미터 - 기본값 지정, 널 가능성

매번 파라미터로 람다를 넘기게 되면 → 함수 호출이 더 불편해지는 경우가 있음

```kotlin
// 선언 및 구현
fun<T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: (T) -> String = { it.toString() } // 선언하면서 기본값 지정
): String {
    // joinToString 구현부
}

// 사용
fun main() {
    val letters = listOf("Alpha", "Beta")
    println(letters.joinToString()) // 디폴트 변환값
    println(letters.joinToString{ it.lowercase() } )     // 람다 인자 전달
    println(letters.joinToString(separator = "! ", postfix = "! ",
        transform = { it.uppercase() }) )     // 람다 인자 전달
}
```

널이 될 수 있는 함수 타입 지정 가능

```kotlin
fun foo(callback: (() -> Unit)?) {
		// ...
		if (callback != null) {
				callback()
		}
}
```

- 널이 될 수 있는 함수 타입으로 함수를 받으면 그 함수를 직접 호출할 수 없음
- null 여부를 명시적으로 검사한 다음 사용해야 함
- 함수 타입은 invoke를 구현하는 인터페이스
    - invoke를 통해 안전한 호출 가능
    - `callback?.invoke`

## 10.1.5 함수를 함수에서 반환

프로그램의 상태나 다른 조건에 따라 달라지는 로직 → 이런 경우에 유용하게 사용

```kotlin
fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if(delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    } else {
        return { order -> 1.2 * order.itemCount }
    }
}
```

## 10.1.6 람다를 활용해 중복을 줄여 코드 재사용성 높이기

```kotlin
// 함수 정의
fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) = 
	filter(predicate).map(SiteVisit::duration).average()
	
// 사용
fun main() {
		println(
				log.averageDurationFor { it.os in setOf(OS.ANDROID, OS.IOS) }
		)
		
		println(
				log.averageDurationFor { it.os == OS.IOS && it.path == "signup" }
		)
}
```

- 함수 타입을 사용하면 필요한 조건을 따로 전달하기가 편해짐
- 람다로 만들면 중복 제거  / 데이터의 반복을 추출 / 반복적인 행동도 추출 가능

# 10.2 인라인 함수를 사용해 람다의 부가 비용 없애기

람다식 컴파일 과정

- 코틀린은 일반적으로 익명 클래스로 람다를 컴파일
- 람다식마다 새로운 클래스 생성 → 람다 정의가 포함된 코드를 호출할 때 마다 새로운 객체 생성

→ 람다를 사용하는 코드는 똑같은 코드를 직접 실행하는 함수보다 덜 효율적

→ inline을 사용하면 성능 향상 가능!

## 10.2.1 인라이닝이 작동하는 방식

inline

- 어떤 함수를 인라인으로 선언하면 그 함수의 본문이 인라인됨
- 함수를 호출하는 코드가 아닌 함수 본문을 번역한 바이트코드로 컴파일

```kotlin
// 컴파일 전
fun foo(l:Lock) {
		println("Before sync")
		synchronized(l) {
				println("Action")
		}
		println("After sync")
}

// 컴파일 후
fun __foo__(l: Lock) {
		println("Before sync")
		l.lock()  
		try {
				println("Action")
		} finally {
				l.unlock()
		}
}
```

- 인라인 함수를 호출하면서 람다를 넘기면 본문에 람다가 인라인된다

```kotlin
// 컴파일 전
fun foo(l:Lock) {
		synchronized(lock, body)
}

// 컴파일 후
fun __foo__(l: Lock) {
		println("Before sync")
		l.lock()  
		try {
				body() // 람다 본문이 인라이닝 되지 않음
		} finally {
				l.unlock()
}
```

- 인라인 함수를 호출하면서 람다를 넘기는 대신 함수 타입의 변수(body, () → Unit 타입)를 넘길 수도 있음
- 람다 본문이 인라이닝 되지 않음
    - 인라인 함수를 호출하는 코드 위치에서는 변수에 저장된 람다의 코드를 알 수 없음

- 하나의 인라인 함수를 두 곳에서 각각 다른 람다를 사용해 호출한다면 그 두 호출은 각각 따로 인라이닝됨

## 10.2.2 인라인 함수의 제약

람다를 사용하는 모든 함수를 인라이닝할 수는 없음

- 람다가 본문에 직접 펼쳐짐
- 함수가 파라미터로 전달받은 람다를 본문에 사용하는 방식이 한정됨
- 파라미터로 받은 람다 변수 저장 → 나중에 변수 사용 시 → 람다를 표현하는 객체가 어딘가는 존재해야 함

```kotlin
class FunctionStorage {
		var myStoredFunction: ((Int) -> Unit)? = null
		inline fun storeFunction(f: (Int) -> Unit) {
				myStoredFunction = f // 전달된 파라미터를 저장하나 모든 호출 지점에서 이 코드를 대치할 수 없음	
		}
}
```

- 전달 받은 인자를 프로퍼티에 저장하려 하면 안 된다.

noinline

- 둘 이상의 람다를 인자로 받는 함수에서 일부 람다만 인라이닝 하고자 할 때
- 인라이닝 하면 안 되는 람다 앞에 변경자
- 람다를 인라인하지 말고 일반 함수처럼 호출하도록 컴파일

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
		// ...
}
```

## 10.2.3 컬렉션 연산 인라이닝

컬렉션에서 자주 사용하는 filter, map은 인라인 함수.

시퀀스에 사용된 람다는 인라이닝 되지 않음

- 중간 시퀀스는 람다를 필드에 저장하는 객체로 표현
- 최종 연산은 중간 시퀀스에 있는 여러 람다를 연쇄 호출

→ 크기가 작은 컬렉션은 인라이닝되는 일반 컬렉션 연산이 더 성능이 나을 수 있다.

시퀀스가 무조건 성능이 좋지는 않음! 컬렉션 크기가 큰 경우만 좋다.

## 10.2.4 언제 함수를 인라인으로 선언할지 결정

람다를 인자로 받는 함수만 성능이 좋아질 가능성이 높다.

일반 함수

- JVM 인라이닝 지원
- 바이트코드 중복 X

코틀린 인라인 함수

- 코드 중복 발생

람다를 인자로 받는 함수를 인라이닝하면 얻는 이익

- 인라이닝을 통해 없앨 수 있는 부가 비용이 상당함
    - 함수 호출 비용 줄어들음
    - 람다 인스턴스에 해당하는 객체를 만들 필요도 없어짐
- 현재의 JVM은 함수 호출과 람다를 인라이닝해줄 정도로 똑똑하지 못함
- 인라이닝을 사용하면 일반 람다에서는 사용할 수 없는 몇 가지 기능 사용 가능
    - ex) 비로컬 리턴

이익도 있지만..

- 코드 크기가 커질 수 있음

→ 코드 크키가 작은 경우 inline을 사용하자

## 10.2.5 withLock, use, useLines로 자원 관리를 위해 인라인된 람다 사용

람다로 중복을 없앨 수 있는 일반적인 패턴

- 자원 관리
    - 어떤 작업을 하기 전에 자원을 획득하고 작업을 마친 후 자원을 해제

withLock

- Lock 인터페이스의 확장 함수

use

- Closable 인터페이스를 구현한 객체에 대해 호출하는 확장 함수
- 람다를 호출하고 사용 후 자원이 확실히 닫히게 함

useLines

- File과 Path 객체에 대해 정의
- 람다가 문자열 시퀀스에 접근하게 해줌

→ 이러한 함수들이 있기에 코틀린에서는 try-with-resource 구문을 지원하지 않음

# 10.3 람다에서 반환: 고차 함수에서 흐름 제어

## 10.3.1 람다 안의 return문: 람다를 둘러싼 함수에서 반환

```kotlin
for lookForAlice(people: List<Person>) {
		people.forEach {
				if (it.name == "Alice") {
						println("Found!")
						return
				}
		}
}
```

- 람다 안에서만 반환되는 것이 아니라 람다를 호출하는 함수가 실행을 끝나고 반환됨

→ 비로컬 return!

- 자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 return

인라이닝되지 않는 함수에 전달되는 람다 안에서 return을 사용할 수는 없음

- 인라이닝되지 않는 함수는 람다를 변수에 저장할 수 있음
- 변수를 저장하는 경우 함수가 반환된 다음에 나중에 람다 실행 가능하기 때문

## 10.3.2 람다로부터 반환: 레이블을 사용한 return

람다에서도 로컬 Return 사용 가능

- for의 break와 비슷한 역할
- 람다의 실행을 끝내고 람다를 호출했던 코드의 실행을 계속 이어나감
- 레이블을 사용해 구분 가능

람다 레이블

```kotlin
// 람다 레이블
people.forEach label@{
		if (it.name != "Alice") return@label
		print("Found Alice!")
}

// 인라인 함수의 이름을 레이블로 사용
fun lookForAlice(people: List<Person>) {
		people.forEach {
				if (it.name != "Alice") return@forEach
				print("Found Alice!:
		}
}
```

- 위 둘 중 하나의 방법만 사용해야 함
- 람다식의 레이블을 명시하면 함수 이름을 레이블로 사용할 수 없음

## 10.3.3 익명 함수: 기본적으로 로컬 return

익명 함수

- 람다식을 작성하는 다른 문법적 형태
- 다른 함수에 전달할 수 있는 코드 블록을 작성하는 다른 방법

```kotlin
fun lookForAlice(people: List<Person>) {
		people.forEach(fun (person) {
				if(person.name == "Alice") return
				println("${person.name} is not Alice")
		})
}
```

- 함수 이름을 생략하고 파라미터 타입을 컴파일러가 추론할 수 있게 함
- 블록이 본문인 익명 함수 → 타입 지정 필요
- 식을 본문으로 하는 익명 함수 → 반환 타입 생략 가능