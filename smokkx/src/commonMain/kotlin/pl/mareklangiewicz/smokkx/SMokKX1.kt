package pl.mareklangiewicz.smokkx

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokKX1<A, T>(var autoCancel: Boolean = false, var autoResume: (suspend (A) -> T)? = null) : Continuation<T> {

    val invocations = mutableListOf<A>()

    var cancellations = 0

    var continuation: Continuation<T>? = null

    suspend fun invoke(arg: A): T {
        invocations += arg
        autoResume?.let { return it(arg) }
        return if (autoCancel) suspendCancellableCoroutine {
            continuation = it
            it.invokeOnCancellation { continuation = null; cancellations ++ }
        }
        else suspendCoroutine { continuation = it }
    }

    override fun resumeWith(result: Result<T>) {
        val c = continuation ?: throw SMokKXException("SMokKX1.invoke not started")
        continuation = null
        c.resumeWith(result)
    }

    override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A, T> smokkx(autoCancel: Boolean = false, autoResume: (suspend (A) -> T)? = null) = SMokKX1<A, T>(autoCancel, autoResume)
