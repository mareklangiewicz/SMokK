package pl.mareklangiewicz.rxmock

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.MaybeSubject

class RxMockCompletable1<A>(var invocationCheck: (A) -> Boolean = { true })
    : CompletableObserver, (A) -> Completable {

    constructor(vararg allowedArgs: A) : this({ it in allowedArgs })

    val invocations = mutableListOf<A>()

    var subject: CompletableSubject? = null

    override fun invoke(arg1: A): Completable {
        if (!invocationCheck(arg1)) throw RxMockException("Rx mock invocation check failed")
        invocations += arg1
        return CompletableSubject.create().also { subject = it }
    }

    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
