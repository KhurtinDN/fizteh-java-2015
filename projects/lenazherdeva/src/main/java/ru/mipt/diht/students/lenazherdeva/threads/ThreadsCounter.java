package ru.mipt.diht.students.lenazherdeva.threads;


/**
 * Created by admin on 13.12.2015.
 */

public class ThreadsCounter {

    public static void main(String[] args) {
            int numberOfThreads = 0;
            try {
                if (args.length == 0) {
                    throw new IllegalArgumentException();
                }

                numberOfThreads = Integer.valueOf(args[0]);

                if (numberOfThreads <= 0) {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e) {
                System.err.println("There should be positive number as command line argument");
                System.exit(1);
            }
            System.out.println("$ Counter " + numberOfThreads);
            startThreads(numberOfThreads);
        }

        private static void startThreads(int numberOfThreads) {
            for (int i = 1; i <= numberOfThreads; ++i) {
                new Counter(i, numberOfThreads).start();
            }
        }
    }
