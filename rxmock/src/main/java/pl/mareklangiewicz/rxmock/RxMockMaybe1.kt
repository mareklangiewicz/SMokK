package pl.mareklangiewicz.rxmock

import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.MaybeSubject

class RxMockMaybe1<A, T>(var invocationCheck: (A) -> Boolean = { true })
    : MaybeObserver<T>, Consumer<T>, (A) -> Maybe<T> {

    constructor(vararg allowedArgs: A) : this({ it in allowedArgs })

    val invocations = mutableListOf<A>()

    var subject: MaybeSubject<T>? = null

    override fun invoke(arg: A): Maybe<T> {
        if (!invocationCheck(arg)) throw RxMockException("RxMockMaybe1 fail for arg: $arg")
        invocations += arg
        return MaybeSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject?.onSuccess(t) ?: throw RxMockException()
    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
