# SMokK

A little bit scary library for mocking suspendable functions in Kotlin :-)

### Example

```kotlin

    suspend fun getUserDetailsFast(
        userId: Int,
        fetchUserDetails: suspend (Int) -> String?,
        getCachedUserDetails: suspend (Int) -> String?,
        putCachedUserDetails: (Int, String) -> Unit
    ): String?  = coroutineScope {
        val cached = async { getCachedUserDetails(userId) }
        val new = async { fetchUserDetails(userId) }
        cached.await() ?: new.await()?.also { putCachedUserDetails(userId, it) }
    }


    @Test
    fun getUserDetailsFastTest() {

        val cache = mutableMapOf<Int, String>()

        val fetchUserDetails = smokk<Int, String?>()
        val getCachedUserDetails = smokk<Int, String?>()
        fun putCachedUserDetails(id: Int, details: String) { cache[id] = details }

        val job = GlobalScope.launch(Dispatchers.Unconfined) {
            val details = getUserDetailsFast(
                userId = 1,
                fetchUserDetails = fetchUserDetails::invoke,
                getCachedUserDetails = getCachedUserDetails::invoke,
                putCachedUserDetails = ::putCachedUserDetails
            )
            assert(details == "abc")
        }

        assert(job.isActive)

        getCachedUserDetails.resume(null)

        assert(job.isActive)

        fetchUserDetails.resume("abc")

        assert(job.isCompleted)

        // TODO: better tests; split to smaller cases
        // TODO: show that we can test different "race conditions"
    }





    suspend fun webSearch(
        inputTextChangeS: Observable<String>,
        inputMinLength: Int,
        webSearchCall: suspend (String) -> List<String>,
        renderResults: (List<String>) -> Unit
    ) {
        renderResults(emptyList())
        while (true) {
            val text = inputTextChangeS.awaitFirstOrNull()
            if (text === null) break
            if (text.length < inputMinLength) continue
            try {
                val result = webSearchCall(text)
                renderResults(result)
            } catch (e: RuntimeException) {
                renderResults(listOf(e.message ?: "network error"))
            }
        }
    }
    
    infix fun <T> T.eq(expected: T) = Assert.assertEquals(expected, this)
    
    @Test
    fun webSearchTest() { // one big test just for brevity
    
        val inputTextChangeS = PublishSubject.create<String>()
        val apiCall = smokk<String, List<String>>()
        
        val job = GlobalScope.launch(Dispatchers.Unconfined) {
            webSearch(inputTextChangeS, 3, apiCall::invoke) { println(it) }
        }
        
        assert(job.isActive)
        apiCall.invocations.size eq 0
        
        inputTextChangeS.onNext("") // too short text - no api call
        
        apiCall.invocations.size eq 0 // no api call
        
        inputTextChangeS.onNext("aaa")
        
        apiCall.invocations.size eq 1
        apiCall.invocations[0] eq "aaa"
        
        apiCall.resume(listOf("aaa bla", "aaa ble"))
        
        inputTextChangeS.onNext("xy") // too short text again
        
        apiCall.invocations.size eq 1 // no new api call

        inputTextChangeS.onNext("abcde")

        apiCall.invocations.size eq 2
        apiCall.invocations.last() eq "abcde"

        apiCall.resumeWithException(RuntimeException("terrible network failure"))

        inputTextChangeS.onComplete()

        assert(job.isCompleted)
    }
```

Full examples are available in the ```kotlinsample``` directory

[![](https://jitpack.io/v/langara/SMokK.svg)](https://jitpack.io/#langara/SMokK)

### Building with JitPack
```gradle
    repositories {
        maven { url "https://jitpack.io" }
    }
   
    dependencies {
        testImplementation 'com.github.langara:SMokK:0.0.2'
    }
```

details: https://jitpack.io/#langara/SMokK
