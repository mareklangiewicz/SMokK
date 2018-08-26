package pl.mareklangiewicz.rxmock

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.CompletableSubject

class RxMockCompletable2<A1, A2>(var invocationCheck: (A1, A2) -> Boolean = { _, _ -> true })
    : CompletableObserver, (A1, A2) -> Completable {

    constructor(vararg allowedArgs: Pair<A1, A2>) : this({ a1, a2 -> a1 to a2 in allowedArgs })

    val invocations = mutableListOf<Pair<A1, A2>>()

    var subject: CompletableSubject? = null

    override fun invoke(arg1: A1, arg2: A2): Completable {
        if (!invocationCheck(arg1, arg2)) throw RxMockException("RxMockCompletable2 fail for args: $arg1, $arg2")
        invocations += arg1 to arg2
        return CompletableSubject.create().also { subject = it }
    }

    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
