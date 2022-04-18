package pl.mareklangiewicz.smokk

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokK0<T>(var invocationCheck: () -> Boolean = { true }) : Continuation<T> {

    var invocations = 0

    var continuation: Continuation<T>? = null

    suspend fun invoke(): T {
        if (!invocationCheck()) throw SMokKException("SMokK0 fail")
        invocations ++
        return suspendCoroutine { continuation = it }
    }

    override fun resumeWith(result: Result<T>) {
        val c = continuation ?: throw SMokKException("SMokK0.invoke not started")
        continuation = null
        c.resumeWith(result)
    }

    override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <T> smokk(invocationCheck: () -> Boolean = { true }) = SMokK0<T>(invocationCheck)
