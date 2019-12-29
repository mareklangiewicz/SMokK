package pl.mareklangiewicz.smokk

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun with1STimeout(
    delay1s: suspend () -> Unit = { delay(1000) },
    block: suspend () -> Unit
) = coroutineScope {
    val job1 = launch { block() }
    val job2 = launch { delay1s() }
    job1.invokeOnCompletion { job2.cancel() }
    job2.invokeOnCompletion { job1.cancel() }
}
