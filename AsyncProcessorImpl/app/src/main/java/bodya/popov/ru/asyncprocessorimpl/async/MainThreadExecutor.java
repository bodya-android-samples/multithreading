package bodya.popov.ru.asyncprocessorimpl.async;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * @author Popov Bogdan
 */

public class MainThreadExecutor implements Executor {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable runnable) {
        mHandler.post(runnable);
    }

}
