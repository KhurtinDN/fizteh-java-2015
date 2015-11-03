
public class CmdReverser {
    public static void main (String[] args) {
        for (int argIndex = args.length - 1; argIndex >= 0; --argIndex) {
            String[] splitedArg = args[argIndex].split("\\s+");
            for (int subArgIndex = splitedArg.length - 1; subArgIndex >= 0; --subArgIndex) {
                System.out.print(splitedArg[subArgIndex] + " ");
            }
        }
        System.out.print("\n");
    }
}
