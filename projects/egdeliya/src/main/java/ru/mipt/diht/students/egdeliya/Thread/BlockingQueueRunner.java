package ru.mipt.diht.students.egdeliya.Thread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Эгделия on 17.12.2015.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class BlockingQueueRunner {
    public static void main(String[] args) {
        int maxQueueSize = Integer.parseInt(args[0]);
        BlockingQueue<String> queue = new BlockingQueue(maxQueueSize);
        List<String> list = new ArrayList<>(4);

        list.add("Hello everyone! I just do it!");
        list.add("But I have to do some more...");
        list.add("Help me, please!");
        list.add("Oh, my God...");

        queue.offer(list);
    }
}
