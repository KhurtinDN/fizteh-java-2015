/**
 * Created by admin on 18.09.2015.
 */
public class Reverser {
    public static void main(String[] args) {
        for(int i=args.length - 1; i>=0; --i) {
            String[] current = args[i].split("[\\s+]");
            for (int j = current.length - 1; j >= 0; --j) {
                System.out.print(current[j] + ' ');
            }
        }
    }
}


