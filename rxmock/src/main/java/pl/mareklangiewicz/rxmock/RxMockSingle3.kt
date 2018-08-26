package pl.mareklangiewicz.rxmock

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.SingleSubject

class RxMockSingle3<A1, A2, A3, T>(var invocationCheck: (A1, A2, A3) -> Boolean = { _, _, _ -> true })
    : SingleObserver<T>, Consumer<T>, (A1, A2, A3) -> Single<T> {

    constructor(vararg allowedArgs: Triple<A1, A2, A3>) : this({ a1, a2, a3 -> Triple(a1, a2, a3) in allowedArgs })

    val invocations = mutableListOf<Triple<A1, A2, A3>>()

    var subject: SingleSubject<T>? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3): Single<T> {
        if (!invocationCheck(arg1, arg2, arg3)) throw RxMockException("RxMockSingle3 fail for args: $arg1, $arg2, $arg3")
        invocations += Triple(arg1, arg2, arg3)
        return SingleSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject?.onSuccess(t) ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
