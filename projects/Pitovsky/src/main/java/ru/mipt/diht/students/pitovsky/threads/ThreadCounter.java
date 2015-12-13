package ru.mipt.diht.students.pitovsky.threads;

public class ThreadCounter {
    private Integer lastBabbler;
    private final int babblerCount;

    public class Babbler extends Thread {
        private int number;

        public Babbler(int myNumber) {
            number = myNumber;
        }

        @Override
        public final void run() {
            while (true) {
                synchronized (lastBabbler) {
                    if (lastBabbler.equals(number - 1)) {
                        System.out.println("Thread-" + number);
                        lastBabbler = lastBabbler + 1;
                        if (lastBabbler.equals(babblerCount)) {
                            lastBabbler = 0;
                        }
                    }
                }

            }
        }
    }

    public ThreadCounter(int threadCount) {
        babblerCount = threadCount;
        lastBabbler = 0;
        for (int i = 0; i < babblerCount; ++i) {
            Thread babbler = new Babbler(i + 1);
            babbler.start();
        }
    }

    public static void main(String[] args) {
        new ThreadCounter(Integer.valueOf(args[0]));
    }
}
