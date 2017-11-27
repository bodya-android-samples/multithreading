package bodya.popov.ru.asyncprocessorimpl.async;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * @author Popov Bogdan
 */

class DownloadCompletionService <T> extends ExecutorCompletionService<T> {

    private ExecutorService executor;

    DownloadCompletionService(ExecutorService executor) {
        super(executor);
        this.executor = executor;
    }

    void shutdown() {
        executor.shutdown();
    }

    boolean isTerminated() {
        return executor.isTerminated();
    }
}