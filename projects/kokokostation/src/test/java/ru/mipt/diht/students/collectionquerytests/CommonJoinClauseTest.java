package ru.mipt.diht.students.collectionquerytests;

import javafx.util.Pair;
import org.junit.Test;
import ru.mipt.diht.students.collectionquery.impl.CommonJoinClause;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static ru.mipt.diht.students.collectionquery.impl.Utils.streamToList;

/**
 * Created by mikhail on 03.02.16.
 */
public class CommonJoinClauseTest {
    @Test
    public void test() {
        List<Integer> left = Arrays.asList(2, 1, 5, 4, 7, 3, 3);
        List<String> right = Arrays.asList("3", "7", "12", "15", "10");

        CommonJoinClause<Integer, String> commonJoinerClause = new CommonJoinClause<>(left, right);

        Stream<Pair<Integer, String>> result = commonJoinerClause.onImpl((a, b) -> a.toString().equals(b));
        assertThat(streamToList(result), contains(new Pair<>(7, "7"), new Pair<>(3, "3"), new Pair<>(3, "3")));

        result = commonJoinerClause.onImpl(t -> t % 5, t -> Integer.parseInt(t) % 5);
        assertThat(streamToList(result), contains(new Pair<>(3, "3"), new Pair<>(3, "3"), new Pair<>(2, "7"),
                new Pair<>(7, "7"), new Pair<>(2, "12"), new Pair<>(7, "12"), new Pair<>(5, "15"),
                new Pair<>(5, "10")));
    }
}