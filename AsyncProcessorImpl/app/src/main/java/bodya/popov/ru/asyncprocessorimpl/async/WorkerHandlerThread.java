package bodya.popov.ru.asyncprocessorimpl.async;

import android.os.HandlerThread;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Popov Bogdan
 */

public class WorkerHandlerThread extends HandlerThread {

    private static final String TAG = "WorkerHandlerThread";

    private DownloadCompletionService<FutureTask> mCompletionService;
    private WeakReference<WorkerCallback> mWorkerCallbackWeakReference = new WeakReference<>(null);
    private HelperThread mHelperThread;


    WorkerHandlerThread() {
        super(TAG);
        ExecutorService executorService = Executors.newCachedThreadPool();
        mCompletionService = new DownloadCompletionService<>(executorService);
        mHelperThread = new HelperThread(mCompletionService);
        mHelperThread.start();
    }

    @Override
    public boolean quit() {
        Log.e(TAG, "quit");
        mCompletionService.shutdown();
        mHelperThread.interrupt();
        return super.quit();
    }

    void setListener(WorkerCallback callback) {
        mWorkerCallbackWeakReference = new WeakReference<>(callback);
    }

    <T> void queueTask(final FutureTask<T> futureTask) {
        Log.e(TAG, "futureTask added to the queue: " + futureTask);
        mCompletionService.submit(new Callable<FutureTask>() {
            @Override
            public FutureTask call() throws Exception {
                T result;
                try {
                    Log.e(TAG, "executing task on thread: " + Thread.currentThread());
                    result = futureTask.getCallable().call();
                    futureTask.setResult(result);
                    return futureTask;
                } catch (Exception e) {
                    futureTask.setException(e);
                    return futureTask;
                }
            }
        });
    }

    private class HelperThread extends Thread {
        private DownloadCompletionService<FutureTask> service;

        HelperThread(DownloadCompletionService<FutureTask> service) {
            super("HelperThread");
            this.service = service;
        }

        @Override
        public void run() {
            super.run();
            try {
                Log.e(TAG, "workerThreadHelper run on thread: " + Thread.currentThread());
                while (!service.isTerminated() && !isInterrupted()) {
                    Future<FutureTask> future = service.poll(1, TimeUnit.SECONDS);
                    if (future != null) {
                        WorkerCallback callback = mWorkerCallbackWeakReference.get();
                        if (callback != null) {
                            callback.onLoadFinished(future.get());
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

     interface WorkerCallback {
        <V> void onLoadFinished(FutureTask<V> task);
    }
}
