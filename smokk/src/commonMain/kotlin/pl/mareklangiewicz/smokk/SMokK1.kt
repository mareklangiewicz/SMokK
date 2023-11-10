package pl.mareklangiewicz.smokk

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokK1<A, T>(var invocationCheck: (A) -> Boolean = { _ -> true }) : Continuation<T> {

    constructor(vararg allowedArgs: A) : this({ it in allowedArgs })

    val invocations = mutableListOf<A>()

    var continuation: Continuation<T>? = null

    suspend fun invoke(arg: A): T {
        if (!invocationCheck(arg)) throw SMokKException("SMokK1 fail for arg: $arg")
        invocations += arg
        return suspendCoroutine { continuation = it }
    }

    override fun resumeWith(result: Result<T>) {
        val c = continuation ?: throw SMokKException("SMokK1.invoke not started")
        continuation = null
        c.resumeWith(result)
    }

    override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A, T> smokk(invocationCheck: (A) -> Boolean = { true }) = SMokK1<A, T>(invocationCheck)
