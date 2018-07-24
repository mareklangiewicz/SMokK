package pl.mareklangiewicz.rxmock

import com.jakewharton.rxrelay2.PublishRelay
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.uspek.USpek.o
import pl.mareklangiewicz.uspek.USpek.uspek
import pl.mareklangiewicz.uspek.USpekJUnitRunner
import pl.mareklangiewicz.uspek.eq

@RunWith(USpekJUnitRunner::class)
class WebSearchTest {

    @Test
    fun webSearchTest() {

        uspek("web search test") {

            val inputTextChangeS = PublishRelay.create<String>()

            val apiCall = RxMockSingle1<String, List<String>>()

            val resultS = webSearch(inputTextChangeS, 3, apiCall).test()

            "On empty text" o {
                inputTextChangeS put "fjdskl"

                "do not call api" o { apiCall.invocations eq 1 }
            }
        }
    }
}