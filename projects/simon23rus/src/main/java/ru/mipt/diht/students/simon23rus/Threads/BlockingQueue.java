package ru.mipt.diht.students.simon23rus.Threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
        try {
            insertLocker.lock();
            for(int i = 0; i < toAdd.size(); ++i) {
                try {
                    footLocker.lock();
                    while (myBlockingQueue.size() == elementsBound) {
                        nonFull.await();
                    }
                    myBlockingQueue.add(toAdd.get(i));
                    nonEmpty.signalAll();
                } catch (InterruptedException e) {

                } finally {
                    footLocker.unlock();
                }
            }
        }
        finally {
            insertLocker.unlock();
        }
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
                    nonFull.signalAll();
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
}