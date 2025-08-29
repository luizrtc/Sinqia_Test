# Sinqia_Test

## Senior Developer Technical Assessment – Debugging & Code Review

### Introduction

As part of our selection process for **Senior Developer (Java / C#)**, we want to evaluate your
ability to **analyze existing code**, identify problems, and propose improvements.

In this test, you will be given two code snippets (one in **Java**, one in **C#**). Each snippet
contains intentional issues related to **thread safety, resource management,
asynchronous execution, and clean coding practices.**

### Instructions

1.  Review the provided code carefully.
2.  Identify all problems you see (bugs, bad practices, scalability issues, security risks, etc.).
3.  For each problem, explain **why it is an issue and how you would fix it.**
4.  You may write your answers in text, or include corrected code snippets where relevant.
5.  There is **no single correct answer** – we are evaluating your reasoning, experience, and
    ability to justify technical decisions.



## Part 1 – Java Snippet
```java
public class FileProcessor {
    private static List<String> lines = new ArrayList<>();
    
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    BufferedReader br = new BufferedReader(new
                    FileReader("data.txt"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line.toUpperCase());
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        System.out.println("Lines processed: " + lines.size());
    }
}
```

### Response

Java code has several issues related to concurrency, resource management, and best practices.

### 1. Lack of Thread Safety

*   **Problem:** The `ArrayList lines` is a static list shared among multiple threads. `ArrayList` is not thread-safe. When several threads attempt to add elements simultaneously (`lines.add(...)`), this can lead to unpredictable results, such as data loss, line duplication, or even exceptions like `ArrayIndexOutOfBoundsException`.
*   **Impact:** In a real-world system, this concurrency failure would result in inconsistent data and hard-to-reproduce bugs, compromising application integrity.
*   **Solution:** Use a thread-safe collection. Options include `Vector` or `Collections.synchronizedList(new ArrayList<>())`. In the provided code, `Collections.synchronizedList` was used for a simple and effective fix.

### 2. Deficient Resource Management

*   **Problem:** The `br.close()` method was inside the `try` block. If an exception occurred during file reading (`br.readLine()`), `br.close()` would never be reached, leading to a resource leak.
*   **Impact:** Resource leaks can lead to progressive slowdown and total system failure in long-running applications.
*   **Solution:** The best practice is to use the `try-with-resources` statement, introduced in Java 7. It ensures that the resource is automatically and safely closed, regardless of whether the `try` block completes successfully or an exception is thrown.

### 3. `ExecutorService` and `main` Synchronization

*   **Problem:** The code `executor.shutdown()` immediately after task submission, but printed the list size (`System.out.println(...)`) without waiting for all tasks to complete. The `shutdown()` method only initiates the `ExecutorService` shutdown but does not guarantee the completion of running tasks.
*   **Impact:** The program's output would not reflect the correct size, making the result different from expected, thus being a critical failure for a data processing system.
*   **Solution:** It is essential to wait for all tasks to complete. Use `executor.shutdown()` followed by `executor.awaitTermination(timeout, unit)`. This blocks the main thread until all tasks have finished or until the timeout expires.

### 4. Generic Exception Handling (`e.printStackTrace()`)

*   **Problem:** The use of `e.printStackTrace()` in a `catch` block is considered bad practice in production systems. It only prints the stack trace to the standard error output, without a robust error handling mechanism.
*   **Impact:** In production systems, the exception can get lost among other logs without a way to track it, and the application may not be able to retry or log the problem in a monitoring system.
*   **Solution:** For a real system, exception handling should be more sophisticated. At a minimum, the exception should be logged in a structured way (using frameworks like Log4j, SLF4J). The application should also decide whether to continue, stop, or retry based on the nature of the exception.

---