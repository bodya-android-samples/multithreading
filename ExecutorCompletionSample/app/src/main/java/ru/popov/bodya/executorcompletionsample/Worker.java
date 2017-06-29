package ru.popov.bodya.executorcompletionsample;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Worker extends HandlerThread {

    private static final String TAG = "Worker";

    private DownloadCompletionService<Drawable> service;
    private HelperThread helperThread;
    private Handler workerHandler;
    private WeakReference<LoaderCallback> callbackWeakReference = new WeakReference<>(null);

    private class HelperThread extends Thread {
        private DownloadCompletionService<Drawable> service;

        public HelperThread(DownloadCompletionService<Drawable> service) {
            this.service = service;
        }

        @Override
        public void run() {
            super.run();
            try {
                Log.e(TAG, "workerThreadHelper run");
                while (!service.isTerminated() && !isInterrupted()) {
                    Log.e(TAG, "workerThreadHelper while");
                    Future<Drawable> future = service.poll(1, TimeUnit.SECONDS);
                    if (future != null) {
                        loadDataToUi(future.get());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public interface LoaderCallback {
        void onLoadFinished(Drawable image);
    }

    public void setListener(LoaderCallback callback) {
        callbackWeakReference = new WeakReference<>(callback);
    }

    public Worker() {
        super(TAG);
        ExecutorService executorService = Executors.newCachedThreadPool();
        service = new DownloadCompletionService<>(executorService);
        helperThread = new HelperThread(service);
        helperThread.start();
    }

    public void queueTask(Callable<Drawable> callable) {
        Log.e(TAG, "callable added to the queue: " + callable);
        service.submit(callable);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        workerHandler = new Handler();
    }


    @Override
    public boolean quit() {
        service.shutdown();
        helperThread.interrupt();
        return super.quit();
    }

    private void loadDataToUi(final Drawable image) {
        Log.e(TAG, "loadDataToUi with thread" + Thread.currentThread().toString());

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            LoaderCallback loaderCallback = callbackWeakReference.get();
            if (loaderCallback != null) {
                loaderCallback.onLoadFinished(image);
            }
        });

    }
}