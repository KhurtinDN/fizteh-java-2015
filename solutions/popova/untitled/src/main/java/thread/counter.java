package thread;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

/**
 * Created by V on 14.12.2015.
 */
class MyRunnable implements Runnable {
    private final Object lock;
    private final BlockingQueue<String> queue;


    public MyRunnable(Object lock, BlockingQueue<String> queue) {
        this.queue = queue;
        this.lock = lock;
    }

    public void run() {
        synchronized (lock) {
            try {
                lock.wait();
                queue.put(currentThread().getName());
            } catch (InterruptedException ignore) {/*NOP*/}
        }
    }
}

class count{
    private static Object lock = new Object();
    private static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    int n;
    count(int N){
        n = N;
    }
    public void muster()throws InterruptedException{
        Thread th[] = new Thread[n];
        for (int k = 0; k < n; k++) {
            th[k] = new Thread(new MyRunnable(lock, queue), "Thread - " + k);
            th[k].start();
            Thread.sleep(100);
        }

        Thread.sleep(100);

        for (int k = 0; k < n; k++) {
            synchronized (lock) {
                lock.notify();
            }
            System.out.println(queue.take());
            th[k].join();
        }
    }
}

class Game{
           private int gamesNumber;
    Game(int Num){
        gamesNumber = Num;
    }
            public void roller()throws InterruptedException{
                Boolean good = false;
                results = new ArrayList<Boolean>();
                for( int i = 0; i < gamesNumber; ++i)
                {
                    results.add( false);
                }

                while (!good)
                {
                    ArrayList<rollcall> calls = new ArrayList<rollcall>();
                    for( int i = 0; i < gamesNumber; ++i)
                    {
                        calls.add( new rollcall(i));
                    }
                    calls.forEach((rollcall r) -> {
                        r.start();
                    });
                    calls.forEach((rollcall r) -> {
                        try {
                            r.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    good = true;
                    for(Boolean b : results )
                    {
                        if (!b)
                        {
                            good = false;
                        }
                    }
                }
            }

        class rollcall extends Thread {
            private int index;
            public rollcall(int idx){
                index = idx;
            }

            @Override
            public void run() {
                double random = new Random().nextDouble();
                if(random > 0.1) {
                    System.out.println("Yes");
                    results.set(index, Boolean.TRUE);
                }
                else{
                    System.out.println("No");
                    results.set(index, Boolean.FALSE);
                }
            }
        }
    static public ArrayList<Boolean> results;

}

class Queue<T>
{
    private int maxQueueSize;
    Queue(int N){
        locker = new ReentrantLock();
        elems = new ArrayList<T>();
        maxQueueSize = N;
    }
    void offer(List<T> offered) throws LimitExceededException {
        if(offered.size()+elems.size() < maxQueueSize){
            locker.lock();
            offered.stream().forEach(p->{
                elems.add(p);
            });
            locker.unlock();
        }
        else {
            throw new LimitExceededException();
        }
    }

    List<T> take(int n) throws LimitExceededException {
        if(elems.size() > n) {
            locker.lock();
            List<T> res = new ArrayList<T>();
            res = elems.stream().limit(n).collect(Collectors.toList());
            for (int i = 0; i < n; ++i) {
                elems.remove(0);
            }
            locker.unlock();
            return res;
        }
        else{
            throw new LimitExceededException();
        }

    }

    private ArrayList<T> elems;
    private ReentrantLock locker;
}

    class counter
    {
        public static void main (String[] args) throws InterruptedException {
            count c = new count(5);
            c.muster();
            Game g = new Game(5);
            g.roller();
            Queue<Integer> myQueue = new Queue<>(10);
            List<Integer> myList = new ArrayList<>();
            for(int i = 0 ; i < 8 ; ++i){
                myList.add(i);
            }
            try{
                myQueue.offer(myList);
            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
            }
            try{
                List<Integer> returnList = myQueue.take(5);
                for(Integer k: returnList){
                    System.out.println(k);
                }
            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }

