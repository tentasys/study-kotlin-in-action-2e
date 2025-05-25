# 8장 기본 타입, 컬렉션, 배열

## 8.1 원시 타입과 기본 타입

- kotlin &rarr; 원시 타입, 래퍼 타입 구분 x

### 8.1.1 정수, 부동소수점 수, 문자, 불리언 값을 원시 타입으로 표현

- kotlin &rarr; 항상 래퍼 타입은 x
  - 실행 시점에 평가하여 표현
  - 일반적으론 kotlin Int가 java int 타입으로 컴파일
  - 컴파일이 불가능한 경우 java.lang.Integer 객체로 컴파일

- 정수 타입 : Byte, Short, Int, Long
- 부동소수점 숫자 타입: Float, Double
- 문자 타입: Char
- 불리언 타입: Boolean

### 8.1.2 양수를 표현하기 위해 모든 비트 범위 사용: 부호 없는 숫자 타입

- UByte
  - 8비트
  - 0 ~ 255
- UShort
  - 16비트
  - 0 ~ 65535
- UInt
  - 32비트
  - 0 ~ 2^32 - 1
- ULong
  - 64비트
  - 0 ~ 2^64 - 1

- 부호 없는 숫자 타입은 상응하는 부호 있는 타입의 범위를 shift &rarr; 같은 크기 메모리로 더 큰 양수 범위 표현

### 8.1.3 널이 될 수 있는 기본 타입: Int?, Boolean? 등

- null이 될 수 있는 코틀린 타입 &rarr; 자바 원시 타입으로 표현 불가, 래퍼 타입으로 표현

```kotlin
data class Person(val name: String,
    val age: Int? = null) {
    fun isOrderThan(other: Person): Boolean? {
        if (age == null || other.age == null) {
            return null
        }
      return age > other.age
    }
}
```

- null 가능성이 있는 필드끼리 비교 불가
- `Person.age` &rarr; java.lang.Integer로 저장됨

### 8.1.4 수 변환

> 코틀린에선 특정 타입의 수를 다른 타입의 수로 자동 변환 x
> ex) int &rarr; long 자동 변환 불가능

```kotlin
val i = 1
val l: Long = i // Error: type mismatch compile error

val l: Long = i.toLong() // 직접 호출 필요
```

### 8.1.5 Any와 Any: 코틀린 타입 계층의 뿌리

> 코틀린에선 Any 타입이 모든 널이 될 수 없는 타입의 조상 타입

- data class &rarr; toString, equals, hashcode 메서드 제공 &rarr; Any 내 정의된 메서드 상속

### 8.1.6 Unit 타입: 코틀린의 void

> kotlin in `Unit` = java in `void`

- unit 함수 : 반환하지 않는 함수의 반환 타입 = 반환 타입 선언 없이 정의한 함수

```kotlin

fun process() {
    
    // return any 
}
```

- 함수 내 특정 return 없이 함수를 종료하면 컴파일러가 return Unit 임시로 넣음
- `Unit`: 단 하나의 인스턴스만 갖는 타입

### 8.1.7 Nothing 타입: 이 함수는 결코 반환되지 않는다

> `Nothing 타입`: 함수의 반환 타입이나 반환 타입으로 쓰일 타입 파라미터로만 사용 가능

```kotlin
val address = company.address ?: fail("No address")
println(address.city)
```

- 컴파일러가 address null이 아님을 추론 가능

## 8.2 컬렉션과 배열

### 8.2.1 널이 될 수 있는 값의 컬렉션과 널이 될 수 있는 컬렉션

- List<Int?> &rarr; element가 int or null
- List<Int>? &rarr; list가 List<Int> or null

```kotlin
val filteredNumbers = numbers.filterNotNull()
```

- `filterNotNull()` : null이 아닌 원소 반환

### 8.2.2 읽기 전용과 변경 가능한 컬렉션

- 읽기 전용 컬렉션이 항상 thread safe 하진 않음
  - 가변 컬렉션의 참조 객체로 view 역할만 할 수도 있음
  - 다른 thread에서 가변 컬렉션 변경 가능
- `mutableList`, `immutableList` 를 제공하긴 함

### 8.2.3 코틀린 컬렉션과 자바 컬렉션은 밀접히 연관됨

- List
  - 읽기 전용 타입: listof, List
  - 변경 가능 타입: mutableListOf, MutableList, arrayListOf, buildList
- Set
  - 읽기 전용 타입: setOf
  - 변경 가능 타입: mutableSetOf, hashSetOf, linkedSetOf, sortedSetOf, buildSet
- Map
  - 읽기 전용 타입: mapOf
  - 변경 가능 타입: mutableMapOf, hashMapOf, linkedMapOf, sortedMapOf, buildMap

### 8.2.4 자바에서 선언한 컬렉션은 코틀린에서 플랫폼 타입으로 보임

- 자바에서 정의한 타입 &rarr; 코틀린에선 플랫폼 타입으로 인식
- 플랫폼 타입 &rarr; 널이 될 수 없는 타입, 될 수 있는 타입 둘다 허용
- 플랫폼 타입 &rarr; 변경 가능성도 허용

### 8.2.5 성능과 상호운용을 위해 객체의 배열이나 원시 타입의 배열을 만들기

> kotlin 에선 원시 타입 배열 제공 ex) IntArray, ByteArray, CharArray, BooleanArray<br>
> int[], byte[], char[] 등으로 컴파일 &rarr; boxing x

- `arrayOf`: 인자를 받은 원소들을 포함하는 배열 생성
- `arrayOfNulls`: 모든 원소가 null인 정해진 크기의 배열 생성 가능
- `Array 생성자`: 배열 크기와 람다를 인자로 받아 람다를 호출해 배열 원소 초기화