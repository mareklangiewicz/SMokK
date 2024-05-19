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

        rendered.size eq 1
        rendered.last() eq emptyList() // renders empty results at start

        apiCall.invocations.size eq 0

        inputTextChangeS.onNext("") // too short text - no api call

        apiCall.invocations.size eq 0 // no api call

        inputTextChangeS.onNext("aaa")

        apiCall.invocations.size eq 1
        apiCall.invocations[0] eq "aaa"

        val results1 = listOf("aaa bla", "aaa ble")
        apiCall.resume(results1)

        rendered.size eq 2
        rendered.last() eq results1

        inputTextChangeS.onNext("xy") // too short text again

        apiCall.invocations.size eq 1 // no new api call

        inputTextChangeS.onNext("abcde")

        apiCall.invocations.size eq 2
        apiCall.invocations.last() eq "abcde"

        rendered.size eq 2 // do not render anything new yet

        apiCall.resumeWithException(RuntimeException("terrible network failure"))

        rendered.size eq 3
        rendered.last() eq listOf("terrible network failure")

        inputTextChangeS.onNext("xyz")

        apiCall.invocations.size eq 3
        apiCall.invocations.last() eq "xyz"

        rendered.size eq 3 // do not render anything new yet

        val results2 = listOf("xyz bla bla bla", "xyz ble ble ble")
        apiCall.resume(results2)

        rendered.size eq 4
        rendered.last() eq results2

        assert(job.isActive)

        inputTextChangeS.onComplete()

        assert(job.isCompleted)
      }
    }
  }
}
