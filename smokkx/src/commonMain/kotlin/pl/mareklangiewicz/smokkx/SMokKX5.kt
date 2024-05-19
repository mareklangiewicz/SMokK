package pl.mareklangiewicz.smokkx

import kotlinx.coroutines.suspendCancellableCoroutine
import pl.mareklangiewicz.tuplek.Jackson
import pl.mareklangiewicz.tuplek.fi
import pl.mareklangiewicz.tuplek.fo
import pl.mareklangiewicz.tuplek.tre
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokKX5<A1, A2, A3, A4, A5, T>(
  var autoCancel: Boolean = false,
  var autoResume: (suspend (A1, A2, A3, A4, A5) -> T)? = null,
) : Continuation<T> {

  val invocations = mutableListOf<Jackson<A1, A2, A3, A4, A5>>()

  var cancellations = 0

  var continuation: Continuation<T>? = null

  suspend fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4, arg5: A5): T {
    invocations += arg1 to arg2 tre arg3 fo arg4 fi arg5
    autoResume?.let { return it(arg1, arg2, arg3, arg4, arg5) }
    return if (autoCancel) suspendCancellableCoroutine {
      continuation = it
      it.invokeOnCancellation { continuation = null; cancellations++ }
    }
    else suspendCoroutine { continuation = it }
  }

  override fun resumeWith(result: Result<T>) {
    val c = continuation ?: throw SMokKXException("SMokKX5.invoke not started")
    continuation = null
    c.resumeWith(result)
  }

  override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A1, A2, A3, A4, A5, T> smokkx(
  autoCancel: Boolean = false,
  autoResume: (suspend (A1, A2, A3, A4, A5) -> T)? = null,
) = SMokKX5<A1, A2, A3, A4, A5, T>(autoCancel, autoResume)

