/**
 * Created by Владимир on 19.12.2015.
 */

@SuppressWarnings("checkstyle:magicnumber")
public class ThreadsCount
{
    private final Object lock = new Object();
    private int currentNumber = 0;
    private int threadsNumber;

    ThreadsCount(int numberOfThreadsToStart) {
        threadsNumber = numberOfThreadsToStart;
        for (int i = 0; i < numberOfThreadsToStart; i++) {
            MyThread myThread = new MyThread(i);
            myThread.start();
        }
    }

    class MyThread extends Thread
    {
        private int id;

        MyThread(int ourId) {
            id = ourId;
        }


        @Override
        public String toString() {
            return "Thread-" + id;
        }

        @Override
        public void run()
        {
            for (; !Thread.currentThread().isInterrupted();) {
                synchronized (lock) {
                    if (currentNumber == id) {
                        System.out.println(this.toString());
                        Thread.yield();
                        currentNumber = (currentNumber + 1) % threadsNumber;
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException i) {
                            System.out.println("Error : " + i);
                        }
                        lock.notify();
                    } else {
                        lock.notify();
                    }
                }
            }
        }
    }
}

