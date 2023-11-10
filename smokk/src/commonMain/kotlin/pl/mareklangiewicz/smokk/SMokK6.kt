package pl.mareklangiewicz.smokk

import pl.mareklangiewicz.tuplek.Rainbow
import pl.mareklangiewicz.tuplek.fi
import pl.mareklangiewicz.tuplek.fo
import pl.mareklangiewicz.tuplek.sik
import pl.mareklangiewicz.tuplek.tre
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokK6<A1, A2, A3, A4, A5, A6, T>(var invocationCheck: (A1, A2, A3, A4, A5, A6) -> Boolean = { _, _, _, _, _, _ -> true }) : Continuation<T> {

    constructor(vararg allowedArgs: Rainbow<A1, A2, A3, A4, A5, A6>) : this({ a1, a2, a3, a4, a5, a6 -> a1 to a2 tre a3 fo a4 fi a5 sik a6 in allowedArgs })

    val invocations = mutableListOf<Rainbow<A1, A2, A3, A4, A5, A6>>()

    var continuation: Continuation<T>? = null

    suspend fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4, arg5: A5, arg6: A6): T {
        if (!invocationCheck(arg1, arg2, arg3, arg4, arg5, arg6)) throw SMokKException("SMokK6 fail for args: $arg1, $arg2, $arg3, $arg4, $arg5, $arg6")
        invocations += arg1 to arg2 tre arg3 fo arg4 fi arg5 sik arg6
        return suspendCoroutine { continuation = it }
    }

    override fun resumeWith(result: Result<T>) {
        val c = continuation ?: throw SMokKException("SMokK6.invoke not started")
        continuation = null
        c.resumeWith(result)
    }

    override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A1, A2, A3, A4, A5, A6, T> smokk(invocationCheck: (A1, A2, A3, A4, A5, A6) -> Boolean = { _, _, _, _, _, _ -> true }) = SMokK6<A1, A2, A3, A4, A5, A6, T>(invocationCheck)
