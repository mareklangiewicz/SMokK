package pl.mareklangiewicz.smokk

import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.*
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(DelicateCoroutinesApi::class)
@ExperimentalCoroutinesApi
class WebSearchTest {

  @TestFactory
  fun webSearchTest() = uspekTestFactory {

    "Test webSearch" o {

      "On one huge webSearch test" o { // TODO: use suspek and split into small test cases

        val inputTextChangeS = PublishSubject.create<String>()

        val apiCall = smokk<String, List<String>>()

        val rendered = mutableListOf<List<String>>()

        fun render(results: List<String>) {
          rendered += results
        }

        val job = GlobalScope.launch(Dispatchers.Unconfined) {
          webSearch(
              inputTextChangeS = inputTextChangeS,
              inputMinLength = 3,
              webSearchCall = apiCall::invoke,
              renderResults = ::render,
          )
        }

        assert(job.isActive)

        rendered.size chkEq 1
        rendered.last() chkEq emptyList() // renders empty results at start

        apiCall.invocations.size chkEq 0

        inputTextChangeS.onNext("") // too short text - no api call

        apiCall.invocations.size chkEq 0 // no api call

        inputTextChangeS.onNext("aaa")

        apiCall.invocations.size chkEq 1
        apiCall.invocations[0] chkEq "aaa"

        val results1 = listOf("aaa bla", "aaa ble")
        apiCall.resume(results1)

        rendered.size chkEq 2
        rendered.last() chkEq results1

        inputTextChangeS.onNext("xy") // too short text again

        apiCall.invocations.size chkEq 1 // no new api call

        inputTextChangeS.onNext("abcde")

        apiCall.invocations.size chkEq 2
        apiCall.invocations.last() chkEq "abcde"

        rendered.size chkEq 2 // do not render anything new yet

        apiCall.resumeWithException(RuntimeException("terrible network failure"))

        rendered.size chkEq 3
        rendered.last() chkEq listOf("terrible network failure")

        inputTextChangeS.onNext("xyz")

        apiCall.invocations.size chkEq 3
        apiCall.invocations.last() chkEq "xyz"

        rendered.size chkEq 3 // do not render anything new yet

        val results2 = listOf("xyz bla bla bla", "xyz ble ble ble")
        apiCall.resume(results2)

        rendered.size chkEq 4
        rendered.last() chkEq results2

        assert(job.isActive)

        inputTextChangeS.onComplete()

        assert(job.isCompleted)
      }
    }
  }
}
