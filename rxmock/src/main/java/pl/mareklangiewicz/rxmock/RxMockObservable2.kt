package pl.mareklangiewicz.rxmock

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RxMockObservable2<A1, A2, T>(var invocationCheck: (A1, A2) -> Boolean = { _, _ -> true })
    : Observer<T>, Consumer<T>, (A1, A2) -> Observable<T> {

    constructor(vararg allowedArgs: Pair<A1, A2>) : this({ a1, a2 -> a1 to a2 in allowedArgs })

    val invocations = mutableListOf<Pair<A1, A2>>()

    var subject: Subject<T>? = null

    override fun invoke(arg1: A1, arg2: A2): Observable<T> {
        if (!invocationCheck(arg1, arg2)) throw RxMockException("RxMockObservable2 fail for args: $arg1, $arg2")
        invocations += arg1 to arg2
        return PublishSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onNext(t)
    override fun onNext(t: T) = subject?.onNext(t) ?: throw RxMockException()
    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
