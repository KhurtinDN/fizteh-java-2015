package projects.JenkaEff.src.main.java.ru.mipt.diht.students.JenkaEff.threads;

public class Threads1 {

    private static int THREADS_NUM = 5;

    private static Object[] cvs = new Integer[THREADS_NUM];

    static class Child implements Runnable {

        private int number;
        private int nextNumber;

        Child(int num) {
            number = num;
            nextNumber = (number + 1) % THREADS_NUM;            
        }

        @Override
        public void run() {
            while (true) {
                synchronized (cvs[number]) {
                    try {
                        cvs[number].wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Thread-" + number);
                    synchronized (cvs[nextNumber]) {
                        cvs[nextNumber].notify();
                    }                    
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[THREADS_NUM];
        for (int i = 0; i < THREADS_NUM; i++) {
            cvs[i] = new Integer(0);
            threads[i] = new Thread(new Child(i));
            threads[i].start();
        }
        synchronized (cvs[0]) {
            cvs[0].notify();
        }        
    }
}
