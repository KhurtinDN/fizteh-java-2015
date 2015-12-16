package query;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lokotochek on 16.12.15.
 */
public class BlockingQueryTest {

    @Test
    public void testTake() throws Exception {
        BlockingQuery bq = new BlockingQuery(100);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            list.add(i*i);
        }
        bq.offer(list);
        List<Integer> answer = bq.take(10);
        assertEquals(10, answer.size());
        for (int i = 0; i < answer.size(); ++i) {
            assertEquals(list.get(i).toString(), answer.get(i).toString());
        }
    }

    @Test
    public void testOffer() throws Exception {
        BlockingQuery bq = new BlockingQuery(1000);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 500; ++i) {
            list.add(i*i);
        }
        for (int i = 0; i < 300; ++i) {
            list.remove(0);
        }
        bq.offer(list);
        List<Integer> answer = bq.take(150);
        assertEquals(150, answer.size());
        for (int i = 0; i < answer.size(); ++i) {
            assertEquals(list.get(i).toString(), answer.get(i).toString());
        }
    }

    @Test
    public void testOffer1() throws Exception {
        BlockingQuery bq = new BlockingQuery(1000);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 5000; ++i) {
            list.add(i*i);
        }
        for (int i = 0; i < 1500; ++i) {
            list.remove(0);
        }
        bq.offer(list, 1000);
        List<Integer> answer = bq.take(10);
        assertEquals(10, answer.size());
        for (int i = 0; i < answer.size(); ++i) {
            assertEquals(list.get(i).toString(), answer.get(i).toString());
        }
    }

    @Test
    public void testTake1() throws Exception {
        BlockingQuery bq = new BlockingQuery(100);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            list.add(i*i);
        }
        bq.offer(list);
        List<Integer> answer = bq.take(10000, 1000);
        assertNull(answer);
    }
}