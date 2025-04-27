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

// todo: 4/28 마무리 예정,,,