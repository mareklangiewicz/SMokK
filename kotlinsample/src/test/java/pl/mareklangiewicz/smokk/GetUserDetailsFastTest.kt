package pl.mareklangiewicz.smokk

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.uspek.USpekRunner
import pl.mareklangiewicz.uspek.eq
import pl.mareklangiewicz.uspek.o
import pl.mareklangiewicz.uspek.uspek
import kotlin.Result.Companion.success
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RunWith(USpekRunner::class)
class GetUserDetailsFastTest {

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetailsFastTest() {

        uspek {

            "On getUserDetailsFast" o {

                val cache = mutableMapOf<Int, String>()

                val fetchUserDetails = smokk<Int, String?>()
                val getCachedUserDetails = smokk<Int, String?>()
                fun putCachedUserDetails(id: Int, details: String) { cache[id] = details }

                val deferred = GlobalScope.async(Dispatchers.Unconfined) {
                    runCatching {
                        getUserDetailsFast(
                            userId = 7,
                            fetchUserDetails = fetchUserDetails::invoke,
                            getCachedUserDetails = getCachedUserDetails::invoke,
                            putCachedUserDetails = ::putCachedUserDetails
                        )
                    }
                }

                "is active" o { deferred.isActive eq true }
                "getting cached details started" o { getCachedUserDetails.invocations eq listOf(7) }
                "fetching details started too" o { fetchUserDetails.invocations eq listOf(7) }

                "On cached details" o {
                    getCachedUserDetails.resume("cached details")

                    "is still active" o { deferred.isActive eq true }

                    "On fetching cancelled" o {
                        fetchUserDetails.resumeWithException(CancellationException())

                        "return cached details" o { deferred.getCompleted() eq success("cached details") }
                    }
                }

                "On no cached details" o {
                    getCachedUserDetails.resume(null)

                    "is still active" o { deferred.isActive eq true }
                    "cache is still empty" o { cache.size eq 0 }

                    "On fetched details " o {
                        fetchUserDetails.resume("details from network")

                        "is completed" o { deferred.isCompleted eq true }
                        "return fetched details" o { deferred.getCompleted() eq success("details from network") }


                        "details are also in the cache now" o { cache[7] eq "details from network" }
                    }

                    "On fetching network error" o {
                        fetchUserDetails.resumeWithException(RuntimeException("network error"))

                        "is completed" o { deferred.isCompleted eq true }
                        "return failure" o { deferred.getCompleted().exceptionOrNull()?.message eq "network error" }
                    }
                }

                "On cached details error" o {
                    getCachedUserDetails.resumeWithException(RuntimeException("cache error"))

                    "is still active" o { deferred.isActive eq true }

                    "On fetching cancelled" o {
                        fetchUserDetails.resumeWithException(CancellationException())

                        "return cache error" o { deferred.getCompleted().exceptionOrNull()?.message eq "cache error" }
                    }
                }
            }
        }
    }
}