package ru.popov.bodya.executorcompletionsample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Worker extends HandlerThread {

    private static final String TAG = "Worker";
    private static final String CLASS_KEY = "Class";
    private static final int CALLABLE_FOR_COMPLETION_SERVICE = 0;

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

    public <T> void queueTask(Callable<T> callable, Class<T> clazz) {
        Log.e(TAG, "callable added to the queue: " + callable);
        Message message = workerHandler.obtainMessage();
        message.arg1 = CALLABLE_FOR_COMPLETION_SERVICE;
        Bundle bundle = new Bundle();
        bundle.putSerializable(CLASS_KEY, clazz);
        message.setData(bundle);
        message.obj = callable;
        message.sendToTarget();
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        prepareHandler();
    }


    @Override
    public boolean quit() {
        service.shutdown();
        helperThread.interrupt();
        return super.quit();
    }

    private void prepareHandler() {
        workerHandler = new Handler(msg -> {
            if (msg.arg1 == CALLABLE_FOR_COMPLETION_SERVICE) {
                Class<? extends Serializable> clazz = msg.getData().getSerializable(CLASS_KEY).getClass();
                Callable<Drawable> drawableCallable = (Callable<Drawable>) msg.obj;
                service.submit(drawableCallable);
                return true;
            }
            return false;
        });
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