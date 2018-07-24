package pl.mareklangiewicz.rxmock

open class RxMockException(message: String = "Rx mock assertion failed", cause: Throwable? = null)
    : AssertionError(message, cause)
