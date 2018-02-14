package foo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.Math.*;

/**
 * This is quick and dirty test on how many WAITING thread count task exectution will face perfomance degradation
 * because of threads in WAIT state.
 */
public class ThreadPoolPerfomanceTest {
    public static void main(String[] s) throws InterruptedException, ExecutionException {
        System.out.println(getAvgExecutionTime(1000));//warmup starts from 1000
        System.gc();
        Thread.sleep(10_000);//sleep 10 seconds, letting compilation to finish
        System.out.println("after warmup");
        int executionCount = 100;
        int threadIncrementNumber = 500;
        List<ThreadPoolExecutor> threadPoolExecutors = new ArrayList<>();
        for (int i = 0; i < 6_000; i = i + threadIncrementNumber) {
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadIncrementNumber);
            threadPoolExecutors.add(executorService);
//            ExecutorService executorService = Executors.newWorkStealingPool();
            System.out.println(executorService);
//            startThreads(executorService, threadIncrementNumber);
            System.out.println(executorService);
            System.gc();
            Thread.sleep(5000);
            System.out.println(executorService);
            int numberOfThreads = threadIncrementNumber + i;
            printStatistics(executionCount, executorService, numberOfThreads);
        }
        threadPoolExecutors.forEach(e -> e.shutdown());
        threadPoolExecutors.forEach(e -> System.out.println(e));
        System.gc();
        Thread.sleep(5000);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadIncrementNumber);
        printStatistics(executionCount, executorService, threadIncrementNumber);
        executorService.shutdown();
        System.out.println("done");
    }

    private static void printStatistics(int executionCount, ThreadPoolExecutor executorService, int numberOfThreads) throws InterruptedException, ExecutionException {
        System.out.println("number of threads = " + numberOfThreads);
        System.out.println("avg execution time in a single thread = " + executorService.submit(() -> getAvgExecutionTime(executionCount)).get());
        System.out.println("avg execution time in different threads for each execution with blocking = " + getAvgExecutionTimeWithBlocking(executionCount, executorService));
        System.out.println("avg execution time in different threads in paralel = " + getAvgExecutionTimeInDifferentThreadsInParalel(executionCount, executorService));
    }

    private static void startThreads(ExecutorService executorService, int thradsToStart) throws InterruptedException {
        for (int i = 0; i < thradsToStart; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    long a = new Date().getTime();
                }
            });
        }
    }

    private static Long getAvgExecutionTime(int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            long calculationTime = getCalculationTime();
            sum += calculationTime;
        }
        long avg = sum / count;
        return avg;
    }


    private static Long getAvgExecutionTimeWithBlocking(int count, ExecutorService executorService) throws ExecutionException, InterruptedException {
        long sum = 0;
        for (int i = 0; i < count; i++) {
            long calculationTime = executorService.submit(() -> getCalculationTime()).get();
            sum += calculationTime;
        }
        long avg = sum / count;
        return avg;
    }

    private static Long getAvgExecutionTimeInDifferentThreadsInParalel(int count, ExecutorService executorService) throws ExecutionException, InterruptedException {
        long sum = 0;
        List<Future<Long>> futures = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            futures.add(executorService.submit(() -> getCalculationTime()));
        }
        for (Future<Long> future : futures) {
            sum += future.get();
        }
        long avg = sum / count;
        return avg;
    }

    private static long getCalculationTime() {
        long start = System.currentTimeMillis();
        int i = 100000;
        while (i != 0) {
            i = i - (int) ceil(abs(sin(tan(cos(sin(exp(cos(log10(i)))))))));
            ;//some calculations that do nothing
        }
        long end = System.currentTimeMillis();
        // if (i != 0) {//we use i here in order java optimization do not exclude it from calculation
        //   throw new IllegalStateException("i!=0");
        //}
        return end - start - i;
    }
}
