package foo;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolPerfomanceTest {
    public static void main(String[] s) throws InterruptedException, ExecutionException {
        int count = 1024;
        System.out.println(getAvgExecutionTime(count));
        Thread.sleep(10_000);//sleep 10 seconds, letting compilation to finish
        System.out.println("after warmup");
        int threadIncrementNumber = 1000;
        for (int i = 0; i < 20_000; i = i + threadIncrementNumber) {
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadIncrementNumber + 1);

//            ExecutorService executorService = Executors.newWorkStealingPool();
            System.out.println(executorService);
            startThreads(executorService, threadIncrementNumber);
            System.out.println(executorService);
            Thread.sleep(5000);
            System.out.println(executorService);
            System.out.println("number of paralel threads = " + i);
            System.out.println("avg execution time = " + executorService.submit(() -> getAvgExecutionTime(count)).get());
        }
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

    private static long getCalculationTime() {
        long start = System.currentTimeMillis();
        int i = 1000000;
        while (i != 0) {
            i = i - (int) Math.ceil(Math.abs(Math.tan(Math.cos(Math.sin(i)))));
            ;//some calculations that do nothing
        }
        long end = System.currentTimeMillis();
        // if (i != 0) {//we use i here in order java optimization do not exclude it from calculation
        //   throw new IllegalStateException("i!=0");
        //}
        return end - start - i;
    }
}
