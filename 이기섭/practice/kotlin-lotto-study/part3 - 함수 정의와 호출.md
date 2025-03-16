#3장 함수 정의와 호출

- 컬렉션, 문자열, 정규 식을 다루기 위한 함수
- 이름 붙인 인자, 디폴트 파라미터 값, 중위 호출 문법 사용
- 확장 함수와 확장 프로퍼티를 사용해 자바 라이브러리를 코틀린에 맞게 통합
- 최상위 및 로컬 함수와 프로퍼티를 사용해 코드 구조화
---
##3.1 코틀린에서 컬렉션 만들기

```kotlin
val set = setOf(1, 7, 53)
val list = listOf(1, 7, 53)
val map = listOf(1 to "one", 7 to "seven", 53 to "fifty-three") //to 는 키워드가 아니라 일반함수.
```
책의 오타가 있네요. listOf -> mapOf

```kotlin
fun main(){
    val set = setOf(1, 7, 53)
    val list = listOf(1, 7, 53)
    val map = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

    println(set.javaClass)
    //class java.util.LinkedHashSet
    println(list.javaClass)
    //class java.util.Arrays$ArrayList
    println(map.javaClass)
    //class java.util.LinkedHashMap
}
```
- javaClass -> 자바의 getClass()
- 코틀린은 표준 자바 컬렉션 클래스를 사용
- 코틀린 컬렉션 인터페이스는 디폴트가 읽기 전용

```kotlin
fun main(){
    val strings = listOf("일", "이", "쉽사")
    
    println(strings.last()) 
    // 쉽사
    
    println(strings.shuffled())
    //[쉽사, 이, 일]
    
    val numbers = setOf(1, 14, 2)
    println(numbers.sum())
}
```
- last() 마지막 원소
```kotlin 
public val <T> List<T>.lastIndex: Int
get() = this.size - 1
```
- shuffled() 섞어
```kotlin
//java Collections 의  shuffle 호출
public actual inline fun <T> MutableList<T>.shuffle() {
    java.util.Collections.shuffle(this)
}
```
---
##3.2 함수를 호출하기 쉽게 만들기 

- joinToString
```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
) : String {

    val result = StringBuilder(prefix)

    for((index, element) in collection.withIndex()) {
        if (index > 0)  result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

fun main() {
    val list2 = listOf(1,2,3)
    println(joinToString(list, "^^", "Start", "Last"))
    //Start1^^7^^53Last
}
```

##3.2.1 이름 붙인 인자
- 가독성 개선
- 인자 일부 전달가능 
- 순서 변경 가능
```kotlin
joinToString(collection, separator = " ", prefix = " ", postfix = ".")
```

##3.2.2 디폴트 파라미터 값
- 자바에서는 클래스 내에 오버로딩이 너무 많아질 수 있음
- 코틀린에서는 파라미터의 기본값 지정으로 개선가능 = 디폴트 파라미터

```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ",",
    prefix: String = "",
    postfix: String = ""
): String
```

```kotlin
fun main() {
    println(joinToString(list, ", ","",""))

    println(joinToString(list))
    
    println(joinToString(list, "; "))
}
```

- 위 케이스의 경우 자바에서는 오버로딩이나 함수명을 변경하여 다른 함수가 생겼을텐데 , 코틀린에서는 디폴트 파라미터를
활용해 하나의 함수로 표현 가능.
  
---

##3.2.3 정적인 유틸리티 클래스 없애기: 최상위 함수와 프로퍼티
```kotlin
package strings

fun joinToString( /* ... */ ): String {/* ... */}
```
  
- 최상위 프로퍼티 : ${최상위에선언된 프로퍼티 변수명}

---

##3.3 메서드를 다른 클래스에 추가: 확장 함수와 확장 프로퍼티

- 확장함수
- 수신 객체 타입 : 확장이 정의될 클래스의 타입
- 수신 객체는 그 타입의 인스턴스 객체
- 메서드와 프로퍼티에 접근
```kotlin
package strings

fun String.lastChar(): Char = this.get(this.length - 1)
//  수신객체타입               수신객체   수신객체
```
```kotlin
fun main() {
    println("Kotlin".lastChar()) //"Kotlin" 이 수신객체 타입인 String으로 확장함수인 lastChar() 호출 가능
}
```

## 3.3.1 임포트와 확장 함수

- 이름 충돌을 피하기 위해 임포트 필요
- as 사용해서 다른이름으로 변경 가능
## 3.3.2 자바에서 확장 함수 호출
- 정적 메서드를 호출

## 3.3.3 확장 함수로 유틸리티 함수 정의

joinToString을 확장으로 정의

```kotlin
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String =""
) : String {

    val result = StringBuilder(prefix)

    for((index, element) in collection.withIndex()) {
        if (index > 0)  result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

fun main() {
    val list = listOf(1,2,3)
    println(list.joinToString("^^", "Start", "Last"))
    //Start1^^7^^53Last
}
```

- 타입도 지정 가능, 다른타입에서 호출하면 receiver type mismatch 발생

##3.3.4 확장 함수는 오버라이드 할 수 없다
```kotlin
open class View {
    open fun click() = println("View Clicked")
}

class Button: View() {
    override fun click() = println("Button clicked")
}

fun main() {
    val view: View = Button()
    view.click()
    //Botten clicked    
}
```
- 확장함수는 오버라이드 할 수 없다 
- 확장함수는 클래스 밖에서 선언된다
- 확장함수는 컴파일 시점에 타입에 의해 결정
- 확장함수보다 멤버 함수의 우선순위가 높다 (동일 시그니처, 이름 인 경우)

##3.3.5 확장 프로퍼티
```kotlin
val String.lastChar: Char
    get() = get(length-1)
    set(value: Char) {
        this.setCharAt(length-1, value)
    }
```
---
#3.4 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원
- vararg 키워드 - 호출 시 인자 개수가 달라질 수 있는 함수를 정의 할 수 있음
- 중위 함수 호출 구문 - 인자가 하나뿐인 메서드를 간편하게 호출할 수 있다.
- 구조 분해 선언 - 복합적인 값을 분해해서 여러 변수에 나눠 담을 수 있다.

##3.4.1 자바 컬렉션 API 확장
```kotlin
fun main() {
    val strings: List<String> = listOf("first","second","fourteenth")
    strings.last()
    //fourteenth
    val numbers: Collection<Int> = setOf(1, 14, 2)
    numbers.sum()
    // 17
}
```

##3.4.2 가변 인자 함수: 인자의 개수가 달라질 수 있는 함수 정의
- vararg 키워드 > vararg value: T
- 자바의  ... 과 같은 표현

```kotlin
fun listOf<T>(vararg values: T) : List<T>{}
```

##3.4.3 튜플 다루기: 중위 호출과 구조 분해 선언

- mapOf 로 예시
- infix 변경자 선언 필요
- to 메서드 활용 (제네릭 함수)
- Pair 로 초기화

```kotlin
fun <K, V> mapOf(vararg values: Pair<K, V>): Map<K, V>
```

##3.5문자열과 정규식 다루기
- 문자열 확장함수를 제공하여 자바 문자열과 완벽히 호환됨

```kotlin
fun main() {
    println("12.3450-6.A".split(".","-"))
}

// split 확장함수
public fun CharSequence.split(vararg delimiters: String, ignoreCase: Boolean = false, limit: Int = 0): List<String> {
  if (delimiters.size == 1) {
    val delimiter = delimiters[0]
    if (!delimiter.isEmpty()) {
      return split(delimiter, ignoreCase, limit)
    }
  }

  return rangesDelimitedBy(delimiters, ignoreCase = ignoreCase, limit = limit).asIterable().map { substring(it) }
}
```

##3.5.2 정규식과 3중 따옴표로 묶은 문자열
```kotlin
fun parsePath(path: String) {
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfter("/")
    val fileName = fullName.substringBeforeLast(".")
    val extension = fullName.substringAfterLast(".")
  
    println("Dir: $directorym, name:$fileName, ext:$extension")
}
```

```kotlin
fun parsePathRegex(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
        println("Dir: $directory, name: $filename, ext: $extension")
    }
}
```

##3.5.3 여러 줄 3중 따옴표 문자열
- trimIndent : 문자열의 모든 줄에서 가장 짧은 공통 들여쓰기를 찾아 각 줄의 첫 부분에서 제거
- 아래 내용 안에 html 코드, json  , XML 등을 사용할 때 문법 내부 하이라이팅을 제공
```kotlin
val expectedage = """
   {내용}
"""
```
---
##3.6 코드 깔끔하게 다듬기 : 로컬 함수와 확장
- 보통 메서드 추출을 통해 긴메서드를 잘게 쪼개서 재사용하며 리팩토링

중복코드 예제
```kotlin
class User(val id: Int, val name: String, val address: String )

fun saveUser(user: User){
    if(user.name.isEmpty()) {
        throw illegalArgumentException{
        "Cant save user ${user.id}: emtpy Name"
      }
    }
  
    if(user.address.isEmpty()) {
      throw illegalArgumentException{
        "Cant save user ${user.address}: emtpy Address"
      }
    }
}
```

코틀린스럽게 리팩토링(로컬함수 사용하여)
```kotlin
class User(val id: Int, val name: String, val address: String )

fun saveUser(user: User) {
    
    fun validate(user: User,
                 value: String,
                 fieldName: String) {
        if (value.isEmpty()) {
          throw illegalArgumentException{
            "Cant save user ${user.id}: emtpy $fieldName"
          }
        }
    }
    validate(user, user.name, "Name")
    validate(user, user.address, "Address")
}

```

확장 함수로 추출하기
```kotlin
class User(val id: Int, val name: String, val address: String )

fun User.validateForSave() {
  fun validate(value: String,
               fieldName: String) {
    if (value.isEmpty()) {
      throw illegalArgumentException{
        "Cant save user $id: emtpy $fieldName"
      }
    }
  }
  
  validate(user, user.name, "Name")
  validate(user, user.address, "Address")
}
```

---



