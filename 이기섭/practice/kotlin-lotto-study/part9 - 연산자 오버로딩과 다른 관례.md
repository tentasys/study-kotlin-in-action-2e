#연산자 오버로딩과 다른 관례

- 연산자 오버로딩
- 관례: 여러 연산을 지원하기 위해 특별한 이름이 붙은 메서드
- 위임 프로퍼티

## 9.1 산술 연산자를 오버로드해서 임의의 클래스에 대한 연산을 더 편리하게 만들기
- operator 키워드 -> 관례를 따르는 함수 명시

```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

fun main() {
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    println(p1 + p2)
}
```

연산자를 확장 함수로 정의하기

```kotlin
operator fun Point.plus(other: Point) : Point {
    return Point(x + other.x, y + other.y)
}
```

오버로딩 가능한 이항 산술 연산자
- a * b  >>  times
- a / b  >>  div
- a % b  >>  mod
- a + b  >>  plus
- a - b  >>  minus

``코틀린 연산자는 자동으로 교환법칙을 지원하지 않음. 교환법칙에 해당하는 반대편 함수도 구현되어있어야 한다.``

### 9.1.2 연산을 적용한 다음에 그 결과를 바로 대임 : 복합 대입 연산자 오버로딩
- plus와 같은 연산자를 오버로딩하면 코틀린은 + 연산자뿐 아니라 그와 관련 있는 연산자인 +=도 자동으로 함께 지원한다. 복합대입연산자
- plusAssign, timeAssign, minusAssign

```kotlin
fun main() {
    val list = mutableListOf(1,2)
    list += 3
    val newList = list + listOf(4,5)
    println(list)
    // [1,2,3]
    println(newList)
    // [1,2,3,4,5]
}
```
### 9.1.3 피연산자가 1개뿐인 연산자: 단항 연산자 오버로딩
- +a -> unaryPlus
- -a -> unaryMinus
- !a -> not
- ++a, a++ -> inc
- --a, a-- -> dec

증가 연산자 정의하기
```kotlin
import java.math.BigDecimal

operator fun BigDecimal.inc() = this + BigDecimal.ONE

fun main() {
    val bd = BigDecimal.ZERO
    println(bd++)
    //0 후위연산자는 실행후 증가
    println(bd)
    //1 
    println(++bd)
    //2 전위 는 증가 후 실행
}
```

## 9.2 비교 연산자를 오버로딩해서 객체들 사이의 관계를 쉽게 검사
- ==직접사용으로 가독성 향상

### 9.2.1 동등성 연산자 : equals
``
a == b -> a?.equals(b) ?: (b == null)
``

equals 메서드 구현하기
```kotlin
class Point(val x: Int, val y: Int) {
    override fun equlas(obj: Any?) : Boolean {
        if (obj === this) return true
        if (obj !is point) return false
        return obj.x == x && obj.y == y
    }
}

fun main() {
    println(Point(10, 20) == Point(10, 20))
    //true
    println(Point(10, 20) != Point(5, 5))
    //true
    println(null == Point(1, 2))
    //false
}
```

### 9.2.2 순서 연산자: compareTo (<,>,<=,>=)
- 비교연산자
- Comparable의 compareTo 호출 관례 제공
```kotlin
class Person(
    val firstName: String, val lastName : String
) : Comparable<Person> {
    override fun compareTo(other: Person) : Int {
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}

fun main() {
    val p1 = Person("Alice", "Smith")
    val p2 = Person("Bob", "Johnson")
    println(p2<p1)
    //false
}
```

## 9.3 컬렉션과 범위에 대해 쓸 수 있는 관례

### 9.3.1 인덱스로 원소 접근 :get과 set
맵 키에 접근
``val value = map[key]``
맵의 값 변경
``mutableMap[key] = newValue``

get 관례 구현하기
```kotlin
operator fun Point.get(index: Int) : Int {
    return when(index) {
        0 -> x
        1 -> y
        else ->
            throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}
fun main() {
    val p = Point(10, 20)
    println(p[1])
    //20
}
```

관례를 따르는 set 구현하기
```kotlin
data class MutablePoint(var x: Int, var y: Int)
operator fun MutablePoint.set(index: Int, value: Int) {
    when(index) {
        0 -> x = value
        1 -> y = value
        else -> 
            throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

fun main() {
    val p = MutablePoint(10, 20)
    p[1] = 42
    println(p)
}
```

### 9.3.2 어떤 객체가 컬렉션에 들어있는지 검사: in 관례
- 대응 함수 contains

```kotlin
data class Rectangle(val upperLeft: Point, val lowerRight: Point)
operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x..<lowerRigth.x && p.y in upperLeft.y..>lowerRight.y
}

fun main() {
    val rect = Rectabnle(Point(10, 20), Point(50, 50))
    println(Point(20, 30) in rect)
    //true
    println(Point(5,5) in rect)
    //false
}
```

### 9.3.3 객체로부터 범위 만들기: rangtTo와 ragneUntil 관례
- start .. end -> start.rangeTo(end)
```kotlin
fun main() {
    val n = 9
    println(0..(n+1))
    // 0..10
}
```

```kotlin
fun main() {
    val n = 9
    (0..n).forEach {print(it)}
    // 0123456789
}
```
- rangeUntil 은 미만 < 

### 9.3.4 자신의 타입에 대해 루프 수행: iterator 관례

## 9.4 component 함수를 사용해 구조 분해 선언 제공
구조분해 선언을 사용해 여러 값 반환하기
```kotlin
data class NameComponents(val name: String, val extension: String)
fun splitFilename(fullName: String): NameComponents {
    val result = fullName.split('.', limit = 2)
    return NameComponents(result[0], result[1])
}
fun main() {
    val (name, ext) = splitFilename("example.kt")
    println(name)
    //example
    println(ext)
    //kt
}
```

### 9.4.1 구조 분해 선언과 루프
구조분해 선언을 사용해 맵 이터레이션 하기
```kotlin
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}
fun main() {
    val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlen")
    printEntires(map)
    // Oracle -> Java
    //JetBrains -> Kotlen
}
```

### 9.4.2 _ 문자를 사용해 구조 분해 값 무시

```kotlin
data class Person(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val city: String
)
fun introducePerson(p: Person) {
    val (firstName, lastName, age, city) = p
    println("This is $firstName, aged $age.")
    
    val (firstName, _ , age) = p  //로 구조분해 가능 
}
```

## 9.5 프로퍼티 접근자 로직 재활용: 위임 프로퍼티 (delegated property)
- var p: Type by Delegate() -> 접근자 로직을 다른 객체에 위임
- 프로퍼티 위임 관례에 따라 Delegate 클래스는 getValue와 setValue 제공

### 9.5.2 위임 프로퍼티 사용: by lazy()를 사용한 지연 초기화
```kotlin
class Person(val name: String) {
    private var _emails: List<Email>? = null
    val emails: List<Email>
    get() {
        if(_emails == null) {
            _emails = loadEmails(this)
        }
        return _emails!!
    }
}

fun main() {
    val p = Person("Alice")
    p.emails
    // Load emails for Alice
    p.emails
}
```

위임 프로퍼티를 통해 지연 초기화 구현
```kotlin
class Person(val name: String) {
    val emails by lazy {loadEmails(this)}
}
```
 
프로퍼티 변경 통지를 직접 구현하기

```kotlin
class Person(val name: String, age: Int, salary: Int) : Observable() {
    var age: Int = age
    set(newValue) {
        val oldValue = field
        field = newValue
        notifyObservers(
            "age", oldValue, newValue
        )
    }
    var salary: Int = salary
    set(newValue) {
        val oldValue= field
        field = newValue
        notifyObservers(
            "salary", oldValue, newValue
        )
    }
}

fun main() {
    val p = Person("Seb", 28, 1000)
    p.observers += Observer { propName, oldValue, newValue ->
        println(
            """
            Property $propName changed from $oldValue to $newValue!
            """.trimIndent()
        )
    }
}
```

도우미 클래스를 통해 프로퍼티 변경 통지 구현하기
```kotlin
class ObservableProperty(
    val propName: String,
    var propValue: Int,
    val observable: Observable
) {
    fun getValue(): Int = propValue
    fun setValue(newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        observable.notifyObservers(propName, oldValue, newValue)
    }
}

class Person(val name: String, age: Int, salary: Int) : Observable() {
    val _age = ObservableProperty("age", age, this)
    var age: Int
        get() = _age.getValue()
        set(newValue) {
            _age.setValue(newValue)
        }
    val _salary: Int
        get() = _salary.getValue()
        set(newValue) {
            _salary.setValue(newValue)
        }
}
```

프로퍼티 위임 객체인 ObservableProperty
```kotlin
import kotlin.reflect.KProperty

class ObservableProperty(var propValue: Int, val servervable: Observable) {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>) : Int = propValue
    operator fun setValue(thisRef: Any?, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        observable.notifyObservers(prop.name, oldValue, newValue)
    }
}
```

위임 프로퍼티를 통해 프로퍼티 변경 통지 받기
```kotlin
class Person(val name: String, age: Int, salary: Int) : Observable() {
    var age by ObservableProperty(age, this)
    var salary by ObservableProperty(salary, this)
}
```

Delegates.observable을 사용해 프로퍼티 변경 통지 구현하기
```kotlin
import kotlin.properties.Delegates

class person(val name: String, age: Int, salary: Int) : Obervarble() {
    private val onChange = { property: KProperty<*>, oldValue: Any?,
        newValue: Any? -> 
            notifyObservers(property.name, oldValue, newValue)
    }
    
    var age by Deletages.observable(age, onChange)
    var salary by Delegates.observable(salary, onChange)
}
```

### 9.5.3 위임 프로퍼티는 커스텀 접근자가 있는 감춰진 프로퍼티로 변환될다.
- val x = c.prop -> val x = <delegate>.getValue(c, <property>)
- c.prop = x -> <delegate>.setValue(c, <property>, x)

### 9.5.5 맵에 위임해서 동적으로 애트리뷰트 접근
값을 맵에 저장하는 프로퍼티 정의하기
```kotlin
class Person {
    private val _attributes = mutableMapOf<String, String>()
    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }
    var name: String
        get() = _attributes["name"]!!
        set(value) {
            _attributes["name"] = value
        }
}

fun main() {
    val p =Person()
    val data = mapOpf("name" to "Seb", "company" to "JetBrains")
    for ((attrName, value) in data)
        p.setAttribute(attrName, value)
    println(p.name)
    // Seb
    p.name = "Sebastian"
    println(p.name)
    // Sebastian
}
```

### 9.5.6 실전 프레임워크가 위임 프로퍼티를 활용하는 방법
위임 프로퍼티를 사용해 데이터베이스 컬럼 접근하기
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
