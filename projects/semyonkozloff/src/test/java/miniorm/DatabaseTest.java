package miniorm;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.mipt.diht.students.semyonkozloff.miniorm.DatabaseService;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseTest extends TestCase {

    private static final int N_TESTS = 100;

    private static final int N_BITS = 130;
    private static final int RANDOM_STRING_SIZE = 32;

    private List<TestClass> testArray;
    private DatabaseService<Integer, TestClass> database;

    private String getRandomString(int nBits, int stringSize) {
        return new BigInteger(nBits, new SecureRandom())
                .toString(stringSize);
    }

    @Before
    public void setUp() {
        testArray = new ArrayList<>();
        Random randomGenerator = new Random();
        for (int i = 0; i < N_TESTS; ++i) {
            testArray.add(new TestClass(i,
                    getRandomString(N_BITS, RANDOM_STRING_SIZE),
                    getRandomString(N_BITS, RANDOM_STRING_SIZE),
                    randomGenerator.nextDouble()));
        }
        database = new DatabaseService<>(TestClass.class);
        database.createTable();
    }

    @Test
    public void testInsert() {
        testArray.forEach(database::insert);

        List<TestClass> response = database.queryForAll();

        for (int i = 0; i < N_TESTS; ++i) {
            assertThat(response.get(i), equalTo(testArray.get(i)));
        }
    }

    @Test
    public void testUpdate() {
        testArray.forEach(database::insert);
        final int randomIndex = new Random().nextInt(testArray.size() - 1);
        testArray.get(randomIndex).string1 = "updated";
        database.update(testArray.get(randomIndex));

        List<TestClass> response = database.queryForAll();

        for (int i = 0; i < N_TESTS; ++i) {
            assertThat(response.get(i), equalTo(testArray.get(i)));
        }
        assertThat(database.getById(testArray.get(randomIndex).key),
                equalTo(testArray.get(randomIndex)));
    }

    @Test
    public void testDelete() {
        testArray.forEach(database::insert);
        final int randomIndex = new Random().nextInt(testArray.size() - 1);
        database.delete(testArray.get(randomIndex));
        testArray.remove(randomIndex);

        List<TestClass> response = database.queryForAll();

        for (int i = 0; i < N_TESTS - 1; ++i) {
            assertThat(response.get(i), equalTo(testArray.get(i)));
        }
    }

    @Test
    public void testDropTable() {
        testArray.forEach(database::insert);

        database.dropTable();

        assertTrue(database.queryForAll().size() == 0);
    }
}
