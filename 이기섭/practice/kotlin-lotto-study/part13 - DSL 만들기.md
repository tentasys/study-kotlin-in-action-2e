#13장 DSL 만들기

13장에서 다루는 내용
- 도메인 특화 언어 만들기
- 수신 객체 지정 람다 사용
- invoke 관례 사용
- 기존 코틀린 DSL 예제

## 13.1 API에서 DSL로: 표현력이 좋은 커스텀 코드 구조 만들기
API가 깔끔하다는 말의 의미
- 코드를 읽는 독자가 어떤 일이 벌어질지 명확하게 이해할 수 있어야 한다. 이름과 개념을 잘 선택하면 이런 목적을 달성할 수 있다.
- 코드에 불필요한 구문이나 번잡한 준비 코드가 가능한 한 적어야 한다. 이번 장에서 주로 초점을 맞추는 것도 그런 간결함이다.


코틀린의 간결한 구문을 지원하는 방법
- 확장함수 : StringUtil.capitalize(s) > s.capitalize()   
- 중위 호출 : 1.to("one") > 1 to "one"
- 연산자 오버로딩 : set.add(2) > set += 2
- get 메서드에 대한 관례 : map.get("key") > map["key"]
- 람다를 괄호 밖으로 빼내는 관례 : file.use({f -> f.read()}) > file.use {it.read()}
- 수신 객체 지정 람다 : sb.append("yes") > with(sb) { add("yes")}
- 람다를 받는 빌더 함수 : val m = mutableListOf<Int>() m.add(1) m.add(2) return m.toList() > return buildList{add(1) add(2) }

### 13.1.2 내부 DSL은 프로그램의 나머지 부분과 매끄럽게 통합된다.

SQL 구문
```sql
SELECT Country.name, COUNT(Customer.id)
FROM Country
INNER JOIN Customer
    ON Country.id = Customer.country_id
GROUP BY COUNT(Customer.id) DESC LIMIT 1
```
코틀린 익스포즈드
```kotlin
(Country innerJoin Customer)
    .slice(Country.name, Count(Customer.id))
    .selectAll()
    .groupBy(Country.name)
    .orderBy(Count(Customer.id), order = SortOder.DESC)
    .limit(1)
```

### 13.1.3 DSL 의 구조

### 13.1.4 내부 DSL로 HTML 만들기

## 13.2 구조화된 API 구축: DSL에서 수신 객체 지정 람다 사용
### 13.2.1 수신 객체 지정 람다와 확장 함수 타입

람다를 인자로 받는 buildString() 정의하기
```kotlin
fun buildString(
    builderAction: (StringBuilder) -> Unit
): String {
    val sb = StringBuilder()
    builderAction(sb)
    return sb.toString()
}

fun main() {
    val s = buildString {
        it.append("Hello, ")
        it.append("World!")
    }
    println(s)
}
```

수신 객체 지정 람다를 파라미터로 받는 buildString()
```kotlin
fun buildString(
    builderAction: StringBuilder.() -> Unit
): String {
    val sb = StringBuilder()
    sb.builderAction()
    return sb.toString()
}

fun main() {
    val s = buildString {
        this.append("Hello,")
        append("World!")
    }
    println(s)
}
```

수신 객체 지정 람다를 변수에 저장하기
```kotlin
val appendExcl: StringBuilder.() -> Unit = {this.append("!")}

fun main() {
    val stringBuilder = StringBuilder("Hi")
    stringBuilder.appendExcl()
    println(stringBuilder)
    
    println(buildString(appendExcl))
}
```

### 13.2.2 수신 객체 지정 람다를 HTML빌더 안에서 사용
코틀린 HTML 빌더를 사용해 간단한 HTML 표 만들기
```kotlin
fun createSimpleTable() = createHTML().
table {
    tr {
        td{+"cell"}
    }
}
```

HTML 빌더를 위한 태그 클래스 정의
```kotlin
open class Tag

class TABLE : Tag {
    fun tr(init : TR.() -> Unit)
}

class TR : Tag {
    fun td(init : TD.() -> Unit)
}
```

HTML 빌더 호출의 수신 객체를 명시한 코드
```kotlin
fun createSimpleTable() = createHTML().table {
    this@table.tr {
        (this@tr).td {
            +"cell"
        }
    }
}
```

영역 안에 여러 수신 객체가 있으면 혼동이 올 수 있음
```kotlin
createHTML().body {
    a {
        img {
            href = "https://..."
        }
    }
}
```

HTML을 문자열로 만들기
```kotlin
fun createTable() =
    table {
        tr {
            td {
                
            }
        }
    }
fun main() {
    println(createTable())
}
```

태그 빌더 함수 정의하기
```kotlin
fun tr(init: TR.() -> Unit) {
    val tr = TR()
    tr.init()
    children.add(tr)
}
```

간단한 HTML 빌더의 전체 구현
```kotlin
@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
open class Tag(val name: String) {
    private val children = mutableListOf<Tag>()
    
    protected fun <T : Tag> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children.add(child)
    }
    
    override fun toString() =
        "<$name>${children.joinToString("")}</$name>"
}

fun table(init: TABLE.() -> Unit) = TABLE().apply(init)

class TABLE: Tag("table") {
    fun tr(init: TR.() -> Unit) = doInit(TR(), init)
}

class TR: Tag("tr") {
    fun td(init: TD.() -> Unit) = doInit(TD(), init)
}
class TD : Tag("td")

fun createTable() = 
    table {
        tr {
            td {
                
            }
        }
    }

fun main() {
    println(createTable())
}

```

### 13.2.3 코틀린 빌더: 추상화와 재사용을 가능하게 해준다
도우미 함수를 사용해 목차가 있는 페이지 만들기
```kotlin
fun buildBookList() = createHTML().body {
    listWithToc {
        item("The Three-Body Problem", "The first book tackles...")
        item("The Dark Forest", "The second book starts with...")
        item("Death's End", "The third book contains...")
    }
}
```

## 13.3 invoke 관례를 사용해 더 유옇나게 블록 내포시키기

클래스 안에서 invoke 메서드 정의하기
```kotlin
class Greater(val greeting: String) {
    operator fun invoke(name: String) {
        println("$greeting, $name!")
    }
}

fun main() {
    val bavarianGreeeter = Greeter("Servus")
    bavarianGreeter("Dmitry")
}
```

### 13.2.2 DSL의 invoke 관례: 그레이들 의존관계 선언
유연한 DSL 문법을 제공하기 위해 invoke 사용하기
```kotlin
class DependencyHandler {
    fun implementation(coordinate: String) {
        println("Added dependency on $coordinate")
    }
    
    operator fun invoke(
        body: DependencyHandler.() -> Unit) {
        body()
    }
}

fun main() {
    val dependencies = DependencyHandler()
    dependencies.implementation("org.jetbrains.kotlinx")
    dependencies {
        implementation("org.jetbrains.kotlinx")
    }
}

```

## 13.4 실전 코틀린 DSL
### 13.4.1 중위 호출 연쇄시키기: 테스트 프레임워크의 should 함수

코테스트로 DSL 단언문 표현하기
```kotlin
import io.kotest.matchers.should
import io.kotest.matchers.string.startWith
import org.junit.jupiter.api.Test

class PrefixTest {
    @Test
    fun testPrefix() {
        val s = "kotlin".uppercase()
        s shuold startWih("K")
    }
}

```

should 함수 사용하기
```kotlin
infix fun <T> T.should(matcher:Matcher<T>) = matcher.test(this)
```

코테스트 DSL에 사용하기 위한 Matcher 선언하기
```kotlin
interface Matcher<T> {
    fun test(value: T)
}
fun startWith(prefix:String): Matcher<String> {
    return object: Matcher<String> {
        override fun test(value: String) {
            if(!value.startsWith(prefix)) {
                throw AssertionError("$value does not start with $prefix")
            }
        }
    }
}
```

### 13.4.2 원시 타입에 대해 확장 함수 정의하기: 날짜 처리
날짜 조작 DSL 정의하기
```kotlin
import kotlin.time.DurationUnit

val Int.days: Durationget 
    get() = this.toDuration(DurationUnit.DAYS)

val Int.hours: Duration
    get() = this.toDuration(DurationUnit.HOURS)
```

### 13.4.3 멤버 확장 함수: SQL을 위한 내부 DSL
익스포즈드에서 테이블 선언하기
```kotlin
object Country : Table() {
    val id = integer("id").autoIncrement()
    val name = varcha("name", 50)
    override val primaryKey = PrimaryKey(id)
}

class Table {
    fun integer(name: String): Column<Int>
    fun varchar(name: String, length: Int): Column<String>
    //...
}
```

익스포즈드에서 두 테이블 조인하기
```kotlin
val result = (Country innerJoin Customer)
    .select { Country.name eq "USA"}
result.forEach { println(it[Customer.name])}
```