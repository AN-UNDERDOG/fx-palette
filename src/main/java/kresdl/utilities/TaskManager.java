package kresdl.utilities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Task mangeger
 */
public class TaskManager {

    private final ExecutorService pool;

    /**
     * Constructs TaskManager backed with given ExecutorService
     *
     * @param s backing executor service
     */
    public TaskManager(ExecutorService s) {
        pool = s;
    }

    /**
     * Returns associated ExecutorService
     *
     * @return executor service
     */
    public ExecutorService getPool() {
        return pool;
    }

    /**
     * Executes the given command at some time in the future. The command may
     * execute in a new thread, in a pooled thread, or in the calling thread, at
     * the discretion of the Executor implementation.
     *
     * @param r the runnable task
     */
    public void execute(Runnable r) {
        pool.execute(r);
    }

    /**
     * Submits a Runnable task for execution and returns a Future representing
     * that task. The Future's get method will return null upon successful
     * completion.
     *
     * @param r the runnable task
     * @return a Future representing pending completion of the task
     */
    public Future<?> submit(Runnable r) {
        return pool.submit(r);
    }

    /**
     * Submits tasks 2 through s.length for execution and runs task 1 in the
     * current thread, then blocks until completion of submitted tasks.
     *
     * @param s a collection of tasks
     */
    public void distribute(Collection<Runnable> s) {
        Iterator<Runnable> t = s.iterator();
        Runnable m = t.next();
        Set<Future<?>> f = new HashSet<>();
        while (t.hasNext()) {
            f.add(pool.submit(t.next()));
        }
        m.run();
        try {
            for (Future<?> x : f) {
                x.get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Submits tasks for execution and returns a CompletableFuture which
     * resolves when all tasks has completed.
     *
     * @param s a collection of tasks
     * @return completable future
     */
    public CompletableFuture<Void> dispatch(Collection<Runnable> s) {
        CompletableFuture<?>[] f = new CompletableFuture[s.size()];
        int i = 0;
        for (Runnable r : s) {
            f[i++] = CompletableFuture.runAsync(r, pool);
        }
        return CompletableFuture.allOf(f);
    }

    /**
     * Shut down
     */
    public void shutdown() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.out.println("Unable to shut down executor service");
                }
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
