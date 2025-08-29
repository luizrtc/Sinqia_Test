import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class FileProcessor {
    private static List<String> lines = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line.toUpperCase());
                    }
                } catch (Exception e) {
                    System.err.println("An error occurred during file processing: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread was interrupted while waiting for tasks to finish.");
        }

        System.out.println("Lines processed: " + lines.size());
    }
}
