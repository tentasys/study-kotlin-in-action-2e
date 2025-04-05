# 📚 7장 널이 될 수 있는 값

___

## 📖 7.1 NullPointerException을 피하고 값이 없는 경우 처리: 널 가능성

- 널 가능성은 NPE 오류를 피할 수 있게 돕는 코틀린 타입 시스템의 특성이다.
- 널이 될수 있는지 여부를 타입 시스템에 추가함으로써 컴파일러가 여러 가지 오류를 컴파일 시 미리 감지해서 실행 시점에
  발생할수 있는 예외 가능성을 줄일 수 있다.

## 📖 7.2 널이 될 수 있는 타입으로 널이 될 수 있는 변수 명시

- 코틀린에서는 타입 시스템이 널이 될수 있는 타입을 명시적으로 지원한다.
- 널이 될수 있는 타입은 프로그램 안의 변수에 null 을 허횽하게 만드는 방법 다시 말해 그 변수에 대해 메서드를 호출시 npe 가 발생할수 있다.
- 코틀린은 그런 메서드 호출을 금지함으로써 많은 예외를 방지한다.

```kotlin
fun strlen(s: String) = s.length // runtime 에 npe 발생하지 않음 
fun strlenNull(s: String?) = s.length // npe 오류 발생 가능 
```

- 코틀린에서 함수를 작성 할때 이함수가 null 을 인자로 받을수 있는지 작성할수 있다.
- ? 를 붙이면 null 을 받을수 있다.
- ? 가 없는 타입은 어떤 경우에도 null 을 참조 할수 없다.

```kotlin
fun strLenSafe(s: String?): Int = if (s != null) s.length else 0
```

## 📖 7.3 타입의 의미 자세히 살펴보기

- 널이 될수 있는 타입과 널이 아닌 타입을 구분하면 각 타입의 값에 대해 어떤 연산이 가능할지 명확히 이해할수 있고,
  실행 시점에 예외를 발생시킬수 있는 연산을 판단할수 있다. 따라서 그런 연산을 아예 금지시킬 수 있다.

## 📖 7.4 안전한 호출 연산자로 null 검사와 메서드 호출 합치기: ?.

- ?. 는 null 검사와 메서드 호출을 한 연산으로 수행한다.

```kotlin
str?.uppercase()
if (str != null) str.uppercase() else null
```

- 두개의 식은 같다.
- 호출하려는 값이 null 이 아니라면 메서드를 호출 하고 아니면 호출을 무시하고 null 반환

## 📖 7.5 엘비스 연산자로 null 에 대한 기본값 제공: ?:

- 엘비스 연산자 ?:
    - null 대신 사용할 기본값을 지정할 때 편리하게 사용할 수 있는 연산자를 제공

```kotlin
fun greet(name: String?) {
    val recipient: String = name ?: "unnamed"
}
```

- 첫번째 값이 null 이 아닌경우 그 값을 반환하고 null 인경우 두번째 값을 반환한다.

```kotlin
fun Person.countryName() = company?.address?.country ?: "Unknown"
```

- 코틀린에서는 retrun 이나 throw 등도 식이기 때문에 엘비스 연산자의 오른쪽에 return, throw 등을 넣을 있어 편하게 사용가능
- 함수의 전제조건을 검사하는 경우 유용하다.

## 📖 7.6 예외를 발생시키지 않고 안전하게 타입을 캐스트하기: as?

- as?
    - 연산자는 어떤 값을 지정한 타입으로 변환한다.
    - 값을 대상 타입으로 변환할수 없으면 null 을 반환한다.
    - 안전한 캐스트를 사용할 때 일반적인 패턴은 캐스트를 수행한 뒤에 엘비스 연산자를 사용

```kotlin
class Person(val firstName: String, val lastName: String) {
    override fun equals(other: Any?): Boolean {
        val otherPerson = other as? Person ?: return false // 타입 캐스트후 엘비스 연산자 사용
        return otherPerson.firstName == firstName &&
                otherPerson.lastName == lastName
    }
}
```

## 📖 7.7 널 아님 단언: !!

- not-null assertion 은 느낌표를 이중으로 사용하여 어떤 값이든 널이 아닌 타입으로 바꿀수 있다.
- 실제 null에 대해 !! 를 적용하면 NPE 가 발생한다.

```kotlin
fun ignoreNulls(str: String?) {
    val strNotNull: String = str!!
    println(strNotNull.length)
}

fun main() {
    ignoreNulls(null)
}
```

- 컴파일러에게 나는 이값이 null 이 아님을 잘 알고 있다고 알려준다.
- !! 은 발생하는 예외의 스택 트레이스에 파일의 몇 번째 줄인지에 대한 정보가 없어 한줄에 작성 금지

```kotlin
person.company!!.adress!!.country // 이방식은 금지
```

## 📖 7.8 let 함수

- let 함수를 안전한 호출 연산자와 함께 사용하면 원하는 식을 평가해서 결과가 null 인지 검사한 다음에 그 결과를 변수에 넣는 작업을
  간단한게 처리 가능

```kotlin
fun sendEmailTo(email: String) { /*...*/
}

fun main() {
    val email: String = "foo@bar.com"
    sendEmailTo(email)
    //ERROR: Type mismatch: inferred type is String? but String was expected

    // should 
    if (email != null) sendEmailTo(email)

    // better
    email?.let { email -> sendEmailTo(email) }
}
```

- null 이 될수 있는 값에 대해 한전한 호출 구문을 사용해 let 을 호출하되 null이 아닌 타입을 인자로 받는 람다를 let 에 전달
- 이렇게 하면 null 이 될수 있는 타입의 값을 null 이 될수 없는 타입의 값으로 바꿔 람다에 전달하게 된다.

## 📖 7.9 직접 초기화하지 않는 널이 아닌 타입: 지연 초기화 프로퍼티

- 코틀린에서는 일반적으로 생성자에서 모든 속성을 초기화해야 하며, 속성에 널이 아닌 유형이 있는 경우 널 이 아닌 초기화 값을 제공해야 한다.
- 해당 값을 제공할 수 없는 경우 대신 널 가능 유형을 사용해야 한다.
- 이렇게 하면 프로퍼티에 액세스할 때마다 null 검사 또는 !! 연산자가 필요하게 된다.

```kotlin
class MyService {
    fun performAction(): String = "Action Done!"
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTest {
    private lateinit var myService: MyService

    @BeforeAll
    fun setUp() {
        myService = MyService()
    }

    @Test
    fun testAction() {
        assertEquals("Action Done!", myService.performAction())
    }
}
```

- val 프로퍼티는 파이널 필드로 컴파일 되며 생성자 안에서 반드시 초기화돼야 하기 때문에 지연 초기화 프로퍼티는 항상 var 여야 한다.

## 📖 7.10 안전한 호출 연산자 없이 타입 확장: 널이 될 수 있는 타입에 대한 확장

- 널이 될수 있는 타입에 대한 확장 함수를 정의하면 null 값을 다루는 강력한 도구로 활용가능

```kotlin
fun veriftyyUserInput(input: String?) {
    if (input.isNullOrBlank()) {
        println("null")
    }
}
```

- 안전한 호출 없이도 널이 될 수 있는 수신 객체 타입에 대해 선언된 확장함수를 호출 가능하다.

## 📖 7.11 타입 파라미터의 널 가능성

- 코틀린에서 함수나 클래스의 모든 타입 파라미터는 기본적으로 null 이 될 수 있다.
- 타입 파라미터 T를 클래스나 함수 안에서 타입 이름으로 사용하면 이름끝에 물음표가 없더라도 T가 null 이 될수 있는 타입이다.

```kotlin
fun <T> printHashCode(t: T) {
    println(t?.hashCode())
}

fun main() {
    printHashCode(null)
    // null
}
```

- 타입 파라미터가 널이 아님을 확실히 하려면 널이 될 수 없는 타입 상계를 지정해야 한다.

```kotlin
fun <T : Any> printHashCode(t: T) {
    println(t.hashCode())
}

fun main() {
    printHashCode(null)
    // Error: Type parameter bound for `T` is not satisfied
    printHashCode(42)
    // 42
}
```

## 📖 7.12 널 가능성과 자바

### 🔖 7.12.1 플랫폼 타입

- 플랫폼 타입
    - 코틀린이 널 관련 정보를 알수 없는 타입을 말한다.
    - 수행하는 모든 연산에 대한 책임이 온전히 개발자에게 있다.
    - 널 안전성 검사를 중복 수행해도 아무 경고도 표시 하지 않는다.
    - 코틀린에서는 플랫폼 타입을 선언할수 없고 자바 코드에서 가져온 타입만 플랫폼 타입이 된다.
- 앞서 언급했듯이 Java 코드에는 어노테이션을 사용하여 표현되는 nullable 가능성에 대한 정보를 표현한다.
  이 정보가 코드에 있으면 Kotlin은 이를 사용한다. 따라서 Java의 @Nullable String은 Kotlin에서 String? 로 표시되고
  @NotNull String은 그냥 String으로 표시된다.

```kotlin
val s: String? = person.name // 자바 프로퍼티를 널이 될 수 있는 타입으로 볼수 있다.
val s1: String = person.name // 또는 자바 프로퍼티를 널이 될수 있는 타입으로도 볼수 있다.
```

### 🔖 7.12.2 상속

- 코틀린에서 자바 메서드를 오버라이드할 때 그 메서드의 파라미터와 반환 타입을 널이 될 수 있는 타입으로 선언할지 널이 될 수 없는
  타입으로 선언할지 결정해야 한다.

```java
interface StringProcessor {
    void process(String value);
}
```

```kotlin
class StringPrinter : StringProcessor {
    override fun process(value: String) {
        println(value)
    }
}

class NullableStringPrinter : StringProcessor {
    override fun process(value: String?) {
        if (value !=) {
            println(value)
        }
    }
}
```

- 자바 클래스나 인터페이스를 코틀린에서 구현할 경우 널 가능성을 제대로 처리하는 일이 중요하다.

