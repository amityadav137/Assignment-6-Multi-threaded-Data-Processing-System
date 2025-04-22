import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int numWorkers = 4, numTasks = 20;
        TaskQueue queue = new TaskQueue();
        List<String> results = new ArrayList<>();
        ReentrantLock resultLock = new ReentrantLock();

        // Start worker threads
        ExecutorService exec = Executors.newFixedThreadPool(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            exec.submit(new Worker(queue, results, resultLock));
        }

        // Enqueue tasks
        for (int i = 1; i <= numTasks; i++) {
            queue.addTask(new Task(i, "Payload " + i));
        }

        // Allow workers to finish
        exec.shutdownNow();
        exec.awaitTermination(5, TimeUnit.SECONDS);

        // Write results to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"))) {
            for (String line : results) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException ioe) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "File I/O error", ioe);
        }
    }
}