package ru.popov.bodya.executorcompletionsample;


import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

public class DownloadCompletionService<T> extends ExecutorCompletionService<T> {

    private ExecutorService executor;

    public DownloadCompletionService(ExecutorService executor) {
        super(executor);
        this.executor = executor;
    }

    public void shutdown() {
        executor.shutdown();
    }

    public boolean isTerminated() {
        return executor.isTerminated();
    }
}
