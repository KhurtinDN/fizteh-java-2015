/**
 * Created by glutolik on 20.09.2015.
 */
public class Reverser {
    public static void main(String[] args) {
        int ind1, ind2;
        for (ind1 = args.length - 1; ind1 >= 0; --ind1) {
            String[] divided = args[ind1].split("\\s");
            for (ind2 = divided.length - 1; ind2 >= 0; --ind2) {
                System.out.print(divided[ind2] + " ");
            }
        }
        System.out.print("\n");
    }
}
