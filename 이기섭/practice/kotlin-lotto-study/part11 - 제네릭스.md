#11장 제네릭스
- 제네릭 함수와 클래스으 ㅣ정의하는 방법
- 타입 소거와 실체화된 타입 파라미터
- 선언 지점과 사용 지점 변성
- 타입 별명

##11.1 타입 인자를 받는 타입 만들기: 제네릭 타입 파라미터

- 제네릭스를 사용하면 타입 파라미터를 받는 타입을 정의 가능
- 코틀린은 타입 인자 추론 가능
`` ex - val authors = listOf("Dmitry","Svetlana")``
- 코틀린에는 raw 타입이 없다 
    -  raw 타입은 코틀린의 플랫폼 타입인 Any! 타입을 사용 가능
    
###11.1.1 제네릭 타입과 함께 동작하는 함수와 프로퍼티
제네릭 함수 호출하기
```kotlin
fun main() {
    val letters = ('a'..'z').toList()
    println(letters.slice<Cahr>(0..2))
  //[a,b,c]
}
```

제네릭 고차 함수 호출하기
```kotlin
fun main() {
    val authors = listOf("Sveta", "Seb","Roman","Dima")
    val readers = mutableListOf<String>("Seb","Hadi")
    println(readers.filter {it !in authors})
    // [Hadi]
}
```

``
확장 프로퍼티만 제네릭하게 만들 수 있다.
일반 프로퍼티를 제네릭 하게 정의하면 오류
``

``
ERROR: type paramter of a property must be used in its receiver type
``

### 11.1.2 제네릭 클래스를 홑화살괄호 구문을 사용해 선언한다
- 홑화살괄호 (<>)
- 클래스나 인터페이스 이름 뒤에 붙임

### 11.1.3 제네릭 클래스나 함수가 사용할 수 있는 타입 제한: 타입 파라미터 제약
- 타입 파라미터 제약 : type paramter constraint
- 어떤 타입을 제네릭 타입의 타입 파라미터에 대한 상계로 지정하면 그 제네릭 타입을 인스턴스화 할 때 사용하는 타입 인자는 반드시 그 상계 타입이거나 그 하위 타입이어야 한다.
- 제약 : 콜론(:) 뒤
``fun <T : Number> List<T>.sum(): T``
  
타입 파라미터를 제약하는 함수 선언하기
```kotlin
fun <T: Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}

fun main() {
    println(max("kotlin", "java"))
    //kotlin
}
```

타입 파라미터에 여러 제약을 가하기
```kotlin
fun <T> ensureTrailingPeriod(seq: T)
      where T : CharSequence, T: Appendable {
      if (!seq.endsWith('.')) {
          seq.append('.')
      }
    }   

fun main() {
    val helloWorld = StringBuilder("Hello World")
  ensureTrailingPeriod(helloWorld)
  print(helloWorld)
  //Hello World.
}
```

###11.1.4 명시적으로 타입 파라미터를 널이 될 수 없는 타입으로 표시해서 널이 될 수 있는 타입 인자 제외시키기
아무런 상계를 정하지 않은 타입 파라미터는 Any? 를 상계로 정한 파라미터와 같다
```kotlin
class Processor<T> {
    fun process(value: T) {
        value?.hashCode
    }
}
```
널 가능성 제외한 아무런 제약도 필요 없다면 Any 
```kotlin
class Processor<T :Any> {
    fun process(value: T) {
        value.hashCode()
    }
}
```

##11.2 실행 시점 제네릭스 동작: 소거된 타입 파라미터와 실체화된 타입 파라미터
- JVM의 제네릭스는 보통 타입소거 type erasure 를 사용해 구현됨

###11.2.1 실행 시점에 제네릭 클래스의 타입 정보를 찾을 때 한계: 타입 검사와 캐스팅
- 제네릭 타입 인자 정보는 런타임에 지워짐(자바와 같음)
- 이는 제네릭 클래스 인스턴스가 그 인스턴스를 생성할 때 쓰인 타입 인자에 대한 정보를 유지하지 않는다는 뜻.

제네릭 타입으로 타입 캐스팅하기
```kotlin

fun printSum(c: Collection<*>) {
    val intList = c as? List<Int> ?: throw IllegalArgumentException("List is expected")
    println(intList.sum())
}

fun main() {
    printSum(listOf(1,2,3))
    // 6 정상동작 
    printSum(setOf(1,2,3))
    // IllegalArgumentException: List is Expected - 집합은 리스트가 아니므로 예외 
}
```

알려진 타입 인자를 사용해 타입 검사하기
```kotlin
fun printSum(c: Collection<Int>) {
    when (c) {
        is List<Int> -> println("List sum: ${c.sum()}")
        is Set<Int> -> println("Set sum: ${c.sum()}")
    }
}

fun main() {
  printSum(listOf(1,2,3))
  // List sum: 6
  printSum(setOf(3,4,5))
  // Set sum: 12 
}
```

### 11.2.2 실체화된 타입 파라미터를 사용하는 함수는 타입 인자를 실행 시점에 언급할 수 있다
- 제네릭 타입의 타입인자 정보는 실행 시점에 지워짐
- 인라인 함수의 타입파라미터는 실체화 되어 위 제약을 피할 수 있다.
- inline 키워드 -> 컴파일러는 그 함수를 호출한 식을 모두 함수를 구현하는 코드로 변경
- reified 로 지정하면 value의 타입이 T의 인스턴스인지를 실행 시점에 검사 가능

실체화된 타입 파라미터를 사용하는 함수 정의하기
```kotlin
inline fun <reified T> isA(value: Any) = value is T

fun main() {
    println(isA<String>("abc"))
    // true
    println(isA<String>(123))
    //false
}
```

filterIsInstance를 간단하게 정리한 버전
```kotlin
inline fun <reified T>
        Iterable<*>.filterIsInstance(): List<T>
    val destination = mutableListOf<T>()
    for(element in this) {
        if(element is T) {
          destination.add(element)
        }
}
```

### 11.2.3 클래스 참조를 실체화된 타입 파라미터로 대신함으로써 java.lang.Class 파라미터 피하기
ServiceLoader
```kotlin
val serviceImpl = ServiceLoader.load(Service::class.java)
```
심플하게 
```kotlin
val serviceImpl = loadService<Service>()
```
loadService 정의
```kotlin
inline fun <reified T> loadService() {
    return ServiceLoader.load(T::class.java)
}
```
### 11.2.4 실체화된 타입 파라미터가 있는 접근자 정의

```kotlin
inline val <reified T> T.canonical: String
    get() = T::class.java.canonicalName

fun main() {
    println(listOf(1,2,3).canonical)
    // java.util.List
    println(1.canonical)
    //java.lang.Integer
}
```

### 11.2.5 실체화된 타입 파라미터의 제약
- 실체화로인한 제약으로 향후 완화 가능
- 다음의 경우 사용 가능
  - 타입 검사와 캐스팅(is, !is, as, as?)
  - 리플렉션 API (::class)
  - 코틀린 타입에 대응하는 java.lang.Class를 얻기(::class.java)
  - 다른 함수를 호출할 때 타입 인자로 사용
- 다음과 같은 일은 할 수 없음
  - 타입 파라미터 클래스의 인스턴스 생성하기
  - 타입 파라미터 클래스의 동반 객체 메서드 호출하기
  - 실체화된 타입 파리머터를 요구하는 함수를 호출하면서 실체화하지 않은 타입 파라미터로 받은 타입을 타입 인자로 넘기기
  - 클래스, 프로퍼티, 인라인 함수가 아닌 함수의 타입 파라미터를 reified로 지정하기
  
## 11.3 변성은 제네릭과 타입 인자 사이의 하위 타입 관계를 기술
### 11.3.1 변성은 인자를 함수에 넘겨도 안전한지 판단하게 해준다

```kotlin
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}

fun main() {
    val strings = mutableListOf("adb","bcd")
    addAnswer(strings)
    println(string.maxBy{it.length}
    // ClassCastException: Integer cannot be cast to String
}
```

### 11.3.2 클래스, 타입, 하위 타입
- List<Int> , List<String?> , List<List<String>> 모두 정상 타입
- Int 상위타입 Number

```kotlin
fun test(i: Int) {
    val n: Number = i
    fun f(s: String) {/*...*/}
    f(i) //Int 가 String의 하위 타입이 아니어서 컴파일 실패
}
```

- 널이 될 수 없는 타입은 , 널이 될 수 있는 타입의 하위 타입
- 서로 상위 타입도 아니고, 하위 타입도 아닌 경우 무공변

### 11.3.3 공변성은 하위 타입 관계를 유지한다.
- 공변성 -> 하위 타입 관게를 유지한다.
- out 을 통해 선언

```kotlin
interface Producer(out T) {
    fun produce(): T
}
```

무공변 컬렉션 역할을 하는 클래스 정의하기
```kotlin
open class Animal {
    fun feed() {/* ... */}
}
class Herd<T: Animal> {
    val size: Int get() = /* ... */
    operator fun get(i: Int): T{/*...*/}
    
}
fun feedAll(animals: Herd<Animal>) {
    for (i in 0..animals.size) {
        animals[i].feed()
    }
}
```

-타입 불일치를 해결하기 위해 강제 캐스팅을 하는 것은 결코 올바른 방법이 아니다.
- Herd 를 공변적인 클래스로 만들고 호출 코드를 적절히 변경

컬렉션 역할을 하는 공변적인 클래스 사용하기
```kotlin
class Herd<out T: Animal> {
    /* ... */
}

fun takeCareOfCats(cats: Herd<Cat>) [
    for (i in 0 ..cats.size) {
        cats[i].cleanLitter()
    }
    feedAll(cats)
]
```

```kotlin
interface Transformer<T> {
    fun transform(t: T) : T //파라미터는 in , 반환타입은 out 위치
}
```

- 클래스 타입 파라미터 T 앞에 out 키워드를 붙이면 클래스 안에서 T를 사용하는 메서드가 아웃 위치에서만 T를 사용하도록 허용하고
인 위치에서는 T를사용하지 못하게 막음. 타입안정성 보장
- 하위 타입 관계가 유지된다 
- T를 아웃 위치에서만 사용할 수 있다.

### 11.3.4 반공변성은 하위 타입 관계를 뒤집는다.
- 반공변성은 공변성을 거울이 비친 상
- 하위 타입 관계는 그 클래스의 타입 파라미터의 상하위 타입 관계와 반대

```kotlin
interface Comparator<in T> {
    fun compare(e1: T, e2: T): Int {/* ... */}
}

sealed class Fruit {
    abstract val weigth: Int
}

data class Apple(
  override val weight: Int,
  val color: String,
): Fruit()

data class Orange(
  override val weight: Int,
  val juicy: Boolean,
): Fruit()

fun main() {
    val weightComparator = Comparator<Fruit> {a, b -> 
      a.weigth - b.weigth
    }
  val fruits = List<Fruit> = listOf(
    Orange(180, true),
    Apple(100, "green")
  )
  val apples: List<Apple> = listOf(
    Apple(50, "red"),
    Apple(120, "green"),
    Apple(155, "yellow")
  )
  println(fruits.sortedWith(weightComparator))
  // [Apple()weight=100, color=green), Orange(weigth=100, juicy=true)]
  println(apples.sortedWith(weightComparator))
  // [Apple()weight=50, color=red), Apple(weigth=120, color=green)] ...
}
```

- 공변성
  - out T
  - 타입 인자의 하위 타입 관계가 제네릭 타입에서도 유지도니다.
  - T를 아웃 위치에만 사용할 수 있다
- 반공변성
  - in T
  - 타입 인자의 하위 타입 관계가 제네릭 타입에서 뒤집힌다.
  - T를 인 위치에서만 사용할 수 있다.
- 무공변성
  -  하위 타입 관계가 성립하지 않는다
  - T를 아무 위치에서나 사용할 수 있다.
  
### 11.3.5 사용 지점 변성을 사용해 타입이 언급되는 지점에서 변성 지정
- 선언지점 변성 : 클래스를 선언하면서 변성 지정
  - 그 클래스를 사용하는 모든 장소에 변성 지정자가 영향을 끼침
- 사용지점 변성: 타입 파라미터가 있는 타입을 사용할 때마다 그 타입 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는지 명시

무공변 파라미터 타입을 사용한느 데이터 복사 함수
```kotlin
fun <T> copyData(source: MutableList<T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}
```

타입 파라미터가 둘인 데이터 복사 함수
```kotlin
fun <T:R, R> copyData(source: MutableList<T>, destination: MutableList<R>) {
    for(item in source) {
        destination.add(item)
    }
}

fun main() {
    val ints = mutableList(1,2,3)
    val anyItems = mutableListOf<Any>()
    copyData(ints, anyItems)
    println(anyItems)
    // [1,2,3]
}
```

아웃 프로젝션 타입 파라미터를 사용하는 데이터 복사 함수
```kotlin
fun <T> copyData(source: MutableList<out T>, destination: MutableList<T>) {
    for(item in source) {
        destnation.add(item)
    }
}
```

코틀린의 사용 지점 변성 선언은 자바의 한정 와일드카드와 똑같다, 코틀린 MutableList<out T> 는 자바 MutableList<? extends T> 와 같은 뜻이며, 
코틀린 MutableList<in T> 는 MutableList<? super T> 에 대응한다.

### 11.3.6 스타 프로젝션: 제네릭 타입 인자에 대한 정보가 없음을 표현하고자 * 사용
- 스타 프로젝션 : 제네릭 타입 인자 정보가 없음을 표현
- List<*> != List<Any?>

입력 검증을 위한 인터페이스
```kotlin
interface  FieldValidator<in T> {
    fun validate(input: T): Boolean
}

object DefaultStringValidator: FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty()
}

object DefaultIntValidator: FieldValidator<Int> {
    override fun validate(input: Int) = input >= 0
}
```

검증기를 가져오면서 명시적 타입 캐스팅 사용하기
```kotlin
val stringValidator = validators[String::class] as FieldValidator<String>
println(stringValidator.validate(""))
//false
```

검증기를 잘못 가져온 경우
```kotlin
val stringValidator = validators[Int::class] as FieldValidator<String>
stringValidator.validate("")
// java.lang.ClassCastException
```