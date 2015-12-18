
public class Reverser {

    public static void main (String[] argv) {
        for (int i = argv.length - 1; i >= 0; i--) {
            String[] psd = argv[i].split("\\s");
            for (int j = psd.length - 1; j >= 0; j--) {
                System.out.print(psd[j] + " ");
            }
        }
        System.out.println();
    }
}

