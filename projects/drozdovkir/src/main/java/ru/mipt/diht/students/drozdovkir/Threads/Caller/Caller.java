import java.util.Random;

public class Caller {
    private final Integer numberOfThreads;
    private final Double LOW_PROBABILITY = 0.1;
    private final long START = 3;
    private Integer numberOfCalled = 0;
    private Integer numberOfCalling = 0;
    private boolean ready = false;
    private Random random;

    class Callable implements Runnable {
        private Integer called;

        Callable() {
            called = 0;
            numberOfCalled++;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (Caller.this) {
                    while (called == numberOfCalling) {
                        if (numberOfCalled == numberOfThreads && ready)
                            return;
                        try {
                            Caller.this.wait();
                        } catch (InterruptedException E) {
                            System.err.printf("Interrupted\n");
                            return;
                        }
                    }
                    called++;
                    numberOfCalled++;
                    if (random.nextDouble() < LOW_PROBABILITY) {
                        System.out.printf("NO\n");
                        ready = false;
                    } else {
                        System.out.printf("YES\n");
                    }
                    Caller.this.notifyAll();
                }
            }
        }
    }

    Caller(int number) {
        numberOfThreads = number;
        random = new Random(START);
        main();
    }

    public void main() {
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Callable()).start();
        }

        while (true) {
            synchronized (Caller.this) {
                while (numberOfCalled != numberOfThreads) {
                    try {
                        Caller.this.wait();
                    } catch (InterruptedException E) {
                        System.err.printf("Interrupted\n");
                        return;
                    }
                }

                if (ready) {
                    return;
                }

                System.out.printf("Are you ready?\n");
                numberOfCalling++;
                numberOfCalled = 0;
                ready = true;
                notifyAll();
            }
        }
    }
}