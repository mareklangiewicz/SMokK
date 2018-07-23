package pl.mareklangiewicz.rxmock

import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.MaybeSubject

class RxMockMaybe2<A1, A2, T> : MaybeObserver<T>, Consumer<T>, (A1, A2) -> Maybe<T> {

    val invocations = mutableListOf<Pair<A1, A2>>()

    private lateinit var subject: MaybeSubject<T>

    override fun invoke(arg1: A1, arg2: A2): Maybe<T> {
        invocations += arg1 to arg2
        subject = MaybeSubject.create<T>()
        return subject
    }

    override fun accept(t: T) = onSuccess(t)
    override fun onSuccess(t: T) = subject.onSuccess(t)
    override fun onComplete() = subject.onComplete()
    override fun onSubscribe(d: Disposable) = subject.onSubscribe(d)
    override fun onError(e: Throwable) = subject.onError(e)
}
