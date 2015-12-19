package ru.mipt.diht.students.simon23rus.Threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//atomarno
public class BlockingQueue<T> {
    Queue<T> myBlockingQueue;
    private int elementsBound;
    private static Lock insertLocker;
    private static Lock deleteLocker;
    private static Lock footLocker;
    private static Condition nonEmpty;
    private static Condition nonFull;

    public List<T> getData() {
        return new ArrayList<>(myBlockingQueue);
    }

    public BlockingQueue(int elementsBound) {
        this.elementsBound = elementsBound;
        this.myBlockingQueue = new LinkedList<T>();
        this.insertLocker = new ReentrantLock(true);
        this.deleteLocker = new ReentrantLock(true);
        this.footLocker = new ReentrantLock(true);
        this.nonEmpty = footLocker.newCondition();
        this.nonFull = footLocker.newCondition();
    }

    void printQueue() {
        System.out.println(myBlockingQueue);
    }

    //dobavit' v konec
     void offer(List<T> toAdd) {
         footLocker.lock();
         try {
             while (myBlockingQueue.size() + toAdd.size() > elementsBound) {
                 try {
                     nonFull.await();
                 } catch (InterruptedException e) {
                     System.out.println("Interrupted");
                 }
             }
             myBlockingQueue.addAll(toAdd);
             nonEmpty.signal();
         } finally {
             footLocker.unlock();
         }
    }


    void offer(List<T> toAdd, long timeout) {
        try {
            long currentTime = System.currentTimeMillis();
            if (footLocker.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                try {
                    while (myBlockingQueue.size() + toAdd.size() > elementsBound) {
                        timeout -= System.currentTimeMillis() - currentTime;
                        currentTime = System.currentTimeMillis();
                        if (nonFull.await(timeout, TimeUnit.MILLISECONDS) == false) {
                            throw new TimeoutException();
                        }
                    }
                    myBlockingQueue.addAll(toAdd);
                    nonEmpty.signal();
                } finally {
                    footLocker.unlock();
                }
            } else {
                throw new TimeoutException();
            }
        } catch (TimeoutException e) {
            System.out.println("Time limit exceeded");
            return;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }
        return;

    }

    //vzyat iz nachala
    List<T> take(int numberOfElements) {
        List<T> takenElements = new ArrayList<T>();
        try {
            deleteLocker.lock();
            for(int i = 0; i < numberOfElements; ++i) {
                try {
                    footLocker.lock();
                    while (myBlockingQueue.size() == 0) {
                        nonEmpty.await();
                    }
                    takenElements.add(myBlockingQueue.poll());
                    nonFull.signal();
                } catch (InterruptedException e) {
                }
                finally {
                    footLocker.unlock();
                }
            }
        }
        finally {
            deleteLocker.unlock();
            return takenElements;
        }
    }

    public List<T> take(int numberOfElements, long timeout) {
        try {
            List<T> taken = new ArrayList<>();
            long currentTime = System.currentTimeMillis();
            if (footLocker.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                try {
                    while (myBlockingQueue.size() < numberOfElements) {
                        timeout -= (System.currentTimeMillis() - currentTime);
                        currentTime = System.currentTimeMillis();
                        if (!nonEmpty.await(timeout, TimeUnit.MILLISECONDS)) {
                            throw new TimeoutException();
                        }
                    }
                    for (int i = 0; i < numberOfElements; ++i) {
                        taken.add(myBlockingQueue.poll());
                    }
                    nonFull.signal();
                    return taken;
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                } finally {
                    footLocker.unlock();
                }
            } else {
                throw new TimeoutException();
            }

        } catch (TimeoutException e) {
            return null;
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
        return null;
    }

}
