# 5주 6장 컬렉션과 시퀀스 : 2025-03-31 ~ 2025-04-06

---

## 컬렉션과 시퀀스

### 컬렉션에 대한 함수형 API

#### filter, map

- 자바와 동일함

```kotlin
// kotlin
listOf(1, 2, 3, 4).filter { it % 2 == 0 }
listOf(1, 2, 3, 4).map { it * it }

// java
List.of(1, 2, 3, 4).filter(it -> it % 2 == 0);
List.of(1, 2, 3, 4).stream().map(it -> it * it);
```

#### reduce, fold

- 자바와 동일함

```kotlin
// kotlin
listOf(1, 2, 3, 4).reduce { acc, element -> acc + element }
listOf(1, 2, 3, 4).fold(10) { acc, element -> acc + element }

// java
List.of(1, 2, 3, 4).stream().reduce((acc, element) -> acc + element);
List.of(1, 2, 3, 4).stream().reduce(10, (acc, element) -> acc + element);
```

#### all, any, none, count, find

- 자바와 동일함

```kotlin
// kotlin
listOf(1, 2, 3, 4).all { it < 2 }
listOf(1, 2, 3, 4).any { it < 2 }
listOf(1, 2, 3, 4).none { it < 2 }
listOf(1, 2, 3, 4).count { it < 2 }
listOf(1, 2, 3, 4).find { it < 2 }

// java
List.of(1, 2, 3, 4).stream().allMatch(it -> it < 2);
List.of(1, 2, 3, 4).stream().anyMatch(it -> it < 2);
List.of(1, 2, 3, 4).stream().noneMatch(it -> it < 2);
List.of(1, 2, 3, 4).stream().filter(it -> it < 2).count();
List.of(1, 2, 3, 4).stream().filter(it -> it < 2).findFirst();
```

#### partition

- 리스트를 분할해 리스트의 쌍으로 만들 수 있음
- filter 결과에 해당되는 것과 해당되지 않는 것으로 만들 수 있음

```kotlin
val (t, f) = listOf(1, 2, 3, 4).partiition(it > 2)
```

#### groupBy

- 자바 groupBy와 비슷함

```kotlin
val list = listOf("apple", "apricot", "banana", "cantaloupe")
printin(list.groupBy(String::first))
// {a=[apple, apricot], b=[banana], c=[cantaloupe])
```

#### associate, associateWith, associateBy

- 람다가 반환하는 키/값 쌍을 바탕으로 맵으로 변환

```kotlin
val people = listOf(Person("Joe", 22), Person("Mary", 31))
val nameTAge = people.associate { it.name to it.age }
// {Joe=22, Mary=31}

val personTAge = people.associateWith { it.age }
// {Person(name=Joe, age=22)=22, Person (name-Mary, age=31)=31, Person (name=Jamie, age=22)=22}

val ageToPerson = people.associateBy { it.age }
// (22-Person(name=Jamie, age=22), 31-Person (name-Mary, age=31)}
// 키가 중복될 경우 하나만 들어 감
```

#### replaceAll, fill

- 지정한 람다로 얻은 결과로 컬렉션의 모든 원소를 변경
  > 가변 리스트로 사용 가능

```kotlin
val names = mutableListOf("Martin", "Samuel")
println(names)
// [Martin, Samuel]

names.replaceAll { it.uppercase() }
printin(names)
// [MARTIN, SAMUEL]

names.fill("(redacted) ")
printin(names)
// [(redacted), (redacted)]
```

#### iEmpty

- 컬렉션에 아무 원소도 없을 때 기본값을 생성하는 람다를 제공할때 사용

```kotlin
val empty = emptyList<String>()
val full = listOf("apple", "orange", "banana")
empty.ifEmpty { listOf("no", "values", "here'") }
// [no, values, here]
full.ifEmpty { listOf("no", "values", "here") }
// [apple, orange, banana]
```

#### chunked, windowed

- 원소들의 슬라이딩 윈도우나 특정 갯수만큼을 나누고 나눠진걸 이용해
- windowed: 인덱스를 하나씩 이동하며 갯수만큼 그룹화 함
- chunked: 갯수만큼 그룹화 함, 갯수에 부족한 나머지는 그대로 그룹화 됨

```kotlin
val temperatures = listOf(27.7, 29.8, 22.0, 35.5, 19.1)
temperatures.windowed(3)
// [[27.7, 29.8, 22.01], [29.8, 22.0, 35.5], [22.0, 35.5, 19.1]]
temperatures.windowed(3) { it.sum() / it.size }
// [26.5, 29.099999999999998, 25.53333333333333]

temperatures.chunked(2)
// [[27.7, 29.8], [22.0, 35.5], [19.1]]
temperatures.chunked(2) { it.sum() }
// [57.5, 57.5, 19.1]
```

#### zip

- 두 컬렉션 합치기
- 반대편 컬렉션에 대응하는 원소가 없는 원소를 무시 되며, 짧은 쪽의 길이로 생성 됨

```kotlin
// 
val names = listOf("Joe", "Mary", "Jamie")
val ages = listOf(22, 31, 31, 44, 0)
names.zip(ages)
// pair
// [(Joe, 22), (Mary, 31), (Jamie, 31)]
names.zip(ages) { name, age -> Person(name, age) }
// [Person (name=Joe, age=22), Person(name=Mary, age=31), Person (name=Jamie, age=31)]

val countries = listOf("DE", "NL", "US")
names zip ages zip countries
// [(Joe, 22), DE), ((Mary, 31), NL), ((Jamie, 31), US)]
// pair 객체의 pair가 또 생긴다.
```

#### flatMap, flatten

- 자바 flatMap과 동일
- flatMap 함수는 컬렉션의 컬렉션을 평평한 리스트로 변환함

```kotlin
Book("Kotlin in Action", listOf("Isakova", "Elizarov", "Aigner", "emerov"))
Book("Atomic Kotlin", listOf("Eckel", "Isakova"))
Book("The Three-Body Problem", listOf("Liu"))

library.flatMap { it.authors }
// [Isakova, Elizarov, Aigner, Jemerov, Eckel, Isakova, Liu]

listOf(
    listOf("Isakova", "Elizarov", "Aigner", "emerov"),
    listOf("Eckel", "Isakova"),
    listOf("Liu")
).flatten()
// [Isakova, Elizarov, Aigner, Jemerov, Eckel, Isakova, Liu]

```

### 지연 계산 컬렉션 연산: 시퀀스
> 자바는 기본이 sequence 처럼 동작 함
- 컬렉션에 대한 함수형 API는 각 스텝마다 컬렉션을 계속 생성함
- map 단계: 원본 리스트와는 별도로 새 리스트 [1, 4, 9, 16, 25] 생성
- filter 단계: 위 리스트를 기반으로 다시 새 리스트 [4, 9, 16, 25] 생성
- 즉, 중간 리스트가 하나 더 생성됨 → 성능/메모리 비용이 발생

```kotlin
listOf(1, 2, 3, 4, 5)
    .map { it * it }       // ➜ [1, 4, 9, 16, 25]
    .filter { it > 2 }     // ➜ [4, 9, 16, 25]

listOf(1, 2, 3, 4, 5)
  .asSequence()
  .map { it * it }
  .filter { it > 2 }
  .toList()
```

- 컬렉션 생성을 피하고 싶다면 sequence를 사용 함
- 함수형 API를 모두 사용 가능
- 중간 리스트를 만들지 않고 최종 결과만 리스트로 생성
- `asSequence` 확장 함수를 호출하면 어떤 컬렉션이든 시퀀스로 바꿀 수 있음
- 다시 리스트로 만들때는 `toList` 사용
- 최종 연산 `toList`을 호출하면 연기됐던 모든 계산이 수행 됨
- sequence를 사용하면 각 단계마다 모두 계산하지 않고 원소를 한번에 하나씩 처리 함
- `find`와 같이 특정 조전을 하나만 찾는 경우 모두 계산되지 않기 때문에 발견되는 즉시 종료되어 성능 이점 있음 
  > API 순서에 따라 성능이 달라질 수 있어 실행 순서를 고려해가며 사용할 것!