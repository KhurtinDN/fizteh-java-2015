public class Counter {
    private final Integer numberOfThreads;
    private Integer currentThread;

    class Counted implements Runnable{
        private final Integer numberOfThread;

        Counted(Integer number) {
            numberOfThread = number;
        }

        @Override
        public void run() {
            while(true) {
                synchronized (Counter.this) {
                    while ((currentThread + 1) % numberOfThreads != numberOfThread) {
                        try {
                            Counter.this.wait();
                        } catch (InterruptedException E) {
                            System.err.printf("Interrutpted %d\n", numberOfThread);
                            return;
                        }
                    }
                    System.out.printf("Thread-%d\n", numberOfThread + 1);
                    currentThread = numberOfThread;
                    Counter.this.notifyAll();
                }
            }
        }
    }

    Counter(Integer number) {
        numberOfThreads = number;
        currentThread = number - 1;
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Counted(i)).start();
        }
    }
}