# Chapter 14. 코루틴

- 동시성과 병렬성의 개념
- 코틀린에서 동시성 연산을 만드는 빌딩 블록인 일시 중단 함수
- 코틀린에서 코루틴을 활용해 동시성 프로그래밍에 접근하는 방법

## 14.1 동시성과 병렬성

- 동시성: 여러 작업을 동시에 실행. 하지만 물리적 실행뿐 아니라 컨텍스트 스위칭을 해도 동시성을 달성
- 병렬성: 여러 작업을 여러 CPU 코어에서 물리적으로 동시에 실행하는 것을 뜻함

## 14.2 코틀린의 동시성 처리 방법: 일시 중단 함수와 코루틴

- 코루틴은 전통적인 스레드에 비교해 훨씬 더 가볍게 작동한다
- 일시 중단 함수(suspending function): 스레드를 블록시키는 단점 없이 순차적 코드처럼 보이는 동시성 코드를 작성할 수 있게 해줌

## 14.3 스레드와 코루틴 비교

```kotlin
import kotlin.concurrent.thread

fun main() {
    println("${Thread.currentThread().name}") // main
    thread {
        println("${Thread.currentThread().name}") // Thread-0
    }
}
```

- 스레드의 단점
  - JVM에서 생성하는 각 스레드는 일반적으로 운영체제가 관리하는 스레드이며, 이런 시스템 스레드를 생성하고 관리하는 것은 비용이 많이 듬
  - 최신 시스템이라도 한 번에 몇 천개의 스레드만 효과적으로 관리할 수 있다
  - 시스템 스레드는 몇 메가바이트의 메모리를 할당받고 스레드 간 전환은 운영체제 커널 수준에서 실행되는 작업이다.
  - 스레드는 작업이 완료되길 기다리는 동안에는 블록된다

- 코틀린에서 스레드의 대안으로 코루틴 추상화 도입
- 코루틴의 장점
  - 코루틴은 초경량 추상화. 일반적인 노트북에서도 100,000개 이상의 코루틴을 쉽게 실행할 수 있다. 또한 생성, 관리 비용이 저렴해 세밀하거나 아주 짧은 시간 동안만 실행하는 작업에도 활용할 수 있다
  - 시스템 자원을 블록시키지 않고 실행을 일시 중단할 수 있으며, 중단된 지점에서 실행을 재개할 수 있다. 네트워크 요청이나 IO 작업 같은 비동기 작업을 처리할 때 블로킹 스레드보다 훨씬 효율적
  - 구조화된 동시성(structured concurrency) 라는 개념을 통해 동시 작업의 구조와 계층을 확립하며, 취소 및 오류 처리를 위한 메커니즘을 제공. 
    동시 계산의 일부가 실패하거나 더이상 필요하지 않게 됐을 때 자식으로 시작된 다른 코루틴들도 함께 취소되도록 보장

## 14.4 잠시 멈출 수 있는 함수: 일시 중단 함수

### 14.4.1 일시 중단 함수를 사용한 코드는 순차적으로 보인다.

```kotlin
/**
 * 여러 함수를 호출하는 블로킹 코드 작성하기
 */

fun login(credentials: Credentials): UserId
fun loadUserData(userId: UserId): UserData
fun showData(data: UserData)

fun showUserInfo(credentials: Credentials) {
    val userId = login(credentials)
    val userData = loadUserData(userId)
    showData(userData)
}
```

- login -> loadUserData -> showData 까지 순차처리가 강요된다.

```kotlin
/**
 * 일시 중단 함수를 사용해 같은 로직 수행하기
 */
suspend fun login(credentials: Credentials): UserId
suspend fun loadUserData(userId: UserId): UserData
fun showData(data: UserData)

suspend fun showUserInfo(credentials: Credentials) {
    val userId = login(credentials)
    val userData = loadUserData(userId)
    showData(userData)
}
```

- 차이는 suspend만 붙임. 함수에 suspend 변경자를 붙인 것은 실행을 잠시 멈출 수도 있다는 의미다.
- 예를 들어 네트워크 응답을 기다리는 경우 실행을 일시 중단할 수 있고, 기저 스레드를 블록시키지 않는다.

## 14.5 코루틴을 다른 접근 방법과 비교

- 콜백, 반응형 스트림, 퓨처와 비교 수행

```kotlin
/**
 * 콜백
 */
fun login(credentials: Credentials, callback: (UserId) -> Unit): UserId
fun loadUserData(userId: UserId, callback: (UserData) -> Unit): UserData
fun showData(data: UserData)

fun showUserInfo(credentials: Credentials) {
  
    login(credentials) { userId ->
      loadUserData(userId) { userData ->
        showData(userData)
      }
    }
}
```

- 콜백 지옥으로 가독성이 급격히 떨어짐
- CompletableFuture를 사용하면 콜백 중첩을 피할 수 있지만, thenCompose theAccept와 같은 새로운 연산자의 의미를 배워야 함

```kotlin
/**
 * 퓨처 사용
 */
fun login(credentials: Credentials): CompletableFuture<UserId>
fun loadUserData(userId: UserId): CompletableFuture<UserData>
fun showData(data: UserData)

fun showUserInfo(credentials: Credentials) {
  
    login(credentials)
      .thenCompose { loadUserData(it) }
      .thenAccept { showData(it) }
}
```

- 여러가지로 suspend 키워드 붙이는 것이 쉽다

### 14.5.1 일시 중단 함수 호출

- 일시 중단 함수는 실행을 일시 중단할 수 있기 때문에 일반 코드 아무 곳에서나 호출할 수는 없다.
- 일시 중단할 수 있는 코드 블록이나 다른 일시 중단 함수에서 호출 가능
- 일반 함수에서 suspend 함수를 호출하면 에러가 발생됨.

## 14.6 코루틴의 세계로 들어가기: 코루틴 빌더

- 코루틴은 일시 중단 가능한 계산의 인스턴스이다.
- 코루틴 빌더 함수
  - runBlocking: 블로킹 코드와 일시 중단 함수의 세계를 연결할 때 쓰인다
  - launch: 값을 반환하지 않는 새로운 코루틴을 시작할 때 쓰인다
  - async: 비동기적으로 값을 계산할 때 쓰인다.

### 14.6.1 일반 코드에서 코루틴의 세계로: runBlocking 함수

- runBlocking 한 코루틴이 완료될 때까지 현재 스레드를 블록함.

```kotlin
/**
 * runBlocking을 사용해 일시 중단 함수 실행하기
 */
import kotlinx.coroutines.*
import kotiln.time.Duration.Companion.milliseconds

suspend fun doSomethingSlowly() {
    delay(500.milliseconds)
    println("I'm done")
}

fun main() = runBlocking {
    doSomethingSlowly()
}
```

### 14.6.2 발사 후 망각 코루틴 생성: launch 함수

- launch 함수는 새로운 자식 코루틴을 시작하는데 사용

```kotlin
private var zeroTime = System.currentTimeMillis()
fun log(message: Any?) = println("${System.currentTimeMillis() - zeroTime}" + "[${Thread.currentThread().name}] $message")

fun main() = runBlocking {
    log("The first, parent, coroutine starts")
    launch {
        log("The second coroutine starts and is read to be suspended")
        delay(100.milliseconds)
        log("The second coroutine is resumed")
    }
    launch {
      log("The third coroutine can run in the meantime")
    }
  log("The first coroutine has launched two more coroutines")
}
```

- 결과는 아래와 같다.
```
[main coroutine#1] The first, parent, coroutine starts
[main coroutine#1] The first coroutine has launched two more coroutines
[main coroutine#2] The first, parent, coroutine startsThe second coroutine starts and is read to be suspended
[main coroutine#3] The third coroutine can run in the meantime
[main coroutine#2] The second coroutine is resumed
```

- coroutine#2 가 delay 함수를 호출하여 (일시 중단 지점) 메인 스레드는 다른 코루틴이 실행될 수 있게 해방된다. 그래서 coroutine#3가 실행됨

### 14.6.3 대기 가능한 연산: async 빌더

```kotlin
/**
 * 새 코루틴을 시작하기 위해 async 코루틴 빌더 사용하기
 */
suspend fun slowlyAddNumbers(a: Int, b: Int): Int {
    log("Waiting a bit before calculating $a + $b")
    delay(100.milliseconds * a)
    return a + b
}

fun main() = runBlocking {
    log("Starting the async computation")
    val myFirstDeferred = async { slowlyAddNumbers(2, 2) }
    val mySecondDeferred = async { slowlyAddNumbers(4, 4) }
    log("The first result: ${myFirstDeferred.await()}") // 결과를 사용할 수 있을 때까지 대기
    log("The first result: ${mySecondDeferred.await()}")
}
```

- myFirstDeferred, mySecondDeferred 은 동시에 실행됨을 확인할 수 있다.
- 자바의 Future와 비슷
- 코틀린의 특징은 결과를 기다릴 필요가 있을 때에만 일시 중단 함수 호출 시 async를 호출하고 이외에는 그냥 이용해도 됨

## 14.7 어디서 코드를 실행할지 정하기: 디스패처

- 코루틴의 디스패처는 코루틴을 실행할 스레드를 결정한다.
- 본질적으로 코루틴은 특정 스레드에 고정되지 않는다.

### 14.7.1 디스패처 선택

- 다중 스레드를 사용하는 범용 디스패처: Dispatchers.Default
- UI 스레드에서 실행: Dispatchers.Main -> UI 프레임워크에선 특정 작업을 메인 스레드라 불리는 특정 스레드에서 실행해야하는 경우가 있어서 사용
- 블로킹되는 IO 작업 처리: Dispatchers.IO -> 기본 디스패처의 스레드 수는 CPU 코어 수와 동일하기에 블로킹 작업이 있을 경우 지연발생 가능성이 있어서 CPU 집약적이지 않은 작업에 사용
- 선택 방법
  - UI 스레드가 요청됐는가? -> Dispatchers.Main
  - 코루틴에서 사용되는 블로킹 API? -> Dispatchers.IO
  - 특별한 즉시 스케줄링 사례 -> Dispatchers.Unconfined
  - 사용자 지정 병렬 처리 요구사항 -> limitedParallelism
  - Dispatchers.Default

### 14.7.2 코루틴 빌더에 디스패처 전달

```kotlin
/**
 * 코루틴 빌더의 인자로 디스패처 지정하기
 */
fun main() {
    runBlocking {
        log("Doing some work")
        launch(Dispatchers.Default) { // Default 지정
            log("Doing some background work")
        }
    }
}
```

### 14.7.3 withContext 를 사용해 코루틴 안에서 디스패처 바꾸기

```kotlin
launch(Dispatchers.Default) {
    val result = performBackgroundOperation()
    withContext(Dispatchers.Main) {
        updateUI(result)
    }
}
```

## 14.8 코루틴은 코루틴 콘텍스트에 추가적인 정보를 담고 있다

- 코루틴 디스패처는 코루틴 콘텍스트의 일부이다
- 코루틴의 콘텍스트를 확인하려면 coroutineContext라는 특별한 속성에 접근하면 된다.