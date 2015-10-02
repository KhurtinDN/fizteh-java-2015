public class Reverser {
    public static void main(String[] args) {
        for (int index = args.length - 1; index >= 0; --index) {
            String[] numbers = args[index].split("\\s");
            for (int numberIdx = numbers.length - 1; numberIdx >= 0; --numberIdx) {
                System.out.print(numbers[numberIdx] + " ");
            }
        }
        System.out.println();
    }
}
