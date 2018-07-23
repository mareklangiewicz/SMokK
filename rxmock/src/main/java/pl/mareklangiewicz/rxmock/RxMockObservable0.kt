package pl.mareklangiewicz.rxmock

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RxMockObservable0<T> : Observer<T>, Consumer<T>, () -> Observable<T> {

    var invocations = 0

    private lateinit var subject: Subject<T>

    override fun invoke(): Observable<T> {
        invocations ++
        subject = PublishSubject.create<T>()
        return subject
    }

    override fun accept(t: T) = onNext(t)
    override fun onNext(t: T) = subject.onNext(t)
    override fun onComplete() = subject.onComplete()
    override fun onSubscribe(d: Disposable) = subject.onSubscribe(d)
    override fun onError(e: Throwable) = subject.onError(e)
}
