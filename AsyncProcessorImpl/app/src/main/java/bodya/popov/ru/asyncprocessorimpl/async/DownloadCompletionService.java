package bodya.popov.ru.asyncprocessorimpl.async;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * @author Popov Bogdan
 */

public class DownloadCompletionService <T> extends ExecutorCompletionService<T> {

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