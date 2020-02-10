# RxTasks
Small library to convert from Google's [Task](https://developers.google.com/android/reference/com/google/android/gms/tasks/Task) to RxJava2.

## Usage

1. [Setup with JitPack](https://jitpack.io/#dzielins42/RxTasks)
2. Use in code:
```
val task = Tasks.forResult(42)
task.asSingle().subscribe{ theAnswer -> searchQuestion(theAnswer) }
```

### Java
RxTasks is build on kotlin extension functions, in order to use it in Java you need to use `RxTasksKt` as follows:
```
Task<Integer> task = Tasks.forResult(42);
RxTasksKt.asSingle(task).subscribe(theAnswer -> searchQuestion(theAnswer));
```
