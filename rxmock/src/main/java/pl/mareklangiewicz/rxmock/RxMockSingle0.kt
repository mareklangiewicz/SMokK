package pl.mareklangiewicz.rxmock

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.SingleSubject

class RxMockSingle0<T>(var invocationCheck: () -> Boolean = { true })
    : SingleObserver<T>, Consumer<T>, () -> Single<T> {

    var invocations = 0

    var subject: SingleSubject<T>? = null

    override fun invoke(): Single<T> {
        if (!invocationCheck()) throw RxMockException("RxMockSingle0 fail")
        invocations ++
        return SingleSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject?.onSuccess(t) ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
