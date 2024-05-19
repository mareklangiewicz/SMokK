package pl.mareklangiewicz.smokkx

open class SMokKXException(message: String = "SMokKX assertion failed", cause: Throwable? = null) :
  AssertionError(message, cause)
