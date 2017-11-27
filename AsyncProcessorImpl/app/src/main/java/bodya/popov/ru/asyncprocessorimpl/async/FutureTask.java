package bodya.popov.ru.asyncprocessorimpl.async;

import java.util.concurrent.Callable;

/**
 * @author Popov Bogdan
 */

class FutureTask<T> {

    private final Callable<? extends T> mCallable;
    private T mResult;
    private Exception mException;

    FutureTask(Callable<? extends T> callable) {
        mCallable = callable;
    }

    Callable<? extends T> getCallable() {
        return mCallable;
    }

    T getResult() {
        return mResult;
    }

    void setResult(T result) {
        mResult = result;
    }

    Exception getException() {
        return mException;
    }

    void setException(Exception exception) {
        mException = exception;
    }
}
