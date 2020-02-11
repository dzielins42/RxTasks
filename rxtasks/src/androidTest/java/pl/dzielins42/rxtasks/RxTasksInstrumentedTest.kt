package pl.dzielins42.rxtasks

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CancellationException

@RunWith(AndroidJUnit4::class)
class RxTasksInstrumentedTest {

    @Test
    fun whenTasksReturnsValueSingleEmitsIt() {
        // Arrange
        val expectedValue = 42
        val task: Task<Int> = Tasks.call {
            Thread.sleep(1000)
            expectedValue
        }

        // Act
        val testObserver = task.asSingle().test().await()

        // Assert
        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(1)
            .assertValue(expectedValue)
    }

    @Test
    fun whenTasksThrowsExceptionSingleCallsOnError() {
        // Arrange
        val expectedError = Exception()
        val task: Task<Int> = Tasks.call {
            Thread.sleep(1000)
            throw expectedError
        }

        // Act
        val testObserver = task.asSingle().test().await()

        // Assert
        testObserver
            .assertError(expectedError)
    }

    @Test
    fun whenTasksHasCompletedWithValueSingleEmitsIt() {
        // Arrange
        val expectedValue = 42
        val task: Task<Int> = Tasks.forResult(expectedValue)

        // Act
        val testObserver = task.asSingle().test().await()

        // Assert
        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(1)
            .assertValue(expectedValue)
    }

    @Test
    fun whenTasksHasCompletedWithoutValueSingleCallsOnError() {
        // Arrange
        val task: Task<Void> = Tasks.forResult(null)

        // Act
        val testObserver = task.asSingle().test().await()

        // Assert
        testObserver
            .assertError { error -> error is IllegalStateException }
    }

    @Test
    fun whenTasksHasCompletedWithoutValueCompletableCompletes() {
        // Arrange
        val task: Task<Void> = Tasks.forResult(null)

        // Act
        val testObserver = task.aCompletable().test().await()

        // Assert
        testObserver
            .assertComplete()
            .assertNoErrors()
    }

    @Test
    fun whenTasksHasCompletedWithErrorSingleCallsOnError() {
        // Arrange
        val expectedError = Exception()
        val task: Task<Int> = Tasks.forException(expectedError)

        // Act
        val testObserver = task.asSingle().test().await()

        // Assert
        testObserver
            .assertError(expectedError)
    }

    @Test
    fun whenTasksWasCancelledSingleCallsOnErrorWithCancellationException() {
        // Arrange
        val task: Task<Int> = Tasks.forCanceled()

        // Act
        val testObserver = task.asSingle().test().await()

        // Assert
        testObserver
            .assertError { error -> error is CancellationException }
    }

    @Test
    fun whenTasksHasCompletedWithErrorCompletableCallsOnError() {
        // Arrange
        val expectedError = Exception()
        val task: Task<Void> = Tasks.forException(expectedError)

        // Act
        val testObserver = task.aCompletable().test().await()

        // Assert
        testObserver
            .assertError(expectedError)
    }

    @Test
    fun whenTasksWasCancelledCompletableCallsOnErrorWithCancellationException() {
        // Arrange
        val task: Task<Void> = Tasks.forCanceled()

        // Act
        val testObserver = task.aCompletable().test().await()

        // Assert
        testObserver
            .assertError { error -> error is CancellationException }
    }
}
