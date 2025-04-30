# 9장 연산자 오버로딩과 다른 관례

## 9.1 산술 연산자를 오버로드해서 임의의 클래스에 대한 연산을 더 편리하게 만들기

### 9.1.1 plus, times, divide 등: 이항 산술 연산 오버로딩

```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

fun main() {
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    
    println(p1 + p2) // Point(40, 60)
}
```

- 연산자 `operator` 관례 적용 &rarr; `a + b` = `a.plus(b)`
- `a * b`: times
- `a / b`: div
- `a % b`: mod
- `a + b`: plus
- `a - b`: minus
- 연산자 우선순위는 수식과 동일
  - *, /, % 모두 우선순위 동일, 세 연산자 우선순위 +, - 보다 높음

### 9.1.2 연산을 적용한 다음에 그 결과를 바로 대입: 복합 대입 연산자 오버로딩

> 복합 대입 연산자란, +=, -=

```kotlin
fun main() {
    var point = Point(1, 2)
    point += Point(3, 4)
    println(point) // Point(4, 6)
}
```

- collection에도 사용 가능 

```kotlin

fun main() {
    val numbers = mutableListOf<Int>()
    numbers += 42
    println(numbers[0]) // 42
}

operator fun <T> MutableCollection<T>.plusAssign(element: T) {
    this.add(element)
}
```

- a += b
  - += 연산자 &rarr; plus, plussAsign 함수 호출로 번역 가능
  - a = a.plus(b)
  - a.plusAssign(b)

<br>

- `+, -` &rarr; 항상 새로운 컬렉션 반환
- `+=, -=` &rarr; 항상 변경 가능한 컬렉션에 작용하여 메모리에 있는 객체 상태 변화
  - 읽기 전용 컬렉션에 +=, -= 적용하면 변경 적용한 복사본 반환

### 9.1.3 피연산자가 1개뿐인 연산자: 단항 연산자 오버로딩

```kotlin
operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
}

fun main() {
    val p = Point(10, 20)
    println(-p) // Point(-10, -20)
}
```

#### 오버로딩 할 수 있는 단항 산술 연산자

- `+a`: unaryPlus
- `-a`: unaryMinus
- `!a`: not
- `++a, a++`: inc
- `--a, a--`: dec

## 9.2 비교 연산자를 오버로딩해서 객체들 사이의 관계를 쉽게 검사

### 9.2.1 동등성 연산자: equals

> 코틀린에선 == 연산자 호출이 equals 메서드 호출로 컴파일<br>
> `a == b` &rarr; `a?.equals(b) ?: (b == null)` 

- equals 메서드에 operator 지정 불필요
  - Any 메서드엔 operator 붙어 있음
  - 하위 클래스에서 상위 클래스 오버라이드 하면 자동으로 상위 클래스의 operator 적용
- Any에서 상속 받은 equals 우선순위가 높아 확장 함수 불가능

### 9.2.2 순서 연산자: compareTo, (<, >, <=, >=)

> `a >= b` &rarr; `a.compareTo(b) >= 0`

- equals 연산자와 흐름 동일
- Comparable 의 compareTo 에도 operator 적용되어 있음
- 하위 클래스에서 오버로드 시 자동 operator 적용

## 9.3 컬렉션과 범위에 대해 쓸 수 있는 관례

### 9.3.1 인덱스로 원소 접근: get과 set

> 각 괄호를 사용한 접근은 get 함수 호출로 변경

```kotlin
operator fun Point.get(index: Int): Int {
    return when(index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("invalid index $index")
    }
}

fun main() {
    val p = Point(10, 20)
    println(p[1]) // 20
}
```

### 9.3.2 어떤 객체가 컬렉션에 들어있는지 검사: in 관례

> `in` &rarr; 객체가 컬렉션에 들어있는지 검사, contains에 대응됨<br>
> `a in c` &rarr; `c.contains(a)`

```kotlin
data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x..<lowerRight.x &&
            p.y in upperLeft.y..<lowerRight.y
}

fun main() {
    val rect = Rectangle(Point(10, 20), Point(50, 50))
    
    println(Point(20, 30) in rect) // true

    println(Point(5, 5) in rect) // false
}
```

### 9.3.3 객체로부터 범위 만들기: rangeTo와 rangeUntil의 관례

- `..` 연산자:  rangeTo 함수를 간략하게 표현
- rangeTo 함수 &rarr; 범위 반환


```kotlin
val now = LocalDate.now()
val vacation = now..now.plusDays(10) // now.rangeTo(now.plusDays(10))와 동일
println(now.plusWeeks(1) in vacation) // true
```

### 9.3.4 자신의 타입에 대해 루프 수행: iterator 관례

- `for (x in list) { … }` = `list.iterator()`
- iterator 확장 함수 정의 가능

```kotlin
operator fun ClosedRange<LocalDate>.iterator() : Iterator<LocalDate> =
object : Iterator<LocalDate> {
var current = start

        override fun hasNext() = current <= endInclusive

        override fun next() = current.apply {
            current = plusDays(1)
        }
    }

fun main() {
val newYear = LocalDate.ofYearDay(2042, 1)
val daysoff = newYear.minusDays(1)..newYear
for(dayoff in daysoff) { println(dayoff) } // 2041-12-31, 2042-01-01
}
```

## 9.4 component 함수를 사용해 구조 분해 선언 제공

- 여러 다른 변수 한번에 초기화 가능

```kotlin
fun main() {
val p = Point(1, 2)
val (x, y) = p
}
```

### 9.4.1 구조 분해 선언과 루프

- for 선언문 안에서 구조 분해 사용 가능

```kotlin
fun printEntries(map: Map<String, String>) {
    for((key, value) in map) {
        println("$key -> $value")
    }
}
```

### 9.4.2 _ 문자를 사용해 구조 분해 값 무시

- 구조 분해 식 내 `_` 키워드 사용하면 미할당

```kotlin
fun introducePerson(p: Person) {
    val (firstName, _, age) = p
    println("this is $fisrtName, aged $age.")
}
```

## 9.5 프로퍼티 접근자 로직 재활용: 위임 프로퍼티

> 위임이란, 객체가 직접 수행하지 않고 다른 도우미 객체가 처리하도록 맡기는 디자인 패턴

### 9.5.1 위임 프로퍼티의 기본 문법과 내부 동작

```kotlin
var p: Type by Delegate()

class Foo {
    var p: Type by Delegate()
}

class Foo {
    private val delegate = Delegate() // 컴파일러가 생성한 도우미 프로퍼티
  
  var p: Type
    set(value: Type) = delegate.setValue(/* ... */, value) // p 프로퍼티를 위해 컴파일러가 생성한 접근자 getValue 메서드 호출
    get() = delegate.getValue( /* ... */)
}
```

### 9.5.2 위임 프로퍼티 사용: by lazy()를 사용한 지연 초기화

> lazy 함수 : 코틀린 관례에 맞는 getValue 메서드 들어있는 객체 반환

```kotlin
class Person(val name: String) {
    private var _emails: List<String>? = null
    val emails: List<String>
        get() {
            if(_emails == null) {
                _emails = loadEmails()
            }
            return _emails!!
        }
}
```

- 지연 초기화 미사용 x
- thread safe x

```kotlin
class Person(val name: String) {
    val emails by lazy { loadEmails() }
}
```

- 지연 초기화 
- 한번만 초기화되는 점 보장

### 9.5.4 위임 프로퍼티는 커스텀 접근자가 있는 감춰진 프로퍼티로 변환된다

```kotlin
// 컴파일 전
class C {
		var prop: Type by MyDelegate()
}

// 컴파일 후
class C {
		private val <delegate> = Mydelegate()
		var prop: Type
			get() = <delegate>.getValue(this, <property>)
			set(value: Type) = <delegate>.setValue(this, <property>, value)
}
```

- 위임 프로퍼티 &rarr; 컴파일러가 모든 프로퍼티 접근자 내 getValue, setValue 호출 코드 생성

### 9.5.5 맵에 위임해서 동적으로 애트리뷰트 접근

```kotlin
// 위임 직접 구현
class Person {
    private val _attribute = hashMapOf<String, String>()
    fun setAttribute(attrName: String, value: String) {
        _attribute[attrName] = value
    }
    
    val name: String
     get() = _attribute["name"]!!
}

// map에 구현되어 있는 getValue, setValue에게 위임
class Person {
    private val _attribute = hashMapOf<String, String>()
    fun setAttribute(attrName: String, value: String) {
        _attribute[attrName] = value
    }

    val name: String by _attribute
}
```

- 자신의 프로퍼티를 동적으로 정의할 수 있는 객체를 만들 때 위임 프로퍼티를 활용

### 9.5.6 실전 프레임워크가 위임 프로퍼티를 활용하는 방법

```kotlin
object Users : IdTable() {
    val name = varchar("name", length = 50).index()
    val age = integer("age")
}

class User(id: EntityID) : Entity(id) {
    var name: String by Users.name
    var age: Int by Users.age
}
```

- 위임 프로퍼티 &rarr; db entity field 직접 접근 가능