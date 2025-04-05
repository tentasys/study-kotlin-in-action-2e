# 📚 6장 컬렉션과 시퀀스

___

## 📖 6.1 컬렉션에 대한 함수형 API

### 🔖 6.1.1 원소 제거와 변환: filter 와 map

- 어떤 술어를 바탕으로 컬렉션의 원소를 걸러내거나, 컬렉션의 각 원소를 다른 형태로 변환 하고 싶을때 사용

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val list = listOf(1, 2, 3, 4)
    list.filter { it % 2 == 0 }
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    people.filter { it.age >= 30 }
}
```

- filter 함수는 컬렉션을 순회하면서 주어진 람다가 true 를 반환하는 원소들만 모은다.
- 주어진 술어와 일치하는 원소들로 이뤄진 새 컬렉션을 만들수 있지만 그 과정에서 원소를 변환하지 않는다.
- 추출로 생각할수 있음

```kotlin
fun main() {
    val list = listOf(1, 2, 3, 4)
    list.map { it * it }
}
```

- map 은 입력 컬렉션의 원소를 변환 할수 있게 해준다.
- 주어진 함수를 컬렉션의 각 원소에 적용하고 그 결괏값들을 새컬렉션에 모아준다.

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7)
    val filtered = numbers.filterIndexed { index, element -> index % 2 == 0 && element > 3 }
    val mapped = numbers.mapIndexed { index, element -> index + element }
}
```

- 걸러내거나 변환하는 연산이 원소의 값 뿐 아니라 인덱스에 따라서도 달라진다면 형제 함수인 filterIndexed 와 mapIndexed 사용

### 🔖 6.1.2 컬렉션 값 누적: reduce 와 fold

- 컬렉션의 정보를 종합하는데 사용
- 원소로 이뤄진 컬렉션을 받아서 한 값을 반환한다.

```kotlin
fun main() {
    val list = listOf(1, 2, 3, 4)
    list.reduce { acc, element -> acc + element }
}
```

- reduce
    - 컬렉션의 첫 번째 값을 누적기에 넣는다.
    - 람다가 호출되면서 누적값과 2번째 원소가 인자로 전달된다.

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    val folded = people.fold("") { acc, person -> acc + person.name }
}
```

- fold 는 컬렉션의 첫 번째 원소를 누적 값으로 시작하는 대신, 임의의 시작 값을 선택할 수 있다.

```kotlin
data class Person(val name: String, val age: Int)

val list = listOf(1, 2, 3, 4)
list.runningReduce { acc, element -> acc + element }
val people = listOf(Person("Alice", 30), Person("Bob", 31))
val folded = people.runningFold("") { acc, person -> acc + person.name }
```

- reduce 와 fold 의 중간 단계 모든 누적 값을 뽑아 내고 싶으면 runningReduce,runningFold 를 사용

### 🔖 6.1.3 컬렉션에 술어 적용: all, any, none, count, find

- all,any,none
    - 컬렉션에 어떤 조건을 만족하는지 판단
- count
    - 조건을 만족하는 원소의 개수를 반환
- find
    - 조건을 만족하는 첫 번째 원소를 반환

```kotlin
data class Person(val name: String, val age: Int)

val canBeInClub27 = { p: Person -> p.age >= 27 }
fun main() {
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    people.all(canBeInClub27)
    people.any(canBeInClub27) // !all 과 같다.
    people.none(canBeInClub27) // !any 와 같다.
    people.count(canBeInClub27)
    people.find(canBeInClub27) // 만족하는 원소가 없는 경우 null 반환
}
```

### 🔖 6.1.4 리스트를 분할해 리스트의 쌍으로 만들기: partition

- 컬렉션을 어떤 술어를 만족하는 그룹과 그렇지 않은 그룹으로 나눌때 사용
    - filter , filterNot 을 사용해도 되지만 더 간결하게 사용가능

```kotlin
data class Person(val name: String, val age: Int)

val canBeInClub27 = { p: Person -> p.age >= 27 }
fun main() {
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    people.partition(canBeInClub27)
}
```

### 🔖 6.1.5 리스트를 여러 그룹으로 이뤄진 맵으로 바꾸기: groupBy

- 컬렉션의 원소를 어떤 특성에 따라 여러 그룹으로 나누고 싶을때 사용

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    people.groupBy { it.age }
}
```

- 컬렉션의 원소를 구분하는 특성이 키이고 키 값에 따른 각 그룹의 값인 맵이다.

### 🔖 6.1.6 컬렉션을 맵으로 변환: associate, associateWith, associateBy

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val people = listOf(Person("Alice", 30), Person("Bob", 31))
    val nameToAge = people.associate { it.name to it.age }
    val personToAge = people.associateWith { it.age }
    val ageToPerson = people.associateBy { it.age }
}
```

- associate
    - 원소를 그룹화 하지 않으면서 컬렉션으로부터 맵을 만들어내고 싶을때 사용
- associateWith
    - 컬렉션의 원래 원소를 키로 사용
    - 람다는 그 원소에 대응하는 값을 만든다.
- assoicateBy
    - 컬렉션의 원래 원소를 맵의 값으로 하고 람다가 만들어 내는 값을 맵의 키로 사용

### 🔖 6.1.7 가변 컬렉션의 원소 변경: replaceAll, fill

```kotlin
fun main() {
    val names = mutableListOf("Martin", "Samuel")
    names.replaceAll { it.uppercase() }
    names.fill("replace")
}
```

- replaceAll
    - mutableList 에 적용하면 람다로 얻은 결과로 컬렉션의 모든 원소를 변경
- fill
    - 가변 리스트의 모든 원소를 똑같은 값으로 바꿀때 사용

### 🔖 6.1.8 컬렉션의 특별한 경우 처리 : ifEmpty

```kotlin
fun main() {
    val empty = emptyList<String>()
    empty.ifEmpty { listOf("no", "values") }
}
```

- 컬렉션 입력에 비어있지 않은 경우, 즉 처리 대상이 될 원소가 있는 경우에만 처리를 계속 하는것이 타당한 경우가 있다.
- ifEmpty 를 사용하면 아무 원소도 없을 때 기본값을 생성하는 람다를 제공한다.

### 🔖 6.1.9 컬렉션 나누기: chunked와 windowed

- 컬렉션의 데이터가 어떤 계열 정보를 표현할 때 데이터를 연속적인 시간의 값들로 처리하고 싶을때 사용

```kotlin
fun main() {
    val temperatures = listOf(27.7, 29.8, 22.0, 35.5, 19.1)
    println(temperatures.windowed(3))
    // [[27.7, 29.8, 22.0], [29.8, 22.0, 35.5], [22.0, 35.5, 19.1]]
    println(temperatures.windowed(3) { it.sum() / it.size })
    // [26.5, 29.099999999999998, 25.53333333333333]
    println(temperatures.chunked(2))
}
```

- windowed
    - 윈도우의 인덱스를 1만큼 밀어서 람다식 적용
- chunked
    - 입력 컬렉션에 대해 슬라이딩 윈도우를 실행하는 대신 컬렉션을 어떤 주어진 크기의 서로 겹치지 않는 부분으로 나누고 싶을때 사용

### 🔖 6.1.10 컬렉션 합치기: zip

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val names = listOf("Joe", "Mary", "Jamie")
    val ages = listOf(22, 31, 31, 44, 0)
    println(names.zip(ages))
    // [(Joe, 22), (Mary, 31), (Jamie, 31)]
    println(names.zip(ages) { name, age -> Person(name, age) })
    // [Person(name=Joe, age=22), Person(name=Mary, age=31), Person(name=Jamie, age=31)]
    println(names zip ages)
}
```

- zip 함수를 사용해 두 컬렉션에서 같은 인덱스에 있는 원소들의 쌍으로 이뤄진 리스트를 만들수 있다.
- 결과 컬렉션의 길이는 두 입력 컬렉션 중 더 짧은 쪽의 길이와 같다.
- 중위 표기법으로 만들수 있고 중위 표기법 사용시 람다는 사용불가능

### 🔖 6.1.11 내포된 컬렉션의 원소 처리 : flatMap 과 flatten

```kotlin
class Book(val title: String, val authors: List<String>)

val library = listOf(
    Book("Kotlin in Action", listOf("Isakova", "Elizarov", "Aigner", "Jemerov")),
    Book("Atomic Kotlin", listOf("Eckel", "Isakova")),
    Book("The Three-Body Problem", listOf("Liu"))
)

fun main() {
    val authorsMap = library.map { it.authors }
    println(authorsMap)
    // [[Isakova, Elizarov, Aigner, Jemerov], [Eckel, Isakova], [Liu]]
    val authorsFlatMap = library.flatMap { it.authors }
    println(authorsFlatMap)
    // [Isakova, Elizarov, Aigner, Jemerov, Eckel, Isakova, Liu]
    println(authorsFlatMap.toSet())
    // [Isakova, Elizarov, Aigner, Jemerov, Eckel, Liu]
}
```

- flatMap 함수를 사용하면 별도의 중첩 없이 라이브러리에 있는 모든 저작자의 집합을 계산할 수 있다.
- 먼저 인자로 주어진 함수에 따라 각 요소를 컬렉션으로 변환(또는 매핑)한 다음(맵 함수에서 본 것처럼) 이러한 목록을 하나로 결합(또는 평탄화)한다.

___

## 📖 6.2 지연 계산 컬렉션 연산: 시퀀스

```kotlin
people.map(Person::name).filter { it.startsWith("A") }
```

- Kotlin 표준 라이브러리 참조에 따르면 맵과 필터는 모두콜렉션을 반환한다.
- 일련의 호출은 맵 함수의 결과를 담는 목록과 필터의 결과를 담는 목록, 두 개의 목록을 생성한다.
- 소스 목록에 두 개의 요소가 포함되어 있을 때는 문제가 되지 않지만, 목록이 백만개라면 효율성이 훨씬 떨어진다.

```kotlin
people
    .asSequence()
    .map(Person::name)
    .filter { it.startsWith("A") }
    .toList()
```

- 모든 컬렉션은 확장 함수 asSequence를 호출하여 시퀀스로 변환할 수 있다.
- 시퀀스에서 일반목록으로 반대변환을 하려면 toList를 호출하면 된다.

### 🔖 6.2.1 시퀀스 연산 실행: 중간 연산과 최종 연산

- 중간 연산
    - 다른 시퀀스를 반환
    - 최초 시퀀스의 원소를 변환하는 방법을 안다.
- 최종 연산
    - 결과를 반환
    - 최초 컬렉션에 대해 변환을 적용한 시퀀스 에서 일련의 계산을 수행해 얻을수 있는 컬렉션이나 원소, 수 또는 객체

```kotlin
fun main() {
    println(
        listOf(1, 2, 3, 4)
            .asSequence()
            .map { it * it }
            .find { it > 3 }
    )
```

- 시퀀스 대신 컬렉션에 동일한 연산을 적용하면 먼저 맵의 결과가 평가되어 초기 컬렉션의 모든 요소가 변환된다.
- 시퀀스의 경우 지연 접근 방식을 사용하면 일부 요소의 처리를 건너 뛸 수 있다.
