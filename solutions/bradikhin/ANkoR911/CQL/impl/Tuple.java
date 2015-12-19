/**
 * Created by Владимир on 19.12.2015.
 */
public class Tuple<F, S>
{

    private final F first;
    private final S second;

    public Tuple(F fst, S snd) {
        this.first = fst;
        this.second = snd;
    }

    public final F getFirst() {
        return first;
    }

    public final S getSecond() {
        return second;
    }

    @Override
    public final String toString() {
        return "Tuple{"
                + "first=" + first
                + ", second=" + second
                + '}';
    }
}