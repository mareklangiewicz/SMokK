package pl.mareklangiewicz.rxmock

import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.MaybeSubject

// TODO NOW: add invocationCheck to all RxMock classes and test it
// (it makes sense for parameterless versions too!)
// TODO NOW: make subject public in all RxMock classes - we want to allow easy hacking in tests

class RxMockMaybe1<A, T>(var invocationCheck: (A) -> Boolean = { true })
    : MaybeObserver<T>, Consumer<T>, (A) -> Maybe<T> {

    constructor(vararg allowedArgs: A) : this({ it in allowedArgs })

    val invocations = mutableListOf<A>()

    var subject: MaybeSubject<T>? = null

    override fun invoke(arg1: A): Maybe<T> {
        if (!invocationCheck(arg1)) throw RxMockException("Rx mock invocation check failed")
        invocations += arg1
        return MaybeSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject?.onSuccess(t) ?: throw RxMockException()
    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
