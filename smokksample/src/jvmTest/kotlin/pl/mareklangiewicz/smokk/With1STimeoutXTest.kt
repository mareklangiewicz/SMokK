package pl.mareklangiewicz.smokk

import kotlinx.coroutines.*
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.smokkx.smokkx
import pl.mareklangiewicz.uspek.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(DelicateCoroutinesApi::class)
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

        "job is active" o { job.isActive chkEq true }
        "block has started" o { block.invocations chkEq 1 }
        "delay1s has started" o { delay1s.invocations chkEq 1 }

        "On block completion" o {
          block.resume(Unit)

          "delay1s is cancelled" o { delay1s.cancellations chkEq 1 }
          "job is not cancelled" o { job.isCancelled chkEq false }
          "job is completed" o { job.isCompleted chkEq true }
        }

        "On block exception" o {
          block.resumeWithException(RuntimeException())

          "delay1s is cancelled" o { delay1s.cancellations chkEq 1 }
          "job is cancelled" o { job.isCancelled chkEq true }
        }

        "On 1s timeout" o {
          delay1s.resume(Unit)

          "block is cancelled" o { block.cancellations chkEq 1 }
          "job is not cancelled" o { job.isCancelled chkEq false }
          "job is completed" o { job.isCompleted chkEq true }
        }
      }
    }
  }
}
