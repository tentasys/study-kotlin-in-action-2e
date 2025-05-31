#12장 어노테이션과 리플렉션

- 어노테이션 적용과 정의
- 리플렉션을 사용해 실행 시점에 객체 내부 관찰하기
- 코틀린 실전 프로젝트 예제

## 12.1 어노테이션 선언과 적용
어노테이션을 사용하면 선언에 추가적인 메타데이터를 연관시킬 수 있다. 
그 후 어노테이션이 설정된 방식에 따라 메타데이터를 소스코드, 컴파일된 클래스 파일, 런타임에 대해 작동하는 도구를 통해 접근할 수 있다.

### 12.1.1 어노테이션을 적용해 선언에 표지 남기기
- @와 어노테이션 이름을 선언 앞에 위치
- 클래스를 어노테이션 인자로 지정
- 다른 어노테이션을 인자로 지정
- 배열을 인자로 지정
@Deprecated 
- 3가지 파라미터 : message, replaceWith, level

### 12.1.2 어노테이션이 참조할 수 있는 정확한 선언 지정: 어노테이션 타깃
- 사용지점 타깃 선언 : 어노테이션을 붙일 요소를 정할 수 있다. (ex - @get:JvmName("obtainCertificate))

```kotlin
class CertificateManager {
    @get: JvmName("obtainCertificate")
    @set: JvmName("putCertificate")
    var certificate: String = "------BEGIN PRIVATE KEY-------"
}

```

- property: 프로퍼티 전체
- field: 프로퍼티에 의해 생성되는 필드
- get: 프로퍼티 게터
- set: 프로퍼티 세터
- receive: 확장 함수나 프로퍼티의 수신 객체 파라미터
- param: 생성자 파라키터
- setparam: 세터 파라미터
- delegate: 위임 프로퍼티의 위임 인스턴스를 담아둔 필드
- file : 파일 안에 선언된 최상위 함수와 프로퍼티를 담아두는 클래스

### 12.1.3 어노테이션을 활용해 JSON 직렬화 제어
- 직렬화 -> 객체를 저장 장치에 저장하거나 네트워크를 통해 전송하기 위해 텍스트나 이진 형식으로 변환하는 것
- 역직렬화 -> 텍스트나 이진 형식으로 저정된 데이터에서 원래의 객체를 만들어낸다.
- 라이브러리 : kotlinx.serialization , Jackson, Gson
- @JsonExclude 직/역직렬화 무시 프로퍼티 지정
- @JsonName 

## 12.1.4 어노테이션 선언
``
annotation class JsonExclude
``

``
annotation class JsonName(val name: String)
``

### 12.1.5 메타어노테이션: 어노테이션을 처리하는 방법 제어

- 메타어노테이션: 어노테이션 클래스에 적용할수 있는 어노테이션
- @Target.
```kotlin
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude
```
- @Retension 어노테이션 : 어노테이션 클래스의 유지, 접근을 지정하는 메타어노테이션 (코틀린 디폴트 RUNTIME)

### 12.1.6 어노테이션 파라미터로 클래스 사용
- @DeserializeInterface : 인터페이스 타입인 프로퍼티에 대한 역직렬화를 제어할 때 쓰는 어노테이션
```kotlin
interface Company {
    val name: String
}

data class CompanyImpl(ovverride val name: String) : Company

data class person(
    val name: String,
    @DeserializeInterface(CompanyImpl::class) val company: Company
)

annotation class DeserializeInterface(val targetClass: KClass<out Any>)
```


### 12.1.7 언노테이션 파라미터로 제네릭 클래스 받기
- 제이키드는 기본적으로 내포된 객체로 직렬화 (프로퍼티 x)
- @CustomSerializer : 커스텀 직렬화 클래스에 대한 참조를 인자로 받음.
- ValueSerializer 인터페이스 구현해야함

```kotlin
interface value Serializer<T> {
    fun toJsonValue(value: T) : Any?
    fun fromJsonValue(jsonValue: Any?): T
}
```

##12.2 리플렉션: 실행 시점에 코틀린 객체 내부 관찰
- 런타임 시점에 동적으로 객체의 프로퍼티와 메서드에 접근 할 수 있게 해주는 방법
- 코틀린 리플렉션 API : kotlin.reflect , kotlin.reflect.full
- 코틀린만 다루는게 아닌 JVM 언어에서 생성한 바이트코드 

### 12.2.1 코틀린 리플렉션 API : KCLass, KCallable, KFunction, KProperty
- KClass : 사용법 -> myObject::class
- 해당 클래스의 프로퍼티 이름 및 .memberrProperties를 통해 클래스와 모든 조상 클래스 내부에 정의된 비확장 프로퍼티 전체 조회
```kotlin
interface KClass<T : Any> {
    val simpleName: String?
    val qualifiedName: String?
    val members: Collection<KCallable<*>>
    val constructor: Collection<KFunction<T>>
    val nestedClases: Collection<KClass<*>>
}
```

- KCallable 
```kotlin
interface KCallable<out R> {
    fun call(varage args: Any?): R
    // ...
}
```

- KFuntion1 = 파라미터 1개라는 뜻
```kotlin
fun foo(x: Int) = println(x)
fun main() {
    val kFuntion = ::foo
    kfuntion.call(42)
}
```

```kotlin
import kotlin.reflect.KFuntion2

fun sum(x: Int, y: Int) = x + y

fun main() {
    val kFuntionL: KFuntion2<Int, Int, Int> = ::sum
    println(kFuntion.invoke(1,2) + kFuntion(3,4))
    //10
    kFuntion(1)
    // ERROR: No value passed for paramter p2
}
```

언제 그리고 어떻게 KFuntionN 인터페이스가 정의되는가

### 12.2.2 리플렉션을 사용해 객체 직렬화 구현
제이키드 직렬화 함수 선언
``
fun serialize(obj: Any): String
``
- 직렬화 StringBuilder의 확장 함수로 구현
- sserialize 는 대부분의 작업을 serializeObject에 위임

``fun serialize(obj: Any`: String = buildString{ serializeObject(obj) }``

객체 직렬화 하기
```kotlin
private fun StringBuilder.serializeObject(obj: Any) {
    val kClass = obj::class as KClass<Any>
    val properties = kClass.memberProperties
    
    properties.joinToStringBuilder(
        this, prefix = "{", postfix = "}" {
            prop -> serializeString(prop.name)
            append(": ")
            serializePropertyValue(prop.get(obj))
        }
    )
}
```

### 12.2.3 어노테이션을 활용해 직렬화 제어
프로퍼티 필터링을 포함하는 객체 직렬화
```kotlin
private fun StringBuilder.serializeObject(ob: Any) {
    (obj::class as KClass<Any>)
        .memberProperties
        .filter {it.findAnnotation<JsonExclude>() == null}
        .joinToStringBuilder(this, prefix = "{", postfix = "}") {
            serializeProperty(it, obj)
        }
}
```

하나의 프로퍼티 직렬화하기
```kotlin
private fun StringBuilder.serializeProperty(
    prop: KProperty1<Any, *>, obj: Any
) {
    val jsonNameAnn = prop.findAnnotation<JsonName> ()
    val propName = jsonNameAnn?.name?:prop.name
    serializeString(propName)
    append(": ")
    serializePropertyValue(prop.get(obj))
}
```

프로퍼티의 값을 직렬화하는 직렬화기 가져오기
```kotlin
fun KProperty<*>.getSerializer(): ValueSerializer<Any?>? {
    val customSerializerAnn = findAnnotation<CustomSerializer>()
        ?: return null
    val serializerClass = customSerializerAnn.serializerClass
    val valueSerializer = serializerClass.objectInstance
        ?:serializerClass.cretaeInstance()
    @Supperess("UNCHECKED_CAST")
    return valueSerializer as ValueSerializer<Any?>
}
```

### 12.2.4 JSON 파싱과 객체 역직렬화
- 실행 시점에 타입 파라미터에 접근 > reified + inline
- 제이키드 역직렬화기 3단계 : 
  
  렉서-어휘분석기 , 파서-문법분석기, 역직렬화 컴포넌트

JSON 파서 콜백 인터페이스
```kotlin
interface JsonObject {
    fun setSimpleProperty(propertyName: String, value: Any?)
    fun createObject(propertyName: String): JsonObject
    fun createArray(propertyName: String) : JsonObject
}
```

JSON 데이터로부터 객체를 만들어 내기 위한 인터페이스
```kotlin
interface Seed : JsonObejct {
    fun spawn(): Any?
    fun createCompositeProperty(
        propetyName: String,
        isList: Boolean
    ): JsonObject
    override fun createObject(propertyName: String) =
        createCompositeProperty(propertyName, false)
    override fun createArray(propertyName: String) = 
        createCompositeProperty(propertyName, true)
}
```

최상위 역직렬화 함수
```kotlin
fun <T: Any> deserialize(json: Reader, targetClass: KClass<T>): T {
    val seed = ObjectSeed(targetClass, ClassInfoCache())
    Parser(json, seed).parse()
    return seed.spawn
}
```

객체 역직렬화 하기
```kotlin
class ObjectSeed<out T: Any> (
    targetClass: KCalss<T>,
    override val classInfoCache: ClassInfoCache
    ) : Seed {
        private val classInfo: ClassInfo<T> = classInfoCache[targetClass]
    private val valueArguments = mutableMapOf<KParameter, Any?>()
    private val seedArguments = mutableMapOf<KParameter, Seed>()
    
    private val arguments: Map<KParameter, Any?>
        get() = valueArguments + seedArguments.mapValues {it.value.spawn()}
    override fun setSimpleProperty(propertyName: String, value: Any?) {
        val param = classInfo.getConstructorParameter(propertyName)
        valueArguments[param]  = classInfo.deserializeConstructorArgument(param, value)
    }
    
    override fun createCompositeProperty(
        propertyName: String, isList: Boolean
    ): Seed {
        val param = classInfo.getConstructorParameter(propertyName)
        val deserializeAs = classInfo.getDeserializeClass(propertyName)?.starProjectedType
        val seed = createSeedForType(
            deserializeAs ?: param.type, isList
        )
        return seed.apply { seedArguments[param] = this }
    }
    
    override fun spawn(): T = 
        classInfo.createInstance(arguments)
}
```

- ObjectSeed -> 생성자 파라미터와 값을 연결해주는 맵 생성
- valueArguments -> 간단한 값 프로퍼티 저장
- seedArguments -> 복합 프로퍼티 저장
- createCompositeProperty -> seedArguments 맵에 새 인자 추가
- spawn 을 재귀적으로 호출해서 내포된 모든 객체 생성
- createSeedForType 함수로 파라미터가 어떤 종류의 컬렉션인지에 따라 ObjectSeed, ObjectListSeed, ValueListSeed 생성


### 12.2.5 최종 역직렬화 단계: callBy()와 리플렉션 사용해 객체 만들기
값 타입에 따라 역직렬화기 가져오기
```kotlin
fun serializerForType(type: Type) : ValueSerializer<out Any?>? = 
    when(type) {
        typeOf<Byte>() -> ByteSerializer
        typeOf<Int>() -> IntSerializer
        typeOf<Boolean>() -> BooleanSerializer
        //...
        else -> null
    }
```

Boolean 값을 위한 역직렬화기
```kotlin
object BooleanSerializer : ValueSerializer<Boolean> {
    override fun fromJsonValue(jsonValue: Any?) : Boolean {
        if (jsonValue !is Boolean) throw JKidException("Boolean expected")
        return jsonValue
    }
    
    override fun toJsonValue(value: Boolean) = value
}
```

리플렉션 데이터 캐시 저장소
```kotlin
class ClassInfoCache {
    private val cacheData = mutableMapOf<KClass<*>, ClassInfo<*>>()
    
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(clas: KClass<T>): ClassInfo<T> = 
        cacheData.getOrPut(cls) { ClassInfo(cls) } as ClassInfo<T>
}
```

필수 파라미터가 모두 있는지 검증하기
```kotlin
private fun ensureAllParameterPresent(arguments: Map<KParameter, Any?>) {
    for (param in constructor.parameters) {
        if (arguments[param] == null && !param.isOptional && !param.type.isMarkedNullable) [
            throw JKidException("Missing value for parameter ${param.name}")
        ]
    }
}
```