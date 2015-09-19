public class Reverser {
    public static void main(String[] args) {
        String[] numbers = String.join(" ", args).trim().split("\\s+");
        for (int i = numbers.length - 1; i >= 0; --i)
            System.out.print(numbers[i] + " ");
        System.out.println();
    }  
}
