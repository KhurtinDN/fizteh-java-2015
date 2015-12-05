package ru.mipt.diht.students.feezboom.Lessons.DataBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * * Created by avk on 05.12.15.
 **/
@SuppressWarnings("checkstyle:magicnumber")
public class Lesson5_12_1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int threads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 0; ++i) {
            Future<Integer> future = executorService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    //calculation
                    Thread.sleep(1000);
                    return new Random().nextInt();
                }
            });
            futures.add(future);
        }
        for(Future<Integer> future : futures) {
            System.out.println(future.get());
        }
        executorService.shutdown();

        long end = System.currentTimeMillis();


    }
}
