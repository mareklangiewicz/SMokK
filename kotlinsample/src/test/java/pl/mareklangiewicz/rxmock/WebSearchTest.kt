package pl.mareklangiewicz.rxmock

import com.jakewharton.rxrelay2.PublishRelay
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.uspek.USpek.o
import pl.mareklangiewicz.uspek.USpek.uspek
import pl.mareklangiewicz.uspek.USpekJUnitRunner
import pl.mareklangiewicz.uspek.eq
import java.io.IOException

@RunWith(USpekJUnitRunner::class)
class WebSearchTest {

    @Test
    fun webSearchTest() {

        uspek("web search test") {

            val inputTextChangeS = PublishRelay.create<String>()

            val apiCall = RxMockSingle1<String, List<String>>()

            val resultsS = webSearch(inputTextChangeS, inputMinLength = 3, webSearchCall = apiCall).test()

            "On empty text" o {
                inputTextChangeS put ""

                "do not call api" o { apiCall.invocations.size eq 0 }
            }

            "On short 2 letters text" o {
                inputTextChangeS put "ab"

                "do not call api" o { apiCall.invocations.size eq 0 }

                "On adding third letter" o {
                    inputTextChangeS put "abc"

                    "call api with current text" o {
                        apiCall.invocations.size eq 1
                        apiCall.invocations[0] eq "abc"
                    }

                    "On the same input again" o {
                        inputTextChangeS put "abc"

                        "do not call api again" o { apiCall.invocations.size eq 1 }
                    }

                    "do not emit any search results yet" o { resultsS.assertEmpty() }

                    "On successful search results" o {
                        val abcResults = listOf("abc is nice", "abc starts a song")
                        apiCall put abcResults

                        "emit call results" o { resultsS isNow abcResults }

                        "On adding new x letter" o {
                            inputTextChangeS put "abcx"

                            "call api second time" o { apiCall.invocations.size eq 2 }
                            "do not emit new search results yet" o { resultsS.assertValueCount(1) }

                            "On new successful search results" o {
                                val abcxResults = listOf("abcx is random", "no interesting results")
                                apiCall put abcxResults

                                "emit call results" o { resultsS isNow abcxResults }
                            }

                            "On api error" o {
                                val exception = IOException("Unexpected api problem")
                                apiCall.onError(exception)

                                "forward error to results stream" o { resultsS.assertError(exception) }
                            }
                        }
                    }
                }
            }
        }
    }
}