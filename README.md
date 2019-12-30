# SMokK

A little bit scary library for mocking suspendable functions in Kotlin :-)

### Example

```kotlin
suspend fun getUserDetailsFast(
    userId: Int,
    fetchUserDetails: suspend (Int) -> String?,
    getCachedUserDetails: suspend (Int) -> String?,
    putCachedUserDetails: suspend (Int, String) -> Unit
): String?  = coroutineScope {
    val cached = async { getCachedUserDetails(userId) }
    val new = async { fetchUserDetails(userId) }
    cached.await()?.also { new.cancel() } ?: new.await()?.also { putCachedUserDetails(userId, it) }
}

@Test
fun getUserDetailsFastTest() {

    uspek {

        "On getUserDetailsFast" o {

            val fetchUserDetails = smokk<Int, String?>()
            val getCachedUserDetails = smokk<Int, String?>()
            val putCachedUserDetails = smokk<Int, String, Unit>()

            val deferred = GlobalScope.async(Dispatchers.Unconfined) {
                runCatching {
                    getUserDetailsFast(
                        userId = 7,
                        fetchUserDetails = fetchUserDetails::invoke,
                        getCachedUserDetails = getCachedUserDetails::invoke,
                        putCachedUserDetails = putCachedUserDetails::invoke
                    )
                }
            }

            "is active" o { deferred.isActive eq true }
            "getting cached details started" o { getCachedUserDetails.invocations eq listOf(7) }
            "fetching details started too" o { fetchUserDetails.invocations eq listOf(7) }

            "On cached details" o {
                getCachedUserDetails.resume("cached details")

                "is still active" o { deferred.isActive eq true }

                "On fetching cancelled" o { // see GetUserDetailsFastXTest for autoCancel and cancellation checking
                    fetchUserDetails.resumeWithException(CancellationException())

                    "return cached details" o { deferred.getCompleted() eq success("cached details") }
                }
            }
        }
    }
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
        testImplementation 'com.github.langara:SMokK:$smokk_version'
    }
```

details: https://jitpack.io/#langara/SMokK
