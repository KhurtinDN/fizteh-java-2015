public class Main {

    public static void main(String[] args) {
        for(int pos = args.length - 1; pos >= 0; --pos) {
            String[] thisString = args[pos].split("\\s");
            for(int toPrint = 0; toPrint < thisString.length; ++toPrint) {
                System.out.print(thisString[toPrint] + " ");
            }
        }
    }
}
