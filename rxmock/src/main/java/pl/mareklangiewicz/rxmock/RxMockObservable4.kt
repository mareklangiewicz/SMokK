package pl.mareklangiewicz.rxmock

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RxMockObservable4<A1, A2, A3, A4, T>(var invocationCheck: (A1, A2, A3, A4) -> Boolean = { _, _, _, _ -> true }) : Observer<T>, Consumer<T>, (A1, A2, A3, A4) -> Observable<T> {

    constructor(vararg allowedArgs: Quad<A1, A2, A3, A4>) : this({ a1, a2, a3, a4 -> Quad(a1, a2, a3, a4) in allowedArgs })

    val invocations = mutableListOf<Quad<A1, A2, A3, A4>>()

    var subject: Subject<T>? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3, arg4: A4): Observable<T> {
        if (!invocationCheck(arg1, arg2, arg3, arg4)) throw RxMockException("RxMockObservable4 fail for args: $arg1, $arg2, $arg3, $arg4")
        invocations += Quad(arg1, arg2, arg3, arg4)
        return PublishSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onNext(t)
    override fun onNext(t: T) = subject?.onNext(t) ?: throw RxMockException()
    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
