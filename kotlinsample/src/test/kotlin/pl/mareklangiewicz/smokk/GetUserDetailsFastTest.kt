package pl.mareklangiewicz.smokk

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import pl.mareklangiewicz.uspek.*
import kotlin.Result.Companion.success
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(DelicateCoroutinesApi::class)
class GetUserDetailsFastTest {

    @ExperimentalCoroutinesApi
    @TestFactory
    fun getUserDetailsFastTest() = uspekTestFactory {

        "Test getUserDetailsFast" o {

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

                "On no cached details" o {
                    getCachedUserDetails.resume(null)

                    "is still active" o { deferred.isActive eq true }

                    "On fetched details " o {
                        fetchUserDetails.resume("details from network")

                        "put fetched details to cache" o {
                            putCachedUserDetails.invocations eq listOf(7 to "details from network")
                        }
                        "On putting to cache finish" o { // see GetUserDetailsFastXTest for mocking with autoResume
                            putCachedUserDetails.resume(Unit)

                            "return fetched details" o { deferred.getCompleted() eq success("details from network") }
                        }
                    }

                    "On fetching network error" o {
                        fetchUserDetails.resumeWithException(RuntimeException("network error"))

                        "do not put anything to cache" o { putCachedUserDetails.invocations.size eq 0 }
                        "is completed" o { deferred.isCompleted eq true }
                        "return failure" o { deferred.getCompleted().exceptionOrNull()?.message eq "network error" }
                    }
                }

                "On cached details error" o {
                    getCachedUserDetails.resumeWithException(RuntimeException("cache error"))

                    "is still active" o { deferred.isActive eq true }

                    "On fetching cancelled" o { // see GetUserDetailsFastXTest for autoCancel and cancellation checking
                        fetchUserDetails.resumeWithException(CancellationException())

                        "return cache error" o { deferred.getCompleted().exceptionOrNull()?.message eq "cache error" }
                    }
                }
            }
        }
    }
}