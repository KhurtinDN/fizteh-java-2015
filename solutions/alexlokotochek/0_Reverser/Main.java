public class Main {
    public static void main (String[] args) {
        for (int i = args.length - 1; i >= 0; --i) {
            String[] input = args[i].split("\\s");
            for (int j = input.length - 1; j >= 0; --j) {
                System.out.print(input[j] + " ");
            }
        }
    }
}