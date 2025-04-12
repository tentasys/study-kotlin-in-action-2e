# 6장 컬렉션과 시퀀스

## 6.1 컬렉션에 대한 함수형 API

#### filteredIndexed, mapIndexed

```kotlin
fun main() {
    val numbers = listof(1, 2, 3, 4, 5)
    val filtered = numbers
        .filterIndexed { index, element -> 
            index % 2 == 0 && element > 3
        }
    
    println(filtered) // 5
    
    val mapped = numbers
        .mapIndexed { index, element -> 
            index + element
        }
    println(mapped) // 1, 3, 5, 7, 9
}
```

### 6.1.2 컬렉션 값 누적: reduce와 fold

#### reduce

```kotlin
fun main() {
    val list = listof(1, 2, 3, 4)
    println(list
        .reduce { acc, element -> 
        acc + element
        }
    ) // 10
}
```

#### fold

```kotlin
fun main() {
    val people = listof(
        People("Alex", 29),
        Person("Nat", 28)
    )
    val folded = people.fold("") { acc, person -> // fold("") 가 초기값 -> "" + "Alex" + "Nat" 
        acc + person.name
    }
    println(folded) // AlexNat
}
```

### 6.1.3 컬렉션에 술어 적용: all, any, none, count, find

- any : 조건을 만족하는 원소가 하나라도 있는 경우
- all : 모든 원소가 조건을 만족하는 경우
- none : !any
- count : 개수
- find : 조건을 만족하는 원소 하나 찾고 싶은 경우

### 6.1.4 리스트를 분할해 리스트의 쌍으로 만들기: partition

<table>
<tr>
<td align="center">partition x</td>
<td align="center">partition o</td>
</tr>
<tr>
<td>

```kotlin
fun main() {
    val people = listof(
        Person("Alice", 26),
        Person("Bob", 29),
        Person("Carol", 31)
    )
    val comeIn = people.filter(canBeInClub27)
    val stayOut = people.filterNot(canBeInClub27)
    println(comeIn) // Alice, 26
    println(stayOut) // Bob, Carol
}
```
</td>
<td>

```kotlin
fun main() {
    val (comeIn, stayOut) = people.partition(canBeInClub27)
    println(comeIn) // Alice
    println(stayOut) // Bob, Carol
}
```
</td>
</tr>
</table>

### 6.1.6 컬렉션을 맵으로 변환: associate, associateWith, associateBy

```kotlin
fun main() {
    val people = listof(Person("Joe", 22), Person("Mary", 31))
    val nameToAge = people.associate { it.name to it.age }
    println(nameToAge) // {Joe=22, Mary=31}
    println(nameToAge["Joe"]) // 22
    
    val personToAge = people.associateWith { it.age }
    println(personToAge) // {Person(name=Joe, age=22)=22, Person(name=Mary, age=31)=31
    
    val ageToPerson = people.associateBy { it.age }
    println(ageToPerson) // {22=Person(name=Joe, age=22), 31=Person(name=Mary, age=31)} // age가 같은 경우 overwrite
}
```

### 6.1.7 가변 컬렉션의 원소 변경: replaceAll, fill

- fill: 가변 리스트의 모든 원소를 동일한 값으로 변경

```kotlin

fun main() {
    val names = mutableListOf("Martin", "Samuel")
    names.replaceAll { it.toUpperCase() }
    println(names) // MARTIN, SAMUEL
    
    names.fill("(redacted)")
    println(names) // (redacted), (redacted)
}
```

### 6.1.8 컬렉션의 특별한 경우 처리: ifEmpty

```kotlin
fun main() {
    val empty = emptyList<String>()
    println(empty.ifEmpty { listof("no", "values") })
    println(empty) // no, values
    
    val full = listof("apple", "orange")
    println(full.ifEmpty { listOf("no", "values")}) // "apple", "orange"
}
```

### 6.1.9 컬렉션 나누기: chunked, windowed

```kotlin
fun main() {
    val temperatures = listof(27.7, 29.8, 22.0, 35.5, 19.1)
    println(temperatures.windowed(3)) // [[27.7, 29.8, 22.0], [29.8, 22.0, 35.5], [22.0, 35.5, 19.1]]
    
    println(temperatures.chunked(2)) // [[27.7, 29.8], [22.0, 35.5], [19.1]]
}
```

### 6.1.10 컬렉션 합치기: zip

```kotlin

fun main() {
    val names = listOf("Joe", "Mary", "Jamie")
    val ages = listof(22, 31, 31, 44, 0)
    
    println(names.zip(ages)) // [(Joe, 22), (Mary, 31), (Jamie, 31)] 나머지 원소 무시
    
    println(names.zip(ages) { name, age -> Person(name, age)}) // [Person(name=Joe, age=22), Person(name=Mary, age=31), Person(name=Jamie, age=31)]
}
```

## 6.2 지연 계산 컬렉션 연산: 시퀀스

- collection 함수(ex. map, filter) &rarr; 연쇄적으로 호출하면 매번 컬렉션 임시로 담아둠
- sequence : 중간 임시 컬렉션을 사용하지 않고 연산을 연쇄

```kotlin
people
    .asSequence()
    .map(Person::name)
    .filter { it.startsWith("A") }
    .toList()
```

- 성능 이점
- Sequence 내부 `iterator` 메서드 한개 존재, 원소 값 조회 가능

### 6.2.1 시퀀스 연산 실행: 중간 연산과 최종 연산

> - 중간 연산 : 다른 시퀀스 반환
> - 최종 연산 : 결과 반환

```kotlin
sequence
    .map { ...}      // 중간 연산 
    .filter { ... }  // 중간 연산
    .toList()        // 최종 연산 
```

- map, filter 변환이 지연되어 결과 얻을 필요가 있을 때 적용
- 최종 연산(= toList) 호출하면 연기됐던 모든 계산 수행
- 첫번째 원소 수행 후 두번째 원소 수행 &rarr; filter, map 순서로 수행 x

### 6.2.2 시퀀스 만들기

```kotlin
fun main() {
    val naturalNumbers = generateSequence(0) { it + 1 } // sequence 만듬
    val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
    println(numbersTo100.sum())
    // 5050
}
```

- `generateSequence` 시퀀스 생성 & 지연 계산