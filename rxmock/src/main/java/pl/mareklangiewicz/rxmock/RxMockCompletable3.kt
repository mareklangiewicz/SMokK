package pl.mareklangiewicz.rxmock

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.CompletableSubject

class RxMockCompletable3<A1, A2, A3>(var invocationCheck: (A1, A2, A3) -> Boolean = { _, _, _ -> true })
    : CompletableObserver, (A1, A2, A3) -> Completable {

    constructor(vararg allowedArgs: Triple<A1, A2, A3>) : this({ a1, a2, a3 -> Triple(a1, a2, a3) in allowedArgs })

    val invocations = mutableListOf<Triple<A1, A2, A3>>()

    var subject: CompletableSubject? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3): Completable {
        if (!invocationCheck(arg1, arg2, arg3))
            throw RxMockException("RxMockCompletable3 fail for args: $arg1, $arg2, $arg3")
        invocations += Triple(arg1, arg2, arg3)
        return CompletableSubject.create().also { subject = it }
    }

    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
