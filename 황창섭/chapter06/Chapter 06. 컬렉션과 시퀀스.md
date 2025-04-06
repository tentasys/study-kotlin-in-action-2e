# Chapter 06. 컬렉션과 시퀀스

- 함수형 스타일로 컬렉션 다루기
- 시퀀스: 컬렉션 연산을 지연시켜 수행하기

## 6.1 컬렉션에 대한 함수형 API

### 6.1.1 원소 제거와 변환: filter와 map

```kotlin
val maxAge = people.maxByOrNull(Person::age)?.age
people.filter { it.age == maxAge }
```

- filter, map이 값을 넘어 인덱스에 따라도 연산이 달라진다면: filterIndexed, mapIndexed 사용

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7)
    val filtered = numbers.filterIndexed { index, element -> index % 2 == 0 && element > 3 }
    val mapped = numbers.mapIndex { index, element -> index + element }
}
```

### 6.1.2 컬렉션 값 누적: reduce와 fold

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7)
    println(list.reduce { acc, element -> acc + element }) // 10

    val people = listOf(
            Person("Alex", 29),
            Person("Natalia", 28)
    )
    
    // fold 안의 원소로 시작값 설정
    val folded = people.fold("") { acc, person -> acc + person.name }  // AlexNatalia
}
```

- 최종 결과값 뿐 아니라 중간 값을 확인하려면: runningReduce, runningFold

### 6.1.3 컬렉션에 술어 적용: all, any, none, count, find

- all: 모든 대상 일치
- any: 하나라도 일치
- none: 모든 대상 불일치

- 빈 컬렉션으로 검사할 경우
  - **all -> true**, none -> true, any -> false

- count: 개수
- find: firstOrNull 과 같음. 첫 번째 원소를 돌려주거나 하나도 일치하지 않으면 null

### 6.1.4 리스트를 분할해 리스트의 쌍으로 만들기: partition

```kotlin
val (comeIn, stayOut) = people.partition(canBeInClub27)
println(comeIn) // age=26
println(stayOut) // age=29, age=31
```

### 6.1.5 리스트를 여러 그룹으로 이뤄진 맵으로 바꾸기: groupBy

- groupBy는 Map<{Key Type}, {Group List}> 로 결과가 나옴

```kotlin
fun main() {
    val list = listOf("apple", "apricot", "banana", "cantaloupe")
    println(list.groupBy(String::first)) // { a = [apple, apricot], b = {banana}, c = [cantaloupe] }
}
```

### 6.1.6 컬렉션을 맵으로 변환: associate, associateWith, associateBy

```kotlin
fun main() {
    val people = listOf(
            Person("Alex", 29),
            Person("Natalia", 28)
    )
    val nameToAge = people.associate { it.name to it.age }
    
    // nameToAge["Alex"] = 29, nameToAge["Natalia"] = 28
}
```

```kotlin
fun main() {
    val people = listOf(
            Person("Alex", 22),
            Person("Natalia", 28),
            Person("Jamie", 22)
    )

    val personToAge = people.associateWith { it.age }
    println(personToAge) // { Person(name=Alex, age=22)=22

    val ageToPerson = people.associateBy { it.age }
    println(ageToPerson) // { 22=Person(name=Alex, age=22)
}
```

- 키 중복이 발생할 경우 덮어쓰기로 수행

### 6.1.7 가변 컬렉션의 원소 변경: replaceAll, fill

```kotlin
fun main() {
    val names = mutableListOf("Martin", "Samuel")
    names.replaceAll { it.uppercase() }
    println(names) // MARTIN, SAMUEL
    names.fill( "(redacted)")
    println(names) // [(redacted), (redacted)]
}
```

### 6.1.8 컬렉션의 특별한 경우 처리: ifEmpty

- 원소가 없을 경우 컬렉션 교체

### 6.1.9 컬렉션 나누기: chunked와 windowed

- windowed: 슬라이딩 윈도우 방식으로 새로운 컬렉션 생성, 3일간의 평균값 같은 로직을 구성할 때 유익
- chunked: 주어진 크기로 컬렉션을 나누고 싶을 때 사용. 예를 들어 7개의 원소를 가진 컬렉션에 chunk(3)을 적용할 경우 3,3,1 개의 원소를 가진 리스트로 변환

### 6.1.10 컬렉션 합치기: zip

- 두 컬렉션에서 같은 인덱스에 있는 원소들의 쌍으로 이뤄진 리스트 생성

```kotlin
fun main() {
    val names = listOf("Joe", "Mary", "Jamie")
    val ages = listOf(22, 31, 31, 44, 0)
    
    names.zip(ages) { name, age -> Person(name, age)} // Person(name=Joe, age=22), Person(name=Mary, age=31)
}
```

- 더 짧은 컬렉션에 맞춰서 생성됨
- zip을 연쇄로 호출하면 리스트가 늘어나는 것이 아닌 내포된 리스트가 될 뿐이다

### 6.1.11 내포된 컬렉션의 원소 처리: flatMap과 flatten

```kotlin

class Book(val title: String, val authors: List<String>)

fun main() {
  
    // library는 Book List
    val authors = library.flatMap { it.authors }
    println(authors) // 하나의 리스트로 작가 이름 나열
}
```

- 만약 별도 map이 필요없으면 flatten만 사용해도 됨 ex) List<List<Int>>

## 6.2 지연 계산 컬렉션 연산: 시퀀스

```kotlin
/*
 map의 결과로 콜렉션이 생기고 filter의 결과로도 콜렉션이 생겨 총 2개 콜렉션이 생성되는 이슈 -> 원소가 많을 때는 비효율적인 구조
 */
people.map(Person::name).filter { it.startsWith("A") }

// 스트림과 유사. 중간 임시 컬렉션을 사용하지 않음
people
        .asSequence()
        .map(Person::name)
        .filter { it.startsWith("A") }
        .toList()
```

- 지연연산이 적용되어 실제로 원소가 필요할 때 계산됨

### 6.2.1. 시퀀스 연산 실행: 중간 연산과 최종 연산

- 중간 연산: 다른 시퀀스를 반환
- 최종 연산: 결과 반환

```kotlin
fun main() {
    println(
            listOf(1,2,3,4)
                    .asSequence()
                    .map {
                        print(it)
                        it * it
                    }.filter {
                        print(it)
                        it % 2 == 0
                    }
    )
}
```

- 위 코드 실행 시 아무 내용도 출력되지 않음
- 최종 연산(toList) 호출 시 연기됐던 모든 계산이 수행

### 6.2.2 시퀀스 만들기

```kotlin
import java.io.File

fun File.isInsideHiddenDirectory() = generateSequence(this) { it.parantFile }.any { it.isHidden }
fun main() {
    val file = File("User/svtk/.HiddenDir/a.txt")
    println(file.isInsideHiddenDirectory())
}
```