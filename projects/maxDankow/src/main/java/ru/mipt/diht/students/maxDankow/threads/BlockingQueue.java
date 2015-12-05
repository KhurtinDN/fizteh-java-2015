package ru.mipt.diht.students.maxDankow.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BlockingQueue<E> {
    private volatile Queue<E> queue;
    private int elementsNumberLimit;

    public BlockingQueue(int elementsNumberLimit) {
        this.elementsNumberLimit = elementsNumberLimit;
    }

    public void offer(List<E> elements) {
        synchronized (queue) {
            try {
                while (elements.size() + queue.size() > elementsNumberLimit) {
                    queue.wait();
                }
            } catch (InterruptedException ignored) {
            }
            queue.addAll(elements);
        }
    }

    public List<E> take(int amount) {
        List<E> extracted = new ArrayList<>();
        // Блокируется все, так как в в противном случае, в момент
        // между получением размера и обработкой могут произойти изменения.
        synchronized (queue) {
            try {
                while (amount < queue.size()) {
                    queue.wait();
                }
            } catch (InterruptedException e) {
                return null;
            }
            for (int i = 0; i < amount; ++i) {
                extracted.add(queue.poll());
            }
            notifyAll();
        }
        return extracted;
    }
    // todo: проблема - сейчас wait может прерваться, но это не означает,
    // todo: что свободно достаточно места.
//    public void offer(List<E> elements, long timeout) {
//        synchronized (queue) {
//            try {
//                if (elements.size() + queue.size() > elementsNumberLimit) {
//                    queue.wait(timeout);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            queue.addAll(elements);
//            queue.notifyAll();
//        }
//    }
//
//    public List<E> take(int amount, long timeout) {
//        List<E> extracted = new ArrayList<>();
//        synchronized (queue) {
//            try {
//                if (amount < queue.size()) {
//                    queue.wait();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            for (int i = 0; i < amount; ++i) {
//                extracted.add(queue.poll());
//            }
//            notifyAll();
//        }
//        return extracted;
//    }
}
