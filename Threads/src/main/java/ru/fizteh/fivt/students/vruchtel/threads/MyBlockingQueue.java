package ru.fizteh.fivt.students.vruchtel.threads;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Серафима on 17.12.2015.
 */
public class MyBlockingQueue<E> {

    private int maxSize;
    private Queue<E> elements;
    private ReentrantLock locker;


    public MyBlockingQueue(int _size) {
        maxSize = _size;
        locker = new ReentrantLock();
        elements = new LinkedList<E>();
    }

    void offer(List<E> e) throws LimitExceededException {
        if(e.size() + elements.size() <= maxSize) {
            locker.lock();
            // Здесь элементы добавляются в очередь
            for(E oneElem : e) {
                elements.add(oneElem);
            }
            locker.unlock();
        } else {
            throw new LimitExceededException();
        }
    }

    List<E> take(int n) throws LimitExceededException {
        if(elements.size() >= n) {
            locker.lock();

            List<E> returningList = new ArrayList<E>();
            for(int i = 0; i < n; i++) {
                returningList.add(elements.remove());
            }

            locker.unlock();
            return returningList;
        } else {
            throw new LimitExceededException();
        }
    }
}
