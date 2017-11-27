package bodya.popov.ru.asyncprocessorimpl.async;

import java.util.concurrent.Callable;

/**
 * @author Popov Bogdan
 */

public class FutureTask<T> {

    private final SingleAsyncExecution.Callback mCallback;
    private final Callable<? extends T> mCallable;
    private T mResult;
    private Exception mException;


    public FutureTask(Callable<? extends T> callable, SingleAsyncExecution.Callback callback) {
        mCallable = callable;
        mCallback = callback;
    }

    public SingleAsyncExecution.Callback getCallback() {
        return mCallback;
    }

    public Callable<? extends T> getCallable() {
        return mCallable;
    }

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        mResult = result;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception exception) {
        mException = exception;
    }
}
