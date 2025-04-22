import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable{
    private static final Logger logger = Logger.getLogger(Worker.class.getName());
    private final TaskQueue taskQueue;
    private final List<String> results;
    private final ReentrantLock resultLock;

    public Worker(TaskQueue queue, List<String> results, ReentrantLock lock) {
        this.taskQueue = queue;
        this.results = results;
        this.resultLock = lock;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Task task = taskQueue.getTask();            // may block
                // Simulate work
                Thread.sleep(100);
                String result = "Task " + task.getId() + " processed by "
                        + Thread.currentThread().getName();
                resultLock.lock();
                try {
                    results.add(result);
                } finally {
                    resultLock.unlock();
                }
                logger.info(result);
            }
        } catch (InterruptedException ie) {
            logger.warning(Thread.currentThread().getName() + " interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in worker", e);
        }
    }

}
