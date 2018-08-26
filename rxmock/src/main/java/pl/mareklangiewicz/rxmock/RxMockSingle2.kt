package pl.mareklangiewicz.rxmock

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.SingleSubject

class RxMockSingle2<A1, A2, T>(var invocationCheck: (A1, A2) -> Boolean = { _, _ -> true })
    : SingleObserver<T>, Consumer<T>, (A1, A2) -> Single<T> {

    constructor(vararg allowedArgs: Pair<A1, A2>) : this({ a1, a2 -> a1 to a2 in allowedArgs })

    val invocations = mutableListOf<Pair<A1, A2>>()

    var subject: SingleSubject<T>? = null

    override fun invoke(arg1: A1, arg2: A2): Single<T> {
        if (!invocationCheck(arg1, arg2)) throw RxMockException("RxMockSingle2 fail for args: $arg1, $arg2")
        invocations += arg1 to arg2
        return SingleSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject?.onSuccess(t) ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
