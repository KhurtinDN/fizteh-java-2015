package ru.mipt.diht.students.annnvl.Threads.BlockingQueue;

import java.util.*;

public class BlockingQueue<T> {
    private Queue<T> data;
    private final int maxQueueSize;

    BlockingQueue(int size){
        maxQueueSize = size;
        data = new LinkedList<T>();
    }

    public int size(){
        return data.size();
    }

    synchronized public void offer(List<T> e){
        while(data.size() + e.size() > maxQueueSize){
            Thread.yield();
        }
        data.addAll(e);
    }

    synchronized public void offer(List<T> e, long timeout){
        long lastBreathTime = System.currentTimeMillis();
        while((data.size() + e.size() > maxQueueSize) && timeout > 0){
            Thread.yield();
            long wakeupTime = System.currentTimeMillis();
            timeout -= (wakeupTime - lastBreathTime);
            lastBreathTime = wakeupTime;
        }
        if(timeout > 0){
            data.addAll(e);
        }
    }

    synchronized List<T> take(int n){
        while (data.size() < n){
            Thread.yield();
        }
        List<T> answer = new ArrayList<T>();
        for (int i = 0; i < n; i++) {
            answer.add(data.remove());
        }
        return answer;
    }

    synchronized List<T> take(int n, long timeout){
        long lastBreathTime = System.currentTimeMillis();
        while((data.size() < n) && timeout > 0){
            Thread.yield();
            long wakeupTime = System.currentTimeMillis();
            timeout -= (wakeupTime - lastBreathTime);
            lastBreathTime = wakeupTime;
        }
        if(timeout > 0){
            List<T> answer = new ArrayList<T>();
            for (int i = 0; i < n; i++) {
                answer.add(data.remove());
            }
            return answer;
        }
        return null;
    }
}
