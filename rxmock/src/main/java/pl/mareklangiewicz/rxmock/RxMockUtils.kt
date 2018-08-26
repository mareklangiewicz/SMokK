@file:Suppress("unused")

package pl.mareklangiewicz.rxmock

import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

infix fun <T> TestObserver<T>.isNow(value: T) {
    assertEquals(value, values().last())
}

infix fun <T> TestObserver<T>.hasNow(predicate: T.() -> Boolean) {
    val values = values()
    assertTrue("TestObserver values: $values", values.last().predicate())
}

infix fun <T> TestObserver<T>.hadAny(predicate: T.() -> Boolean) {
    val values = values()
    assertTrue("TestObserver values: $values", values.any { it.predicate() })
}

infix fun <T> TestObserver<T>.wasAny(value: T) {
    val values = values()
    assertTrue("TestObserver values: $values", values.any { it == value })
}

@Suppress("UNCHECKED_CAST")
infix fun <T> TestObserver<*>.hasNowType(predicate: T.() -> Boolean) {
    val values = values()
    assertTrue("TestObserver values: $values", (values.last() as T).predicate())
}


infix fun <T> Consumer<T>.put(value: T) = accept(value)

infix fun <T> Collection<T>.hasNo(t: T) = assertEquals(0, count { it == t })

infix fun <T> Collection<T>.hasOne(t: T) = assertEquals(1, count { it == t })

infix fun <T> Collection<T>.hasTwo(t: T) = assertEquals(2, count { it == t })

infix fun <T> Collection<T>.hasThree(t: T) = assertEquals(3, count { it == t })

infix fun <T> Collection<T>.hasAny(t: T) = assertTrue("Collection contains: $this", contains(t))

fun setupRxJavaErrorHandler() {
    RxJavaPlugins.setErrorHandler { if (it !is UndeliverableException) throw it }
}

