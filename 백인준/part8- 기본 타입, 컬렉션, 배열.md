# 📚 8장 기본 타입, 컬렉션 배열

___

## 📖 8.1 원시 타입과 기본 타입

- 코틀린은 원시 타입과 래퍼 타입을 구분하지 않는다.

### 🔖 8.1.1 정수, 부동소수점 수, 무자, 불리언 값을 원시 타입으로 표현

```kotlin
val i Int = 1
val list: List<Int> = listOf(1, 2, 3)

fun showProgress(progress: Int) {
    val percent = progress.coerceIn(0, 100)
}
```

- 원시, 래퍼 타입 구분하지 않고 항상 같은 타입 사용

### 🔖 8.1.2 양수를 표현하기 위해 모든 비트 범위 사용: 부호 없는 숫자 타입

- 비트 및 바이트 수준에서 작업하거나 비트맵의 픽셀, 파일의 바이트 또는 기타 이진 데이터를 조작하는 경우와 같이 양수 값을
  나타내는 정수의 전체 비트 범위를 활용 해야 하는 상황이 가끔 있다.
- 이런경우 코틀린은 JVM의 일반적인 원시 타입을 확장해 부호 없는 타입을 제공한다.
  다른 원시타입과 마찬가지로 코틀린의 부호 없는 수도 필요할 때만 래핑된다.

### 🔖 8.1.3 널이 될 수 있는 기본 타입: Int?, Boolean? 등

- null 참조를 자바의 참조 타입의 변수에만 대입할 수 있기 때문에 널이 될 수 있는 코틀린 타입은 자바 원시 타입으로 표현할수 없다.
- 코틀린에서 널이 될 수 있는 원시타입을 사용하면 그 타입은 자바의 래퍼타입으로 컴파일 된다.
-

```kotlin
data class Person(
    val name: String,
    val age: Int? = null
) {
    fun isOlderThan(other: Person): Boolean? {
        if (age == null || other.age == null)
            return null
        return age > other.age
    }
}

fun main() {
    println(Person("Sam", 35).isOlderThan(Person("Amy", 42)))
    // false
    println(Person("Sam", 35).isOlderThan(Person("Jane")))
    // null
}
```

- 제네릭 클래스의 경우 래퍼 타입을 사용한다.
- 클래스의 타입인자로 원시 타입을 넘기면 코틀린은 그 타입에 대한 박스 타입을 사용

### 🔖 8.1.4 수 변환

- 코틀린은 한 타입의 수를 다른 타입의 수로 자동 변환하지 않는다.
- 결과 타입이 허용하는 수의 법위가 원래타입의 범위보다 넓은 경우조차도 자동 변환은 불가능하다.

```kotlin
val i = 1
val l: Long = i // ERROR

val i = 1
val l: Long = i.toLong()
```

```kotlin
fun main() {
    val x = 1
    println(x.toLong() in listOf(1L, 2L, 3L))
}
```

- 코틀린은 개발자의 혼란은 피하고자 타입 변환을 명시하기로 결정
- 코틀린에서 타입을 명시적으로 변환해서 같은 타입의 값으로 만든후 비교 해야 한다.

### 🔖 8.1.5 Any 와 Any?: 코틀린 타입의 계층의 뿌리

- 자바에서 Object가 클래스 계층의 최상위 타입이듯 코틀린에서는 Any 타입이 모든 널이 될수 없는 타입의 조상 타입이다.
- 코틀린에서는 Any가 Int 등의 원시 타입을 포함한 모든 타입의 조상 타입이다.

```kotlin
val answer: Any = 42
```

- Any 타입은 java.lang.Object에 대응한다.

### 🔖 8.1.6 Unit 타입: 코틀린의 void

- 코틀린의 Unit타입은 자바 void 와 같은 기능을 한다.
- 내용을 전혀 반환하지 않는 함수의 반환 타입으로 Unit을 쓸 수 있다.

```kotlin
fun f() { /* ... */
}
```

- Unit은 모든 기능을 갖는 일반적인 타입이며 void 와 달리 Unit을 타입 인자로 사용가능
- Unit 타입에 속한 값은 단 하나 뿐이며 그 이름도 Unit 이다.
- Unit 타입의 함수는 Unit 값을 암시적으로 반환

```kotlin
interface Processor<T> {
    fun process(): T
}

class NoResultProcessor : Processor<Unit> {
    override fun process() {
    }
}
```

### 🔖 8.1.7 Nothing 타입: 이 함수는 결코 반환되지 않는다.

- 코틀린에는 결코 성공적으로 값을 돌려주는 일이 없으므로 '반환값'이라는 개변 자체가 의미가 없는 함수가 일부 존재한다.

```kotlin
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}
fun main() {
    fail("Error occurred")
}
```

- Nothing 타입은 아무 값도 포함하지 않는다.
- Nothing은 함수의 반환 타입이나 반환 타입으로 쓰일 타입 파라미터로만 쓸 수 있다.

___

## 📖 8.2 컬렉션과 배열

### 🔖 8.2.1 널이 될 수 있는 값의 컬렉션과 널이 될 수 있는 컬렉션

```kotlin
fun readNumbers(text: String): List<Int?> {
    val result = mutableListOf<Int?>()
    for (line in text.lineSequence()) {
        val numberOrNull = line.toIntOrNull()
        result.add(numberOrNull)
    }
    return result
}
```

- List<Int?> Int? 타입의 값을 저장할수 있다.
- List<Int>? 는 리스트 가 null 이가 될수있다.

```kotlin
fun readNumbers2(text: String): List<Int?> =
    text.lineSequence().map { it.toIntorNull() }.toList()
```

- 목록의 개별 요소가 없을 수도 있지만 목록 전체가 없을 수도 있음을 표현할 수 있다.
- 이를 작성하는 Kotlin 방 식은 두 개의 물음표가 있는 List<Int?>? 이다.

```kotlin
fun addValidNumbers(numbers: List<Int?>) {
    var sumOfValidNumbers = 0
    var invalidNumbers = 0
    for (number in numbers) {
        if (number != null) {
            sumOfValidNumbers += number
        } else {
            invalidNumbers++
        }
    }
    println("Sum of valid numbers: $sumOfValidNumbers")
    println("Invalid numbers: $invalidNumbers")
}

fun main() {
    val input = """
		1 abc 42
    """.trimIndent()
    val numbers = readNumbers(input)
    addValidNumbers(numbers)
    // Sum of valid numbers: 43
    // Invalid numbers: 1
}
fun addValidNumbers(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()
    println("Sum of valid numbers: ${validNumbers.sum()}")
    println("Invalid numbers: ${numbers.size - validNumbers.size}")
}
```

- null 가능한 값의 컬렉션을 가져와서 null을 필터링하는 것은 매우 일반적인 작업임으로,
  Kotlin에서는 이를 수행할 수 있는 표준 라이브러리함수 filterNotNull을 제공한다.

### 🔖 8.2.2 읽기 전용과 변경 가능한 컬렉션

- 코틀린 컬렉션과 자바 컬렉션을 나눈 가장 중요한 특성 중 하나는 코틀린에서는 컬렉션 안의 데이터에 접근하는 인터페이스와
  컬렉션 안의 데이터를 변경하는 인터페이스를 분리했다는 점이다.
- 데이터를 수정하려면 MutableCollection 을 사용하여야 한다.

```kotlin
fun <T> copyElements(
    source: Collection<T>,
    target: MutableCollection<T>
) {
    for (item in source) {
        target.add(item)
    }
}

fun main() {
    val source: Collection<Int> = arrayListOf(3, 5, 7)
    val target: MutableCollection<Int> = arrayListOf(1)
    copyElements(source, target)
    println(target)
    // [1, 3, 5, 7]
}
```

- 컬렉션 인터페이스를 사용할 때 항상 염두에 둬야 할 핵심은 읽기 전용 컬렉션이더라도 꼭 변경 불가능한 컬렉션일 필요는 없다는 점이다.
- 읽기 전용 인터페이스 타입인 변수를 사용할때 그 인터페이스는 실제로는 어떤 컬렉션 인스턴스를 가리키는 수많은 참조중 하나일 수 있다.
- 읽기 전용 컬렉션이 항상 스레드 안전 하지는 않다는 점이다.

### 🔖 8.2.3 코틀린 컬렉션과 자바 컬렉션은 밀접히 연관됨

- 모든 코틀린 컬렉션은 그에 상응하는 자바 컬렉션 인터페이스의 인스턴스이다. 따라서 코틀리과 자바 사이를 오갈때 아무 변화도 필요없고 데이터
  복사도 필요없다.
- 코틀린은 모든 자바 컬렉션 인터페이스 마다 읽기전용 변경 가능한 인터페이스 2가지 표현을 제공한다.

### 🔖 8.2.4 자바에서 선언한 컬렉션은 코틀린에서 플랫폼 타입으로 보임

- 플랫폼 타입의 경우 코틀린쪽에서는 널관련된 정보가 없다.
- 컴파일러는 코틀린 코드가 그 타입을 널이 될수 있는 타입이나 널이 될수 없는 타입 어느쪽으로든 허용한다.
- 하지만 컬렉션 타입이 시그니처에 들어간 자바 메서드 구현을 오버라이드 하려는 경우 읽기 전용 컬렉션과 변경 가능 컬렉션의 차이가 문제가 된다.
- 이상황에 선택이 필요
    - 컬렉션이 null 이될수 있는가?
    - 컬렉션의 원소가 null 이 될수 있는가?
    - 여러분이 작성할 메서드가 컬렉션을 변경할 수 있는가?

### 🔖 8.2.5 성능과 상호운용을 위해 객체의 배열이나 원시 타입의 배열을 만들기

- 자바 main 함수의 표준 시그니처에는 밴열 파라미터가 들어있기 떄문에 코틀린을 시작하자마자 코틀린 배열 타입과 마주치게된다.

```kotlin
fun main(args: Array<String>) {
    for (i in args.indices) {
        println("Argument $i is: ${args[i]}")
    }
}
```

- 코틀린에서 배열 만드는법
    - arrayOf 함수는 인자로 받은 원소들을 포함하는 배열을 만든다.
    - arrayOfNulls 함수는 모든 원소가 null 인 정해진 크기의 배열을 만들 수 있다.(원소타입이 null 일되수있는값만 사용가능 )
    - Array 생성자는 배열 크기와 람다를 인자로 받아 람다를 호출해서 각 배열 원소를 초기화 해준다.

```kotlin
fun main() {
    val letters = Array<String>(26) { i -> ('a' + i).toString() }
    println(letters.joinToString(""))
    // abcdefghijklmnopqrstuvwxyz
}

```

- Array<String>(26) 처럼 타입 인자를 굳이 지정했지만 생략해도 컴파일러가 알아서 원소 타입을 추론해준다.
- 코틀린에서는 배열을 인자로 받는 자바 함수를 호출하거나 varag파라미터를 받는 코틀린 함수를 호출하기 위해 가장 자주 배열을 만든다.
- toTypedArray 메서드를 사용하면 쉽게 컬렉션을 배열로 만들수 있다.

```kotlin
fun main() {
    val strings = listOf("a", "b", "c")
    println("%s/%s/%s".format(*strings.toTypedArray())) // a/b/c
}
```

- 코틀린은 원시 타입의 배열을 표현하는 별도 클래스를 각 원시타입마다 하나씩 제공한다.
  ByeArray, CharArray, BooleanArray 등의 원시타입의 배열을 제공
- 그런 배열의 값은 박싱하지 않고 가장 효율적인 방식으로 저장된다.

```kotlin
val fiveZeros = IntArray(5)
val fiveZerosToo = intArrayof(0, 0, 0, 0, 0, 0)


fun main() {
    val squares = IntArray(5) { i -> (i + 1) * (i + 1) }
    println(squares.joinToString())
    // 1, 4, e, 16, 25
}
```

- 원시타입의 배열을 만드는 방법
    - 각 배열 타입의 생성자는 size 인자를 받아 해당 원시 타입의 기본값으로 초기화된 size 크기의 배열을 만든다.
    - 팩토리 함수는 여러 값을 가변 인자로 받아 그런 값이 들어간 배열을 반환한다.
    - 크기와 람다를 인자로 받는 다른 생성자를 사용한다.