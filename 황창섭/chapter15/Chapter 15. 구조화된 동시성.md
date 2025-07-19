# Chapter 15. 구조화된 동시성

- 구조화된 동시성을 통해 코루틴 간의 계층을 설정하는 방법
- 구조화된 동시성을 통해 코드 실행과 취소를 세밀하게 제어하고, 코루틴 계층 전반에 걸쳐 자동으로 취소를 전파하는 방법
- 코루틴 콘텍스트와 동시성 간의 관계
- 취소 시에도 올바르게 동작하는 코드를 작성하는 방법

## 15.1 코루틴 스코프가 코루틴 간의 구조를 확립한다

- 구조화된 동시성을 통해 각 코르틴은 코루틴 스코프(coroutine scope)에 속하게 된다.
- launch, asnyc 코루틴 빌더 함수는 CoroutineScope 인터페이스의 확장 함수
- 다른 코루틴 빌더의 본문에서 launch, async를 사용해 새로운 코루틴을 만들면, 새로운 코루틴은 자동으로 코루틴의 자식이 됨

```kotlin
/**
 * 다른 여러 코루틴 시작하기
 */
fun main() {
    runBlocking {
        launch {
            delay(1.seconds)
            launch {
                delay(250.milliseconds)
                log("Grandchild done")
            }
            log("Child 1 done!")
        }
        launch {
            delay(500.milliseconds)
            log("Child 2 done!")
        }
        log("Parent done!")
    }
}

/*

결과

29 main@coroutine#1 Parent done!
539 main@coroutine#3 Child 2 done!
1039 main@coroutine#2 Child 1 done!
1293 main@coroutine#4 Grandchild done!

 */
```

- 코루틴 간에 부모-자식 관계가 있기에 runBlocking은 어떤 자식 코루틴이 작업 중인지 알고 모든 작업이 완료될 때까지 대기
- 구조화된 동시성 덕분에 코루틴은 계층 구조 안에 존재한다. 명시적으로 지정하지 않았음에도 각 코루틴은 자식이나 부모를 알고 있다.

### 15.1.1 코루틴 스코프 생성: coroutineScope 함수

- 코루틴 빌더를 사용해 새로운 코루틴을 만들면 이 코루틴은 자체적인 CoroutineScope를 생성한다.
- 또한 coroutineScope 함수를 사용하여 코루틴 스코프를 그룹화 할 수 있다.
- coroutineScope 함수의 전형적인 사용 사례는 동시적 작업 분해, 즉 여러 코루틴을 활용해 계산을 수행하는 것

```kotlin
suspend fun computeSum() { // computeSum 함수는 일시 중단 함수
    val sum = coroutineScope { // 새로운 코루틴 스코프를 생성
        val a = async { generateValue() }
        val b = async { generateValue() }
        a.awiat() + b.await()
    }
}

fun main() = runBlocking { computeSum() }
```

### 15.1.2 코루틴 스코프를 컴포넌트와 연관시키기

- 작업을 분해하는 데 그치지 않고 구체적 생명주기를 정의하고, 동시 처리나 코루틴의 시작과 종료를 관리하는 클래스를 만들고자 한다면 CoroutineScope 생성자 함수를 사용
- coroutineScope와 달리 실행을 일시 중단하지 않으며, 새로운 코루틴을 시작할 때 쓸 수 있는 새로운 코루틴 스코프를 생성
- CoroutineScope를 디스패처만으로 호출하면 새로운 Job이 자동 생성되지만 실무에서는 SupervisorJob을 함께 사용하는게 좋다.(관련하여 18장 참조)

```kotlin
/**
 * 코루틴 스코프와 연관된 컴포넌트
 */
class ComponentWithScope(dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    fun start() {
        log("Starting!")
        scope.launch {
            while (true) {
                delay(500.milliseconds)
                log("Component working!")
            }
        }
        scope.launch {
            log("Doing a one-off task...")
            delay(500.milliseconds)
            log("Task done!")
        }
    }

    fun stop() {
        log("Stopping!")
        scope.cancel()
    }
}


fun main() {
    val c = ComponentWithScope()
    c.start()
    Thread.sleep(2000)
    c.stop()
}

/*
22 [main] Starting!
37 [DefaultDispatcher-worker-2@coroutine#2] Doing a one-off task...
544 [DefaultDispatcher-worker-1@coroutine#2] Task Done!
544 [DefaultDispatcher-worker-2@coroutine#1] Component working!
1050 [DefaultDispatcher-worker-1@coroutine#1] Component working!
1555 [DefaultDispatcher-worker-1@coroutine#1] Component working!
2039 [main] Stopping!
 */

```

### 15.1.3 GlobalScope의 위험성

- GlobalScope의 단점
    - 구조화된 동시성이 제공하는 모든 이점을 포기해야 한다.
    - 코루틴은 자동으로 취소되지 않으며, 생명주기에 대한 개념도 없다
    - 리소스 누수의 가능성
- GlobalScope를 선택해야 하는 경우는 매우 드뭄(애플리케이션의 전체 생명주기 동안 활성 상태를 유지해야 하는 최상위 백그라운드 프로세스)

### 15.1.4 코루틴 콘텍스트와 구조화된 동시성

- 코루틴 콘텍스트: 코루틴의 동작을 정의하는 요소들의 집합. 각 코루틴은 자신만의 코루틴 컨텍스트를 가지며 코루틴이 어떻게 실행되고, 어떤 스레드에서 실행되며, 어떤 이름으로 로깅될지 등을 결정
- 코루틴 콘텍스트 구성 요소
    - Job: 코루틴의 Lifecycle 관리
    - CoroutineDispatcher: 코루틴이 어떤 스레드에서 실행될지 결정
- 명시적으로 컨텍스트를 지정하지 않으면 부모 코루틴의 컨텍스트를 상속
- 명시적으로 컨텍스트를 지정하면 merge 수행. 동일한 타입의 Element가 중복될 경우, 새로 지정된 Element로 덮어씀

## 15.2 취소

- 취소
    - 불필요한 작업을 방지함
    - 메모리나 리소스 누스를 방지하는 데 도움을 줌
    - 오류 처리에 도움을 줌

### 15.2.1 취소 촉발

- launch는 Job을 반환하고 async는 Deferred를 반환 -> 둘 다 cancel을 호출해 해당 코루틴의 취소를 촉발할 수 있다.

### 15.2.2 시간제한이 초과된 후 자동으로 취소 호출

- 코루틴을 자동으로 취소하도록 돕는 함수: withTimeout, withTimeoutOrNull

```kotlin
suspend fun calculateSomething(): Int {
    delay(3.seconds)
    return 2 + 2
}

fun main() = runBlocking {
    val quickResult = withTimeoutOrNull(500.milliseconds) {
        calculateSomthing()
    }
    println(quickResult) // null

    val slowResult = withTimeoutOrNull(5.seconds) {
        calculateSomething()
    }
    println(slowResult) // 4
}
```

### 15.2.3 취소는 모든 자식 코루틴에게 전파된다

- 코루틴을 취소하면 모든 자식 코루틴도 **자동**으로 취소

### 15.2.4 취소된 코루틴은 특별한 지점에서 CancellationException을 던진다

- 취소 메커니즘은 delay와 같은 일시 중단 지점에서 CancellationException 라는 특수한 예외를 던지는 방식으로 작동함

### 15.2.5 취소는 협력적이다

- 코루틴에 기본적으로 포함된 모든 함수는 취소 가능하다
- 하지만 직접 작성한 코드에서 취소를 하려면 협력적으로 취소 가능하도록 작성을 해야함

```kotlin
fun main() {
    runBlocking {
        val myJob = launch {
            repeat(5) {
                doCpuHeavyWork()
            }
        }
    }
    delay(600.milliseconds)
    myJob.cancel()
}
```

- doCpuHeavyWork() 에 delay와 같은 취소 가능 지점이 없다면 myJob.cancel을 호출해도 5번 반복 수행이 완료된 뒤 종료된다.

### 15.2.6 코루틴이 취소됐는지 확인

- CoroutineScope의 isActive 속성 확인
- ensureActive 함수를 사용하면 코루틴이 활성 상태가 아닐 경우 CancellationException을 던진다

### 15.2.7 다른 코루틴에게 기회를 주기: yield 함수

- yield 함수: 코드 내에서 취소 가능 지점 제공. 현재 점유된 디스패처에서 다른 코루틴이 작업할 수 있도록 해줌

```kotlin

fun doCpuHeavyWork(): Int {
    var counter = 0
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() < startTime + 500) {
        count++;
        yield()
    }

    return counter
}

fun main() {
    runBlocking {
        launch {
            repeat(3) {
                doCpuHeavyWork()
            }
        }

        launch {
            repeat(3) {
                doCpuHeavyWork()
            }
        }
    }
}

/*
coroutine#2
coroutine#2
coroutine#2
coroutine#3
coroutine#3
coroutine#3
 */
```

- doCpuHeavyWork 내에 일시 중단 지점이 없으면 두 번째 코루틴은 실행되지 않는다.

```kotlin
/**
 * yield를 사용해 다른 코르틴으로 전환하기
 */
fun doCpuHeavyWork(): Int {
    var counter = 0
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() < startTime + 500) {
        count++;
        yield()
    }

    return counter
}

fun main() {
    runBlocking {
        launch {
            repeat(3) {
                doCpuHeavyWork()
            }
        }

        launch {
            repeat(3) {
                doCpuHeavyWork()
            }
        }
    }
}

/*
coroutine#2
coroutine#3
coroutine#2
coroutine#3
coroutine#2
coroutine#3
 */
```

- 첫 번째 코루틴과 두 번째 코루틴이 번갈아 가며 수행


### 15.2.8 리소스를 얻을 때 취소를 염두에 두기

- DB 커넥션 등을 코루틴에서 사용할 때 항상 finally로 close를 호출해 적절히 리소스 해제를 해야할 필요가 있다