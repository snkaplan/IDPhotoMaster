package com.idphoto.idphotomaster.core.common

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            if (it.exception != null) {
                cont.resumeWithException(it.exception!!)
            } else {
                cont.resume(it.result, null)
            }
        }
    }
}

suspend fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: suspend (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}