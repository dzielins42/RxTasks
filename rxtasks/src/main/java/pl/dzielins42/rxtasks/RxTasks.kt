package pl.dzielins42.rxtasks

import com.google.android.gms.tasks.Task
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.concurrent.CancellationException

/**
 * Wraps [Task] in [Single], which emits its value or error.
 */
fun <T : Any> Task<T>.asSingle(): Single<T> {
    return Single.create<T> { emitter ->
        if (isComplete || isCanceled) {
            handleEmitter(emitter)
        } else {
            addOnCompleteListener { task -> task.handleEmitter(emitter) }
            addOnCanceledListener { emitter.onError(CancellationException("Task cancelled")) }
        }
    }
}

/**
 * Wraps [Task], which does not return a value, in [Completable], which
 * completes when [Task] completes or calls `onError`.
 */
fun Task<Void>.aCompletable(): Completable {
    return Completable.create { emitter ->
        if (isComplete || isCanceled) {
            handleEmitter(emitter)
        } else {
            addOnCompleteListener { task -> task.handleEmitter(emitter) }
            addOnCanceledListener { emitter.onError(CancellationException("Task cancelled")) }
        }
    }
}

private fun <T : Any> Task<T>.handleEmitter(emitter: SingleEmitter<T>) {
    if (!isCanceled) {
        if (exception != null) {
            emitter.onError(exception!!)
        } else {
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

private fun <T : Any> Task<T>.handleEmitter(emitter: CompletableEmitter) {
    if (!isCanceled) {
        if (exception != null) {
            emitter.onError(exception!!)
        } else {
            emitter.onComplete()
        }
    } else {
        emitter.onError(CancellationException("Task cancelled"))
    }
}