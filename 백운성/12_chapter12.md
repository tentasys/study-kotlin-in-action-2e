# 12주 12장 애노테이션과 리플렉션 : 2025-05-19 ~ 2025-05-25

# 애노테이션과 리플렉션

---

### 어노테이션을 적용해 선언에 표지 남기기

* 클래스를 어노테이션 인자로 지정: @MyAnnotation(MyClass::class)처럼 ::class 를 클래스 이름 뒤에 넣어야 한다.
* 다른 어노테이션을 인자로 지정: 어노테이션의 인자로 들어가는 어노테이션에는 @를 붙이지 않는다.
* 배열을 인자로 지정: `[]`를 사용하여, arrayOf 함수를 사용할 수 도 있다.

### 어노테이션이 참조할 수 있는 정확한 선언 지정: 어노테이션 타깃

* 코틀린은 어노테이션 타깃을 명시적으로 지정 가능.
* 예: `@get:MyAnnotation` → getter에만 어노테이션 적용.

### 어노테이션을 활용해 JSON 직렬화 제어

* 어노테이션을 사용하여 특정 프로퍼티를 JSON 직렬화에서 제외하거나 이름을 바꿔서 매핑 가능.
* 커스텀 직렬화 로직에서 어노테이션 정보를 활용하여 직렬화 여부 결정.

```kotlin
data class Person(
    // 직렬화 대상
    @JsonName("alias") val firstName: String,
    // 직렬화 제외
    // 직렬화 제외 시 반드시 기본값 설정!!
    @sonExclude val age: Int? = null
)
```

### 어노테이션 선언

* 파라미터가 있는 어노테이션을 정의하려면 어노테이션 클래스의 주 생성자에 피라 미터를 선언해야 한다.
* 모든 파라미터는 val 프로퍼티로 선언해야 하며, 본문은 없어야 함.
* 선언 형식: `annotation class MyAnnotation(val param: String)`

### 메타어노테이션: 어노테이션을 처리하는 방법 제어

* 어노테이션 클래스에 붙이는 어노테이션을 `메타어노테이션`이라고 함
* 예: `@Target`, `@Retention`, `@Repeatable` 등
* `@Target`으로 적용 가능한 요소 지정
* `@Retention`으로 유지 범위 지정 (SOURCE, BINARY, RUNTIME)

### 어노테이션 파라미터로 클래스 사용

* 클래스 참조를 어노테이션 파라미터로 전달 가능: `val clazz: KClass<*>`
* 예: `@MyAnnotation(clazz = String::class)`

### 어노테이션 파라미터로 제네릭 클래스 받기

* 제네릭 타입 자체는 어노테이션 인자로 전달 불가.
* 대신 일반 클래스 타입을 전달하거나 구체 타입으로 제한해야 함.

---

## 리플렉션: 실행 시점에 코틀린 객체 내부 관찰

* 실행 시점에 (동적으로) 객체의 프로퍼티와 메서드에 접근할 수 있게 해주는 방법

### 코틀린 리플렉션 API: KClass, KCallable, KFunction, KProperty

* `ClassName::class` 또는 `obj::class`로 `KClass` 인스턴스를 획득.
    * KClass: 클래스 안에 있는 모든 선언을 열거하고 각 선언에 접근하거나 클래스의 상위 클래스를 얻는 등의 작업하는데 사용
* `KCallable`은 함수와 프로퍼티를 표현하는 공통 인터페이스.
* `KFunction0`, `KFunction1`, ... 은 파라미터 수에 따라 구분.
* `invoke()`로 호출 가능.
* `KProperty0`, `KProperty1`은 프로퍼티 표현. `get()`으로 값 획득, `KMutableProperty` 계열은 `set()`도 지원.

| 기능      | Kotlin 리플렉션                                  | Java 리플렉션                                         |
|---------|----------------------------------------------|---------------------------------------------------|
| 클래스 참조  | `obj::class`                                 | `obj.getClass()`                                  |
| 프로퍼티 접근 | `KProperty.get()` / `KMutableProperty.set()` | `Field.get()`, `Field.set()` 또는 Getter/Setter 메서드 |
| 생성자 호출  | `KFunction.call()`                           | `Constructor.newInstance()`                       |
| 함수 호출   | `KFunction.invoke()`                         | `Method.invoke()`                                 |

#### Kotlin
```kotlin
import kotlin.reflect.*
import kotlin.reflect.full.*

data class Person(val name: String, var age: Int)

fun main() {
    val person = Person("Alice", 30)

    // KClass
    val kClass: KClass<out Person> = person::class
    println("클래스 이름: ${kClass.simpleName}")

    // KProperty
    val prop: KProperty1<Person, *> = Person::name
    println("이름 프로퍼티 값: ${prop.get(person)}")

    // KMutableProperty
    val mutableProp = Person::age as KMutableProperty1<Person, Int>
    mutableProp.set(person, 35)
    println("변경된 나이: ${person.age}")

    // KFunction
    val constructor: KFunction<Person> = ::Person
    val newPerson = constructor.call("Bob", 22)
    println("새 인스턴스: $newPerson")
}
```

#### Java 
```java
import java.lang.reflect.*;

class Person {
    public final String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String toString() {
        return "Person(name=" + name + ", age=" + age + ")";
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        Person person = new Person("Alice", 30);

        // Class
        Class<?> clazz = person.getClass();
        System.out.println("클래스 이름: " + clazz.getSimpleName());

        // Field (public name)
        Field nameField = clazz.getField("name");
        System.out.println("이름 프로퍼티 값: " + nameField.get(person));

        // Method (private age with getter/setter)
        Method setAge = clazz.getMethod("setAge", int.class);
        setAge.invoke(person, 35);
        Method getAge = clazz.getMethod("getAge");
        System.out.println("변경된 나이: " + getAge.invoke(person));

        // Constructor
        Constructor<?> constructor = clazz.getConstructor(String.class, int.class);
        Object newPerson = constructor.newInstance("Bob", 22);
        System.out.println("새 인스턴스: " + newPerson);
    }
}
```

### 리플렉션을 사용해 객체 직렬화 구현
* 리플렉션을 이용해 객체의 프로퍼티 목록을 얻고, 값을 추출해 JSON 등으로 직렬화.
* 직렬화 프레임워크를 직접 만들 때 사용.
#### Kotlin

```kotlin
val jsonMap = Person::class.memberProperties
    .associate { it.name to it.get(person) }
```

#### Java

```java
Map<String, Object> jsonMap = new HashMap<>();
for (Field field : clazz.getDeclaredFields()) {
    field.setAccessible(true);
    jsonMap.put(field.getName(), field.get(person));
}
```

|          | Kotlin               | Java                     |
|----------|----------------------|--------------------------|
| 접근 방식    | `KProperty` 기반       | `Field` 기반               |
| 가독성      | 선언적, 간결함             | 반복적, verbose             |
| 접근 제어 우회 | 필요 없음 (대개 public 기준) | `setAccessible(true)` 필수 |

---

### 어노테이션을 활용해 직렬화 제어
* 특정 어노테이션이 붙은 프로퍼티만 직렬화
* 어노테이션의 파라미터 값에 따라 이름 변경, 무시, 조건부 직렬화 가능
#### Kotlin

```kotlin
annotation class JsonExclude
annotation class JsonName(val name: String)

fun serialize(obj: Any): Map<String, Any?> {
    return obj::class.memberProperties
        .filter { it.findAnnotation<JsonExclude>() == null }
        .associate {
            val name = it.findAnnotation<JsonName>()?.name ?: it.name
            name to it.get(obj)
        }
}
```

#### Java

```java
if(field.getAnnotation(JsonExclude.class) == null){
    String name = field.isAnnotationPresent(JsonName.class)
        ? field.getAnnotation(JsonName.class).value()
        : field.getName();
    map.put(name, field.get(person));
}
```

|            | Kotlin                   | Java                    |
|------------|--------------------------|-------------------------|
| 어노테이션 활용   | `findAnnotation<T>()` 사용 | `field.getAnnotation()` |
| 코드 양       | 간결                       | 복잡하고 길어짐                |
| DSL 설계 적합성 | 매우 적합                    | 상대적으로 불리                |

---

### JSON 파싱과 객체 역직렬화
* JSON 필드를 매칭해 객체의 프로퍼티에 값을 할당
* 리플렉션으로 생성자나 프로퍼티를 찾아 값을 설정
#### Kotlin

```kotlin
val constructor = Person::class.primaryConstructor!!
val instance = constructor.callBy(
    constructor.parameters.associateWith { paramMap[it.name] }
)
```

#### Java

```java
Constructor<?> ctor = clazz.getConstructor(String.class, int.class);
Object obj = ctor.newInstance(jsonMap.get("name"), jsonMap.get("age"));
```

|                | Kotlin                   | Java                  |
|----------------|--------------------------|-----------------------|
| 생성자 파라미터 이름 사용 | 가능 (`parameter.name`)    | 불가능 (기본 리플렉션으론 불가)    |
| 유연성            | 높음 (`callBy`로 기본값 사용 가능) | 낮음 (모든 파라미터 순서 맞춰야 함) |

---

### 최종 역직렬화 단계: callBy()와 리플렉션을 사용해 객체 만들기

* `callBy()`를 통해 일부 매개변수만 전달하거나 기본값 활용 가능 → 유연한 생성자 호출 가능
* Java는 생성자 인자 순서를 맞춰야 하며, 파라미터 이름을 알 수 없어서 불편함
