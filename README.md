# RxMock
Tiny library for mocking RxJava calls.

### Example
```kotlin
    @Test
    fun someApiCallTest() { // whole scenario in one test just for brevity
    
        val inputTextChangeS = PublishRelay.create<String>()
        val apiCall = RxMockSingle1<String, List<String>>()
        
        val resultsS = webSearch(inputTextChangeS, inputMinLength = 3, webSearchCall = apiCall).test()
        
        apiCall.invocations.size eq 0
        
        inputTextChangeS put "ab" // too short to call api
        
        apiCall.invocations.size eq 0
        
        inputTextChangeS put "abc"
        
        apiCall.invocations.size eq 1
        apiCall.invocations[0] eq "abc"
        resultsS.assertEmpty() // do not emit any search results yet
        
        val abcResults = listOf("abc is nice", "abc starts a song")
        apiCall put abcResults // simulate successful api response
        
        resultsS.assertValue(abcResults)
        
        inputTextChangeS put "abce"
        
        val abceError = IOException("Broken network connection")
        apiCall.onError(abceError) // simulate error api response for last api call
        
        resultsS.assertError(abceError)
    }

    infix fun <T> T.eq(expected: T) = Assert.assertEquals(expected, this)
    infix fun <T> Consumer<T>.put(value: T) = accept(value)
```

Full examples are available in the ```kotlinsample``` directory

[![](https://jitpack.io/v/langara/RxMock.svg)](https://jitpack.io/#langara/RxMock)

### Building with JitPack
```gradle
    repositories {
        maven { url "https://jitpack.io" }
    }
   
    dependencies {
        testImplementation 'com.github.langara:RxMock:master-SNAPSHOT'
    }
```

details: https://jitpack.io/#langara/RxMock
