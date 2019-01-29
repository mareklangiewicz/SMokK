package pl.mareklangiewicz.rxmock

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.SingleSubject

class RxMockSingle5<A1, A2, A3, A4, A5, T>(var invocationCheck: (A1, A2, A3, A4, A5) -> Boolean = { _, _, _, _, _ -> true }) : SingleObserver<T>, Consumer<T>, (A1, A2, A3, A4, A5) -> Single<T> {

    constructor(vararg allowedArgs: Jackson<A1, A2, A3, A4, A5>) : this({ a1, a2, a3, a4, a5 -> Jackson(a1, a2, a3, a4, a5) in allowedArgs })

    val invocations = mutableListOf<Jackson<A1, A2, A3, A4, A5>>()

    var subject: SingleSubject<T>? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4, arg5: A5): Single<T> {
        if (!invocationCheck(arg1, arg2, arg3, arg4, arg5)) throw RxMockException("RxMockSingle5 fail for args: $arg1, $arg2, $arg3, $arg4, $arg5")
        invocations += Jackson(arg1, arg2, arg3, arg4, arg5)
        return SingleSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject?.onSuccess(t) ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
