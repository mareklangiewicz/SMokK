package pl.mareklangiewicz.rxmock

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RxMockObservable1<A, T> : Observer<T>, Consumer<T>, (A) -> Observable<T> {

    val invocations = mutableListOf<A>()

    private lateinit var subject: Subject<T>

    override fun invoke(arg1: A): Observable<T> {
        invocations += arg1
        subject = PublishSubject.create<T>()
        return subject
    }

    override fun accept(t: T) = onNext(t)
    override fun onNext(t: T) = subject.onNext(t)
    override fun onComplete() = subject.onComplete()
    override fun onSubscribe(d: Disposable) = subject.onSubscribe(d)
    override fun onError(e: Throwable) = subject.onError(e)
}
