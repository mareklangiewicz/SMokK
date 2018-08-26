package pl.mareklangiewicz.rxmock

import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.MaybeSubject
import io.reactivex.subjects.SingleSubject

class RxMockMaybe0<T> : MaybeObserver<T>, Consumer<T>, () -> Maybe<T> {
    var invocations = 0

    private lateinit var subject: MaybeSubject<T>

    override fun invoke(): Maybe<T> {
        invocations ++
        subject = MaybeSubject.create<T>()
        return subject
    }

    override fun accept(t: T) = onSuccess(t)

    override fun onSuccess(t: T) = subject.onSuccess(t)
    override fun onComplete() = subject.onComplete()
    override fun onSubscribe(d: Disposable) = subject.onSubscribe(d)
    override fun onError(e: Throwable) = subject.onError(e)
}