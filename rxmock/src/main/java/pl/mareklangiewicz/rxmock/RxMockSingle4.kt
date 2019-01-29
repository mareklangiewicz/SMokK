package pl.mareklangiewicz.rxmock

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.SingleSubject

class RxMockSingle4<A1, A2, A3, A4, T>(var invocationCheck: (A1, A2, A3, A4) -> Boolean = { _, _, _, _ -> true }) : SingleObserver<T>, Consumer<T>, (A1, A2, A3, A4) -> Single<T> {

    constructor(vararg allowedArgs: Quad<A1, A2, A3, A4>) : this({ a1, a2, a3, a4 -> Quad(a1, a2, a3, a4) in allowedArgs })

    val invocations = mutableListOf<Quad<A1, A2, A3, A4>>()

    var subject: SingleSubject<T>? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4): Single<T> {
        if (!invocationCheck(arg1, arg2, arg3, arg4)) throw RxMockException("RxMockSingle4 fail for args: $arg1, $arg2, $arg3, $arg4")
        invocations += Quad(arg1, arg2, arg3, arg4)
        return SingleSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject?.onSuccess(t) ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
