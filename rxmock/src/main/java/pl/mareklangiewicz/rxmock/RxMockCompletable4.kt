package pl.mareklangiewicz.rxmock

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.CompletableSubject

class RxMockCompletable4<A1, A2, A3, A4>(var invocationCheck: (A1, A2, A3, A4) -> Boolean = { _, _, _, _ -> true })
    : CompletableObserver, (A1, A2, A3, A4) -> Completable {

    constructor(vararg allowedArgs: Quad<A1, A2, A3, A4>) : this({ a1, a2, a3, a4 -> Quad(a1, a2, a3, a4) in allowedArgs })

    val invocations = mutableListOf<Quad<A1, A2, A3, A4>>()

    var subject: CompletableSubject? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4): Completable {
        if (!invocationCheck(arg1, arg2, arg3, arg4))
            throw RxMockException("RxMockCompletable4 fail for args: $arg1, $arg2, $arg3, $arg4")
        invocations += Quad(arg1, arg2, arg3, arg4)
        return CompletableSubject.create().also { subject = it }
    }

    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
