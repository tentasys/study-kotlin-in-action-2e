# 📚 5장 람다를 사용한 프로그래밍

- 람다식 , 람다
    - 기본적으로 다른 함수에 넘길 수 있는 작은 코드 조각을 의미한다.

___

## 📖 5.1 람다식과 멤버 참조

### 🔖 5.1.1 람다 소개: 코드 블록을 값으로 다루기

- 함수를 값처럼 다루기
- 클래스를 선언하고 클래스의 인스턴스를 함수에 넘기는 대신, 함수를 직접 다른 함수에 전달할 수 있다.
- 람다식을 사용하면 함수를 선언할 필요가 없고, 대신 실질적으로 코드 블록을 직접 함수의 인자로 전달할 수 있다.

- 함수형 프로그래밍
    - 일급 시민인 함수
        - 함수를 변수에 저장하고 파라미터로 전달하며 함수에서 다른 함수를 반환가능
    - 불변성
        - 객체를 만들 때 일단 만들어진 다음 내부 상태가 변하지 않음
    - 부수 효과 없음
        - 함수가 똑같은 입력에 대해 항상 같은 출력을 내놓고 다른 객체나 외부 로부터 상태를 변경하지 않게 구성된다.

```kotlin
// Object 로 리스너 구현
button.setOnClickListener(object : OnClickListener) {
    override fun onClick(v: View) {
        println("Clicked")
    }
}
// 람다로 구현 
button.setOnClickListener {
    println("Clicked");
}
```

### 🔖 5.1.2 람다와 컬렉션

- 람다로 인해 코틀린은 컬렉션을 다룰 때 강력한 기능을 제공하는 표준 라이브러리를 제공

```kotlin
data class Person(val name: String, val age: Int)

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
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    findTheOldest(people)

    //maxByOrNull 
    println(people.maxByOrNull { it.age })
}
```

- 컬렉션에 대해 수행하는 대부분의 작업은 람다나 멤버 참조를 인자로 취하는 라이브러리 함수를 통해 더 간결하게 표현 가능
- 개선한 코드는 더 짧고 이해하기 쉬우며, 루프 기반의 구현보다 의도를 더 잘드러낸다.

### 🔖 5.1.3 람다식의 문법

- 람다는 값처럼 여기저기 전달할 수 있는 동작의 조각이다.
- 코틀린의 람다식은 중괄호로 둘러싸여 있다.

```kotlin
fun main() {
    val sum = { x: Int, y: Int -> x + y }
    println(sum(1, 2))
    { println(42) }()
    run { { println(42) } }
}
```

- 람다를 만들자마자 바로 사용하는 것보다 본문의 코드를 직접 실행하는 편이 낫다.
- 코드의 일부분을 블록으로 둘러싸 실행할 필요가 있으면 run을 사용하라
    - run은 인자로 받은 람다를 실행해 주는 라이브러리 이다.

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    people.maxByOrNull({ p: Person -> p.age }) // 구분자가 많아서 가독성이 떨어짐 
    people.maxByOrNull() { p: Person -> p.age } // 인자가 단하나 뿐인 경우 굳이 인자에 이름을 붙이지 않아도 된다.
    people.maxByOrNull { p: Person -> p.age } // 람다가 어떤 함수의 유일한 인자이고 괄호 뒤에 람다를 썻다면 호출시 빈괄호는 없어도된다.
    people.maxByOrNull { p -> p.age } // 로컬변수 처럼 컴파일러는 람다의 파라미터 타입도 추론할수 있다.
    people.maxByOrNull { it.age } // 람다의 파라미터가 하나뿐이고 타입을 컴파일러가 추론할수 있는경우 it 을 사용할수 있다.

    val sum = { x: Int, y: Int ->
        x + y // 본문이 여러줄인경우 본문의 가장 마지막 식이 값이된다.
    }
}
```

### 🔖 5.1.4 현재 영역에 있는 변수 접근

- 람라를 함수 안에서 정의한다면 함수의 파라미터뿐 아니라 람다 정의보다 앞에 선언된 로컬 변수까지 람다에서 모두 사용가능 하다.

```kotlin
fun printMessageWithPrefix(messages: Collection<String>, prefix: String) {
    messages.forEach(println("$prefix $it"))
}
```

- 코틀린 람다 안에서는 파이널 변수가 아닌 변수에 접근할 수 있다는 점
- 람다 안에서 바깥의 변수를 변경가능

```kotlin
fun printProblomeCounts(responses: Collection<String>) {
    var clientError = 0
    responses.forEach {
        if (it.startsWith("4")) {
            clientError++
        }
    }
}
```

- 기본적으로 함수 안에 정의된 로컬 변수의 생명주기는 함수가 반환 되면 끝난다. 하지만 어떤 함수가 자신의 로컬 변수를
  캡처한 람다를 반환하거나 다른 변수에 저장한다면 로컬 변수의 생명주기와 함깨 함수의 생명주기가 달라질수있다.
- 람다를 이벤트 핸들러나 다른 비동기적으로 실행되는 코드로 활용하는 경우 로컬 변수의 변경은 람다가 실행될 때만 일어난다.

### 🔖 5.1.5 멤버 참조

- :: 을 사용하는 식을 멤버 참조라고 한다.
- 참조 대상이 프로퍼티 함수인지 프로퍼티 인지와는 관계없이 멤버 참조 뒤에는 괄호를 넣으면 안된다.
- 해당 대상을 참조할 뿐이지 호출하려는 것은 아니기 때문이다.

### 🔖 5.1.6 값과 엮인 호출 가능 참조

- 같은 멤버 참조 구문을 사용해 특정 객체 인스턴스에 대한 메서드 호출에 대한 참조를 만들수 있다.

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val seb = Person("sebastian", 26)
    val personAgeFunction = Person::age
    val sebAgeFunction = seb::age
}
```

___

## 📖 5.2 자바의 함수형 인터페이스 사용: 단일 추상 메서드

- 코틀린 람다가 자바 API 와 완전히 호환

```java
button.setOnClickListener(view->{});
// 단일 추상 메서드 인터페이스 
```

```kotlin
button.setOnClickListener { view ->/**/ }
```

- funtional interface 같다.

### 🔖 5.2.1 람다를 자바 메서드의 파라미터로 전달

- 함수형 인터페이스를 파라미터로 받는 모든 자바 메서드에 람다를 전달할수 있다.

```java
class test {
    void postponeComputation(int delay, Runnable runnable);

    void main() {
        postponeComputation(1000, object:Runnable {
            override fun run() {
                println(42)
            }
        })
    }
}
```

- 명시적으로 객체를 선언하면 매번 호출 할때마다 새 인스턴스가 생성
- 람다를 사용하면 자신이 정의된 함수의 변수에 접근하지 않는다면 함수가 호출 될때마다 익명객체가 재사용된다.
- 람다가 자신을 둘러싼 환경의 변수를 캡처하면 더이상 각각의 함수 호출 같은 인스턴스를 재사용 할수 없다.
    - 이경우 컴파일러도 호출 마다 새로운 인스턴스를 만들고 그 객체안에 캡처한 변수를 저장한다.

```kotlin
fun handleComputation(id: String) {
    postponeComputation(1000) { // handleComputation을 호출 할때 마다 새 인스턴스 생성 
        println(id) // id 를 캡처한다.
    }
}
```

- 람다에 대한 익명 클래스와 그 클래스의 인스턴스를 생성하는 것에 대한 논의는 함수형 인터페이스를 받는 자바 메서드에 대해서는 성립
  하지만 코틀린 확장 함수를 사용하는 컬렉션에 대해서는 성립하지 않는다.

### 🔖 5.2.2 SAM 변환: 람다를 함수형 인터페이스로 명시적 변환

- SAM 생성자는 컴파일러가 생성한 함수로 람다를 단일 추상 메서드 인터페이스의 인스턴스로 명시적으로 변환해준다.
- 이를 컴파일러가 변환을 자동을 수행하지 못하는 맥락에서 사용할 수 있다.

```kotlin
fun createAllDoneRunnable(): Runnable {
    return Runnable { println("a") }
}
fun main() {
    createAllDoneRunnable().run()
}
```

- SAM 생성자의 이름은 사용하려는 함수형 인터페이스의 이름과 같다.
- SAM 생성자는 하나의 인자만을 받아 함수형 인터페이스를 구현하는 클래스의 인스턴스를 반환한다.

___

## 📖 5.3 코틀린에서 SAM 인터페이스 정의: fun interface

- 코틀린에서는 함수형 인터페이스를 사용해야 할 부분에서 함수 타입을 사용해 행동을 표현할 때가 있다.
- 이때 fun interface 를 정의하면 자신의 함수형 인터페이스를 정의 할수 있다.
- 코틀린의 함수형 인터페이스는 정확히 하나의 추상 메서드만 포함하지만 다른 비추상 메서드를 여럿 가질수 있다.
    - 함수 타입의 시그니처에 들어맞지 않는 여러 복잡한 구현을 표현할수 있다.

```kotlin
fun interface IntCondition {
    fun check(i: Int): Boolean
    fun checkString(s: String) = check(s.toInt())
}
```

- fun interface 라고 정의된 타입의 파라미터를 받는 함수가 있을 때 람다 구현이나 람다에 대한 참조를 직접 넘길수 있고
  두 경우 모두 동적으로 인터페이스 구현을 인스턴스화 해준다.
- 함수형 타입 시그니처로 표현할 수 없는 연산이나 더 복잡한 계약을 표현하려면 함수형 인터페이스가 좋은 선택일 수 있다.

## 📖 5.4 수신 객체 지정 람다: with, apply, also

### 🔖 5.4.1 with 함수

```kotlin
fun alphabet(): String {
    val stringBuilder = StringBuilder()
    return with(stringBuilder) {
        for (letter in 'A'..'Z') {
            append(letter)
        }
        toString()
    }
}
```

- with 함수는 첫 번째 인자로 받은 객체를 두 번째 인자로 받은 람다의 수신 객체로 만든다.
- 람다 안에서는 명시적인 this 참조를 사용해 그 수신 객체에 접근 할수 있거나 보통 this 와 마찬가지로 메서드나 프로퍼티 이름만
  사용해 접근 할수 있다.
- with 가 반환하는 값은 람다 코드를 실행한 결과이며 , 그 결과는 람다식의 본문에 있는 마지막 식의 값이다.

### 🔖 5.4.2 apply 함수

- apply 는 항상 자신에 전달된 객체를 반환한다.

```kotlin
fun alphabet() = StringBuilder().apply {
    for (letter in 'A'..'Z') {
        append(letter)
    }
}.toString()
```

- apply 를 임의의 타입의 확장 함수로 호출 할수 있다.
- apply 를 호출한 객체는 apply에 전달된 람다의 수신 객체가 된다.
- 인스턴스를 만들면서 즉시 프로퍼티 중 일부를 초기화해야 하는 경우 apply 가 유용하다.

```kotlin
fun alphabet() = buildString {
    for (letter in 'A'..'Z') {
        append(letter)
    }
}
```

### 🔖 5.4.3 객체에 추가 작업 수행: also

- also 함수도 수신객체를 받으며, 그 수신 객체에 대한 어떤 동작을 수행한 후 수신 객체를 돌려준다.
- also 의 람다 안에서는 수신객체를 인자로 참조 해야한다.
- 원래의 수신 객체를 인자로 받는 동작을 실행할 때 also 가 유용하다.

```kotlin
fun main() {
    val fruits = listOf("Apple", "Banana", "Cherry")
    val upperCaseFruits = mutableListOf<String>()
    val reversedLongFruits = fruits
        .map { it.uppercase() }
        .also { upperCaseFruits.addAll(it) }
        .filter { it.length > 5 }
        .also { println(it) }
        .reversed()
}
```

- 수신 객체 지정 람다는 DSL 을 만들 때 아주 유용한 도구이다.

