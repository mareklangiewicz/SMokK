package pl.mareklangiewicz.rxmock

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.SingleSubject

class RxMockSingle0<T> : SingleObserver<T>, Consumer<T>, () -> Single<T> {

    var invocations = 0

    private lateinit var subject: SingleSubject<T>

    override fun invoke(): Single<T> {
        invocations ++
        subject = SingleSubject.create<T>()
        return subject
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject.onSuccess(t)
    override fun onSubscribe(d: Disposable) = subject.onSubscribe(d)
    override fun onError(e: Throwable) = subject.onError(e)
}
