package pl.mareklangiewicz.smokk

import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirstOrNull

suspend fun webSearch(
    inputTextChangeS: Observable<String>,
    inputMinLength: Int,
    webSearchCall: suspend (String) -> List<String>,
    renderResults: (List<String>) -> Unit
) {
    renderResults(emptyList())
    while (true) {
        val text = inputTextChangeS.awaitFirstOrNull()
        if (text === null) break
        if (text.length < inputMinLength) continue
        try {
            val result = webSearchCall(text)
            renderResults(result)
        } catch (e: RuntimeException) {
            renderResults(listOf(e.message ?: "network error"))
        }
    }
}
