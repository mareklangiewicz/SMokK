package pl.mareklangiewicz.smokk

import pl.mareklangiewicz.tuplek.Quad
import pl.mareklangiewicz.tuplek.fo
import pl.mareklangiewicz.tuplek.tre
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokK4<A1, A2, A3, A4, T>(var invocationCheck: (A1, A2, A3, A4) -> Boolean = { _, _, _, _ -> true }) : Continuation<T> {

    constructor(vararg allowedArgs: Quad<A1, A2, A3, A4>) : this({ a1, a2, a3, a4 -> a1 to a2 tre a3 fo a4 in allowedArgs })

    val invocations = mutableListOf<Quad<A1, A2, A3, A4>>()

    var continuation: Continuation<T>? = null

    suspend fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4): T {
        if (!invocationCheck(arg1, arg2, arg3, arg4)) throw SMokKException("SMokK4 fail for args: $arg1, $arg2, $arg3, $arg4")
        invocations += arg1 to arg2 tre arg3 fo arg4
        return suspendCoroutine { continuation = it }
    }

    override fun resumeWith(result: Result<T>) {
        val c = continuation ?: throw SMokKException("SMokK4.invoke not started")
        continuation = null
        c.resumeWith(result)
    }

    override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A1, A2, A3, A4, T> smokk(invocationCheck: (A1, A2, A3, A4) -> Boolean = { _, _, _, _ -> true }) = SMokK4<A1, A2, A3, A4, T>(invocationCheck)
