package pl.mareklangiewicz.rxmock

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.SingleSubject

class RxMockSingle2<A1, A2, T> : SingleObserver<T>, Consumer<T>, (A1, A2) -> Single<T> {

    val invocations = mutableListOf<Pair<A1, A2>>()

    private lateinit var subject: SingleSubject<T>

    override fun invoke(arg1: A1, arg2: A2): Single<T> {
        invocations += arg1 to arg2
        subject = SingleSubject.create<T>()
        return subject
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject.onSuccess(t)
    override fun onSubscribe(d: Disposable) = subject.onSubscribe(d)
    override fun onError(e: Throwable) = subject.onError(e)
}
