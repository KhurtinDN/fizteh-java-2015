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

    public void offer(List<E> elements) throws IllegalArgumentException, InterruptedException {
        if (elements.size() > elementsNumberLimit) {
            throw new IllegalArgumentException("Queue limit overflow.");
        }
        synchronized (queue) {
            while (elements.size() + queue.size() > elementsNumberLimit) {
                queue.wait();
            }
            queue.addAll(elements);
            // Уведомим тех, кто ждет добавления.
            queue.notifyAll();
        }
    }

    public List<E> take(int amount) throws IllegalArgumentException, InterruptedException {
        if (amount > elementsNumberLimit) {
            throw new IllegalArgumentException("Queue limit overflow.");
        }
        List<E> extracted = new ArrayList<>();
        // Блокируется все, так как в в противном случае, в момент
        // между получением размера и обработкой могут произойти изменения.
        synchronized (queue) {
            while (amount < queue.size()) {
                queue.wait();
            }
            for (int i = 0; i < amount; ++i) {
                extracted.add(queue.poll());
            }
            // Уведомим тех, кто ждет извлечения.
            queue.notifyAll();
        }
        return extracted;
    }
}
