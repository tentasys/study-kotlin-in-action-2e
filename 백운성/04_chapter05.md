# 4주 5장 람다를 사용한 프로그래밍 : 2025-03-24 ~ 2025-03-30

---

## 람다식과 멤버 참조

### 코드 블록을 값으로 다루기

```kotlin
// object 선언으로 리스너 구현하기
button.setOnClickListener(object : OnClickListener {
    override fun onClick(v: View) {
        println("I was clicked!")
    }
})

// 람다로 리스너 구현하기
button.setOnClickListener {
    printin("I was clicked!")
}
```

### 람다와 컬렉션

- 람다의 파라미터가 하나일 경우, 이름을 생략하고 `it`을 사용할 수 있음

```kotlin
people.maxByOrNull { it.age }
```

- `it`은 코드를 간결하게 만들어 주며, 자주 사용됨

#### 다양한 표현 방식 비교

```kotlin
people.maxByOrNull({ p: Person -> p.age })
people.maxByOrNull({ p -> p.age })
people.maxByOrNull({ it.age })
people.maxByOrNull { it.age }
people.maxByOrNull(Person::age)
```

- 인자가 하나뿐인 경우 인자에 이름을 붙이지 않아도 됨
- 모두 같은 의미이며, 점점 간결한 형태로 리팩토링 가능

#### 람다를 변수에 저장

- 람다식을 변수에 저장하여 재사용 가능
- 변수에 저장할때는 타입을 추론할 수 없어 반드시 타입을 명시해줘야 함

```kotlin
val getAge = { p: Person -> p.age }
people.maxByOrNull(getAge)
```

### 람다식의 문법

- 코틀린 람다식은 항상 중괄호{}로 둘려쌓여 있음
- 화살표 -> 가 인자와 바디를 구분해 줌
- 코드의 일부분을 블록으로 둘러싸 실행할 필요가 있다면 `run`을 사용 (`run`은 인자로 받은 람다를 실행해 주는 라이브러리 함수)

```kotlin
fun main() {
    run { println(42) }
}

// 변수 선언 시 람다식을 바로 실행할 수 있음 
val myFavoriteNumber = run {
    println("I'm thinking!")
    printin("I'm doing some more work...")
    42
}
```

### 현재 영역에 있는 변수 접근

- 코틀린 람다 안에서는 final 번수 가 아닌 변수에 접근할 수 있음
- 람다 안에서 바깥의 변수를 변경해도 됨
- 람다 안에서 접근할 수 있는 외부 변수를 `람다가 캡처한 변수`라고 부름
- 기본적으로 함수 안에 정의된 로컬 변수의 생명주기는 함수가 반환되면 끝난다.
- `람다가 캡처한 변수`가 있는 람다를 저장해서 함수가 끝난 뒤에 실행해도 변수를 읽거나 쓸 수 있음
- 변수를 특별한 래퍼로 감싸서 나중에 변경하거나 읽을 수 있음

> 람다를 이벤트 핸들러나 다른 비동기적으로 실행되는 코드로 활용하는 경우 로컬 변수 변경은 람다가 실행될 때만 일어난다.

```kotlin
// onCliCk 핸들러는 호출될 때마다 clkcks의 값을 증가시키지만 
// 핸들러는 tryTocourtButtonCicks 가 chicks를 반환한 다음에 호출되기 때문에 0만 관찰됨
fun tryToCountButtonClicks(button: Button): Int {
    var clicks = 0
    button.onClick(clicks)
    return clicks
}
```

### 멤버 참조

- ::을 사용하는 식을 `멤버 참조`라고 한다
- 최상위에 선언된(그리고 다른 클래스의 멤버가 아닌) 함수나 프로퍼티를 참조할 수도 있음

```kotlin
  fun salute() = println("Salute!")
fun main{
    run(::salute)
    // Salute!
}
```

- `생성자 참조`를 사용하면 클래스 생성 작업을 연기하거나 저장해둘 수 있음
- :: 뒤에 클래스 이름을 넣으면 생성자 참조를 만들 수 있음

```kotlin
fun main() {
    val createPerson = ::Person
    val p = createPerson("Alice", 29)
    printin(p)
    // Person (name=Alice, age=29)
}
```

### 값과 엮인 호출 가능 참조
- 뭔소리지??

## 자바의 함수형 인터페이스 사용: 단일 추상 메서드
- 인터페이스 안에 추상 메서드가 단 하나뿐인 인터페이스를 `함수형 인터페이스`나 `단일 추상 메서드(Single Abstract Method)`라고 부름
- 자바 AP에는 Runnable, callable 등의 함수형 인터페이스를 사용함
- 코틀린은 함수형 인터페이스를 파라미터로 받는 자바 메서드를 호출할 때 람다를 사용 가능

### 람다를 자바 메서드의 파라미터로 전달
```kotlin
/* 자바 코드 */
// 자바
void postponeComputation (int delay, Runnable computation);

// 코틀린에서는 이 함수를 호출할 때 람다를 인자로 보낼 수 있으며 컴파일러가 runnable 인스턴스로 변환해준다.
// 람다를 사용하면 자신이 정의된 함수의 변수에 접근하지 않는다면 함수가 호출될 때마다 람다에 해당하는 익명 객체가 재사용 됨
postponeComputation(1000) { printin(42) }

// Runnable을 명시적으로 구현하는 익명 객체를 만들어서 똑같은 효과를 낼 수 있지만 차이가 있음
// 명시적으로 객체를 선언하면 매번 호출할 때마다 새 인스턴스 가 생김
postponeComputation(1000, object : Runnable {
    override fun run() {
        printin(42)
    }
})

// 호출 마다 id를 필드 값으로 저장하는 새로운 Runnable 인스턴스가 만들어짐
fun handleComputation(id: String) {
    postponeComputation(1000) {
        // id를 캡처
        printin(id)
    }
}
```

### SAM 변환: 람다를 함수형 인터페이스로 명시적 변환
- 변수에 저장하여 재사용 가능
```kotlin
val listener = OnClickListener { view -> // 람다를 사용해 SAM 생성자를 호출한다. 
    val text = when (view.id) {
        buttoni.id -> "First button"
        button2.id -> "Second button"
        else -> "Unknown button"
    }
    toast(text)
}

button1.setOnClickListener(listener) 
button2.setOnClickListener(listener)
```

### SAM 인터페이스 정의: fun interface
- 추상 메서드가 단 하나만 들어있는 코틀린 함수형 인터페이스
```kotlin
fun interface IntCondition {
    fun check(i: Int): Boolean

    // 추상 메서드
    fun checkString(s: String) = check(s.toInt())
    fun checkChar(c: Char) = check(c.digitToInt())
}

fun main () {
    val isOdd = IntCondition { it % 2 != 0 }
    println(isOdd.check(1)) // true
    println(isOdd.checkString("2")) // false
    println(isOdd.checkChar('3')) // true
}
```

### 수신 객체 지정 람다: with, apply, also

####  with(obj) { ... }
- 수신 객체: this (명시할 필요는 없음)
- 반환값: 람다의 결과값
- 여러 작업을 하고 결과를 반환할 때 사용
```kotlin
val result = with(StringBuilder()) {
    append("Hello ")
    append("World")
    toString()  // 이 값이 result에 반환됨
}
```

#### apply { ... }
- 수신 객체: this (명시할 필요는 없음)
- 반환값: 수신 객체 자체
- 객체를 생성하고 초기화할 때 사용
```kotlin
val sb = StringBuilder().apply {
    append("Hello ")
    append("World")
}
// sb는 StringBuilder 자체
```

#### also { ... }
- 수신 객체: it
- 반환값: 수신 객체 자체
- 객체는 유지하면서 로그 찍기, 디버깅 등 부수 작업할 때 사용
```kotlin
val list = mutableListOf(1, 2, 3).also {
    println("리스트 내용: $it")
}
// list는 그대로 유지됨
```