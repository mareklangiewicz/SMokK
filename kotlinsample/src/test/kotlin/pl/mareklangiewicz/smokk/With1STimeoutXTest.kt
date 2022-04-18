package pl.mareklangiewicz.smokk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.smokkx.smokkx
import pl.mareklangiewicz.uspek.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
class With1STimeoutXTest {

    @TestFactory
    fun with1STimeoutTest() = uspekTestFactory {

        "Test X with1sTimeout" o {

            "On launch code block with1STimeout" o {

                val delay1s = smokkx<Unit>(autoCancel = true)

                val block = smokkx<Unit>(autoCancel = true)

                val job = GlobalScope.launch(Dispatchers.Unconfined) {
                    with1STimeout(delay1s::invoke, block::invoke)
                }

                "job is active" o { job.isActive eq true }
                "block has started" o { block.invocations eq 1 }
                "delay1s has started" o { delay1s.invocations eq 1 }

                "On block completion" o {
                    block.resume(Unit)

                    "delay1s is cancelled" o { delay1s.cancellations eq 1 }
                    "job is not cancelled" o { job.isCancelled eq false }
                    "job is completed" o { job.isCompleted eq true }
                }

                "On block exception" o {
                    block.resumeWithException(RuntimeException())

                    "delay1s is cancelled" o { delay1s.cancellations eq 1 }
                    "job is cancelled" o { job.isCancelled eq true }
                }

                "On 1s timeout" o {
                    delay1s.resume(Unit)

                    "block is cancelled" o { block.cancellations eq 1 }
                    "job is not cancelled" o { job.isCancelled eq false }
                    "job is completed" o { job.isCompleted eq true }
                }
            }
        }
    }
}