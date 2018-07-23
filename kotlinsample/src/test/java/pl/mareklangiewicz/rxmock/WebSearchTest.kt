package pl.mareklangiewicz.rxmock

import com.jakewharton.rxrelay2.PublishRelay
import org.junit.Test
import pl.mareklangiewicz.uspek.USpek.uspek
import pl.mareklangiewicz.uspek.USpek.o

class WebSearchTest {

    @Test
    fun webSearchTest() {

        uspek("web search test") {

            val inputTextChangeS = PublishRelay.create<String>()

            val webSearchCall = RxMockSingle1<String, List<String>>()

            val resultS = webSearch(inputTextChangeS, 3, webSearchCall)

            val resultObserver = resultS.test()

            "On empty text" o {

            }
        }

    }
}