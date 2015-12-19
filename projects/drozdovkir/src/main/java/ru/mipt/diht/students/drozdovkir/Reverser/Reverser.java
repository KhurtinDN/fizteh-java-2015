public class Reverser {
    public static void main(String[] args){
        for (int v = args.length - 1; v >= 0; v--){
            String[] currentString = args[v].split("//s");
            for (int r = currentString.length - 1; r >= 0; r--){
                System.out.print(currentString[r] + " ");
            }
        }
    }
}
