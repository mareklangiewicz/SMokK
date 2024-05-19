package pl.mareklangiewicz.smokk

open class SMokKException(message: String = "SMokK assertion failed", cause: Throwable? = null) :
  AssertionError(message, cause)
