package pl.mareklangiewicz.smokk

import pl.mareklangiewicz.tuplek.Jackson
import pl.mareklangiewicz.tuplek.fi
import pl.mareklangiewicz.tuplek.fo
import pl.mareklangiewicz.tuplek.tre
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokK5<A1, A2, A3, A4, A5, T>(var invocationCheck: (A1, A2, A3, A4, A5) -> Boolean = { _, _, _, _, _ -> true }) :
  Continuation<T> {

  constructor(vararg allowedArgs: Jackson<A1, A2, A3, A4, A5>) : this({ a1, a2, a3, a4, a5 -> a1 to a2 tre a3 fo a4 fi a5 in allowedArgs })

  val invocations = mutableListOf<Jackson<A1, A2, A3, A4, A5>>()

  var continuation: Continuation<T>? = null

  suspend fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4, arg5: A5): T {
    if (!invocationCheck(
        arg1,
        arg2,
        arg3,
        arg4,
        arg5,
      )
    ) throw SMokKException("SMokK5 fail for args: $arg1, $arg2, $arg3, $arg4, $arg5")
    invocations += arg1 to arg2 tre arg3 fo arg4 fi arg5
    return suspendCoroutine { continuation = it }
  }

  override fun resumeWith(result: Result<T>) {
    val c = continuation ?: throw SMokKException("SMokK5.invoke not started")
    continuation = null
    c.resumeWith(result)
  }

  override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A1, A2, A3, A4, A5, T> smokk(invocationCheck: (A1, A2, A3, A4, A5) -> Boolean = { _, _, _, _, _ -> true }) =
  SMokK5<A1, A2, A3, A4, A5, T>(invocationCheck)
