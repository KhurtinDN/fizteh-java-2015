package ru.fizteh.fivt.students.bulgakova.Threads;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;


public class BlockingQueue<E> {

    private int maxSize;
    private Queue<E> elements;
    private ReentrantLock locker;


    public BlockingQueue(int _size) {
        maxSize = _size;
        locker = new ReentrantLock();
        elements = new LinkedList<E>();
    }

    void offer(List<E> e) throws LimitExceededException {
        if(e.size() + elements.size() <= maxSize) {
            locker.lock();

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