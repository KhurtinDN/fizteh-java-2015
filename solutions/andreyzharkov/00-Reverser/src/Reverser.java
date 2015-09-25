/**
 * Created by Андрей on 19.09.2015.
 */
public class Reverser {
    public static void main(String[] args) {
        for (int i = args.length - 1; i >= 0; --i) {
            String[] strings = args[i].split("\\s");
            for (int j = strings.length - 1; j >= 0; --j) {
                System.out.print(strings[j] + " ");
            }
        }
    }
}
