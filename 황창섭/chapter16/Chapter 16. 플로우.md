# Chapter 16. 플로우

- 값의 연속적인 스트림을 모델링하는 플로우를 다루는 방법
- 콜트 플로우와 핫 플로우의 차이점과 사용 사례

## 16.1 플로우는 연속적인 값의 스트림을 모델링한다

```kotlin
/**
 * 일시 중단 함수는 중간값을 반환하지 않는다
 */
suspend fun createValues(): List<Int> {
    return buildList {
        add(1)
        delay(1.seconds)
        add(2)
        delay(1.seconds)
        add(3)
        delay(1.seconds)
    }
}

fun main() = runBlocking {
    val list = createValues()
    list.forEach {
        log(it) // 모든 값이 3초 후 출력된다
    }
}
```

- 중간값을 받고 싶을 경우? 플로우
- 플로우의 설계는 reactive stream에서 영감을 얻음

### 16.1.1 플로우를 사용하면 배출되자마자 원소를 처리할 수 있다

```kotlin
/**
 * 플로우 생성과 수집
 */
suspend fun createValues(): Flow<Int> {
    return flow {
        emit(1)
        delay(1.seconds)
        emit(2)
        delay(1.seconds)
        emit(3)
        delay(1.seconds)
    }
}

fun main() = runBlocking {
    val myFlow = createValues()
    myFlow.collect { log(it) } // 값이 배출되자마자 출력
}
```

### 16.1.2 코틀린 플로우의 여러 유형

- 콜드 플로우: 비동기 데이터 스트림으로, 값이 실제로 소비되기 시작할 때만 값을 배출한다.
- 핫 플로우: 값이 실제로 소비되고 있는지와 상관없이 값을 독립적으로 배출하며, 브로드캐스트 방식으로 동작한다.

## 16.2 콜드 플로우

### 16.2.1 flow 빌더 함수를 사용해 콜드 플로우 생성

```kotlin
/**
 * flow 빌더에서 일시 중단 함수 호출하기
 */
fun main() {
    val letters = flow {
        emit("A")
        delay(200.milliseconds)
        emit("B")
    }
}
```

- 위 플로우는 비활성 상태이며, 최종 연산자(terminal operator)가 호출돼야만 빌더에서 정의된 계산이 시작된다.
- flow 빌더 함수를 호출해도 실제 작업이 시작되지 않기 때문에 일시 중단 코드가 아닌 코틀린 코드에서 플로우를 작성할 수 있음.

### 16.2.2 콜드 플로우는 수집되기 전까지 작업을 수행하지 않는다

- Flow에 대해 collect 함수를 호출하면 로직이 실행
- collect는 일시 중단 함수이다

```kotlin
/**
 * 호출 순서 살펴보기
 */
val letters = flow {
    emit("A")
    delay(200.milliseconds)
    emit("B")
}

fun main() = runBlocking {
    letters.collect { 
        log("Collecting $lt")
        delay(500.milliseconds)
    }
}
/*
플로우빌더: emit("A")
수집자: log("Collecting A")
수집자: delay(500)
플로우빌더: delay(200)
플로우빌더: emit("B")
수집자: log("Collecting B")
수집자: delay(500)
 */
```

### 16.2.3 플로우 수집 취소

```kotlin
fun main() = runBlocking {
    val collector = launch {
        counterFlow.collect {
            println(it)
        }
    }
    delay(5.seconds)
    collector.cancel()
}
```

- 플로우에서 emit 이 일시 중단 지점으로 작동한다.

### 16.2.4 콜드 플로우의 내부 구현

- collect() 호출: 소비자가 Flow 객체에 대해 collect()를 호출
- 생산자 코루틴 시작: Flow 빌더 내부의 코드가 실행되기 위한 생산자 코루틴이 시작
- emit() 호출: 생산자 코루틴은 데이터를 생성하고 emit() 함수를 호출
- collect() 재개 및 처리: emit() 호출은 실제로 collect() 블록의 람다 함수를 호출
- 스트림 종료: 생산자 코루틴의 모든 작업이 완료되거나, 예외가 발생하거나, 구독이 취소되면 스트림 종료

### 16.2.5 채널 플로우를 사용한 동시성 플로우

- 채널 플로우: 여러 코루틴에서 배출을 허용하는 동시성 플로우
- 채널 플로우는 순차적으로 배출하는 emit 함수를 제공하지 않고 send를 사용해 값을 제공할 수 있다.

```kotlin
/**
 * 채널 플로우에서 원소를 동시적으로 보내기
 */
val randomNumbers = channelFlow {
    repeat(10) {
        launch {
            send(getRandomNumber())
        }
    }
}
```

- 플로우 안에서 새로운 코루틴을 시작해야하는 경우에만 채널 플로우를 선택하는 것을 권장

## 16.3 핫 플로우

- 시스템에서 이벤트나 상태 변경이 발생해서 수집자가 존재하는지 여부에 상관없이 값을 배출하는 것과 같은 케이스에서 핫 플로우 사용
- 핫 플로우 구현
  - 공유 플로우(shared flow): 값을 브로드캐스트하기 위해 사용
  - 상태 플로우(state flow): 상태를 전달하는 특별한 경우에 사용

### 16.3.1 공유 플로우는 값을 구독자에게 브로드캐스트한다

```kotlin
class RadisStation {
    private val _messageFlow = MutableSharedFlow<Int>()
    val messageFlow = _messageFlow.asSharedFlow() // 공유 플로우에 대한 읽기 전역 뷰를 제공
    
    fun beginBroadcasting(scope: CoroutineScope) {
        scope.launch {
            while(true) {
                delay(500.milliseconds)
                val number = Random.nextInt(0..10)
                log("Emitting $number!")
                _messageFlow.emit(number)
            }
        }
    }
}
```

### 16.3.2 시스템 상태 추적: 상태 플로우

- 상태 플로우 주요 주제
  - 상태 플로우를 생성하고 구독자에게 노출시키는 방법
  - 상태 플로우의 값을 병렬로 접근해도 안전하게 갱신하는 방법
  - 값이 실제로 변경될 때만 상태 플로우가 값을 배출하게 하는 동등성 기반 통합(equality-based conflation) 개념
  - 콜드 플로우를 상태 플로우로 변환하는 방법

```kotlin
/**
 * 상태 플로우를 사용하는 기본적인 뷰 카운터 구현
 */
class ViewCounter {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()
    fun increment() {
        _counter.update {it + 1}
    }
}

fun main() {
    val vc = ViewCount()
    vc.increment()
    println(vc.counter.value)
}
```

- update 함수를 사용하여야 원자적으로 값을 갱신할 수 있다
- 상태 플로우는 값이 실제로 달라졌을 때만 값을 배출한다: 동등성 기반 통합

### 16.3.3 상태 플로우와 공유 플로우의 비교

```kotlin
/**
 * 첫 번째 구독자가 나타나기 전에 모든 메시지를 배출하는 브로드캐스트
 */
class Broadcaster {
    private val _messages = MutableSharedFlow<String>()
    val messaages = _messages.asSharedFlow()
    fun beginBroadcasting(scope: CoroutineScope) {
        scope.launch {
            _messages.emit("Hello!")
            _messages.emit("Hi!")
            _messages.emit("Hola!")
        }
    }
}

// 인스턴스 생성 및 begineBroadcasting 호출 후 collect가 되어 있지 않았다면 메시지가 유실됨. 즉 메시지 기록을 보는 상황에서 공유 플로우는 부적절
```

```kotlin
/**
 * 상태 플로우를 사용해 전체 메시지 기록 저장하기
 */
class Broadcaster {
    private val _messages = MutableStateFlow<List<String>>(emptyList<>())
    val messaages = _messages.asStateFlow()
    fun beginBroadcasting(scope: CoroutineScope) {
        scope.launch {
            _messages.update("Hello!")
            _messages.update("Hi!")
            _messages.update("Hola!")
        }
    }
}

// 인스턴스 생성 및 begineBroadcasting 호출 후 collect가 되어 있지 않았다면 메시지가 유실됨
```

### 16.3.4 핫 플로우, 콜드 플로우, 공유 플로우, 상태 플로우: 언제 어떤 플로우를 사용할까?

- 콜드 플로우
  - 기본적으로 비활성
  - 수집자가 하나 있음
  - 수집자는 모든 배출을 받음
  - 보통은 완료됨
  - 하나의 코루틴에서 배출 발생(channelFlow 사용 시 예외)
- 핫 플로우
  - 기본적으로 활성화됨
  - 여러 구독자가 있음
  - 구독자는 구독 시작 시점부터 배출을 받음
  - 완료되지 않음
  - 여러 코루틴에서 배출 발생