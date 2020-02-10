package pl.dzielins42.rxtasks

import com.google.android.gms.tasks.Task
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.concurrent.CancellationException

fun <T : Any> Task<T>.asSingle(): Single<T> {
    return Single.create<T> { emitter ->
        if (isComplete || isCanceled) {
            handleSingleEmitter(emitter)
        } else {
            addOnCompleteListener { task -> task.handleSingleEmitter(emitter) }
            addOnCanceledListener { emitter.onError(CancellationException("Task cancelled")) }
        }
    }
}

private fun <T : Any> Task<T>.handleSingleEmitter(emitter: SingleEmitter<T>) {
    if (!isCanceled) {
        if (exception != null) {
            emitter.onError(exception!!)
        } else {
            // Believe or not but getResult may throw an Exception
            try {
                if (result != null) {
                    emitter.onSuccess(result!!)
                } else {
                    emitter.onError(IllegalStateException("Task completed without result"))
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    } else {
        emitter.onError(CancellationException("Task cancelled"))
    }
}