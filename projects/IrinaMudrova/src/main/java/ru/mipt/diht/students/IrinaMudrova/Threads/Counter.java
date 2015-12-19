package main.java.ru.mipt.diht.students.IrinaMudrova.Threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private static Integer quantity;
    private static Integer currentId;
    private static Lock lock = new ReentrantLock(true);
    private static Condition needWrite = lock.newCondition();
    private static Thread[] threads;

    public static void main(String[] args) throws Exception {

        class OurTask implements Runnable {
            private final Integer id;

            OurTask(Integer id) {
                this.id = id;
            }

            @Override
            public void run() {
                Integer iteration = 0;
                while (true) {
                    lock.lock();
                    try {
                        while (!currentId.equals(id)) {
                            needWrite.await();
                            if (Thread.interrupted()) {
                         //       System.err.println("Interrupting thread #" + id);
                                return;
                            }
                        }
                        System.out.println("Thread-" + id);
                        iteration++;
                        currentId = (currentId + 1) % quantity;
                        needWrite.signalAll();
                        final int number = 5;
                        if (iteration > number) {
                            return;
                        }
                    } catch (InterruptedException e) {
                      //  System.err.println("Interrupting thread #" + id);
                        return;
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }

        quantity = Integer.valueOf(args[0]);
        threads = new Thread[quantity];
        currentId = 0;
        for (int i = 0; i < quantity; i++) {
            threads[i] = new Thread(new OurTask(i));
        }
        currentId = -1;
        for (Thread thread : threads) {
            thread.start();
        }
        lock.lock();
        currentId = 0;
        needWrite.signalAll();
        lock.unlock();
       /* Thread.sleep(100);
         for (Thread thread : threads) {
            thread.interrupt();

        }*/
    }
}

