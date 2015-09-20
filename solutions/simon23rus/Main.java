public class Main {

    public static void main(String[] args) {
        for(int pos = args.length - 1; pos >= 0; --pos) {
            String[] thisString = args[pos].split("\\s");
            for(int toPrint = thisString.length -1; toPrint >= 0; --toPrint) {
                System.out.print(thisString[toPrint] + " ");
            }
        }
    }
}
