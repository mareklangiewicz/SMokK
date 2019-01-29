package pl.mareklangiewicz.rxmock


infix fun <A, B, C> Pair<A, B>.tre(c: C) = Triple(first, second, c)
infix fun <A, B, C, D> Triple<A, B, C>.fo(d: D) = Quad(first, second, third, d)
infix fun <A, B, C, D, E> Quad<A, B, C, D>.fi(e: E) = Jackson(a, b, c, d, e)

data class Quad<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D)

data class Jackson<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E)
