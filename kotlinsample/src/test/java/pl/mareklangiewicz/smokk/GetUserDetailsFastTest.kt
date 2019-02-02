package pl.mareklangiewicz.smokk

import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.uspek.o
import pl.mareklangiewicz.uspek.uspek
import pl.mareklangiewicz.uspek.USpekRunner
import pl.mareklangiewicz.uspek.eq
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RunWith(USpekRunner::class)
class GetUserDetailsFastTest {

    @Test
    fun getUserDetailsFastTest() {

        uspek {

            "On getUserDetailsFast" o { // TODO: split test into smaller nested scenarios with suspek

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

                // TODO: better tests
                // TODO: show that we can test different "race conditions"
            }
        }
    }
}