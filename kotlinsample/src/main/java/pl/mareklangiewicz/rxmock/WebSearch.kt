package pl.mareklangiewicz.rxmock

import io.reactivex.Observable
import io.reactivex.Single

fun webSearch(
    inputTextChangeS: Observable<String>,
    inputMinLength: Int,
    webSearchCall: (String) -> Single<List<String>>
): Observable<List<String>> =
    inputTextChangeS
        .filter { it.length >= inputMinLength }
        .distinctUntilChanged()
        .switchMapSingle(webSearchCall)


// TODO: add new version (wrapper) with debouncing and with parametrized scheduler, and test it with advanceTimeBy...

// TODO: another wrapper that wraps the call itself and adds some retry on timeout error, and test this behavior