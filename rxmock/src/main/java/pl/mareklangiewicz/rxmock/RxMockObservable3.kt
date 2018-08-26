package pl.mareklangiewicz.rxmock

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RxMockObservable3<A1, A2, A3, T>(var invocationCheck: (A1, A2, A3) -> Boolean = { _, _, _ -> true })
    : Observer<T>, Consumer<T>, (A1, A2, A3) -> Observable<T> {

    constructor(vararg allowedArgs: Triple<A1, A2, A3>) : this({ a1, a2, a3 -> Triple(a1, a2, a3) in allowedArgs })

    val invocations = mutableListOf<Triple<A1, A2, A3>>()

    var subject: Subject<T>? = null

    override fun invoke(arg1: A1, arg2: A2, arg3: A3): Observable<T> {
        if (!invocationCheck(arg1, arg2, arg3)) throw RxMockException("RxMockObservable3 fail for args: $arg1, $arg2, $arg3")
        invocations += Triple(arg1, arg2, arg3)
        return PublishSubject.create<T>().also { subject = it }
    }

    override fun accept(t: T) = onNext(t)
    override fun onNext(t: T) = subject?.onNext(t) ?: throw RxMockException()
    override fun onComplete() = subject?.onComplete() ?: throw RxMockException()
    override fun onSubscribe(d: Disposable) = subject?.onSubscribe(d) ?: throw RxMockException()
    override fun onError(e: Throwable) = subject?.onError(e) ?: throw RxMockException()
}
