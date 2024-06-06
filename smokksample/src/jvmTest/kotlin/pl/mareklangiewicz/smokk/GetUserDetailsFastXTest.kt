package pl.mareklangiewicz.smokk

import kotlinx.coroutines.*
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.smokkx.smokkx
import pl.mareklangiewicz.uspek.*
import kotlin.Result.Companion.success
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(DelicateCoroutinesApi::class)
class GetUserDetailsFastXTest {

  @ExperimentalCoroutinesApi
  @TestFactory
  fun getUserDetailsFastXTest() = uspekTestFactory {

    "Test X getUserDetailsFast" o {

      "On getUserDetailsFast" o {

        val fetchUserDetails = smokkx<Int, String?>(autoCancel = true)
        val getCachedUserDetails = smokkx<Int, String?>(autoCancel = true)
        val putCachedUserDetails = smokkx<Int, String, Unit>(autoCancel = true) { _, _ -> Unit }

        val deferred = GlobalScope.async(Dispatchers.Unconfined) {
          runCatching {
            getUserDetailsFast(
              userId = 7,
              fetchUserDetails = fetchUserDetails::invoke,
              getCachedUserDetails = getCachedUserDetails::invoke,
              putCachedUserDetails = putCachedUserDetails::invoke,
            )
          }
        }

        "is active" o { deferred.isActive chkEq true }
        "getting cached details started" o { getCachedUserDetails.invocations chkEq listOf(7) }
        "fetching details started too" o { fetchUserDetails.invocations chkEq listOf(7) }

        "On cached details" o {
          getCachedUserDetails.resume("cached details")

          "fetching is cancelled" o { fetchUserDetails.cancellations chkEq 1 }
          "return cached details" o { deferred.getCompleted() chkEq success("cached details") }
        }

        "On no cached details" o {
          getCachedUserDetails.resume(null)

          "is still active" o { deferred.isActive chkEq true }

          "On fetched details " o {
            fetchUserDetails.resume("details from network")

            "put fetched details to cache" o {
              putCachedUserDetails.invocations chkEq listOf(7 to "details from network")
              // putCachedUserDetails resumes immediately thanks to autoResume
            }
            "return fetched details" o { deferred.getCompleted() chkEq success("details from network") }
          }

          "On fetching network error" o {
            fetchUserDetails.resumeWithException(RuntimeException("network error"))

            "do not put anything to cache" o { putCachedUserDetails.invocations.size chkEq 0 }
            "is completed" o { deferred.isCompleted chkEq true }
            "return failure" o { deferred.getCompleted().exceptionOrNull()?.message chkEq "network error" }
          }

        }

        "On cancel whole thing from outside" o {
          deferred.cancel()

          "getting cached details is cancelled" o { getCachedUserDetails.cancellations chkEq 1 }
        }

        "On cached details error" o {
          getCachedUserDetails.resumeWithException(RuntimeException("cache error"))

          "fetching is cancelled" o { fetchUserDetails.cancellations chkEq 1 }
          "return cache error" o { deferred.getCompleted().exceptionOrNull()?.message chkEq "cache error" }
        }
      }
    }
  }
}

