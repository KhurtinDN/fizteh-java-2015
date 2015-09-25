
public class Reverser {
    public static void main(String[] args) {

        for (int i = args.length - 1; i >= 0; --i) {
            String[] strings = args[i].split("\\s+");
            for (int j = strings.length - 1; j >= 0; --j) {
                if(strings[j].charAt(strings[j].length() - 1) == '\\'){
                   System.out.print(strings[j].substring(0, strings[j].length() - 1) + " ");
                }
                else
                System.out.print(strings[j] + " ");
            }
        }
    }
}