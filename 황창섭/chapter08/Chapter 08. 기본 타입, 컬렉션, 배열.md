# Chapter 08. 기본 타입, 컬렉션, 배열

- 원시 타입과 다른 기본 타입 및 자바 타입과의관계
- 코틀린 컬렉션과 배열 및 이들의 널 가능성과 상호운용성

## 8.1 원시 타입과 기본 타입

- 코틀린은 원시 타입과 래퍼 타입을 구분하지 않음

### 8.1.1 정수, 부동소수점 수 , 문자, 불리언 값을 원시 타입으로 표현

- 코틀린은 실행 시점에 가능한 한 가장 효율적인 방식으로 표현된다.
- Int 라 작성했을 때 int 또는 Integer 모두 될 수 있다.
- 코틀린의 타입
  - 정수: Byte, Short, Int, Long
  - 부동소수점 숫자: Float, Double
  - 문자: Char
  - 불리언: Boolean

### 8.1.2 양수를 표현하기 위해 모든 비트 범위 사용: 부호 없는 숫자 타입

- 부호 없는 타입
  - UByte: 0 ~ 255
  - UShort: 0 ~ 65535
  - Uint: 0 ~ 2^32 - 1
  - ULong: 0 ~ 2^64 - 1
  
### 8.1.3 널이 될 수 있는 기본 타입: Int?, Boolean? 등

- 널이 될 수 있는 타입은 당연하게 자바 래퍼 타입으로 컴파일 됨

### 8.1.4 수 변환

- 코틀린과 자바의 가장 큰 차이점은 수를 변환하는 방식
- 코틀린은 한 타입의 수를 다른 타입의 수로 자동 변환하지 않음

```kotlin
val i = 1
val l: Long = i // type mismatch 오류 발생
```

```kotlin
val i = 1
val l: Long = i.toLong() // 직접 변환을 해야함.
```

### 8.1.5 Any와 Any?: 코틀린 타입 계층의 뿌리

- 코틀린에서는 Any 타입이 모든 널이 될 수 없는 타입의 조상 타입
- 내부에서 Any 타입은 java.lang.Object 에 대응함

### 8.1.6 Unit 타입: 코틀린의 void

- 코틀린 Unit 타입은 자바 void 와 같은 기능을 함
- 문법적으로 아래 두 함수는 동일

```kotlin
fun f(): Unit {/**/}

fun f(): {/**/}
```

- Unit이 void와 다르게 좋은 점은 제네릭 파라미터를 반환하는 함수를 오버라이드 할 때

```kotlin
interface Processor<T> {
    fun process(): T
}

class NoReulstProcessor: Processor<Unit> {
    override fun process() {
        
    }
}
```
- 자바에서 Void를 사용한다 할지라도 return null 을 해줘야함

### 8.1.7 Nothing 타입: 이 함수는 결코 반환되지 않는다

- Nothing: 인스턴스를 절대로 가질 수 없는 타입

```kotlin
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

fun main() {
    fail("Error occurred")
}
```

- 사용 예
  - 항상 예외를 던지거나(throw) 무한 루프에 빠지는 함수
  - 컨트롤 흐름 상 불가능한 분기 표현
  - 표현력 강화 & 컴파일러 최적화 힌트

## 8.2 컬렉션과 배열

### 8.2.1 널이 될 수 있는 값의 컬렉션과 널이 될 수 있는 컬렉션

```kotlin
/**
 * 널이 될 수 있는 값으로 이뤄진 컬렉션 만들기
 */
fun readNumbers(text: String): List<Int?> {
    val result = mutableListOf<Int?>()
    for(line in text.lineSequence()) {
        val numberOrNull = line.toIntOrNull()
        result.add(numberOrNull)
    }
    
    return result
}
```

### 8.2.2 읽기 전용과 변경 가능한 컬렉션

- 코틀린에서는 컬렉션 안의 데이터에 접근하는 인터페이스와 변경하는 인터페이스를 분리함
- Collection: 컬렉션 안 데이터 조회, 이터레이션, size 등
- kotlin.collections.MutableCollection: 컬렉션 데이터 수정 인터페이스

```kotlin
/**
 * 읽기 전용과 변경 가능한 컬렉션 인터페이스
 */
fun <T> copyElements(source: Collection<T>,
                     tartget: MutableCollection<T>) {
    for(item in source) {
        target.add(item)
    }
}
```

### 8.2.3 코트린 컬렉션과 자바 컬렉션은 밀접히 연관됨

- 모든 코틀린 컬렉션은 그에 상응하는 자바 컬렉션 인터페이스의 인스턴스
- 코틀리과 자바 사이를 오갈때 변환이 필요없음
- 코틀린은 모든 자바 컬렉션 인터페이스마다 읽기 전용, 변경 가능 인터페이스라는 2가지 표현을 제공함

### 8.2.4 자바에서 선언한 컬렉션은 코틀린에서 플랫폼 타입으로 보임

- 자바 코드에서 선언한 컬렉션 타입의 변수를 코틀린에서는 플랫폼 타입으로 봄 = 변경 가능성에 대해 알 수 없음
- 그렇기에 읽기 전용인지 변경 가능성이 있는지 널 가능성이 있는지 맥락을 파악해야함.
- 자바에서 동일한 List<String> 타입이더라도 코틀린에서는 맥락에 따라 List<String>? MutableList<String?> 이 될 수 있다

### 8.2.5 성능과 상호운용을 위해 객체의 배열이나 원시 타입의 배열을 만들기

- 코틀린에서 배열을 만드는 방법
  - arrayOf 함수는 인자로 받은 원소들을 포함하는 배열을 만듬
  - arrayOfNulls 함수는 모든 원소가 null인 정해진 크기의 배열을 만들 수 있다.
  - Array 생성자는 배열 크기와 람다를 인자로 받아 원소 초기화를 할 수 있음

- 코틀린에서 Array<Int> 와 같은 타입은 무조건 박싱된 타입이다.
- 원시 타입 배열은 IntArray, ByteArray, CharArray, BooleanArray와 같이 별도 클래스가 존재