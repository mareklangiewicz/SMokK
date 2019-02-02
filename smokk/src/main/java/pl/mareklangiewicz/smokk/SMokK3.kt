package pl.mareklangiewicz.smokk

import pl.mareklangiewicz.tuplek.tre
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokK3<A1, A2, A3, T>(var invocationCheck: (A1, A2, A3) -> Boolean = { _, _, _ -> true }) : Continuation<T> {

    constructor(vararg allowedArgs: Triple<A1, A2, A3>) : this({ a1, a2, a3 -> a1 to a2 tre a3 in allowedArgs })

    val invocations = mutableListOf<Triple<A1, A2, A3>>()

    var continuation: Continuation<T>? = null

    suspend fun invoke(arg1: A1, arg2: A2, arg3: A3): T {
        if (!invocationCheck(arg1, arg2, arg3)) throw SMokKException("SMokK3 fail for args: $arg1, $arg2, $arg3")
        invocations += arg1 to arg2 tre arg3
        return suspendCoroutine { continuation = it }
    }

    override fun resumeWith(result: Result<T>) {
        val c = continuation ?: throw SMokKException("SMokK3.invoke not started")
        continuation = null
        c.resumeWith(result)
    }

    override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A1, A2, A3, T> smokk(invocationCheck: (A1, A2, A3) -> Boolean = { _, _, _ -> true }) = SMokK3<A1, A2, A3, T>(invocationCheck)
