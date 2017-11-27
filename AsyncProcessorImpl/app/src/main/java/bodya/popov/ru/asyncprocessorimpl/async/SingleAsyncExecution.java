package bodya.popov.ru.asyncprocessorimpl.async;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * @author Popov Bogdan
 */

public class SingleAsyncExecution<T> implements WorkerHandlerThread.WorkerCallback {

    private static final String TAG = "SingleAsyncExecution";

    private final Callable<? extends T> mCallable;
    private WeakReference<Callback> mWeakCallback;
    private Executor mMainThreadExecutor = new MainThreadExecutor();
    private WorkerHandlerThread mWorkerHandlerThread;

    @NonNull
    public static <T> SingleAsyncExecution<T> fromCallable(Callable<? extends T> callable) {
        if (callable == null) {
            throw new IllegalStateException("Callable cannot be null");
        }
        return new SingleAsyncExecution<T>(callable);
    }

    private SingleAsyncExecution(@NonNull Callable<? extends T> callable) {
        this.mCallable = callable;
        mWorkerHandlerThread = new WorkerHandlerThread();
        mWorkerHandlerThread.setListener(this);
    }

    @NonNull
    public SingleAsyncExecution<T> addCallback(@NonNull Callback callback) {
        if (callback.getClass().isMemberClass() && !Modifier.isStatic(callback.getClass().getModifiers())) {
            throw new IllegalArgumentException(
                    "No anonymous class here: " + callback);
        }
        this.mWeakCallback = new WeakReference<>(callback);
        return this;
    }

    public void execute() {
        submit(mCallable, mWeakCallback.get());
    }

    @Override
    public <V> void onLoadFinished(FutureTask<V> task) {
        V result = task.getResult();
        Exception exception = task.getException();
        if (result != null && exception == null) {
            postSuccess(result);
        } else if (exception != null) {
            postException(exception);
        }
    }


    private void submit(@NonNull Callable<? extends T> callable, Callback callback) {
        final FutureTask<T> futureTask = new FutureTask<>(callable, callback);
        mWorkerHandlerThread.queueTask(futureTask);
    }

    private <V> void postSuccess(final V result) {
        mMainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "success on thread: " + Thread.currentThread());
                Callback callback = mWeakCallback.get();
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }
        });
    }

    private void postException(final Exception exception) {
        mMainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "exception on thread: " + Thread.currentThread());
                Callback callback = mWeakCallback.get();
                if (callback != null) {
                    callback.onException(exception);
                }
            }
        });
    }

    public interface Callback {
        <V> void onSuccess(@NonNull V result);

        void onException(Exception e);
    }
}
