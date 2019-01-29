package pl.mareklangiewicz.rxmock

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.CompletableSubject

class RxMockCompletable5<A1, A2, A3, A4, A5>(var invocationCheck: (A1, A2, A3, A4, A5) -> Boolean = { _, _, _, _, _ -> true })
    : CompletableObserver, (A1, A2, A3, A4, A5) -> Completable {

    constructor(vararg allowedArgs: Jackson<A1, A2, A3, A4, A5>) : this({ a1, a2, a3, a4, a5 -> Jackson(a1, a2, a3, a4, a5) in allowedArgs })

    val invocations = mutableListOf<Jackson<A1, A2, A3, A4, A5>>()

    var subject: CompletableSubject? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4, arg5: A5): Completable {
        if (!invocationCheck(arg1, arg2, arg3, arg4, arg5))
            throw RxMockException("RxMockCompletable5 fail for args: $arg1, $arg2, $arg3, $arg4, $arg5")
        invocations += Jackson(arg1, arg2, arg3, arg4, arg5)
        return CompletableSubject.create().also { subject = it }
    }

    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
