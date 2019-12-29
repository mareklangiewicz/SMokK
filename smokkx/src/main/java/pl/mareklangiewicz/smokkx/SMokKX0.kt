package pl.mareklangiewicz.smokkx

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokKX0<T>(var autoCancel: Boolean = false, var autoResume: (suspend () -> T)? = null) : Continuation<T> {

    var invocations = 0

    var continuation: Continuation<T>? = null

    var cancellations = 0

    suspend operator fun invoke(): T {
        invocations ++
        autoResume?.let { return it() }
        return if (autoCancel) suspendCancellableCoroutine {
            continuation = it
            it.invokeOnCancellation { continuation = null; cancellations ++ }
        }
        else suspendCoroutine { continuation = it }
    }

    override fun resumeWith(result: Result<T>) {
        val c = continuation ?: throw SMokKXException("SMokKX0.invoke not started")
        continuation = null
        c.resumeWith(result)
    }

    override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <T> smokkx(autoCancel: Boolean = false, autoResume: (suspend () -> T)? = null) = SMokKX0<T>(autoCancel, autoResume)
