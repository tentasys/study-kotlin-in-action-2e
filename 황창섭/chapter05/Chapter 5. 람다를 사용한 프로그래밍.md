# Chapter 05. 람다를 사용한 프로그래밍

- 람다식과 멤버 참조를 사용해 코드 조각과 행동 방식을 함수에게 전달
- 코틀린에서 함수형 인터페이스를 정의하고 자바의 함수형 인터페이스 사용
- 수신 객체 지정 람다 사용

## 5.1 람다식과 멤버 참조

### 5.1.1 람다 소개: 코드 블록을 값으로 다루기

- 함수형 프로그래밍의 특성
  - 일급 시민인 함수: 함수를 값으로 다룰 수 있다. 함수를 변수에 저장하고 파라미터로 전달하며 함수에서 다른 함수를 반환할 수 있다.
  - 불변성: 객체가 만들어진 다음에는 내부 상태가 변하지 않음을 보장
  - 부수 효과 없음: 똑같은 입력에 대해 항상 같은 출력을 내놓고 다른 객체나 외부 세계의 상태를 변경하지 않게 구성

```kotlin
/**
 * object 선언으로 리스너 구현하기
 */
button.setOnClickListener(object: OnClickListener {
  override fun onClick(v: View) {
      println("I was clicked!")
  }
})
```

```kotlin
/**
 * 람다로 리스너 구현하기
 */
button.setOnClickListener { 
    println("I was clicked!")
}
```

### 5.1.2 람다와 컬렉션

```kotlin
/**
 * 컬렉션을 for 루프로 직접 검색하기
 */
fun findTheOldest(people: List<Person>) {
    var maxAge = 0
    var theOldest: Person? = null
    for (person in people) {
        if(person.age > maxAge) {
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

```kotlin
/**
 * maxByOrNull 함수를 사용해 컬렉션 검색하기
 */
fun main() {
  val people = listOf(Person("Alice", 29), Person("Bob", 31))
  findTheOldest(people.maxByOrNull { it.age })
}
```

### 5.1.3 람다식의 문법

- 람다식 문법: 람다는 항상 중괄호로 싸여 있으며, 파라미터를 지정하고 실제 로직이 담긴 본문을 제공한다.
  - 화살표가 인자 목록과 람다 본문을 구분

- 람다식을 변수에 저장할 수 있음
  - `val sum = { x: Int, y: Int -> x + y }`

- 람다의 다양한 표현
  - people.maxByOrNull({ p: Person -> p.age })
  - people.maxByOrNull() { p: Person -> p.age }
  - people.maxByOrNull { p: Person -> p.age }
  - people.maxByOrNull { p -> p.age }
  - people.maxByOrNull { it.age }
  - people.maxByOrNull(Person::age)

- 람다를 변수에 저장할 때는 파라미터의 타입을 추론할 문맥이 존재하지 않기에 명시가 필수적
  - `val getAge = { p: Person -> p.age }`

- 본문이 여러 줄로 이뤄진 경우 마지막 라인이 결과값이 됨(return 이 필요하지 않음)
```kotlin
fun mina () {
    val sum = { x:Int, y: Int ->
        println("Computing the sum of $x and $y...")
        x + y
    }
}
```

### 5.1.4 현재 영역에 있는 변수 접근

- 자바와 코틀린 람다의 다른 점은 람다 안에서 변수에 접근하고 변경할 수 있다는 점
- 가능한 이유
  - Closure 를 사용하여 변경 가능한 변수도 캡처 가능
  - 변수는 Heap 에 저장되고, 람다가 이를 참조하여 변경 가능하도록 함
```kotlin
var count = 0  // 변경 가능한 변수
val lambda = { count++ }  // 람다가 count를 캡처
lambda()
println(count)  // 1 (값이 변경됨)
```

### 5.1.5 멤버 참조

- `val getAge = Person::age`
- ::을 사용하는 식을 멤버 참조(member reference) 라고 부름

- 최상위에 선언된 함수나 프로퍼티를 참조하는 방법
```kotlin
fun salute() = println("Salute!")

fun main {
    run(::salute)
}
```

- 확장 함수도 멤버 함수와 똑같은 방식으로 참조 가능하다

### 5.1.6 값과 엮인 호출 가능 참조

```kotlin
fun main() {
    val seb = Person("seb", 26)
    val personsAgeFunction = Person::age // 사람이 주어지면 나이를 돌려주는 멤버 참조
    println(personsAgeFunction(seb)) // 사람을 인자로 받음
    val sebsAgeFunction = seb::age // 특정 사람의 나이를 돌려주는, 값과 엮인 호출 가능 참조
    println(sebsAgeFunction()) // 특정 값과 엮어 있기 때문에 아무 파라미터를 지정하지 않아도 된다
}
```

## 5.2 자바의 함수형 인터페이스 활용: 단일 추상 메서드

- kotlin 람다는 함수형 인터페이스를 파라미터로 받는 자바 메서드를 호출할 때 람다를 사용할 수 있게 해줌.

### 5.2.1 람다를 자바 메서드의 파라미터로 전달

- 함수형 인터터페이스를 파라미터로 받는 모든 자바 메서드에 람다를 전달할 수 있음

```java
void postponeComputation(int delay, Runnable computation);
```

```kotlin
postponeComputation(1000) { println(42) }
```

- 람다가 자신이 정의된 함수의 변수에 접근하지 않는다면 함수가 호출될 때마다 람다에 해당하는 익명 객체가 재사용 됨

```kotlin
fun handleComputation(id: String) {
  postponeComputation(1000) {
      println(id)
  }
}
```

- handleComputation 호출마다 새 Runnable 인스턴스 생성

### 5.2.2 SAM 변환: 람다를 함수형 인터페이스로 명시적 변환

- SAM (Single Abstract Method) 인터페이스 
  - 단 하나의 추상 메서드만 가지는 함수형 인터페이스 
  - 코틀린에서 람다를 함수형 인터페이스로 변환할 때 사용됨

- SAM 변환
  - 람다는 함수형 인터페이스를 직접 반환할 수 없음
  - 대신, 람다를 SAM 생성자로 감싸서 변환해야 함

```kotlin
fun createAllDoneRunnable(): Runnable {
    return Runnable { println("All done!") }
}

fun main() {
  createAllDoneRunnable().run() // All done!
}
```

- 람다에서 this 참조
  - 람다 내부에서 this는 람다가 속한 클래스의 인스턴스를 가리킴
  - 익명 객체와의 차이점: 익명 객체 내부에서는 this가 해당 객체를 가리킴

## 5.4 수신 객체 지정 람다: with, apply, also

### 5.4.1 with 함수

```kotlin
fun alphabet(): String {
  val stringBuilder = StringBuilder()
  return with(stringBuilder) {
    for (letter in 'A'..'Z') {
      append(letter)
    }
    apply("\nNow I know the alphabet!")
    toString()
  }
}
```

- with 함수는 특정 객체를 람다의 수신 객체로 사용하고, 람다의 결과를 반환

### 5.4.2 apply 함수

- apply는 수신 객체를 반환
- apply는 주로 객체 생성과 초기화 과정에서 사용

### 5.4.3 also 함수

- also는 기존 객체를 유지하면서 추가 작업을 수행할 때 사용