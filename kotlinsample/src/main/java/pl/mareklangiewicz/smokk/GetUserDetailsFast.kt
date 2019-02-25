package pl.mareklangiewicz.smokk

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun getUserDetailsFast(
    userId: Int,
    fetchUserDetails: suspend (Int) -> String?,
    getCachedUserDetails: suspend (Int) -> String?,
    putCachedUserDetails: (Int, String) -> Unit
): String?  = coroutineScope {
    val cached = async { getCachedUserDetails(userId) }
    val new = async { fetchUserDetails(userId) }
    cached.await()?.also { new.cancel() } ?: new.await()?.also { putCachedUserDetails(userId, it) }
}
