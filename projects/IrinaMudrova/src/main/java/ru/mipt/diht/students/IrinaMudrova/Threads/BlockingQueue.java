package main.java.ru.mipt.diht.students.IrinaMudrova.Threads;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ирина on 16.12.2015.
 */

public class BlockingQueue <T> {

    public static class Query implements Comparable<Query> {
        public ReentrantLock lock = new ReentrantLock();
        public Condition cond = lock.newCondition();
        public Integer count;
        public Boolean ready = false;
        public void setReady() {
            lock.lock();
            ready = true;
            cond.signal();
            lock.unlock();
        }
        public Query(int n) {
            count = n;
        }

        @Override
        public int compareTo(Query o) {
            if (!count.equals(o.count))
                return count.compareTo(o.count);
            return ((Integer) cond.hashCode()).compareTo(o.cond.hashCode());
        }
    }
    int maxQueueSize;
    ArrayDeque<T> data;
    SortedSet<Query> takeQueries, offerQueries;

    public void offer(List<T> list) {
        Query query = new Query(list.size());
        while (true) {
            synchronized (this) {
                if (data.size() + list.size() <= maxQueueSize) {
                    for (T item : list) {
                        data.add(item);
                    }
                    while (!takeQueries.isEmpty() &&
                            takeQueries.first().count <= data.size()) {
                        Query takeQuery = takeQueries.first();
                        takeQueries.remove(takeQuery);
                        takeQuery.setReady();
                    }
                    return;
                } else {
                    offerQueries.add(query);
                }
            }
            query.lock.lock();
            try {
                while (!query.ready) {
                        query.cond.await();
                }
                query.ready = false;
            } catch (InterruptedException e) {
                System.out.println("Sad but interrupted =(");
            } finally {
                query.lock.unlock();
            }
        }

    }
    public List<T> take(int n) {
        Query query = new Query(n);
        while (true) {
            synchronized (this) {
                if (n <= data.size()) {
                    List<T> result = new ArrayList<T>();
                    for (int i = 0; i < n; i++) {
                        result.add(data.poll());
                    }
                    while (!offerQueries.isEmpty() &&
                            data.size() + offerQueries.first().count <= maxQueueSize) {
                        Query offerQuery = offerQueries.first();
                        offerQueries.remove(offerQuery);
                        offerQuery.setReady();
                    }
                    return result;
                } else {
                    takeQueries.add(query);
                }
            }
            query.lock.lock();
            try {
                while (!query.ready) {
                        query.cond.await();
                }
                query.ready = false;
            } catch (InterruptedException e) {
                System.out.println("Sad but interrupted =(");
            } finally {
                query.lock.unlock();
            }
        }
    }
    public BlockingQueue(int maxQueueSizeArg) {
        maxQueueSize = maxQueueSizeArg;
        data = new ArrayDeque<T>();
        takeQueries = new TreeSet<Query>();
        offerQueries = new TreeSet<Query>();
    }
}


