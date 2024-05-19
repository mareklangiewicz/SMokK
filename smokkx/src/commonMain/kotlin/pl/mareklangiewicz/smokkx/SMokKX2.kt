package pl.mareklangiewicz.smokkx

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

class SMokKX2<A1, A2, T>(var autoCancel: Boolean = false, var autoResume: (suspend (A1, A2) -> T)? = null) :
  Continuation<T> {

  val invocations = mutableListOf<Pair<A1, A2>>()

  var cancellations = 0

  var continuation: Continuation<T>? = null

  suspend fun invoke(arg1: A1, arg2: A2): T {
    invocations += arg1 to arg2
    autoResume?.let { return it(arg1, arg2) }
    return if (autoCancel) suspendCancellableCoroutine {
      continuation = it
      it.invokeOnCancellation { continuation = null; cancellations++ }
    }
    else suspendCoroutine { continuation = it }
  }

  override fun resumeWith(result: Result<T>) {
    val c = continuation ?: throw SMokKXException("SMokKX2.invoke not started")
    continuation = null
    c.resumeWith(result)
  }

  override val context: CoroutineContext get() = continuation?.context ?: EmptyCoroutineContext
}

fun <A1, A2, T> smokkx(autoCancel: Boolean = false, autoResume: (suspend (A1, A2) -> T)? = null) =
  SMokKX2<A1, A2, T>(autoCancel, autoResume)

