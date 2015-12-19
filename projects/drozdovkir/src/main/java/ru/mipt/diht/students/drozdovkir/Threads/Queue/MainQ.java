import java.util.ArrayList;
import java.util.List;

public class MainQ {
    private static QueueBlock<Integer> queue = new QueueBlock<>(3);

    static class Put implements Runnable {
        private List<Integer> putting;

        Put(List<Integer> list) {
            putting = list;
        }

        @Override
        public void run() {
            try {
                queue.offer(putting);
            } catch (InterruptedException e) {
                System.err.printf("Interrupted\n");
            }
        }
    }

    static class Get implements Runnable {
        private int n;

        Get(int number) {
            n = number;
        }

        @Override
        public void run() {
            try {
                List<Integer> result = queue.take(n);
                for(int i = 0; i < n; i++) {
                    System.out.printf(String.valueOf(result.get(i)));
                }
            } catch (InterruptedException e) {
                System.err.printf("Interrupted\n");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        new Thread(new Put(list)).start();
        new Thread(new Get(1)).start();
    }
}